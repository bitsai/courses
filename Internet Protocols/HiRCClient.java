import java.io.*;
import java.net.*;
import java.util.*;
import java.util.regex.*;

public class HiRCClient extends Thread {
	HiRCClientUserInterface user_interface = null;
	Socket clientSocket = null;
	PrintWriter out = null;
	BufferedReader in = null;
	String myUser = null; // user that i'm logged-in as
	int seq = 0;
	boolean done = false;

	int connect = -1;
	int bye = -1;

	String otherGuy = null; // user that i'm direct-connected to
	String lastOtherGuy = null; // user that i last tried to direct-connect to
	String lastLogin = null; // the last user name i tried to log-in as
	InetAddress localaddr = null;
	HiRCDCThread DCThread = null; // this client's direct-connect thread

	int direct_port_number = -1;
	int incomingDirectConnect = -1; // sequence number of my last outgoing direct-connect request
	int outgoingDirectConnect = -1; // sequence number of my last incoming direct-connect request

// Initialization

	public HiRCClient(String hostname, int port_number, int direct_port_number) {
		user_interface = new HiRCClientUserInterface(this);
		this.direct_port_number = direct_port_number;

		try {
			localaddr = InetAddress.getLocalHost();
			clientSocket = new Socket(hostname, port_number);
			out = new PrintWriter(clientSocket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		} catch (Exception e) {
			System.err.println(e);
			System.exit(1);
		}

		(new Thread(this)).start();
	}

// Utility methods

	// Read input from socket
	public String getInput() {
		String input = null;

		try {
			input = in.readLine();
			if (input.length() > 0) { System.out.println("Received string from server: " + input); }
		} catch (Exception e) {
			System.err.println(e);
			System.exit(1);
		}

		return input;
	}

	// Get sequence number of command, return -1 if none is found
	public int getInputSeq(String input) {
		int inputSeq = -1;
		String tagValue = stripTagName(input, "SEQ: (.+)");
		if (!tagValue.equals("")) {
			inputSeq = new Integer(tagValue).intValue();
		}
		return inputSeq;
	}

	// Return new sequence number, increment sequence number counter
	public int getSeq() {
		seq++;
		return seq - 1;
	}

	// Given a tag, strip off the tag name according to the pattern, and return the tag value
	public String stripTagName(String input, String pattern) {
		String output = "";
		Pattern p = Pattern.compile(pattern);
		Matcher m;
		if ((m = p.matcher(input)).matches()) {
			output = m.group(1);
		}
		return output;
	}

	// Process stuff the user is typing
	// I should probably refactor this into smaller methods, like i did everywhere else
	// But that's a lot of work
	public void processClientMessage(String message) {
		user_interface.displayText(message);
		StringTokenizer ST = new StringTokenizer(message);
		String command = "";

		// Might be the case the user just whacked the keyboard, with no command at all
		if (ST.hasMoreTokens()) {
			command = ST.nextToken();
		}

		// Login command must have 2 arguments, UID and password
		if (command.equals("/login") && ST.countTokens() == 2) {
			connect = getSeq();
			String UID = ST.nextToken();
			String passwd = ST.nextToken();
			lastLogin = UID;
			out.println("CONNECT");
			out.println("UID: " + UID);
			out.println("PASSWD: " + passwd);
			out.println("SEQ: " + connect);
			out.println();
		}

		// Who command must have no arguments
		else if (command.equals("/who") && ST.countTokens() == 0) {
			out.println("WHO");
			out.println("SEQ: " + getSeq());
			out.println();
		}

		// Logout command must have no arguments
		else if (command.equals("/logout") && ST.countTokens() == 0) {
			bye = getSeq();
			out.println("BYE");
			out.println("SEQ: " + bye);
			out.println();
		}

		// Knock command must have 1 argument, the recipient name
		else if (command.equals("/knock") && ST.countTokens() == 1) {

			// Haven't logged in yet, can't knock
			if (myUser == null) {
				user_interface.displayText("User has not authenticated.");
			} else

			// Already direct-connected to someone, can't knock
			if (otherGuy != null) {
				user_interface.displayText("One direct-connection only.");
			} else

			// Knocked already, can't knock again until the first knock is responded to
			if (outgoingDirectConnect != -1) {
				user_interface.displayText("One pending knock only.");

			// Knock
			} else {
				outgoingDirectConnect = getSeq(); // Store sequence number of knock request
				String UID = ST.nextToken();
				lastOtherGuy = UID; // Store name of user we just knocked
				out.println("TALKTO");
				out.println("UID: " + UID);
				out.println("SEQ: " + outgoingDirectConnect);
				out.println();
			}
		}

		// Accept command must have no arguments
		else if (command.equals("/accept") && ST.countTokens() == 0) {

			// There's no direct-connect request to be accepted
			if (incomingDirectConnect == -1)
			{
				user_interface.displayText("No direct-connect request.");

			// Accept
			} else {
				setupDCListener(); // Setup direct-connect listener doohickey
				out.println("OK");
				out.println("PORT: " + direct_port_number);
				out.println("IP: " + localaddr.getHostAddress());
				out.println("SEQ: " + incomingDirectConnect);
				out.println();
			}
		}

		// Deny command can have message of arbitrary length attached
		else if (command.equals("/deny")) {

			// There's no direct-connect request to be denied
			if (incomingDirectConnect == -1)
			{
				user_interface.displayText("No direct-connect request.");

			// Deny
			} else {
				String msg = myUser + " doesn't want to talk to you, and says:";
				if (ST.hasMoreTokens()) {
					while (ST.hasMoreTokens()) {
						msg += " " + ST.nextToken();
					}
				} else {
					msg = myUser + " doesn't want to talk to you.";
				}
				int temp = incomingDirectConnect;
				incomingDirectConnect = -1; // Clear incoming direct-connect sequence number tracker variable
				out.println("OK");
				out.println("MSG: " + msg);
				out.println("SEQ: " + temp);
				out.println();
			}
		}

		// Msg command can have message of arbitrary length
		else if (command.equals("/msg")) {

			// No one to send private message to
			if (otherGuy == null)
			{
				user_interface.displayText("No direct-connection.");

			// Msg
			} else {
				String msg = "";
				while (ST.hasMoreTokens()) {
					msg += ST.nextToken() + " ";
				}
				DCThread.sendMsg(msg); // Ask direct-connect thread to send message for us
			}
		}

		// Endprivate command must have no arguments
		else if (command.equals("/endprivate") && ST.countTokens() == 0) {

			// No direct-connection to end
			if (otherGuy == null)
			{
				user_interface.displayText("No direct-connection.");

			// Endprivate
			} else {
				DCThread.sendBye();
			}
		}

		// As you can see, we send everything that remains; so users will broadcast mis-typed commands
		// And look like fools
		// I thought this would be fun
		else {
			out.println("SENDALL");
			out.println("MSG: " + message);
			out.println("SEQ: " + getSeq());
			out.println();
		}
	}

	// Start direct-connection listener thread
	public void setupDCListener() {
		HiRCDCListener listener = new HiRCDCListener(this, direct_port_number);
		listener.start();
	}

	// Set my direct-connection thread to the one just passed in; only called by the direct-connection listener
	public void setDCThread(HiRCDCThread DCThread) {
		this.DCThread = DCThread;
	}

	// Set up a new direct-connection thread
	public void setupDCThread(String port, String IP) {
		try {
			int socketPort = Integer.parseInt(port);
			InetAddress socketIP = convertIP(IP);
			Socket socket = new Socket(socketIP, socketPort);
			DCThread = new HiRCDCThread(this, socket);
			DCThread.start();
			DCThread.sendConnect(); // Send initial connect command
		} catch (Exception e) {
			System.err.println(e);
		}
	}

	// We can only pass around IP address as a string between clients
	// Sockets need IP addresses as an InetAddress object
	// This little method does the necessary conversion
	public InetAddress convertIP(String IP) {
		try {
			StringTokenizer ST = new StringTokenizer(IP, ".");
			byte[] temp = new byte[4];
			for (int index = 0; index < 4; index++) {
				Integer dummy = new Integer(ST.nextToken());
				temp[index] = dummy.byteValue();
			}
			return(InetAddress.getByAddress(temp));
		} catch (Exception e) {
			System.err.println(e);
		}
		return null;
	}

// Outgoing command methods

	// Send error command
	public void sendError(String errMsg, int seqNum) {
		out.println("ERROR");
		out.println("ERRMSG: " + errMsg);
		out.println("SEQ: " + seqNum);
		out.println();
	}

	// Send OK command
	public void sendOK(int seqNum) {
		out.println("OK");
		out.println("SEQ: " + seqNum);
		out.println();
	}

// Thread run method

	public void run(){
		try {
			String fromServer = "";

			// Listen for incoming commands, and call appropriate method

			while (!done && (fromServer = getInput()) != null) {
				if (fromServer.equals("OK")) { ok(); } else
				if (fromServer.equals("ERROR")) { error(); } else
				if (fromServer.equals("PUBLISH")) { publish(); } else
				if (fromServer.equals("ULIST")) { ulist(); } else
				if (fromServer.equals("TALKREQ")) { talkReq(); } else
				if (fromServer.equals("TALKRESP")) { talkResp(); } else
				if (fromServer.length() > 0) { sendError("", -1); }
			}

			out.close();
			in.close();
			clientSocket.close();
			System.exit(0);
		} catch (Exception e) {
			System.err.println(e);
			System.exit(1);
		}
	}

// Incoming command methods

	// Process OK command
	public void ok() {
		int inputSeq = getInputSeq(getInput());

		// If sequence number matches our last outgoing connect command, we've logged-in
		if (inputSeq == connect) {
			myUser = lastLogin; // Set user name to last user we tried to log-in as
			user_interface.displayText("Login successful.");
		} else

		// If sequence number matches our last outgoing bye command, time to die
		if (inputSeq == bye) { done = true; }
	}

	// Process error command
	public void error() {
		String errMsg = stripTagName(getInput(), "ERRMSG: (.+)");
		int inputSeq = getInputSeq(getInput());

		// If there's a message attached, display it
		if (!errMsg.equals("")) {
			user_interface.displayText(errMsg);
		}

		// If sequence number matches our last outgoing direct-connect request, start over
		if (inputSeq == outgoingDirectConnect) {
			outgoingDirectConnect = -1;
		}
	}

	// Process publish command
	public void publish() {
		String msg = stripTagName(getInput(), "MSG: (.+)");
		int inputSeq = getInputSeq(getInput());

		// Malformed; send error
		if (msg.equals("") || inputSeq == -1) {
			sendError("", -1);

		// Display text, send OK
		} else {
			user_interface.displayText(msg);
			sendOK(inputSeq);
		}
	}

	// Process ulist command
	public void ulist() {
		String users = "users:";
		int inputSeq = -1;
		String fromServer = "";

		// We don't know how many tags there are, so have to keep processing them
		// Until we hit a SEQ tag or run out of tags
		while ((fromServer = getInput()) != null) {

			// An UID tag, eh?
			if (fromServer.matches("UID: (.+)")) {
				users += " " + stripTagName(fromServer, "UID: (.+)") + ",";
			} else

			// A SEQ tag, eh?
			if (fromServer.matches("SEQ: (.+)")) {
				inputSeq = getInputSeq(fromServer);
				break;
			}
		}

		// Malformed; send error
		if (users.equals("users:") || inputSeq == -1) {
			sendError("", -1);

		// Display all logged-in users
		} else {
			user_interface.displayText(users.substring(0, users.length()-1));
		}
	}

	// Process talkReq command
	public void talkReq() {
		String usera = stripTagName(getInput(), "UID: (.+)");
		int inputSeq = getInputSeq(getInput());

		// Malformed; send error
		if (usera.equals("") || inputSeq == -1) {
			sendError("", -1);
		} else

		// This client is already involved in a direct-connection; send negative OK response
		if (incomingDirectConnect != -1 || outgoingDirectConnect != -1) {
			out.println("OK");
			out.println("MSG: " + myUser + " is already direct-connected.");
			out.println("SEQ: " + inputSeq);
			out.println();

		// All is well
		} else {
			incomingDirectConnect = inputSeq; // Record sequence number of this direct-connect request
			user_interface.displayText(usera + " wishes to direct-connect.");
		}
	}

	// Process talkResp command
	public void talkResp() {
		String fromServer = getInput();

		// A PORT tag!  A positive response
		if (fromServer.matches("PORT: (.+)")) {
			String port = stripTagName(fromServer, "PORT: (.+)");
			String IP = stripTagName(getInput(), "IP: (.+)");
			int inputSeq = getInputSeq(getInput());

			// Malformed; send error
			if (port.equals("") || IP.equals("") || inputSeq == -1) {
				sendError("", -1);

			// Our direct-connect request has been accepted
			} else {
				otherGuy = lastOtherGuy;
				setupDCThread(port, IP);
				sendOK(inputSeq);
			}
		} else

		// A MSG tag!  A negative response
		if (fromServer.matches("MSG: (.+)")) {
			String msg = stripTagName(fromServer, "MSG: (.+)");
			int inputSeq = getInputSeq(getInput());

			// Malformed; send error
			if (msg.equals("") || inputSeq == -1) {
				sendError("", -1);

			// Our direct-connect request has been denied
			} else {
				outgoingDirectConnect = -1;
				user_interface.displayText(msg);
				sendOK(inputSeq);
			}
		}
	}

// Main method

	public static void main(String[] args) {
		if (args.length != 3) {
			System.out.println("usage error, run as \"java HiRCClient <host> <port> <direct-connect port>\"");
			System.exit(0);
		}

		String hostname = args[0];
		int port_number = (new Integer(args[1])).intValue();
		int direct_port_number = (new Integer(args[2])).intValue();
		HiRCClient client = new HiRCClient(hostname, port_number, direct_port_number);
	}
}