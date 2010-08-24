package cs321.superscrabble;

/** This class is responsible for tracking tile positions on the board. */
public class SBoard
{
	char[][] sboard = new char[15][15];

/** Default constructor. */
	public SBoard()
	{
	}

/** Cloning constructor. */
	public SBoard(char[][] inboard)
	{
		sboard = inboard;
	}

/** Returns 2-D array of tiles for cloning. */
	public char[][] getBoard()
	{
		char[][] temp = new char[15][15];
		for (int count = 0; count < 15; count++)
		{
			for (int count2 = 0; count2 < 15; count2++)
			{
				temp[count][count2] = sboard[count][count2];
			}
		}
		return(temp);
	}

/** Returns tile at a given position on the board. */
	public String getPiece(int y, int x)
	{
		char piece = sboard[y][x];
		return(String.valueOf(piece));
	}

/** Returns true if a given tile exists at given position, otherwise returns false. */
	public boolean hasPiece(int y, int x, char c)
	{
		Character boardpiece = new Character(sboard[y][x]);
		Character piece = new Character(c);
		return(boardpiece.equals(piece));
	}

/** Updates board; depending on direction input, calls on either addDown or addRight method to actually place word's tiles on board. */
	public void update(int y, int x, String direction, String word)
	{
		if (direction.equals("DOWN"))
		{
			addDown(y, x, word);
		}
		else if (direction.equals("RIGHT"))
		{
			addRight(y, x, word);
		}
	}

/** Iteratively places a given word's tiles on the board, from top to bottom. */
	public void addDown(int y, int x, String word)
	{
		sboard[y][x] = word.charAt(0);
		if (word.length() > 1)
		{
			String newword = word.substring(1);
			addDown(y+1, x, newword);
		}
	}

/** Iteratively places a given word's tiles on the board, from left to right. */
	public void addRight(int y, int x, String word)
	{
		sboard[y][x] = word.charAt(0);
		if (word.length() > 1)
		{
			String newword = word.substring(1);
			addRight(y, x+1, newword);
		}
	}
}