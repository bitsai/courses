package gameserver;

import junit.framework.*;


/**
	Unit tests for Player class.
*/
public class PlayerTest extends TestCase
{
	Player myPlayer;
	GameMessage dummyMessage;
	
	protected void setUp() 
		{
		myPlayer = new Player("Bob");
		dummyMessage = new TextMessage(null);
		}	

	public void testGetName()
		{
		assertEquals(myPlayer.getName(), "Bob");
		}
		
	public void testGetLocation()
		{
		assertEquals(myPlayer.getLocation(), "Lobby");
		}
		
	/** Tests both sendMessage() and getMessages(). */
	public void testSendMessage() throws IdlePlayerException
		{
		myPlayer.sendMessage(dummyMessage);
		
		GameMessage[] myMessages = myPlayer.getMessages();
		
		assertEquals(myMessages[0], dummyMessage);
		}


	public static Test suite() 
		{
		return new TestSuite(PlayerTest.class);
		}
}
