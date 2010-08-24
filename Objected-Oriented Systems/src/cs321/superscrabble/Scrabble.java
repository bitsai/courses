package cs321.superscrabble;

import java.util.*;

/** This class oversees propagation of errors. */
class IllegalMove extends Exception
{
	IllegalMove(String message)
	{
		super (message);
	}
}

/** This class oversees implementation of various Scrabble rules. */
public class Scrabble
{
	HashSet words = new HashSet();	
	Stack newsquares = new Stack();

	SBoard board;
	SPieces pieces;
	SRack rack;
	int score;
	int[][] squaresused;

	SBoard tempboard = new SBoard();
	SPieces temppieces = new SPieces();
	SRack temprack = new SRack();

	int tempscore = 0;
	int[][] tempsquaresused = new int[15][15];

/** Adds input word to Scrabble's HashSet of legal words. */
	public void addWord(String word)
	{
		words.add(word);
	}

/** Sets up board, tile bag, rack, score, and array of used positions for new game. */
	public void initialize() throws IllegalMove
	{
		board = new SBoard();
		pieces = new SPieces();
		pieces.initialize();
		rack = new SRack();
		score = 0;
		squaresused = new int[15][15];
		squaresused[7][7] = 1;

		for(int count = 0; count < 7; count++)
		{
			rack.insertPiece(count, pieces.draw());
		}
	}

/** Reinitializes temporary board, tile bag, rack, score, and array of used positions to last saved values. */
	public void reset()
	{
		newsquares = new Stack();

		tempboard = new SBoard(board.getBoard());
		temppieces = new SPieces(pieces.getPieces());
		temprack = new SRack(rack.getTiles());
		tempscore = score;

		for (int count = 0; count < 15; count++)
		{
			for (int count2 = 0; count2 < 15; count2++)
			{
				tempsquaresused[count][count2] = squaresused[count][count2];
			}
		}
	}

/** Saves temporary board, tile bag, rack, score, and array of used positions values. */
	public void update()
	{
		board = tempboard;
		pieces = temppieces;
		rack = temprack;
		score = tempscore;
		squaresused = tempsquaresused;
	}

/** Returns true if position input is out of bounds. */
	public boolean outBounds(int y, int x)
	{
		if ((y == -1) || (y == 15) || (x == -1) || (x == 15))
		{
			return(true);
		}
		return(false);
	}

/** Takes input string, breaks it up into 3 tokens (square, direction, and word) for processing by Parse() method, and calls for update of GUI components.  Any errors are caught and propagated upwards. */
	public void takeTurn(String input) throws IllegalMove
	{
		StringTokenizer st = new StringTokenizer(input);
		String square = "";
		String direction = "";
		String word = "";

		try
		{
			square = st.nextToken();
			direction = st.nextToken();
			word = st.nextToken();

			reset();

			parse(square.toUpperCase(), direction.toUpperCase(), word.toUpperCase());

			update();
		}
		catch (IllegalMove e)
		{
			reset();
			throw e;
		}
		catch (NoSuchElementException n)
		{
			IllegalMove e = new IllegalMove("Incorrect input!");
			throw e;
		}
		catch (NumberFormatException n)
		{
			IllegalMove e = new IllegalMove("Incorrect input!");
			throw e;
		}
	}

/** Translates input square string to numerical horizontal and vertical board positions, sends resulting values along with direction and word to Process() method. */
	public void parse(String square, String direction, String word) throws IllegalMove
	{
		char leftrightpos = square.charAt(0);
		String updownpos = square.substring(1);
		int x = 0;
		int y = new Integer(updownpos).intValue() - 1;

		if ((y < 0) || (y > 14))
		{
			IllegalMove e = new IllegalMove("Vertical move was incorrect!");
			throw e;
		}

		switch (leftrightpos)
		{
			case 'A':
			x = 0;
			break;
			case 'B':
			x = 1;
			break;
			case 'C':
			x = 2;
			break;
			case 'D':
			x = 3;
			break;
			case 'E':
			x = 4;
			break;
			case 'F':
			x = 5;
			break;
			case 'G':
			x = 6;
			break;
			case 'H':
			x = 7;
			break;
			case 'I':
			x = 8;
			break;
			case 'J':
			x = 9;
			break;
			case 'K':
			x = 10;
			break;
			case 'L':
			x = 11;
			break;
			case 'M':
			x = 12;
			break;
			case 'N':
			x = 13;
			break;
			case 'O':
			x = 14;
			break;
			default:
			IllegalMove e = new IllegalMove("Horizontal move was incorrect!");
			throw e;
		}

		process(y, x, direction, word);
	}

/** Sends input y, x, direction, and word values to various methods implementing Scrabble rule checks (checkTile(), findPieces(), validNewWords()).  Also checks boundaries, whether new word is legal, whether direction is legal, and whether new tiles are used. */
	public void process(int y, int x, String direction, String word) throws IllegalMove
	{
		if ((direction.equals("DOWN") == false) && (direction.equals("RIGHT") == false))
		{
			IllegalMove e = new IllegalMove("Invalid move direction!");
			throw e;
		}

		if ((direction.equals("DOWN") && (y + word.length()) > 15))
		{
			IllegalMove e = new IllegalMove("Move was out of bounds!");
			throw e;
		}

		if ((direction.equals("RIGHT") && (x + word.length()) > 15))
		{
			IllegalMove e = new IllegalMove("Move was out of bounds!");
			throw e;
		}

		if (words.contains(word.toLowerCase()) == false)
		{
			IllegalMove e = new IllegalMove("Illegal word input!");
			throw e;
		}

		if (checkTile(y, x, direction, word.length()) == false)
		{
			IllegalMove e = new IllegalMove("Incontinuous move!");
			throw e;
		}

		if (findPieces(y, x, direction, word) == false)
		{
			IllegalMove e = new IllegalMove("Pieces were not found!");
			throw e;
		}

		if (newsquares.empty() == true)
		{
			IllegalMove e = new IllegalMove("No new piece used!");
			throw e;
		}

		tempboard.update(y, x, direction, word);

		if (validNewWords(direction) == false)
		{
			IllegalMove e = new IllegalMove("Illegal words formed!");
			throw e;
		}
	}

/** Checks to see if new words formed are contiguous with existing tiles, by calling checkTileDown() or checkTileRight() methods according to input direction. */
	public boolean checkTile(int y, int x, String direction, int length)
	{
		if (direction.equals("DOWN"))
		{
			return(checkTileDown(y, x, length));
		}
		return(checkTileRight(y, x, length));
	}

/** Checks to see if new words formed vertically are contiguous with existing tiles. */
	public boolean checkTileDown(int y, int x, int length)
	{
		if (outBounds(y, x) == true)
		{
			return(false);
		}
		if (tempsquaresused[y][x] == 1)
		{
			return(true);
		}
		if (length >= 0)
		{
			return(checkTileDown(y+1, x, length-1));
		}
		return(false);
	}

/** Checks to see if new words formed horizontally are contiguous with existing tiles. */
	public boolean checkTileRight(int y, int x, int length)
	{
		if (outBounds(y, x) == true)
		{
			return(false);
		}
		if (tempsquaresused[y][x] == 1)
		{
			return(true);
		}
		if (length >= 0)
		{
			return(checkTileRight(y, x+1, length-1));
		}
		return(false);
	}

/** Checks to see if tiles used already exist on the board, or are already racked, by calling findPiecesDown() or findPiecesRight() methods according to input direction. */
	public boolean findPieces(int y, int x, String direction, String word) throws IllegalMove
	{
		if (direction.equals("DOWN"))
		{
			return(findPiecesDown(y, x, word));
		}
		return(findPiecesRight(y, x, word));
	}

/** Checks to see if tiles used vertically already exist on the board, or are already racked.  If a rack tile is used, replace it by drawing from the tile bag. */
	public boolean findPiecesDown(int y, int x, String word) throws IllegalMove
	{
		char c = word.charAt(0);

		if (tempboard.hasPiece(y, x, c) == true)
		{
			tempsquaresused[y][x] = 1;
			if (word.length() == 1)
			{
				return(true);
			}
			String newword = word.substring(1);
			return(findPiecesDown(y+1, x, newword));
		}

		for (int count = 0; count < 7; count++)
		{
			if (temprack.hasPiece(count, c) == true)
			{
				String newsquare = y + " " + x;
				newsquares.push(newsquare);

				temprack.insertPiece(count, temppieces.draw());
				tempsquaresused[y][x] = 1;
				if (word.length() == 1)
				{
					return(true);
				}
				String newword = word.substring(1);
				return(findPiecesDown(y+1, x, newword));
			}
		}

		return(false);
	}

/** Checks to see if tiles used horizontally already exist on the board, or are already racked.  If a rack tile is used, replace it by drawing from the tile bag. */
	public boolean findPiecesRight(int y, int x, String word) throws IllegalMove
	{
		char c = word.charAt(0);

		if (tempboard.hasPiece(y, x, c) == true)
		{
			tempsquaresused[y][x] = 1;
			if (word.length() == 1)
			{
				return(true);
			}
			String newword = word.substring(1);
			return(findPiecesRight(y, x+1, newword));
		}

		for (int count = 0; count < 7; count++)
		{
			if (temprack.hasPiece(count, c) == true)
			{
				String newsquare = y + " " + x;
				newsquares.push(newsquare);

				temprack.insertPiece(count, temppieces.draw());
				tempsquaresused[y][x] = 1;
				if (word.length() == 1)
				{
					return(true);
				}
				String newword = word.substring(1);
				return(findPiecesRight(y, x+1, newword));
			}
		}

		return(false);
	}

/** Checks to see if new words formed on board are legal, by calling validNewWordsDown() or validNewWordsRight() methods according to input direction. */
	public boolean validNewWords(String direction)
	{
		boolean validnewwords = true;

		String newsquare = (newsquares.peek()).toString();
		StringTokenizer st = new StringTokenizer(newsquare);
		Integer newy = new Integer(st.nextToken());
		Integer newx = new Integer(st.nextToken());
		int y = newy.intValue();
		int x = newx.intValue();

		if (direction.equals("DOWN"))
		{
			if (validNewWordsDown(y, y, x, tempboard.getPiece(y, x)) == false)
			{
				validnewwords = false;
			}
			while ((validnewwords == true) && (newsquares.empty() == false))
			{
				newsquare = (newsquares.pop()).toString();
				st = new StringTokenizer(newsquare);
				newy = new Integer(st.nextToken());
				newx = new Integer(st.nextToken());
				y = newy.intValue();
				x = newx.intValue();

				if (validNewWordsRight(x, x, y, tempboard.getPiece(y, x)) == false)
				{
					validnewwords = false;
				}
			}
		}
		else if (direction.equals("RIGHT"))
		{
			if (validNewWordsRight(x, x, y, tempboard.getPiece(y, x)) == false)
			{
				validnewwords = false;
			}
			while ((validnewwords == true) && (newsquares.empty() == false))
			{
				newsquare = (newsquares.pop()).toString();
				st = new StringTokenizer(newsquare);
				newy = new Integer(st.nextToken());
				newx = new Integer(st.nextToken());
				y = newy.intValue();
				x = newx.intValue();

				if (validNewWordsDown(y, y, x, tempboard.getPiece(y, x)) == false)
				{
					validnewwords = false;
				}
			}
		}

		return(validnewwords);
	}

/** Checks to see if new words formed vertically on board are legal.  If new word is legal, calculate and add score to temporary score object. */
	public boolean validNewWordsDown(int up, int down, int hor, String word)
	{
		int u = 1;
		int d = 1;

		if (up != -1)
		{
			u = (tempboard.getPiece(up, hor)).hashCode();
		}

		if (down != 15)
		{
			d = (tempboard.getPiece(down, hor)).hashCode();
		}

		if (up == down)
		{
			if (((u == 0) || (u == -1)) && ((d == 0) || (down == 15)))
			{
				return(true);
			}
			else if ((u == 0) || (up == -1))
			{
				return(validNewWordsDown(up, down+1, hor, word));
			}
			else if ((d == 0) || (down == 15))
			{
				return(validNewWordsDown(up-1, down, hor, word));
			}
			return(validNewWordsDown(up-1, down+1, hor, word));
		}

		if (((u == 0) || (up == -1)) && ((d == 0) || (down == 15)))
		{
			if (word.length() == 1)
			{
				return(true);
			}
			else if (words.contains(word.toLowerCase()) == true)
			{
				calculateScore(word.toLowerCase());
				return(true);
			}
			return(false);
		}
		else if ((u == 0) || (up == -1))
		{
			word = word + tempboard.getPiece(down, hor);
			return(validNewWordsDown(up, down+1, hor, word));
		}
		else if ((d == 0) || (down == 15))
		{
			word = tempboard.getPiece(up, hor) + word;
			return(validNewWordsDown(up-1, down, hor, word));
		}
		word = tempboard.getPiece(up, hor) + word + tempboard.getPiece(down, hor);
		return(validNewWordsDown(up-1, down+1, hor, word));
	}

/** Checks to see if new words formed horizontally on board are legal.  If new word is legal, calculate and add score to temporary score object. */
	public boolean validNewWordsRight(int left, int right, int vert, String word)
	{
		int l = 1;
		int r = 1;

		if (left != -1)
		{
			l = (tempboard.getPiece(vert, left)).hashCode();
		}

		if (right != 15)
		{
			r = (tempboard.getPiece(vert, right)).hashCode();
		}

		if (left == right)
		{
			if (((l == 0) || (left == -1)) && ((r == 0) || (right == 15)))
			{
				return(true);
			}
			else if ((l == 0) || (left == -1))
			{
				return(validNewWordsRight(left, right+1, vert, word));
			}
			else if ((r == 0) || (right == 15))
			{
				return(validNewWordsRight(left-1, right, vert, word));
			}
			return(validNewWordsRight(left-1, right+1, vert, word));
		}

		if (((l == 0) || (left == -1)) && ((r == 0) || (right == 15)))
		{
			if (word.length() == 1)
			{
				return(true);
			}
			else if (words.contains(word.toLowerCase()) == true)
			{
				calculateScore(word.toLowerCase());
				return(true);
			}
			return(false);
		}
		else if ((l == 0) || (left == -1))
		{
			word = word + tempboard.getPiece(vert, right);
			return(validNewWordsRight(left, right+1, vert, word));
		}
		else if ((r == 0) || (right == 15))
		{
			word = tempboard.getPiece(vert, left) + word;
			return(validNewWordsRight(left-1, right, vert, word));
		}
		word = tempboard.getPiece(vert, left) + word + tempboard.getPiece(vert, right);
		return(validNewWordsRight(left-1, right+1, vert, word));
	}

/** Calculate score for input word and adds to temporary score object. */
	public void calculateScore(String word)
	{
		char c = word.charAt(0);
		switch(c)
		{
			case 'a':
			tempscore = tempscore + 1;
			break;
			case 'b':
			tempscore = tempscore + 3;
			break;
			case 'c':
			tempscore = tempscore + 3;
			break;
			case 'd':
			tempscore = tempscore + 2;
			break;
			case 'e':
			tempscore = tempscore + 1;
			break;
			case 'f':
			tempscore = tempscore + 4;
			break;
			case 'g':
			tempscore = tempscore + 2;
			break;
			case 'h':
			tempscore = tempscore + 4;
			break;
			case 'i':
			tempscore = tempscore + 1;
			break;

			case 'j':
			tempscore = tempscore + 8;
			break;
			case 'k':
			tempscore = tempscore + 5;
			break;
			case 'l':
			tempscore = tempscore + 1;
			break;
			case 'm':
			tempscore = tempscore + 3;
			break;
			case 'n':
			tempscore = tempscore + 1;
			break;
			case 'o':
			tempscore = tempscore + 1;
			break;
			case 'p':
			tempscore = tempscore + 3;
			break;
			case 'q':
			tempscore = tempscore + 10;
			break;
			case 'r':
			tempscore = tempscore + 1;
			break;

			case 's':
			tempscore = tempscore + 1;
			break;
			case 't':
			tempscore = tempscore + 1;
			break;
			case 'u':
			tempscore = tempscore + 1;
			break;
			case 'v':
			tempscore = tempscore + 4;
			break;
			case 'w':
			tempscore = tempscore + 4;
			break;
			case 'x':
			tempscore = tempscore + 8;
			break;
			case 'y':
			tempscore = tempscore + 4;
			break;
			case 'z':
			tempscore = tempscore + 10;
			break;
		}
		if (word.length() > 1)
		{
			calculateScore(word.substring(1));
		}
	}

/** Returns current score. */
	public int currentScore()
	{
		return(score);
	}

/** Returns 2-D array of current tiles on board. */
	public char[][] getBoard()
	{
		return(board.getBoard());
	}

/** Returns string representation of currently racked tiles. */
	public String getRack()
	{
		return(rack.getRack());
	}
}