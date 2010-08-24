package A1;

import simnet.*;
import java.util.*;

import org.bouncycastle.util.encoders.Hex;

public class TLSCrackedClient extends Application
{
	/**
	 *  The TLS Socket this client is operating on/with
	 */
	TLSSocket s;

	/**
	 * setup the socket and connect
	 *
	 *  @param host - host with which to communicate
	 *  @param port - the port to reach 'host' on
	 */
	public boolean doHandshake (String host, Integer port) {
		s = new TLSSocket(this,node);

		try {
			s.connect(sim.lookup(host), port.intValue());
		}
		catch (Exception e) {
			printout(0, node.id, "Got exception trying to do the connection: " + e);
			return false;
		}

		return true;
	}

	/**
	 *  Send a string over the TLS Socket and recv the reply
	 *
	 *  @param str - string to echo
	 */
	public void echo (String str) {
		// Spam message 400,000 times, enough for cracker to try to guess key
		int totalpackets = 400000;

		for (int i = 0; i < totalpackets; i++) {
			try {
				// Progress update every 2048 packets
				if (i % 2048 == 0) {
					printout(0, node.id, "Sent " + i + " packets");
				}

				// Send like mad!
				s.send("from me: " + str);
				Object o = s.recv();
			} catch (Exception sse) {
				printout(0, node.id, "Got exception in echo: " + sse);
			}
		}
	}

	public boolean prePlugout (Object o) {
		return true;
	}
}
