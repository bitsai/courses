

package A1;
import simnet.*;
import java.util.*;
import java.math.BigInteger;

/** A router that implements VPN. */
public class VPNRouter extends Router {

    public final static int DH_RAND_SIZE = 20;
    public final static int AES_KEY_SIZE = 16;

    // LOCK passed to threads for synchronization
    Object LOCK = new Object();
    SimnetCryptoEngine crypto = new SimnetCryptoEngine();
    transient ServerSocket ssock=null;
    transient HashSet sessions=new HashSet(); // set of active client threads
        
    public void initialize(int router_id, String name){
	super.initialize(router_id, name);
	try {
	    ssock=((TCP)this.getTransport(Simnet.PROTO_TCP)).createServerSocket(this);
	    ssock.bind(VPNParams.getVPNPort());
	    ssock.listen(3);
	    VPNServerThread st=new VPNServerThread(LOCK);
	    st.start();
	}
	catch (Exception e) {
	    printout(0,id,"VPNRouter: error opening connection on port "+ VPNParams.getVPNPort() +": "+e);
	}
	
    }
    
    public String toString() 
    {
	String retval;
	synchronized(LOCK) {
	    retval = new String(name + ":" + "VPN tunnels: " + sessions.size());
	}
	return(retval);
    }
    
    public void dump_vpn_state()
    {
	printout(0,id,this.toString());
	int count = 0;
	synchronized(LOCK) {
	    Iterator it=sessions.iterator();
	    while (it.hasNext()) {
		VPNThread t=(VPNThread)it.next();
		printout(0,id,"Connection #" + count++);
		printout(0,id,t.toString());
	    }
	    
	}
	
    }
    
    public void open_tunnel(String router, String src, Integer src_fb, String dest, Integer dest_fb) {
	Socket sock;
	byte[] key = new byte[AES_KEY_SIZE];

	VPNConfig config = new VPNConfig(sim.lookup(src), src_fb.intValue(), sim.lookup(dest), dest_fb.intValue());
	try {
	    sock=((TCP)this.getTransport(Simnet.PROTO_TCP)).createSocket(this);
	    sock.connect(sim.lookup(router),VPNParams.getVPNPort());
	    
	    // client-side DH
	    
	    BigInteger recvRand, sendRand, secret, shared;
	    byte[] recvRandBytes;
	    byte[] secretRandBytes = new byte[DH_RAND_SIZE];
	    byte[] sharedDigest;
	    ByteArrayWrapper recvWrapper;
	    ByteArrayWrapper sendWrapper;
		    
	    
	    crypto.getRandomBytes(secretRandBytes);
	    secret = new BigInteger(secretRandBytes);
	    sendRand = VPNParams.getG().modPow(secret, VPNParams.getP());
	    sendWrapper = new ByteArrayWrapper(null, sendRand.toByteArray());
	    
	    
	    sock.send(sendWrapper);
	    recvWrapper = (ByteArrayWrapper) sock.recv();
	    recvRandBytes = recvWrapper.data;
	    //FIX: check for DH_RAND_SIZE?
	    recvRand = new BigInteger(recvRandBytes);
	    shared = recvRand.modPow(secret, VPNParams.getP());
	    
	    printout(0, id, "DH: g^xy");
	    printout(0, id, Utils.toHexString(shared.toByteArray()));
	    
	    sharedDigest = crypto.getSHA1Digest(shared.toByteArray());
	    for(int i = 0; i < AES_KEY_SIZE; i++) {
		key[i] = sharedDigest[i + 4];
	    }
	    byte [] sendVPN = crypto.encryptObjectAESCBC(config, key);
	    sendWrapper = new ByteArrayWrapper(null, sendVPN); 
	    sock.send(sendWrapper);
	}
	catch (Exception e) {
	    printout(0,id,"VPN couldn't connect to "+
		     router +":"+VPNParams.getVPNPort()+": "+e);
	    
	    return;
	}
	VPNThread t;
	synchronized(LOCK) {
	    t=new VPNThread(sock, key, config, LOCK);
	    
	    // connection established, so add this thread to set of sessions
	    sessions.add(t);
	}
	t.start();
	return;
	
	
	
    }
    
    // VPNServerThread: One VPNServerThread is run. It listens for
    // connections from other VPNs, and when one connects to set up a
    // tunnel, it does the DH key exchange, and if
    // successful spawns off a VPNThread for the created tunnel

    private class VPNServerThread extends Thread {    
	
	
	Object LOCK;
	VPNServerThread(Object LOCK) {
	    this.LOCK = LOCK;
	}
	SimnetCryptoEngine servercrypto = new SimnetCryptoEngine();
	public void run() {
	    Thread.currentThread().setName(name + "-" + "VPNServer_Listerner");
	
	    if (ssock==null) return;
	    printout(0,id,"starting VPNServer thread on socket "+ssock);
	    while (!ssock.isClosed()) {
		try {
		    
		    //    printout(0,id,"\tVPN: waiting to accept connection ");
		    Socket s=ssock.accept();
		    
		    // server-side DH
		    
		    BigInteger recvRand, sendRand, secret, shared;
		    byte[] recvRandBytes;
		    byte[] secretRandBytes = new byte[DH_RAND_SIZE];
		    byte[] sharedDigest;
		    ByteArrayWrapper recvWrapper;
		    ByteArrayWrapper sendWrapper;
		    byte[] key = new byte[AES_KEY_SIZE];
		    
		    servercrypto.getRandomBytes(secretRandBytes);
		    secret = new BigInteger(secretRandBytes);
		    sendRand = VPNParams.getG().modPow(secret, VPNParams.getP());
		    sendWrapper = new ByteArrayWrapper(null, sendRand.toByteArray());
		    
		    //FIX: check for cast exception?

		    // recieve clients random
		    recvWrapper = (ByteArrayWrapper) s.recv();
		    // send random
		    s.send(sendWrapper);
		    recvRandBytes = recvWrapper.data;
		    //FIX: check for DH_RAND_SIZE?
		    recvRand = new BigInteger(recvRandBytes);
		    // compute the shared g^xy
		    shared = recvRand.modPow(secret, VPNParams.getP());
		    
		    synchronized ((Object) sim) {
			printout(0, id, "DH g^xy");
			printout(0, id, Utils.toHexString(shared.toByteArray()));
		    }
		    
		    // key is bytes 4-19 of sha1(g^xy)
		    sharedDigest = servercrypto.getSHA1Digest(shared.toByteArray());
		    
		    for(int i = 0; i < AES_KEY_SIZE; i++) {
			key[i] = sharedDigest[i + 4];
		    }

		    //FIX: synchronize crypto or give each thread its own?
		    
		    // server decrypts client's VPNConfig and sets its
		    // VPNConfig using reverse src/dest
		    recvWrapper = (ByteArrayWrapper) s.recv();
		    VPNConfig otherConfig = null;
		    otherConfig = (VPNConfig) servercrypto.decryptObjectAESCBC(recvWrapper.data, key);
		    		    
		    VPNConfig myVPNConfig = new VPNConfig(otherConfig.dest, otherConfig.dest_fb, otherConfig.src, otherConfig.src_fb);   
		    
		    // if we're here, connection established so add to
		    // sessions set and start tunnel thread
		    
		    VPNThread t;
		    synchronized(LOCK) {
			t=new VPNThread(s,key,myVPNConfig,LOCK);
			sessions.add(t);
		    }

		    t.start();
		}
		catch (Exception e) {
		    printout(0,id,"VPN Server: got exception: "+e);
		}
	    }
	    
	    printout(0,id,"VPN Server: stopping main thread [port="+",ssock="+ssock+"]");
	    ssock=null;
	}

	public String toString() 
	{
	    String retval;
	    retval = this.getName(); 
	    return(retval);
	}

    }
    
    // VPNThread: Each VPN tunnel is run in a VPNThread which is
    // started once the DH exchange is successful.
    // The thread sits in a while loop calling recv on its socket, and
    // when it receives data, it decrypts it and forwards it to its
    // destination 
    

    private class VPNThread extends Thread {
	
	Socket s;
	VPNConfig myVPNConfig;
	Object LOCK;
	byte[] key;
	SimnetCryptoEngine crypto = new SimnetCryptoEngine();
	

	VPNThread(Socket s, byte[] key, VPNConfig myVPNConfig, Object LOCK) {
	    this.s=s;
	    this.key = key;
	    this.LOCK = LOCK;
	    this.myVPNConfig = myVPNConfig;
	}
		
	public String toString() {
	    String retval = new String(this.getName() + "\n");
	    retval += s.toString() + "\n";
	    retval += myVPNConfig + "\n";
	    retval += "key: " + Utils.toHexString(key) + "\n";
	    retval += "-----------------------\n";
		    
	    return(retval);
	}
	       
		   
	public void run() {
	    Thread.currentThread().setName(name + "-tunnel-" + sim.lookup(s.getIP()));

	    printout(0,id,"Starting VPNThread " + getName() + " on socket "+s);
	    
	    while (!s.isClosed()) {
		IP_Packet recvPacket = null;
		try {
		    ByteArrayWrapper recvWrapper = (ByteArrayWrapper) s.recv();
		    recvPacket = (IP_Packet) crypto.decryptObjectAESCBC(recvWrapper.data, key);
		}
		catch(Exception e) {
		    printout(0, id, "VPNThread" + getName() + " socket "+s+"Error decrypting packet "+e + ", packet not forwarded");
		}
		if(recvPacket != null) {
		    decryptedForward(recvPacket);
		}
		
		    
	    }
	    //}
	    //catch (Exception e) {
	    //		printout(0,id,"VPNServer [cid="+connID+"], exception receiving packet on "+s);
	    //}
	    
	    printout(0,id,"VPNThread "+ getName() +",thread done: "+s);
	    
	    // clean up this thread
	    synchronized(LOCK) {
		sessions.remove(this);
	    }
	}
	
	
    }

    // forward is overridden to check each incoming packet to see if
    // it should go into one of this router's VPN tunnels.  If so, it
    // encrypts it and sends it through the proper tunnel socket, if
    // not it calls the superclass forward to send it conventionally
    
    protected void forward(IP_Packet ip_packet){
	boolean intunnel = false;
	int REMOVEME = 0;
	
	// since we have to iterate through the sessions hashset, we
	// make this block of code synchronized
	
	synchronized(LOCK) {
	    if(sessions != null) {
		Iterator it=sessions.iterator();
		while (it.hasNext()) {
			
		    VPNThread t=(VPNThread)it.next();
		    if(Utils.inSameSubnet(ip_packet.src, t.myVPNConfig.src, 
					  t.myVPNConfig.src_fb) &&
		       Utils.inSameSubnet(ip_packet.dest, t.myVPNConfig.dest, 
					      t.myVPNConfig.dest_fb)) {
			intunnel = true;
			try {
			    
			    byte [] sendPacket = crypto.encryptObjectAESCBC(ip_packet, t.key);
			    ByteArrayWrapper sendWrapper = new ByteArrayWrapper(null, sendPacket); 
			    
			    t.s.send(sendWrapper);
			    
			}
			
			catch (Exception e) {
			    printout(0,id,"VPN "+ t.getName()+", exception sending packet on socket  "+t.s +", msg: " +e);
			}
			
			// we found the tunnel, so stop iterating
			break;
		    }
		}
	    }
	    //	    } catch (Exception e) {
	    
	    //	printout(0,id,e.toString());
	    //  }
	    if(!intunnel) {
		super.forward(ip_packet);
	    }
	}
    }
    
    // this is called from inside thread after decryption of recieved
    // packet 
    
    protected void decryptedForward(IP_Packet ip_packet) {
	super.forward(ip_packet);
    }
  
 
    
}
      
