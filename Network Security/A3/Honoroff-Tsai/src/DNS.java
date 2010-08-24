////////////////////////////////////////////////////////////////////
// Simnet, version 1.0                                            //
// COPYRIGHT Seny Kamara, Darren Davis, Fabian Monrose, 2002-2005 //
// Please read the DISCLAIMER                                     //
////////////////////////////////////////////////////////////////////

package A3;

import simnet.*;
import java.util.*;
import java.math.BigInteger;
import java.io.*;

/**
A simple DNS resolver. 
 Assuming that all domain names are lower case (or, treat domain names as case sensitive). 
 No support for retransmissions of requests or wildcards.
 No support for multiple addresses for one host.
 */

public class DNS extends Resolver {
	
	/**
	* Whether or not this resolver is a stub (client) or local (!client) resolver
	 */
	protected boolean client=true; 
	
	/**
	* If we are a client, then this is our nameserver
	 */
	protected int nameserver=-1;
	
	/** the name of our resolver (not DNS, but node, mostly used in DNSSEC) */
	protected String resolverName;
	
	/**
	 * Used to generate request id's, so they (probably) will be unique.
	 */
	protected Random random=new Random();
	
	/**
	 * Used to communicate DNS requests/responses
	 */
	protected DatagramSocket sock;
	
	/**
	 * Used to keep track of pending requests.
	 * Indexed by request ID, holds Objects of type {@link PendingRequest}.
	 */
	protected Hashtable pending=new Hashtable();
	
	/**
	 * Our primary thread to handle requests/responses.
	 */
	protected Processor processor;
	
	/**
	 * Thread to remove expired items from our cache. (Only for local resolvers)
	 */
	protected Timer cacheCleaner;
	
	
	/**
	 * Holds information about our zone(s) -- for servers only.
	 * Objects of type ZoneData are indexed by zone name.  Each ZoneData
	 * object holds RRGroups, which in turn hold RRs. (only used by local resolvers)
	 */
	Hashtable zones;
	
	/**
	 * Cache of RRGroups returned by queries to remote name servers, indexed by RRGroup name.
	 * (only used by local resolvers)
	 */
	Hashtable cache;
	
	/**
	 * Information for ROOT name server.  All name servers may use this as a fall back
	 * when they cannot produce any information about a request.
	 */
	RRGroup rootNSRRG;
	
	/**
	 * Address of the ROOT name server.  All name servers may use this as a fall back
	 * when they cannot produce any information about a request.
	 */
	RRGroup rootARRG;
	
	/**
	 * Global Parameter -- how long RRs may be cached before they are considered invalid.
	 */
	protected int CACHE_TTL;
	
	/**
	 * Global Parameter -- how long RRs may last until they are considered invalid.
	 */
	protected int RR_DURATION;
	
	/**
	 * Global Parameter -- ip address of root node.
	 */
	protected int ROOT;
	
	/**
	 * Global Parameter -- name of root node.
	 */
	protected String ROOTNAME;
	
	/**
		* Global Parameter -- amount of time that should pass before we consider a request failed.
	 */
	protected int TIMEOUT;
	
	/**
		* Loads information for this resolver.
	 * This is where a resolver can do any resolver-specific initialization it requires.
	 * @param	config	Parsed array that represents resource records and server information.
	 * @throws Exception	If an entry of config cannot be processed correctly
	 */
	public void customInit(String config[]) throws Exception {
		
		zones = new Hashtable();
				
		String zone=null;
		synchronized(zones){
			for (int i=0; i<config.length; i++) {
				String tokens[]=config[i].split("\t");
				if (tokens.length<2) throw new Exception("Incomplete Configuration Line: "+config[i]);
				
				if (tokens[0].equals("SERVER")) {
					client=false;
				} else if (tokens[0].equals("CLIENT")) {
					client=true;
					if (tokens.length>=3){
						nameserver=sim.lookup(tokens[2]);
						resolverName = tokens[2];
					}else{
						nameserver=ROOT;
						resolverName = ROOTNAME;
					}
				} else if (tokens[0].equals("ZONE")) {
					if (client) throw new Exception("Clients cannot have zones!");
					zone=tokens[1];
					if (!zone.endsWith(".")) throw new Exception("Zones must end with a dot: "+zone);
					if (zone.length()>1 && zone.startsWith("."))
						throw new Exception("Zones cannot start with a dot: "+zone);
					zones.put(zone, new ZoneData(zone));
					
					node.printout(Simnet.VC_DNS, Simnet.VL_DEBUG, node.id,
									  "Created ZoneData for zone " + zone);
					
					// Create an SOA RR record
					if (!loadRR(zone, tokens))
						throw new Exception("Invalid RR in Zone '"+zone+"': "+config[i]);
				} else {
					// Create an RR
					if (!loadRR(zone,tokens))
						throw new Exception("Invalid RR in Zone '"+zone+"': "+config[i]);
				}
			} //close for
		}
		
		if( !client ){
			cacheCleaner = new Timer();
			cache = new Hashtable();
			
			//get the NS and A RRGroups for the root
			// all local resolvers have these pointers
			createRootRRGs();
		}
		
		// now that the config info is loaded, create a DatagramSocket to send/recv DNS messages
		sock=((UDP)node.getTransport(Simnet.PROTO_UDP)).createDatagramSocket(this);
		sock.bind(Simnet.DNS);
		
		// create a thread to process received requests
		processor=new Processor();
		processor.start();
		
		if( !client ){
			cacheCleaner.scheduleAtFixedRate(new CacheCleaner(), CACHE_TTL, CACHE_TTL);
		}
	}
	
	/** 
		* Loads the tokenized record for the specified zone into the database.
		* Returns if the record was valid.
		* @param	zone  The zone this record is in
		* @param	tokens	Parsed string that specifies an RR
		* @return  whether or not the tokens could correspond to a valid RR
		* @throws	Exception	If the RR isn't defined correctly
		*/
    protected boolean loadRR(String zone, String tokens[]) throws Exception {
		if (zone==null) {
			// check for global parameters -- these will be at the head of the text file
			if (tokens[0].equals("ROOT")) {
				ROOT=sim.lookup(tokens[1]);
				if (ROOT==-1) throw new Exception("Invalid IP address for ROOT: "+tokens[1]);
				ROOTNAME=tokens[2];
			} else if (tokens[0].equals("CACHE_TTL")) {
				CACHE_TTL=Integer.parseInt(tokens[1]);
			} else if (tokens[0].equals("RR_DURATION")) {
				RR_DURATION=Integer.parseInt(tokens[1]);
			} else if (tokens[0].equals("TIMEOUT")) {
				TIMEOUT=Integer.parseInt(tokens[1]);
			} else {
				return false;
			}
			
			return true;
		}
		
		// fill out the name
		String name;
		if( !tokens[1].equals("") ){
			name=Utils.makeAbsoluteDNSName(tokens[1], zone);
		}else{
			name = zone;
		}
		String value;
		ResourceRecord rr;
		if (tokens[0].equals("ZONE")){
		    //it's an SOA record ... won't really use this except for DNSSEC
			rr = new ResourceRecord("SOA",name, -1);
			node.printout(Simnet.VC_DNS, Simnet.VL_DEBUG, node.id,
							  "made SOA record: " + rr.toString());
		}else if (tokens[0].equals("A")) {
			//A Resource Record
			int tmp=sim.lookup(tokens[2]);
			if (tmp==-1) throw new Exception("Invalid IP for an A-type RR '"+name+"': '"+tokens[2]+"'");
			value=Utils.getStringFromIP(tmp);
			rr=new ResourceRecord("A",name,-1);
			rr.makeARecord(value);
			node.printout(Simnet.VC_DNS, Simnet.VL_DEBUG, node.id,
							  "made A record: " + rr.toString());
		}else if (tokens[0].equals("NS")) {
			//NS Resource Record
			value=Utils.makeAbsoluteDNSName(tokens[2],zone);

			rr=new ResourceRecord("NS",name,-1);
			rr.makeNSRecord(value);
			node.printout(Simnet.VC_DNS, Simnet.VL_DEBUG, node.id,
							  "made NS record: " + rr.toString());
		}else {
			//don't know how to parse other records
			return false;
		}
		
		//Are we authoratative for this RR, or do we just know about it to keep the DNS tree happy?
		boolean auth = (tokens.length > 3 && tokens[3].equals("AUTH") );
		
		//get the correct zone to add the RR to
		// the ZoneData object will add it to the correct internal RRGroup object
		synchronized(zones){
			( (ZoneData) zones.get(zone)).addRR(name, rr, auth);
		}
		
		return true;
	}
	
	
	/** Fill out our pointers to the ROOT of the DNS */
	protected void createRootRRGs(){
		rootNSRRG = new RRGroup(".");
		ResourceRecord ns = new ResourceRecord("NS", ".", -1);
		ns.makeNSRecord(ROOTNAME);
		rootNSRRG.add(ns);
		
		rootARRG = new RRGroup(ROOTNAME);
		ResourceRecord a = new ResourceRecord("A", ROOTNAME, -1);
		a.makeARecord( Utils.getStringFromIP( ROOT ) );
		rootARRG.add(a);
		
	}
	
	/**
	 * Method that checks our zones database and the cache for information
	 * pertaining to to the specified name.
	 * @param	name  The name of the node we are looking up.
	 * @return  A LinkedList containing pertinent RRGroups.  If we found an RRGroup containing an A or NX RR,
	 *				it will be the first RRGroup in the list.  If we have an NS record, it will be the
	 *				first in the list and will be followed by an A record indicating the IP address
	 *				of the NS.
	 */
	public LinkedList doIterativeQuery(String name, String type){
		node.printout(Simnet.VC_DNS, Simnet.VL_COMPLETE, node.id,
						  "doIterativeQuery() called with name = " + name + ", " + type);
		if( client ){
			throw new Error("CANNOT run iterative query from a client.");
		}
		
		//will hold a set of RRGroups pertinent to this query
		//the most recent (usefull) first
		LinkedList results = new LinkedList();
		
		//do we have an RRGroup for this request?
		// not checking authoritative first because we might have the record
		// but not be authoritative for it
		RRGroup rrg = getRRGroup(name, type);
		if( rrg != null){
			//we will be returning this, so make sure that it is a copy
			node.printout(Simnet.VC_DNS, Simnet.VL_COMPLETE, node.id,
							  "Found the RRGroup: " + rrg.name);
			results.addFirst(rrg.cache(RR_DURATION));
			return results;
		}
		
		//if we are authoritative for this name then the RR requested doesn't
		// exist, return an NX record.
		if( authoritativeFor( name ) ){
			node.printout(Simnet.VC_DNS, Simnet.VL_DEBUG, node.id,
							  "No RRGroup, returning an NX");
			//we don't have one (since we are authoritative, it must not exist)
			//make an rrg with an NX record (this will eventually be SOA/NXT records)
			RRGroup nx = new RRGroup(name);
			nx.add( new ResourceRecord("NX", name, RR_DURATION));
			//make sure to copy/update expiration time
			results.addFirst(nx.cache(RR_DURATION));
			return results;
		}
		
		//we are not authoritative, but lets check our cache!
		node.printout(Simnet.VC_DNS, Simnet.VL_COMPLETE, node.id,
						  "We are NOT authoratative for this name: " + name + " checking cache...");
		
		RRGroup cachedRRG = checkCache(name, type);
		
		if( cachedRRG != null ){
			//we got a cache hit, return that (don't update the TTL)
			node.printout(Simnet.VC_DNS, Simnet.VL_COMPLETE, node.id,
							  "got a cache hit:" + cachedRRG);
			//checkCache returns a cloned version of this RRG, we don't need to do it here
			results.addFirst(cachedRRG);
			return results;
		}
		
		
		//cache miss
		node.printout(Simnet.VC_DNS, Simnet.VL_COMPLETE, node.id, "cache miss");
		
		
		//now we need to get the closest name server that could process this
		// request.  check to see if we have an NS in any of our zones or in our cache
		// if not, return a pointer to the root NS
		
		node.printout(Simnet.VC_DNS, Simnet.VL_COMPLETE, node.id,
						  "Finding most accurate NS:");
		
		//return the NS/A pair that is most applicable to our query
		LinkedList nsRRG = getBestNS(name, true);
		
		if( nsRRG == null ){
			//no name servers that are applicable to this query, return a pointer to the root
			node.printout(Simnet.VC_DNS, Simnet.VL_COMPLETE, node.id,
							  "We dont even have a name server, returning the ROOT");
			
			//copy and update TTL
			results.addFirst( rootARRG.cache(RR_DURATION));
			results.addFirst( rootNSRRG.cache(RR_DURATION) );
			
			return results;
		} 
		
		//copy/update TTL on each record
		Iterator it = nsRRG.iterator();
		while( it.hasNext() )
			results.addLast( ((RRGroup) it.next()).cache(RR_DURATION));
		return results;
	}
	
	
	
	/**
	 * Method to recursively search for the answer to a query.
	 * Unlike doIterativeQuery(), does not return until it has received an A or NX record.
	 * @param	name  The name of the node to query
	 * @return  a LinkedList of pertinant records, starting with the most useful (either an A or NX).
	 *				The second record is the NS that gave the response, the third is the A for that NS, etc..
	 * @throws	DNSException if verification of RRGroups fail.  This is only if they are expired for regular DNS
	 */
	public LinkedList doRecursiveQuery(String name, String type)
		throws DNSException{
			
		node.printout(Simnet.VC_DNS, Simnet.VL_COMPLETE, node.id,
						  "Entering Recursive Query for: " + name + ", " + type);
		
		//first see if we can get an answer from our database/cache
		LinkedList relevantRRGroups = doIterativeQuery(name, type);

		
		//while we do not have a final answer...
		while(!hasFinalAnswer(relevantRRGroups, name, type)){

			Iterator it = relevantRRGroups.iterator();
			
			RRGroup rrg;
			while( it.hasNext() ){
				
				rrg = (RRGroup) it.next();
				
				//if this RRGroup describes a Name Server
				if(rrg.hasRecord("NS") ){
					String nsName = rrg.getRecord("NS").value;
					//then the next RRGroup in the list should contain the NS's A Record
					try{
						rrg = (RRGroup) it.next();
						if( ! rrg.hasRecord("A") ){
							throw new Error("doRecursiveQuery (NO A): no A record corresponding to the record hfa = (" + 
												 hasFinalAnswer( relevantRRGroups, name, type) +"): " + rrg + " called at " + node.name
												 + " with q:" + name + "/" +type +"\nLL=" + relevantRRGroups);	
							//return null;
						}
					}catch(NoSuchElementException nsee){
						throw new Error("doRecursiveQuery (NSEE): no A record corresponding to the record: " + rrg + 
											 " called at " + node.name + " with q:" + name + "/" +type +"\nLL=" + relevantRRGroups);					
						///return null;
					}
				
					//send the query to the name server...
					ResourceRecord arr = rrg.getRecord("A", nsName );
					node.printout(Simnet.VC_DNS, Simnet.VL_COMPLETE, node.id,
									  "Sending remote query to " + arr);
					//query the NS
					LinkedList remoteResults = doRemoteQuery(name, type, Utils.getIPFromString(arr.value), false );

						
					node.printout(Simnet.VC_DNS, Simnet.VL_COMPLETE, node.id,
									  "Received response: " + remoteResults);
					
					//add the new results to the information we already have
					Iterator it_debug = remoteResults.iterator();
					while(it_debug.hasNext()){
						RRGroup next = (RRGroup) it_debug.next();
						if( relevantRRGroups.contains( next ) ){
								throw new Error("Found a lookup cycle! Query = " + name + "/" + type + "\tcycle = " + next);
						}
					}

					remoteResults.addAll(relevantRRGroups);
					relevantRRGroups = remoteResults;
					if(relevantRRGroups.size() > 12 ){
						throw new Error("Found a lookup cycle! Query = " + name + "/" + type + "\tcycle = " + relevantRRGroups);
					}
						break;
				}
			}
		}
		
		//return a list of all relevant RRGroups ... the first being an A or NX record
		return relevantRRGroups;
		
	}
	
	
	
	/**
	 * Query a remote name server, get the results and cache and return them.
	 * @param	name  The name to lookup
	 * @param	server	The name server to query
	 * @param	recurse  Whether or not this should be recurive.  Clients should
	 *							set this to true, servers should not.
	 * @return  The responses from the remote host 
	 * @throws	DNSException	When verification of results fail.
	 */
	protected LinkedList doRemoteQuery(String name, String type, int server, boolean recurse)
		throws DNSException{
			//make a random ID for this query so we can track it
			int reqID=random.nextInt(999999);
			PendingRequest p=new PendingRequest();
			
			// NOTE the non-standard locking mechanism below: pending, p, pending
			// elsewhere, we lock pending, p (Processor.processResponse())
			// this looks like it could deadlock, but it wont, because the other thread
			// will not execute concurrently (we spawn it after releasing the first lock on pending)
			// then, when it notifies us, it has already released both locks
			
			
			// add the request to the list of waiting requests
			synchronized (pending) {
				pending.put(new Integer(reqID),p);
			}
			
			//make a request
			DNSRequest req=new DNSRequest(reqID,recurse,name, type);
			
			//if we are a client, maybe we will need to do some preprocessing
			if( client ){
				clientPreProcessRequest(req);
			}
			
			//need to send our request and then wait for a response
			synchronized (p) {
				
				try {
					sock.sendto(server,Simnet.DNS,req);
					p.wait(TIMEOUT);
				}
				catch (SimnetSocketException sse) {
					node.printout(0,node.id,"doRemoteQuery: could not send request: "+sse);
					throw new DNSException("doRemoteQuery caught a SimnetSocketException");
				}
				catch (InterruptedException ie) {
					ie.printStackTrace();
				}
			}	
			
			
			// reacquire lock on pending
			synchronized (pending) {
				pending.remove(new Integer(reqID));
			}
			

			if (p.response==null){
				//node.printout(0,node.id,"doRemoteQuery: timeout or error");
				throw new DNSException("doRemoteQuery: Timeout or error");
			}
			
			
			// verify the contents of the query
			if( ! verify( p.response ) ){
				throw new DNSException("Could not verify contents of remote query.");
			}


			
			int i;
			LinkedList ret=new LinkedList();
			for (i=0; i<p.response.answers.length; i++) {
				//add the responses to the list we are returning
				// make sure to copy them and use the TTL that was given to us
				ret.addLast(p.response.answers[i].cache());
			}
			
			// if we are not a stub resolver cache the results to use in further requests			
			if( !client ){
				for (i=0; i<p.response.answers.length; i++) {
					cache(p.response.answers[i].cache());
				}
			}
			
			
			return ret;
			
		} // end remote query
	
	
	
	/**
	 * Put an RRGroup in the cache.
	 * @param	rrg	The RRGroup to cache.
	 */
	protected void cache(RRGroup rrg){
		
		if( client ){
			throw new Error("Clients are stupid and therefore do not cache anything");
		}
		long t = System.currentTimeMillis();
		int delayUntilRemoved = (int) (rrg.expires - t < CACHE_TTL ? rrg.expires - t : CACHE_TTL);
		
		synchronized(cache){
			//if this has already been cached, then we want to make sure that
			// it will not be removed from the previous time stamp
			CachedObject old = (CachedObject) cache.get(rrg.name);
			if( old != null ){
				old.expires = t + delayUntilRemoved;
				return;
			}
			
			//put the item in the cache
			CachedObject co = new CachedObject(rrg.name, rrg, t+delayUntilRemoved);
			cache.put(rrg.name, co);
		}
	}
	
	
	/**
		* Check the cache for an RRGroup that matches this name and type.
	 * @param	name  the name the RRGroup should match
	 * @param	type  the type of the RR that this RRGroup should hold
	 * @return  the specific RRGroup, null if there in no match
	 */
	protected RRGroup checkCache(String name, String type){
		if( client ){
			throw new Error("Clients are stupid and therefore do not cache anything!");
		}
		synchronized(cache){

			CachedObject co = (CachedObject) cache.get(name);
			if( co != null) {
				if( co.isExpired() ){
					cache.remove(name);
					return null;
				}
				RRGroup rrg = co.rrg;
				if(rrg.isExpired() ){
					cache.remove(name);
					return null;
				}
				if(rrg.hasRecord(type) ){
					return rrg.cache();
				}
			}
			return null;
		}
	}
	
	/**
	 * Determine whether this RRGroup can be used.  This method should
	 * be over-ridden to verify signatures, right now it just checks
	 * that no Resource Record has expired (it never should be).
	 * @param rrg  The RRGroup to verify
	 * @return whether or not this RRGroup should be trusted
	 */
	protected boolean verify(RRGroup rrg){
		
		Iterator it = rrg.records.iterator();
		while(it.hasNext()){
			if( ((ResourceRecord) it.next()).isExpired() ){
				return false;
			}
		}
		return true;
		 
	}
	
	/*
	 * Applies verify(RRGroup) to a list of of RRGroups in a DNSResponse
	 * @param	resp  The response we are verifying
	 * @returns Whether or not each element in the list has been verified.
	 */
	protected boolean verify(DNSResponse resp){
		//System.out.println("resp =  " + resp);
		for(int i = 0; i < resp.answers.length; ++i){
			if( ! verify( resp.answers[i]) ){
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Method to search all name servers (in our zones and cache) to get the one
	 * that is 'most' authoratative for the specified name.
	 * @param	name  the name we are querying
	 * @param	includeCache	whether or not we should search our cache too
	 * @return  a 2-RRGroup linked list holding the NS that we feel is most likely responsible
	 *				for the queried node and a RRGroup containing an A record for the NS.
	 */
	protected LinkedList getBestNS(String name, boolean includeCache){
		//first check our zones and our cache for all name servers
		// list is formatted as an alternating set of RRGroups, even entries (0,2,4...) are
		// RRGroups that contain NS RRs, odd entries contain A RRs describing the NS
		
		if( includeCache && client ){
			throw new Error("Clients don't have caches!");
		}
		
		LinkedList allNameServers = getNameServers(includeCache);
		
		LinkedList result = null;
		
		RRGroup nsRRG, aRRG;
		ResourceRecord nsRR;
		
		//keeps track of which NS knows the most about our name 
		// the one that is responsbile the zone that ends with the longest portion
		// of our name is the best bet.
		int longestMatch = 0;
		
		Iterator nsIt = allNameServers.iterator();
		while( nsIt.hasNext() ){
			//get the RRGroup holding the NS and A records
			nsRRG = (RRGroup) nsIt.next();
			aRRG = (RRGroup) nsIt.next();
			nsRR = nsRRG.getRecord("NS");
			
			//see if this NS is the longest match (our name ends with it's zone)
			if( zoneEndsWith(name, nsRR.name) && nsRR.name.length() > longestMatch ){
				longestMatch = nsRR.name.length();
				result = new LinkedList();
				result.addFirst(nsRRG);
				result.addLast(aRRG);
			}
			
		}
		
		return result;
		
	}
	
	public boolean zoneEndsWith(String name, String zoneName){
		
		if(!name.endsWith(zoneName) ){
			return false;
		}
		
		if(zoneName.equals(".")){
			return true;
		}
				
		//turns out the endsWith operator isn't what we really want here
		//foobar.com and bar.com are quite different
		String [] fParts = name.split("\\.");
		String [] sParts = zoneName.split("\\.");
		boolean match = false;
		//see ZoneData.isCannonicallyBefore for description of following logic
		int min = (fParts.length < sParts.length ? fParts.length : sParts.length);
		
		for(int i = 0; i < min; ++i){
			if(fParts[fParts.length - 1 - i].equals(sParts[sParts.length - 1 - i])){
				match = true;
				continue;
			}
			if(fParts[fParts.length - 1 -i].endsWith(sParts[sParts.length - 1 - i])){
				return false;
			}else{
				return true;
			}
			
		}
		
		return match;
		
	}
	
	
	/**
	 * Method that finds all Name servers that we know about.
	 *	Checks our zones and our cache.
	 * @param	includeCache	whether or not to check the cache
	 * @return a Linked list of alternating seta of RRGroups, even entries (0,2,4...) are
	 *			  RRGroups that contain NS RRs, odd entries contain A RRs describing the NS
	 */	
	protected LinkedList getNameServers(boolean includeCache){
		synchronized(zones){
			synchronized(cache){
				//first go though our zones database (this will be empty on clients)
				Enumeration e = zones.elements();
				ZoneData zd = null;
				LinkedList nameServers = null;
				LinkedList result = new LinkedList();
				
				RRGroup nsRRG;
				ResourceRecord nsRR;
				
				while( e.hasMoreElements() ){
					//see if this zone has any name servers
					zd = (ZoneData) e.nextElement();			
					nameServers = zd.getRRGroups("NS");
					if( nameServers == null ) continue;
					
					Iterator nsIt = nameServers.iterator();
					//for each name server that we add, we also add the A record
					// that says where it is (so we can contact it)
					while( nsIt.hasNext() ){
						nsRRG = (RRGroup) nsIt.next();
						nsRR = nsRRG.getRecord("NS");
						result.addLast(nsRRG.cache());
						result.addLast(zd.getRRGroup("A", nsRR.value).cache());
						
					}
				}
								
				if( !includeCache )
					return result;
				
				//now check our cache
				e = cache.elements();
				CachedObject co;
				while(e.hasMoreElements() ){
					co = (CachedObject) e.nextElement();

					if( co.isExpired() ){
						cache.remove(co.name);
						continue;
					}

					nsRRG = co.rrg;
					RRGroup aRRG = null;
					if(nsRRG.hasRecord("NS") ){
						nsRR = nsRRG.getRecord("NS");
						result.addLast(nsRRG.cache());
						aRRG = checkCache(nsRR.value, "A");
						if( aRRG == null ){
							result.remove(result.size() - 1);
						}else{
						//if we got a pointer to the NS, then it had to come
						// w/ an accompanying A Record
							result.addLast(aRRG);
						}
					}
					
				}
				
				return result;
			}
		}
		
	}
	
	
	/**
	 * Method to search all of our zones for an RRGroup holding an RR for a certain name/type.
	 * @param name The name we want to search.
	 * @return  the RRGroup holding the corresponding A record, null if we can't find one.
	 */
	protected RRGroup getRRGroup(String name, String type){
		RRGroup result;
		synchronized(zones){
			
			String firstName = name;
			while( !name.equals("") ){
				ZoneData zd = (ZoneData) zones.get( name );
				if( zd != null){
					result = zd.getRRGroup(type,firstName);
					if( result != null ){
						return result.cache();
					}else{
						return null;
					}
				}
				name = chop(name);
			}
			return null;
		}
	}
	
	/**
		* Check if we are authoritative for a certain name.
	 * @param	name  The name to check
	 * @return  whether or not we are authoratative for this name
	 */
	public boolean authoritativeFor(String name){
		
		// logic is as follows:
		// 1. Check our zones, if any are authoritative, then we are
		// 2. If not, check to see if we have an NS that has the longest match
		//    If one exists, then IT is responsible for the record, so we aren't
		// 3. If we have no corresponding NS, but one of our zones had a suffix match
		//    Then the record doesn't exist, but we would be authoritative for it
		
		boolean haveCorrespondingZone = false;
		
		synchronized(zones){
			Enumeration e = zones.elements();
			ZoneData zd;
			while( e.hasMoreElements() ){
				zd = (ZoneData) e.nextElement();
				if(zd.authoritativeFor(name) ){
					return true;
				}
				if( zoneEndsWith(name, zd.zone) ){
					haveCorrespondingZone = true;
					break;
				}
												
			}
		}
		
		//if we have a NS for it, then we are not authoritative...
		// unless, that NS is us!  Then, it will be the case that we are authoritative
		// DONT check our cache for NS records
		LinkedList applicableNS = getBestNS(name, false);
		if( applicableNS != null){
			
			if(applicableNS.size() < 2){
				throw new Error( "getBestNS did not return an A record");
			}
			
			RRGroup aRRG = (RRGroup) applicableNS.get(1);
			ResourceRecord aRR = aRRG.getRecord("A");
			if( aRR == null ){
				throw new Error( "getBestNS did not return an A record");
			}
			
			if( Utils.getIPFromString(aRR.value) == node.id )
				return true;
			
			return false;

		}
		
		//if we dont have an NS, but one of our zones should cover it,
		// then it doesnt exist (and so we WOULD be authoritative for it)
		return haveCorrespondingZone;
		
	}
	
	
	
	/**
		* Method that can be called from the UI to lookup an ip address.
	 * @param	name  The name we are looking up
	 */
	public void nslookup(String name){
		int answer = getHostByName(name);
		if( answer == -1 ){
			node.printout(Simnet.VC_ALWAYS, 0, node.id,
							  "nslookup( " + name + " ) failed.");
		}else{
			node.printout(Simnet.VC_ALWAYS, 0, node.id,
							  "nslookup( " + name + " ) = " + Utils.getStringFromIP(answer));
		}
		
	}
	
	
	public String getNSByName(String name){
		
		//make sure the name is formatted correctly
		if( !name.endsWith(".") ) name += ".";
		
		LinkedList queryResults;
		
		try{
			if( client ){
				queryResults = doRemoteQuery(name, "NS", nameserver, true);			
			}else{
				queryResults = doRecursiveQuery(name, "NS");
			}
		}catch(DNSException dnse){
			node.printout(Simnet.VC_ALWAYS, Simnet.VL_ERROR, node.id,
							  "Caught DNSExcpetion: " + dnse);
			return null;
		}
		
		if( queryResults == null || queryResults.size() < 1){
			return null;
		}
		
		//if this succeeded, then the first record will be an A record
		RRGroup answer = (RRGroup) queryResults.getFirst();
		if( answer.hasRecord("NS") ){
			ResourceRecord aRecord = answer.getRecord("NS");
			
			if( aRecord.name.equals(name) ){
				//System.out.println(aRecord);
				return aRecord.value;
			}
		}
		return null;
		
	}
	
	
	
	/**
	 * Performs a lookup of the specified name.
	 * @param	name  The name we are querying
	 * @return  This ip address of the name, -1 if we couldn't find an answer.
	 */
	
	public int getHostByName(String name){

		//make sure the name is formatted correctly
		if( !name.endsWith(".") ) name += ".";
		
		LinkedList queryResults;
		
		try{
			if( client ){
				queryResults = doRemoteQuery(name, "A", nameserver, true);			
			}else{
				queryResults = doRecursiveQuery(name, "A");
			}
		}catch(DNSException dnse){
			node.printout(Simnet.VC_ALWAYS, Simnet.VL_ERROR, node.id,
							  "Caught DNSExcpetion: " + dnse);
			return -1;
		}
		
		if( queryResults == null || queryResults.size() < 1){
			return -1;
		}
				
		//if this succeeded, then the first record will be an A record
		RRGroup answer = (RRGroup) queryResults.getFirst();
		if( answer.hasRecord("A") ){
			ResourceRecord aRecord = answer.getRecord("A");
			
			if( aRecord.name.equals(name) )
				return Utils.getIPFromString(aRecord.value);
		}
		return -1;
		
	}
	
	

	/**
	 * Check to see if the first item in the linked list is the answer 
	 * we are looking for -- an NX or A record that has the correct name.
	 * @param	rrgs  list or RRGroups that we are checking
	 * @param	name  the name we are looking for
	 * @return  whether or not this linked list contains an answer that we can use (A or NX)
	 */
	public boolean hasFinalAnswer(LinkedList rrgs, String name, String type){
		if( rrgs == null) return true;
		if( rrgs.size() < 1 ) return false;
		RRGroup first = (RRGroup) rrgs.getFirst();
		if( first.getRecord(type, name) != null ) return true;
		if( first.getRecord("NX", name) != null ) return true;
		return false;
		
		//return (first.name.equals(name) && 
		//		  (first.hasRecord(type) || first.hasRecord("NX")));
	}
	
	
	/**
	 * Method where local resolver can do any processing that it needs to
	 * on receiving a response.
	 * @param	resp  The response we received.
	 * @param	ip		The ip this response will be sent to.
	 */
	protected void preProcessResponse(DNSResponse resp, int ip){
		
	}

	/**
	 * Method where local resolver can do any processing that it needs to
	 * on receiving a request.
	 * @param	req  The request we received.
	 */
	protected void preProcessRequest(DNSRequest req, int ip){
		
	}

	/**
	 * Method where stub resolver can do any processing that it needs to
	 * before sending a request.
	 * @param	req  The request we will send.
	 */
	protected void clientPreProcessRequest(DNSRequest req){
		
	}
	
	/**
	 * Utility method to remove the first part of a period-delimited name.
	 */
	public String chop(String name){
		if( name.indexOf(".") == name.length() - 1 && name.length() > 1)
			return "."; //special case for root zone
		
		return name.substring(name.indexOf(".") + 1, name.length());
	}
	

	
	/**
	 * Wrapper method to display the DNS state of a resolver.
	 * (got sick of typing "dump_dns_state")
	 */
	public synchronized void dds(){ dump_dns_state(); }
	
	
	/**
	 * Prints the state (zone database, cache and pending requests).
	 */
	public synchronized void dump_dns_state(){
		String out=getClass().getName()+" Resolver State:\n";

		if( !client ){
			synchronized(zones){
				synchronized(cache){
					if (!client){
						
						out+="********Zones********\n";
						Enumeration e = zones.elements();
						while(e.hasMoreElements()){
							out+=e.nextElement() + "\n";
						}
						out+="\n";
						
						out+="********Cache********\n";
						e = cache.elements();
						while(e.hasMoreElements()){
							out+= "\t" + e.nextElement();
							if (e.hasMoreElements()) out+=",\n";
						}
						out+="\n";
						
					}
				}
			}
		}
		synchronized(pending){
			out+="Pending Request IDs["+pending.size()+"] = {";
			Iterator it=pending.keySet().iterator();
			while (it.hasNext()) {
				out+=it.next();
				if (it.hasNext()) out+=",";
			}
		}
		out+="}\n";
		
		node.printout(0,node.id,out);		
		
	}
	
	
	/**
	 * Thread that handles the processing of all requests/responses.
	 */
	class Processor extends Thread{
		public void run() {
			setName(node.name+"-DNS-Processor");
			try {
				//get a message
				while (!sock.isClosed()) {
					DatagramPacket p=sock.recvfrom();
					node.printout(0, node.id, "Packet size: " + p.getSize());
					//request or response?
					if (p.data instanceof DNSResponse) {
						DNSResponse res=(DNSResponse)p.data;
						node.printout(Simnet.VC_DNS, Simnet.VL_COMPLETE,  node.id,
										  "Received response");
						processResponse(res);
					} else if (p.data instanceof DNSRequest) {
						processRequest(p);
					} else
						node.printout(Simnet.VC_DNS,Simnet.VL_ERROR,node.id,"DNS got an invalid object: "+
										  ((p.data==null)?"<null>":p.data.getClass().getName()));
				} // end while socket is open socket
			}
			catch (Exception e) {
				node.printout(Simnet.VC_DNS,0,node.id,"DNS Processor thread got exception: "+e);
			}
			node.printout(Simnet.VC_DNS,0,node.id,"DNS Processor thread finished!");
		}
		
		/**
		* Processes a DNS request.
		 * @param p The packet holding the request.
		 */
		protected void processRequest(DatagramPacket p) {
			
		    long startTime, endTime; 
		    startTime = System.currentTimeMillis();
			if( client ){
				throw new Error("Clients cannot process requests");
			}
			
			
			
			DNSRequest req=(DNSRequest)p.data;
			node.printout(Simnet.VC_DNS,Simnet.VL_COMPLETE,node.id,"Received a DNS Request: "+req);
			
			node.printout(0, node.id, "Type: " + req.type);
			
			// if the request is recursive, start a thread to deal with it,
			if (req.recursive) {
			    //node.printout(0, node.id, "Recursive!");
				//first leg of the request sequence
				preProcessRequest( req , p.ip);
				RecursiveRequestProcessor rrp=new RecursiveRequestProcessor(p);
				rrp.start();
			} else {
			    //node.printout(0, node.id, "Iterative!");
				// otherwise, do the lookup and return a response
				// ith leg of the request sequence, i > 1
			    LinkedList relevantRecords=doIterativeQuery(req.name, req.type);
				
			    node.printout(Simnet.VC_DNS,Simnet.VL_COMPLETE,node.id,
								  "sending iterative response to:" + req.name + ", " + req.type);
				
				DNSResponse resp=new DNSResponse(req.id,req.name,relevantRecords.size());
				relevantRecords.toArray(resp.answers);
				
				
				try {
					sock.sendto(p.ip,p.port,resp);
				}
				catch (SimnetSocketException sse) {
					node.printout(0,node.id,"Could not send iterative response: "+sse);
				} // end try to send iterative response
			} // end if request is recursive
			endTime = System.currentTimeMillis();
			node.printout(0, node.id, "Req process took: " + (endTime - startTime));
		} // end process request
		
		/**
		* Process a response to a DNS request that we issued [hopefully ;-)]
		*/
		protected void processResponse(DNSResponse res) {
			
			synchronized (pending) {
				PendingRequest p=(PendingRequest)pending.get(new Integer(res.id));
				if (p==null) {
					node.printout(Simnet.VC_DNS,Simnet.VL_DEBUG,node.id,"DNS got non-pending response, id="+res.id);
				} else {
					synchronized (p) {
						node.printout(Simnet.VC_DNS,Simnet.VL_COMPLETE,node.id,"DNS got valid response "+res);
						p.response=res;
						
						
						
						p.notify();
					}
				} // end if got response to a pending request
			}
			
		}
	}
	
	
	
	/**
	 * A thread that processes a recursive request and returns a response to the sender.
	 */
	class RecursiveRequestProcessor extends Thread {
		
		/** packet that started it all off */
		protected DatagramPacket p;
		
		/** the request */
		protected DNSRequest req;
		
		/** name we are looking for */
		protected String name;
		
		/** type we are looking for */
		protected String type;
		
		protected long startTime, endTime;
		
		public RecursiveRequestProcessor(DatagramPacket p) {
			this.p=p;
			req = (DNSRequest) p.data;
			name = req.name;
			type = req.type;
			
		}
		
		public void run() {
		    setName(node.name+"-DNS-RRProc(" + name + ", " + type +")" );
			
			//do a recursive query for the name
			LinkedList relevantRecords;
			DNSResponse resp;
			
			startTime = System.currentTimeMillis();
			
			try{
				relevantRecords = doRecursiveQuery(name, type);
				//populate our response with the records we were able to obtain
				
				resp=new DNSResponse(req.id,req.name,relevantRecords.size());
				relevantRecords.toArray(resp.answers);
			}catch(DNSException e){
				node.printout(Simnet.VC_ALWAYS, 0, node.id,
								  "lookup failed returning empty response");
				resp = new DNSResponse(req.id, req.name, 0);
				resp.answers = new RRGroup[0];
			}

			
			//send the response
			try {
				// need to do any specific preprocessing here? (hint, hint)
				preProcessResponse(resp, p.ip);
				
				sock.sendto(p.ip,p.port,resp);
				
				endTime = System.currentTimeMillis();
				
				node.printout(Simnet.VC_DNS_BM, 0, node.id, "\nRRPT\t" + node.name + "\t" + name + "\t" + 
								  type + "\t" + startTime + "\t" + (endTime - startTime) );
				
			}
			catch (SimnetSocketException sse) {
				node.printout(Simnet.VC_ALWAYS, 0,node.id,"Could not send recursive response: "+sse);
			}
		}
	}
	
	/**
		*An object for doRemoteQuery to wait for, and to contain the response packet.
	 */
	class PendingRequest {
		public DNSResponse response=null;
		public PendingRequest() {}
	}
	
	/**
		* TimerTask to remove stale items from the cache.
	 */
	class CacheCleaner extends TimerTask{
		
		
		public void run(){
			Thread.currentThread().setName( node.name + "-CACHE-CLEANER");
			synchronized(cache){
				String key;
				CachedObject co;
				Enumeration e = cache.keys();
				
				//cycle over all elements in the cache
				while(e.hasMoreElements()){
					key = (String) e.nextElement();
					co = (CachedObject) cache.get(key);
					
					//time to remove it...
					if( co.isExpired() ){
						cache.remove(key);
						node.printout(Simnet.VC_DNS, Simnet.VL_DEBUG, node.id,
										  "Removed RRGroup associated with: " + key);
						
					}
				}
				
			}
		}
	}
}

/**
 * An object to hold elements stored in cache.
 */
class CachedObject{

	/** The RRGroup to be cached */
	RRGroup rrg;
	
	/** The name of the RRGroup to be cached */
	String name;
	
	/** When this object should be removed from cache */
	long expires;
	
	/** Determines if this element should now be removed from cache */
	public boolean isExpired(){
		return expires < System.currentTimeMillis();
	}
	
	public CachedObject(String name, RRGroup rrg, long expires){
		this.name = name;
		this.rrg = rrg;
		this.expires = expires;
	}
	
	public String toString(){
		return rrg.toString();
	}
	
}



/** Exception that is thrown when any DNS verification fails */
class DNSException extends Exception{
	public DNSException(String s){
		super(s);
	}
}
