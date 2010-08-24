////////////////////////////////////////////////////////////////////
// Simnet, version 0.9                                            //
// COPYRIGHT Seny Kamara, Darren Davis, Fabian Monrose, 2002-2004 //
////////////////////////////////////////////////////////////////////
package A2;

import simnet.*;
import java.util.*;
import java.io.*;
import org.bouncycastle.util.encoders.Hex;

/**
 * Class that defines all Kerberos servers (not Kerberized-services).
 * Has three inner classes.  The KDC handles initial authentication,
 * the TGS provides tickets to Kerberized-services, and the KDBM handles
 * all administrative requests.
 */
public class KerberosAuthenticationServers extends KerberosApplication implements Pluggable{



	/**
	 * UDP port on which the KDC listens
	 */
	public final static int AUTH_PORT    = 88;

	/**
	 * UDP port on which the TGS listens
	 */
	public final static int TGS_PORT     = 89;

	/**
	 * UDP port on which the KDBM listens
	 */
	public final static int KDBM_PORT    = 90;

	/**
	 * Timeout for all sockets.  Assume that this is long enough for
	 * RTT and end-host processing
	 */
	public final static long SOCKET_TIMEOUT = 500;

	/**
	 * Maximum duration of the Ticket-Granting ticket
	 */
	public final static long TGT_DURATION    = 6000;

	/**
	 * Maximum duration of a ticket to the KDBM
	 */
	public final static long KDBM_DURATION   = 1000;

	/**
	 * Maximum duration for a ticket to an end service
	 */
	public final static long TICKET_DURATION = 3000;

	/**
	 * Maximum duration of an Authenticator
	 */
	public final static long AUTH_DURATION = 500;


	/**
	 * A key to index into hash tables for keys and tickets associated with the TGS.
	 */
	public final static String TGS_INDEX_KEY  = "krbtgs";

	/**
	 * The first part of the name of the TGS.  For communication,
	 * a fully-qualified name is required, e.g., TGS_SRVC@ISI
	 */
	public final static String TGS_SRVC       = "krbtgs";

	/**
	 * A key to index into hash tables for keys and tickets associated with the KDBM.
	 */
	public final static String KDBM_INDEX_KEY = "krbkdbm";

	/**
	 * The first part of the name of the KDBM.  For communication,
	 * a fully-qualified name is required, e.g., KDBM_SRVC@ISI
	 */
	public final static String KDBM_SRVC      = "krbkdbm";

	/**
	 * Inner Thread that handles all initial authentication requests.
	 */
	private KDC  kdc;

	/**
	 * Inner Thread that handles all requests for tickets to Kerberized-services.
	 */
	private TGS  tgs;

	/**
	 * Inner Thread that handles all administrative requests.
	 */
	private KDBM kdbm;

	/**
	 * Stores all user keys.  Keys are indexed by user name (not fully-qualified -- e.g., "alice").
	 * This database is shared between the KDC and KDBM and is populated when the
	 * class Kerberos is plugged in.  User keys are read from the file krb.db and
	 * are preceded by the fixed string "usr".
	 */
	private Hashtable authKeys;

	/**
	 * Stores all service keys.  Keys are indexed by service name (fully-qualified -- e.g., "echo@JHU").
	 * This database is shared between the TGS and KDBM and is populated when the
	 * class Kerberos is plugged in.  User keys are read from the file krb.db and
	 * are preceded by the fixed string "srvc".
	 */
	private Hashtable srvcKeys;

	/**
	 * Primary method of the class.  Initializes the databases and binds Datagram sockets.
	 * Passes these objects to the constructors of the threads (<code>KDC</code>, <code>TGS</code>,
    * and <code>KDBM</code>) and starts these threads.
	 */
	public void run(){
		authKeys = new Hashtable();
		srvcKeys = new Hashtable();

		DatagramSocket dg0 = ((UDP) node.getTransport(Simnet.PROTO_UDP)).createDatagramSocket(this);
		DatagramSocket dg1 = ((UDP) node.getTransport(Simnet.PROTO_UDP)).createDatagramSocket(this);
		DatagramSocket dg2 = ((UDP) node.getTransport(Simnet.PROTO_UDP)).createDatagramSocket(this);

		//pass in the datagram sockets because they can't create their own
		//kdbm/tgs MUST be created first or the other threads won't be able to get their keys
		kdbm = new KDBM( authKeys, srvcKeys, dg2 );

		tgs  = new TGS( srvcKeys, dg1 );

		byte [] tgsKey, kdbmKey;
		synchronized(srvcKeys){
			printout(0, node.id, System.currentTimeMillis()+"");
			tgsKey = (byte []) srvcKeys.get(TGS_INDEX_KEY + "@" + node.name);
			printout(0, node.id, System.currentTimeMillis()+"");
			kdbmKey = (byte [])srvcKeys.get(KDBM_INDEX_KEY + "@" + node.name);
		}

		if(tgsKey == null || kdbmKey == null)
			printout(Simnet.VC_ALWAYS, Simnet.VL_DEBUG, node.id,
						"Warning!  The database was not initialized correctly!  Check for tgs and kdbm keys!");

		kdc = new KDC( authKeys, dg0, tgsKey, kdbmKey );

		//start the threads and let them do the work
		kdc.start();
		tgs.start();
		kdbm.start();

	}


	/**
	 * Thread for handling initial authentication requests, i.e., messages of type
	 * KRB_AP_REQ and KRB_AP_REP.
	 */
	private class KDC extends Thread{

		/**
		 * Stores all user keys.  Keys are indexed by user name (not fully-qualified -- e.g., "alice").
		 */
		private Hashtable keys;

		/**
		 * Socket to listen for client requests.
		 */
		private DatagramSocket dgsock;

		/**
		 * The TGS's private key.  We need a copy to encrypt TGTs.
		 */
		private byte [] tgsKey;

		/**
		 * The KDBM's private key.  We need a copy to encrypt tickets to the KDBM.
		 */
		private byte [] kdbmKey;


		/**
		 * Sets up structures to manage keys and sets the inner instance of the
		 * threads's DatagramSock
		 * @param keys table to store keys for all users
		 * @param dgsock socket to communicate with clients
		 * @param tgsKey the TGS's private key.  Used to encrypt TGTs
		 * @param kdbmKey the KDBM's private key.  Used to encrypt tickets to the KDBM
		 */
		public KDC(Hashtable keys, DatagramSocket dgsock, byte [] tgsKey, byte [] kdbmKey){
			this.keys = keys;
			this.dgsock = dgsock;

			//we need to know about these two services, no others!
			this.tgsKey = tgsKey;
			this.kdbmKey = kdbmKey;
		}


		/**
		 * Receives message of type KRB_AP_REQ, and returns a message of type
		 * KRB_AP_REP if the request is valid.  Otherwise, returns the appropriate
		 * error message (see {@link KerberosMessage}).
		 */
		public void run(){

			Thread.currentThread().setName(node.name + "-" + "KDC");

			try{
				dgsock.bind(AUTH_PORT);


				while(!dgsock.isClosed()){

					//wait indefinitely for Authentication request

					DatagramPacket p = dgsock.recvfrom();

					KerberosMessage km = (KerberosMessage) p.data;


					//we only know how to process this type of message -- shouldn't get other types
					if(km.type != KerberosMessage.KRB_AS_REQ){
						node.printout(Simnet.VC_ALWAYS, Simnet.VL_ERROR, node.id,
										  "Unknown packet type (" + km.type + ")");
						dgsock.sendto(p.ip, p.port, new KerberosMessage(KerberosMessage.KRB_ERR_UXPT_PACKET));
						continue;
					}

					//get the data from the message
					KerberosMessageData kmd = (KerberosMessageData) km.data;

					printout(Simnet.VC_ALWAYS, Simnet.VL_DEBUG, node.id,
								"Received Authentication Request from `" + kmd.client +
								"' to `" + kmd.service + "'");

					//find out who is requesting access
					String userName = null;
					try{
						//if this is malformed, an ArrayIndexOutOfBounds excpetion will be thrown
						userName = getClientName( (String) kmd.client);
					}catch(Exception e){
						node.printout(Simnet.VC_ALWAYS, Simnet.VL_ERROR, node.id,
										  "Requesting client was not a properly-formatted principle");
						dgsock.sendto(p.ip, p.port, new KerberosMessage(KerberosMessage.KRB_ERR_CLT_UNKNOWN));
						continue;
					}


					byte [] clientKey;  //will hold the client's key after it is pulled out of the db
					byte [] serviceKey; // ""				service's   ""

					synchronized(keys){

						//check if the user is registered, if not respond w/ error
						if(!keys.containsKey(userName)){

							node.printout(Simnet.VC_ALWAYS, Simnet.VL_ERROR, node.id,
											  "User \"" + userName + "\" not registered.");

							dgsock.sendto(p.ip, p.port, new KerberosMessage(KerberosMessage.KRB_ERR_CLT_UNKNOWN));

							continue;
						}

						if( sim.lookup( getClientAddress( (String) kmd.client ) ) != p.ip ){
							node.printout(Simnet.VC_ALWAYS, Simnet.VL_ERROR, node.id,
											  "Client node name does not match source address of ip packet");
							dgsock.sendto(p.ip, p.port, new KerberosMessage(KerberosMessage.KRB_ERR_ADDR_MISMATCH));

							continue;
						}

						//which service are they requesting -- kdbm or tgs?
						String service = null;

						try{
							service = getServiceName((String) kmd.service);
						}catch(Exception e){
							node.printout(Simnet.VC_ALWAYS, Simnet.VL_ERROR, node.id,
											  "The requested service is malformatted");
							dgsock.sendto(p.ip, p.port, new KerberosMessage(KerberosMessage.KRB_ERR_SRVC_UNKNOWN));
							continue;
						}

						clientKey = (byte []) keys.get(userName);

						if( service.equals(TGS_SRVC) )
							serviceKey = tgsKey;
						else if( service.equals(KDBM_SRVC))
							serviceKey = kdbmKey;
						else{
							node.printout(Simnet.VC_ALWAYS, Simnet.VL_ERROR, node.id,
											  "The requested service is not the TGS or KDBM");
							dgsock.sendto(p.ip, p.port, new KerberosMessage(KerberosMessage.KRB_ERR_SRVC_UNKNOWN));
							continue;
						}
					}

					//make a random session key to be shared between client and kdbm/tgs
					byte [] sessionKey = makeRandomKey();

					//make the ticket
					KerberosTicket t = new KerberosTicket();
					t.service = (String) kmd.service; //format [krbtgs|krbkdbm]@node
					t.client = (String) kmd.client;   //format name@node -- redundant?
					t.address = sim.lookup(getClientAddress( (String) kmd.client)); //client ip
					t.time = System.currentTimeMillis();
					//how long is this ticket good for?
					t.duration = (getServiceName((String) kmd.service).equals(TGS_SRVC) ?
									  TGT_DURATION : KDBM_DURATION);
					//add the shared session key
					t.key = sessionKey;

					//make final message
					KerberosMessageData respKmd = new KerberosMessageData();

					//encrypt the ticket under the service key
					respKmd.ticket = encryptObject(t, serviceKey);

					//give the nonce back
					respKmd.nonce = kmd.nonce;

					//provide the shared session key
					respKmd.key = sessionKey;

					//make the response, encrypted under the client's key
					KerberosMessage response =
						new KerberosMessage(KerberosMessage.KRB_AS_REP,
												  encryptObject(respKmd, clientKey));

					node.printout(Simnet.VC_ALWAYS, Simnet.VL_DEBUG, node.id,
									  "Valid user and service, sending a response.");

					//send response
					dgsock.sendto(p.ip, p.port, response);

				} //closes while

			}catch(Exception e){
				e.printStackTrace();
			}
		}

	}



	/**
	 * Thread for handling requests for end services, i.e., messages of type
	 * KRB_TGS_REQ and KRB_TGS_REP.
	 */
	private class TGS extends Thread{

		/**
		 * Stores all service keys.  Keys are indexed by servcie name ( fully-qualified -- e.g., "echo@JHU").
		 */
		private Hashtable keys;

		/**
		 * Socket to listen for client requests.
		 */
		private DatagramSocket dgsock;

		/**
		 * Our private key.
		 */
		private byte [] privateKey;

		/**
		 * Initializes structures to hold keys, generates our private key.
		 * @param keys Database to hold service keys, initialized by the KDBM.
		 * @param dgsock Our own datagram socket.
		 */
		public TGS(Hashtable keys, DatagramSocket dgsock){
			this.keys = keys;
			this.dgsock = dgsock;
			privateKey = makeRandomKey();
			synchronized(keys){
				keys.put( TGS_INDEX_KEY + "@" + node.name, privateKey);
			}
		}

		/**
		 * Receives message of type KRB_TGS_REQ, and returns a message of type
		 * KRB_TGS_REP if the request is valid.  Otherwise, returns the appropriate
		 * error message (see {@link KerberosMessage}).
		 */
		public void run() {
			Thread.currentThread().setName(node.name + "-" + "TGS");

			try{
				dgsock.bind(TGS_PORT);

				while(!dgsock.isClosed()) {
					//wait indefinitely for TGS request
					DatagramPacket p = dgsock.recvfrom();
					KerberosMessage km = (KerberosMessage) p.data;

					//we only know how to process this type of message -- shouldn't get other types
					if(km.type != KerberosMessage.KRB_TGS_REQ) {
						node.printout(Simnet.VC_ALWAYS, Simnet.VL_ERROR, node.id, "Unknown packet type (" + km.type + ")");
						dgsock.sendto(p.ip, p.port, new KerberosMessage(KerberosMessage.KRB_ERR_UXPT_PACKET));
						continue;
					}

					//get the data from the message
					KerberosMessageData kmd = (KerberosMessageData) km.data;

					printout(Simnet.VC_ALWAYS, Simnet.VL_DEBUG, node.id, "Received request for the service '" + kmd.service + "'");

					byte[] serviceKey;

					synchronized(keys){
						serviceKey = (byte[]) keys.get(kmd.service);
					}

					// Extract and decrypt ticket
					KerberosTicket ticket = (KerberosTicket) decryptObject((byte[]) kmd.ticket, privateKey);

					// Extract the shared client-TGS key
					byte[] sharedKey = ticket.key;

					// Extract and decrypt authenticator
					KerberosAuthenticator auth = (KerberosAuthenticator) decryptObject((byte[]) kmd.authenticator, sharedKey);

					// Test for validity
					int result = compareAuthToTicket(auth, ticket, p.ip);

					if(result != 0) {
						node.printout(Simnet.VC_ALWAYS, Simnet.VL_ERROR, node.id, "Invalid ticket and authenticator");
						dgsock.sendto(p.ip, p.port, new KerberosMessage(result));
						continue;
					}

					//make a random session key to be shared between client and service
					byte[] newKey = makeRandomKey();

					//make the ticket
					KerberosTicket newTicket = new KerberosTicket();
					newTicket.service = (String) kmd.service;
					newTicket.client = auth.client;
					newTicket.address = auth.address;
					newTicket.time = System.currentTimeMillis();

					//how long is this ticket good for?
					newTicket.duration = TICKET_DURATION;

					//add the shared session key
					newTicket.key = newKey;

					//make final message
					KerberosMessageData newKmd = new KerberosMessageData();

					//encrypt the ticket under the service key
					newKmd.ticket = encryptObject(newTicket, serviceKey);

					//give the nonce back
					newKmd.nonce = kmd.nonce;

					//provide the shared session key
					newKmd.key = newKey;

					//make the response, encrypted under the shared client-TGS key
					KerberosMessage newKM = new KerberosMessage(KerberosMessage.KRB_TGS_REP, encryptObject(newKmd, sharedKey));
					node.printout(Simnet.VC_ALWAYS, Simnet.VL_DEBUG, node.id, "Valid user and service, sending a response.");

					//send response
					dgsock.sendto(p.ip, p.port, newKM);
				}
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Thread for handling requests for administrative services, i.e., messages of type
	 * KRB_KDBM_*.
	 */
	private class KDBM extends Thread{
		/**
		 * Holds private keys of each user -- indexed by username
		 */
		private Hashtable authKeys;

		/**
		 * Holds private keys of each service -- indexed by service name, e.g., echo@JHU
		 */
		private Hashtable serviceKeys;

		/**
		 * the KDBM's private key used by the KDC to encrypt tickets for the KDBM
		 */
		private byte [] privateKey;

		/**
		 * Used to receive requests from clients
		 */
		private DatagramSocket dgsock;

		/**
		 * Sets up structures to manage keys and generates the KDBM's own key.
		 * Loads keys for users/services from the file `krb.db'
		 * @param authKeys table to store keys for all users -- will be populated here
		 * @param serviceKeys table to store keys for all services -- will be populated here
		 * @param dgsock socket to communicate with clients
		 */
		public KDBM( Hashtable authKeys, Hashtable serviceKeys, DatagramSocket dgsock){
			this.authKeys = authKeys;
			this.serviceKeys = serviceKeys;
			this.dgsock = dgsock;

			//initialize database
			privateKey = makeRandomKey();
			populateDatabases();
			synchronized(serviceKeys){
				serviceKeys.put( KDBM_INDEX_KEY + "@" + node.name, privateKey);
			}
		}

		/**
		 * Populates both databases (for users and services).  Reads data from the file
		 * `krb.db'  Each line should be of the form:
		 * `<code>[usr|srvc] name password</code>'.
		 * Note that for users, the <code>name</code> does not specify a node, but it does for services
		 */
		private void populateDatabases(){

			try{
				BufferedReader in = new BufferedReader( new FileReader("krb.db"));

				String line;
				String [] entry;

				synchronized(authKeys){
					synchronized(serviceKeys){
						while( (line =  in.readLine()) != null ){
							entry = line.split("\t");

							if(entry.length < 3 ) continue;
							//anything after the third entry (tab-delimited) will
							// be ignored and can be a comment

							if(entry[0].equals("srvc") ){
								serviceKeys.put(entry[1], getPrivateKey(entry[2]));
							}else if(entry[0].equals("usr") ){
								authKeys.put(entry[1], getPrivateKey(entry[2]));
							}else{
								continue;
							}
						}
					}
				}

			}catch(IOException ioe){
				ioe.printStackTrace();
			}

		}


		/**
		 * Receives message of type KRB_KDBM_*, and returns a message of type
		 * KRB_KDBM_REP if the request is valid.  Otherwise, returns the appropriate
		 * error message (see {@link KerberosMessage}).
		 */

		public void run(){
			Thread.currentThread().setName(node.name + "-" + "KDBM");

			try{

				dgsock.bind(KDBM_PORT);

				while(!dgsock.isClosed()){

					DatagramPacket p = dgsock.recvfrom();
					KerberosMessage km = (KerberosMessage) p.data;
					KerberosMessageData kmd = (KerberosMessageData) km.data;


					if(! KerberosMessage.isKDBMMessage( km.type )){
						node.printout(Simnet.VC_ALWAYS, Simnet.VL_ERROR, node.id,
										  "Unknown packet type");
						dgsock.sendto(p.ip, p.port, new KerberosMessage(KerberosMessage.KRB_ERR_UXPT_PACKET));
						continue;
					}


					//should really do this after we check the type,

					//usage is as follows:
					//  to find out who is requesting, check the ticket/authenticator
					//  admin can do everything except change a diff. user's password
					//  to find out who the request is for, check the client/service

					KerberosTicket t;
					KerberosAuthenticator a;
					byte [] sessionKey;
					byte [] newKey = null;
					String client = null;
					String service = null;

					try{

						t = getTicket(kmd);
						sessionKey = t.key;
						a = getAuthenticator(kmd, sessionKey);
						if(kmd.key != null) newKey = getKey(kmd, sessionKey);
						if(kmd.client != null) client = getClient(kmd, sessionKey);
						if(kmd.service != null) service = getService(kmd, sessionKey);


					}catch(SimnetDecryptionException sde){
						node.printout(Simnet.VC_ALWAYS, Simnet.VL_DEBUG, node.id,
										  "There was a problem with decryption!");
						dgsock.sendto(p.ip, p.port, new KerberosMessage(KerberosMessage.KRB_ERR_DCRYPT));
						continue;
					}


					printout(Simnet.VC_ALWAYS, Simnet.VL_COMPLETE, node.id,
								"\n******** Received Message ********" +
								"\n\tRecevied type: " + km.type +
								"\n\tAuthenticator: " + a.toString() +
								"\n\tTicket: "+ t.toString() +
								(client != null ? "\n\tClient: " + client : "") +
								(service != null ? "\n\tService: " + service : ""));

					//check the authenticator/ticket
					int type;
					if( (type = compareAuthToTicket(a, t, p.ip) ) != 0 ){
						node.printout(Simnet.VC_ALWAYS, Simnet.VL_ERROR, node.id,
										  "Authenticator and/or Ticket is invalid or expired!");
						dgsock.sendto(p.ip, p.port, new KerberosMessage( type ));
						continue;
					}

					int error = 0;
					switch(km.type){
						//wants to add a client -- only admin can do this
						case KerberosMessage.KRB_KDBM_ADD_CLT:
							if(getClientName(t.client).equals("admin") ){
								synchronized(authKeys) { authKeys.put(client, newKey); }
								node.printout(Simnet.VC_ALWAYS, Simnet.VL_DEBUG, node.id,
												  "Added user: " + client);
							}else
								error = KerberosMessage.KRB_ERR_KDBM_UNAUTH;

							break;

							//wants to add a service -- only admin can do this
						case KerberosMessage.KRB_KDBM_ADD_SRVC:
							if(getClientName(t.client).equals("admin") ){
								synchronized(serviceKeys) { serviceKeys.put(service, newKey); }
								node.printout(Simnet.VC_ALWAYS, Simnet.VL_DEBUG, node.id,
												  "Added service: " + service);
							}else
								error = KerberosMessage.KRB_ERR_KDBM_UNAUTH;

							break;

							//change a password -- the client must match the client on the ticket
						case KerberosMessage.KRB_KDBM_CH_CLT_PASS:
							if(getClientName(t.client).equals(client) ){
								synchronized(authKeys) {
									authKeys.remove(client);
									authKeys.put(client, newKey);
								}
								node.printout(Simnet.VC_ALWAYS, Simnet.VL_DEBUG, node.id,
												  "Changed Password for: " + client);
							}else
								error = KerberosMessage.KRB_ERR_KDBM_UNAUTH;

							break;

							//change password for service -- need to bee admin
						case KerberosMessage.KRB_KDBM_CH_SRVC_PASS:
							if(getClientName(t.client).equals("admin") ){
								synchronized(serviceKeys) {
									serviceKeys.remove(service);
									serviceKeys.put(service, newKey);
								}
								node.printout(Simnet.VC_ALWAYS, Simnet.VL_DEBUG, node.id,
												  "Changed Password for: " + service);
							}else
								error = KerberosMessage.KRB_ERR_KDBM_UNAUTH;

							break;

							//remove client -- need to bee admin
						case KerberosMessage.KRB_KDBM_RM_CLT:
							if(getClientName(t.client).equals("admin") ){
								synchronized(serviceKeys){ serviceKeys.remove(client); }
								node.printout(Simnet.VC_ALWAYS, Simnet.VL_DEBUG, node.id,
													"Removed: " + client);

							}else
								error = KerberosMessage.KRB_ERR_KDBM_UNAUTH;

							break;

							//remove service -- need to bee admin
						case KerberosMessage.KRB_KDBM_RM_SRVC:
							if(getClientName(t.client).equals("admin") ){
								synchronized(serviceKeys) { serviceKeys.remove(service); }
							}else
								error = KerberosMessage.KRB_ERR_KDBM_UNAUTH;

							break;

						default:
							error = KerberosMessage.KRB_ERR_UXPT_PACKET;
							break;

					}

					if(error == 0){
						node.printout(Simnet.VC_ALWAYS, Simnet.VL_NOTICE, node.id, "Operation succeeded");
						dgsock.sendto(p.ip, p.port, new KerberosMessage(KerberosMessage.KRB_KDBM_REP));
					}else{
						node.printout(Simnet.VC_ALWAYS, Simnet.VL_NOTICE, node.id, "Operation failed. (" + error + ")");
						dgsock.sendto(p.ip, p.port, new KerberosMessage(error));
					}
				} //closes while

			}catch(SimnetSocketException sse){
				sse.printStackTrace();
			}catch(InterruptedException ie){
				ie.printStackTrace();
			}//closes try

		}//closes run


		/**
		 * Utility method to extract/decrypt the <code>KerberosTicket</code> from a
		 * <code>KerberosMessageData</code> object (Uses the KDBM's <code>privateKey</code>
		 * for decryption.
		 * @param kmd decrypted data from the <code>KerberosMessage</code> received from the client
		 */
		private KerberosTicket getTicket(KerberosMessageData kmd)
			throws SimnetDecryptionException{
				return (KerberosTicket) decryptObject( (byte []) kmd.ticket, privateKey);
			}

		/**
		 * Utility method to extract/decrypt the <code>KerberosAuthenticator</code> from a
		 * <code>KerberosMessageData</code> object.
		 * @param kmd decrypted data from the <code>KerberosMessage</code> received from the client
		 * @param key session key used to decrypt the authenticator, taken from the corresponding ticket
		 */
		private KerberosAuthenticator getAuthenticator(KerberosMessageData kmd, byte [] key)
			throws SimnetDecryptionException{
				return (KerberosAuthenticator) decryptObject( (byte []) kmd.authenticator, key);
			}

		/**
		 * Utility method to extract/decrypt the <code>key</code> from a
		 * <code>KerberosMessageData</code> object.
		 * @param kmd decrypted data from the <code>KerberosMessage</code> received from the client
		 * @param key session key used to decrypt the key, taken from the corresponding ticket
		 */
		private byte [] getKey(KerberosMessageData kmd, byte [] key)
			throws SimnetDecryptionException{
				return (byte []) decrypt( (byte []) kmd.key, key);
			}

		/**
		 * Utility method to extract/decrypt a <code>client</code> from a
		 * <code>KerberosMessageData</code> object.
		 * @param kmd decrypted data from the <code>KerberosMessage</code> received from the client
		 * @param key session key used to decrypt the client, taken from the corresponding ticket
		 */
		private String getClient(KerberosMessageData kmd, byte [] key)
			throws SimnetDecryptionException{
				if( kmd.client != null ){
					return (String) decryptObject( (byte []) kmd.client, key);
				}else{
					return null;
				}
			}

		/**
		 * Utility method to extract/decrypt the <code>service</code> from a
		 * <code>KerberosMessageData</code> object.
		 * @param kmd decrypted data from the <code>KerberosMessage</code> received from the client
		 * @param key session key used to decrypt the service, taken from the corresponding ticket
		 */
		private String getService(KerberosMessageData kmd, byte [] key)
			throws SimnetDecryptionException{
				if(kmd.service != null){
					return (String) decryptObject( (byte []) kmd.service, key);
				}else{
					return null;
				}
			}

	}


	/**
	 * Displays the private keys of the services and users.
	 */
	public void dumpState(){
		String output = "Dumping Kerberos State:";
		Enumeration nameEnum = authKeys.keys();
		output += "\n******** Authentication Keys ********";

		while (nameEnum.hasMoreElements()) {
			String name = (String) nameEnum.nextElement();
			byte[] key = (byte[]) authKeys.get(name);
			output += "\n\t" + name + "\t" + new String(Hex.encode(key));
		}

		nameEnum = srvcKeys.keys();
		output += "\n******** Service Keys ********";

		while (nameEnum.hasMoreElements()) {
			String name = (String) nameEnum.nextElement();
			byte[] key = (byte[]) srvcKeys.get(name);
			output += "\n\t" + name + "\t" + new String(Hex.encode(key));
		}

		printout(0, node.id, output);
	}


	public boolean prePlugout(Object o){ return true; }

}