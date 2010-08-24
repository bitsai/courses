package gameclient;


import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.rmi.*;
import java.rmi.server.*;
import gameserver.RemoteGameServer;


/**
	A simply Dialog Box class for asking a player if they accept or decline a challenge offer.  The ChallengeDialog sends the player's response to the server automatically.
*/
public class ChallengeDialog
{
	private String userName;
	private RemoteGameServer myServer;
	
	public ChallengeDialog(Client owner, String gameName, String challenger)
	 	{
		userName = owner.getUser();
		myServer = owner.getServer();

		String text = new String(challenger + " has challenged you to a game of " + Client.fixGameName(gameName) + ".\nDo you accept?");
		int selection = JOptionPane.showConfirmDialog(owner, text, "You have been challenged!", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
		boolean answer = (selection == JOptionPane.YES_OPTION);
	
		try
			{
			myServer.respondToChallenge(userName, challenger, gameName, answer);
			}
		catch ( RemoteException problem )
			{
			JOptionPane.showMessageDialog(owner, "Action aborted - unable to reach server!", "Warning", JOptionPane.WARNING_MESSAGE);
			}
		
		}
}



