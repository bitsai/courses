package battletech;

import gameserver.*;
import java.rmi.*;
import java.rmi.server.*;

/** This class represents the BattleTech game module. */

public class BattleTech extends UnicastRemoteObject implements BattleTechRemote
{
	private Server server;
	private String player1;
	private String player2;
	private String output = "";

	private int playersInitialized = 0;
	private boolean mapInitialized = false;

	private Map map;
	private MechRemote mech1;
	private MechRemote mech2;
	private int whoseTurn = 1;

	public BattleTech() throws RemoteException {}

// Server module functionality

	/** 
		The Server calls this method to tell the ServerModule to begin gameplay. 
		@param aServer A reference to the Server object; needed so the ServerModule knows what to call gameOver() on.
		@param player1 The name of the first player.
		@param player2 The name of the second player.
		@return A string indicating the name of the player who won the game.
	*/
	public synchronized void playGame(Server _server, String _player1, String _player2)
	{
		server = _server;
		player1 = _player1;
		player2 = _player2;
	}

	/**
		Tells the game that a player's connection has been lost, or that a player hit their "abort game" button.  The
		game module should handle this appropriately, by calling Server.gameOver().
		@param playerName the name of the player who disconnected/aborted.
	*/
	public synchronized void playerDisconnection(String loser)
	{
		if (loser.equalsIgnoreCase(player1))
		{
			player2Win();
			return;
		}

		player1Win();
		return;
	}

	/**
		Returns the module's name.
	*/
	public synchronized String getName() { return("BattleTech"); }

// Query methods

	/** Returns the status updates for the currently inactive player. */
	public synchronized String getOutput() 
	{
		String out = output;
		output = "";
	 	return out;
	}

	/** Returns the current map. */
	public synchronized Map getMap() { return map; }

	/** Returns a player's own Mech. */
	public synchronized MechRemote getOwnMech(String player)
	{
		if (player.equalsIgnoreCase(player1)) { return mech1; }

		return mech2;
	}

	/** Returns a player's opponent's Mech. */
	public synchronized MechRemote getOtherMech(String player)
	{
		if (player.equalsIgnoreCase(player1)) { return mech2; }

		return mech1;
	}

	/** Returns true if both players have sent in initialization information, false otherwise. */
	public synchronized boolean isInitialized()
	{
		if (playersInitialized == 2)
		{ return true; }

		return false;
	}

	/** Returns true if atleast one player's Mech is dead, false otherwise. */
	public synchronized boolean isGameOver() throws RemoteException
	{
		if (isMechDead(mech1) || isMechDead(mech2))
		{ return true; }

		return false;
	}

	/** Returns the name of the currently active player. */
	public synchronized String getWhoseTurn()
	{
		if (whoseTurn == 1)
		{ return player1; }

		return player2;
	}

	/** Returns true if the Mech passed in is dead, false otherwise. */
	public synchronized boolean isMechDead(MechRemote mech) throws RemoteException
	{
		boolean mechDead = (mech.getMW().isDead() || (mech.getComponents().getEngineHits() == 3));

		return mechDead;
	}

// Gameplay stuff
	
	/** Updates the status updates for the currently inactive player. */
	public synchronized void addOutput(String newText) { output = output + newText + "\n"; }

	/** Initializes the Mech for the player whose name is passed in.  If this is the first initialization call, it is the map name passed in here that is used to choose the map for the game. */
	public synchronized void initialize(String mapName, String player, String mechType) throws RemoteException
	{
		if (!mapInitialized)
		{
			try
			{	
				map = new Map(mapName + ".txt");
			}
			catch(java.io.IOException e)
			{
				System.out.println("Error loading " + mapName);
				map = new Map(100,100,5);
			}

			mapInitialized = true;
		}

		try
		{
			if (player.equalsIgnoreCase(player1))
			{ mech1 = MechMaker.loadMech(mechType, this); }
			else
			{ mech2 = MechMaker.loadMech(mechType, this); }
		}
		catch (Exception e)
		{
			mech1 = MechMaker.sampleMech(this);
			mech2 = MechMaker.sampleMech(this);
		}
		playersInitialized++;

		if (playersInitialized == 2)
		{ startGame(); }
	}

	/** Starts the game, by calling setStartingPositions() and setupMechs(). */
	public synchronized void startGame() throws RemoteException
	{
		setStartingPositions();
		setupMechs();
	}

	/** Sets up the starting positions of the mechs. */
	public synchronized void setStartingPositions()
	{
		try
		{
			mech1.setLocation(40, 50);
			mech1.setFacing(0);
			mech2.setLocation(map.getWidth() - 40, map.getLength() - 50);
			mech2.setFacing(180);
		}
		catch(RemoteException e)
		{
			System.out.println("BattleTech RemoteException!");
		}
	}

	/** Activates the mech whose turn it is, and deactivates the other mech. */
	public synchronized void setupMechs()
	{
		try
		{
			if (whoseTurn == 1) 
			{
				mech2.endTurn();
				mech1.startTurn();
			}
			else
			{
				mech1.endTurn();
				mech2.startTurn();
			}
		}
		catch(RemoteException e)
		{
			System.out.println("BattleTech RemoteException!");
		}
	}

	/** Ends the current turn, starts up the next turn. */
	public synchronized void endTurn() throws RemoteException // Called on by player via GUI
	{
		if (isGameOver())
		{
			endGame();
			return;
		}

		if (whoseTurn == 1) 
		{ whoseTurn = 2; }
		else
		{ whoseTurn = 1; }

		setupMechs();
	}

	/** Ends the game, and checks to see who the winner is, if there is one. */
	public synchronized void endGame() throws RemoteException
	{
		if (isMechDead(mech1) && isMechDead(mech2))
		{
			tie();
			return;
		}

		if (isMechDead(mech1))
		{
			player2Win();
			return;
		}

		if (isMechDead(mech2))
		{
			player1Win();
			return;
		}
	}

	/** Notifies the server that player 1 has won. */
	public synchronized void player1Win() { server.gameOver(player1, player1, player2); }

	/** Notifies the server that player 2 has won. */
	public synchronized void player2Win() { server.gameOver(player2, player1, player2); }

	/** Notifies the server that the game resulted in a tie. */
	public synchronized void tie() { server.gameOver("None", player1, player2); }
}