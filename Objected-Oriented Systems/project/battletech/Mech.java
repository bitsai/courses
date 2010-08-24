package battletech;

import java.util.*;
import java.math.*;
import java.rmi.*;
import java.rmi.server.*;

/** This class represents Mechs. */

public class Mech extends UnicastRemoteObject implements MechRemote
{
// Owner
	private BattleTechRemote game;

// MechWarrior
	private MechWarrior mw = new MechWarrior(this); // A default Inner Sphere MechWarrior

// Physical stats
	private String type;
	private int mass;
	private int engineRating;
	private int heatSinks;

	private AmmosRemote ammos;
	private BodyPartsRemote bodyParts;
	private ComponentsRemote components;
	private WeaponsRemote weapons;

// Heat stats
	private int lastHeat = 0;
	private int currentHeat = 0;

// Damage this turn
	private int damageThisTurn = 0;

// Physical attacks
	private PhysicalAttacksRemote physicalAttacks;

// Movement stats
	private int movementMode = 0;	// -1 = Undeclared, 0 = Standing, 1 = Walking, 2 = Running, 3 = Jumping
	private int movementPoints = 0;
	private int facing = 0;
	private int torsoFacing = 0;
	private boolean prone = false;

	private Point location;
	private Point lastLocation;
	private int lastElevation;

// Activeness stats

	private boolean active = false;
	private boolean shutdown = false;

// Constructor method(s)

	public Mech(String t, int m, int er, int hs, BattleTechRemote _game) throws RemoteException
	{
		game = _game;
		type = t;
		mass = m;
		engineRating = er;
		heatSinks = hs;
		
		ammos = new Ammos(this);
		bodyParts = new BodyParts(this);
		components = new Components(this);
		physicalAttacks = new PhysicalAttacks(this);
		weapons = new Weapons(this);
	}

	/** Assign a new MechWarrior to this Mech. */
	public synchronized void addMW(MechWarrior _mw) throws RemoteException
	{ mw = _mw; }

	/** Set the location of this mech. */
	public synchronized void setLocation(int x, int y)  throws RemoteException
	{
		location = new Point(x, y);
		lastLocation = location;

		lastElevation = getGrid().getElevation();
	}

// Query methods

	/** Returns the game module this Mech belongs to. */
	public synchronized BattleTechRemote getOwner() { return(game); }

	/** Returns this Mech's MechWarrior. */
	public synchronized MechWarrior getMW() { return(mw); }

	/** Returns the map in the game module. */	
	public synchronized Map getMap() throws RemoteException { return(game.getMap()); }

	/** Returns the map grid this Mech is in. */
	public synchronized MapGrid getGrid() throws RemoteException
	{
		int x = location.getx();
		int y = location.gety();
		return(getMap().getNearestGrid(x, y));
	}	

	/** Returns this Mech's ammunition items. */
	public synchronized AmmosRemote getAmmos() { return(ammos); }

	/** Returns this Mech's body locations. */
	public synchronized BodyPartsRemote getBodyParts() { return(bodyParts); }

	/** Returns this Mech's component items. */
	public synchronized ComponentsRemote getComponents() { return(components); }

	/** Returns this Mech's physical attacks. */
	public synchronized PhysicalAttacksRemote getPhysicalAttacks() { return(physicalAttacks); }

	/** Returns this Mech's weapons. */
	public synchronized WeaponsRemote getWeapons() { return(weapons); }

	/** Returns this Mech's current location. */
	public synchronized Point getLocation() { return(location); }

	/** Returns this Mech's last location. */
	public synchronized Point getLastLocation() { return(lastLocation); }

	/** Returns this Mech's type. */
	public synchronized String getType() { return(type); }

	/** Returns this Mech's mass. */
	public synchronized int getMass() { return(mass); }

	/** Returns this Mech's engine rating. */
	public synchronized int getEngineRating() { return(engineRating); }

	/** Returns this Mech's number of heat sinks. */
	public synchronized int getHeatSinks() { return(heatSinks); }

	/** Returns this Mech's heat at the beginning of the turn. */
	public synchronized int getLastHeat() { return(lastHeat); }

	/** Returns this Mech's current heat. */
	public synchronized int getCurrentHeat() { return(currentHeat); }

	/** Returns this Mech's damage taken so far this turn. */
	public synchronized int getDamageThisTurn() { return(damageThisTurn); }

	/** Returns this Mech's movement mode. */
	public synchronized int getMovementMode() { return(movementMode); }

	/** Returns this Mech's movement points. */
	public synchronized int getMovementPoints() { return(movementPoints); }

	/** Returns this Mech's facing, in degrees. */
	public synchronized int getFacing() { return(facing); }

	/** Returns this Mech's torso facing, in degrees. */
	public synchronized int getTorsoFacing() { return(torsoFacing); }

	/** Returns true if Mech is prone, false otherwise. */
	public synchronized boolean isProne() { return(prone); }

	/** Returns true if it is this Mech's turn, false otherwise. */
	public synchronized boolean isActive() { return(active); }

	/** Returns true if Mech is shutdown, false otherwise. */
	public synchronized boolean isShutdown() { return(shutdown); }

	/** Returns this Mech's elevation. */
	public synchronized int getElevation() throws RemoteException { return(getGrid().getElevation()); }

	/** Returns this Mech's line of sight elevation. */
	public synchronized int getSightElevation() throws RemoteException 
	{
		if (prone) { return(getGrid().getElevation()); }
		else { return(getGrid().getElevation() + 1); }
	}

	/** Returns this Mech's movement penalties. */
	public synchronized int getMovementModifier()
	{
		int modifier = 0;

		int heatModifier = Tables.getHeatMovementModifier(lastHeat);

		modifier = modifier + heatModifier;

		return(modifier);
	}

	/** Returns this Mech's attack penalties. */
	public synchronized int getAttackModifier() throws RemoteException
	{
		int modifier = 0;

		int heatModifier = Tables.getHeatAttackModifier(lastHeat);

		if (components.getSensorsHits() > 0) { modifier = modifier + 2; } // Sensors hit

		if (prone) { modifier = modifier + 2; } // Prone

		modifier = modifier + heatModifier + movementMode;

		return(modifier);
	}

// Activeness methods

	/** Starts this Mech's turn. */
	public synchronized void startTurn() throws RemoteException
	{
		weapons.activate();
		physicalAttacks.activate();
		movementMode = -1;
		movementPoints = 0;
	}

	/** End this Mech's turn. */
	public synchronized void endTurn() throws RemoteException
	{
		damageThisTurn = 0;
		processHeat();
		mw.revive();
		deactivate();
	}

	/** Deactivate this Mech. */
	public synchronized void deactivate() { active = false; }

	/** Shut down this Mech. */
	public synchronized void shutdown()
	{
		Rules.rollPiloting(3, this);
		shutdown = true;
	}

// Movement methods

	/** Set this Mech's facing and torso facing. */
	public synchronized String setFacing(int newFacing)
	{
		if ((newFacing < 0) || (newFacing > 360))
		{
			return("Invalid facing!");
		}
		
		facing = newFacing;
		torsoFacing = newFacing;
		
		return("New facing: " + newFacing);
	}

	/** Set this Mech's movement mode. */
	public synchronized String setMovementMode(int mode) throws RemoteException
	{
		if (mw.isUnconscious()) // MW not conscious; can't do squat
		{ 
			return("MechWarrior not conscious!");
		}

		if (isShutdown())
		{
			return("Mech shutdown!");
		}

		if (movementMode != -1) // Can only set movement mode at beginning of turn
		{
			return("Can only set movement mode at beginning of turn!");
		}

		String outputMode = "";
		int walkingMP = (engineRating / mass) + getMovementModifier();

		switch (mode)
		{
			case 0: // Standing
				outputMode = "Standing";
				movementPoints = 0; 
				break;

			case 1: // Walking
				outputMode = "Walking";
				movementPoints = walkingMP * 60;

				if ((bodyParts.legs() == 1) && !prone)
				{ movementPoints = 60; }

				addHeat(1);
				break;

			case 2: // Running
				if ((bodyParts.legs() < 2) && !prone)
				{
					return("Can't run on less than 2 legs!");
				}

				outputMode = "Running";
				movementPoints = (int) (walkingMP * 60 * 1.5);

				addHeat(2);
				break;

			case 3: // Jumping
				if (components.getJumpJets() == 0)
				{
					return("No jump jets!");
				}

				if (prone) // Can't jump while prone
				{
					return("Can't jump while prone!");
				}

				if ((getElevation() <= -2) && (getGrid().getType() == 4)) // Can't jump while submerged
				{ 
					return("Can't jump while submerged!");
				}

				outputMode = "Jumping";

				if ((getElevation() == -1) && (getGrid().getType() == 4))
				{ movementPoints = components.getTorsoJumpJets() * 60; }
				else
				{ movementPoints = components.getJumpJets() * 60; }
		}

		active = true;
		movementMode = mode;

		if (movementPoints < 0)
		{ movementPoints = 0; }

		return("Movement Mode: " + outputMode + " Movement Points: " + movementPoints);
	}

	/** Process a fall. */
	public synchronized String fall() throws RemoteException
	{
		if (prone = true) { return(""); } // Already prone; can't fall

		game.addOutput("Mech fell!");

		prone = true;
		int roll = Rules.rollDie(); // "Facing After Fall" table stuff

		int turnAmount = Tables.facingAfterFall1(roll);
		String hitArc = Tables.facingAfterFall2(roll);

		facing = facing + turnAmount;
		if (facing < 0) { facing = 360 - facing; }
		if (facing > 360) { facing = facing - 360; }
		torsoFacing = facing;

		int damage = mass / 10; // Falling damage to mech
		takeClusterDamage(damage, 5, hitArc);

		boolean avoidDamage = Rules.checkPiloting(1, this); // Falling damage to pilot

		if (avoidDamage == false)
		{ mw.takeDamage(1); }

		return("Mech fell!");
	}

	/** Process a drop. */
	public synchronized String drop()
	{
		if (getMW().isUnconscious())
		{ return("MechWarrior is unconscious!"); }

		if (isShutdown())
		{ return("Mech shutdown!"); }

		if (!isActive()) // Can't do anything while shutdown or not my turn
		{
			return("Mech not active!");
		}

		if (movementPoints < (2 * 60)) 
		{ 
			return("Insufficient movement points!");
		}

		prone = true;
		torsoFacing = facing;

		return("Dropped!");
	}

	/** Process a stand. */
	public synchronized String stand() throws RemoteException 
	{
		if (getMW().isUnconscious())
		{ return("MechWarrior is unconscious!"); }

		if (isShutdown())
		{ return("Mech shutdown!"); }

		if (!isActive()) // Can't do anything while shutdown or not my turn
		{
			return("Mech not active!");
		}

		if (components.getGyroHits() == 2)
		{
			return("Gyro destroyed!");
		}

		if (movementMode == 3)
		{
			return("Cannot stand while jumping!");
		}

		if (movementPoints < (2 * 60)) // Insufficient MP
		{
			return("Insufficient movement points!");
		}

		if (bodyParts.legs() == 0) // I have no legs!
		{
			return("No legs!");
		}

		currentHeat = currentHeat + 1;
		movementPoints = movementPoints - (2 * 60);
		boolean getUp = Rules.rollPiloting(0, this);
		prone = false;

		if (getUp)
		{
			if (bodyParts.legs() == 1)
			{
				movementMode = 1;
				movementPoints = 60;
			}

			return("Stand successful!");
		}

		fall();
		return("Stand unsuccessful!");
	}

	/** Turn Mech's torso specified number of degrees. */
	public synchronized String turnTorso(int degrees)
	{
		if (getMW().isUnconscious())
		{ return("MechWarrior is unconscious!"); }

		if (isShutdown())
		{ return("Mech shutdown!"); }

		if (!isActive()) // Can't do anything while shutdown or not my turn
		{
			return("Mech not active!");
		}

		if (prone) // Can't torsotwist while prone
		{
			return("Can't torso twist while prone!");
		}

		int tempTorsoFacing = torsoFacing + degrees;
		int difference = Math.abs(facing - tempTorsoFacing);

		if (difference > 60) // Too large a twist
		{
			return("Torso twist is too far!");
	 	}

		torsoFacing = tempTorsoFacing;

		if (torsoFacing < 0) { torsoFacing = 360 + torsoFacing; }
		if (torsoFacing > 360) { torsoFacing = torsoFacing - 360; }

		return("Torso now facing: " + torsoFacing); // Torsotwist successful
	}

	/** Turn Mech specified number of degrees. */
	public synchronized String turnMech(int degrees)
	{
		int cost = Math.abs(degrees);
		movementPoints = movementPoints - cost;

		torsoFacing = torsoFacing + degrees;
		facing = facing + degrees;

		if (torsoFacing < 0) { torsoFacing = 360 + torsoFacing; }
		if (torsoFacing > 360) { torsoFacing = torsoFacing - 360; }

		if (facing < 0) { facing = 360 + facing; }
		if (facing > 360) { facing = facing - 360; }

		return("Mech now facing: " + facing); // Turn successful
	}
	
	/** Turn mech towards specified point. */
	public synchronized String turnToward(Point destination)
	{
		if (getMW().isUnconscious())
		{ return("MechWarrior is unconscious!"); }

		if (isShutdown())
		{ return("Mech shutdown!"); }

		if (!isActive()) // Can't do anything while shutdown or not my turn
		{
			return("Mech not active!");
		}

		int angle = Rules.getAngle(location, destination);
		int turnAmount = 0;
		int turnAmount1 = 0;
		int turnAmount2 = 0;
		
		if (angle == facing) { return("Mech now facing: " + facing); }
		
		if (angle > facing)
		{
			turnAmount1 = angle - facing - 360;
			turnAmount2 = angle - facing;
		}
		else // facing > angle
		{
			turnAmount1 = angle - facing + 360;
			turnAmount2 = angle - facing;
		}
		
		if (Math.abs(turnAmount1) > Math.abs(turnAmount2)) { turnAmount = turnAmount2; }
		else {turnAmount = turnAmount1; }
		
		int cost = Math.abs(turnAmount);

		if (movementPoints < cost) // Insufficient MP
		{
			return("Insufficient movement points!");
		}
		
		return turnMech(turnAmount);
	}
	
	/** Turn mech away from specified point. */
	public synchronized String turnAway(Point destination)
	{
		if (getMW().isUnconscious())
		{ return("MechWarrior is unconscious!"); }

		if (isShutdown())
		{ return("Mech shutdown!"); }

		if (!isActive()) // Can't do anything while shutdown or not my turn
		{
			return("Mech not active!");
		}

		int angle = Rules.getAngle(location, destination) + 180;
		int turnAmount = 0;
		int turnAmount1 = 0;
		int turnAmount2 = 0;
		
		if (angle > 360) { angle = angle - 360; } // Adjust if angle is greater than 360
		
		if (angle == facing) { return("Mech now facing: " + facing); }
		
		if (angle > facing)
		{
			turnAmount1 = angle - facing - 360;
			turnAmount2 = angle - facing;
		}
		else // facing > angle
		{
			turnAmount1 = angle - facing + 360;
			turnAmount2 = angle - facing;
		}
		
		if (Math.abs(turnAmount1) > Math.abs(turnAmount2)) { turnAmount = turnAmount2; }
		else {turnAmount = turnAmount1; }
		
		int cost = Math.abs(turnAmount);

		if (movementPoints < cost) // Insufficient MP
		{
			return("Insufficient movement points!");
		}
		
		return turnMech(turnAmount);
	}	

	/** Move Mech to specified point. */
	public synchronized String moveMech(Point destination) throws RemoteException 
	{	
		lastLocation = location;
		lastElevation = getElevation();

		if (getMW().isUnconscious())
		{ return("MechWarrior is unconscious!"); }

		if (isShutdown())
		{ return("Mech shutdown!"); }

		if (!isActive()) // Can't do anything while shutdown or not my turn
		{
			return("Mech not active!");
		}

		if (prone) // Can't move while prone
		{
			return("Can't move while prone!");
		}

		boolean facingOK = Rules.checkMovementFacing(movementMode, facing, location, destination);

		if (facingOK == false) // Facing wrong direction; move unsuccessful
		{
			return("Facing incorrect!");
		}

		boolean moved = processMove(destination);

		if (moved && (movementMode == 2) && (components.getGyroHits() > 0)) // Ran with gyro damaged
		{ Rules.rollPiloting(0, this); }

		if (moved && (movementMode == 3)) // Mech jumped
		{
			if ((bodyParts.legs() == 1) || (components.getGyroHits() > 0)) // One leg / gyro hit jump piloting check
			{ Rules.rollPiloting(0, this); }

			return("Jump complete!");
		}
		
		return("Move complete!");
	}

	/** Process a move. */
	public synchronized boolean processMove(Point destination) throws RemoteException
	{
		boolean moved = false;

		MapGrid grid = getGrid();
		int distance = 0;

		int xdir = Rules.sign(destination.getx() - location.getx());
		int ydir = Rules.sign(destination.gety() - location.gety());
		double changex = (double) Math.abs(destination.getx() - location.getx());
		double changey = (double) Math.abs(destination.gety() - location.gety());
// First run
		double xdist = Rules.dist(location.getx(), getMap().getMapGridSize(), xdir);
		double ydist = Rules.dist(location.gety(), getMap().getMapGridSize(), ydir);

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
			distance = (int) Math.sqrt(xdist*xdist + ydist*ydist);
			grid = getMap().getNearestGrid(nextGridx - xdir, nextGridy - ydir);

			if (movementPoints >= Rules.getTerrainCost(grid, distance, getElevation()))
			{
				moved = true;
				updateMove(grid, distance, nextGridx - xdir, nextGridy - ydir);
			}
			else { return moved; }

			distance = (int) Math.sqrt(xdir * xdir + ydir * ydir);
			grid = getMap().getNearestGrid(nextGridx, nextGridy);

			if (canMoveIntoGrid(nextGridx, nextGridy) == false)
			{ return moved; }

			if (movementPoints >= Rules.getTerrainCost(grid, distance, getElevation()))
			{
				currentx = nextGridx;
				currenty = nextGridy;

				moved = true;
				updateMove(grid, distance, nextGridx, nextGridy);

				if ((grid.getType() == 4) && (grid.getElevation() != 0))
				{
					if (fallInWater())
					{ return moved; }
				}
			}
			else { return moved; }

			xdist = Rules.dist(currentx, getMap().getMapGridSize(), xdir);
			ydist = Rules.dist(currenty, getMap().getMapGridSize(), ydir);

			xcost = (changex == 0)? 1000:xdist / changex;
			ycost = (changey == 0)? 1000:ydist / changey;

			if (xcost <= ycost) { ydist = xdist * (changey / changex); }
			else { xdist = ydist * (changex / changey); }

			currentx += xdir * xdist;
			currenty += ydir * ydist;

			nextGridx = (int) Rules.round(currentx, xdir);
			nextGridy = (int) Rules.round(currenty, ydir);
		}

		xdist = destination.getx() - location.getx();
		ydist = destination.gety() - location.gety();

		distance = (int) Math.sqrt(xdist*xdist + ydist*ydist);
		grid = getMap().getNearestGrid(location.getx(), location.gety());

		if (movementPoints >= Rules.getTerrainCost(grid, distance, getElevation()))
		{
			moved = true;
			updateMove(grid, distance, destination.getx(), destination.gety());
		}

		return moved;
	}

	/** Update Mech's location and elevation. */
	public synchronized void updateMove(MapGrid grid, int distance, int x, int y) throws RemoteException
	{
		if (movementMode == 3)
		{
			int heat = distance / getMap().getMapGridSize();
			addHeat(heat);

			int cost = distance * getMap().getMapGridSize();
			movementPoints = movementPoints - cost;
		}
		else
		{
			int cost = Rules.getTerrainCost(grid, distance, getElevation());
			movementPoints = movementPoints - cost;
		}

		location = new Point(x, y);
		lastElevation = grid.getElevation();
	}

	/** Returns true if Mech can move into specified map grid, false otherwise. */
	public synchronized boolean canMoveIntoGrid(int gridx, int gridy) throws RemoteException
	{
		MapGrid grid = getMap().getNearestGrid(gridx, gridy);
		Point gridlocation = new Point(gridx, gridy);

		if ((Math.abs(grid.getElevation() - getElevation()) > 2) && (movementMode != 3))
		{ return false; } // Cannot change elevation more than 2 at a time, unless jumping

		if ((Math.abs(grid.getElevation() - getElevation()) > 0) && Rules.movingBackward(facing, location, gridlocation))
		{ return false; } // Can only change elevation when moving forward

		if ((grid.getElevation() < 0) && (grid.getType() == 4) && (movementMode == 2))
		{ return false; } // Cannot run into water hex deeper than 0

		if ((movementMode == 3) && (Math.abs(grid.getElevation() - lastElevation) > components.getJumpJets()))
		{ return false; } // Cannot jump over hill higher than jumping MP

		return true;
	}

	/** Returns true if Mech fell in current water map grid, false otherwise. */
	public synchronized boolean fallInWater() throws RemoteException
	{
		int modifier = 0;
		boolean avoidFall = true;

		if (getElevation() == -1)
		{ modifier = -1; }

		if (getElevation() == -2)
		{ modifier = 0; }

		if (getElevation() <= -3)
		{ modifier = 1; }

		avoidFall = Rules.rollPiloting(modifier, this);

		return(!avoidFall);
	}

// Heat methods

	/** Add heat to Mech. */
	public synchronized void addHeat(int newHeat)
	{ 
		currentHeat = currentHeat + newHeat;
	}

	/** Process Mech's heat. */
	public synchronized void processHeat() throws RemoteException
	{
		int engineHeat = components.getEngineHits() * 5;
		currentHeat = currentHeat + engineHeat - heatSinks;

		if (currentHeat < 0) { currentHeat = 0; } // Heat cannot drop below 0

		if (currentHeat >= 30) // Heat cannot rise above 30
		{
			currentHeat = 30;
			shutdown();
		}

		if (currentHeat < 14) { shutdown = false; } // Auto-start when heat drops below 14

		int shutdownAvoid = Tables.getShutdownAvoid(currentHeat); // Heat shutdown
		if (Rules.rollDice() < shutdownAvoid)
		{ shutdown(); }
		if (!mw.isUnconscious() && (Rules.rollDice() >= shutdownAvoid)) // MechWarrior restart
		{ shutdown = false; }

		int ammoExplodeAvoid = Tables.getAmmoExplodeAvoid(currentHeat); // Ammo Explosion
		if (Rules.rollDice() < ammoExplodeAvoid)
		{ ammos.overheat(); }

		if (components.getLifeSupportHits() == 1) // MechWarrior damage
		{
			if (currentHeat >= 25) { mw.takeDamage(2); }
			else if (currentHeat >= 15) { mw.takeDamage(1); }
		}

		lastHeat = currentHeat;
	}

// Damage methods

	/** Inflict specified damage to a body location. */
	public synchronized String takeDamage(int damage, String hitArc) throws RemoteException
	{
		String hitLocation = Tables.hitLocation(hitArc);
		String side = "Front";
		String output = "Location hit: " + hitLocation;

		if (hitArc.equalsIgnoreCase("Rear"))
		{
			side = "Rear";
		}

		output = output + " Side hit: " + side;
		output = output + "\n" + bodyParts.damageBodyPart(hitLocation, damage, side);
		damageThisTurn = damageThisTurn + damage;

		if (damageThisTurn >= 20)
		{
			damageThisTurn = 0;
			Rules.rollPiloting(1, this);
		}

		return output;
	}

	/** Inflict specified damage to an upper body location. */
	public synchronized String takePunchDamage(int damage, String hitArc) throws RemoteException
	{
		String hitLocation = Tables.punchHitLocation(hitArc);
		String side = "Front";
		String output = "Location hit: " + hitLocation;

		if (hitArc.equalsIgnoreCase("Rear"))
		{
			side = "Rear";
		}

		output = output + " Side hit: " + side;
		output = output + "\n" + bodyParts.damageBodyPart(hitLocation, damage, side);
		damageThisTurn = damageThisTurn + damage;

		if (damageThisTurn >= 20)
		{
			damageThisTurn = 0;
			Rules.rollPiloting(1, this);
		}

		return output;
	}

	/** Inflict specified damage to a lower body location. */
	public synchronized String takeKickDamage(int damage, String hitArc) throws RemoteException
	{
		String hitLocation = Tables.kickHitLocation(hitArc);
		String side = "Front";
		String output = "Location hit: " + hitLocation;

		if (hitArc.equalsIgnoreCase("Rear"))
		{
			side = "Rear";
		}

		output = output + " Side hit: " + side;
		output = output + "\n" + bodyParts.damageBodyPart(hitLocation, damage, side);
		damageThisTurn = damageThisTurn + damage;

		if (damageThisTurn >= 20)
		{
			damageThisTurn = 0;
			Rules.rollPiloting(1, this);
		}

		return output;
	}

	/** Inflict damage to multiple body locations. */
	public synchronized String takeClusterDamage(int damage, int damage_per_group, String hitArc) throws RemoteException
	{
		String output = "";
	
		while (damage > damage_per_group)
		{
			output = output + "\n" + takeDamage(damage_per_group, hitArc);
			damage = damage - damage_per_group;
		}

		if (damage > 0)
		{
			output = output + "\n" + takeDamage(damage, hitArc);
		}

		return output;
	}

	/** Inflict damage to multiple upper body locations. */
	public synchronized String takeClusterPunchDamage(int damage, int damage_per_group, String hitArc) throws RemoteException
	{
		String output = "";

		while (damage > damage_per_group)
		{
			output = output + "\n" + takePunchDamage(damage_per_group, hitArc);
			damage = damage - damage_per_group;
		}

		if (damage > 0)
		{
			output = output + "\n" + takePunchDamage(damage, hitArc);
		}

		return output;
	}
}
