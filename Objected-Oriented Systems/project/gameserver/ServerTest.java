package gameserver;

import java.rmi.*;
import java.rmi.server.*;
import junit.framework.*;


/**
	Server-side unit tests.  These test the server's local functionality.  Please note that the RMI-based Server 
	methods are tested in ClientTest instead of here, so that they are actually called and tested remotely.
*/
public class ServerTest extends TestCase
{
	private Server myServer;
	
	protected void setUp() 
		{
		try
			{
			myServer = new Server(null);
			}
		catch (Exception e)
			{
			}
		}
	
	
	public void testGetPassword() throws java.rmi.RemoteException, java.io.IOException
		{
		// Test removed since server's password-fetcher is now private.  To test password-fetching, use the
		// new test class, PlayerListTest.
		}
	
	
	public void testGetPlayer() throws java.rmi.RemoteException, java.io.IOException
		{
		// Log a fake player in on-line, and then make sure getPlayer() works
		
		// Create a new account and check it's password.
		String user = "fakeuser"+Math.random();
		boolean success = myServer.createPlayer(user, "pass" );
		assertTrue("Attempt to create new user with random name failed", success);
		assertNotNull("getPlayer() failed to locate a player object!", myServer.getPlayer(user) );		
		}
		
		
	public void testAddGame() throws java.rmi.RemoteException
		{
		// Add a game to the server, and then make sure it's in the game list.
		
		boolean worked = false;
		
		try
			{
			myServer.addGame("chess");
			worked = true;
			}
		catch ( ClassNotFoundException good )
			{
			}
		assertFalse("Was able to add a non-existant class to game list!", worked);
		
		worked = false;
		
		try
			{
			myServer.addGame("ServerGUI");
			worked = true;
			}
		catch ( ClassNotFoundException bad )
			{
			}
			
		assertTrue("Was unable to add a valid class!", worked);
			
			
		
		
		String[] gameNames = myServer.getGameNames();
		boolean isListed = false;
		for ( int n = 0; n < gameNames.length; n++ )
			{
			if ( gameNames[n].equals("ServerGUI") )
				{
				assertFalse("Game appears on game list twice!", isListed);
				isListed = true;
				}
			}
		
		assertTrue("Despite having been added, game does not appear in game list!", isListed);
		}
	
	
	public static Test suite() 
		{
		return new TestSuite(ServerTest.class);
		}
	
}