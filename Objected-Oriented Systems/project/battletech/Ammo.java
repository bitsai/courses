package battletech;

/** This class represents ammunition items. */

public class Ammo extends Component implements java.io.Serializable
{
	private int damage = 0;
	private int shots = 0;

// Constructor methods	

	public Ammo(String _name, String _type, int _damage, int _shots, BodyPart _location, MechRemote _owner)
	{
		super(_name, _type, 1, _location, _owner);

		damage = _damage;
		shots = _shots;

		try
		{
			owner.getAmmos().addAmmo(this);
		}
		catch(java.rmi.RemoteException e)
		{
			System.out.println("Ammo class RemoteException");
		}
	}

// Query methods

	/** Returns the number of shots currently contained by this ammunition item. */
	public int getShots()
	{ return(shots); }

	/** Returns the amount of damage this item does in case of an ammunition explosion. */
	public int getDamageValue()
	{
		int dv = damage * shots;
	
		return(dv);
	}

// Modifying methods

	/** Use up a shot from this ammunition item. */
	public void use()
	{ 
		shots--;

		if (shots == 0)
		{
			destroy();
		}
	}

	/** Destroy this ammunition item. */
	public void destroy()
	{
		try
		{
			owner.getOwner().addOutput(getName() + " destroyed!"); 
			owner.getComponents().removeComponent(this);
			owner.getAmmos().removeAmmo(this);
		}
		catch(java.rmi.RemoteException e)
		{
			System.out.println("Ammo RemoteException!");
		}
	}

	/** Explode this ammunition item. */
	public void explode()
	{
		removeSlot();

		int dmg = damage * shots;
		location.ammoDamage(dmg);
	}		

	/** Inflict a critical hit on this ammunition item. */
	public void criticalHit()
	{
		try
		{
			owner.getOwner().addOutput(getName() + " hit!"); 
			owner.getAmmos().ammoExplosion(getLocation());
		}
		catch(java.rmi.RemoteException e)
		{
			System.out.println("Ammo RemoteException!");
		}
	}
}