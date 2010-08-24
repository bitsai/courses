package battletech;

/** This class represents projectile weapons. */

public class ProjectileWeapon extends Weapon implements java.io.Serializable
{
	public ProjectileWeapon(String _name, String _type, int h, int d, int rMin, int rS, int rMed, int rL, int size, BodyPart _location, MechRemote _owner)
	{
		super(_name, _type, h, d, rMin, rS, rMed, rL, size, _location, _owner);
	}
}