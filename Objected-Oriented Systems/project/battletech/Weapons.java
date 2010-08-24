package battletech;

import java.util.*;
import java.rmi.*;
import java.rmi.server.*;

/** This class represents the collection of all weapons in a Mech. */

public class Weapons extends UnicastRemoteObject implements WeaponsRemote
{
	private MechRemote owner;

	private boolean rArmWeaponsFired = false;
	private boolean lArmWeaponsFired = false;
	private boolean legWeaponsFired = false;

	private ArrayList weapons = new ArrayList(0);

// Constructor

	public Weapons(MechRemote _owner) throws RemoteException
	{ owner = _owner; }

// Query

	/** Returns number of weapons contained. */
	public synchronized int getSize()
	{ return weapons.size(); }

	/** Returns a weapon, referenced by index number. */
	public synchronized Weapon getWeapon(int num)
	{
		Weapon weapon = (Weapon) weapons.get(num);
		return (weapon);
	}

	/** Returns a weapon, referenced by name. */
	public synchronized Weapon getWeapon(String name)
	{
		Weapon weapon = getWeapon(0);

		for (int count = 0; count < getSize(); count++)
		{
			weapon = getWeapon(count);

			if (weapon.getName().equalsIgnoreCase(name))
			{ return weapon; }
		}

		return weapon;
	}

	/** Returns true if weapons in the right arm fired already, false otherwise. */
	public synchronized boolean rArmWeaponsFired() { return(rArmWeaponsFired); }

	/** Returns true if weapons in the left arm fired already, false otherwise. */
	public synchronized boolean lArmWeaponsFired() { return(lArmWeaponsFired); }

	/** Returns true if weapons in the legs fired already, false otherwise. */
	public synchronized boolean legWeaponsFired() { return(legWeaponsFired); }

// Modify

	/** Permit all weapons to be fired. */
	public synchronized void activate()
	{
		rArmWeaponsFired = false;
		lArmWeaponsFired = false;
		legWeaponsFired = false;
		Weapon weapon;

		for (int count = 0; count < getSize(); count++)
		{
			weapon = getWeapon(count);
			weapon.activate(); 
		}
	}

	/** Notify collection that weapons in the right arm fired. */
	public synchronized void rArmWeaponFire() { rArmWeaponsFired = true; }

	/** Notify collection that weapons in the left arm fired. */
	public synchronized void lArmWeaponFire() { lArmWeaponsFired = true; }

	/** Notify collection that weapons in the legs fired. */
	public synchronized void legWeaponFire() { legWeaponsFired = true; }

	/** Add a weapon. */
	public synchronized void addWeapon(Weapon w) { weapons.add(w); }

	/** Remove a weapon. */
	public synchronized void removeWeapon(Weapon w) { weapons.remove(w); }

	/** Fire the weapon referenced by index number at specified target. */
	public synchronized String fireWeapon(int num, MechRemote target)
	{
		if (num >= getSize())
		{ return("Weapon not found!"); }

		Weapon weapon = getWeapon(num);
		String output = weapon.fireWeapon(target);

		return output;
	}

	/** Fire the weapon referenced by name at specified target. */
	public synchronized String fireWeapon(String name, MechRemote target)
	{
		String output = "Weapon not found!";
		Weapon weapon;

		for (int count = 0; count < getSize(); count++)
		{
			weapon = getWeapon(count);

			if (weapon.getName().equalsIgnoreCase(name))
			{ output = weapon.fireWeapon(target); }
		}

		return output;
	}
}