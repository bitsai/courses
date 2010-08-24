package A0;

import simnet.*;
import java.util.*;
import graph.*;

/** An application that implements edge sampling traceback. */
public class Traceback extends Application implements BPFConsumer, Pluggable {
	/** BPF rule. */
	BPF bpf;

	/**
	* Hash of arrays, where each array stores packets with the same distance, and the hash indexes arrays by distance.
	* Hashtables and vectors are chosen because they are thread-safe.
	* Used for path reconstruction.
	*/
	Hashtable packets;

	/** Max distance seen in traceback marked packets. */
	int max_distance;

	/**
	* Called from UI to initiate traceback'ing.
	* Place BPF rule matching all incoming packets.
	* Clear previous state.
	*/
	public void start_tb() {
		/** Place a BPF rule matching all incoming packets. */
		bpf = new BPF(-1, node.id, -1, -1, this);
		node.addBPF(bpf, Simnet.IN, -1);

		/** Clear previous state. */
		packets = new Hashtable();
		max_distance = -1;
	}

	/**
	* Called from UI to perform reconstruction of attack paths.
	* Run path reconstruction algorithm.
	* Output attack paths.
	*/
	public void traceback() {
		/** Run path reconstruction algorithm. */
		Graph graph = new Graph(); // Graph object from Dr. Scheinerman's graph package
		int edge_name = 0; // Counter for generating unique edge names
		HashSet origins = new HashSet(); // Stores id of nodes that start attack paths
		HashSet last_set = new HashSet(); // Set of IP value of nodes at distance d - 1
		Hashtable packets_by_node = new Hashtable(); // Stores number of packets from each node

		/** For each distance from 0 to d... */
		for (int d = 0; d <= max_distance; d++) {
			/** Get all packets at this distance. */
			Vector packet_array = (Vector) packets.get(new Integer(d));

			/** Get ready to store the IP value of nodes at this distance for the next iteration. */
			HashSet next_last_set = new HashSet();

			/** For each packet at this distance... */
			for (int packet_index = 0; packet_index < packet_array.size(); packet_index++) {
				IP_Packet packet = (IP_Packet) packet_array.get(packet_index);
				int z = packet.trace;

				/** If distance is 0... */
				if (d == 0) {
					/** Increment the number of packets from this node. */
					incrementHashOfIntegers(packets_by_node, new Integer(z));

					/** Add a new edge from this node to traceback caller. */
					graph.add(new Edge(edge_name, node.id, z, false));

					/** Increment edge name counter. */
					edge_name++;

					/** The tail of an edge cannot be the start of an attack path. */
					origins.remove(new Integer(node.id));

					/** The head of an edge might be the start of an attack path. */
					origins.add(new Integer(z));

					/** Add this node's IP to the set of "last" IP's for next iteration. */
					next_last_set.add(new Integer(z));
				/** If distance > 0... */
				} else {
					/** Get iterator for set of IP of nodes at distance d - 1. */
					Iterator last_iterator = last_set.iterator();

					/** For each "last" IP... */
					while (last_iterator.hasNext()) {
						int last = ((Integer) last_iterator.next()).intValue();

						/** XOR the "last" IP with the trace field to get the real IP. */
						int real_z = z ^ last;

						/** If the "last" IP and current IP are both valid... */
						if (node.lookup(last) != null && node.lookup(real_z) != null) {
							/** Increment the number of packets from this node. */
							incrementHashOfIntegers(packets_by_node, new Integer(real_z));

							/** Add a new edge from this node to the "last" node. */
							graph.add(new Edge(edge_name, last, real_z, false));

							/** Increment edge name counter. */
							edge_name++;

							/** The tail of an edge cannot be the start of an attack path. */
							origins.remove(new Integer(last));

							/** The head of an edge might be the start of an attack path. */
							origins.add(new Integer(real_z));

							/** Add this node's IP to the set of "last" IP's for next iteration. */
							next_last_set.add(new Integer(real_z));
						}
					}
				}
			}

			/** Update set of "last" IP's for next iteration. */
			last_set = next_last_set;
		}

		/** Output attack paths. */
		node.printout(0, node.id, "traceback (Edge Sampling PPM) from " + node.lookup(node.id) + "(" + Utils.getStringFromIP(node.id) + ")");

		/** Get iterator for origins of attack paths. */
		Iterator origin_iterator = origins.iterator();

		/** For each origin... */
		while (origin_iterator.hasNext()) {
			int id = ((Integer) origin_iterator.next()).intValue();

			/** Call TracebackPath to get attack path from origin to traceback caller. */
			TracebackPath attack_path = new TracebackPath(graph, id, node.id);

			/** Get nodes on the attack path. */
			Vector nodes = attack_path.getPath(node.id);

			/** Print information for each node on the attack path. */
			for (int index = 1; index < nodes.size(); index++) {
				int vertex_id = Integer.parseInt(nodes.get(index).toString());
				String vertex_name = node.lookup(vertex_id);
				String vertex_IP = Utils.getStringFromIP(vertex_id);
				Integer vertex_packets = (Integer) packets_by_node.get(new Integer(vertex_id));
				node.printout(0, node.id, index + " " + vertex_name + "(" + vertex_IP + ") " + vertex_packets);
			}

			node.printout(0, node.id, "");
		}
	}

	/** Generic method for incrementing a value in a hash of integers. */
	protected void incrementHashOfIntegers(Hashtable hash, Object key) {
		if (hash.containsKey(key)) {
			int old_value = ((Integer) hash.get(key)).intValue();
			Integer new_value = new Integer(old_value + 1);
			hash.put(key, new_value);
		} else {
			hash.put(key, new Integer(1));
		}
	}

	/**
	* Called from UI to perform reconstruction of attack paths.
	* Shorthand notation for calling traceback().
	*/
	public void tb() { traceback(); }

	/**
	* Called from UI to end traceback'ing.
	* Remove the BPF.
	*/
	public void stop_tb() {
		if (bpf != null) {
			node.removeBPF(bpf, Simnet.IN, -1);
			bpf = null;
		}
	}

	/** Process incoming packets. */
	public synchronized void inBPF(int bpf_id, IP_Packet ip_packet) {
		/**
		* If a packets has distance >= 0, put it in the packets hash of arrays.
		* Packets with distance = -1 never got marked by a traceback router.
		* Therefore they have no useful information.
		*/
		if (ip_packet.distance >= 0) {
			addToHashOfArrays(packets, new Integer(ip_packet.distance), ip_packet);
			if (ip_packet.distance > max_distance) { max_distance = ip_packet.distance; }
		}
	}

	/** Generic method for adding an element to a hash of arrays. */
	protected void addToHashOfArrays(Hashtable hash, Object key, Object element) {
		if (hash.containsKey(key)) {
			Vector array = (Vector) hash.get(key);
			array.add(element);
		} else {
			Vector array = new Vector();
			array.add(element);
			hash.put(key, array);
		}
	}

	/** A traceback application can only be removed if it is not currently traceback'ing. */
	public synchronized boolean prePlugout(Object replacement) {
		if (bpf != null) {
			node.printout(0, node.id, "Cannot remove traceback application while traceback is in progress.");
			return false;
		} else { return true; }
	}
}
