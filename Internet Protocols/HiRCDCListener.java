import java.io.*;
import java.net.*;

// This listener has only one job; listen for incoming direct-connect requests and handle them

public class HiRCDCListener extends Thread {
	HiRCClient client = null;
	ServerSocket serverSocket = null;
	boolean done = false;

	public HiRCDCListener(HiRCClient client, int direct_port_number) {
		try {
			this.client = client;
			serverSocket = new ServerSocket(direct_port_number);
		} catch (Exception e) {
			System.err.println(e);
		}
	}

	public void run() {
		try {

			// Listen for incoming direct-connect requests, and create a new direct-connect thread to handle them
			// Do this only once, since we're limiting each client to only one direct-connection
			// Once a new direct-thread is created, hand it off to the client, and shut down

			while (!done) {
				HiRCDCThread DCThread = new HiRCDCThread(client, serverSocket.accept());
				client.setDCThread(DCThread);
				DCThread.start();
				done = true;
			}
			serverSocket.close();
		} catch (Exception e) {
			System.err.println(e);
		}
	}
}