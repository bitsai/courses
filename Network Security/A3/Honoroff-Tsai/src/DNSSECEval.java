package A3;

import simnet.*;
import java.io.*;
import java.util.*;
import java.lang.Math;

public class DNSSECEval extends DNSSEC implements Pluggable {
    //public class DNSEval extends DNS implements Pluggable {
    
    private final static int INTERVAL_LEN  = 500;
    private final static int NUM_SAMPLES = 10;
    private final static int BASE_REQUESTS = 10;
    private final static int MAX_ADDITIONAL_REQUESTS = 100;
    
    String myname = null;
    
    private Vector sameTops = new Vector();
    private Vector diffTops = new Vector();
    private Vector sameSecs = new Vector();

    private Vector nsSameTops = new Vector();
    private Vector nsDiffTops = new Vector();
    private Vector nsSameSecs = new Vector();
    
    public void evaldns() {
	generateQueriesThread t = new generateQueriesThread();
	t.start();
    }

    
    public void customInit() {
	
    }

    private String[] nsnames = new String[] {
	"akadns.com",
	"apple.com",
	"brandeis.edu",
	"com",
	"cs.jhu.edu",
	"edu",
	"google.com",
	"isi.jhu.edu",
	"jhu.edu",
	"yahoo.com"
    };
	
    
    private String[] hostips = new String[] {
	"68.208.48.46",
	"68.142.226.49",
	"68.190.35.230",
	"68.218.71.230",
	"68.190.25.17",
	"128.2.42.42",
	"65.1.2.3",
	"128.2.42.42",
	"128.2.0.1",
	"128.2.11.44",
	"128.2.203.179",
	"128.220.2.7",
	"129.64.99.11",
	"128.220.2.7",
	"128.220.2.80",
	"128.220.2.80",
	"128.220.2.207",
	"128.220.13.50",
	"128.220.13.50",
	"65.17.33.7",
	"216.239.41.104",
	"216.239.51.107",
	"216.239.51.88",
	"64.233.161.104",
	"17.112.152.32",
	"17.254.2.129",
	"17.254.3.41",
	"128.220.13.50",
	"128.220.13.101",
	"128.220.13.95",
	"128.220.224.76",
	"128.220.223.217",
	"128.220.223.223",
	"128.220.247.1",
	"128.220.247.140",
	"128.220.247.26",
	"128.220.247.141",
	"65.1.2.3",
	"65.42.42.42",
	"65.0.0.1",
	"65.0.0.1",
	"64.236.24.12",
	"199.181.132.250",
	"65.17.33.7",
	"65.17.33.7",
	"65.17.33.7",
	"68.208.48.46",
	"129.64.99.11",
	"129.64.99.130",
	"129.64.99.12",
	"129.64.99.132",
	"129.64.3.61",
	"129.64.3.60",
	"129.64.3.241",
    };

    private String[]  hostnames = new String[] {
	"ns.yahoo.com",
	"www.yahoo.com",
	"sports.yahoo.com",
	"games.yahoo.com",
	"maps.yahoo.com",
	"ns.cmu.edu",
	"ns.vs.com",
	"ns.cmu.edu",
	"www.cmu.edu",
	"andrew.cmu.edu",
	"www.cs.cmu.edu",
	"jhname.hcf.jhu.edu",
	"frasier.brandeis.edu",
	"jhname.hcf.jhu.edu",
	"www.jhu.edu",
	"jhuniverse.hcf.jhu.edu",
	"webapps.jhu.edu",
	"blaze.cs.jhu.edu",
	"blaze.cs.jhu.edu",
	"ns.akadns.com",
	"www.google.com",
	"gmail.google.com",
	"answers.google.com",
	"images.google.com",
	"www.apple.com",
	"dev.apple.com",
	"store.apple.com",
	"blaze.cs.jhu.edu",
	"www.cs.jhu.edu",
	"rtfm.cs.jhu.edu",
	"ugrad1.cs.jhu.edu",
	"masters1.cs.jhu.edu",
	"phd1.cs.jhu.edu",
	"gw.isi.jhu.edu",
	"spar.isi.jhu.edu",
	"simnet1.isi.jhu.edu",
	"gls.isi.jhu.edu",
	"ns.vs.com",
	"root.vs.com",
	"vs.com",
	"www.vs.com",
	"www.cnn.com",
	"www.espn.com",
	"ns.akadns.com",
	"ns.akadns.com",
	"ns.akadns.com",
	"ns.yahoo.com",
	"frasier.brandeis.edu",
	"sam.brandeis.edu",
	"lilith.brandeis.edu",
	"norm.brandeis.edu",
	"bia.cs.brandeis.edu",
	"talos.cs.brandeis.edu",
	"lichas.cs.brandeis.edu"
    };
    
    private void getRandChars(byte [] chars)
    {
	int count = chars.length;
	for(int i = 0; i < chars.length; i++) {
	    chars[i] = (byte) ('A' + (int) (Math.random() * 26.0));
	}
    }
    
    private String randstringDiffTop()
    {
	String newtop = null;
	byte [] randbytes = new byte[8]; 
	getRandChars(randbytes);
	
	int topindex1 = myname.lastIndexOf('.');
	String top1 = myname.substring(topindex1);
	if(top1.equals(".com")) {
	    return new String(randbytes + ".edu");
	} else {
	    return new String(randbytes + ".com");
    	}
    }

    private String randstringSameTop()
    {
	byte [] randbytes = new byte[8]; 
	getRandChars(randbytes);

	int topindex1 = myname.lastIndexOf('.');
	String top1 = myname.substring(topindex1);
	return (new String(randbytes) + top1);
    }
    
    private String randstringSameSec()
    {
	byte [] randbytes = new byte[8]; 
	getRandChars(randbytes);

	int topindex1 = myname.lastIndexOf('.');
	int secindex1 = myname.lastIndexOf('.', topindex1 - 1);
	String sec1 = myname.substring(secindex1);
	
	return ((new String(randbytes)) + sec1);
    }

    private boolean sameTop(String s1, String s2)
    {
	int topindex1 = s1.lastIndexOf('.');
	int topindex2 = s2.lastIndexOf('.');
	// skip the '.', or its beginning if lastindex returned -1
	String top1 = s1.substring((topindex1 + 1)); 
	String top2 = s2.substring((topindex2 + 1));
    	return(top1.equals(top2));
    }

    private boolean sameSec(String s1, String s2)
    {
	int topindex1 = s1.lastIndexOf('.');
	int topindex2 = s2.lastIndexOf('.');
	
     	if(topindex2 == -1) {
	    return(false);
	}
	
	int secindex1 = s1.lastIndexOf('.', topindex1 - 1);
	int secindex2 = s2.lastIndexOf('.', topindex2 - 1);
	// skip the '.', or its beginning if lastindex returned -1
	String sec1 = s1.substring(secindex1 + 1);
	String sec2 = s2.substring(secindex2 + 1);

	    
	return(sec1.equals(sec2));
    }
    
    public DNSSECEval() {
	
    }
    
     // Thread that runs the query generator
    private class generateQueriesThread extends Thread {
	public void run() {
	    
	    int i;
	    int j;

	    if(!client) {
		node.printout(0, node.id, "Not a client");
		return;
	    }
	    
	    // first we initialize the dns name of this node, and then
	    // use that to initialize the sameTops, diffTops, and
	    // sameSecs vectors.
	    
	    for(i = 0; i < hostips.length; i++) {
		    if(node.id == Utils.getIPFromString(hostips[i])) {
			myname = hostnames[i];
		    }
		}
		node.printout(0, node.id, myname);
		for(i = 0; i < hostnames.length; i++) {
		    if(!sameTop(myname, hostnames[i])) {
			diffTops.add(hostnames[i]);
		    } else {
			if (!sameSec(myname, hostnames[i])) {
			    sameTops.add(hostnames[i]);
			} else {
			    sameSecs.add(hostnames[i]);
			}
		    }
		}
		node.printout(0, node.id, diffTops.toString());
		node.printout(0, node.id, sameTops.toString());
		node.printout(0, node.id, sameSecs.toString());
		
		for(i = 0; i < nsnames.length; i++) {
		    if(!sameTop(myname, nsnames[i])) {
			nsDiffTops.add(nsnames[i]);
		    } else {
			if (!sameSec(myname, nsnames[i])) {
			    nsSameTops.add(nsnames[i]);
			} else {
			    nsSameSecs.add(nsnames[i]);
			}
		    }
		}
		node.printout(0, node.id, nsDiffTops.toString());
		node.printout(0, node.id, nsSameTops.toString());
		node.printout(0, node.id, nsSameSecs.toString());
			
		for(i = 0; i < NUM_SAMPLES; i++) {
		    
		    
		    int numreq = (int) (BASE_REQUESTS + (Math.sin(Math.PI * (((double) i) / NUM_SAMPLES)) * MAX_ADDITIONAL_REQUESTS)); 
		    int sleepAmount = INTERVAL_LEN / numreq;
		    
		
		node.printout(0, node.id, "interval: " + i + " numreq: " + numreq + " sleepamt " + sleepAmount);
 	    
		// sleep a random initial amount so that nodes are not in
		// sync
	    
		int initsleep = (int) (Math.random() * sleepAmount);
	    
		try {
		    sleep(initsleep);
		} catch (Exception e) {
		    node.printout(0, node.id, e.toString());
		}
		for(j = 0; j < numreq; j++) {
		    String query = null;
		    String result;
		    int resultInt;
		    double r = Math.random();

		    if(r < .05) {
			while(query == null) {
			    query = getQuery(false);
			}
			node.printout(0, node.id, "NSQuery " + query);
			result = getNSByName(query);
		    } else {
			while(query == null) {
			    query = getQuery(true);
			}
			node.printout(0, node.id, "AQuery " + query);
			resultInt = getHostByName(query);
			result = Utils.getStringFromIP(resultInt);
		    }
		    node.printout(0, node.id, "Result " + query + " " + result);
		    try {
			sleep(sleepAmount);
		    } catch (Exception e) {
			node.printout(0, node.id, e.toString());
		    }
		
		}
	    }
	}
    }

    
    private String getQuery(boolean AQuery)
    {
	
	// FIX add A vs. NS query
	Vector st;
	Vector dt;
	Vector ss;
	boolean fail = false;
	String retval = "";
	double r;
	
	if(AQuery) {
	    st = sameTops;
	    dt = diffTops;
	    ss = sameSecs;
	} else {
	    st = nsSameTops;
	    dt = nsDiffTops;
	    ss = nsSameSecs;
	}
	r = Math.random();
	if(r < .15) {
	    fail = true;
	}
	
	r = Math.random();
	
	if(r < .2) {

	    // query other top level domain
	    if(fail) {
		retval = randstringDiffTop();
		//node.node.printout(0, node.id, "LOOK " + myname + " " + retval);
	    } else {
		if(dt.size() == 0) {
		    return null;
		}
		int index = (int) (Math.random() * ((double) (dt.size())));
		retval = (String) dt.get(index);
	    }
	} else if(r >= .2 && r < .5) {
	    // query to same top level domain
	    if(fail) {
		retval = randstringSameTop();
	    } else {
		if(st.size() == 0) {
		    return null;
		}
		int index = (int) (Math.random() * ((double) (st.size())));
		retval = (String) st.get(index);
	    }
	} else {
	    // query to different top level domain
	    if(fail) {
		retval = randstringSameSec();
	    } else {
		if (ss.size() == 0) {
		    return null;
		}
		int index = (int) (Math.random() * ((double) (ss.size())));
		retval = (String) ss.get(index);
	    }
	}
	return(retval);
    }

	
	

    public synchronized boolean prePlugout(Object replacement) {
	return true;
    }
}
