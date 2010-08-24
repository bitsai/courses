package battletech;

/** This class represents component items. */

public class Component implements java.io.Serializable
{
	/** The name of this component. */
	protected String name;
	/** The type of this component. */
	protected String type;
	/** The number of hits taken by this component. */
	protected int hits = 0;
	/** The location of this component. */
	protected BodyPart location;
	/** The Mech this component belongs to. */
	protected MechRemote owner;

// Constructor

	public Component(String _name, String _type, int size, BodyPart _location, MechRemote _owner)
	{
		name = _name;
		type = _type;

		location = _location;
		owner = _owner;

		location.addComponent(this, size);
		
		try
		{
			owner.getComponents().addComponent(this);
		}
		catch(java.rmi.RemoteException e)
		{
			System.out.println("Component class RemoteException");
		}
	}

// Query methods

	/** Returns the name of this component item. */
	public String getName() { return(name); }

	/** Returns the type of this component item. */
	public String getType() { return(type); }

	/** Returns the number of hits taken by this component item. */
	public int getHits() { return(hits); }

	/** Returns the location of this component item. */
	public String getLocation() { return(location.getType()); }

// Modifying methods

	/** Destroy this component item. */
	public void destroy()
	{
		try
		{
			owner.getOwner().addOutput(getName() + " destroyed!");
			owner.getComponents().removeComponent(this);
		}
		catch(java.rmi.RemoteException e)
		{
			System.out.println("Component RemoteException!");
		}
	}

	/** Inflict damage on this component item. */
	public void removeSlot()
	{
		hits++;
		destroy();
	}

	/** Inflict a critical hit on this component item. */
	public void criticalHit() 
	{
		try
		{
			owner.getOwner().addOutput(getName() + " hit!");
			removeSlot(); 
		}
		catch(java.rmi.RemoteException e)
		{
			System.out.println("Component RemoteException!");
		}
	}
}