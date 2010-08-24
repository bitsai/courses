package gameserver;

import gameclient.*;

/**
	A GameMessage containing a challenge from one player to another.	
*/
public class ChallengeMessage implements GameMessage
{
	private String challengeName;
	private String challenger;
	
	/**
		@param aGameName The name of the game the player is being challenged to.
		@param aPlayer	The name of the *challenging* player.
	*/
	public ChallengeMessage(String aGameName, String aPlayer)
		{
		challengeName = aGameName;
		challenger = aPlayer;
		}
		
	/** Creates a challenge dialog, which gets the receiving player's response and sends it to the server. */
	public void process(Client myClient)
		{
		ChallengeDialog challDialog = new ChallengeDialog(myClient, challengeName, challenger);
		}

}