package battletech;

import java.io.*;
import java.util.*;

/** This class represents maps. */

public class Map implements Serializable
{
	private int width;
	private int length;
	private int mapGridSize;

	private int horizontalNumGrids;
	private int verticalNumGrids;

	private MapGrid[][] mapGrids;

	/** Creates a default map. */
	public Map(int _width, int _length, int _mapGridSize)
	{
		setup(_width, _length, _mapGridSize);
	}

	/** Loads map from a text file. */
	public Map(String path) throws IOException
	{
		/*Loads a map from a text file
		*/
		BufferedReader Bread;
		StringTokenizer Stoke;
		String line;
		int _width, _length, _mapGridSize;

		try
		{
			Bread = new BufferedReader(new FileReader(path));
		}
		catch (FileNotFoundException e)
		{
			System.out.println("The file '" + path + "' was not found.");
			return;
		}

		line = Bread.readLine();
		Stoke = new StringTokenizer(line);

		_width = Integer.parseInt(Stoke.nextToken());
		_length = Integer.parseInt(Stoke.nextToken());
		_mapGridSize = Integer.parseInt(Stoke.nextToken());

		setup(_width, _length, _mapGridSize);

		line = Bread.readLine();

		while (line !=null)
		{
			parseLine(line);
			line = Bread.readLine();
		}

		Bread.close();
	}

	/** Parse a line of text to create a terrain grid. */
	public void parseLine(String line)
	{
		StringTokenizer Stoke = new StringTokenizer(line);

		int x = Integer.parseInt(Stoke.nextToken());
		int y = Integer.parseInt(Stoke.nextToken());
		int type = Integer.parseInt(Stoke.nextToken());
		int elevation = Integer.parseInt(Stoke.nextToken());

		setMapGrid(x, y, type, elevation);
	}	

	/** Creates a map of specified sizes, filled with level 0 clear terrain. */
	public void setup(int _width, int _length, int _mapGridSize)
	{
		width = _width;
		length = _length;
		mapGridSize = _mapGridSize;

		horizontalNumGrids = width / mapGridSize;
		verticalNumGrids = length / mapGridSize;

		mapGrids = new MapGrid[horizontalNumGrids][verticalNumGrids];

		for (int x = 0; x < (horizontalNumGrids); x++)
		{
			for (int y = 0; y < (horizontalNumGrids); y++)
			{
				MapGrid mapGrid = new MapGrid(x, y, 0, 0, mapGridSize);
				mapGrids[x][y] = mapGrid;
			}
		}
	}

	/** Returns the map grid closest to the specified coordinates. */
	public MapGrid getNearestGrid(int x, int y)
	{
		float floatx = (float) x / mapGridSize;
		float floaty = (float) y / mapGridSize;

		int gridx = Math.round(floatx);
		int gridy = Math.round(floaty);

		if (gridx >= horizontalNumGrids) { gridx = horizontalNumGrids - 1; }
		if (gridy >= verticalNumGrids) { gridy = verticalNumGrids - 1; }

		return(mapGrids[gridx][gridy]);
	}

	/** Places a map grid on the map at specified coordinates. */
	public void setMapGrid(int x, int y, int type, int elevation)
	{
		float floatx = (float) x / mapGridSize;
		float floaty = (float) y / mapGridSize;

		int gridx = Math.round(floatx);
		int gridy = Math.round(floaty);

		if (gridx >= horizontalNumGrids) { gridx = horizontalNumGrids - 1; }
		if (gridy >= verticalNumGrids) { gridy = verticalNumGrids - 1; }

		MapGrid mapGrid = new MapGrid(x, y, type, elevation, mapGridSize);
		mapGrids[gridx][gridy] = mapGrid;
	}

	/** Returns width of this map. */
	public int getWidth()
	{
		return width;
	}

	/** Returns length of this map. */
	public int getLength()
	{
		return length;
	}

	/** Returns the size of map grids of this map. */
	public int getMapGridSize()
	{
		return mapGridSize;
	}

	/** Returns true if the two specified Mechs have a line of sight to each other, false otherwise. */
	public boolean lineOfSight(MechRemote mech1, MechRemote mech2)
	{
		double noLOS = 6;
		double intervening = 0;

		try
		{
			MapGrid mech1Grid = mech1.getGrid();
			MapGrid mech2Grid = mech2.getGrid();

			if ((mech1Grid.getType() == 4) && (mech1Grid.getElevation() <= -2))
			{ return false; }

			if ((mech2Grid.getType() == 4) && (mech2Grid.getElevation() <= -2))
			{ return false; }

			if (hasAdjacentCover(mech1, mech2) || hasAdjacentCover(mech2, mech1)) { return false; }

			Point location = mech1.getLocation();
			Point destination = mech2.getLocation();

			MapGrid grid = getNearestGrid(location.getx(), location.gety());
			double distance = 0;

			int xdir = Rules.sign(destination.getx() - location.getx());
			int ydir = Rules.sign(destination.gety() - location.gety());
			double changex = (double) Math.abs(destination.getx() - location.getx());
			double changey = (double) Math.abs(destination.gety() - location.gety());
	// First run
			double xdist = Rules.dist(location.getx(), mapGridSize, xdir);
			double ydist = Rules.dist(location.gety(), mapGridSize, ydir);

			double xcost = (changex == 0)? 1000:xdist / changex;
			double ycost = (changey == 0)? 1000:ydist / changey;

			if (xcost <= ycost) { ydist = xdist * (changey / changex); }
			else { xdist = ydist * (changex / changey); }

			double currentx = location.getx() + xdir * xdist;
			double currenty = location.gety() + ydir * ydist;

			int nextGridx = (int) Rules.round(currentx, xdir);
			int nextGridy = (int) Rules.round(currenty, ydir);

			while ((xdir * (destination.getx() - nextGridx) > 0) || (ydir * (destination.gety() - nextGridy) > 0))
			{
//				System.out.println(nextGridx + " " + nextGridy);
				
				distance = (int) Math.sqrt(xdist*xdist + ydist*ydist);
				grid = getNearestGrid(nextGridx - xdir, nextGridy - ydir);

				if ((grid.getElevation() >= mech1.getSightElevation() - 2) || (grid.getElevation() >= mech2.getSightElevation() - 2))
				{
					if (grid.getType() == 2)
					{ intervening = intervening + (distance / mapGridSize) * 2; } // light woods

					if (grid.getType() == 3)
					{ intervening = intervening + (distance / mapGridSize) * 3; } // heavy woods
				} // Woods modifier

				if ((grid.getElevation() > mech1.getSightElevation()) && (grid.getElevation() > mech2.getSightElevation()))
				{
					if (!grid.equals(mech1.getGrid()))
					{return false; } // Hill blocks LOS
				}

				xdist = Rules.dist(currentx, mapGridSize, xdir);
				ydist = Rules.dist(currenty, mapGridSize, ydir);

				xcost = (changex == 0)? 1000:xdist / changex;
				ycost = (changey == 0)? 1000:ydist / changey;

				if (xcost <= ycost) { ydist = xdist * (changey / changex); }
				else { xdist = ydist * (changex / changey); }

				currentx += xdir * xdist;
				currenty += ydir * ydist;

				nextGridx = (int) Rules.round(currentx, xdir);
				nextGridy = (int) Rules.round(currenty, ydir);
			}

			xdist = destination.getx() - currentx;
			ydist = destination.gety() - currenty;

			distance = (int) Math.sqrt(xdist*xdist + ydist*ydist);
			grid = getNearestGrid((int) currentx, (int) currenty);

			if ((grid.getElevation() >= mech1.getSightElevation() - 2) || (grid.getElevation() >= mech2.getSightElevation() - 2))
			{
				if (grid.getType() == 2)
				{ intervening = intervening + (distance / mapGridSize) * 2; } // light woods

				if (grid.getType() == 3)
				{ intervening = intervening + (distance / mapGridSize) * 3; } // heavy woods
			} // Woods modifier

			if (intervening >= noLOS)
			{ return false; }
		}
		catch(java.rmi.RemoteException e)
		{
			System.out.println("Map RemoteException!");
		}

		return true;
	}

	/** Returns the firing modifier due to terrain conditions between the two specified Mechs. */
	public int getTerrainModifier(MechRemote shooter, MechRemote target)
	{
		double modifier = 0;
		double distance = 0;

		try
		{
			if ((shooter.getGrid().getType() == 4) && (shooter.getElevation() == -1))
			{ modifier = modifier + 1; } // Shooter water modifier

			if (hasPartialCover(shooter, target))
			{ modifier = modifier + 3; } // Partial cover
		
			Point location = target.getLocation(); // Go from target to shooter, so it's easy to get "hex" next to target
			Point destination = shooter.getLocation();

			int xdir = Rules.sign(destination.getx() - location.getx());
			int ydir = Rules.sign(destination.gety() - location.gety());
			double changex = (int) Math.abs(destination.getx() - location.getx());
			double changey = (int) Math.abs(destination.gety() - location.gety());
	// First run
			double xdist = Rules.dist(location.getx(), mapGridSize, xdir);
			double ydist = Rules.dist(location.gety(), mapGridSize, ydir);

			double xcost = (changex == 0)? 1000:xdist / changex;
			double ycost = (changey == 0)? 1000:ydist / changey;

			if (xcost <= ycost) { ydist = xdist * (changey / changex); }
			else { xdist = ydist * (changex / changey); }

			double currentx = location.getx() + xdir * xdist;
			double currenty = location.gety() + ydir * ydist;

			int nextGridx = (int) Rules.round(currentx, xdir);
			int nextGridy = (int) Rules.round(currenty, ydir);

			MapGrid grid = getNearestGrid(nextGridx - xdir, nextGridy - ydir); // Target "hex"

			if ((grid.getType() == 4) && (grid.getElevation() == -1)) 
			{ modifier = modifier - 1; } // Target water modifier

			while ((xdir * (destination.getx() - nextGridx) > 0) || (ydir * (destination.gety() - nextGridy) > 0))
			{
				distance = (int) Math.sqrt(xdist*xdist + ydist*ydist);
				grid = getNearestGrid(nextGridx - xdir, nextGridy - ydir);

				if ((grid.getElevation() >= shooter.getSightElevation() - 2) || (grid.getElevation() >= target.getSightElevation() - 2))
				{
					if (grid.getType() == 2)
					{ modifier = modifier + (distance / mapGridSize); } // light woods

					if (grid.getType() == 3)
					{ modifier = modifier + (distance / mapGridSize) * 2; } // heavy woods
				} // Woods modifier

				xdist = Rules.dist(currentx, mapGridSize, xdir);
				ydist = Rules.dist(currenty, mapGridSize, ydir);

				xcost = (changex == 0)? 1000:xdist / changex;
				ycost = (changey == 0)? 1000:ydist / changey;

				if (xcost <= ycost) { ydist = xdist * (changey / changex); }
				else { xdist = ydist * (changex / changey); }

				currentx += xdir * xdist;
				currenty += ydir * ydist;

				nextGridx = (int) Rules.round(currentx, xdir);
				nextGridy = (int) Rules.round(currenty, ydir);
			}

			xdist = destination.getx() - currentx;
			ydist = destination.gety() - currenty;

			distance = (int) Math.sqrt(xdist*xdist + ydist*ydist);
			grid = getNearestGrid((int) currentx, (int) currenty);

			if ((grid.getElevation() >= shooter.getSightElevation() - 2) || (grid.getElevation() >= target.getSightElevation() - 2))
			{
				if (grid.getType() == 2)
				{ modifier = modifier + (distance / mapGridSize); } // light woods

				if (grid.getType() == 3)
				{ modifier = modifier + (distance / mapGridSize) * 2; } // heavy woods
			} // Woods modifier
		}
		catch(java.rmi.RemoteException e)
		{
			System.out.println("Map RemoteException!");
		}

		return((int) modifier);
	}

	/** Returns true if the specified target has partial cover from terrain, false otherwise. */
	public boolean hasPartialCover(MechRemote shooter, MechRemote target)
	{
		try
		{
			if (shooter.getElevation() > target.getElevation()) // Firing down negates partial cover
			{ return false; }
		
			Point location = target.getLocation(); // Go from target to shooter, so it's easy to get "hex" next to target
			Point destination = shooter.getLocation();

			int xdir = Rules.sign(destination.getx() - location.getx());
			int ydir = Rules.sign(destination.gety() - location.gety());
			double changex = (int) Math.abs(destination.getx() - location.getx());
			double changey = (int) Math.abs(destination.gety() - location.gety());
	// First run
			double xdist = Rules.dist(location.getx(), mapGridSize, xdir);
			double ydist = Rules.dist(location.gety(), mapGridSize, ydir);

			double xcost = (changex == 0)? 1000:xdist / changex;
			double ycost = (changey == 0)? 1000:ydist / changey;

			if (xcost <= ycost) { ydist = xdist * (changey / changex); }
			else { xdist = ydist * (changex / changey); }

			double currentx = location.getx() + xdir * xdist;
			double currenty = location.gety() + ydir * ydist;

			int nextGridx = (int) Rules.round(currentx, xdir);
			int nextGridy = (int) Rules.round(currenty, ydir);

			MapGrid grid = getNearestGrid(nextGridx - xdir, nextGridy - ydir); // Target "hex"

			if ((grid.getType() == 4) && (grid.getElevation() == -1)) { return true; } // Water partial cover

			grid = getNearestGrid(nextGridx, nextGridy); // "Hex" adjacent to target

			if (grid.getElevation() == (target.getElevation() + 1)) { return true; } // Partial cover
		}
		catch(java.rmi.RemoteException e)
		{
			System.out.println("Map RemoteException!");
		}

		return false;
	}

	/** Returns true if the specified target has full cover from terrain, false otherwise. */
	public boolean hasAdjacentCover(MechRemote shooter, MechRemote target)
	{
		try
		{
			Point location = target.getLocation(); // Go from target to shooter, so it's easy to get "hex" next to target
			Point destination = shooter.getLocation();

			int xdir = Rules.sign(destination.getx() - location.getx());
			int ydir = Rules.sign(destination.gety() - location.gety());
			double changex = (double) Math.abs(destination.getx() - location.getx());
			double changey = (double) Math.abs(destination.gety() - location.gety());
	// First run
			double xdist = Rules.dist(location.getx(), mapGridSize, xdir);
			double ydist = Rules.dist(location.gety(), mapGridSize, ydir);

			double xcost = (changex == 0)? 1000:xdist / changex;
			double ycost = (changey == 0)? 1000:ydist / changey;

			if (xcost <= ycost) { ydist = xdist * (changey / changex); }
			else { xdist = ydist * (changex / changey); }

			double currentx = location.getx() + xdir * xdist;
			double currenty = location.gety() + ydir * ydist;

			int nextGridx = (int) Rules.round(currentx, xdir);
			int nextGridy = (int) Rules.round(currenty, ydir);

			MapGrid grid = getNearestGrid(nextGridx, nextGridy); // "Hex" adjacent to target

			if (grid.getElevation() > target.getSightElevation()) { return true; } // Full cover
		}
		catch(java.rmi.RemoteException e)
		{
			System.out.println("Map RemoteException!");
		}

		return false;
	}
}