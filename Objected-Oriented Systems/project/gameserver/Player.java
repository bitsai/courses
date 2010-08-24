package gameserver;

import java.util.*;

/** 
	The Player class is used to represent server-side information on users that are logged on to the game server. 
	This class stores the player's name, what game they are in, and when their client last polled the server.
	A MessageQueue is used to store messages that the player's client has not yet retrieved.
*/
public class Player
{
	private String name;
	private Date lastResponse;
	private MessageQueue unsentMessages;
	private ServerModule curModule;
	boolean abortedGame;
	
	/** @param name The player's name */
	public Player(String myName)
		{
		name = myName;
		unsentMessages = new MessageQueue();
		curModule = null;
		abortedGame = false;
		lastResponse = new Date();
		}
		
	/** Returns the player's name */
	public String getName()
		{
		return name;
		}
		
	/** Returns the player's location */
	public String getLocation()
		{
		try
			{
			if ( curModule != null )
				return curModule.getName();
			else
				return new String("Lobby");
			}

		catch ( java.rmi.RemoteException noProblem )
			{
			// Not an issue since calling locally.
			return new String("Lobby");
			}
		}
		
	/** Returns a reference to the player's current module, or null if the user is in the Lobby. */
	public ServerModule getModule()
		{
		return curModule;
		}
		
	
	/** 
		Sets a reference to the player's current module.
		@param whichMod The ServerModule to put the player in.  Use a value of null to put the user in the lobby.
	*/
	public void setModule(ServerModule whichMod)
		{
		curModule = whichMod;
		setAborted(false);
		}

	
	/** Sets the player's flag indicating that their current game ended because they aborted it. */
	public void setAborted(boolean value)
		{
		abortedGame = value;
		}
	
	
	/** Query for if a player just aborted a game. */
	public boolean didAbort()
		{
		return abortedGame;
		}
	

	/** 
		Retreives (dequeues) all of the player's unread messages.
		@return An array containing the player's messages in sequential order, or null if no messages found.
	*/
	public GameMessage[] getMessages()
		{
		// Refresh the last-polling-time.
		lastResponse = new Date();

		if ( unsentMessages.size() < 1 )
			return null;

		GameMessage[] myMessages = new GameMessage[unsentMessages.size()];
		int nCount = 0;
		
		while ( !unsentMessages.isEmpty() )
			{
			myMessages[nCount++] = unsentMessages.dequeue();
			}
	
		return myMessages;
		}
		
	
	/** 
		Adds a message to a player's message queue.  This message is retrieved by the player's client the next time it
		polls the server for messages, which it does once every few seconds.
		@throws IdlePlayerException If the player's client has not polled for new messages in an unusually long period of time, indicating the connection has likely been lost.
	*/
	public void sendMessage(GameMessage msgToSend) throws IdlePlayerException
		{
		unsentMessages.enqueue(msgToSend);
		
		Date curDate = new Date();
			
		// Calculate the elapsed time between last poll and now, in milliseconds.
		long timeDiff = curDate.getTime() - lastResponse.getTime();
			
		// If last poll was too long ago, throw exception.
		if ( timeDiff > Server.MAX_INACTIVITY )
			{
			throw new IdlePlayerException(this);
			}
		}
}




/** 
	An exception class indicating that an attempt to send a message to a player may have failed, because the player's
	client does not appear to be responding.
*/
class IdlePlayerException extends RuntimeException
{
	private Player myPlayer;
	
	public IdlePlayerException(Player whichPlayer)
		{
		super();
		}
		
	public Player getPlayer()
		{
		return myPlayer;
		}
}