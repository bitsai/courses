package battletech;

import javax.swing.*;
import java.util.Vector;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

/** This class represents the pop-up dialog box that allows players to choose a map and a Mech. */

public class MechDialog extends JDialog
{
	/** The list of Mech choices. */
	public JComboBox mechNameList;
	/** The list of map choices. */
	public JComboBox mapNameList;

	public MechDialog(JFrame owner)
	{
		super(owner, "Select a Mech", true);
		Container myContent = getContentPane();

		// initially mechnames is empty
		Vector mechnames = new Vector();
		Vector mapnames = new Vector();
		mechnames.add("BattleMaster");
		mapnames.add("Map1");

		//try to load from database
		try
		{
        	mechnames = MechMaker.getMechNames();

		}
		catch (Exception e)
		{
			//load defaults
			System.out.println("Error loading mechs from database:");
			System.out.println("Default mechs loaded");
			System.out.println(e.getMessage());
			e.printStackTrace();
		}

		try
		{
			mapnames = MechMaker.loadMapNames();

		}
		catch (Exception e)
		{
			//load defaults
			System.out.println("Error loading maps from database:");
			System.out.println("Default maps loaded");
			System.out.println(e.getMessage());
			e.printStackTrace();
		}


		JLabel label = new JLabel("Select a mech");
		JLabel label2 = new JLabel("Select a map");

        // Create the combo box, select the mech
        mechNameList = new JComboBox(mechnames);
        mapNameList = new JComboBox(mapnames);

        JButton okbutton = new JButton("OK");
        okbutton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
             MechDialog.this.setVisible(false);
            }
        });

        //myContent.setLayout(new BorderLayout());
        JPanel p1 = new JPanel();
        JPanel p2 = new JPanel();

        p1.add(label);
        p1.add(mechNameList);
        p2.add(label2);
        p2.add(mapNameList);

        myContent.add(p1,BorderLayout.EAST);
        myContent.add(p2,BorderLayout.WEST);
        myContent.add(okbutton,BorderLayout.SOUTH);
        pack();
		show();
	}

	/** Returns the name of the chosen Mech. */
	public String getMechName()
	{
		String name = (String) mechNameList.getSelectedItem();
		return name;
	}

	/** Returns the name of the chosen map. */
	public String getMapName()
	{
		String name = (String) mapNameList.getSelectedItem();
		return name;
	}

	public static void main(String s[])
	{
		JFrame frame = new JFrame("Dialog Screen");
		frame.setSize(650,500);
		//frame.setMinimumSize(new java.awt.Dimension(650,500));

		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {System.exit(0);}
		});

		//frame.pack();
		frame.setVisible(true);

		MechDialog dialog = new MechDialog(null);
		try
		{
		BattleTechRemote game=new BattleTech();
		String b = dialog.getMechName();
		MechMaker.loadMech(b,game);
		}
		catch( Exception e)
		{
			System.out.println("Exception");
			e.getMessage();
			e.printStackTrace();
		}
		System.out.println("DONE");
    }
}