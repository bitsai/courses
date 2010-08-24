package cs321.superscrabble;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/** This class oversees the functionality of the GUI buttons. */
public class ScrabbleListener implements ActionListener
{
	ScrabbleFrame owner;
	Scrabble aScrabble;

/** Default constructor, takes in ScrabbleFrame and Scrabble object references for control purposes. */
	public ScrabbleListener(ScrabbleFrame in1, Scrabble in2)
	{
		owner = in1;
		aScrabble = in2;
	}

/** Quits, or calls the newGame or makeMove methods according to what ActionEvent triggered this method. */
	public void actionPerformed(ActionEvent e)
	{
		String button = e.getActionCommand();

		if (button == "Quit")
		{
			System.exit(0);
		}

		if (button == "New Game")
		{
			newGame();
		}

		if (button == "Make Move")
		{
			makeMove();
		}
	}

/** Initializes Scrabble object, and updates ScrabbleFrame object.  IllegalMove errors are propagated to here and handled (error message displayed as current status).  */
	public void newGame()
	{
		try
		{
			aScrabble.initialize();
			owner.update();
		}
		catch (IllegalMove i)
		{
			String error = i.toString().substring(32);
			owner.display(error);
		}
	}

/** Displays MakeMove dialog window, and sends input string to Scrabble object's takeTurn method, then updates ScrabbleFrame object.  IllegalMove errors are propagated to here and handled (error message displayed as current status). */
	public void makeMove()
	{
		String input;
		input = JOptionPane.showInputDialog("Make Move");

		try
		{
			aScrabble.takeTurn(input);
			owner.update();
		}
		catch (IllegalMove i)
		{
			String error = i.toString().substring(32);
			owner.display(error);
		}
		catch (NullPointerException n)
		{
			owner.display("Move Canceled!");			
		}
	}
}