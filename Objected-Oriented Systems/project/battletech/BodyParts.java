package battletech;

import java.util.*;
import java.rmi.*;
import java.rmi.server.*;

/** This class represents the collection of all body locations in a Mech. */

public class BodyParts extends UnicastRemoteObject implements BodyPartsRemote
{
	private MechRemote owner;
	private ArrayList bodyParts = new ArrayList(0);

// Constructor

	public BodyParts(MechRemote _owner) throws RemoteException
	{ owner = _owner; }

// Query

	/** Returns the number of body locations contained. */
	public synchronized int getSize()
	{ return(bodyParts.size()); }

	/** Returns a body location, referenced by index number. */
	public synchronized BodyPart getBodyPart(int num)
	{
		BodyPart bodyPart = (BodyPart) bodyParts.get(num);
		return (bodyPart);
	}

	/** Returns a body location, referenced by name. */
	public synchronized BodyPart getBodyPart(String location)
	{
		BodyPart bodyPart = null; // See if errors show up

		for (int count = 0; count < getSize(); count++)
		{
			bodyPart = getBodyPart(count);

			if (bodyPart.getType().equalsIgnoreCase(location))
			{ return bodyPart; }
		}

		return bodyPart;
	}

	/** Returns true if the right arm isn't destroyed yet, false otherwise. */
	public synchronized boolean rArm()
	{
		if (getBodyPart("RArm").isDestroyed())
		{ return false; }

		return true;
	}

	/** Returns true if the left arm isn't destroyed yet, false otherwise. */
	public synchronized boolean lArm()
	{
		if (getBodyPart("LArm").isDestroyed())
		{ return false; }

		return true;
	}

	/** Returns the number of legs remaining. */
	public synchronized int legs()
	{
		int legs = 2;

		if (getBodyPart("RLeg").isDestroyed())
		{ legs--; }

		if (getBodyPart("LLeg").isDestroyed())
		{ legs--; }

		return(legs);
	}

	/** Returns amount of total internal structure remaining in a Mech. */
	public synchronized int getIS()
	{
		int IS = 0;
		BodyPart current;

		for (int count = 0; count < getSize(); count++)
		{
			current = getBodyPart(count);
			IS = IS + current.getIS();
		}

		return(IS);
	}

	/** Returns amount of internal structure remaining in a specific body location. */	
	public synchronized int getIS(String location)
	{
		BodyPart bp = getBodyPart(location);
		return bp.getIS();
	}

	/** Returns max internal structure in a specific body location. */	
	public synchronized int getMaxIS(String location)
	{
		BodyPart bp = getBodyPart(location);
		return bp.getMaxIS();
	}

	/** Returns amount of total armor remaining in a Mech. */		
	public synchronized int getArmor()
	{
		int armor = 0;
		BodyPart current;

		for (int count = 0; count < getSize(); count++)
		{
			current = getBodyPart(count);;
			armor = armor + current.getArmor("Front");
			armor = armor + current.getArmor("Rear");
		}

		return(armor);
	}

	/** Returns amount of armor remaining in a specific body location and side. */
	public synchronized int getArmor(String location, String side)
	{
		BodyPart bp = getBodyPart(location);
		return bp.getArmor(side);
	}
	
	/** Returns max armor in a specific body location and side. */
	public synchronized int getMaxArmor(String location, String side)
	{
		BodyPart bp = getBodyPart(location);
		return bp.getMaxArmor(side);		
	}

// Modify

	/** Add specified body location. */
	public synchronized void addBodyPart(BodyPart bp)
	{ bodyParts.add(bp); }

	/** Destroy specified body location. */
	public synchronized void destroyBodyPart(String location)
	{
		BodyPart bp = getBodyPart(location);
		bp.destroy();
	}

	/** Damage specified specific body location. */
	public synchronized String damageBodyPart(String location, int damage, String side)
	{
		String output;
		boolean criticalHits = false;

		if (location.substring(0, 3).equalsIgnoreCase("(C)")) // A 2 rolled on the hitlocation table; critical hits on the location
		{
			location = location.substring(3);
			criticalHits = true;
		}

		BodyPart bp = getBodyPart(location);
		output = bp.damageArmor(damage, side);

		if (criticalHits)
		{ bp.determineCriticalHits(); }

		return output;
	}

	/** Transfer excess damage from specified body location to next logical body location. */
	public synchronized String transferDamage(String location, int damage, String side)
	{
		if (location.equalsIgnoreCase("CTorso")) { return(""); }

		if (location.equalsIgnoreCase("Head")) { return(""); }

		String newLocation = Rules.transfer(location);
		return damageBodyPart(newLocation, damage, side);
	}

	/** Transfer critical from specified body location to next logical body location. */
	public synchronized void transferCriticalHit(String location)
	{
		if (location.equalsIgnoreCase("CTorso")) { return; }

		if (location.equalsIgnoreCase("Head")) { return; }

		String newLocation = Rules.transfer(location);
		BodyPart bp = getBodyPart(newLocation);
		bp.criticalHit();
	}

	/** Transfer excess ammunition explosion damage from specified body location to next logical body location. */
	public synchronized void transferAmmoDamage(String location, int damage)
	{
		if (location.equalsIgnoreCase("CTorso")) { return; }

		if (location.equalsIgnoreCase("Head")) { return; }

		String newLocation = Rules.transfer(location);
		BodyPart bp = getBodyPart(newLocation);
		bp.ammoDamage(damage);
	}
}