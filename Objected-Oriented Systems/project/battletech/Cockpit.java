package battletech;

/** This class represents the cockpit of a Mech. */

public class Cockpit extends Component implements java.io.Serializable
{
	public Cockpit(BodyPart _location, MechRemote _owner)
	{
		super("Cockpit", "Cockpit", 1, _location, _owner);
	}

	/** Destroys the cockpit. */
	public void destroy()
	{
		try
		{
			owner.getOwner().addOutput(getName() + " destroyed!");
			owner.getComponents().removeComponent(this);
			owner.getMW().kill();
		}
		catch(java.rmi.RemoteException e)
		{
			System.out.println("Cockpit RemoteException!");
		}
	}
}