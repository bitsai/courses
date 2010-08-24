package gameserver;


import java.io.*;
import java.net.URL;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.rmi.*;
import java.rmi.server.*;



/** 
	Implementation of RemoteGameServer for an RMI-Based Expandable Game Server.  This class contains all of the server's
	RMI-enabled methods, controlling vital client/server functions (log on, log off, instant messages, player challenges,
	etc).  Server has a main() method, which can be executed to run the console-mode game server.<BR><BR>
	
	To execute, first be sure that all necessary files have been RMIC'd, and make sure the RMIREGISTRY is running. 
	Then call "java gameserver/Server" to run.  A port number may be specified on the command line if the RMIREGISTRY
	is running on a non-standard port.<BR><BR>
	
	For a game server with a GUI for admin functions, use the ServerGUI class instead.
*/
public class Server extends UnicastRemoteObject implements RemoteGameServer
{
	/** If a client does not poll the server in this amount of time (in milli-seconds), the client is considered disconnected.*/
	public final static long MAX_INACTIVITY = 60000;

	private PlayerList playerList;
	private ModuleList gameList;
	private String webURL;

	public Server(String regAddy) throws RemoteException
	 	{
		playerList = new PlayerList();
		gameList = new ModuleList();
		webURL = new String("http://ugrad.cs.jhu.edu/~group3/");
		
		String regLocation = new String("gameserver");
		if ( regAddy != null ) 
			regLocation = "//localhost:" + regAddy + "/" + regLocation;
		
		try
			{
			Naming.rebind(regLocation, this);
			}
		catch ( Exception e )
			{
			System.out.println("error with rmi registry.");
			}
  		}


	/** Runs the console-mode interface for the server. */
	public void runConsoleInterface()
		{
		BufferedReader stdin = new java.io.BufferedReader(new InputStreamReader(System.in));
		String inpLine;
		
		while ( true )
			{
			System.out.println("\r\n\r\nConsole-mode Game Server running.  Options:");
			System.out.println("  A>  Add new game to server");
			System.out.println("  R>  Remove game from server");
			System.out.println("  S>  Set lobby directory URL");
			System.out.println("  X>  Shut down server\r\n");
			System.out.print("Your choice?  ");		
			
			try
				{
				if ( (inpLine = stdin.readLine()) != null && inpLine.length() > 0 )
					{
					inpLine = inpLine.toLowerCase().trim();
							
					if ( inpLine.charAt(0) == 'a' )
						{
						System.out.println("Please specify ServerModule implementation class, WITH full package, but \nWITHOUT the .class extension.\n");
						System.out.print("Class name?  ");
						String gameName = stdin.readLine();			
						try
							{
							addGame(gameName);
							}
						catch ( ClassNotFoundException badClass )
							{
							System.out.println("Unable to locate " + gameName + ".class!");
							}
						}
						
					if ( inpLine.charAt(0) == 'x' )
						shutDown();
						
					if ( inpLine.charAt(0) == 'r' )
						{
						System.out.print("Remove what game?   ");
						String gameName = stdin.readLine();			
						removeGame(gameName);
						}
						
					if ( inpLine.charAt(0) == 's' )
						{
						System.out.println("Lobby directory URL currently is  " + webURL);
						System.out.print("Change to what URL?   ");
						String newURL = stdin.readLine();			
						setLobbyAddress(newURL);
						System.out.println("Note:  Users currently on-line will not be affected until after they log off.");
						}
					}
					
				}
			
			catch (IOException ioe)
				{
				System.exit(0);
				}
			}
		
		}

	/** Adds a game to the server's installed games list. */	
	public void addGame(String gameName) throws ClassNotFoundException
		{
		if ( gameName != null )
			gameList.addGame(gameName);		
		}

	/** Removes a game from the server. */
	public void removeGame(String gameName) 
		{
		if ( gameName != null )
			gameList.removeGame(gameName);		
		}
		
	/** Shuts down the server. */
	public void shutDown()
		{
		gameList.saveModuleList();
		System.exit(0);
		}
		
	/** Sets the lobby URL. */
	public void setLobbyAddress(String newURL)
		{
		if ( newURL != null && newURL.length() > 7 )
			webURL = newURL;
		}


	/** Converts a player name string into a player object. */
	public Player getPlayer(String playerName)
		{
		return playerList.lookup(playerName);
		}


	/** Returns the password for a given player. Returns null if that player does not currently exist. */
	private String getPassword(String name)
		{
		return playerList.lookupPassword(name);
		}
		
	
	/** Call this method in response to an IdlePlayerException to appropriately handle it. */
	public void logOffIdlePlayer(Player whichPlayer)
		{
		whichPlayer.setAborted(true);
		
		// If the player is in a module, send a player-disconnect notification to that module.
		ServerModule myModule = whichPlayer.getModule();
		try
			{
			if ( myModule != null )
				myModule.playerDisconnection(whichPlayer.getName());
			}
		catch ( RemoteException ex )
			{
			// Never occurs since playerDisconnection() called locally
			}

		// Remove the player from the on-line player list.  (This must be done BEFORE sending the global message, to 
		// avoid a potential infinite loop of throwing IdlePlayerExceptions!)
		playerList.remove(whichPlayer);

		// Create and send a log-off notification message to all players.
		String[] fullMessage = new String[2];
		fullMessage[0] = new String(whichPlayer.getName() + "'s connection to the game server has been lost.\n");
		fullMessage[1] = new String("log off");
		GameMessage globalMessage = new TextMessage(fullMessage);		
		sendToAll(globalMessage);		
		sendUpdatedUserList();
		}
	
	
	
	/** Sends a message to all on-line players. Used in global messages, log-on messages, log-off messages, and
		winner notification messages. */
	public void sendToAll(GameMessage globalMessage)
		{
		Player[] myPlayers = playerList.getAll();
		
		for ( int n = 0; n < myPlayers.length; n++ )
			{
			send(myPlayers[n], globalMessage);
			}
		}
		
		
	/** Sends a message to one user. Catches the potential IdlePlayerException and handles properly. */
	public void send(Player toPlayer, GameMessage message)
		{
		if ( toPlayer == null )
			return;
		
		try
			{
			toPlayer.sendMessage(message);
			}
		catch ( IdlePlayerException idle )
			{
			logOffIdlePlayer(idle.getPlayer());
			}
		}
		
		
	/** Sends an updated user list to all players.  Call this method whenever a player logs on, logs off, enters a game,
		exits a game, or gets disconnected due to inactivity. */
	public void sendUpdatedUserList()
		{
		GameMessage userMessage = new UpdateUserListMessage( getPlayerNames() );
		sendToAll(userMessage);
		}


	/**
		Builds an up-to-date list of names of players who are currently on-line, along with their locations.  
		Strings alternate in this fashion; for example, the array might contain data such as "Bobby", "Scrabble", 
		"JoeJoe", "Lobby", etc.
	*/
	public String[] getPlayerNames()
		{
		int counter = 0, cur = 0;
		Player[] myPlayers = playerList.getAll();
		String[] result = new String[myPlayers.length * 2];
		
		for ( int n = 0; n < myPlayers.length; n++ )
			{
			result[cur++] = new String( myPlayers[n].getName() );
			result[cur++] = new String( myPlayers[n].getLocation() );
			}
			
		return result;
		}


	/**
		Called by a ServerModule to signify a game has ended.
		@param winner The name of the winning player, or another non-player-name string to designate a tie/no winner.
		@param player1 The name of a player in the game.
		@param player2 The name of the other player in the game.
	*/
	public synchronized void gameOver(String winner, String player1, String player2)
		{
		String gameName = null;
		String loser = null;
		Player first, second;
		boolean wasTie = false;
		boolean wasAborted = false;
		
		first = getPlayer(player1);
		second = getPlayer(player2);
		
		// If both players already disconnected, get out of here
		if ( first == null && second == null )
			return;

		if ( first != null ) 
			gameName = first.getLocation();
		else
			gameName = second.getLocation();
		
		// Figure out who's the loser.
		if ( winner.equalsIgnoreCase(player1) )
			loser = player2;
		if ( winner.equalsIgnoreCase(player2) )
			loser = player1;
			
		if ( first != null && first.didAbort() )
			wasAborted = true;
		if ( second != null && second.didAbort() )
			wasAborted = true;
			
		if ( loser == null && !wasAborted )
			wasTie = true;
				
		// Display a global message telling who won the game, unless the game was a tie.
		if ( !wasTie && gameName != null )
			{
			String[] fullMessage = new String[10];
			fullMessage[0] = winner;
			fullMessage[1] = TextMessage.WINNER_BOLD;
	
			fullMessage[2] = new String(" has defeated ");
			fullMessage[3] = TextMessage.WINNER;
		
			fullMessage[4] = loser;
			fullMessage[5] = TextMessage.WINNER_BOLD;
	
			fullMessage[6] = new String(" in a game of ");
			fullMessage[7] = TextMessage.WINNER;
		
			fullMessage[8] = gameName + "!\n";
			fullMessage[9] = TextMessage.WINNER;
	
			GameMessage globalMessage = new TextMessage(fullMessage);		
			sendToAll(globalMessage);		
			}
		
		if ( first != null )
			first.setModule(null);
		
		if ( second != null )
			second.setModule(null);
		
		sendUpdatedUserList();
			
		// Send messages to the clients to tell them to exit their module.
		GameMessage exitGameMessage = new ExitGameMessage(winner, wasTie, wasAborted);
		send(first, exitGameMessage);
		send(second, exitGameMessage);
		}
	
	/** Called remotely by a client when the "abort game" button is pressed. The server handles this by attempting
		to terminate the game, by calling the game's playerDisconnection() method.
	*/
	public synchronized void abortGame(String playerName) throws RemoteException
		{
		Player whichPlayer = getPlayer(playerName);
		if ( whichPlayer == null )
			return;
		
		whichPlayer.setAborted(true);
		
		ServerModule myModule = whichPlayer.getModule();
		try
			{
			if ( myModule != null )
				myModule.playerDisconnection(whichPlayer.getName());
			}
		catch ( RemoteException ex )
			{
			// Never occurs since playerDisconnection() is called locally.
			}
		}


	/** 
		Called remotely by a client to attempt to log in to the game server.
		@param userName The user name
		@param password The user's password
		@return RemoteGameServer.SUCCESS if the user logged in successfully, or an error message if otherwise
	*/
	public synchronized String logIn(String userName, String password) throws RemoteException
		{
		String correctPW = getPassword(userName);
		
		// If getPassword() returned null, it means the username is invalid, so report error.
		if ( correctPW == null )
			return "Invalid user name!";
			
		// Ensure password is valid.
		if ( !correctPW.equals(password) )
			return "Incorrect password!";
			
		// Ensure that user isn't already on-line.
		if ( getPlayer(userName) != null )
			return "You are already logged in!";
		
		playerList.add(userName);
		String[] fullMessage = new String[2];
		fullMessage[0] = userName + " has logged on to the game server.\n";
		fullMessage[1] = TextMessage.LOG_ON;
		GameMessage globalMessage = new TextMessage(fullMessage);		
		sendToAll(globalMessage);		
		sendUpdatedUserList();
		return SUCCESS;
		}



	/**
		Called remotely by a client when the client attempts to log off, or when the client window is closed.
	*/
	public synchronized void logOff(String userName) throws RemoteException
		{
		Player whichPlayer = getPlayer(userName);
		if ( whichPlayer == null )
			return;
		
		whichPlayer.setAborted(true);

		// If the player is in a module, send a player-disconnect notification to that module.
		ServerModule myModule = getPlayer(userName).getModule();
		try
			{
			if ( myModule != null )
				myModule.playerDisconnection(userName);
			}
		catch ( RemoteException ex )
			{
			// Never occurs since playerDisconnection() is called locally.
			}

		// Create and send a log-off notification message to all players.
		String[] fullMessage = new String[2];
		fullMessage[0] = new String(userName + " has logged off of the game server.\n");
		fullMessage[1] = TextMessage.LOG_OFF;
		GameMessage globalMessage = new TextMessage(fullMessage);		
		sendToAll(globalMessage);		
		
		Player who = getPlayer(userName);
		playerList.remove(who);
		
		sendUpdatedUserList();
		}
		
	
	
	/**
		Called remotely by a client to create a new player in the player database; if successful, then logs the player in.
		@param userName The player's name
		@param password The player's password
		@return false if that user name is already in use; true otherwise.
	*/
	public synchronized boolean createPlayer(String userName, String password) throws RemoteException, IOException
		{
		// Ensure that no player with this name already exists, by looking up password for that user name.
		// getPassword(userName) will return null if the name is not in use.
		if ( getPassword(userName) != null )
			return false;
		
		playerList.addToDatabase(userName, password);
		
		// Attempt to log the user in.
		if ( logIn(userName, password).equals(RemoteGameServer.SUCCESS) )
			return true;
		else
			return false;
		}
		

	/**
		Called remotely by clients to retreive new messages.  These messages are dequeued from the player's server-side
		message queue.  The client calls this function once every second.
		@param userName The name of the player that needs to retrieve messages
		@return An array of GameMessage objects
	*/
	public synchronized GameMessage[] pollForMessages(String userName) throws RemoteException
		{
		Player forPlayer = getPlayer(userName);
		
		if ( forPlayer == null )
			return null;
		
		GameMessage[] myReturn = forPlayer.getMessages();
		
		return myReturn;
		}

	
	/**
		Called remotely by clients to send a global message to all players.
		@param fromUser The name of the player sending the message
		@param message The message being sent
	*/
	public synchronized void sendGlobalMessage(String fromUser, String message) throws RemoteException
		{
		String[] fullMessage = new String[6];
		
		// Define the message and its styles (these strings alternate -- text, style, text, style)
		fullMessage[0] = fromUser;
		fullMessage[1] = TextMessage.MESSAGE_SENDER;
		
		fullMessage[2] = new String(" says: ");
		fullMessage[3] = TextMessage.WHITE;
		
		fullMessage[4] = new String("\"" + message + "\"\n");
		fullMessage[5] = TextMessage.YELLOW;
		
		// Create a TextMessage object for the text
		GameMessage globalMessage = new TextMessage(fullMessage);		

		sendToAll(globalMessage);		
		}


	/**
		Called remotely by clients to send an instant message (and/or a challenge) from one user to another.
		@param fromUser The name of the player sending the message
		@param toUser The name of the player receiving the message
		@param message The message being sent
		@param gameChallenge The game the player is being challenged to, or null if none.
	*/
	public synchronized void sendInstantMessage(String fromUser, String toUser, String message, String gameChallenge) throws RemoteException
		{
		Player toPlayer = getPlayer(toUser);
		Player fromPlayer = getPlayer(fromUser);
		
		if ( toPlayer == null || fromPlayer == null )
			return;
			
		// Handle non-challenge portion of message first.
		if ( message != null && message.length() > 0 )
			{
			String[] fullMessage = new String[8];
		
			// Define the message and its styles (these strings alternate -- text, style, text, style)
			fullMessage[0] = new String(">>> ");
			fullMessage[1] = TextMessage.RED;
			fullMessage[2] = new String("Instant message from ");
			fullMessage[3] = TextMessage.WHITE;
			fullMessage[4] = fromUser;
			fullMessage[5] = TextMessage.MESSAGE_SENDER;
			fullMessage[6] = new String(":  \"" + message + "\"\n");
			fullMessage[7] = TextMessage.IM_TEXT;
			GameMessage instantMessage = new TextMessage(fullMessage);		
			
			String[] confirmMessage = new String[2];
			confirmMessage[0] = new String("Message sent to " + toUser + ".\n");
			confirmMessage[1] = TextMessage.WHITE;
			GameMessage confirmation = new TextMessage(confirmMessage);
						
			send(toPlayer, instantMessage);
			send(fromPlayer, confirmation);
			}
			
		// Now handle the challenge part.  Check for case where receiving player is already in a game.
		if ( gameChallenge != null && !gameChallenge.equals("none") )
			{
			if ( toPlayer.getModule() != null )
				{
				String[] fullMessage = new String[2];
				fullMessage[0] = new String(toUser + " cannot be challenged; that user is already playing another game.\n");
				fullMessage[1] = TextMessage.WHITE;
				GameMessage instantMessage = new TextMessage(fullMessage);		
						
				send(fromPlayer, instantMessage);
				return;
				}
			

			GameMessage challengeMessage = new ChallengeMessage(gameChallenge, fromUser);

			String[] confirmMessage = new String[8];
			confirmMessage[0] = gameclient.Client.fixGameName(gameChallenge);
			confirmMessage[1] = TextMessage.MESSAGE_SENDER;
			confirmMessage[2] = " challenge sent to ";
			confirmMessage[3] = TextMessage.WHITE;
			confirmMessage[4] = toUser;
			confirmMessage[5] = TextMessage.MESSAGE_SENDER;
			confirmMessage[6] = "; waiting for reply.\n";
			confirmMessage[7] = TextMessage.WHITE;
			GameMessage confirmation = new TextMessage(confirmMessage);

			send(toPlayer, challengeMessage);
			send(fromPlayer, confirmation);
			}
		}
		


	/**
		Called remotely by a client to answer a challenge.  Handles the response appropriately.
		@param respondingUser The name of the player responding to the challenge
		@param challengingUser The name of the player who sent the challenge
		@param gameName The name of the game being challenged to
		@param response True if the user accepted the challenge; false otherwise.
	*/
	public synchronized void respondToChallenge(String respondingUser, String challengingUser, String gameName, boolean response) throws RemoteException
		{
		Player responder = getPlayer(respondingUser);
		Player challenger = getPlayer(challengingUser);

		if ( responder == null || challenger == null )
			return;

		// If the user declined the challenge, simply notify the challenger with a message.
		if ( !response )
			{
			String[] fullMessage = new String[4];
		
			// Define the message and its styles (these strings alternate -- text, style, text, style)
			fullMessage[0] = respondingUser;
			fullMessage[1] = TextMessage.MESSAGE_SENDER;

			fullMessage[2] = new String(" has declined your challenge to play " + gameclient.Client.fixGameName(gameName) + ".\n");
			fullMessage[3] = TextMessage.WHITE;
		
			// Create a GameMessage object for the text
			GameMessage instantMessage = new TextMessage(fullMessage);		
			send(challenger, instantMessage);
			}
			
			
		// If the user accepted the challenge, start up the game module and then send messages to each client to
		// have them start the appropriate GUI client-side module.
		else
			{
			// If either of the two players are already in a game, abort the challenge process.
			if ( responder.getModule() != null || challenger.getModule() != null )
				return;

			String[] confirmMessage = new String[4];
			confirmMessage[0] = respondingUser;
			confirmMessage[1] = TextMessage.MESSAGE_SENDER;
			confirmMessage[2] = " has accepted your challenge.\n";
			confirmMessage[3] = TextMessage.RED;
			GameMessage confirmation = new TextMessage(confirmMessage);
			send(challenger, confirmation);
			
			ServerModule myModule;

			try
				{
				myModule = (ServerModule)(Class.forName(gameName).newInstance());
				}
			catch ( Exception excep )
				{
				System.out.println("Error: An invalid game may be installed.  Full stack trace:\n");
				excep.printStackTrace();
				System.out.println("\nThe offending game, " + gameName + ", has been auto-removed from the server's game list.\n");
				System.out.println("Please be sure to only install games that implement the ServerModule interface.\n");
				gameList.removeGame(gameName);
				return;
				}
			
			challenger.setModule(myModule);
			responder.setModule(myModule);
			sendUpdatedUserList();

			// Create and send enter-game messages to both clients.
			GameMessage enterGameMessage = new EnterGameMessage(gameName);
	
			myModule.playGame(this, challenger.getName(), responder.getName());			
			send(challenger, enterGameMessage);
			send(responder, enterGameMessage);
			}
		}
		
		

	/**
		Called remotely by clients to retrieve a list of names of games available on the server.
	*/
	public synchronized String[] getGameNames() throws RemoteException
		{
		return gameList.getModuleList();
		}
	
		
	/**
		Called remotely by client to retreive the server for a player's module.
	*/
	public synchronized ServerModule getPlayerServerModule(String playerName) throws RemoteException
		{
		Player forPlayer = getPlayer(playerName);
		
		if ( forPlayer == null )
			return null;
			
		return forPlayer.getModule();
		}
		

	/**
		Called remotely by client to get base directory of game's web server.  Used by client's lobby.
	*/
	public synchronized String getWebAddress() throws RemoteException
		{
		return webURL;
		}
	
	/** 
		Used to run the console-mode version of the game server.  A command-line parameter can optionally be supplied
		to specify that the RMI Registry is running on a nonstandard port.
	*/
	public static void main(String[] arg) throws RemoteException
		{
	    String serverArg = null;
	    
	    if ( arg.length > 0 )
	    	serverArg = arg[0];
	    
    	Server myServer = new Server(serverArg);
	    myServer.runConsoleInterface();	    
  		}
}