////////////////////////////////////////////////////////////////////
// Simnet, version 0.9                                            //
// COPYRIGHT Seny Kamara, Darren Davis, Fabian Monrose, 2002-2004 //
////////////////////////////////////////////////////////////////////
package A3;

import simnet.*;
import java.security.SecureRandom;
import java.math.BigInteger;
import java.io.*;
import java.util.*;
import org.bouncycastle.crypto.generators.DSAKeyPairGenerator;
import org.bouncycastle.crypto.generators.DSAParametersGenerator;
import org.bouncycastle.crypto.params.DSAParameters;
import org.bouncycastle.crypto.params.DSAKeyGenerationParameters;
import org.bouncycastle.crypto.params.DSAPublicKeyParameters;
import org.bouncycastle.crypto.params.DSAPrivateKeyParameters;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.util.encoders.Hex;

/**
 * Resolver class to secure DNS
 */
public class DNSSEC extends DNS{

	//variables used for reading files
	//  we will assume that all DNSSEC files will be in the networks/dnssec directory

	/** Directory containing DNSSEC info */
	public final static String FILE_PREFIX   = "networks" + File.separator + "dnssec" + File.separator;

	/** Prefix for files containing RSA keys */
	public final static String RSA_PREFIX    = "RSA.KEY.";

	/** Prefix for files containing DSA keys */
	public final static String DSA_PREFIX    = "DSA.KEY.";

	/** Prefix for files containing KEY RRs */
	public final static String KEY_RR_PREFIX = "KEY.RR.";

	/** Prefix for files containing SIG RRs */
	public final static String SIG_RR_PREFIX = "SIG.RR.";

	/** Handle our crypto */
	protected SimnetCryptoEngine			sce;

	/**
	 * RSA private key for local resolvers -- so they can decrypt session
	 * keys created by clients.
	 */
	protected RSAKeyParameters				rsaPrivateKey;

	/** RSA public key so stub resolvers can encrypt session keys for local resolvers */
	protected RSAKeyParameters				resolverRSAPublicKey;

	/** The roots DSA key, each resolver gets a copy */
	protected DSAPublicKeyParameters		rootDSAPublicKey;

	/** Timer to fire threads to update the signatures in our zone `file` */
	protected Timer zoneUpdater;

	/** Stores the DSA secret keys for each zone we are responsible for, keyed by zone name */
	protected Hashtable zoneKeys;

	/** Holds session keys to authenticate TSIGS, keyed by request source ip. */
	protected Hashtable tsigKeys;

	/**
	 * Table to hold RRGroups that we have authenticated (to reduce signature verifications).
	 * Keyed by RRGroup name.
	 */
	protected Hashtable authenticatedRRs;

	/**
	 * Table to hold keys that we have authenticated (to reduce signature verifications).
	 * Keyed by zone name.
	 */
	protected Hashtable authenticatedKeys;

	protected byte[] MACkey = null;

	/**
	 * Method to read data from a DNS data file.  This method just uses DNS's
	 * version and then reads the necessary keys and allocated memory/starts threads
	 * @param	config	Lines from the file
	 */
	public void customInit(String config[]) throws Exception {

		super.customInit(config);

		sce = new SimnetCryptoEngine();

		tsigKeys = new Hashtable();

		//if we are a server, then we need to read in our private keys.
		// (our public key is in our KEY RR, we don't need to `know` it)

		//if we are a client then we need to read in the RSA private key of our resolver

		if(!client){

			rsaPrivateKey = readRSAPrivateKey(FILE_PREFIX + RSA_PREFIX + "_" + node.name + "_");
			rootDSAPublicKey = readDSAPublicKey(FILE_PREFIX + DSA_PREFIX + "_._");

			zoneKeys = new Hashtable();
			authenticatedRRs = new Hashtable();
			authenticatedKeys = new Hashtable();

			//for each of our zones, we need to get the necessary keys
			// we'll also sign the zone and add NXT records
			synchronized( zones ){
				synchronized( zoneKeys ){
					Enumeration e = zones.elements();
					while( e.hasMoreElements() ){
						ZoneData zd = (ZoneData) e.nextElement();
						String zoneName = zd.zone;

						//get the private key for this zone so we can sign things
						DSAPrivateKeyParameters privateKey = readDSAPrivateKey( FILE_PREFIX + DSA_PREFIX + "_" + zoneName + "_");
						zoneKeys.put(zoneName, privateKey);


						//read the key RR for this zone
						ResourceRecord keyRR = getRRFromFile( FILE_PREFIX + KEY_RR_PREFIX + "_" + zoneName + "_");
						zd.addRR( keyRR.name, keyRR, true);

						//read the sig of the key RR for this zone
						ResourceRecord sigRR = getRRFromFile( FILE_PREFIX + SIG_RR_PREFIX + "_" + zoneName + "_");
						zd.addRR( sigRR.name, sigRR, true);

						createNXTRecords(zd);
						signZone(zd, privateKey);
						//generate SIG and NXT records
					}
				}
			}

			//schedule a TimerTask to periodically resign our zone
			//  add a small random delay so timers don't always fire at the same time
			zoneUpdater = new Timer();
			zoneUpdater.schedule(new ZoneUpdater(), 0, RR_DURATION - random.nextInt(500));

		}else{
			//we are a lowly stub resolver, just need to read in our local resolvers pub. key
			resolverRSAPublicKey = readRSAPublicKey(FILE_PREFIX + RSA_PREFIX + "_" + resolverName + "_");
		}
	}

	/**
	 * Called by stub resolvers to generate a random session key.
	 * Stores the key, encrypts it and puts the result in the request
	 * @param	req	the DNS request to hold the key
	 */

	protected void clientPreProcessRequest(DNSRequest req){
		if(MACkey == null) { MACkey = sce.makeMacKey(); }
		req.tsigKey = sce.encryptRSA(MACkey, resolverRSAPublicKey);
	}

	/**
	 * Called by local resolver to compute TSIGs and add them to responses
	 * @param resp The response to MAC
	 * @param ip	The ip address of the stub that made this request
	 */
	protected void preProcessResponse(DNSResponse resp, int ip){
		byte[] tsigKey = (byte[]) tsigKeys.get(new Integer(ip));
		resp.tsig = sce.HMacMD5Object(resp.answers, tsigKey);
	}

	/**
	 * Called by local resolver upon receiving a recursive request.
	 * Extracts the encrypted session key and stores it for later use.
	 * @param	req	The request to preProcess
	 * @param ip	The ip address of the stub that made this request
	 */
	protected void preProcessRequest(DNSRequest req, int ip){
		if(tsigKeys.containsKey(new Integer(ip)) == false) {
			try {
				byte[] decryptedKey = sce.decryptRSA(req.tsigKey, rsaPrivateKey);
				tsigKeys.put(new Integer(ip), decryptedKey);
			} catch(Exception e) {
				node.printout(0, node.id, e.toString());
			}
		}
	}

	/**
	 * Method that checks our zones database and the cache for information
	 * pertaining to to the specified name/type.
	 * @param	name  The name of the node we are looking up.
	 * @param	type  The type of the record we are looking up
	 * @return  A LinkedList containing pertinent RRGroups.  If we found an A or SOA/NXT RR,
	 *				it will be the first(first/second) RRGroups in the list.  If we have an NS record, it will be the
	 *				first in the list and will be followed by an A record indicating the IP address
	 *				of the NS.  Probably shouldn't rely on ordering, but it works.
	 */
	public LinkedList doIterativeQuery(String name, String type){
		node.printout(Simnet.VC_DNS, Simnet.VL_COMPLETE, node.id, "doIterativeQuery() called with name = " + name + ", " + type);

		if( client ){ throw new Error("CANNOT run iterative query from a client."); }

		//will hold a set of RRGroups pertinent to this query
		//the most recent (usefull) first
		LinkedList results = new LinkedList();

		//do we have an RRGroup for this request?
		// not checking authoritative first because we might have the record
		// but not be authoritative for it
		RRGroup rrg = getRRGroup(name, type);
		if(rrg != null) {
			//we will be returning this, so make sure that it is a copy
			node.printout(Simnet.VC_DNS, Simnet.VL_COMPLETE, node.id, "Found the RRGroup: " + rrg.name);
//			results.addFirst(rrg.cache(RR_DURATION));
			results.addFirst(rrg.cache());
			return results;
		}

		//if we are authoritative for this name then the RR requested doesn't
		// exist, return SOA/NXT records.
		if(authoritativeFor(name)) {
			node.printout(Simnet.VC_DNS, Simnet.VL_DEBUG, node.id, "No RRGroup, returning SOA/NXT");
			//we don't have one (since we are authoritative, it must not exist)

//			node.printout(0, node.id, "Zones: " + zones.size());

			// Get our zone
			Enumeration zds = zones.elements();
			ZoneData zd = (ZoneData) zds.nextElement();

			//get the rrg with SOA record
			LinkedList rrgs = zd.getRRGroups("SOA");
			RRGroup soa = (RRGroup) rrgs.getFirst();

			//make sure to copy/update expiration time
			results.addFirst(soa.cache());

			// get the rrgs with NXT records
			rrgs = zd.getRRGroups("NXT");
			RRGroup nxt = null;

			//get the rrg with correct NXT record
			for(int index = 0; index < rrgs.size(); index++) {
				RRGroup group = (RRGroup) rrgs.get(index);
				ResourceRecord record = group.getRecord("NXT");
				if((record.name.equals(name) || isCannonicallyBefore(record.name, name)) && (name.equals(record.next) || isCannonicallyBefore(name, record.next))) {
					nxt = group;
					break;
				}
			}

			//make sure to copy/update expiration time
			results.add(nxt.cache());

			return results;
		}

		//we are not authoritative, but lets check our cache!
		node.printout(Simnet.VC_DNS, Simnet.VL_COMPLETE, node.id, "We are NOT authoratative for this name: " + name + " checking cache...");

		RRGroup cachedRRG = checkCache(name, type);

		if(cachedRRG != null) {
			//we got a cache hit, return that (don't update the TTL)
			node.printout(Simnet.VC_DNS, Simnet.VL_COMPLETE, node.id, "got a cache hit:" + cachedRRG);
			//checkCache returns a cloned version of this RRG, we don't need to do it here
			results.addFirst(cachedRRG);
			return results;
		}

		//cache miss
		node.printout(Simnet.VC_DNS, Simnet.VL_COMPLETE, node.id, "cache miss");

		//now we need to get the closest name server that could process this
		// request.  check to see if we have an NS in any of our zones or in our cache
		// if not, return a pointer to the root NS

		node.printout(Simnet.VC_DNS, Simnet.VL_COMPLETE, node.id, "Finding most accurate NS:");

		//return the NS/A pair that is most applicable to our query
		LinkedList nsRRG = getBestNS(name, true);

		if(nsRRG == null) {
			//no name servers that are applicable to this query, return a pointer to the root
			node.printout(Simnet.VC_DNS, Simnet.VL_COMPLETE, node.id, "We dont even have a name server, returning the ROOT");

			//copy and update TTL
//			results.addFirst(rootARRG.cache(RR_DURATION));
//			results.addFirst(rootNSRRG.cache(RR_DURATION));
			results.addFirst(rootARRG.cache());
			results.addFirst(rootNSRRG.cache());

			return results;
		}

		//copy/update TTL on each record
		Iterator it = nsRRG.iterator();
		while(it.hasNext())
//			results.addLast(((RRGroup) it.next()).cache(RR_DURATION));
			results.addLast(((RRGroup) it.next()).cache());
		return results;
	} //close doIterativeQuery

	/**
	 * Examines a linked list and determines whether or not it has an answer
	 * to the query specified by name/type.
	 * @param	rrgs  The list to process (remember, most relevant records first)
	 * @param	name  Name of the RR requested
	 * @param	type  Tyep of the RR requested
	 * @return			True if we have the record we are looking for, or info
	 *						that indicates that it doesn't exist
	 */
	public boolean hasFinalAnswer(LinkedList rrgs, String name, String type){
		if( rrgs == null) return true;
		if( rrgs.size() < 1 ) return false;
		RRGroup first = (RRGroup) rrgs.getFirst();
		if( first.getRecord(type, name) != null ) return true;
		RRGroup second = (RRGroup) rrgs.get(1);
		if((first.getRecord("SOA") != null) && (second.getRecord("NXT") != null)) {
			if((verify(first) == true) && (verify(second) == true)) {
				ResourceRecord nxt = second.getRecord("NXT");
				if((nxt.name.equals(name) || isCannonicallyBefore(nxt.name, name)) && (name.equals(nxt.next) || isCannonicallyBefore(name, nxt.next))) { return true; }
			}
		}
		return false;
	}

	/**
	 * Checks whether a dns name comes before another (see {@link ZoneData} for a better description).
	 * @param	f  the first name we are checking
	 * @param	s  the second name we are checking
	 * @return  if the first name comes before the second
	 */

	public boolean isCannonicallyBefore(String f, String s){
			//separate the names based on delimiter: www.jhu.edu --> [www][jhu][edu]
		String [] fParts = f.split("\\.");
		String [] sParts = s.split("\\.");

		//we need to start checking at the end, and then moving backwards,
		// but we don't want to 'run off the front' of an array, so see which is shorter
		int min = (fParts.length < sParts.length ? fParts.length : sParts.length);

		//start at the end and compare the highest-level domains of the two names
		// if one is alphabetically before the other, that one is cannonically first
		for(int i = 0; i < min; ++i){
			if(fParts[fParts.length - 1 - i].equals(sParts[sParts.length - 1 - i]))
				continue;
			return (fParts[fParts.length - 1 - i].toLowerCase().compareTo(sParts[sParts.length - 1 - i].toLowerCase()) < 0);

		}
		//if all of the domains are the same, then the shorter of the two names comes first
		return fParts.length < sParts.length;
	}

	/**
	 * Verifies (cryptographically) that a response is valid.
	 * @param	resp  The response to verify
	 * @return  whether we were able to verify the response
	 */
	protected boolean verify(DNSResponse resp){
		// Client aka stub resolvers check TSIG
		if(client) {
			byte[] tsig = sce.HMacMD5Object(resp.answers, MACkey);
			return sce.isEqual(tsig, resp.tsig);
		}

		// Local resolvers need to check each RRGroup in the response
		for(int groupIndex = 0; groupIndex < resp.answers.length; groupIndex++) {
			RRGroup rrg = resp.answers[groupIndex];
			if (verify(rrg) == false) { return false; }
		}

		return true;
	}

	/**
	 * Called by local resolvers to verify a RRGroup.  Uses DSA to verify each SIG RR.
	 * If we don't have the key we need, then we need to make a remote query to get it.
	 */
	protected boolean verify(RRGroup rrg) {
		// Expiration check
		if(rrg.isExpired()) { return false; }

		// If this rrg is a rootNSRRG, check for match
		if(rrg.name.equals(".")) { return rrg.equals(rootNSRRG); }

		// If this rrg is a rootARRG, check for match
		if(rrg.name.equals(ROOTNAME)) { return rrg.equals(rootARRG); }

		// If this rrg was already verified, check for match
		if(authenticatedRRs.containsKey(rrg.name)) {
			RRGroup verified = (RRGroup) authenticatedRRs.get(rrg.name);
			if (verified.equals(rrg)) { return true; }
		}

		// Cryptographic verification
		LinkedList records = rrg.records;

		// For each record
		for(int index = 0; index < records.size(); index++) {
			ResourceRecord record = (ResourceRecord) records.get(index);

			// If it's not a SIG record
			if(!record.type.equals("SIG")) {
				ResourceRecord rSIG = null;

				// Look for the corresponding SIG record
				for(int i = 0; i < records.size(); i++) {
					ResourceRecord r = (ResourceRecord) records.get(i);
					if(r.type.equals("SIG") && r.typeCovered.equals(record.type)) { rSIG = r; }
				}

				// If we can't find the SIG record for this record, fail
				if(rSIG == null) { return false; }

				DSAPublicKeyParameters key = null;

				// If the signer was self, get the key from group
				if(rSIG.signer.equals(rrg.name)) {
					key = rrg.getRecord("KEY").key.toDSAPublicKey();
				// If the signer was root, get the root key
				} else if(rSIG.signer.equals(".")) {
					key = rootDSAPublicKey;
				// If the signer key is already stored, fetch it
				} else if(authenticatedKeys.containsKey(rSIG.signer)) {
					key = (DSAPublicKeyParameters) authenticatedKeys.get(rSIG.signer);
				// If we still don't have a key, do remote query
				} else {
					// Get best nameserver's A record
					LinkedList best = getBestNS(rSIG.signer, true);

					if(best == null) { node.printout(0, node.id, "No NS"); }

					RRGroup second = (RRGroup) best.get(1);
					ResourceRecord A = second.getRecord("A");

					LinkedList answers = new LinkedList();

					// Send remote query
					try {
						answers = doRemoteQuery(rSIG.signer, "KEY", Utils.getIPFromString(A.value), false);
					} catch(Exception e) {
						node.printout(0, node.id, e.toString());
					}

					// For each answer in the response
					for(int i = 0; i < answers.size(); i++) {
						RRGroup answer = (RRGroup) answers.get(i);

						// If answer has key, fetch it
						if(answer.getRecord("KEY") != null) {
							key = answer.getRecord("KEY").key.toDSAPublicKey();
						}
					}
				}

				// If we can't get the key, fail
				if(key == null) { return false; }

				// Verify signature
				if(sce.DSAVerifyObjectSignature(record, rSIG.signature, key) == false) { return false; }

				// Store authenticated key
				authenticatedKeys.put(rSIG.signer, key);
			}
		}

		// Store authenticated RRG
		authenticatedRRs.put(rrg.name, rrg);

		return true;
	}

	/**
	 * Cycles over each zone and makes NXT records.
	 * @param	zd The Zone information
	 */
	protected void createNXTRecords(ZoneData zd){
		RRGroup rrg;
		for(int i = 0; i < zd.names.size(); ++i){
			rrg = (RRGroup) zd.names.get(i);
			// note that if this is the last RRGroup in our zone, we need the NXT
			// record to point to the SOA RRGroup, otherwise, just point to the next RRGroup
			String next = (i == zd.names.size() - 1 ?
								((RRGroup) zd.names.getFirst()).name :
								((RRGroup) zd.names.get(i+1)).name);

			ResourceRecord nrr = new ResourceRecord("NXT", rrg.name, -1);

			//sort of ugly, but we KNOW that this will eventually have SIGs and NXT,
			// so we need to add SIG and NXT types :-(
			ArrayList types = rrg.getTypes();

			//deal with the case of when a SIG is already there (for KEY RRs)
			if( types.indexOf( "SIG" ) == -1 )
				types.add("SIG");
			types.add("NXT");

			nrr.makeNXTRecord(next, types);
			rrg.records.addLast(nrr);

		}
	}

	/**
	 * Sign this zone.  Computes the DSA for each Resource Record in each group.
	 * Adds the appropriate SIG record to each RRGroup (it ignores SIGs for keys though)
	 * @param	zd The Zone we are processing
	 * @param	privateKey  The DSA private key for this zone
	 */
	protected void signZone(ZoneData zd, DSAPrivateKeyParameters privateKey){
		// Remove non-KEY SIG records
		zd.adjustSIGRecords();

		// Update durations
		zd.updateDurations(RR_DURATION);

		LinkedList rrgs = zd.names;

		// For each RRGroup
		for(int groupIndex = 0; groupIndex < rrgs.size(); groupIndex++) {
			RRGroup rrg = (RRGroup) rrgs.get(groupIndex);
			LinkedList rrs = rrg.records;
			LinkedList newSigs = new LinkedList();

			// For each RR
			for(int recordIndex = 0; recordIndex < rrs.size(); recordIndex++) {
				ResourceRecord rr = (ResourceRecord) rrs.get(recordIndex);

				// If it's not a KEY or SIG RR
				if((rr.type.equals("KEY") == false) && (rr.type.equals("SIG") == false)) {
					// Generate new signature
					DSASignature signature = sce.DSASignObject(rr, privateKey);

					// Create new SIG record
					ResourceRecord sigRR = new ResourceRecord("SIG", rr.name, RR_DURATION);
					sigRR.expires = rr.expires;
					sigRR.makeSIGRecord(rr.type, rr.name, zd.zone, signature);

					// Store new SIG record
					newSigs.add(sigRR);
				}
			}

			// Add new SIG records to RRGroup
			rrs.addAll(newSigs);
		}
	}

	/** print everything out */
	public void dump_dns_state(){
		super.dump_dns_state();

		String out = "\n*************Authenticated RRGroups****************";
		Enumeration e = authenticatedRRs.elements();
		while( e.hasMoreElements() )
			out+="\n\t" + e.nextElement();

		out += "\n*************Authenticated Keys*****************";
		e = authenticatedKeys.elements();
		while( e.hasMoreElements() )
			out+="\n\t" + e.nextElement();

		out +="\n*************Hardcoded Root Information*****************";
		out +="\n\tROOT NS PTR: " + rootNSRRG;
		out +="\n\tROOT A PTR: " + rootARRG;

		out +="\n*************TSIG Keys*****************\n";
		e = tsigKeys.keys();
		while( e.hasMoreElements() ){
			Integer id = (Integer) e.nextElement();
			byte [] key = (byte []) tsigKeys.get( id );
			out+="\t" + id + "-->" + Utils.toHexString( key ) +"\n";
		}


		node.printout(Simnet.VC_ALWAYS, 0, node.id, out);
	}


	// the following four methods are very redundant, oh well

	/** Method to read DSA Private keys from disk */
	private DSAPrivateKeyParameters readDSAPrivateKey(String file){
		BigInteger [] values = new BigInteger[ 5 ];

		try{

			BufferedReader in = new BufferedReader( new FileReader( file ) );
			in.readLine(); //skip header
			String [] parts;
			for(int i = 0; i < values.length; ++i){
				parts = in.readLine().split("\t");
				values[i] = new BigInteger( parts[1] );
			}
		}catch(IOException ioe){
			node.printout(Simnet.VC_ALWAYS, Simnet.VL_ERROR, node.id,
							  "Could not read DSA private key for " + file);
		}

		DSAParameters params = new DSAParameters(values[0], values[1], values[2]);
		DSAPrivateKeyParameters priv = new DSAPrivateKeyParameters(values[3], params);
		return priv;
	}

	/** Method to read DSA Public keys from disk */
	private DSAPublicKeyParameters readDSAPublicKey(String file){
		BigInteger [] values = new BigInteger[ 5 ];

		try{

			BufferedReader in = new BufferedReader( new FileReader( file ) );
			in.readLine(); //skip header
			String [] parts;
			for(int i = 0; i < values.length; ++i){
				parts = in.readLine().split("\t");
				values[i] = new BigInteger( parts[1] );
			}
		}catch(IOException ioe){
			node.printout(Simnet.VC_ALWAYS, Simnet.VL_ERROR, node.id,
							  "Could not read DSA public key for " + file);
		}

		DSAParameters params = new DSAParameters(values[0], values[1], values[2]);
		DSAPublicKeyParameters pub = new DSAPublicKeyParameters(values[4], params);
		return pub;
	}

	/** Method to read RSA Private keys from disk */
	private RSAKeyParameters readRSAPrivateKey(String file){
		BigInteger [] values = new BigInteger[ 3 ];

		try{

			BufferedReader in = new BufferedReader( new FileReader( file ) );
			in.readLine(); //skip header
			String [] parts;
			for(int i = 0; i < values.length; ++i){
				parts = in.readLine().split("\t");
				values[i] = new BigInteger( parts[1] );
			}
		}catch(IOException ioe){
			node.printout(Simnet.VC_ALWAYS, Simnet.VL_ERROR, node.id,
							  "Could not read RSA private key for " + file);
		}

		return new RSAKeyParameters(true, values[0], values[2]);

	}

	/** Method to read RSA Public keys from disk */
	private RSAKeyParameters readRSAPublicKey(String file){
		BigInteger [] values = new BigInteger[ 3 ];

		try{

			BufferedReader in = new BufferedReader( new FileReader( file ) );
			in.readLine(); //skip header
			String [] parts;
			for(int i = 0; i < values.length; ++i){
				parts = in.readLine().split("\t");
				values[i] = new BigInteger( parts[1] );
			}
		}catch(IOException ioe){
			node.printout(Simnet.VC_ALWAYS, Simnet.VL_ERROR, node.id,
							  "Could not read RSA public key for " + file);
		}

		return new RSAKeyParameters(false, values[0], values[1]);

	}

	/** Method to get an RR from disk */
	private ResourceRecord getRRFromFile(String file){
		ResourceRecord rr = null;
		try{
			ObjectInputStream ois = new ObjectInputStream( new FileInputStream( file ) );
			rr = (ResourceRecord) ois.readObject();
			ois.close();
		}catch(Exception e){
			node.printout(Simnet.VC_ALWAYS, Simnet.VL_ERROR, node.id,
							  "Could not read RR record from file: " + file);
		}
		return rr;

	}

	/**
	 * TimerTask that wakes up every RR_DURATION - rand()*500 ms, and updates RR_DURATION and signatures
	 * for all records (except KEYs and their SIGs).
	 */
	class ZoneUpdater extends TimerTask{
		public void run(){
			//you should make a call to (and implement) signZone()
			// make sure to avoid race conditions...

			Thread.currentThread().setName(node.name + "-ZoneUpdater");

			synchronized( zones ){
				synchronized( zoneKeys ){
					Enumeration e = zones.elements();
					while( e.hasMoreElements() ){
						ZoneData zd = (ZoneData) e.nextElement();
						String zoneName = zd.zone;

						DSAPrivateKeyParameters privateKey = (DSAPrivateKeyParameters) zoneKeys.get(zoneName);
						signZone(zd, privateKey);
					}
				}
			}
		}
	}
}
