package tictactoe;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.rmi.*;
import gameclient.ClientModule;
import gameserver.ServerModule;

/** 
	Client GUI for an RMI-based TicTacToe game.
*/
public class TicTacToeGUI extends JPanel implements ClientModule
{
	TicTacToeRemote myServer;
	String myName;
	Cell[][] myCells;
	JLabel infoLabel;
	
	// Constructor created via reflection in gameclient.Client; takes no args
	public TicTacToeGUI()
		{
		setLayout(new BorderLayout(10, 10));
		
		infoLabel = new JLabel(" ");
		
		JPanel boardPanel = new JPanel();
		boardPanel.setLayout(new GridLayout(3, 3));

		myCells = new Cell[3][3];

		// Adds the cells of the tic-tac-toe board and the listeners for the buttons.
		for ( int n = 0; n < 3; n++ )
			{
			for ( int z = 0; z < 3; z++ )
				{
				myCells[n][z] = new Cell(n, z);
				boardPanel.add(myCells[n][z]);
				
				myCells[n][z].addActionListener( new ActionListener() 
					{
					public void actionPerformed(ActionEvent event)
						{
						String result = null;
						Cell myCell = (Cell)event.getSource();
						
						try
							{
							result = myServer.makeMove(myName, myCell.getRow(), myCell.getCol());
							}
						catch ( RemoteException connectProb )
							{
							JOptionPane.showMessageDialog(null, "Your connection to the server may have been lost.", "Warning", JOptionPane.WARNING_MESSAGE);
							}
						
						if ( result != null )
							JOptionPane.showMessageDialog(null, result, "Message", JOptionPane.WARNING_MESSAGE);
							
						}
					} );
				}
			}
			
		add(boardPanel, BorderLayout.CENTER);
		add(infoLabel, BorderLayout.NORTH);
		}
	
	/** Provides references to the owning frame, a remote ServerModule for this game, and the player's name. 
		Called by Client immediately after creating the client module. */
	public void setGameInfo(JFrame myFrame, ServerModule myServerModule, String playerName)
		{
		myServer = (TicTacToeRemote)myServerModule;
		myName = playerName;

		// Spawn a new thread in order to have a game board with real-time updates.
		TicTacToeThread updateThread = new TicTacToeThread(this, myServer);
		updateThread.start();
		}
	
		
	void setCell(int row, int col, String text)
		{
		myCells[row][col].setOwner(text);
		}
}


/** Class for a "cell" of the TicTacToe game board. */
class Cell extends JButton
{
	private int myRow;
	private int myCol;
	private String myOwner;
	
	Cell(int row, int col)
		{
		myRow = row;
		myCol = col;
		}
		
	int getRow()
		{
		return myRow;
		}
		
	int getCol()
		{
		return myCol;
		}
		
	void setOwner(String newOwner)
		{
		setText(newOwner);
		}
}



class TicTacToeThread extends Thread
{
	TicTacToeGUI myGUI;
	TicTacToeRemote myServer;
	
	public TicTacToeThread(TicTacToeGUI aGUI, TicTacToeRemote aServer)
		{
		myGUI = aGUI;
		myServer = aServer;
		}
		

	public void run()
		{
		String winner = new String("");
		
		try
			{
			while ( myServer.getWinner().equals("") )
				{
				updateBoard();
				Thread.sleep(1000);
				}
				
			updateBoard();
			winner = myServer.getWinner();
			}

		catch ( Exception excep )
			{
			System.out.println("Problem: " + excep.getMessage());
			//--> make this more complex
			}
		}

	
	/** Updates the game board by marking cells with X or O as needed. */
	public void updateBoard()
		{
		String[][] myLabels;
		
		try
			{
			myLabels = myServer.getBoard();
			myGUI.infoLabel.setText(myServer.getPlayerStatus(myGUI.myName));
			}
		catch ( Exception excep )
			{
			System.out.println("Problem:" + excep.getMessage());
			excep.printStackTrace();
			return;
			}
			
		for ( int n = 0; n < 3; n++ )
			{
			for ( int k = 0; k < 3; k++ )
				{
				myGUI.setCell(n, k, myLabels[n][k]);
				}
			}
			
		}

}