package gameclient;


import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.rmi.*;
import java.rmi.server.*;
import gameserver.RemoteGameServer;


/**
	A Dialog Box class for entering an instant message and/or challenge.  Gets message text and/or game challenge name from
	user and then sends the message to the remote server.  The challenge portion of the dialog box is only enabled if the
	player is not currently in a game (since a player can only be in 1 game at a time).
*/
public class MessageDialog extends JDialog
{
	private String userName;
	private String targetName;
	private RemoteGameServer myServer;
	private JTextArea messageArea;
	private JComboBox challengeSelector;
	private String[] gameNames;
	private Client myClient;
	
	/**
		@param owner The client who created the dialog box
		@param target The name of the player that the IM or challenge is being sent to.
	*/
	public MessageDialog(Client owner, String target)
	 	{
		super(owner, "Send IM or Challenge to " + target, true);
		
		myClient = owner;
		targetName = target;
		userName = owner.getUser();
		myServer = owner.getServer();
				
		messageArea = new JTextArea(3, 50);
		messageArea.setLineWrap(true);
		messageArea.setEditable(true);
		
		challengeSelector = new JComboBox();
		challengeSelector.addItem("none");
		try
			{
			gameNames = myServer.getGameNames();
			for ( int n = 0; n < gameNames.length; n++ )
				{
				challengeSelector.addItem(Client.fixGameName(gameNames[n]));
				}
			}
		catch ( RemoteException problem )
			{
			JOptionPane.showMessageDialog(this, "Action aborted - unable to reach server!", "Warning", JOptionPane.WARNING_MESSAGE);
			setVisible(false);				
			return;
			}
		if ( myClient.isInGame() )
			challengeSelector.setEnabled(false);

		JButton sendButton = new JButton("Send");
		sendButton.addActionListener( new ActionListener()
			{
			public void actionPerformed(ActionEvent event)
				{
				String selectedGame;
				
				int selectedIndex = challengeSelector.getSelectedIndex();
				if ( selectedIndex <= 0 )
					selectedGame = new String("none");
				else
					selectedGame = gameNames[selectedIndex-1];
				
				String messageText = messageArea.getText();
				
				if ( selectedGame.equals("none") && messageText.length() < 1 )
					{
					MessageDialog.this.setVisible(false);
					return;
					}
				
				try
					{
					myServer.sendInstantMessage(userName, targetName, messageText, selectedGame);
					}
				catch ( RemoteException problem )
					{
					JOptionPane.showMessageDialog(MessageDialog.this, "Action aborted - unable to reach server!", "Warning", JOptionPane.WARNING_MESSAGE);
					}
					
				MessageDialog.this.setVisible(false);				
				}
			} );
			
		
		JPanel aPanel;
		Container myContent = getContentPane();
		myContent.setLayout( new BorderLayout(15, 15) );

		aPanel = new JPanel();
		aPanel.setLayout( new BorderLayout(8, 8) );
		aPanel.add(new JLabel("Enter message text, if any:", JLabel.CENTER), BorderLayout.NORTH);
		JScrollPane msgScroll = new JScrollPane(messageArea);
		msgScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		msgScroll.setPreferredSize(new Dimension(200, 50));
		aPanel.add(msgScroll, BorderLayout.CENTER);
		myContent.add(aPanel, BorderLayout.NORTH);
		
		aPanel = new JPanel();
		aPanel.add(new JLabel("Select game to challenge to, if any:", JLabel.CENTER));
		aPanel.add(challengeSelector);
		myContent.add(aPanel, BorderLayout.CENTER);

		aPanel = new JPanel();
		aPanel.add(sendButton);
		myContent.add(aPanel, BorderLayout.SOUTH);

		pack();
		show();
		}
		
	
		
	
}



