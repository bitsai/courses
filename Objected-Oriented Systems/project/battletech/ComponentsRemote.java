package battletech;

import gameserver.*;
import java.rmi.*;

/** This is the interface for the collection of all component items in a Mech. */

public interface ComponentsRemote extends Remote
{
	
// Query
	/** Returns number of component items contained. */
	public int getSize() throws RemoteException;

	/** Returns a component item, referenced by index number. */
	public Component getComponent(int num) throws RemoteException;

	/** Returns a component item, referenced by name. */
	public Component getComponent(String name) throws RemoteException;

	/** Returns number of hits taken by the engine. */
	public int getEngineHits() throws RemoteException;

	/** Returns number of hits taken by the gyro. */
	public int getGyroHits() throws RemoteException;

	/** Returns number of hits taken by the sensors. */
	public int getSensorsHits() throws RemoteException;

	/** Returns number of hits taken by the life support. */
	public int getLifeSupportHits() throws RemoteException;

	/** Returns total number of jumpjets in a Mech. */
	public int getJumpJets() throws RemoteException;

	/** Returns total number of jumpjets not in the legs in a Mech. */
	public int getTorsoJumpJets() throws RemoteException;

// Modify

	/** Add a component item. */
	public void addComponent(Component c) throws RemoteException;

	/** Remove a component item. */
	public void removeComponent(Component c) throws RemoteException;
}