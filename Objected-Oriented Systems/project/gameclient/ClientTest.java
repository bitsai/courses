package gameclient;

import java.rmi.*;
import java.rmi.server.*;
import junit.framework.*;
import gameserver.*;


/**
	Client-side unit tests.  The tests attempt to invoke remote methods on the server, mimicing the activities of an
	actual game client.  Some of these tests are somewhat indirect, in order to bypass the Client's GUI.  Nonetheless,
	all activities of classes like LogOnDialog and NewUserDialog are mimiced here.
	<BR><BR>
	NOTE:  If the remote game server is not running at rmi://localhost, none of these tests will pass, since testing the
	connection is done in all test cases!
*/
public class ClientTest extends TestCase
{
	private Client myClient;
	private RemoteGameServer myServer;
	
	protected void setUp() 
		{
		try
			{
			myServer = (RemoteGameServer)Naming.lookup("rmi://localhost/gameserver");
			myClient = new Client();
			myClient.setVisible(false);		
			}
			
		catch ( Exception e )
			{
			}
		}
	
	
	public void testCreatePlayer() throws java.rmi.RemoteException, java.io.IOException
		{
		assertNotNull("Unable to connect to server at rmi://localhost/gameserver", myServer);

		String user = "fakeuser"+Math.random();
		
		boolean success = myServer.createPlayer(user, "pass" );
		assertTrue("Attempt to create new user with random name failed", success);
		
		success = myServer.createPlayer(user, "pass" );
		assertFalse("Server allowed creation of duplicate user name", success);
		}
		
		
	public void testLogIn() throws java.rmi.RemoteException, java.io.IOException
		{
		assertNotNull("Unable to connect to server at rmi://localhost/gameserver", myServer);
		
		String result = myServer.logIn("fakeuser", "nopw");
		assertFalse("Log-in was successful with invalid user name", result.equals(RemoteGameServer.SUCCESS) );
		
		String user = "fakeuser"+Math.random();
		boolean success = myServer.createPlayer(user, "pass" );
		assertTrue("Attempt to create new user with random name failed", success);
		myServer.logOff(user);
		
		result = myServer.logIn(user, "nopw");				
		assertFalse("Log-in was successful with incorrect password", result.equals(RemoteGameServer.SUCCESS) );
		
		result = myServer.logIn(user, "pass");				
		assertTrue("Log-in failed despite correct password", result.equals(RemoteGameServer.SUCCESS) );

		result = myServer.logIn(user, "pass");				
		assertFalse("Log-in was successful despite user already being on-line", result.equals(RemoteGameServer.SUCCESS) );
		}
		
		
	public void testLogOff() throws java.rmi.RemoteException, java.io.IOException
		{
		assertNotNull("Unable to connect to server at rmi://localhost/gameserver", myServer);
		
		String user = "fakeuser"+Math.random();
		boolean success = myServer.createPlayer(user, "pass" );
		assertTrue("Attempt to create new user with random name failed", success);
		
		myServer.logOff(user);
		
		assertTrue("Log-off did not log player off", myServer.logIn(user, "pass").equals(RemoteGameServer.SUCCESS));		
		}
		

	public void testPollForMessages() throws java.lang.InterruptedException, java.rmi.RemoteException, java.io.IOException
		{
		assertNotNull("Unable to connect to server at rmi://localhost/gameserver", myServer);
		
		String user = "fakeuser"+Math.random();
		boolean success = myServer.createPlayer(user, "pass" );
		assertTrue("Attempt to create new user with random name failed", success);

		// Send a global message.
		myServer.sendGlobalMessage(user, "This is a global message.");

		// Pause briefly to allow server to process.
		Thread.sleep(1000);

		// Poll the server for messages and ensure there's a text message in the message queue.
		GameMessage[] myMessages = myServer.pollForMessages(user);
		assertNotNull("Polling for messages unsuccessful.", myMessages);
		boolean found = false;
		for ( int n = 0; n < myMessages.length; n++ )
			{
			if ( myMessages[n] instanceof TextMessage )
				found = true;
			}
			
		assertTrue("Global message send not successful.", found);
		}
		
	
	public static Test suite() 
		{
		return new TestSuite(ClientTest.class);
		}
	
}