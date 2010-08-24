package cs321.superscrabble;

/** This class oversees tracking of currently racked tiles. */
public class SRack
{
	char[] srack = new char[7];

/** Default constructor. */
	public SRack()
	{
	}

/** Cloning constructor. */
	public SRack(char[] inrack)
	{
		srack = inrack;
	}

/** Returns string representation of all currently racked tiles. */
	public String getRack()
	{
		String out = "";
		for (int count = 0; count < 7; count++)
		{
			out = out + " " + srack[count];
		}
		return(out);
	}

/** Returns array of currently racked tiles for cloning. */
	public char[] getTiles()
	{
		char[] temp = (char[])srack.clone();
		return(temp);
	}

/** Returns true if input tile is present at input rack position. */
	public boolean hasPiece(int i, char c)
	{
		Character rackpiece = new Character(srack[i]);
		Character piece = new Character(c);
		return(rackpiece.equals(piece));
	}

/** Inserts input tile at input position. */
	public void insertPiece(int i, char c)
	{
		srack[i] = c;
	}
}