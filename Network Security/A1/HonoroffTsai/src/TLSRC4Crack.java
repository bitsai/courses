////////////////////////////////////////////////////////////////////
// Simnet, version 1.0                                            //
// COPYRIGHT Seny Kamara, Darren Davis, Fabian Monrose, 2002-2005 //
// Please read the DISCLAIMER                                     //
////////////////////////////////////////////////////////////////////

package A1;

import java.io.*;
import java.util.*;

import simnet.*;
import org.bouncycastle.util.encoders.Hex;

public class TLSRC4Crack extends Application implements BPFConsumer,Pluggable {
	BPF inbpf = null;
	byte crib = (byte) 0xac; // We fixed client so that first character of string is always "f"
						// Because of this, the first byte of the serialized string is always 0xAC
						// XOR this with first byte of encrypted packets to get first key output byte

	int target1_id;
	int t1_counter;
	byte[] t1_recoveredKeyBytes;
	int[] t1_table;
	int t1_offsetSum;
	int t1_recoveredSum;

	int target2_id;
	int t2_counter;
	byte[] t2_recoveredKeyBytes;
	int[] t2_table;
	int t2_offsetSum;
	int t2_recoveredSum;

	public synchronized void crack(String t1, String t2) {
		// Stop previous crack attempt
		if (inbpf!=null) {
			node.printout(0,node.id,"Stopping old crack before starting new one.");
			stopCrack();
		}

		// Initialize variables for target 1
		target1_id = sim.lookup(t1);
		t1_counter = 2;
		t1_recoveredKeyBytes = new byte[8];
		t1_table = new int[256];
		t1_offsetSum = 3;
		t1_recoveredSum = 0;

		// Initialize variables for target 2
		target2_id = sim.lookup(t2);
		t2_counter = 3;
		t2_recoveredKeyBytes = new byte[8];
		t2_table = new int[256];
		t2_offsetSum = 3;
		t2_recoveredSum = 0;

		// Add BPF rule
		inbpf = new BPF(-1, -1, Simnet.PROTO_TCP, -1, this);
		node.addBPF(inbpf, Simnet.IN, -1);
	}

	public synchronized void stopCrack() {
		// Remove BPF rule
		if (inbpf!=null) {
			node.removeBPF(inbpf, Simnet.IN, -1);
			inbpf=null;
		}
	}

	public synchronized void inBPF(int bpf_id, IP_Packet ipp) {
		// Get TCP packet from IP packet, then get TCP packet data
		TCP_Packet tcpp = (TCP_Packet)ipp.data;
		String dataString = Utils.toShortString(tcpp.data, 125);

		// If packet isn't long enough, get out
		if (dataString.length() < 50) { return; }

		// If there isn't TLS app data, get out
		if (!(dataString.substring(10, 25)).equals("ApplicationData")) { return; }

		// Get first byte of data
		String firstByteString = dataString.substring(43, 45);

		// Convert to byte array
		byte[] firstByteArray = Hex.decode(firstByteString);

		// XOR with known value of first plaintext byte to get first byte of keystream
		int firstByteInt = (firstByteArray[0] ^ crib) & 0xff;

		// If src is target 1 and dest is target 2...
		if(ipp.src == target1_id && ipp.dest == target2_id) {
			// Set X = lowest byte of counter
			int x = t1_counter & 0xff;

			// Get highest byte of counter
			int counterHigh = (t1_counter >>> 16 ) & 0xff;

			// Calculate index of current key byte
			int keyIndex = counterHigh - 3;

			// If 0 <= key index < 8 and middle byte of counter = 0xFF, proceed
			if ((keyIndex >= 0) && (keyIndex < 8) && (((t1_counter >>> 8) & 0xff) == 255)) {
				// For this direction, x = 2 is initial state for a key byte, so initialize variables
				if (x == 2) {
					t1_offsetSum += keyIndex + 3;
					t1_table = new int[256];
				}

				// Compute guess of key byte based on current packet
				int guess = (firstByteInt - x - t1_offsetSum - t1_recoveredSum) & 0xff;

				// Increment count of guess in table
				t1_table[guess]++;

				// x = 254 means we're on the last packet to use for guessing current key byte
				if (x == 254) {
					// Find guess with highest frequency
					int max = -1;
					int maxIndex = -1;
					for (int i = 0; i < 256; i++) {
						if (t1_table[i] > max) {
							max = t1_table[i];
							maxIndex = i;
						}
					}

					// Update sum used for guess calculation
					t1_recoveredSum += maxIndex;

					// Store best guess for current key byte
					t1_recoveredKeyBytes[keyIndex] = (new Integer(maxIndex)).byteValue();

					// Print guess
					printout(0, node.id, "Frequency of best guess for current key byte: " + max);
					printout(0, node.id, "Recovered " + (keyIndex + 1) + " bytes of client key: " + new String(Hex.encode(t1_recoveredKeyBytes)));

					// If we've reached the last key byte, we're done
					if (keyIndex == 7) {
						printout(0, node.id, "Recovered client key: " + new String(Hex.encode(t1_recoveredKeyBytes)));
						return;
					}
				}
			}

			// Update counter for this direction
			t1_counter += 2;
		} else if(ipp.src == target2_id && ipp.dest == target1_id) {
			// Set X = lowest byte of counter
			int x = t2_counter & 0xff;

			// Get highest byte of counter
			int counterHigh = (t2_counter >>> 16 ) & 0xff;

			// Calculate index of current key byte
			int keyIndex = counterHigh - 3;

			// If 0 <= key index < 8 and middle byte of counter = 0xFF, proceed
			if ((keyIndex >= 0) && (keyIndex < 8) && (((t2_counter >>> 8) & 0xff) == 255)) {
				// For this direction, x = 3 is initial state for a key byte, so initialize variables
				if (x == 3) {
					t2_offsetSum += keyIndex + 3;
					t2_table = new int[256];
				}

				// Compute guess of key byte based on current packet
				int guess = (firstByteInt - x - t2_offsetSum - t2_recoveredSum) & 0xff;

				// Increment count of guess in table
				t2_table[guess]++;

				// x = 255 means we're on the last packet to use for guessing current key byte
				if (x == 255) {
					// Find guess with highest frequency
					int max = -1;
					int maxIndex = -1;
					for (int i = 0; i < 256; i++) {
						if (t2_table[i] > max) {
							max = t2_table[i];
							maxIndex = i;
						}
					}

					// Update sum used for guess calculation
					t2_recoveredSum += maxIndex;

					// Store best guess for current key byte
					t2_recoveredKeyBytes[keyIndex] = (new Integer(maxIndex)).byteValue();

					// Print guess
					printout(0, node.id, "Frequency of best guess for current key byte: " + max);

					printout(0, node.id, "Recovered " + (keyIndex + 1) + " bytes of server key: " + new String(Hex.encode(t2_recoveredKeyBytes)));

					// If we've reached the last key byte, we're done
					if (keyIndex == 7) {
						printout(0, node.id, "Recovered server key: " + new String(Hex.encode(t2_recoveredKeyBytes)));

						// Guessing last key byte for this direction means we're really done
						stopCrack();

						return;
					}
				}
			}

			// Update counter for this direction
			t2_counter += 2;
		}
	}

	public synchronized boolean prePlugout(Object replacement) {
		stopCrack();

		return true;
	}
}
