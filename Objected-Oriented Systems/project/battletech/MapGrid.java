package battletech;

/** This class represents map grids of a map. */

public class MapGrid implements java.io.Serializable
{
	private int x;
	private int y;

	private int terrainType; // 0 = clear, 1 = rough, 2 = light woods, 3 = heavy woods, 4 = water
	private int elevation; // 0 = default, negative numbers = water depth

	private int size;

	public MapGrid(int _x, int _y, int tt, int e, int _size)
	{
		x = _x;
		y = _y;

		terrainType = tt;
		elevation = e;

		size = _size;
	}

	/** Returns the x coordinate of this map grid. */
	public int getx()
	{ return x; }

	/** Returns the y coordinate of this map grid. */
	public int gety()
	{ return y; }

	/** Returns the terrain type of this map grid. */
	public int getType()
	{
		return(terrainType);
	}

	/** Returns the elevation of this map grid. */
	public int getElevation()
	{
		return(elevation);
	}

	/** Returns the size of this map grid. */
	public int getSize()
	{ return size; }

	/** Returns true if the specifed map grid has the same coordinates of this map grid, false otherwise. */
	public boolean equals(MapGrid other)
	{
		if ((x == other.getx()) && (y == other.gety()))
		{ return true; }

		return false;
	}
}