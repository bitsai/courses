package cs321.superscrabble;

import java.util.*;

/** This class oversees tile distribution, and takes care of producing fresh tiles for the rack. */
public class SPieces
{
	int[] spieces = new int[26];
	Random generator = new Random();

/** Default constructor. */
	public SPieces()
	{
	}

/** Cloning constructor. */
	public SPieces(int[] inpieces)
	{
		spieces = inpieces;
	}

/** Returns array of tiles distribution for cloning. */
	public int[] getPieces()
	{
		int[] temp = (int[])spieces.clone();
		return(temp);
	}

/** Sets up initial tile distribution. */
	public void initialize()
	{
		spieces[0] = 9;
		spieces[1] = 2;
		spieces[2] = 2;
		spieces[3] = 4;
		spieces[4] = 12;
		spieces[5] = 2;
		spieces[6] = 3;
		spieces[7] = 2;
		spieces[8] = 9;

		spieces[9] = 1;
		spieces[10] = 1;
		spieces[11] = 4;
		spieces[12] = 2;
		spieces[13] = 6;
		spieces[14] = 8;
		spieces[15] = 2;
		spieces[16] = 1;
		spieces[17] = 6;

		spieces[18] = 4;
		spieces[19] = 6;
		spieces[20] = 4;
		spieces[21] = 2;
		spieces[22] = 2;
		spieces[23] = 1;
		spieces[24] = 2;
		spieces[25] = 1;
	}

/** Returns true if bag is empty. */
	public boolean isEmpty()
	{
		for (int count = 0; count < 26; count++)
		{
			if (spieces[count] > 0)
			{
				return(false);
			}
		}
		return(true);
	}

/** Returns new tile if there are any remaining; otherwise throws IllegalMove (Empty Bag) exception. */
	public char draw() throws IllegalMove
	{
		int i = 0;
		int r = 0;
		char c = ' ';
		boolean pieceExists = false;

		if (isEmpty() == true)
		{
			IllegalMove e = new IllegalMove("All pieces/tiles played!");
			throw e;
		}

		while(pieceExists == false)
		{
			r = generator.nextInt(98);

			if ((r >= 0) && (r <= 8))
			{
				i = 0;
				c = 'A';
			}
			else if ((r >= 9) && (r <= 10))
			{
				i = 1;
				c = 'B';
			}
			else if ((r >= 11) && (r <= 12))
			{
				i = 2;
				c = 'C';
			}
			else if ((r >= 13) && (r <= 16))
			{
				i = 3;
				c = 'D';
			}
			else if ((r >= 17) && (r <= 28))
			{
				i = 4;
				c = 'E';
			}
			else if ((r >= 29) && (r <= 30))
			{
				i = 5;
				c = 'F';
			}
			else if ((r >= 31) && (r <= 33))
			{
				i = 6;
				c = 'G';
			}
			else if ((r >= 34) && (r <= 35))
			{
				i = 7;
				c = 'H';
			}
			else if ((r >= 36) && (r <= 44))
			{
				i = 8;
				c = 'I';
			}

			else if (r == 45)
			{
				i = 9;
				c = 'J';
			}
			else if (r == 46)
			{
				i = 10;
				c = 'K';
			}
			else if ((r >= 47) && (r <= 50))
			{
				i = 11;
				c = 'L';
			}
			else if ((r >= 51) && (r <= 52))
			{
				i = 12;
				c = 'M';
			}
			else if ((r >= 53) && (r <= 58))
			{
				i = 13;
				c = 'N';
			}
			else if ((r >= 59) && (r <= 66))
			{
				i = 14;
				c = 'O';
			}
			else if ((r >= 67) && (r <= 68))
			{
				i = 15;
				c = 'P';
			}
			else if (r == 69)
			{
				i = 16;
				c = 'Q';
			}
			else if ((r >= 70) && (r <= 75))
			{
				i = 17;
				c = 'R';
			}

			else if ((r >= 76) && (r <= 79))
			{
				i = 18;
				c = 'S';
			}
			else if ((r >= 80) && (r <= 85))
			{
				i = 19;
				c = 'T';
			}
			else if ((r >= 86) && (r <= 89))
			{
				i = 20;
				c = 'U';
			}
			else if ((r >= 90) && (r <= 91))
			{
				i = 21;
				c = 'V';
			}
			else if ((r >= 92) && (r <= 93))
			{
				i = 22;
				c = 'W';
			}
			else if (r == 94)
			{
				i = 23;
				c = 'X';
			}
			else if ((r >= 95) && (r <= 96))
			{
				i = 24;
				c = 'Y';
			}
			else if (r == 97)
			{
				i = 25;
				c = 'Z';
			}

			if (spieces[i] > 0)
			{
				pieceExists = true;
			}
		}

		spieces[i] = spieces[i] - 1;
		return(c);
	}
}