package cs321.superscrabble;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/** This class oversees display of the Scrabble board. */
public class SBoardSwing extends JPanel
{
	int boxsize = 20;
	char[][] board = new char[15][15];

	Dimension minimumsize = new Dimension(16*boxsize + 1, 16*boxsize + 1);
	Dimension maximumsize = new Dimension(16*boxsize + 1, 16*boxsize + 1);
	Dimension preferredsize = new Dimension(16*boxsize + 1, 16*boxsize + 1);

/** Sets preferred size at 16 * boxsize (preset class variable) + 1 (for proper display of boundaries). */
	public Dimension getPreferredSize()
	{
		return(preferredsize);
	}

/** Sets maximum size at 16 * boxsize (preset class variable) + 1 (for proper display of boundaries). */
	public Dimension getMaximumSize()
	{
		return(maximumsize);
	}

/** Sets minimum size at 16 * boxsize (preset class variable) + 1 (for proper display of boundaries). */
	public Dimension getMinimumSize()
	{
		return(minimumsize);
	}

/** Updates the class's own 2-D array of tiles according to the input. */
	public void setBoard(char[][] in)
	{
		for (int count = 0; count < 15; count++)
		{
			for (int count2 = 0; count2 < 15; count2++)
			{
				board[count][count2] = in[count][count2];
			}
		}
	}

/** Display method, draws tiles, lines, row and column labels. */
	public void paintComponent(Graphics g)
	{
		int textx;
		int texty;
		int numx;
		int numy;

		int number = 0;
		String text = "ABCDEFGHIJKLMNO";
		String tile;

		super.paintComponent(g);

		for (int count = 1; count < 16; count++)
		{
			if (count < 10)
			{
				numx = 6;
			}
			else
			{
				numx = 3;
			}
			numy = count*boxsize + 14;
			textx = count*boxsize + 6;
			texty = 14;

			String letter = String.valueOf(text.charAt(0));

			text = text.substring(1);
			g.drawString(letter, textx, texty);

			number++;
			g.drawString(String.valueOf(number), numx, numy);

			g.drawLine(0, count*boxsize, 16*boxsize, count*boxsize);
			g.drawLine(count*boxsize, 0, count*boxsize, 16*boxsize);

			for (int count2 = 1; count2 < 16; count2++)
			{
				tile = String.valueOf(board[count2-1][count-1]);
				if (tile.hashCode() != 0)
				{
					g.drawString(tile, count*boxsize+6, count2*boxsize+14);
				}
			}
		}

		g.drawLine(0, 0, 16*boxsize, 0);
		g.drawLine(0, 0, 0, 16*boxsize);
		g.drawLine(0, 16*boxsize, 16*boxsize, 16*boxsize);
		g.drawLine(16*boxsize, 0, 16*boxsize, 16*boxsize);
	}
}