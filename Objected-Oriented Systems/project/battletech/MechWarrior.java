package battletech;

/** This class represents the MechWarrior of a Mech. */

public class MechWarrior implements java.io.Serializable
{
	private int gunnerySkill;
	private int pilotingSkill;

	private MechRemote mech;

	private int damage = 0;
	private boolean unconscious = false;

// Constructors

	/** Default constructor. */
	public MechWarrior(MechRemote _mech)
	{
		gunnerySkill = 4;
		pilotingSkill = 5;

		mech = _mech;
	}

	/** Creates a MechWarrior of specified gunnery and piloting skill. */
	public MechWarrior(int gunnery, int piloting, MechRemote _mech)
	{
		gunnerySkill = gunnery;
		pilotingSkill = piloting;

		mech = _mech;
	}

// Query methods

	/** Returns gunnery skill of this MechWarrior. */
	public int getGunnery()
	{
		return(gunnerySkill);
	}

	/** Returns piloting skill of this MechWarrior. */
	public int getPiloting()
	{
		return(pilotingSkill);
	}

	/** Returns number of hits taken by this MechWarrior. */
	public int getDamage()
	{
		return(damage);
	}

	/** Returns true if this MechWarrior is unconscious, false otherwise. */
	public boolean isUnconscious()
	{
		return(unconscious);
	}

	/** Returns true if this MechWarrior is dead, false otherwise. */
	public boolean isDead()
	{
		if (damage == 6)
		{ return true; }

		return false;
	}

// Modifying methods

	/** Give MechWarrior a chance to regain consciousness. */
	public void revive() 
	{
		if (!isUnconscious()) { return; }

		try
		{
			int difficulty = Tables.mwConsciousness(damage);

			if (Rules.rollDice() >= difficulty)
			{
				mech.getOwner().addOutput("MechWarrior revived!");
				unconscious = false; 
			}
		}
		catch(java.rmi.RemoteException e)
		{
			System.out.println("MechWarrior RemoteException!");
		}
	}

	/** Kill MechWarrior. */
	public void kill()
	{
		try
		{
			mech.getOwner().addOutput("MechWarrior killed!");
			damage = 6;
			unconscious = true;
		}
		catch(java.rmi.RemoteException e)
		{
			System.out.println("MechWarrior RemoteException!");
		}
	}

	/** Inflict specified amount of damage on MechWarrior. */
	public void takeDamage(int _damage)
	{
		try
		{
			mech.getOwner().addOutput("MechWarrior hit!");
			damage = damage + _damage;

			if (damage >= 6)
			{ kill(); }

			int difficulty = Tables.mwConsciousness(damage);

			if (Rules.rollDice() < difficulty)
			{
				mech.getOwner().addOutput("MechWarrior unconscious!");
				unconscious = true;
				mech.deactivate();
			}
		}
		catch(java.rmi.RemoteException e)
		{
			System.out.println("MechWarrior RemoteException!");
		}
	}
}