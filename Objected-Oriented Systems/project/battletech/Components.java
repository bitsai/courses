package battletech;

import java.util.*;
import java.rmi.*;
import java.rmi.server.*;

/** This class represents the collection of all component items in a Mech. */

public class Components extends UnicastRemoteObject implements ComponentsRemote
{
	private MechRemote owner;
	private ArrayList components = new ArrayList(0);

// Constructor

	public Components(MechRemote _owner) throws RemoteException
	{ owner = _owner; }

// Query

	/** Returns number of component items contained. */
	public synchronized int getSize() { return(components.size()); }

	/** Returns a component item, referenced by index number. */
	public synchronized Component getComponent(int num)
	{
		Component component = (Component) components.get(num);
		return (component);
	}

	/** Returns a component item, referenced by name. */
	public synchronized Component getComponent(String name)
	{
		Component component = getComponent(0);

		for (int count = 0; count < getSize(); count++)
		{
			component = getComponent(count);

			if (component.getName().equalsIgnoreCase(name))
			{ return component; }
		}

		return component;
	}

	/** Returns number of hits taken by the engine. */
	public synchronized int getEngineHits()
	{
		Component current;

		for (int count = 0; count < getSize(); count++)
		{
			current = getComponent(count);

			if (current.getType().equalsIgnoreCase("Engine"))
			{ return(current.getHits()); }
		}

		return 3;
	}

	/** Returns number of hits taken by the gyro. */
	public synchronized int getGyroHits()
	{
		Component current;

		for (int count = 0; count < getSize(); count++)
		{
			current = getComponent(count);

			if (current.getType().equalsIgnoreCase("Gyro"))
			{ return(current.getHits()); }
		}

		return 2;
	}

	/** Returns number of hits taken by the sensors. */
	public synchronized int getSensorsHits()
	{
		Component current;

		for (int count = 0; count < getSize(); count++)
		{
			current = getComponent(count);

			if (current.getType().equalsIgnoreCase("Sensors"))
			{ return(current.getHits()); }
		}

		return 2;
	}

	/** Returns number of hits taken by the life support. */
	public synchronized int getLifeSupportHits()
	{
		Component current;

		for (int count = 0; count < getSize(); count++)
		{
			current = getComponent(count);

			if (current.getType().equalsIgnoreCase("LifeSupport"))
			{ return(current.getHits()); }
		}

		return 1;
	}

	/** Returns total number of jumpjets in a Mech. */
	public synchronized int getJumpJets()
	{
		int jumpJets = 0;
		Component current;

		for (int count = 0; count < getSize(); count++)
		{
			current = getComponent(count);

			if (current.getType().equalsIgnoreCase("JumpJet"))
			{ jumpJets++; }
		}

		return jumpJets;
	}

	/** Returns total number of jumpjets not in the legs in a Mech. */
	public synchronized int getTorsoJumpJets()
	{
		int jumpJets = 0;
		Component current;

		for (int count = 0; count < getSize(); count++)
		{
			current = getComponent(count);
			String location = current.getLocation();

			if (current.getType().equalsIgnoreCase("JumpJet") && (location.substring(1).equalsIgnoreCase("Torso")))
			{ jumpJets++; }
		}

		return jumpJets;
	}

// Modify

	/** Add a component item. */
	public synchronized void addComponent(Component c) { components.add(c); }

	/** Remove a component item. */
	public synchronized void removeComponent(Component c) { components.remove(c); }
}