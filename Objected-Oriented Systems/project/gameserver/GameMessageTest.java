package gameserver;

import junit.framework.*;
import javax.swing.text.*;


/**
	Unit tests for the GameMessage class + its sub-classes.
*/
public class GameMessageTest extends TestCase
{
	private TextMessage textMessage;
	private GameMessage challengeMessage;
	private GameMessage enterGameMessage;
	private GameMessage exitGameMessage;
	private String[] someText;

	
	protected void setUp() 
		{
		someText = new String[2];
		someText[0] = "hello word";
		someText[1] = "in style";
		String someGame = "chess";
		String challenger = "Challenger";
		
		textMessage = new TextMessage(someText);
		challengeMessage = new ChallengeMessage(someGame, challenger);
		enterGameMessage = new EnterGameMessage(someGame);
		exitGameMessage = new ExitGameMessage(challenger, false, false);
		}

	public void testText()
		{
		assertEquals(someText[1], textMessage.getStyleName(0));
		assertEquals(someText[0], textMessage.getTextString(0));
		}
		
	// GameMessage was recently refactored into several polymorphic subclasses which do their work in a perform()
	// method.  Alas, this cannot be automatically tested due to the nature of perform() needed to interact with 
	// an actual Client object that is fully connected to a remote game server and is actually playing a full game module.

	public static Test suite() 
		{
		return new TestSuite(GameMessageTest.class);
		}
		
}
