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
 * Super-class of all kerberized-clients.
 * Defines methods that allow interaction with the KDC, TGS, KDBM, and end-services.
 */
public abstract class KerberosClient extends KerberosApplication{

	private class TicketUpdater extends TimerTask {
		String service;

		public TicketUpdater(String service) {
			this.service = service;
		}

		public void run() {
			if (tickets != null && keys != null) {
				Thread.currentThread().setName(node.name + "-" + "TicketUpdater");
				printout(0, node.id, "Expiration of ticket: " + service);
				tickets.remove(service);
				keys.remove(service);
			}
		}
	}

	/**
	 * The user currently logged in to this instance
	 * of a Kerberized-client (there can be only one).  Has the format `user@node'.
	 */
	private String user;

	/**
	 * The node hosting the KDC, TGS and KDBM.
	 */
	private String authServer;

	/**
	 * Holds fresh tickets returned from the KDC/TGS.  Indexed by name,
	 * example: `service@node'
	 */
	public Hashtable tickets;

	/**
	 * Holds session-keys associated with the tickets in <code>tickets</code>.
	 * Indexed by name: example: `service@node'
	 */
	private Hashtable keys;

	/**
	 * UDP socket for communication with KDC/TGS/KDBM
	 */
	private DatagramSocket dgsock;

	/**
	 * Instantiate all objects and prepare for communication.
	 */
	public void customInit(){
		super.customInit();
		keys = new Hashtable();    //hold keys associated with tickets
		tickets = new Hashtable(); //tickets from the auth server and TGS
		dgsock = ((UDP) node.getTransport(Simnet.PROTO_UDP)).createDatagramSocket(this);
		try{
			dgsock.bind(-1);
			dgsock.setTimeout(KerberosAuthenticationServers.SOCKET_TIMEOUT);
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	/**
	 * Stub-routine to facilitate login from Simnet scripts.
	 * This is called only after a client has logged in and her TGT has expired.
	 * it reads a password that has been saved to disk so the user does not
	 * have to type the password again.
	 * @param	service  The server to which we want a ticket (KDBM or TGS).
	 *							of the form, `[krbtgs|krbkdbm]@node'
	 * @param	user		Who we are, of the form `name@node'
	 */
	protected void getTicketFromAS(String service, String user)
		throws KerberosAuthenticationException{
			getTicketFromAS(service, user, getPassword());
		}

	/**
	 * Routine to log into the system.  Contacts the KDC and requests a ticket to
	 * either the TGS or KDBM.  Stores the resulting ticket/session key for later use.
	 * This method also saves the user's password to disk so it can be accessed
	 * quickly when the TGT expires.  While this breaks the 'spirit' of Kerberos, it
	 * is necessary here.
	 * @param	service  The server to which we want a ticket (KDBM or TGS).
	 *							of the form, `[krbtgs|krbkdbm]@node'
	 * @param	user		Who we are, of the form `name@node'
	 * @param	password The user's password
	 * @throws  KerberosAuthenticationException if there is any problem that
	 *				prevents authentication.
	 */
	protected void getTicketFromAS(String service, String user, String password)
		throws KerberosAuthenticationException {

			//this is yucky, yucky, yucky!
			// however, we need to do it because it is hard to interactively
			// enter passwords to the UI.  This will simulate a user
			// logging in again
			writePasswordToDisk(user, password);

			//start implementation here...
			this.user = user;

			// Set authServer
			authServer = getServerAddress(service);

			// Create message
			KerberosMessageData kmd = new KerberosMessageData();
			KerberosMessage km = new KerberosMessage(KerberosMessage.KRB_AS_REQ, kmd);
			KerberosMessage r = null;
			KerberosMessageData rd = null;

			// Set fields
			kmd.client = user;
			kmd.service = service;
			Integer nonce = makeNonce();
			kmd.nonce = nonce;

			// Send and wait for response
			try {
				dgsock.sendto(sim.lookup(authServer), KerberosAuthenticationServers.AUTH_PORT, km);
				r = (KerberosMessage) ((DatagramPacket) dgsock.recvfrom()).data;
			} catch (Exception e) {
				throw new KerberosAuthenticationException("Could not contact " + authServer);
			}

			// Check response type
			if(r.type != KerberosMessage.KRB_AS_REP){
				throw new KerberosAuthenticationException(authServer + " sent non-KRB_AS_REP message");
			}

			// Get key
			byte[] key = getPrivateKey(password);

			// Decrypt response
			try {
				rd = (KerberosMessageData) decryptObject((byte[]) r.data, key);
			} catch (Exception e) {
				throw new KerberosAuthenticationException("There was a problem in decryption");
			}

			String output = "\n******** Received Message ********";
			output += "\n\tReceived nonce: " + rd.nonce;
			output += "\n\tReceived ticket: (should be encrypted.)";
			output += "\n\tReceived key: " + new String(Hex.encode(rd.key));
			printout(0, node.id, output);

			// Check nonce
			if (nonce.equals(rd.nonce) == false) {
				throw new KerberosAuthenticationException("Returned nonce did not match");
			}

			printout(0, node.id, "Adding key/tickets keyed by " + service);

			// Store ticket and key
			tickets.put(service, rd.ticket);
			keys.put(service, rd.key);

			// *** Set ticket and key removal timer task ***
			Timer theTimer = new Timer();
			TicketUpdater tu = new TicketUpdater(service);

			if (getServiceName(service).equals(KerberosAuthenticationServers.TGS_SRVC)) {
				theTimer.schedule(tu, KerberosAuthenticationServers.TGT_DURATION);
			}
			else if (getServiceName(service).equals(KerberosAuthenticationServers.KDBM_SRVC)) {
				theTimer.schedule(tu, KerberosAuthenticationServers.KDBM_DURATION);
			}
		}

	/**
	 * Logs out of the Kerberized-client.  This method erases all passwords/tickets
	 * and the password files and cleans up all objects.
	 */
	public void logout(){
		// get rid of our password file
		File f = new File("krb.pass." + getClientName(user));
		if( f.exists() ) f.delete();

		user = null;
		authServer = null;
		synchronized(tickets){
			synchronized(keys){
				tickets = null;
				keys = null;
			}
		}

		dgsock.close();
		dgsock = null;
	}

	/**
	 * Contacts the TGS and requests a ticket/session key to a Kerberized-service.
	 * Saves the results for communication with this service.
	 * @param	service  The service we wish to contact, e.g., `echo@ISI'
	 * @throws  KerberosAuthenticationException if there is any problem that
	 *				prevents authentication.
	 */
	private void getServiceTicket(String service)
		throws KerberosAuthenticationException{
			String tgsName = KerberosAuthenticationServers.TGS_SRVC + "@" + authServer;

			// Check for existing TGT
			if (tickets.containsKey(tgsName) == false || keys.containsKey(tgsName) == false) {
				getTicketFromAS(tgsName, user);
			}

			// Retrieve TGT and TGS session key
			Object tgt = tickets.get(tgsName);
			byte[] tgsKey = (byte[]) keys.get(tgsName);

			// Create message
			KerberosMessageData kmd = new KerberosMessageData();
			KerberosMessage km = new KerberosMessage(KerberosMessage.KRB_TGS_REQ, kmd);
			KerberosMessage r = null;
			KerberosMessageData rd = null;

			// Set fields
			kmd.service = service;
			Integer nonce = makeNonce();
			kmd.nonce = nonce;
			kmd.authenticator = encryptObject(new KerberosAuthenticator(user, node.id, System.currentTimeMillis()), tgsKey);
			kmd.ticket = tgt;

			// Send and wait for response
			try {
				dgsock.sendto(sim.lookup(authServer), KerberosAuthenticationServers.TGS_PORT, km);
				r = (KerberosMessage) ((DatagramPacket) dgsock.recvfrom()).data;
			} catch (Exception e) {
				throw new KerberosAuthenticationException("Could not contact " + authServer);
			}

			// Check response type
			if(r.type != KerberosMessage.KRB_TGS_REP){
				throw new KerberosAuthenticationException(authServer + " sent non-KRB_TGS_REP message");
			}

			printout(0, node.id, "Received Kerberos TGS_REP message");

			// Decrypt response
			try {
				rd = (KerberosMessageData) decryptObject((byte[]) r.data, tgsKey);
			} catch (Exception e) {
				throw new KerberosAuthenticationException("There was a problem in decryption");
			}

			String output = "\n******** Received Message ********";
			output += "\n\tReceived nonce: " + rd.nonce;
			output += "\n\tReceived ticket: (should be encrypted.)";
			output += "\n\tReceived key: " + new String(Hex.encode(rd.key));
			printout(0, node.id, output);

			// Check nonce
			if (nonce.equals(rd.nonce) == false) {
				throw new KerberosAuthenticationException("Returned nonce did not match");
			}

			// Store ticket and key
			tickets.put(service, rd.ticket);
			keys.put(service, rd.key);

			// *** Set ticket and key removal timer task ***
			Timer theTimer = new Timer();
			TicketUpdater tu = new TicketUpdater(service);

			theTimer.schedule(tu, KerberosAuthenticationServers.TICKET_DURATION);
		}

	/**
	 * Requests a service of the KDBM.
	 * @param	type			What are we asking the KDBM to do, e.g., add a new service?
	 *								(see {@link KerberosMessage}) for possible types.
	 * @param	principle	On whose behalf are we doing this?
	 * @param	pass			Are we changing a password?  If so, replace it with this one.
	 * @return  whether or not the operation succeeded
	 * @throws	KerberosAuthenticationException if there is authentication is impossible.
	 */
	protected boolean useKDBMService(int type, String principle, String pass)
		throws KerberosAuthenticationException{
			Object ticket;
			byte [] sessionKey;

			//see if we can get key/ticket for this service
			synchronized(tickets){
				synchronized(keys){
					//do we already have this ticket?
					if( ! tickets.containsKey(KerberosAuthenticationServers.KDBM_SRVC + "@" + authServer) )
						//if not, lets get it from the KDC
						getTicketFromAS(KerberosAuthenticationServers.KDBM_SRVC + "@" + authServer, user);

					//lets get the ticket and session key
					ticket = tickets.get(KerberosAuthenticationServers.KDBM_SRVC + "@" + authServer);
					sessionKey = (byte []) keys.get(KerberosAuthenticationServers.KDBM_SRVC + "@" + authServer);
				}
			}

			//make the message for the KDBM server
			KerberosMessage km = new KerberosMessage(type);
			KerberosMessageData kmd = new KerberosMessageData();

			kmd.ticket = ticket;  //ticket to kdbm
			kmd.authenticator =   //our own authenticator, encrypted under the shared key
				encryptObject(new KerberosAuthenticator(user, node.id, System.currentTimeMillis()),
								  sessionKey);

			if(pass != null)  //for operations that require a password, encrypt and send it
				kmd.key = encrypt(getPrivateKey(pass), sessionKey);

			//is this for a client or a service?  add appropriate field and encrypt
			if(type == KerberosMessage.KRB_KDBM_ADD_CLT ||
				type == KerberosMessage.KRB_KDBM_CH_CLT_PASS ||
				type == KerberosMessage.KRB_KDBM_RM_CLT ){

				kmd.client = encryptObject(principle, sessionKey);
				kmd.service = null;
			}else{
				kmd.service = encryptObject(principle, sessionKey);
				kmd.client =  null;
			}

			km.data = kmd;

			KerberosMessage response = null;

			try{
				dgsock.sendto(sim.lookup(authServer), KerberosAuthenticationServers.KDBM_PORT, km);

				response = (KerberosMessage) ((DatagramPacket)dgsock.recvfrom()).data;
			}catch(Exception e){
				throw new KerberosAuthenticationException("Could not contact " + authServer);
			}

			//did it work?
			if(response.type == KerberosMessage.KRB_KDBM_REP){
				return true;
			}else{
				String message = makeErrorMessage("useKDBMService(): ", response.type);
				throw new KerberosAuthenticationException(message);
			}
		}

	//Kerberized classes call this method to get a bound TCP socket
	//  they should check to make sure it is open (!null?) on return --
	//  a closed socket indicates a problem with authentication at some point

	/**
	 * Establishes an authenticated TCP connection with a Kerberized service.
	 * @param	service  The end-service we wish to contact (of the form `name@node').
	 * @param	port		The port this service is listening on.
	 * @return	A bound socket if authentication is successful.
	 * @throws  KerberosAuthenticationException if there is a problem during authentication.
	 */
	protected Socket requestKerberizedService(String service, int port)
		throws KerberosAuthenticationException{
			// Check for service ticket and key
			if (tickets.containsKey(service) == false || keys.containsKey(service) == false) {
				getServiceTicket(service);
			}

			// Retrieve service ticket and session key
			Object ticket = tickets.get(service);
			byte[] key = (byte[]) keys.get(service);

			// Create message
			KerberosMessageData kmd = new KerberosMessageData();
			KerberosMessage km = new KerberosMessage(KerberosMessage.KRB_AP_REQ, kmd);
			KerberosMessage r = null;
			KerberosMessageData rd = null;

			// Set fields
			kmd.authenticator = encryptObject(new KerberosAuthenticator(user, node.id, System.currentTimeMillis()), key);
			kmd.ticket = ticket;

			// Create a socket
			Socket socket = ((TCP) node.getTransport(Simnet.PROTO_TCP)).createSocket(this);

			try {
				socket.bind(-1);
			} catch(Exception e) {
				e.printStackTrace();
			}

			String serviceNode = getServerAddress(service);

			// Send and wait for response
			try {
				socket.connect(sim.lookup(serviceNode), port);
				socket.send(km);
				r = (KerberosMessage) socket.recv();
			} catch (Exception e) {
				throw new KerberosAuthenticationException("Could not contact " + serviceNode);
			}

			// Check response type
			if(r.type != KerberosMessage.KRB_AP_REP){
				throw new KerberosAuthenticationException(serviceNode + " sent non-KRB_AP_REP message");
			}

			// Did it work?
			if (r.data == null) {
				printout(0, node.id, "Successfully authenticated to service... returning (bound) socket");
				return socket;
			}

			throw new KerberosAuthenticationException("Failed authentication");
		}

	/**
	 * Method that must be defined by extending classes.  Should call <code>getTicketFromAS()</code>
	 * with the correct parameters.
	 * @param	remoteNode  The node running the KDC
	 * @param	user			Who is logging in
	 * @param	pass			The user's password
	 */
	public abstract void login(String remoteNode, String user, String pass);

	/**
	 * Method that must be defined by extending classes.  Should call <code>requestService()</code>
	 * with the correct parameters.
	 * @param	service  The service we wish to connect to.
	 */
	public abstract void connect(String service);

	/**
	 * Utility method to make an error message based on KRB_ERR_* types returned from servers.
	 * This method can be used to print messages to a screen or add useful information to Exceptions.
	 * @param	prefix	How the error message should start.
	 * @param	type		They type of error message.
	 * @return  the error message.
	 */
	private String makeErrorMessage(String prefix, int type){
		if( type < 31 ){
			//it's not an error message, but we didn't expect it
			//  -- otherwise the first if statement would have grabbed it
			return prefix + "Unexpected Packet (" + type + ")";
		}

		switch(type){
			case KerberosMessage.KRB_ERR_CLT_UNKNOWN  : prefix += "KRB_ERR_CLT_UNKNOWN"; break;
			case KerberosMessage.KRB_ERR_TIMEOUT      : prefix += "KRB_ERR_TIMEOUT"; break;
			case KerberosMessage.KRB_ERR_SRVC_UNKNOWN : prefix += "KRB_ERR_SRVC_UNKNOWN"; break;
			case KerberosMessage.KRB_ERR_CLT_MISMATCH : prefix += "KRB_ERR_CLT_MISMATCH"; break;
			case KerberosMessage.KRB_ERR_ADDR_MISMATCH: prefix += "KRB_ERR_ADDR_MISMATCH"; break;
			case KerberosMessage.KRB_ERR_DCRYPT       : prefix += "KRB_ERR_DCRYPT"; break;
			case KerberosMessage.KRB_ERR_UXPT_PACKET  : prefix += "KRB_ERR_UXPT_PACKET"; break;
			case KerberosMessage.KRB_ERR_KDBM_UNAUTH  : prefix += "KRB_ERR_KDBM_UNAUTH"; break;
			default: prefix += type;
		}

		return prefix;
	}

	/**
	 * Utility function to read the current user's password from disk.
	 * @return  the user's password
	 */
	protected String getPassword(){
		String pass = "";

		String pwFile = "krb.pass." + getClientName(user);

		printout(Simnet.VC_ALWAYS, Simnet.VL_DEBUG, node.id,
					"Reading password from " + pwFile);
		try{
			BufferedReader in = new BufferedReader( new FileReader( pwFile ) );
			pass = in.readLine();
			in.close();
		}catch(IOException ioe){
			ioe.printStackTrace();
		}

		return pass;
	}

	/**
	 * Utility method to write a user's password to disk at initial login.
	 * This should be called from the <code>getTicketFromAS()</code> when a
	 * user first logs in.
	 * @param	user  The user logging in.
	 * @param	pass  The user's password.
	 */
	private void writePasswordToDisk(String user, String pass){
		try{
			File f = new File("krb.pass." + getClientName(user));
			if( f.exists() ) f.delete();

			FileWriter out = new FileWriter(f);
			out.write(pass);
			out.close();
		}catch(IOException ioe){
			ioe.printStackTrace();
		}
	}

	/**
	 * Method that prints out each key in the database.
	 */
	public void dumpState(){
		String output = "Dumping Kerberos State:";
		Enumeration nameEnum = keys.keys();
		output += "\n******** Session Keys ********";

		while (nameEnum.hasMoreElements()) {
			String name = (String) nameEnum.nextElement();
			byte[] key = (byte[]) keys.get(name);
			output += "\n\t" + name + "\t" + new String(Hex.encode(key));
		}

		printout(0, node.id, output);
	}
}
