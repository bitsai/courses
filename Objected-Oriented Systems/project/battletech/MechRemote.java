package battletech;

import java.rmi.*;

/** This is the interface for Mechs. */

public interface MechRemote extends Remote
{
// Constructor methods
	/** Assign a new MechWarrior to this Mech. */
	public void addMW(MechWarrior _mw) throws RemoteException;

	/** Set the location of this mech. */
	public void setLocation(int x, int y) throws RemoteException;
	
// Query methods

	/** Returns the game module this Mech belongs to. */	
	public BattleTechRemote getOwner() throws RemoteException;

	/** Returns this Mech's MechWarrior. */
	public MechWarrior getMW() throws RemoteException; 

	/** Returns the map in the game module. */	
	public Map getMap() throws RemoteException;

	/** Returns the map grid this Mech is in. */
	public MapGrid getGrid() throws RemoteException;
	
	/** Returns this Mech's ammunition items. */
	public AmmosRemote getAmmos() throws RemoteException;

	/** Returns this Mech's body locations. */
	public BodyPartsRemote getBodyParts() throws RemoteException;

	/** Returns this Mech's component items. */
	public ComponentsRemote getComponents() throws RemoteException;

	/** Returns this Mech's physical attacks. */
	public PhysicalAttacksRemote getPhysicalAttacks() throws RemoteException;

	/** Returns this Mech's weapons. */
	public WeaponsRemote getWeapons() throws RemoteException;
	
	/** Returns this Mech's current location. */
	public Point getLocation() throws RemoteException;

	/** Returns this Mech's last location. */
	public Point getLastLocation() throws RemoteException;
	
	/** Returns this Mech's type. */
	public String getType() throws RemoteException;

	/** Returns this Mech's mass. */
	public int getMass() throws RemoteException;

	/** Returns this Mech's engine rating. */
	public int getEngineRating() throws RemoteException;

	/** Returns this Mech's number of heat sinks. */
	public int getHeatSinks() throws RemoteException;
	
	/** Returns this Mech's heat at the beginning of the turn. */
	public int getLastHeat() throws RemoteException;

	/** Returns this Mech's current heat. */
	public int getCurrentHeat() throws RemoteException;
	
	/** Returns this Mech's damage taken so far this turn. */
	public int getDamageThisTurn() throws RemoteException;
	
	/** Returns this Mech's movement mode. */
	public int getMovementMode() throws RemoteException;

	/** Returns this Mech's movement points. */
	public int getMovementPoints() throws RemoteException;

	/** Returns this Mech's facing, in degrees. */
	public int getFacing() throws RemoteException;

	/** Returns this Mech's torso facing, in degrees. */
	public int getTorsoFacing() throws RemoteException;

	/** Returns true if Mech is prone, false otherwise. */
	public boolean isProne() throws RemoteException;
	
	/** Returns true if it is this Mech's turn, false otherwise. */
	public boolean isActive() throws RemoteException;

	/** Returns true if Mech is shutdown, false otherwise. */
	public boolean isShutdown() throws RemoteException;
	
	/** Returns this Mech's elevation. */
	public int getElevation() throws RemoteException;

	/** Returns this Mech's line of sight elevation. */
	public int getSightElevation() throws RemoteException;
		
	/** Returns this Mech's movement penalties. */
	public int getMovementModifier() throws RemoteException;

	/** Returns this Mech's attack penalties. */
	public int getAttackModifier() throws RemoteException;
	
// Activeness
	/** Starts this Mech's turn. */
	public void startTurn() throws RemoteException;

	/** End this Mech's turn. */
	public void endTurn() throws RemoteException;
	
	/** Deactivate this Mech. */
	public void deactivate() throws RemoteException;

	/** Shut down this Mech. */
	public void shutdown() throws RemoteException;
	
// Movement methods
	/** Set this Mech's facing and torso facing. */
	public String setFacing(int newFacing) throws RemoteException;

	/** Set this Mech's movement mode. */
	public String setMovementMode(int mode) throws RemoteException;

	/** Process a fall. */
	public String fall() throws RemoteException;

	/** Process a drop. */
	public String drop() throws RemoteException;

	/** Process a stand. */
	public String stand() throws RemoteException;

	/** Turn Mech's torso specified number of degrees. */
	public String turnTorso(int degrees) throws RemoteException;

	/** Turn Mech specified number of degrees. */
	public String turnMech(int degrees) throws RemoteException;

	/** Turn Mech towards specified point. */
	public String turnToward(Point destination) throws RemoteException;

	/** Turn Mech away from specified point. */
	public String turnAway(Point destination) throws RemoteException;

	/** Move Mech to specified point. */
	public String moveMech(Point destination) throws RemoteException;
	
	/** Process a move. */
	public boolean processMove(Point destination) throws RemoteException;

	/** Update Mech's location and elevation. */
	public void updateMove(MapGrid grid, int distance, int x, int y) throws RemoteException;

	/** Returns true if Mech can move into specified map grid, false otherwise. */
	public boolean canMoveIntoGrid(int gridx, int gridy) throws RemoteException;

	/** Returns true if Mech fell in current water map grid, false otherwise. */
	public boolean fallInWater() throws RemoteException;

// Heat methods
	/** Add heat to Mech. */
	public void addHeat(int newHeat) throws RemoteException;

	/** Process Mech's heat. */
	public void processHeat() throws RemoteException;
									 
// Damage methods
	/** Inflict specified damage to a body location. */
	public String takeDamage(int damage, String hitArc) throws RemoteException;

	/** Inflict specified damage to an upper body location. */
	public String takePunchDamage(int damage, String hitArc) throws RemoteException;

	/** Inflict specified damage to a lower body location. */
	public String takeKickDamage(int damage, String hitArc) throws RemoteException;

	/** Inflict damage to multiple body locations. */
	public String takeClusterDamage(int damage, int damage_per_group, String hitArc) throws RemoteException;

	/** Inflict damage to multiple upper body locations. */
	public String takeClusterPunchDamage(int damage, int damage_per_group, String hitArc) throws RemoteException;
}