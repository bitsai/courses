

package A2;
import simnet.*;
import java.util.*;
import java.io.*;

/** A router that implements Synkill. */
public class SynkillRouter extends Router {
    

    // output stream for logfile
    FileOutputStream fos = null;

    // initial rulenum for firewall rules
    int rulenum = 1;

    // A lock
    Object LOCK = new Object();
    
    // use constants to represent states
    public final static int NULL =     0;
    public final static int NEW =      1;
    public final static int GOOD =     2;
    public final static int BAD =      3;
    public final static int PERFECT =  4;
    public final static int EVIL =     5;
    
    // table to map src addr to vector of stored records of open
    // connections 
    Hashtable records = new Hashtable();
    // table to map src addr to states
    Hashtable states = new Hashtable();
    // table to map src addr to expire timertasks
    Hashtable expires = new Hashtable();
    // table to map src addr to stale timertasks
    Hashtable stales = new Hashtable();
    // timer to schedule expire and stale tasks
    Timer theTimer = new Timer ();
    
    // for printing
    private String statestring(int state) {
	switch(state) {
	case NULL:
	    return("null");
	case NEW:
	    return("new");
	case GOOD:
	    return("good");
	case BAD:
	    return("bad");
	case PERFECT:
	    return("perfect");
	case EVIL:
	    return("evil");
	}
	return null;
    }

    // Record: used to record open connections for possible expiration
    
    class Record {
	int ipsrc;
	int ipdest;
	int srcport;
	int destport;
	int seq;
	int ack;			       
    
	public Record(IP_Packet ip_packet) {
	    TCP_Packet tcp_packet = (TCP_Packet) ip_packet.data;
	    ipsrc = ip_packet.src;
	    ipdest = ip_packet.dest;
	    srcport = tcp_packet.src_port;
	    destport = tcp_packet.dest_port;
	    seq = tcp_packet.seq;
	    ack = tcp_packet.ack;
	}
	
    
    }
    
    // from PacketDump.java...
    
    public String fix(int num, int len) {
	String ret=""+num;
	while (ret.length()<len) {
	    ret="0"+ret;
	}
	return ret;
    }
    


    public synchronized void printToLog(Record record, String info) {
	Calendar calendar = Calendar.getInstance();
	String time = 
	    fix(calendar.get(Calendar.HOUR_OF_DAY),2) + ":" + 
	    fix(calendar.get(Calendar.MINUTE),2) + ":" + 
	    fix(calendar.get(Calendar.SECOND),2) + ":" +
	    fix(calendar.get(Calendar.MILLISECOND),3); 
	
	String srcstring;
	String deststring;
	
	if(sim.lookup(record.ipsrc) == null) {
	    srcstring = Utils.getStringFromIP(record.ipsrc);
	} else {
	    srcstring = sim.lookup(record.ipsrc);
	}
	
	if(sim.lookup(record.ipdest) == null) {
	    deststring = Utils.getStringFromIP(record.ipdest);
	} else {
	    deststring = sim.lookup(record.ipdest);
	}
	try{    
	    
	    fos.write((PacketDump.getNextID() + "\t" + time + "\t" + srcstring + "." + record.srcport + "\t" + deststring + "." + record.destport + "\t" + info + "\n").getBytes());

	}
	catch(IOException ioe){
	    sim.error(id, "Could not write to log file"); 
	}
    
    }
    
    public void printToLog(IP_Packet packet, String info) {
	printToLog(new Record(packet), info);
    }
    
    public void dumpSynkill() {
	synchronized (LOCK) {
	    Enumeration e = states.keys();
	    while(e.hasMoreElements()) {
		Integer key = (Integer) e.nextElement();
		System.out.println(Utils.getStringFromIP(key.intValue()) + " " + statestring(((Integer) states.get(key)).intValue()));
		
	    }
	}

    }
    
    private void flushStale(int addr) {
	Integer key = new Integer(addr);
	Stale stale = (Stale) stales.get(key);
	if(stale != null) {
	    stale.cancel();
	    stales.remove(key);
	}
	
    }
    
    
    private void flushExpire(int addr) {
	Integer key = new Integer(addr);
	Expire expire = (Expire) expires.get(key);
	if(expire != null) {
	    expire.cancel();
	    expires.remove(key);
	}
	
    }


    private void flushRecords(int addr) {
	Integer key = new Integer(addr);
	records.remove(key);
    }
    
    private void updateExpire(int addr) {
	Expire newexp;
	Integer key = new Integer(addr);
	
	// if there's a timer task for this addr, cancel it
	if(expires.get(key) != null) {
	    newexp = (Expire) expires.get(key);
	    newexp.cancel();
	}
	// make a new timer task starting now
	newexp = new Expire(addr, LOCK);
	expires.put(key, newexp);
	theTimer.schedule(newexp, Expire.TIMEOUT);
    }
    
    private void updateStale(IP_Packet ip_packet) {
	Record rec = new Record(ip_packet);
	Stale newstale;
	Integer key = new Integer(rec.ipsrc);
	
	// if there's a timer task for this addr, cancel it
	if(stales.get(key) != null) {
	    newstale = (Stale) stales.get(key);
	    newstale.cancel();
	}
	// make a new timer task starting now
	newstale = new Stale(rec, LOCK);
	stales.put(key, newstale);
	theTimer.schedule(newstale, Stale.TIMEOUT);
    }

    // send an ack to allow dest to move from half-open state

    private void sendAck(IP_Packet ip_packet) {
	IP_Packet ackIP = new IP_Packet();
	TCP_Packet ackTCP = new TCP_Packet();
	TCP_Packet synack = (TCP_Packet) ip_packet.data;
	ackTCP.src_port = synack.dest_port;
	ackTCP.dest_port = synack.src_port;
	ackTCP.ACK = true;
	ackTCP.seq = 0;
	ackTCP.ack = synack.seq + 1;
	ackIP.src = ip_packet.dest;
	ackIP.dest = ip_packet.src;
	ackIP.ttl = 255;
	ackIP.protocol = Simnet.PROTO_TCP;
	ackIP.data = ackTCP;
	super.forward(ackIP);
	
    }
	    
    // send a reset

    private void sendReset(Record resetRecord) {
	IP_Packet resetIP = new IP_Packet();
	TCP_Packet resetTCP = new TCP_Packet();
	resetTCP.src_port = resetRecord.srcport;
	resetTCP.dest_port = resetRecord.destport;
	resetTCP.RST = true;
	resetIP.src = resetRecord.ipsrc;
	resetIP.dest = resetRecord.ipdest;
	resetIP.ttl = 255;
	resetIP.protocol = Simnet.PROTO_TCP;
	resetIP.data = resetTCP;
	super.forward(resetIP);
    }
    

    private void sendReset(IP_Packet ip_packet) {
	sendReset(new Record(ip_packet));
	
    }
	
    // add a connection record to the records table
    
    private void putRecord(Record newRecord) {
	synchronized (LOCK) {
	    Vector recordVec;
	    Integer key = new Integer(newRecord.ipsrc);
	    if(records.get(key) == null) {
		recordVec = new Vector();
		recordVec.add(newRecord);
		records.put(key, recordVec);
		
	    } else {
		recordVec = (Vector) records.get(key);
		recordVec.add(newRecord);
		records.put(key, recordVec);
	    }
	}
	return;
    }
    
    private void putRecord(IP_Packet ip_packet) {
	putRecord(new Record(ip_packet));
	
    }
	
    // get state... 3 possibilities:
    // not in routing table => EVIL
    // not in states hashtable  => NULL
    // in states hashtable => return the state
    
    private int getState(int ipAddr) {
	Integer key = new Integer(ipAddr); 
	// FIX is this checking the routing table or cheating?
	if(sim.lookup(ipAddr) == null) {
	    return(EVIL);
	} else if(states.get(key) == null) {
	    return(NULL);
	} else {
	    return(((Integer) states.get(key)).intValue());
	}
    }
    
    // update the states table
    
    private void putState(int ipAddr, int state) {
	Integer key = new Integer(ipAddr);
	Integer val = new Integer(state);
	if(state == NULL) {
	    states.remove(key);
	}
	states.put(key, val);
    }
        
    // the synkill router overrides the forward class and does the
    // synkill checks and appropriate actions after forwarding the
    // packet 
    
    protected void forward(IP_Packet ip_packet) {
	Expire exp;
	Stale stale;
	super.forward(ip_packet);
	if(ip_packet.protocol !=  Simnet.PROTO_TCP) {
	    return;
	}
	TCP_Packet tcp_packet = (TCP_Packet) ip_packet.data;
	
	if(tcp_packet.isSynAck()) {
	    sendAck(ip_packet);
	    return;
	}
	

	switch(getState(ip_packet.src)) {
	case NULL:
	    if(tcp_packet.isSyn()) {
		printToLog(ip_packet, statestring(NEW));
		putState(ip_packet.src, NEW);
		putRecord(ip_packet);
		// update
		updateExpire(ip_packet.src);
		
	    }
	    //  Don't agree with this part of FSM (GOOD on ACK/RST)
	    if(tcp_packet.isAck() ||
	       tcp_packet.isRst()) {
		printToLog(ip_packet, statestring(GOOD));
		putState(ip_packet.src, GOOD);
		// update
		updateStale(ip_packet);
	    }
	    break;
	case GOOD:
	    if(tcp_packet.isSyn()) {
		// don't change state
		// update
		updateStale(ip_packet);
	    }
	    
	    break;
	case NEW:
	    if(tcp_packet.isSyn()) {
		// don't change state
		putRecord(ip_packet);
	    }
	    if(tcp_packet.isAck() ||
	       tcp_packet.isRst()) {
		// PROB: It will move to GOOD when spoofed guy
		// responds with REST
		printToLog(ip_packet, statestring(GOOD));
		putState(ip_packet.src, GOOD);
		// update by removing the Expire check and
		// adding a Stale check
		flushExpire(ip_packet.src);
		updateStale(ip_packet);
	    }
	    break;
	case BAD:
	    if(tcp_packet.isSyn()) {
		printout(5, id, "Cause its bad");
		sendReset(ip_packet);
	    }
	    if(tcp_packet.isAck() ||
	       tcp_packet.isRst()) {
		printToLog(ip_packet, statestring(NEW));
		putState(ip_packet.src, NEW);
		//update
		updateExpire(ip_packet.src);
	    }
	    break;
	case PERFECT:
	    break;
	case EVIL:
	    Integer key = new Integer(ip_packet.src);
	    if(states.get(key) == null ||
	       ((Integer) states.get(key)).intValue() != EVIL) {
		// we haven't seen it yet, so we need a new rule
		// so add rule to drop further packets
		putState(ip_packet.src, EVIL);
		Rule newrule = new Rule(rulenum, Simnet.DENY, -1,ip_packet.src,-1,-1);
		addRule(newrule, Simnet.IN, -1);
		printToLog(ip_packet, statestring(EVIL) + " - filter inserted");
		rulenum++;
	    } else {
	    
		if(tcp_packet.isSyn()) {
		    sendReset(ip_packet);
		}
	    }
	    break;
	default:
	    // this should never happen!
	    ;
	
	}

    }
    public void initialize(int router_id, String name){
	printout(0, id, "Plugging in synkill");
	try{
	    fos = new FileOutputStream("Synkill.log");
	}
	catch(FileNotFoundException fne){
	    sim.error(id, "Could not open a log file");
	    return;
	}
	super.initialize(router_id, name);
    }
    
    // A timertask for expiring... when its run, we transition to BAD
    // and send RSTs for all the open sessions that are stored in the
    // vector of records

    public class Expire extends TimerTask
    {
	static final int TIMEOUT = 5000;
	Object LOCK;
	int addr;
	public Expire (int addr, Object LOCK)
	{
	    this.addr = addr;
	    this.LOCK = LOCK;
	}
	public void run()
	{
	    
	    synchronized (LOCK)
		{
		    printout(5, id, "Expiring: " + Utils.getStringFromIP(addr));
		    // transition to BAD state
		    
		    putState(addr, BAD);
		    Vector recordVec = (Vector) records.get(new Integer(addr));
		    if(recordVec != null) {
			// iterate through records and send RSTs
			// send a RST
			
			
			Iterator it = recordVec.iterator();
			while(it.hasNext()) {
			    Record r = (Record) it.next();
			    printToLog(r, statestring(BAD) + " - RST sent");
			    sendReset(r);
			}
			flushRecords(addr);
		    } else {
			printout(5, id, "null records vector");
		    }
		    flushExpire(addr);
		    
		}
	}
    }
    
    // A timertask for going stale:  we transition to NEW

    public class Stale extends TimerTask
    {
	static final int TIMEOUT = 30000;
	Object LOCK;
	Record rec;
	public Stale (Record rec, Object LOCK)
	{
	    this.rec = rec;
	    this.LOCK = LOCK;
	}
	public void run()
	{
	    synchronized (LOCK)
		{
		    printout(5, id, "Staling: " + Utils.getStringFromIP(rec.ipsrc));
		    
		    printToLog(rec, statestring(NEW) + " - went stale");
		    putState(rec.ipsrc, NEW);
		    updateExpire(rec.ipsrc);
		    flushStale(rec.ipsrc);
		    putRecord(rec);
		    
		}
	}
    }
}
      
