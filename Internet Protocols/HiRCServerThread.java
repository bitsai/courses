import java.io.*;
import java.net.*;
import java.util.*;
import java.util.regex.*;

// Handles communication with a specific client

public class HiRCServerThread extends Thread {
	String threadName = null;
	Socket socket = null;
	PrintWriter out = null;
	BufferedReader in = null;
	String myUser = null; // Name of the user that owns the client this thread is responsible for
	int seq = 0;
	boolean done = false;

	HashMap users = null; // user -> thread
	HashMap passwords = null; // user -> password

	boolean loggedIn = false;
	HiRCServerThread useraThread = null;	// The server thread belonging to the client that initiated
											// direct-connection with this thread's client

// Initialization

	public HiRCServerThread(Socket socket, String threadName, HashMap users, HashMap passwords) {
		this.threadName = threadName;
		this.users = users;
		this.passwords = passwords;

		try {
			this.socket = socket;
			out = new PrintWriter(socket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		} catch (Exception e) {
			System.err.println("Returning from thread " + threadName + ", socket exception occurred");
		}
	}

// Utility methods

	// Gets the next line of input from the socket
	public String getInput() {
		String input = null;

		try {
			input = in.readLine();
			if (input.length() > 0) { System.out.println("Received string from thread " + threadName + ": " + input); }
		} catch (Exception e) {
//			System.err.println("Returning from thread " + threadName + ", socket exception occurred");
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

	// Sends publish command
	public void publish(String user, String msg) {
		if (!user.equals(myUser)) {
			msg = "<" + user + "> " + msg;
			out.println("PUBLISH");
			out.println("MSG: " + msg);
			out.println("SEQ: " + getSeq());
			out.println();
		}
	}

	// Sends talk request to client
	public void talkReq(String usera, HiRCServerThread useraThread) {
		this.useraThread = useraThread;
		out.println("TALKREQ");
		out.println("UID: " + usera);
		out.println("SEQ: " + getSeq());
		out.println();
	}

	// Sends talk response to client
	public void talkResp(String input1, String input2) {
		out.println("TALKRESP");

		// Send the first tag, be it MSG or PORT
		out.println(input1);

		// If the first tag is PORT, the second must be IP, and we'll want to send this too
		if (input1.matches("PORT: (.+)")) {
			out.println(input2);
		}

		out.println("SEQ: " + getSeq());
		out.println();
	}

// Thread run method

	public void run() {
		try {
			String fromClient = "";

			// Listen for incoming commands, and call the appropriate processing method

			while (!done && (fromClient = getInput()) != null) {
				if (fromClient.equals("CONNECT")) { connect(); } else
				if (fromClient.equals("OK")) { ok(); } else
				if (fromClient.equals("ERROR")) { error();} else
				if (fromClient.equals("SENDALL")) { sendAll(); } else
				if (fromClient.equals("WHO")) { who(); } else
				if (fromClient.equals("BYE")) { bye(); } else
				if (fromClient.equals("TALKTO")) { talkTo(); } else
				if (fromClient.length() > 0) { sendError("", -1); }
			}

			out.close();
			in.close();
			socket.close();
			System.out.println("Returning from thread " + threadName + ", client closed socket");
		} catch (Exception e) {
			System.err.println("Returning from thread " + threadName + ", socket exception occurred");
		}
	}

// Incoming command methods

	// Hey, an incoming connect request
	public void connect() {
		String UID = stripTagName(getInput(), "UID: (.+)");
		String passwd = stripTagName(getInput(), "PASSWD: (.+)");
		int inputSeq = getInputSeq(getInput());

		// Malformed; send error
		if (UID.equals("") || passwd.equals("") || inputSeq == -1) {
			sendError("", -1);
		} else

		// Logged in already; send error
		if (loggedIn) {
			sendError("You are already logged in.", inputSeq);
		} else

		// UID being signed in with is already used; send error
		if (users.containsKey(UID)) {
			sendError("User \"" + UID + "\" is already logged in.", inputSeq);
		} else

		// UID being signed in with is invalid; send error
		if (!passwords.containsKey(UID)) {
			sendError("User \"" + UID + "\" failed authentication.", inputSeq);
		} else

		// Wrong password for this UID; send error
		if (!((String) passwords.get(UID)).equals(passwd)) {
			sendError("User \"" + UID + "\" failed authentication.", inputSeq);

		// Whew, finally, valid log-in information
		} else {
			loggedIn = true;
			myUser = UID;
			users.put(UID, this);
			sendOK(inputSeq);
		}
	}

	// Process incoming OK command
	public void ok() {
		String fromClient = getInput();

		// If it comes with a PORT or MSG tag, it's a response to a TALKREQ command
		// Call usera's thread so it can send the appropriate TALKRESP command
		if (fromClient.matches("PORT: (.+)") || fromClient.matches("MSG: (.+)")) {
			String input1 = fromClient;
			String input2 = getInput();

			// If the next tag is IP, then the one after that is the sequence number
			if (input2.matches("IP: (.+)")) {
				int inputSeq = getInputSeq(getInput());
			}

			// Get usera's thread to send the TALKRESP command, then clear it, as we don't need it any more
			useraThread.talkResp(input1, input2);
			useraThread = null;
		}
	}

	// Process error command
	public void error() {
		String errMsg = stripTagName(getInput(), "ERRMSG: (.+)");
		int inputSeq = getInputSeq(getInput());
	}

	// Process sendAll command
	public void sendAll() {
		String msg = stripTagName(getInput(), "MSG: (.+)");
		int inputSeq = getInputSeq(getInput());

		// Client hasn't logged in yet; send error
		if (!loggedIn) {
			sendError("User has not authenticated.", inputSeq);
		} else

		// Malformed; send error
		if (msg.equals("") || inputSeq == -1) {
			sendError("", -1);

		// Send publish command to all thread with logged-in clients, and send OK to client
		} else {
			Iterator iterator = users.values().iterator();
			while (iterator.hasNext()) {
				HiRCServerThread thread = (HiRCServerThread) iterator.next();
				thread.publish(myUser, msg);
			}
			sendOK(inputSeq);
		}
	}

	// Process who command
	public void who() {
		int inputSeq = getInputSeq(getInput());

		// Client hasn't logged in yet; send error
		if (!loggedIn) {
			sendError("User has not authenticated.", inputSeq);
		} else

		// Malformed; send error
		if (inputSeq == -1) {
			sendError("", -1);

		// Reply with ULIST command and name of logged-in users
		} else {
			out.println("ULIST");
			Iterator iterator = users.keySet().iterator();
			while (iterator.hasNext()) {
				String UID = (String) iterator.next();
				out.println("UID: " + UID);
			}
			out.println("SEQ: " + inputSeq);
			out.println();
		}
	}

	// Process bye command
	public void bye() {
		int inputSeq = getInputSeq(getInput());

		// Malformed; send error
		if (inputSeq == -1) {
			sendError("", -1);

		// Send OK, remove this thread from threads with logged-in users, and signal for shutdown
		} else {
			sendOK(inputSeq);
			users.remove(myUser);
			done = true;
		}
	}

	// Process talkTo command
	public void talkTo() {
		String UID = stripTagName(getInput(), "UID: (.+)");
		int inputSeq = getInputSeq(getInput());

		// Not logged in; send error
		if (!loggedIn) {
			sendError("User has not authenticated.", inputSeq);
		} else

		// Malformed; send error
		if (UID.equals("") || inputSeq == -1) {
			sendError("", -1);
		} else

		// Recipient is not logged-in; send error
		if (!users.containsKey(UID)) {
			sendError(UID + " is not logged in.", inputSeq);
		} else

		// Recipient is self; send error
		if (UID.equals(myUser)) {
			sendError("No talking to yourself.", inputSeq);

		// Ok, you can send your direct-connect request
		} else {
			HiRCServerThread userbThread = (HiRCServerThread) users.get(UID);

			if (userbThread.useraThread != null) {
				sendError(UID + " is already direct-connected.", inputSeq);
			} else {
				userbThread.talkReq(myUser, this);
				sendOK(inputSeq);
			}
		}
	}
}