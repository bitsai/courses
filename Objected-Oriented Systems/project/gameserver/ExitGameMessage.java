package gameserver;

import gameclient.Client;
import javax.swing.*;

/** 
	A GameMessage that tells a client to exit its current client-side game module and return to the lobby.  Send by the
	server when a game ends.
*/
public class ExitGameMessage implements GameMessage
{
	private String winnerName;
	private boolean wasTie;
	private boolean wasAborted;
	
	/**
		@param theWinner The name of the player who won the game, or any other string to designate no one won.
		@param aTie True if the game was a tie, false otherwise.
		@param anAbort True if one of the players logged off mid-game or hit the "abort game" button, false otherwise.
	*/
	public ExitGameMessage(String theWinner, boolean aTie, boolean anAbort)
		{
		winnerName = theWinner;
		wasTie = aTie;
		wasAborted = anAbort;
		}
		
	/** Brings up a dialog box on the client saying who won the game, and then returns the user to the lobby. */
	public void process(Client myClient)
		{
		String endText;
		
		if ( winnerName.equalsIgnoreCase(myClient.getUser()) )
			{
			endText = "You win!";
			if ( wasAborted )
				endText = "Your opponent quit the game!  " + endText;
			}
		else
			{	
			if ( wasTie )
				endText = "The game was a draw!";
			else
				endText = "You lose!  " + winnerName + " was the winner!";
			}
		
		JOptionPane.showMessageDialog(myClient, endText, "Game over", JOptionPane.WARNING_MESSAGE);
		myClient.enterLobby();
		}
}