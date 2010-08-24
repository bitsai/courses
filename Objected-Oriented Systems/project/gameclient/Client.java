package gameclient;


import java.io.*;
import java.net.URL;
import java.awt.*;
import java.awt.event.*;
import java.rmi.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;
import gameserver.GameMessage;
import gameserver.TextMessage;
import gameserver.RemoteGameServer;


/** 
	A GUI-based game client for connecting to the expandable Game Server.  The client's UI contains the following widgets:<BR><BR>
	- A user list (right sidebar) which displays what users are on-line and what modules they are in.  Users can be challenged
	  to games, and can be sent instant messages.<BR><BR>
	- A console window (bottom) which displays stylized text messages.  These messages include log-on/log-off notifications,
	  game victory reports, and global player chat.  A text box allows the user to send global chat messages.<BR><BR>
	- A game panel (center) which is controlled entirely by a ClientModule object, depending on what game the user is playing.<BR><BR>
	
	This class has a main() method for running the client.  Simply use "java gameclient/Client" to run the client. Make	sure that the client.policy file is in your current directory (which is NOT necessarily the client's directory) in	order for the Client to have proper RMI permissions.
*/
public class Client extends JFrame
{
	private String userName;
	private RemoteGameServer myServer;
	private LobbyPanel myLobby;	private UserListPanel userPanel;
	private JPanel gamePanel;
	private JTextField textInp;
	private JTextPane consoleArea;
	private JScrollPane userScroll;
	private JPanel userListingPanel;
	private JButton abortGameButton;


	public Client() 
	 	{
		setTitle("Game Client");
		setSize(700, 700);
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		System.setProperty("java.security.policy", "client.policy");
		System.setSecurityManager(new RMISecurityManager());

		gamePanel = new JPanel();
		gamePanel.setSize(500, 500);
		gamePanel.setPreferredSize(new Dimension(500, 500));
		gamePanel.setBackground(Color.GRAY);

		consoleArea = new JTextPane();
		consoleArea.setEditable(false);
		consoleArea.setBackground(Color.BLACK);
		
		createStyles(consoleArea);
		JScrollPane consoleScroll = new JScrollPane(consoleArea);
		consoleScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		consoleScroll.setPreferredSize(new Dimension(700, 120));

		textInp = new JTextField();
		JPanel globalPanel = new JPanel();
		JButton sendGlobalButton = new JButton("Send");
		globalPanel.setLayout( new BorderLayout(10, 10) );
		globalPanel.add(consoleScroll, BorderLayout.NORTH);
		globalPanel.add(new JLabel("Enter global text:"), BorderLayout.WEST);
		globalPanel.add(sendGlobalButton, BorderLayout.EAST);
		globalPanel.add(textInp, BorderLayout.CENTER);

		// Add the action listener for the text input button
		sendGlobalButton.addActionListener( new ActionListener()
			{
			public void actionPerformed(ActionEvent event)
				{
				if ( textInp.getText().length() > 0 )
					{
					try
						{
						myServer.sendGlobalMessage(userName, textInp.getText());
						textInp.setText("");
						}
						
					catch ( RemoteException badConnect )
						{
						JOptionPane.showMessageDialog(Client.this, "Action aborted - unable to reach server!", "Warning", JOptionPane.WARNING_MESSAGE);
						}
					}
				}
			} );
		

		userPanel = new UserListPanel(null);
		userScroll = new JScrollPane(userPanel);
		userScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		userScroll.setSize(200, 440);
		userScroll.setPreferredSize(new Dimension(200, 440));
		
		JButton sendChallengeButton = new JButton("Challenge / Message");
		sendChallengeButton.addActionListener( new ActionListener()
			{
			public void actionPerformed(ActionEvent event)
				{
				// Create a dialog to get the IM/challenge info, unless user is trying to IM/challenge self.
				if ( userPanel.getSelection().equalsIgnoreCase(userName) )
					JOptionPane.showMessageDialog(Client.this, "You cannot send challenges or messages to yourself.", "Error", JOptionPane.WARNING_MESSAGE);
				else
					{
					MessageDialog myDialog = new MessageDialog(Client.this, userPanel.getSelection());
					}
				}
			} );
				abortGameButton = new JButton("Abort Game");		abortGameButton.setEnabled(false);
		abortGameButton.addActionListener( new ActionListener()
			{
			public void actionPerformed(ActionEvent event)
				{
				try
					{					myServer.abortGame(userName);
					}				catch ( RemoteException badConnect )					{					JOptionPane.showMessageDialog(Client.this, "Action aborted - unable to reach server!", "Warning", JOptionPane.WARNING_MESSAGE);
					}				}			} );
				JPanel userButtonPanel = new JPanel();
		userButtonPanel.setLayout( new GridLayout(2, 1) );
		userButtonPanel.add(sendChallengeButton);
		userButtonPanel.add(abortGameButton);				
		userListingPanel = new JPanel();
		userListingPanel.setLayout( new BorderLayout(5, 5) );
		userListingPanel.add(userScroll, BorderLayout.CENTER);
		userListingPanel.add(userButtonPanel, BorderLayout.SOUTH);
		userListingPanel.setPreferredSize(new Dimension(200, 500));


		// Add all widgets to the frame's content pane.
		Container myContent = getContentPane();
		myContent.setLayout( new BorderLayout(15, 15) );

		myContent.add(gamePanel, BorderLayout.CENTER);
		myContent.add(globalPanel, BorderLayout.SOUTH);
		myContent.add(userListingPanel, BorderLayout.EAST);
		show();
  		}
  		
  		
  	/** Defines styles for a console message window. */
  	void createStyles(JTextPane textPane)
  		{
		Style def = StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE);

        Style regular = textPane.addStyle(TextMessage.REGULAR, def);
        StyleConstants.setFontFamily(def, "SansSerif");

        Style newStyle = textPane.addStyle(TextMessage.WINNER, regular);
        StyleConstants.setForeground(newStyle, Color.CYAN);
        
        newStyle = textPane.addStyle(TextMessage.WINNER_BOLD, regular);
        StyleConstants.setForeground(newStyle, Color.CYAN);
        StyleConstants.setBold(newStyle, true);

        newStyle = textPane.addStyle(TextMessage.LOG_ON, regular);
        StyleConstants.setForeground(newStyle, Color.GRAY);

        newStyle = textPane.addStyle(TextMessage.LOG_OFF, regular);
        StyleConstants.setForeground(newStyle, Color.GRAY);

        newStyle = textPane.addStyle(TextMessage.MESSAGE_SENDER, regular);
        StyleConstants.setForeground(newStyle, new Color(125, 125, 255));
        StyleConstants.setBold(newStyle, true);

        newStyle = textPane.addStyle(TextMessage.WHITE, regular);
        StyleConstants.setForeground(newStyle, Color.WHITE);

        newStyle = textPane.addStyle(TextMessage.YELLOW, regular);
        StyleConstants.setForeground(newStyle, Color.YELLOW);

        newStyle = textPane.addStyle(TextMessage.RED, regular);
        StyleConstants.setForeground(newStyle, Color.RED);

        newStyle = textPane.addStyle(TextMessage.IM_TEXT, regular);
        StyleConstants.setForeground(newStyle, Color.GREEN);
  		}


	/** 
		This method routinely polls the server for new messages and then calls their process() method.  Game messages		are polymorphic, so the effects of process() depends on the message type.
	*/
	public void messageLoop()
		{
		GameMessage[] myMessages = null;
		int errorCount = 0;
		
		while ( true )
			{
			try
				{
				myMessages = myServer.pollForMessages(userName);
				errorCount = 0;

				if ( myMessages != null )
					{
					for ( int n = 0; n < myMessages.length; n++ )
						{
						myMessages[n].process(this);
						}
					}
				}
				
			catch ( RemoteException badConnect )
				{
				errorCount++;
				if ( errorCount == 15 )
					JOptionPane.showMessageDialog(this, "Your connection to the server may have been lost.", "Warning", JOptionPane.WARNING_MESSAGE);
				}
						// Pause 1 second between polling intervals
			try
				{
				Thread.sleep(1000);
				}
			catch ( Exception e )
				{
				// No need to do anything special if sleep() was interupted.				}
			}
		
		}

	
	/** Sets the game panel to the lobby panel */
	public void enterLobby()
		{
		try
			{
			if ( myLobby == null ) 
				{
				String webAddy = myServer.getWebAddress();
				myLobby = new LobbyPanel(webAddy, myServer);
				}
			
			getContentPane().remove(gamePanel);
			gamePanel = myLobby;
			getContentPane().add(gamePanel, BorderLayout.CENTER);			abortGameButton.setEnabled(false);
			pack();
			}

		catch ( RemoteException rmiProblem )
			{
			JOptionPane.showMessageDialog(this, "Your connection to the server may have been lost.", "Warning", JOptionPane.WARNING_MESSAGE);
			}
		}
	

	/**
		Displays a stylized text message in the client's console message window.
		@param msg A TextMessage object, containing the message to be displayed and what styles to use.
	*/
	public void displayMessage(TextMessage msg)
		{
	    Document doc = consoleArea.getDocument();

		for ( int n = 0; n < msg.getTextCount(); n++ )
			{
			try 
				{
                doc.insertString( doc.getLength(), msg.getTextString(n), consoleArea.getStyle(msg.getStyleName(n)) );
	            }
            
        	catch (BadLocationException ble) 
        		{
				JOptionPane.showMessageDialog(this, "Message console error.", "Warning", JOptionPane.WARNING_MESSAGE);
        	    }
			}
			
		Dimension boundary = consoleArea.getSize(null);
		consoleArea.scrollRectToVisible( new Rectangle(1, boundary.height-1, 1, 1) );
		}
		
	/**
		Updates the user list.  Called by UpdateUserListMessage.process().
		@param updatedList An updated user list in the format provided by the server.
	*/
	public void updateUserList(String[] updatedList)
		{
		userListingPanel.remove( userScroll );
		userPanel = new UserListPanel( updatedList );
		userScroll = new JScrollPane(userPanel);
		userScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		userScroll.setPreferredSize(new Dimension(200, 470));
		userListingPanel.add( userScroll, BorderLayout.CENTER);
		pack();
		}


	/**
		Causes the player to enter a game.  The game's class is specified by className; this class must implement the
		ClientModule interface.  The client dynamically creates an object of this class, using 
		Class.forName(className).newInstance().  A stub for the game's ServerModule object is retrieved from the remote 		server and then passed to the client with the ClientModule.setServer() method, and the player's name is set 
		with ClientModule.setPlayerName().
		@param guiClassName The name of a class implementing the ClientModule interface
	*/
	public void enterGame(String guiClassName)
		{
		try
			{
			ClientModule myModule = (ClientModule)(Class.forName(guiClassName).newInstance());
			myModule.setGameInfo(this, myServer.getPlayerServerModule(userName), userName);  
			getContentPane().remove(gamePanel);
			gamePanel = (JPanel)myModule;
			getContentPane().add(gamePanel, BorderLayout.CENTER);
			gamePanel.setPreferredSize( gamePanel.getMinimumSize() );			abortGameButton.setEnabled(true);
			pack();
			}
		catch ( Exception someError )
			{
			String badGame = new String("An error has occurred; you cannot play this game for one of the following reasons:\n");			badGame += " - You do not have " + guiClassName + ".class on your system, which is required to play this game.\n";			badGame += " - You have the file, but it is not a valid JPanel or ClientModule.\n";			badGame += " - The server is currently down.\n";			
			JOptionPane.showMessageDialog(this, badGame, "Warning", JOptionPane.WARNING_MESSAGE);			}
	
		}
		
		
	/**
		Sets the client's user name and reference to remote server.  Also sets the Client Frame's close-window operation
		to call logOff() method on remote server.
	*/
	public void setUser(final String myUser, final RemoteGameServer server)
		{
		userName = myUser;
		myServer = server;

	    addWindowListener( new WindowAdapter()
	    	{
	    	public void windowClosing(WindowEvent event)
	    		{
	    		try
	    			{
		    		server.logOff(myUser);	
		    		}
		    	catch ( RemoteException e )
		    		{
					// No need to do anything on this remote exception, since the user is trying to log off anyway.
		    		}
	    		
	    		System.exit(0);
	    		}
	    	} );
	    	
	    	
		}
		
	
	/** Returns the client's user name. */
	public String getUser()
		{
		return userName;
		}
	
	/** Returns a reference to the client's remote server. */
	public RemoteGameServer getServer()
		{
		return myServer;
		}
		
	/** Returns true if the user is playing a game, or false if the player is in lobby. */
	public boolean isInGame()
		{
		if ( gamePanel == myLobby )
			return false;
		else
			return true;
		}
		/** Returns a string containing a game class name, and removes the package portion of the name.
	    This is useful for display purposes, ie the client will want to display "TicTacToe" instead
		of "tictactoe.TicTacToe".
	 */
	public static String fixGameName(String fullClass)
		{
		for ( int n = 0; n < fullClass.length(); n++ )
			{
			if ( fullClass.charAt(n) == '.' )
				{
				fullClass = fullClass.substring(n+1);
				n = 0;
				}
			}
				return fullClass;
		}	
	
	/** Runs the client.  Does not use any command-line parameters. */	public static void main(String[] arg) throws IOException
		{
	    Client gameClient = new Client();
	    LogOnDialog logOn = new LogOnDialog(gameClient);
	    
	    gameClient.setUser(logOn.getUser(), logOn.getServer());
	    gameClient.enterLobby();
    	gameClient.messageLoop();
  		}
  	
}


