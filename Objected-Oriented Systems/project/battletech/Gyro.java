package battletech;

/** This class represents the gyro of a Mech. */

public class Gyro extends Component implements java.io.Serializable
{
	public Gyro(BodyPart _location, MechRemote _owner)
	{
		super("Gyro", "Gyro", 4, _location, _owner);
	}

	/** Destroy the gyro. */
	public void destroy()
	{
		try
		{
			owner.getOwner().addOutput(getName() + " destroyed!");
			owner.getComponents().removeComponent(this);
			owner.fall();
		}
		catch(java.rmi.RemoteException e)
		{
			System.out.println("Gyro RemoteException!");
		}
	}

	/** Inflict damage on the gyro. */
	public void removeSlot()
	{
		hits++;

		if (hits == 2) // Max = 2
		{ destroy(); }

		Rules.rollPiloting(3, owner);
	}
}