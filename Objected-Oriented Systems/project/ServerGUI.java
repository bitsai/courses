import gameserver.Server;
import gameclient.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;


/** 
	GUI for the game server.  To execute, use "java ServerGUI" (note that this file intentionally not packaged, to make
	it easier to run)
*/
public class ServerGUI extends JFrame
{
	private JTextArea gameListArea;
	private JTextField lobbyAddy;
	private Server myServer;
	
	public ServerGUI() throws java.rmi.RemoteException
	 	{
		setTitle("Game Server");
		setSize(400, 400);
		getContentPane().setLayout( new BorderLayout(8, 8) );


		gameListArea = new JTextArea();
		gameListArea.setEditable(false);
		JScrollPane listScroll = new JScrollPane(gameListArea);
		listScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

		getContentPane().add(listScroll, BorderLayout.CENTER);
		
		JButton addGameButton = new JButton("Add Game");
		addGameButton.addActionListener( new ActionListener() 
			{
			public void actionPerformed(ActionEvent ev)
				{
				String userInput = JOptionPane.showInputDialog(ServerGUI.this, "Please specify ServerModule implementation class,\n WITH full package, but WITHOUT the .class extension.");
				try
					{
					myServer.addGame(userInput);
					updateGameList();
					}
				catch ( ClassNotFoundException badClass )
					{
					JOptionPane.showMessageDialog(ServerGUI.this, "Unabled to locate " + userInput + ".class!", "Error", JOptionPane.ERROR_MESSAGE);
					}
				}
				
			} );
			
		JButton removeGameButton = new JButton("Remove Game");
		removeGameButton.addActionListener( new ActionListener()
			{
			public void actionPerformed(ActionEvent ev)
				{
				try
					{
					String[] gameList = myServer.getGameNames();
					
					if ( gameList.length == 0 )
						{
						JOptionPane.showMessageDialog(ServerGUI.this, "There are currently no games installed.", "Oops", JOptionPane.WARNING_MESSAGE);
						return;
						}
						
					
					int userOption = JOptionPane.showOptionDialog(	ServerGUI.this, "Remove what game?", "Remove", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, gameList, gameList[0] );
					if ( userOption != JOptionPane.CLOSED_OPTION )
						{
						myServer.removeGame(gameList[userOption]);
						updateGameList();
						}
					}
				catch ( java.rmi.RemoteException ignore )
					{
					// Can safely ignore; we're calling getGameNames() locally.
					}
				}
			} );
		
		JButton changeLobbyButton = new JButton("Change lobby URL");
		changeLobbyButton.addActionListener( new ActionListener()
			{
			public void actionPerformed(ActionEvent ev)
				{
				try
					{
					String userInput = JOptionPane.showInputDialog(ServerGUI.this, "Enter new URL for lobby directory.\n(Please note that users who are currently on-line\nmay not be affected until they log off.)", myServer.getWebAddress());
					myServer.setLobbyAddress(userInput);
					updateGameList();
					}
				catch ( java.rmi.RemoteException ignoreSinceLocal )
					{
					}
				}
			} );

		JButton shutdownButton = new JButton("Shut down server");
		shutdownButton.addActionListener( new ActionListener()
			{
			public void actionPerformed(ActionEvent ev)
				{
				myServer.shutDown();
				}
			} );
				
		JPanel myButtonPanel = new JPanel();
		myButtonPanel.setLayout( new GridLayout(4,1) );
		myButtonPanel.add(addGameButton);
		myButtonPanel.add(removeGameButton);
		myButtonPanel.add(changeLobbyButton);
		myButtonPanel.add(shutdownButton);
		JPanel outerPanel = new JPanel();
		outerPanel.setBackground(Color.DARK_GRAY);
		outerPanel.add(myButtonPanel);
		getContentPane().add(outerPanel, BorderLayout.EAST);


	    addWindowListener( new WindowAdapter()
	    	{
	    	public void windowClosing(WindowEvent event)
	    		{
	    		myServer.shutDown();	
	    		}
	    	} );


		show();
		
		String portInput = JOptionPane.showInputDialog(this, "What port is the RMI registry running on?", "1099");
		if ( portInput != null && portInput.length() < 1 )
			portInput = null;
		
		myServer = new Server(portInput);
		
		updateGameList();
		}


	public void updateGameList()
		{
		String gameText = new String("Game server running.\n\n\nINSTALLED GAMES:\n");

		try
			{
			String[] gameList; 
			gameList = myServer.getGameNames();
			
			if ( gameList != null && gameList.length > 0 )
				{
				for ( int n = 0; n < gameList.length; n++ )
					{
					gameText = gameText + "  - " + gameList[n] + "\n";
					}
				}
			else
				gameText += "  None.";
				
			gameText += "\n\nURL for lobby directory set to:\n  - " + myServer.getWebAddress();
			}
			
		catch ( java.rmi.RemoteException ignore )
			{
			// Can safely ignore since we're calling getGameNames() and getWebAddress() locally
			}
			
		gameListArea.setText(gameText);		
		}

	
	public static void main(String[] arg) throws java.rmi.RemoteException
		{
	    ServerGUI myServer = new ServerGUI();
  		}
		
		
}


