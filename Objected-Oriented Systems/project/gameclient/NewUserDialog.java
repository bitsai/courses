package gameclient;


import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.rmi.*;
import java.rmi.server.*;
import gameserver.RemoteGameServer;


/**
	A simple dialog box class that obtains a user name, password, and host name to create a new account for the user.
*/
public class NewUserDialog extends JDialog
{
	private JTextField userName;
	private JTextField hostName;
	private JPasswordField password1, password2;
	private boolean success;
	private RemoteGameServer myServer;

	/** 
		Takes as a parameter the log-on dialog box that created this new user dialog box.  
		The NewUserDialog auto-sizes itself to exactly cover the LogOnDialog, for a sleek display.
	*/
	public NewUserDialog(LogOnDialog owner)
		{
		super(owner, "Enter Account Information", true);

		success = false;
		myServer = null;

		JPanel inputPanel = new JPanel();
		inputPanel.setLayout( new GridLayout(4, 2) );
		inputPanel.add( new JLabel("Select User name:") );
		inputPanel.add( userName = new JTextField("", 12) );
		inputPanel.add( new JLabel("Select Password:") );
		inputPanel.add( password1 = new JPasswordField("", 12) );
		inputPanel.add( new JLabel("Re-enter Password:") );
		inputPanel.add( password2 = new JPasswordField("", 12) );
		inputPanel.add( new JLabel("Server:") );
		inputPanel.add( hostName = new JTextField(owner.getHostName(), 12) );
		
		JButton okButton = new JButton("Create Account");
		JPanel buttonPanel = new JPanel();
		buttonPanel.add(okButton);
		okButton.addActionListener( new ActionListener()
			{
			public void actionPerformed(ActionEvent event)
				{
				createAccount();		
				}
			} );
			
		JPanel titlePanel = new JPanel();
		titlePanel.add( new JLabel("Please fill out the following...") );
		
		Box vertBox = Box.createVerticalBox();
		vertBox.add(Box.createVerticalStrut(10));
		vertBox.add(titlePanel);
		vertBox.add(Box.createVerticalStrut(10));
		vertBox.add(inputPanel);
		vertBox.add(Box.createVerticalStrut(10));
		vertBox.add(buttonPanel);
		vertBox.add(Box.createVerticalStrut(10));
		
		getContentPane().add(vertBox);
		setSize( owner.getSize(null) );
		show();
		}
		
	
	/** Returns true if the account was created successfully, false otherwise */
	public boolean wasSuccessful()
		{
		return success;
		}
		

	/** Gets the user's name, as inputed */
	String getUserName()
		{
		if ( success)
			return userName.getText();
		else
			return null;
		}
		
	/** If successfully connected and created account, returns a reference to the remote game server. */
	RemoteGameServer getServer()
		{
		if ( success )
			return myServer;
		else
			return null;
		}

	
	/** Attempts to create a new account on the remote server. */
	public void createAccount()
		{
		String pass1 = new String( password1.getPassword() );
		String pass2 = new String( password2.getPassword() );
				
		// If two password fields do not match, notify user.
		if ( !pass1.equals(pass2) )
			{
			JOptionPane.showMessageDialog(this, "Password fields do not match!", "Problem", JOptionPane.WARNING_MESSAGE);
			return;
			}
			
		// If any information not provided, notify user.
		if ( pass1.length() < 1 || userName.getText().length() < 1 || hostName.getText().length() < 1 )
			{
			JOptionPane.showMessageDialog(this, "Please supply a user name, password, and host name!", "Problem", JOptionPane.WARNING_MESSAGE);
			return;
			}

		// Generate a proper URL to the remote server.
		String url = hostName.getText();
		if ( !url.startsWith("rmi://") )
			url = "rmi://" + url;
		if ( url.charAt( url.length()-1 ) != '/' )
			url += "/";
					
		try
			{
			myServer = (RemoteGameServer)Naming.lookup(url + "gameserver");
			success = myServer.createPlayer( userName.getText(), pass1 );

			if ( success )					
				NewUserDialog.this.setVisible(false);
			else
				JOptionPane.showMessageDialog(this, "That user name is already in use.", "Problem", JOptionPane.WARNING_MESSAGE);
			}
		catch ( Exception e )
			{
			JOptionPane.showMessageDialog(NewUserDialog.this, "Unable to connect to server.", "Error", JOptionPane.ERROR_MESSAGE);
			}
			
		
		}

}