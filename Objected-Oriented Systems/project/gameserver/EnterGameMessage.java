package gameserver;

import gameclient.Client;

/** A GameMessage telling a client to load a client-side game module.  Sent by the server when a challenge is accepted. */
public class EnterGameMessage implements GameMessage
{
	private String moduleName;
	
	/**
		@param aGameName the name of the game to enter.
	*/
	public EnterGameMessage(String aGameName)
		{
		moduleName = aGameName;
		}
		
	/** Call's the client's enterGame() method. */
	public void process(Client myClient)
		{
		myClient.enterGame(moduleName + "GUI");
		}
}