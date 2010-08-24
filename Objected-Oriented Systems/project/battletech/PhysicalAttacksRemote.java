package battletech;

import java.rmi.*;

/** This is the interface for the collection of physical attacks of a Mech. */

public interface PhysicalAttacksRemote extends Remote
{
// Query methods

	/** Returns true if right punch has already been executed. */
	public boolean rightPunched() throws RemoteException;

	/** Returns true if left punch has already been executed. */
	public boolean leftPunched() throws RemoteException;

	/** Returns true if kick has already been executed. */
	public boolean kicked() throws RemoteException;

// Modify methods

	/** Permit the Mech to punch and kick. */
	public void activate() throws RemoteException;

	/** Punch specified target, using specified arm (0 is right arm, 1 is left arm). */
	public String punch(MechRemote target, int side) throws RemoteException;

	/** Kick specified target. */
	public String kick(MechRemote target) throws RemoteException;
}