package gameclient;

import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;
import java.io.*;
import java.rmi.*;
import gameserver.RemoteGameServer;

/** 
	A class for controlling the game panel for the client's "Lobby".  This panel is used in place of a ClientModule when
	the user is not currently playing a game.  The panel allows the player to view game instructions.
	Game instructions are in HTML format and are displayed by the lobby via use of a JEditorPane.  These instruction
	files are stored on the server and are retrieved by the client.<BR><BR>
	
	IMPORTANT:  It is up to the server administration to put the lobby / instruction files in the correct directories.
	The server does not generate these instruction files automatically.  However, the web URL to use for the instruction
	file directory can be configured in the game server.
*/
public class LobbyPanel extends JPanel
{
	JEditorPane lobbyDisplay;
	RemoteGameServer myServer;
	
	/** Constructor must be passed a string containing a URL to the host's directory which stores the indexes for game
		instruction files.
	*/
	public LobbyPanel(final String hostDirectoryURL, RemoteGameServer fromServer)
		{
		myServer = fromServer;
		
		setMaximumSize(new Dimension(500, 500));
		setBackground(Color.BLACK);
		
		lobbyDisplay = new JEditorPane();
		lobbyDisplay.setEditable(false);

		try
			{
			lobbyDisplay.setPage(hostDirectoryURL + "lobby.html");
			}
		catch ( IOException e )
			{
			JOptionPane.showMessageDialog(null, "This game server does not appear to have a lobby at " + hostDirectoryURL + "lobby.html.  Please report this problem to the server admin.", "Warning", JOptionPane.WARNING_MESSAGE);
			setBackground(Color.GRAY);
			}

		// Add a listener for hyperlinks in the lobby and instruction files.  A special hyperlink of "SHOW_LIST" may
		// be used in the Lobby to bring up a list of games available on the server and prompt which one to view instructions
		// for.  For example, tictactoe.TicTacToe displays as "TicTacToe" and will link to "TicTacToe.html".
		lobbyDisplay.addHyperlinkListener( new HyperlinkListener()
			{
			public void hyperlinkUpdate(HyperlinkEvent event)
				{
				if ( event.getEventType() != HyperlinkEvent.EventType.ACTIVATED )
					return;
				
				try
					{
					if ( event.getURL().toString().equals(hostDirectoryURL+"SHOW_LIST") )
						{
						String[] gameList = myServer.getGameNames();
						
						for ( int n = 0; n < gameList.length; n++ )
							{
							gameList[n] = Client.fixGameName(gameList[n]);
							}
						
						if ( gameList.length >= 1 )
							{
							int userOption = JOptionPane.showOptionDialog(	null, 
																			"View instructions on what game?", 
																			"Game Instructions", 
																			JOptionPane.DEFAULT_OPTION,
																			JOptionPane.QUESTION_MESSAGE, 
																			null, 
																			gameList, 
																			gameList[0] );
							if ( userOption != JOptionPane.CLOSED_OPTION )
								{
								lobbyDisplay.setPage(hostDirectoryURL + gameList[userOption] + ".html");
								LobbyPanel.this.setSize(500, 500);
								}

							}
						else
							JOptionPane.showMessageDialog(null, "This server has no games.", "Oops", JOptionPane.WARNING_MESSAGE);
						}
					else
						{
						lobbyDisplay.setPage(event.getURL());
						LobbyPanel.this.setSize(500, 500);
						}
						
					}
						
				catch ( RemoteException re )
					{
					JOptionPane.showMessageDialog(null, "Action aborted - unable to reach server!", "Warning", JOptionPane.WARNING_MESSAGE);
					}
				catch ( IOException e )
					{
					JOptionPane.showMessageDialog(null, "The document cannot be found!", "HTTP 404", JOptionPane.WARNING_MESSAGE);
					}
				}
			} );
		
		setLayout( new BorderLayout(0, 0) );
		add(lobbyDisplay);
		}
		
}


