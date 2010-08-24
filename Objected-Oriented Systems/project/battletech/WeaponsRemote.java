package battletech;

import java.rmi.*;

/** This is the interface for the collection of all weapons in a Mech. */

public interface WeaponsRemote extends Remote
{
// Query

	/** Returns number of weapons contained. */
	public int getSize() throws RemoteException;

	/** Returns a weapon, referenced by index number. */
	public Weapon getWeapon(int num) throws RemoteException;

	/** Returns a weapon, referenced by name. */
	public Weapon getWeapon(String name) throws RemoteException;

	/** Returns true if weapons in the right arm fired already, false otherwise. */
	public boolean rArmWeaponsFired() throws RemoteException;

	/** Returns true if weapons in the left arm fired already, false otherwise. */
	public boolean lArmWeaponsFired() throws RemoteException;

	/** Returns true if weapons in the legs fired already, false otherwise. */
	public boolean legWeaponsFired() throws RemoteException;

// Modify

	/** Permit all weapons to be fired. */
	public void activate() throws RemoteException;

	/** Notify collection that weapons in the right arm fired. */
	public void rArmWeaponFire() throws RemoteException;

	/** Notify collection that weapons in the left arm fired. */
	public void lArmWeaponFire() throws RemoteException;

	/** Notify collection that weapons in the legs fired. */
	public void legWeaponFire() throws RemoteException;

	/** Add a weapon. */
	public void addWeapon(Weapon w) throws RemoteException;

	/** Remove a weapon. */
	public void removeWeapon(Weapon w) throws RemoteException;

	/** Fire the weapon referenced by index number at specified target. */
	public String fireWeapon(int num, MechRemote target) throws RemoteException;

	/** Fire the weapon referenced by name at specified target. */
	public String fireWeapon(String name, MechRemote target) throws RemoteException;
}