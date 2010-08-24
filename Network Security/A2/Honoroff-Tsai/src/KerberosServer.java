////////////////////////////////////////////////////////////////////
// Simnet, version 0.9                                            //
// COPYRIGHT Seny Kamara, Darren Davis, Fabian Monrose, 2002-2004 //
////////////////////////////////////////////////////////////////////
package A2;


import simnet.*;
import java.util.*;
import java.lang.reflect.*;

/**
 * Super-class of all Kerberized-servers.  Receives TCP requests and attempts to authenticate
 * connections.  If this succeeds, forks a thread (as defined by extending classes) to handle
 * communication.
 */
public abstract class KerberosServer extends KerberosApplication{


	/**
	 * The service's private key.  Loaded by setPrivateKey(), which is called
	 * form the Simnet UI.
	 */
	private byte [] privateKey;

	/**
	 * Listen here for requests
	 */
	private ServerSocket ssock;

	/**
	 * The name of our service, e.g., `echo@CMU'.  Set by <code>setName()</code>.
	 */
	protected String serviceName;

	/**
	 * The fully qualified name of the thread that we will spawn.
	 * Set by <code>setSpawnedThreadClass()</code>
	 */
	protected String spawnedThreadClass;

	/**
	 * The port we will listen on.  Set by <code>setPort()</code>.
	 */
	protected int port;

	/**
	 * Extending classes that wish to share objects among threads must
	 * instantiate this array and put the objects here
	 */
	public Object [] sharedObjects;

	/**
	 * Set information about ourselves.
	 */
	public void customInit(){
		super.customInit();
		setName();
		setPort();
		setSpawnedThreadClass();
	}

	/**
	 * Method which sets our private key based on a password.
	 * This must be called from the UI before any tickets are sent.
	 * @param	pass  The password used to generate our private key.
	 */
	public void setPrivateKey(String pass){
		privateKey = getPrivateKey(pass);
	}


	/**
	 * Method that listens for requests, attempts authentication, and
	 * then forks the correct thread to handle the connection
	 */
	public void run(){
		ssock = ((TCP) node.getTransport(Simnet.PROTO_TCP)).createServerSocket(this);

		Thread.currentThread().setName(serviceName);

		try{
			ssock.bind(port);
			ssock.listen(5);

			while (!ssock.isClosed()) {
				Socket s = ssock.accept();

				//get service request
				KerberosMessage km = (KerberosMessage) s.recv();

				//we only know how to process this type of message -- shouldn't get other types
				if(km.type != KerberosMessage.KRB_AP_REQ) {
					node.printout(Simnet.VC_ALWAYS, Simnet.VL_ERROR, node.id, "Unknown packet type (" + km.type + ")");
					s.send(new KerberosMessage(KerberosMessage.KRB_ERR_UXPT_PACKET));
					wait(KerberosAuthenticationServers.SOCKET_TIMEOUT);
					s.close();
					continue;
				}

				//get the data from the message
				KerberosMessageData kmd = (KerberosMessageData) km.data;

				// Extract and decrypt ticket
				KerberosTicket ticket = (KerberosTicket) decryptObject((byte[]) kmd.ticket, privateKey);

				// Extract the shared client-TGS key
				byte[] sharedKey = ticket.key;

				// Extract and decrypt authenticator
				KerberosAuthenticator auth = (KerberosAuthenticator) decryptObject((byte[]) kmd.authenticator, sharedKey);

				// Test for validity
				int result = compareAuthToTicket(auth, ticket, s.getIP());

				if(result != 0) {
					node.printout(Simnet.VC_ALWAYS, Simnet.VL_ERROR, node.id, "Invalid ticket and authenticator");
					s.send(new KerberosMessage(result));
					wait(KerberosAuthenticationServers.SOCKET_TIMEOUT);
					s.close();
					continue;
				}

				printout(0, node.id, "Valid User, Authenticating connection");

				s.send(new KerberosMessage(KerberosMessage.KRB_AP_REP));

				Application spawnedThread = getThreadInstance(s);
				spawnedThread.start();
			}
		} catch(Exception e) {
			e.printStackTrace();
		}

	}  //closes run



	/** Method to determine what type of thread should be spawned.
	 * <p />
	 * We'll use reflection so we can launch instances of different types of threads
	 * all such threads must have the constructor Thread(Socket, Node, KerberosServer);
	 * See <a href="http://java.sun.com/docs/books/tutorial/reflect/object/create.html">Reflection</a>
	 * for more information on instantiating objects through reflection
	 * @param s The socket to communicate over.
	 * @return an Application to handle communication over the socket.
	 * @throws Exception when there is any sort of problem with reflection.
	 */

	private Application getThreadInstance(Socket s) throws Exception{
		Class cls = Class.forName(spawnedThreadClass);

		Class [] dummyConstructorArgs = new Class [] {Socket.class, Node.class, KerberosServer.class};
		Constructor constructor = cls.getConstructor( dummyConstructorArgs );

		Object [] constructorArgs = new Object [] {s, node, this};

		return (Application) constructor.newInstance( constructorArgs );

	}

	/**
	 * Let extending classes tell us the port to listen on.
	 */
	protected abstract void setPort();

	/**
	 * Let extending classes tell us the name of our service.
	 */
	protected abstract void setName();

	/**
	 * Let extending classes tell us the name of the type of thread to spawn.
	 */
	protected abstract void setSpawnedThreadClass();

	/**
	 * Close our server socket.
	 */
	public void close(){
		ssock.close();
	}

}
