package gameserver;

import java.rmi.*;


/** 
	An RMI-based interface for a Game Server.  Implemented by Server on the server-side, or by a stub class
	on the client side.
*/
public interface RemoteGameServer extends Remote
{
	/** Returned by logIn() if method is successful, instead of returning an error message. */
	public static final String SUCCESS = "success";
	
	/**	Called remotely by a client to attempt to log in to the game server.*/
	public String logIn(String userName, String password) throws RemoteException;

	/** Called remotely by a client when the client attempts to log off, or when the client window is closed.*/
	public void logOff(String userName) throws RemoteException;

	/** Called remotely by a client to create a new player in the player database. */
	public boolean createPlayer(String userName, String password) throws RemoteException, java.io.IOException;

	/** Called remotely by clients to retreive new messages. */
	public GameMessage[] pollForMessages(String userName) throws RemoteException;

	/** Called remotely by a client when "abort game" button is pressed. */
	public void abortGame(String playerName) throws RemoteException;

	/** Called remotely by clients to send a global message to all players.*/
	public void sendGlobalMessage(String fromUser, String message) throws RemoteException;

	/** Called remotely by clients to send an instant message (and/or a challenge) from one user to another.*/
	public void sendInstantMessage(String fromUser, String toUser, String message, String gameChallenge) throws RemoteException;

	/** Called remotely by a client to answer a challenge. */
	public void respondToChallenge(String respondingUser, String challengingUser, String gameName, boolean response) throws RemoteException;

	/** Called remotely by clients to retrieve a list of names of games available on the server. */
	public String[] getGameNames() throws RemoteException;
	
	/** Called remotely by client to retrieve a reference to the player's server module */
	public ServerModule getPlayerServerModule(String playerName) throws RemoteException;
	
	/** Called remotely by client to get base directory of game's web server.  Used by client's lobby. */
	public String getWebAddress() throws RemoteException;
}