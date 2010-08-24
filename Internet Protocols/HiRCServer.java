import java.io.*;
import java.net.*;
import java.util.*;

public class HiRCServer {
	public static void main(String[] args) {
		ServerSocket serverSocket = null;
		boolean listening = true;
		HashMap users = new HashMap(); // user name -> thread
		HashMap passwords = new HashMap(); // user name -> password
		HashMap threads = new HashMap(); // thread name -> thread

		if (args.length != 2) {
			System.out.println("usage error, run as \"java HiRCServer <port> <password file>\"");
			System.exit(0);
		}

		try {
			// Display IP and hostname for ease of connection

			InetAddress localaddr = InetAddress.getLocalHost();
			System.out.println ("Local IP Address : " + localaddr.getHostAddress());
			System.out.println ("Local hostname : " + localaddr.getHostName());

			int port_number = (new Integer(args[0])).intValue();
			String passwordsFile = args[1];

			BufferedReader in = new BufferedReader(new FileReader(passwordsFile));
			String line;

			// Read user-password pairs into memory

			while ((line = in.readLine()) != null) {
				StringTokenizer ST = new StringTokenizer(line, ":");
				String username = ST.nextToken();
				String password = ST.nextToken();
				passwords.put(username, password);
			}

			in.close();
			System.out.println("There are " + passwords.size() + " users in database.");
			serverSocket = new ServerSocket(port_number);

			// Listen for incoming connections, and create new server threads to handle accepted connections

			while (listening) {
				HiRCServerThread thread = new HiRCServerThread(serverSocket.accept(), "Thread-" + threads.size(), users, passwords);
				threads.put("Thread-" + threads.size(), thread);
				thread.start();
				System.out.println("New HiRCServerThread created: Thread-" + (threads.size() - 1));
			}

			serverSocket.close();
		} catch (Exception e) {
			System.err.println(e);
			System.exit(-1);
		}
	}
}