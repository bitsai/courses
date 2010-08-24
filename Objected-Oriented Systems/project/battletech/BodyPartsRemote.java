package battletech;

import gameserver.*;
import java.rmi.*;

/** This is the interface for the collection of all body locations in a Mech. */

public interface BodyPartsRemote extends Remote
{
// Query

	/** Returns the number of body locations contained. */
	public int getSize() throws RemoteException;

	/** Returns a body location, referenced by index number. */
	public BodyPart getBodyPart(int num) throws RemoteException;

	/** Returns a body location, referenced by name. */
	public BodyPart getBodyPart(String location) throws RemoteException;

	/** Returns true if the right arm isn't destroyed yet, false otherwise. */
	public boolean rArm() throws RemoteException;

	/** Returns true if the left arm isn't destroyed yet, false otherwise. */
	public boolean lArm() throws RemoteException;

	/** Returns the number of legs remaining. */
	public int legs() throws RemoteException;

	/** Returns amount of total internal structure remaining in a Mech. */
	public int getIS() throws RemoteException;

	/** Returns amount of internal structure remaining in a specific body location. */	
	public int getIS(String location) throws RemoteException;
	
	/** Returns max internal structure in a specific body location. */
	public int getMaxIS(String location) throws RemoteException;
		
	/** Returns amount of total armor remaining in a Mech. */
	public int getArmor() throws RemoteException;

	/** Returns amount of armor remaining in a specific body location and side. */
	public int getArmor(String location, String side) throws RemoteException;

	/** Returns max armor in a specific body location and side. */	
	public int getMaxArmor(String location, String side) throws RemoteException;

// Modify

	/** Add specified body location. */
	public void addBodyPart(BodyPart bp) throws RemoteException;

	/** Destroy specified body location. */
	public void destroyBodyPart(String location) throws RemoteException;

	/** Damage specified body location. */
	public String damageBodyPart(String location, int damage, String side) throws RemoteException;

	/** Transfer excess damage from specified body location to next logical body location. */
	public String transferDamage(String location, int damage, String side) throws RemoteException;

	/** Transfer critical from specified body location to next logical body location. */
	public void transferCriticalHit(String location) throws RemoteException;

	/** Transfer excess ammunition explosion damage from specified body location to next logical body location. */
	public void transferAmmoDamage(String location, int damage) throws RemoteException;
}