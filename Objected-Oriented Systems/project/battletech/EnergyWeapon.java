package battletech;

/** This class represents energy weapons. */

public class EnergyWeapon extends Weapon implements java.io.Serializable
{
// Constructor
	public EnergyWeapon(String _name, String _type, int h, int d, int rMin, int rS, int rMed, int rL, int size, BodyPart _location, MechRemote _owner)
	{
		super(_name, _type, h, d, rMin, rS, rMed, rL, size, _location, _owner);
	}

// Query

	/** Returns true always, since energy weapons don't need ammunition. */
	public boolean hasAmmo()
	{ return true; }

	/** Returns string representation of this energy weapon. */
	public String toString()
	{
		String output = getName() + " " + getLocation();
		return output;
	}
}