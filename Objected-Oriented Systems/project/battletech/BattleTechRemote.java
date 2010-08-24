package battletech;

import java.rmi.*;

/** This is the interface for the BattleTech game module. */

public interface BattleTechRemote extends Remote, gameserver.ServerModule
{
// Query methods

	/** Returns the status updates for the currently inactive player. */	
	public String getOutput() throws RemoteException;
	
	/** Returns the current map. */
	public Map getMap() throws RemoteException;

	/** Returns a player's own Mech. */
	public MechRemote getOwnMech(String player) throws RemoteException;

	/** Returns a player's opponent's Mech. */
	public MechRemote getOtherMech(String player) throws RemoteException;

	/** Returns true if both players have sent in initialization information, false otherwise. */
	public boolean isInitialized() throws RemoteException;

	/** Returns true if atleast one player's Mech is dead, false otherwise. */
	public boolean isGameOver() throws RemoteException;

	/** Returns the name of the currently active player. */
	public String getWhoseTurn() throws RemoteException;
	
// GUI calls

	/** Updates the status updates for the currently inactive player. */	
	public void addOutput(String newText) throws RemoteException;

	/** Initializes the Mech for the player whose name is passed in.  If this is the first initialization call, it is the map name passed in here that is used to choose the map for the game. */
	public void initialize(String mapName, String playerName, String mechType) throws RemoteException;

	/** Ends the current turn, starts up the next turn. */
	public void endTurn() throws RemoteException;
}