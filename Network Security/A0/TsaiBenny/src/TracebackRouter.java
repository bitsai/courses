package A0;

import simnet.*;
import java.util.*;

/** A router that implements edge sampling traceback. */
public class TracebackRouter extends Router {
	/** Probability of a packet being marked; here we are using p = 1/25 as per the traceback paper. */
	public static final double p = 0.04;

	/**
	 * Marks incoming IP packets according to edge sampling algorithm,
	 * then forwards them to the appropriate transport
	 * protocol (after stripping IP header).
	 *
	 * @param ip_packet incoming IP packet to be marked and forwarded
	 */
	protected void forward(IP_Packet ip_packet){
		/** Mark packet according to edge sampling algorithm. */
		mark(ip_packet);

		/** Pass packet to original forwarding method. */
		super.forward(ip_packet);
	}

	/* Marks incoming IP packets according to edge sampling algorithm. */
	protected void mark(IP_Packet ip_packet) {
		double x = random.nextDouble();

		if (x < p) {
			ip_packet.trace = id;
			ip_packet.distance = 0;
		}
		else {
			if (ip_packet.distance == 0) { ip_packet.trace = id ^ ip_packet.trace; }
			if (ip_packet.distance >= 0) { ip_packet.distance++; }
		}
	}
}
