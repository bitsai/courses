// Plugin to demonstrate Attack on Kerberos Random Number Generation

// Attack recovers TGS key through exhaustion over random number
// generator seeds

package A2;

import simnet.*;
import java.io.*;
import org.bouncycastle.util.encoders.Hex;

public class KerberosEvilEchoClient extends KerberosEchoClient {
    final int TIME_MAX = 100;
    final int TIME_DIF_MAX = 50;
    final int RAND16_MAX = 16;
    final long MAGIC = 0xcafebabe;
    final long VERSION = 0x2e;
    final long MAGIC_AND_VERSION = (MAGIC << 32) ^ VERSION;

    Object encryptedTicket;
    String tgsNode;
    long loginTime;

    public void login(String remoteNode, String user, String pass) {
	loginTime = System.currentTimeMillis();
	super.login(remoteNode, user, pass);
	encryptedTicket = tickets.get(KerberosAuthenticationServers.TGS_SRVC + "@" + remoteNode);
	tgsNode = remoteNode;
    }

    public void crack(Integer intervalGuess) {
	long initTime;
	initTime = loginTime - intervalGuess.intValue();
	printout(0, node.id, "You are guessing that the TGS key was created between " + intervalGuess.intValue() + " and " + (intervalGuess.intValue() + TIME_MAX) + " ms before you logged in.");
	if(attack(tgsNode, initTime) == -1) {
	    printout(0, node.id, "Attack failed; guess a different time interval between TGS key creation and your log-in.");
	}
    }

    // For debugging, so we can check the tryKey function from the UI
    public void tryKey(String key) {
		if(tryKey(Hex.decode(key))) {
		    printout(0, node.id, "Yes");
		} else {
		    printout(0, node.id, "No");
		}
    }

    // function used to test each putative key in the attack, returns
    // true if the guessed key is correct which is checked by
    // attempting to decrypt (and deserialize) the ticket from the
    // TGS, and if succesful also checks for the correct service
    // name for some extra check 

    private boolean tryKey(byte[] key) {
		KerberosTicket ticket;

		try {
		    ticket = (KerberosTicket) decryptObject((byte[]) encryptedTicket, key);
		} catch (Exception e) {
		    return false;
		}

		if (ticket.service.equals(KerberosAuthenticationServers.TGS_SRVC + "@" + tgsNode)) { return true; }

		return false;
    }

    private int attack(String tgsNode, long initTime) {
	
	int victim_ip = sim.lookup(tgsNode);

	File dir = new File("classes" + File.separator + "simnet");

	if( !dir.exists()){
	    System.err.println("WARNING: the class directory does not exist!");
	    System.err.println("The resulting seed will have no entropy from the files!");
	    return -1;
	}
	File [] classes = dir.listFiles(new ClassFilter());
	File f;
	int i;
	int j;
	
	// data structure for holding the (classfile_byte,
	// classfile_byte) tuples 
	class tuple {
	    int i;
	    int j;
	}

	short[] rands = new short[256 * 256];
	for(i = 0; i < classes.length; i++) {
	    f = classes[i];
	    short rand = getRandomBytes(f);
	    rands[rand]++;
	}
	int numentries = 0;
	int maxval = 0;
	int maxindex = 0;
	for(i = 0; i < rands.length; i++) {
	    if (rands[i] != 0) {
		numentries++;
	    }
	}

	int[] freqorder = new int[numentries];

	// freqorder will hold the byte values in descending order of
	// frequencies 
	
	for(i = 0; i < freqorder.length; i++) {
	    maxval = 0;
	    maxindex = 0;
	    for(j = 0; j < rands.length; j++) {
		if(rands[j] > maxval) {
		    maxval = rands[j];
		    maxindex = j;
		}
	    }
	    freqorder[i] = maxindex;
	    rands[maxindex] = 0;
	}
	
	// we will be looking at pairs of byte values, so we
	// want to visit the pairs in an intelligent way.  we
	// store in the tupleorder array our ordering, which
	// maximizes the sum of frequencies of the bytes
	// i.e. (0,0) (1,0) (0,1) (1,1) etc.


	tuple[] tupleorder = new tuple[freqorder.length * freqorder.length];
	int count = 0;
	for(i = 0; i < freqorder.length; i++) {
	    for(j = 0; j <= i; j++) {
		tupleorder[count] = new tuple();
		tupleorder[count].i = freqorder[i];
		tupleorder[count].j = freqorder[j];
		count++;
		if(i != j) {
		    int tmp = i;
		    i = j;
		    j = tmp;
		    tupleorder[count] = new tuple();
		    tupleorder[count].i = freqorder[i];
		    tupleorder[count].j = freqorder[j];
		    count++;
		    tmp = i;
		    i = j;
		    j = tmp;
		}
	    }
	}
	
	// Now that we have the ordering for exhausting the "random"
	// bytes from the class files, we start the attack
	
	int bytecounter;
	int timecounter;
	int diffcounter;
	long time1;
	long time2;
	short short1; // in practice, its a byte;
	short short2; // in practice, its a byte;
	int rand16;
	long ip = victim_ip;
	int  cNetMask        = 0xfffffff0;
	int  cSubNetMask     = 0x0000000f;


	for(bytecounter = 0; bytecounter < tupleorder.length; bytecounter++) {
	    // print a progress of what class file byte tuple we are on
	    printout(0, node.id, "Trying file bytes " + "(" + Integer.toHexString(tupleorder[bytecounter].i) + "," + Integer.toHexString(tupleorder[bytecounter].j) + ")");
	    for(timecounter = 0; timecounter <= TIME_MAX; timecounter++) {

		for(diffcounter = 0; diffcounter < TIME_DIF_MAX; diffcounter++) {

		    for(rand16 = 0; rand16 < RAND16_MAX; rand16 ++) {

			short1 = (short) tupleorder[bytecounter].i;
			short2 = (short) tupleorder[bytecounter].j;
			time1 = initTime - timecounter;
			time2 = time1 - diffcounter;
			int  randNode        = rand16;
			long randNeighbor    = (long) (randNode | (ip & cNetMask));
			long shiftedNeighbor = circShift(randNeighbor, (long) randNode);

			//generate 3 `sub-seeds'
			long seed1 = time1 ^ ip ^ shiftedNeighbor;
			long seed2 = circShift(time2, time1 % time2);
			long seed3 = ( (((long) short1) << 32) ^
				       ((long) short2) ^
				       MAGIC_AND_VERSION
				       );
			random.setSeed( seed1 ^ seed2 ^ seed3);
			//make the key
			byte [] out = new byte[ BLOCK_LENGTH ];
			random.nextBytes(out);
			if(tryKey(out)) {
			    printout(0, node.id, "Got it!: ");
			    printout(0, node.id, "TGS Key: " +  Utils.toHexString(out));
			    printout(0, node.id, "3 seeds: " + Long.toHexString(seed1) + " " + Long.toHexString(seed2) + " " + Long.toHexString(seed3));
			    return(0);
			}
		    }
		    
		}
	    }
	}
	return -1;
	// ATTACK CODE END
    }

    // some code copied from KerberosApplication.java that we need to
    // run the attack

    	/**
	 * Utility method to perform a left circular-shift (<<<) on <code>l</code>
	 * by <code>s</code> bits.
	 * @param	l  the long to be shifted
	 * @param	s  the length of the shift
	 * @return  the shifted result.
	 */
	private long circShift(long l, long s){
	    s = s % 64;
	    long l2 = l << s;
	    l >>= (64 - s);
	    // remember that right-shifting will put 1s in the high order bits
	    l &= getMask( s );

	    return l | l2;
	}
    	private long getMask(long bits){
	    long out = 0;
	    for(int i = 0; i < bits; ++i)
		out |= ( ((long)1) << i);
	    return out;
	}

    private class ClassFilter implements FileFilter{
	public boolean accept(File f){
	    return f.getName().endsWith(".class");
	}
    }
    	/**
	 * Takes a file and produces 2 random bytes.
	 * @param f File to be searched
	 * @return two random bytes
	 */


	private short getRandomBytes(File f){
	    short r1, r2 = 0, i;
	    byte t;
	    int s;

	    DataInputStream dis = null;
	    int curOffset = 0;
	    try{
		dis = new DataInputStream(new FileInputStream( f ));

		dis.skipBytes(8);
		curOffset = 8;
		//printout(0, node.id, "" + f);
		r1 = dis.readShort();
		// bytes 8-9
		//printout(0, node.id, "r1 init: " + Integer.toHexString(r1));
		curOffset += 2;
		for(i = 1; i < r1; ++i){
		    t = dis.readByte();
		    r2 = dis.readShort();

		    s = getRandomSkip(t, r2);
		    //printout(0, node.id, "offset: " + Integer.toHexString(curOffset) + " t: " + Integer.toHexString(t) + " r2: " + Integer.toHexString(r2) + " skip: " + s);
		    if( t == 0 || t == 5 || t == 6 || t == 15) ++i;
		    dis.skipBytes(s);
		    curOffset += 3 + s;
		}
		dis.skipBytes(6);
		curOffset +=6;
		r2 = dis.readShort();

		//printout(0, node.id, "offset: " + Integer.toHexString(curOffset) + " r2 " + r2);
		curOffset +=2;
		dis.skipBytes(r2 << 1);
		curOffset += (r2 << 1);
		r2 = dis.readShort();
		//printout(0, node.id, "offset: " + Integer.toHexString(curOffset) + " r2 " + r2);
		dis.close();
	    }catch(EOFException eofe){
		printout(0, node.id, "Past end!");
		//we went past the end of the file!
		// this might happen because we are bouncing around randomly
		// if this happens, just jump out and return the last 2 bytes read (r2)
	    }catch(IOException ioe){
		//Shouldn't get this error
		ioe.printStackTrace();
	    }
	    //		printout(0, node.id, "File: " + f + " byte: " + r2);
	    return r2;
	}

	/**
	 * Based on three bytes of input, arbitrarily decide how far the next seek
	 * in <code>getRandomBytes()</codE> should be.
	 * @param	t  a byte (will be reduced mod 16 to limit our work)
	 * @param	s  in several instances, this will also be the output
	 * @return  how long the next seek should be.
	 */
	private int getRandomSkip(byte t, short s){
	    t &= 0xf;
	    switch(t){
	    case 0  : return (int) s;
	    case 1  : return (int) s;
	    case 2  : return (int) s;
	    case 3  : return 2;
	    case 4  : return 2;
	    case 5  : return 6;
	    case 6  : return 6;
	    case 7  : return 0;
	    case 8  : return 0;
	    case 9  : return 2;
	    case 10 : return 2;
	    case 11 : return 2;
	    case 12 : return 2;
	    case 13 : return 6;
	    case 14 : return 6;
	    case 15 : return 0;
	    default : return 0; //should never get called, just to placate javac
	    }
	}
}
