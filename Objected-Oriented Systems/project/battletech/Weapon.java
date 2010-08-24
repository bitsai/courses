package battletech;

/** This class represents weapons. */

public class Weapon extends Component implements java.io.Serializable
{
	/** Heat generated. */
	protected int heat;

	/** Damage inflicted. */
	protected int damage;

	/** Minimum range. */
	protected int rangeMin;

	/** Short range. */
	protected int rangeShort;

	/** Medium range. */
	protected int rangeMedium;

	/** Long range. */
	protected int rangeLong;

	/** True if this weapon fired already, false otherwise. */
	protected boolean fired = false;

	public Weapon(String _name, String _type, int h, int d, int rMin, int rS, int rMed, int rL, int size, BodyPart _location, MechRemote _owner)
	{
		super(_name, _type, size, _location, _owner);

		heat = h;
		damage = d;

		rangeMin = rMin;
		rangeShort = rS;
		rangeMedium = rMed;
		rangeLong = rL;

		try
		{
			owner.getWeapons().addWeapon(this);
		}
		catch(java.rmi.RemoteException e)
		{
			System.out.println("Weapon RemoteException!");
		}
	}

// Query methods

	/** Return heat generated. */
	public int getHeat() { return(heat); }

	/** Return damage inflicted. */
	public int getDamage() { return(damage); }

	/** Return minimum range. */
	public int getMinRange() { return(rangeMin); }

	/** Return short range. */
	public int getShortRange() { return(rangeShort); }

	/** Return medium range. */
	public int getMediumRange() { return(rangeMedium); }

	/** Return long range. */
	public int getLongRange() { return(rangeLong); }

	/** Returns true if this weapon has ammunition left, false otherwise. */
	public boolean hasAmmo()
	{
		if (getAmmo() == 0)
		{ return false; }

		return true;
	}

	/** Returns amount of ammunition left to this weapon. */
	public int getAmmo()
	{
		int ammoAmount = 0;

		try
		{
			ammoAmount = owner.getAmmos().getAmmoCount(type);
		}
		catch(java.rmi.RemoteException e)
		{
			System.out.println("Weapon RemoteException!");
		}

		return(ammoAmount);
	}

	/** Returns true if target is in the firing arc(s) of this weapon, false otherwise. */
	public boolean checkFiringArc(MechRemote target)
	{
		try
		{
			String firingArc = Rules.getArc(owner.getTorsoFacing(), owner.getLocation(), target.getLocation());

			if (location.getType().substring(1).equalsIgnoreCase("Leg"))
			{ firingArc = Rules.getArc(owner.getFacing(), owner.getLocation(), target.getLocation()); }

			if (firingArc.equalsIgnoreCase("Front") && !name.substring(0, 3).equalsIgnoreCase("(R)"))
			{ return(true); }

			if ((firingArc.equalsIgnoreCase("Right")) && (location.getType().equalsIgnoreCase("RArm")))
			{ return(true); }

			if ((firingArc.equalsIgnoreCase("Left")) && (location.getType().equalsIgnoreCase("LArm")))
			{ return(true); }

			if (name.substring(0, 3).equalsIgnoreCase("(R)") && firingArc.equalsIgnoreCase("Rear"))
			{ return(true); }
		}
		catch(java.rmi.RemoteException e)
		{
			System.out.println("Weapon RemoteException!");
		}		

		return(false);
	}

	/** Returns string representation of this weapon. */
	public String toString()
	{
		String output = getName() + " " + getLocation() + " " + getAmmo();
		return output;
	}

// Modify methods

	/** Permit this weapon to be fired. */
	public void activate() { fired = false; }

	/** Destroy this weapon. */
	public void destroy()
	{
		try
		{
			owner.getComponents().removeComponent(this);
			owner.getWeapons().removeWeapon(this);
		}
		catch(java.rmi.RemoteException e)
		{
			System.out.println("Weapon class RemoteException!");
		}
	}

	/** Do this weapon's damage to target. */
	public String inflictDamage(MechRemote target)
	{
		try
		{
			String output = "";
			String hitArc = Rules.getArc(target.getFacing(), target.getLocation(), owner.getLocation());

			if (owner.getMap().hasPartialCover(owner, target))
			{ output = target.takePunchDamage(damage, hitArc); }
			else
			{ output = target.takeDamage(damage, hitArc); }

			return output;
		}
		catch(java.rmi.RemoteException e)
		{
			System.out.println("Weapon class RemoteException!");
		}

		return("Error");
	}

	/** Fire this weapon. */
	public String fireWeapon(MechRemote target)
	{
		try
		{
			if (owner.getMW().isUnconscious())
			{ return("MechWarrior is unconscious!"); }

			if (owner.isShutdown())
			{ return("Mech shutdown!"); }

			if (!owner.isActive())
			{ return("Mech not active!"); }

			if (!owner.getMap().lineOfSight(owner, target))
			{ return("No line of sight!"); }

			if (owner.getComponents().getSensorsHits() == 2) // Can't fire if sensors destroyed
			{ return("Sensors destroyed!"); }

			if (fired) // Can't fire more than once
			{ return("Weapon already fired!"); }

			if (getLocation().equalsIgnoreCase("RArm") && owner.getPhysicalAttacks().rightPunched())
			{ return("Arm already punched!"); }

			if (getLocation().equalsIgnoreCase("LArm") && owner.getPhysicalAttacks().leftPunched())
			{ return("Arm already punched!"); }

			if (getLocation().substring(1).equalsIgnoreCase("Leg") && owner.getPhysicalAttacks().kicked())
			{ return("Legs already kicked!"); }

			if ((owner.getMap().hasPartialCover(target, owner)) && (location.getType().substring(1).equalsIgnoreCase("Leg")))
			{ return("Weapon blocked by terrain!"); }

			if (checkFiringArc(target) == false) // Can't fire on targets outside firing arc
			{  return("Target outside firing arc!"); }

			double distance = Rules.getDistance(owner.getLocation(), target.getLocation());
			int range = (int) distance / owner.getMap().getMapGridSize();

			if (rangeLong < range) // If target is out of weapon range, do not shoot
			{ return("Target out of range!"); }

			if (!hasAmmo()) // If weapon does not have ammo left, do not shoot
			{ return("Weapon out of ammo!"); }

	// Fired

			owner.getAmmos().useAmmo(type); // Use ammo
			owner.addHeat(heat); // Shooter mech gets more heat

			fired = true;

			if (getLocation().equalsIgnoreCase("RArm"))
			{ owner.getWeapons().rArmWeaponFire(); }

			if (getLocation().equalsIgnoreCase("LArm"))
			{ owner.getWeapons().lArmWeaponFire(); }

			if (getLocation().substring(1).equalsIgnoreCase("Leg"))
			{ owner.getWeapons().legWeaponFire(); }
		
			int roll = Rules.rollDice();
			int difficulty = Rules.getGunneryDifficulty(owner, target, this); // See if pilot makes the shot

			if (roll < difficulty) // If pilot misses, stop
			{ return(getName() + " missed! Difficulty: " + difficulty + " Roll: " + roll); }

			String output = inflictDamage(target);
			return(getName() + " hit! Difficulty: " + difficulty + " Roll: " + roll + "\n" + output);
		}
		catch(java.rmi.RemoteException e)
		{
			System.out.println("Weapon class RemoteException!");
		}

		return("Error");
	}
}