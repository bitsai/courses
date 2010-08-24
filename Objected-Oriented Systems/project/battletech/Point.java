package battletech;

/** This class represents coordinates on a map. */

public class Point implements java.io.Serializable
{
   private int x;
   private int y;

   public Point(int newx, int newy)
   {
      x = newx;
      y = newy;
   }

	/** Return x-value of this coordinate. */
   public int getx()
   {
      return(x);
   }

	/** Return y-value of this coordinate. */
   public int gety()
   {
      return(y);
   }

	/** Returns true of specified coordinate is the same as this coordinate, false otherwise */
	public boolean equals(Point other)
	{
		if ((x == other.getx()) && (y == other.gety()))
		{ return true; }

		return false;
	}

	/** Returns string representation of this coordinate. */
	public String toString()
	{
		String output = x + " " + y;
		return output;
	}
}