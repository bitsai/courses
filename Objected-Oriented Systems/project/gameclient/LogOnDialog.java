package gameclient;


import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.rmi.*;
import java.rmi.server.*;
import gameserver.RemoteGameServer;


/**
	A Dialog Box class for obtaining log-on information from the user.  Logs on to the remote server when data is
	submitted.
*/
public class LogOnDialog extends JDialog
{
	JTextField userNameField;
	JTextField hostNameField;
	JPasswordField passwordField;
	private String userName;
	private RemoteGameServer myServer;
	
	public LogOnDialog(JFrame owner)
	 	{
		super(owner, "Enter Log-In Information", true);
		
		userName = null;
		myServer = null;

		// If this window is manually closed, abort program
		addWindowListener( new WindowAdapter()
			{
			public void windowClosing(WindowEvent e)
				{
				System.exit(0);
				}
			} );


		JButton okButton = new JButton("  Log On  ");
		okButton.addActionListener( new ActionListener()
			{
			public void actionPerformed(ActionEvent event)
				{
				tryLogOn();
				}
			} );
		JPanel okPanel = new JPanel();
		okPanel.add(okButton);

		JButton newUserButton = new JButton("Create New Account");
		newUserButton.addActionListener( new ActionListener()
			{
			public void actionPerformed(ActionEvent event)
				{
				NewUserDialog newUser = new NewUserDialog(LogOnDialog.this);
				if ( newUser.wasSuccessful() )
					{
					userName = newUser.getUserName();
					myServer = newUser.getServer();
					LogOnDialog.this.setVisible(false);
					}
				}
			} );

		JPanel leftPanel = new JPanel();
		leftPanel.setLayout( new GridLayout(3, 1) );
		leftPanel.add( new JLabel("User name:   ") );
		leftPanel.add( new JLabel("Password:   ") );
		leftPanel.add( new JLabel("Server:   ") );
		
		JPanel rightPanel = new JPanel();
		rightPanel.setLayout( new GridLayout(3, 1) );
		rightPanel.add( userNameField = new JTextField("", 10) );
		rightPanel.add( passwordField = new JPasswordField("", 10) );
		rightPanel.add( hostNameField = new JTextField("rmi://localhost", 10) );
		
		JPanel titlePanel = new JPanel();
		JLabel titleLabel = new JLabel("If you already have have an account:");
		titleLabel.setForeground(Color.BLACK);
		titlePanel.add(titleLabel);

		JPanel inputPanel = new JPanel();
		inputPanel.setLayout( new BorderLayout(5, 5) );
		inputPanel.add(leftPanel, BorderLayout.WEST);
		inputPanel.add(rightPanel, BorderLayout.EAST);
		inputPanel.add(okPanel, BorderLayout.SOUTH);
		inputPanel.add(titlePanel, BorderLayout.NORTH);


		JPanel accountPanel = new JPanel();
		JLabel accountText = new JLabel("Need an account?  ");
		accountText.setForeground(Color.BLUE);
		accountPanel.add(accountText);
		accountPanel.add(newUserButton);
	
		getContentPane().setLayout( new BorderLayout(25, 25) );
		getContentPane().add(inputPanel, BorderLayout.CENTER);
		getContentPane().add(accountPanel, BorderLayout.SOUTH);
		
		pack();
		show();
		}
		
	
	/** Returns the host name the user has entered */
	public String getHostName()
		{
		return hostNameField.getText();
		}
		
		
	/** Returns the user name the user has entered */
	public String getUser()
		{
		return userName;
		}
		
	/** Returns a reference to the remote game server. */
	public RemoteGameServer getServer()
		{
		return myServer;
		}
		
		
	/** Attempts to log on to the game server. */
	public void tryLogOn()
		{
		String pass = new String( passwordField.getPassword() );
				
		// If any information not provided, notify user.
		if ( pass.length() < 1 || userNameField.getText().length() < 1 || hostNameField.getText().length() < 1 )
			{
			JOptionPane.showMessageDialog(this, "Please supply a user name, password, and host name!", "Problem", JOptionPane.WARNING_MESSAGE);
			return;
			}

		// Generate a proper URL to the remote server.
		String url = hostNameField.getText();
		if ( !url.startsWith("rmi://") )
			url = "rmi://" + url;
		if ( url.charAt( url.length()-1 ) != '/' )
			url += "/";
					
		try
			{
			myServer = (RemoteGameServer)Naming.lookup(url + "gameserver");
			String result = myServer.logIn( userNameField.getText(), pass );

			if ( result.equals( RemoteGameServer.SUCCESS ) )
				{
				userName = userNameField.getText();
				setVisible(false);
				}

			else
				JOptionPane.showMessageDialog(this, result, "Problem", JOptionPane.WARNING_MESSAGE);
			}
			
		catch ( Exception e )
			{
			JOptionPane.showMessageDialog(this, "Unable to connect to server.", "Error", JOptionPane.ERROR_MESSAGE);
			}
		}
	
}



