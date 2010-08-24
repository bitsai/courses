package gameclient;

import gameserver.ServerModule;

/** 
	Interface for a game client module.  These modules plug-in to the game client to provide a GUI for the game. 
	The client creates these modules using reflection, since the module name is dynamically provided in 
	a string at run-time.<BR><BR>
	
	All game modules must have a client-side ClientModule class, ending in "GUI".  For example, a TicTacToe game has
	a class which implements ClientModule; this class is called "TicTacToeGUI.class".
*/
public interface ClientModule
{
	/** 
		Called by Client immediately after creating the ClientModule, to provide the ClientModule with its owner-frame,
		its remote ServerModule, and its player name.
	*/
	void setGameInfo(javax.swing.JFrame myOwner, ServerModule myServerModule, String playerName);
	
}