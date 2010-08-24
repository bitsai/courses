package gameclient;


import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import gameserver.GameMessage;

/** 
	Panel class for handling the user list.	 This panel displays all on-line users, along with what
	game module the player is currently in.  Users can be selected, in order to challenge or send
	instant messages to them.
*/
public class UserListPanel extends JPanel
{
	Box layoutBox;
	ButtonGroup radioGroup;
	JRadioButton[] userButtons;
	int currentSelection;
	String[] userNameList;
	
	/** Takes a string with a user-list in string form, as provided by the server. */
	public UserListPanel(String[] info)
		{
		setBackground(Color.WHITE);
		currentSelection = 0;
		
		radioGroup = new ButtonGroup();
		
		JLabel titleLabel = new JLabel("PLAYERS ON-LINE:");
		titleLabel.setForeground(new Color(85, 85, 255));
		titleLabel.setBackground(Color.BLACK);
		
		layoutBox = Box.createVerticalBox();
		layoutBox.add( Box.createVerticalStrut(10) );
		layoutBox.add( titleLabel );
		layoutBox.add( Box.createVerticalStrut(20) );

		if ( info == null )
			return;
		
		// Create new buttons for the new player list entries.
		userButtons = new JRadioButton[ info.length / 2 ];
		userNameList = new String[ info.length / 2 ];
		for ( int n = 0; n < userButtons.length; n++ )
			{
			addButton(n, info[n*2] + "   [" + info[n*2+1] + "]");
			userNameList[n] = info[n*2];
			}
		
		add(layoutBox);
		}

	
	/** Returns the currently-selected user for IMs/challenges */
	public String getSelection()
		{
		return userNameList[currentSelection];		
		}
	

	private void addButton(final int index, String textLabel)
		{
		userButtons[index] = new JRadioButton( textLabel, (index==0) );
		userButtons[index].setBackground(Color.WHITE);
		radioGroup.add(userButtons[index]);
		layoutBox.add(userButtons[index]);
		
		userButtons[index].addActionListener( new ActionListener()
			{
			public void actionPerformed(ActionEvent evt)
				{
				currentSelection = index;
				}
			} );
		}
}
