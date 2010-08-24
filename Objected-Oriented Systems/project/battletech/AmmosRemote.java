package battletech;

import java.rmi.*;

/** This is the interface for the collection of all ammunition items in a Mech. */

public interface AmmosRemote extends Remote
{
// Query methods
	/** Returns number of Ammo objects in this item. */
	public int getSize() throws RemoteException;

	/** Returns a specific Ammo object, referenced by index number. */
	public Ammo getAmmo(int num) throws RemoteException;

	/** Returns a specific Ammo object, referenced by name. */
	public Ammo getAmmo(String name) throws RemoteException;

	/** Returns the number of shots remaining for a specific ammunition type. */
	public int getAmmoCount(String ammoType) throws RemoteException;

// Modify methods

	/** Adds an Ammo object to this item. */
	public void addAmmo(Ammo a) throws RemoteException;

	/** Removes an Ammo object from this item. */
	public void removeAmmo(Ammo a) throws RemoteException;

	/** Use up a shot of an ammunition type. */
	public void useAmmo(String type) throws RemoteException;

	/** Explodes all the Ammo objects in a specific location. */
	public void ammoExplosion(String location) throws RemoteException;

	/** Chooses the Ammo object with the highest damage value, and explodes all Ammo objects in the same location. */
	public void overheat() throws RemoteException;
}