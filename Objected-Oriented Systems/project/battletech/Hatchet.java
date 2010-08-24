package battletech;

/** This class represents hatchets. */

public class Hatchet extends Weapon implements java.io.Serializable
{
// Constructor method

	public Hatchet(int damage, int size, BodyPart _location, MechRemote _owner)
	{
		super("Hatchet", "Hatchet", 0, damage, 0, 1, 1, 1, size, _location, _owner);
	}

// Modifying methods

	/** Returns true if the target is in the front firing arc. */
	public boolean checkFiringArc(MechRemote target)
	{
		try
		{
			String firingArc = Rules.getArc(owner.getTorsoFacing(), owner.getLocation(), target.getLocation());

			if (firingArc.equalsIgnoreCase("Front")) // Hatchet can only hit targets in front arc
			{ return(true); }
		}
		catch(java.rmi.RemoteException e)
		{
			System.out.println("Hatchet RemoteException!");
		}

		return(false);
	}
}