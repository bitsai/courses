package cs321.superscrabble;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/** This class oversees initialization and placement of all GUI components. */
public class ScrabbleFrame extends JFrame
{
	Scrabble aScrabble;

	SBoardSwing boardswing;

	JLabel rackswing;
	JLabel score;
	JLabel msgs;

/** Constructor method, initializes and places all GUI components.  Also takes in Scrabble reference so that the display components have something to synchronize with. */
	public ScrabbleFrame(Scrabble in)
	{
		aScrabble = in;
		setTitle("SuperScrabble");

		boardswing = new SBoardSwing();

		rackswing = new JLabel("Current Racked Pieces: " + aScrabble.getRack());
		score = new JLabel("Current Score: " + aScrabble.currentScore());
		msgs = new JLabel("Current Status:");

		JButton makemove = new JButton("Make Move");
		JButton newgame = new JButton("New Game");
		JButton quit = new JButton("Quit");

		ScrabbleListener sl = new ScrabbleListener(this, aScrabble);

		makemove.addActionListener(sl);
		newgame.addActionListener(sl);
		quit.addActionListener(sl);

		JPanel display = new JPanel();
		display.setLayout(new BoxLayout(display, BoxLayout.Y_AXIS));
		display.add(rackswing);
		display.add(score);
		display.add(msgs);

		JPanel controls = new JPanel();
		controls.setLayout(new BoxLayout(controls, BoxLayout.X_AXIS));
		controls.add(newgame);
		controls.add(makemove);
		controls.add(quit);

		Container contentpane = getContentPane();

		contentpane.add(boardswing, BorderLayout.NORTH);
		contentpane.add(display, BorderLayout.CENTER);
		contentpane.add(controls, BorderLayout.SOUTH);

		setResizable(false);
		pack();
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

/** Updates all display components according to Scrabble object's state. */
	public void update()
	{
		boardswing.setBoard(aScrabble.getBoard());
		boardswing.repaint();
		rackswing.setText("Current Racked Pieces: " + aScrabble.getRack());
		score.setText("Current Score: " + aScrabble.currentScore());
		msgs.setText("Current Status: ");
	}

/** Displays input string as current status. */
	public void display(String in)
	{
		msgs.setText("Current Status: " + in);
	}
}