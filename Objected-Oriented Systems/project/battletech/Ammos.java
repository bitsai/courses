package battletech;

import java.util.*;
import java.rmi.server.*;

/** This class represents the collection of all ammunition items in a Mech. */

public class Ammos extends UnicastRemoteObject implements AmmosRemote
{
	private MechRemote owner;
	private ArrayList ammos = new ArrayList(0);

// Constructor

	public Ammos(MechRemote _owner) throws java.rmi.RemoteException
	{
		owner = _owner;
	}

// Query

	/** Returns number of Ammo objects in this item. */
	public synchronized int getSize()
	{ return(ammos.size()); }

	/** Returns a specific Ammo object, referenced by index number. */
	public synchronized Ammo getAmmo(int num)
	{
		Ammo ammo = (Ammo) ammos.get(num);
		return (ammo);
	}

	/** Returns a specific Ammo object, referenced by name. */
	public synchronized Ammo getAmmo(String name)
	{
		Ammo ammo = getAmmo(0);

		for (int count = 0; count < getSize(); count++)
		{
			ammo = getAmmo(count);

			if (ammo.getName().equalsIgnoreCase(name))
			{ return ammo; }
		}

		return ammo;
	}

	/** Returns the number of shots remaining for a specific ammunition type. */
	public synchronized int getAmmoCount(String ammoType)
	{
		if (ammos.size() == 0) // No ammo at all!
		{ return 0; }

		int ammoCount = 0;
		Ammo ammo;

		for (int count = 0; count < getSize(); count++)
		{
			ammo = getAmmo(count);

			if (ammo.getType().equalsIgnoreCase(ammoType))
			{ ammoCount = ammoCount + ammo.getShots(); }
		}

		return ammoCount;
	}

// Modify

	/** Adds an Ammo object to this item. */
	public synchronized void addAmmo(Ammo a)
	{ ammos.add(a); }

	/** Removes an Ammo object from this item. */
	public synchronized void removeAmmo(Ammo a)
	{ ammos.remove(a); }

	/** Use up a shot of an ammunition type. */
	public synchronized void useAmmo(String type)
	{
		Ammo ammo;

		for (int count = 0; count < getSize(); count++)
		{
			ammo = getAmmo(count);

			if (ammo.getType().equalsIgnoreCase(type))
			{
				ammo.use();
				return;
			}
		}
	}

	/** Explodes all the Ammo objects in a specific location. */
	public synchronized void ammoExplosion(String location) throws java.rmi.RemoteException
	{
		owner.getOwner().addOutput(location + " ammo explosion");
		owner.getMW().takeDamage(2);
		Ammo ammo;

		for (int count = 0; count < getSize(); count++)
		{
			ammo = getAmmo(count);

			if (ammo.getLocation().equalsIgnoreCase(location))
			{ ammo.explode(); }
		}
	}

	/** Chooses the Ammo object with the highest damage value, and explodes all Ammo objects in the same location. */
	public synchronized void overheat() throws java.rmi.RemoteException
	{
		if (getSize() == 0) // No ammo to explode
		{ return; }

		Ammo ammoLargest = getAmmo(0);
		Ammo ammoCurrent;

		for (int count = 0; count < getSize(); count++) // Get ammo with largest Damage Value
		{
			ammoCurrent = getAmmo(count);

			if (ammoCurrent.getDamageValue() > ammoLargest.getDamageValue())
			{ ammoLargest = ammoCurrent; }
		}

		String location = ammoLargest.getLocation();
		ammoExplosion(location);
	}
}