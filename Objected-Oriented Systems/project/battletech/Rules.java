package battletech;

import java.util.*;
import java.math.*;

/** This class represents various BattleTech rules. */

public class Rules implements java.io.Serializable
{
	private static Random random = new Random();

// Weapons fire stuff

	/** Returns the range modifier for attacks. */
	public static int getRangeModifier(double distance, Weapon weapon, int gridSize)
	{
		int modifier = 0;
		int range = (int) distance / gridSize;

		if (range > weapon.getMediumRange())
		{ modifier = modifier + 4; }
		else if (range > weapon.getShortRange())
		{ modifier = modifier + 2; }

		if (range <= weapon.getMinRange())
		{
			int minRangeModifier = weapon.getMinRange() - range;
			modifier = modifier + minRangeModifier;
		}

		return(modifier);
	}

	/** Returns the target movement modifier for attacks. */
	public static int getMovementModifier(MechRemote mech)
	{
		int modifier = 0;
		int displacement = 0;

		try
		{
			if (mech.getMovementMode() == 3) { modifier = modifier + 1; } // Target jumping modifier
			displacement = (int) getDistance(mech.getLocation(), mech.getLastLocation()) / mech.getMap().getMapGridSize();
		}
		catch(java.rmi.RemoteException e)
		{
			System.out.println("Rules RemoteException!");
		}

		if (displacement >= 10) { modifier = modifier + 4; } // Target displacement modifier
		else if (displacement >= 7) { modifier = modifier + 3; }
		else if (displacement >= 5) { modifier = modifier + 2; }
		else if (displacement >= 3) { modifier = modifier + 1; }

		return(modifier);
	}

	/** Returns the difficulty for weapon attacks. */
	public static int getGunneryDifficulty(MechRemote shooter, MechRemote target, Weapon weapon)
	{
		int difficulty = 0;

		try
		{
			Map map = shooter.getMap();

			double distance = getDistance(shooter.getLocation(), target.getLocation());

			difficulty = difficulty + shooter.getMW().getGunnery(); // MechWarrior's Gunnery skill

			difficulty = difficulty + shooter.getAttackModifier(); // Shooter attack modifiers

			difficulty = difficulty + getRangeModifier(distance, weapon, map.getMapGridSize()); // Range modifiers

			difficulty = difficulty + map.getTerrainModifier(shooter, target); // Terrain modifiers

			if (target.isProne()) // Prone target modifier
			{
				if (distance <= map.getMapGridSize())
				{ difficulty = difficulty - 2; }
				else
				{ difficulty = difficulty + 1; }
			}

			if (target.isShutdown() || target.getMW().isUnconscious()) // Immobile target modifier
			{ difficulty = difficulty - 4; }

			difficulty = difficulty + getMovementModifier(target); // Target movement modifier
		}
		catch(java.rmi.RemoteException e)
		{
			System.out.println("Rules RemoteException!");
		}

		return(difficulty);
	}

	/** Returns the difficulty for physical attacks. */
	public static int getPhysicalAttackDifficulty(MechRemote shooter, MechRemote target, int base)
	{
		int difficulty = base;

		try
		{
			Map map = shooter.getMap();

			difficulty = difficulty + shooter.getMovementMode(); // Shooter movement modifiers

			difficulty = difficulty + getMovementModifier(target); // Target movement modifier

			difficulty = difficulty + map.getTerrainModifier(shooter, target); // Terrain modifiers

			if (target.isProne()) { difficulty = difficulty - 2; } // Prone target modifier

			if (target.isShutdown() || target.getMW().isUnconscious()) // Immobile target modifier
			{ difficulty = difficulty - 4; }
		}
		catch(java.rmi.RemoteException e)
		{
			System.out.println("Rules RemoteException!");
		}

		return(difficulty);
	}

// Movement stuff

	/** Return true if destination is behind origin, false otherwise. */
	public static boolean movingBackward(int facing, Point origin, Point destination)
	{
		int angle = getAngle(origin, destination);

		if ((angle >= facing - 195) && (angle <= facing - 165))
		{ return true; }

		int angle1 = angle + 360;

		if ((angle1 >= facing - 195) && (angle1 <= facing - 165))
		{ return true; }

		int angle2 = angle - 360;

		if ((angle2 >= facing - 195) && (angle2 <= facing - 165))
		{ return true; }

		return false;

	}

	/** Return true if destination is in front of origin, false otherwise. */
	public static boolean movingForward(int facing, Point origin, Point destination)
	{
		int angle = getAngle(origin, destination);

		if ((angle >= facing - 15) && (angle <= facing + 15))
		{ return true; }

		int angle1 = angle + 360;

		if ((angle1 >= facing - 15) && (angle1 <= facing + 15))
		{ return true; }

		int angle2 = angle - 360;

		if ((angle2 >= facing - 15) && (angle2 <= facing + 15))
		{ return true; }

		return false;

	}

	/** Return true if movement is allowed, false otherwise. */
	public static boolean checkMovementFacing(int movementMode, int facing, Point origin, Point destination)
	{
		if (movementMode == 3) { return(true); } // Can jump in any direction

		if (movingForward(facing, origin, destination)) // Destination is directly ahead; move OK
		{ return(true); }

		if ((movingBackward(facing, origin, destination)) && (movementMode == 1)) // Can walk backwards
		{ return(true); }

		return(false);
	}

	/** Returns cost of moving specified distance in specified map grid. */
	public static int getTerrainCost(MapGrid grid, int distance, int currentElevation)
	{
		int cost = 0;

		if (grid.getType() == 1)	// Rough terrain cost
		{ cost = cost + (distance * 2); }

		else if (grid.getType() == 2)	// Light woods cost
		{ cost = cost + (distance * 2); }

		else if (grid.getType() == 3)	// Heavy woods cost
		{ cost = cost + (distance * 3); }

		else if ((grid.getType() == 4) && (grid.getElevation() == 1))	// Depth 1 Water cost
		{ cost = cost + (distance * 2); }

		else if ((grid.getType() == 4) && (grid.getElevation() >= 2))	// Depth 2+ Water cost
		{ cost = cost + (distance * 4); }

		else { cost = cost + distance; } // Clear terrain

		cost = cost * grid.getSize();

		if (grid.getElevation() != currentElevation) { cost = cost + (Math.abs(grid.getElevation() - currentElevation) * 60); } // Elevation change cost

		return(cost);
	}

// Piloting stuff

	/** Returns difficulty of a Piloting Skill check. */
	public static int getPilotingDifficulty(int modifier, MechRemote mech)
	{
		int difficulty = modifier;

		try
		{
			int pilotSkill = mech.getMW().getPiloting();
			int legModifier = (2 - mech.getBodyParts().legs()) * 5;
			int gyroModifier = mech.getComponents().getGyroHits() * 3;
			difficulty = difficulty + pilotSkill + legModifier + gyroModifier;
		}
		catch(java.rmi.RemoteException e)
		{
			System.out.println("Rules RemoteException!");
		}

		return(difficulty);
	}

	/** Returns true if a Piloting Skill check exceeds its difficulty, false otherwise. */
	public static boolean checkPiloting(int modifier, MechRemote mech)
	{
		int difficulty = getPilotingDifficulty(modifier, mech);

		if (rollDice() < difficulty) // Return false if pilot fails roll
		{ return(false); }

		return(true); // Return true for pilot making roll
	}

	/** Processes a Piloting Skill check.  Returns true if check succeeds, false otherwise. */
	public static boolean rollPiloting(int modifier, MechRemote mech)
	{
		try
		{
			if (mech.isShutdown() || mech.getMW().isUnconscious()) // If mech is shutdown or mechwarrior is unconscious, we fail and fall automatically
			{
				mech.fall();
				return false;
			}

			boolean pilotingCheck = checkPiloting(modifier, mech);

			if (pilotingCheck == false) // See if pilot can avoid fall
			{
				mech.fall();
				return false;
			}

			mech.getOwner().addOutput("Mech avoided fall!");
		}
		catch(java.rmi.RemoteException e)
		{
			System.out.println("Rules RemoteException!");
		}

		return true;
	}

// Generic stuff

	/** Dummy test method. */
	public static boolean dan(Point p1, Point p2) 
	{ 
//		Point p1 = mech1.getLocation(); 
//		Point p2 = mech2.getLocation(); 
		int p1x = p1.getx(); 
		int p1y = p1.gety(); 
		int p2x = p2.getx(); 
		int p2y = p2.gety(); 
		int dx = p2x-p1x; 
		int dy = p2y-p1y; 
		int xdir = sign(dx); 
		int ydir = sign(dy); 
//		double dist = Math.sqrt(dx*dx+dy*dy); 
		double changex = (double) Math.abs(dx); // Diff in x 
		double changey = (double) Math.abs(dy); // Diff in y 

		double currentx = p1x; 
		double currenty = p1y; 
		int nextx=p1x;
		int nexty=p1y;
		double xdist,ydist; 
		double xcost,ycost; 

		int width = 5; 

		while ((p2x-nextx) * xdir > 0 || (p2y-nexty) * ydir > 0) 
		{ 
			xdist = dist(currentx,width,xdir); //total distance need to encounter next terrain object in x direction 
			ydist = dist(currenty,width,ydir); 

			xcost = (changex ==0)? 10000:xdist / changex; 
			ycost = (changey ==0)? 10000:ydist / changey; 

			if (xcost <= ycost) ydist = xdist*(changey/changex); 
			else xdist = ydist*(changex/changey); 

			currentx += xdir * xdist; 
			currenty += ydir * ydist; 

			nextx = (int) round(currentx,ydir); 
			nexty = (int) round(currenty,ydir); 

//			if (Map.getNearestGrid(nextx,nexty).getType() !=0) return false; 
		} 

		System.out.println("Nextx: " + nextx + " Nexty: " + nexty);
		System.out.println("Currentx: " + currentx + " Currenty: " + currenty);
		return true; 
    }

	/** Returns name of the next logical body location for transferring of damage or critical hits from the specified body location. */
	public static String transfer(String location)
	{
		String newLocation = "CTorso";

		if ((location.equalsIgnoreCase("RArm")) || (location.equalsIgnoreCase("RLeg")))
		{ newLocation = "RTorso"; }

		if ((location.equalsIgnoreCase("LArm")) || (location.equalsIgnoreCase("LLeg")))
		{ newLocation = "LTorso"; }

		return newLocation;
	}

	/** Returns distance to next map grid. */
	public static double dist(double num, int width, int dir)
	{
		//distance to next terrain square
		double next = num + width * dir;
 		int square = (int) (round((next / width), dir)) * width;
		double dist = (square - num) - dir * ((double) width / 2);

		return Math.abs(dist);
	}

	/** Rounds the specified number. */
	public static double round (double num,int d)
	{
		//if decimal is 0.5, rounds up if d positive; down if d is negative
		return (d>=0)? Math.floor(num+0.5):Math.ceil(num-0.5);
	}

	/** Returns 1 if specified number is greater than 0, -1 if it is less than 0, 0 otherwise. */
	public static int sign(int num)
	{
		if (num > 0) return 1;
		else if (num < 0) return -1;
		else return 0;
	}

	/** Returns distance between specified coordinates. */
	public static double getDistance(Point origin, Point destination)
	{
		int x = destination.getx() - origin.getx();
		int y = destination.gety() - origin.gety();
		double distance = Math.sqrt(x * x + y * y);

		return distance;
	}

	/** Returns angle in degrees from specified origin to specified destination. */
	public static int getAngle(Point origin, Point destination) // origin != destination
	{	
		double distance = getDistance(origin, destination);
		int add = 0;
		int x = destination.getx() - origin.getx();
		int y = origin.gety() - destination.gety(); // Java flips things around
		
		double angleRad = Math.asin(Math.abs(y)/distance);
		double angleDeg = Math.toDegrees(angleRad);
		
		if ((x >= 0) && (y >= 0)) // Quadrant 1
		{
			angleDeg = angleDeg;
		}
		if ((x < 0) && (y > 0)) // Quadrant 2
		{
			angleDeg = 180 - angleDeg;
		}
		if ((x <= 0) && (y <= 0)) // Quadrant 3
		{
			angleDeg = 180 + angleDeg;
		}
		if ((x > 0) && (y < 0)) // Quadrant 4
		{
			angleDeg = 360 - angleDeg;
		}
		
		float angleFloat = Math.round(angleDeg);
		int angleInt = Math.round(angleFloat);

		return(angleInt);
	}

	/** Returns firing arc from specified origin to specified destination. */
	public static String getArc(int facing, Point origin, Point other)
	{
		int angle = getAngle(origin, other);

		String side = "";

		if (((angle >= facing) && (angle < facing + 90)) || ((angle <= facing) && (angle > facing - 90)))
		{
			side = "Front";
		}
		else if (((angle >= facing + 90) && (angle < facing + 150)) || ((angle < facing - 210) && (angle >= facing - 270)))
		{
			side = "Left";
		}
		else if (((angle >= facing + 150) && (angle <= facing + 210)) || ((angle <= facing - 150) && (angle >= facing - 210)))
		{
			side = "Rear";
		}
		else if (((angle > facing + 210) && (angle <= facing + 270)) || ((angle <= facing - 90) && (angle > facing - 150)))
		{
			side = "Right";
		}
		else if (((angle > facing + 270) && (angle <= facing + 360)) || ((angle < facing - 270) && (angle >= facing - 360)))
		{
			side = "Front";
		}

		return(side);
	}

// Dice roll stuff

	/** Returns result of rolling 1 six-sided die. */
	public static int rollDie() // 1 die rolled
	{
		int result = (random.nextInt(6) + 1);

		return(result);
	}

	/** Returns result of rolling 2 six-sided dice. */
	public static int rollDice() // 2 dice rolled
	{
		int result = (random.nextInt(6) + 1) + (random.nextInt(6) + 1);

		return(result);
	}

	/** Returns critical hit location for body locations with 6 critical hit locations. */
	public static int rollCritical1() // Critical roll for location with 6 slots
	{
		int result = random.nextInt(6);

		return(result);
	}

	/** Returns critical hit location for body locations with 12 critical hit locations. */
	public static int rollCritical2() // Critical roll for location with 12 slots
	{
		int result = random.nextInt(12);

		return(result);
	}

	/** Return 1 if player 1 wins the initiative, 2 if player 2 wins the initiative. */
	public static int rollInitiative()
	{
		int result = random.nextInt(2);

		return(result + 1);
	}
}