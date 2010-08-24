package battletech;

/** This class represents missile weapons. */

public class MissileWeapon extends Weapon implements java.io.Serializable
{
	private int missiles;
	private int damage_per_group;

// Constructor method

	public MissileWeapon(String _name, String _type, int h, int d, int rMin, int rS, int rMed, int rL, int size, BodyPart _location, MechRemote _owner)
	{
		super(_name, _type, h, d, rMin, rS, rMed, rL, size, _location, _owner);

		missiles = Integer.parseInt(type.substring(3));

		if (type.substring(0, 2).equalsIgnoreCase("LRM")) // Weapon is an LRM
		{ damage_per_group = 5; }

		damage_per_group = 2; // Weapon is an SRM
	}

// Modifying methods

	/** Do missile damage to target. */
	public String inflictDamage(MechRemote target)
	{
		int missileHits = Tables.missileHits(missiles);
		String output = missileHits + " Missiles Hit!";

		try
		{
			int missileDamage = missileHits * damage;
			String hitArc = Rules.getArc(target.getFacing(), target.getLocation(), owner.getLocation());

			if (owner.getMap().hasPartialCover(owner, target))
			{ output = output + target.takeClusterPunchDamage(missileDamage, damage_per_group, hitArc); }
			else
			{ output = output + target.takeClusterDamage(missileDamage, damage_per_group, hitArc); }
		}
		catch(java.rmi.RemoteException e)
		{
			System.out.println("MissileWeapon RemoteException!");
		}

		return output;
	}
}