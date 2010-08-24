package battletech;

import java.util.*;

/** This class represents Mech body locations. */

public class BodyPart implements java.io.Serializable
{
	private String type;
	private int armorFront;
	private int armorRear;
	private int internalStructure;

	private int maxArmorFront;
	private int maxArmorRear;
	private int maxInternalStructure;
	private boolean destroyed = false;

	private MechRemote owner;
	private ArrayList criticals = new ArrayList(0);

// Constructor methods

	public BodyPart(String t, int aF, int aR, int IS, MechRemote _owner)
	{
		type = t;

		armorFront = aF;
		armorRear = aR;
		internalStructure = IS;

		maxArmorFront = aF;
		maxArmorRear = aR;
		maxInternalStructure = IS;

		owner = _owner;
		
		try
		{
			owner.getBodyParts().addBodyPart(this);
		}
		catch(java.rmi.RemoteException e)
		{
			System.out.println("BodyPart class RemoteException");
		}
	}

// Query methods

	/** Returns the name of this body location. */
	public String getType() { return(type); }

	/** Returns the remaining internal structure. */
	public int getIS() { return(internalStructure); }

	/** Returns the maximum internal structure. */
	public int getMaxIS() { return(maxInternalStructure); }

	/** Returns the remaining armor on the indexed side. */
	public int getArmor(String side) 
	{
		if (side.equalsIgnoreCase("Front"))
		{ return(armorFront); }

		return(armorRear);
	}

	/** Returns the maximum armor on the indexed side. */
	public int getMaxArmor(String side)
	{
		if (side.equalsIgnoreCase("Front"))
		{ return(maxArmorFront); }

		return(maxArmorRear);
	}

	/** Returns true if this body location is dead, false otherwise. */
	public boolean isDestroyed() { return(destroyed); }

// Modifying methods

	/** Add a component item to this body location. */
	public void addComponent(Component c, int size)
	{
		for (int count = 0; count < size; count++)
		{ criticals.add(c); }
	}

	/** Destroy this body location. */
	public void destroy() // Location destroyed
	{
		try
		{
			if (isDestroyed()) { return; }

			owner.getOwner().addOutput(getType() + " destroyed!");

			armorFront = 0;
			armorRear = 0;
			internalStructure = 0;

			for (int count = 0; count < criticals.size(); count++)
			{
				Component component = (Component) criticals.get(count);
				component.removeSlot();
			}

			if (type.equalsIgnoreCase("RTorso"))
			{
				owner.getBodyParts().destroyBodyPart("RArm"); 
			}

			if (type.equalsIgnoreCase("LTorso"))
			{
				owner.getBodyParts().destroyBodyPart("LArm"); 
			}

			if (type.substring(1).equalsIgnoreCase("Leg"))
			{ owner.fall(); }

			destroyed = true;
		}
		catch(java.rmi.RemoteException e)
		{
			System.out.println("BodyPart class RemoteException");
		}
	}

	/** Critical hit a component item in this body location.  If no more component items remain, transfer critical hit to next logical body location. */
	public void criticalHit() // Hit a component
	{
		try
		{
			int size = criticals.size();
			int slot = 12;

			if (criticals.isEmpty())
			{
				owner.getBodyParts().transferCriticalHit(type);
				return;
			}

			while (slot >= size)
			{
				slot = Rules.rollCritical1(); // Location has 6 criticals

				if (size == 12) // Location has 12 criticals
				{ slot = Rules.rollCritical2(); }
			}

			Component component = (Component) criticals.remove(slot); // Get the component and remove it
			component.criticalHit(); // Hit component
		}
		catch(java.rmi.RemoteException e)
		{
			System.out.println("BodyPart class RemoteException");
		}
	}

	/** Determines how many critical hits result from a hit. */
	public boolean determineCriticalHits() // Determine extent of critical hits damage
	{
		if (isDestroyed()) { return false; }

		int hits = Tables.criticalHits();

		if (hits == 0) { return false; }

		if ((hits == 3) && !type.substring(1).equalsIgnoreCase("Torso")) // Destroy location (applicable to head and limbs)
		{ 
			destroy();
			return true;
		}

		for (int count = 0; count < hits; count++) // Resolve critical hits
		{ criticalHit(); }

		return true;
	}

	/** Inflict ammunition explosion damage. */
	public void ammoDamage(int damage)
	{
		try
		{
			determineCriticalHits();

			if (internalStructure >= damage)
			{ internalStructure = internalStructure - damage; }
			else
			{
				int remainder = damage - internalStructure;
				internalStructure = 0;

				destroy();
				owner.getBodyParts().transferAmmoDamage(type, remainder);
			}
		}
		catch(java.rmi.RemoteException e)
		{
			System.out.println("BodyPart class RemoteException");
		}
	}

	/** Inflict damage to internal structure. */
	public String damageInternalStructure(int damage, String side)
	{
		String output = "";

		try
		{
			determineCriticalHits();

			if (internalStructure >= damage)
			{
				owner.getOwner().addOutput(getType() + " IS hit! Damage: " + damage);
				output = getType() + " IS hit! Damage: " + damage;
				internalStructure = internalStructure - damage;
			}
			else // damage > internalStructure
			{
				owner.getOwner().addOutput(getType() + " IS hit! Damage: " + internalStructure);
				output = getType() + " IS hit! Damage: " + internalStructure;

				int remainder = damage - internalStructure;
				internalStructure = 0;
			
				destroy();
				output = output + "\n" + owner.getBodyParts().transferDamage(type, remainder, side);
			}
		}
		catch(java.rmi.RemoteException e)
		{
			System.out.println("BodyPart class RemoteException");
		}

		return output;
	}

	/** Inflict damage to armor on the indexed side. */
	public String damageArmor(int damage, String side)
	{
		String output = "";

		try
		{
			if ((side.equalsIgnoreCase("Rear")) && (type.substring(1).equalsIgnoreCase("Torso")))
        		{
				if (armorRear >= damage)
				{ 
					owner.getOwner().addOutput(getType() + " rear armor hit! Damage: " + damage);
					output = getType() + " rear armor hit! Damage: " + damage;
					armorRear = armorRear - damage; }
				else
				{
					owner.getOwner().addOutput(getType() + " rear armor hit! Damage: " + armorRear);
					output = getType() + " rear armor hit! Damage: " + armorRear;

					int remainder = damage - armorRear;
					armorRear = 0;

					output = output + "\n" + damageInternalStructure(remainder, side);
				}
			}
			else
			{
				if (armorFront >= damage)
				{
					owner.getOwner().addOutput(getType() + " front armor hit! Damage: " + damage); 
					output = getType() + " front armor hit! Damage: " + damage;
					armorFront = armorFront - damage; 
				}
				else
				{
					owner.getOwner().addOutput(getType() + " front armor hit! Damage: " + armorFront); 
					output = getType() + " front armor hit! Damage: " + armorFront;

					int remainder = damage - armorFront;
					armorFront = 0;

					output = output + "\n" + damageInternalStructure(remainder, side);
				}
			}

			if (type.equalsIgnoreCase("Head")) // Headshots always damage MechWarrior
			{ owner.getMW().takeDamage(1); }
		}
		catch(java.rmi.RemoteException e)
		{
			System.out.println("BodyPart class RemoteException");
		}

		return output;
	}
}