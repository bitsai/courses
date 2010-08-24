import java.io.*;
import java.net.*;
import java.util.regex.*;

// This thread handles direct-connect communication for its parent client

public class HiRCDCThread extends Thread {
	HiRCClient client = null; // The client that owns this direct-connect thread
	Socket socket = null;
	PrintWriter out = null;
	BufferedReader in = null;
	String myUser = null; // Name of the user that owns the client that owns this direct-connect thread
	int seq = 0;
	boolean done = false;

	int connect = -1;
	int bye = -1;

// Initialization

	public HiRCDCThread(HiRCClient client, Socket socket) {
		this.client = client;
		this.socket = socket;
		this.myUser = client.myUser;

		try {
			out = new PrintWriter(socket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		} catch (Exception e) {
			System.err.println(e);
		}
	}

// Utility methods

	// Gets the next line of input from the socket
	public String getInput() {
		String input = null;

		try {
			input = in.readLine();
			if (input.length() > 0) { System.out.println("Received string from DCThread: " + input); }
		} catch (Exception e) {
			System.err.println(e);
		}

		return input;
	}

	// Gets a command's sequence number, returns -1 if nothing is found
	public int getInputSeq(String input) {
		int inputSeq = -1;
		String tagValue = stripTagName(input, "SEQ: (.+)");
		if (!tagValue.equals("")) {
			inputSeq = new Integer(tagValue).intValue();
		}
		return inputSeq;
	}

	// Returns a new sequence number, and increments sequence counter by 1
	public int getSeq() {
		seq++;
		return seq - 1;
	}

	// Given a line, strips off the tag name according to the input pattern, and returns the tag value
	// ... hopefully
	public String stripTagName(String input, String pattern) {
		String output = "";
		Pattern p = Pattern.compile(pattern);
		Matcher m;
		if ((m = p.matcher(input)).matches()) {
			output = m.group(1);
		}
		return output;
	}

// Outgoing command methods

	// Sends message
	public void sendMsg(String msg) {
		out.println("SEND");
		out.println("UID: " + myUser);
		out.println("MSG: " + msg);
		out.println("SEQ: " + getSeq());
		out.println();
	}

	// Sends connect request
	public void sendConnect() {
		connect = getSeq();
		out.println("CONNECT");
		out.println("UID: " + myUser);
		out.println("SEQ: " + connect);
		out.println();
	}

	// Sends error command
	public void sendError(String errMsg, int seqNum) {
		out.println("ERROR");
		out.println("ERRMSG: " + errMsg);
		out.println("SEQ: " + seqNum);
		out.println();
	}

	// Sends OK command
	public void sendOK(int seqNum) {
		out.println("OK");
		out.println("SEQ: " + seqNum);
		out.println();
	}

	// Sends bye command
	public void sendBye() {
		bye = getSeq();
		out.println("BYE");
		out.println("SEQ: " + bye);
		out.println();
	}

// Thread run method

	public void run() {
		try {
			String fromThread = "";

			// Listen for incoming commands, and call the appropriate processing method

			while (!done && (fromThread = getInput()) != null) {
				if (fromThread.equals("CONNECT")) { connect(); } else
				if (fromThread.equals("OK")) { ok(); } else
				if (fromThread.equals("ERROR")) { error(); } else
				if (fromThread.equals("BYE")) { bye(); } else
				if (fromThread.equals("SEND")) { send(); } else
				if (fromThread.length() > 0) { sendError("", -1); }
			}

			out.close();
			in.close();
			socket.close();
		} catch (Exception e) {
			System.err.println(e);
		}
	}

// Incoming command methods

	// Process incoming connect request
	public void connect() {
		String UID = stripTagName(getInput(), "UID: (.+)");
		int inputSeq = getInputSeq(getInput());
		if (UID.equals("") || inputSeq == -1) {
			sendError("", -1);
		} else {
			client.otherGuy = UID;
			client.user_interface.displayText("Direct-connected to " + client.otherGuy);
			sendOK(inputSeq);
		}
	}

	// Process incoming OK command
	public void ok() {
		int inputSeq = getInputSeq(getInput());

		// If the sequence number matches our last outgoing connect command, connection is successful
		if (inputSeq == connect) {
			client.user_interface.displayText("Direct-connected to " + client.otherGuy);
		} else

		// If the sequence number matches our last outgoing bye command, time to tear down the connection
		if (inputSeq == bye) {
			client.user_interface.displayText("Direct-connection to " + client.otherGuy + " torn down.");
			client.otherGuy = null;
			client.DCThread = null;
			client.incomingDirectConnect = -1;
			client.outgoingDirectConnect = -1;
			done = true;
		}
	}

	// Process error command
	public void error() {
		String errMsg = stripTagName(getInput(), "ERRMSG: (.+)");
		int inputSeq = getInputSeq(getInput());
	}

	// Process incoming bye command
	public void bye() {
		int inputSeq = getInputSeq(getInput());

		// Bad or non-existent sequence number; send error
		if (inputSeq == -1) {
			sendError("", -1);

		// Time to tear down the direct-connection
		} else {
			sendOK(inputSeq);
			client.user_interface.displayText("Direct-connection to " + client.otherGuy + " torn down.");
			client.otherGuy = null;
			client.DCThread = null;
			client.incomingDirectConnect = -1;
			client.outgoingDirectConnect = -1;
			done = true;
		}
	}

	// Process incoming send command
	public void send() {
		String UID = stripTagName(getInput(), "UID: (.+)");
		String msg = stripTagName(getInput(), "MSG: (.+)");
		int inputSeq = getInputSeq(getInput());

		// Malformed command; send error
		if (UID.equals("") || inputSeq == -1) {
			sendError("", -1);

		// Print the message that was sent
		} else {
			client.user_interface.displayText("<" + UID + "> " + msg);
			sendOK(inputSeq);
		}
	}
}