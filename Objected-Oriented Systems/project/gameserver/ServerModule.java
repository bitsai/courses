package gameserver;

import java.rmi.*;

/** 
	Interface for an RMI-based game server module.  Each server module object handles a 2-player game.  Every game 
	should have ONE class that implements ServerModule and additionally extends UnicastRemoteObject.  The name of this
	class should match the game name; ie, a scrabble game ServerModule should be called Scrabble.class.
*/
public interface ServerModule 
{
	/** 
		The Server calls this method to tell the ServerModule to begin gameplay. 
		@param aServer A reference to the Server object; needed so the ServerModule knows what to call gameOver() on.
		@param player1 The name of the first player.
		@param player2 The name of the second player.
		@return A string indicating the name of the player who won the game.
	*/
	void playGame(Server myServer, String player1, String player2) throws RemoteException;
	
	
	/**
		Tells the game that a player's connection has been lost, or that a player hit their "abort game" button.  The
		game module should handle this appropriately, by calling Server.gameOver().
		@param playerName the name of the player who disconnected/aborted.
	*/
	void playerDisconnection(String playerName) throws RemoteException;

	
	/**
		Returns the module's name.
	*/
	String getName() throws RemoteException;
}