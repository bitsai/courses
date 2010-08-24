package battletech;

import java.rmi.*;
import java.rmi.server.*;

/** This class represents the collection of physical attacks of a Mech. */

public class PhysicalAttacks extends UnicastRemoteObject implements PhysicalAttacksRemote
{
	private MechRemote owner;

	private boolean rightPunched = false;
	private boolean leftPunched = false;
	private boolean kicked = false;

// Constructor

	public PhysicalAttacks(MechRemote _owner) throws RemoteException
	{ owner = _owner; }

// QUery methods

	/** Returns true if right punch has already been executed. */
	public boolean rightPunched()
	{ return rightPunched; }

	/** Returns true if left punch has already been executed. */
	public boolean leftPunched()
	{ return leftPunched; }

	/** Returns true if kick has already been executed. */
	public boolean kicked()
	{ return kicked; }

// Modify methods

	/** Permit the Mech to punch and kick. */
	public synchronized void activate()
	{
		rightPunched = false;
		leftPunched = false;
		kicked = false;
	}

	/** Punch specified target, using specified arm (0 is right arm, 1 is left arm). */
	public synchronized String punch(MechRemote target, int side) throws RemoteException // 0 == right, 1 == left
	{	
		int range = (int) Rules.getDistance(owner.getLocation(), target.getLocation()) / owner.getMap().getMapGridSize();
		String firingArc = Rules.getArc(owner.getTorsoFacing(), owner.getLocation(), target.getLocation());

		if (owner.getMW().isUnconscious())
		{ return("MechWarrior is unconscious!"); }

		if (owner.isShutdown())
		{ return("Mech shutdown!"); }

		if (!owner.isActive())
		{ 
			return("Mech not active!");
		}

		if (owner.isProne())
		{
			return("Cannot punch while prone!");
		}

		if ((owner.getWeapons().rArmWeaponsFired()) && (side == 0))
		{
			return("Arm weapons already fired!");
		}

		if ((owner.getWeapons().lArmWeaponsFired()) && (side == 1))
		{
			return("Arm weapons already fired!");
		}

		if (kicked)
		{
			return("Cannot punch and kick in same turn!");
		}

		if (1 < range) // If target is out of range, do not shoot
		{ 
			return("Target out of range!");
		}

		if (target.getElevation() > owner.getElevation() + 1)
		{
			return("Target too high!");
		}

		if (target.getElevation() < owner.getElevation())
		{
			return("Target too low!");
		}

		if ((target.getElevation() == owner.getElevation()) && target.isProne())
		{
			return("Target too low!");
		}

		if (side == 0)
		{
			if (!owner.getBodyParts().rArm()) // Right arm must be present to punch
			{
				return("Right arm not present!");
			}

			if (rightPunched) // Can only punch once per turn
			{ 
				return("Already punched with right arm!");
			}

			if (!firingArc.equalsIgnoreCase("Front") && !firingArc.equalsIgnoreCase("Right")) // Can't punch targets not in front or right
			{
				return("Target outside firing arc!");
			}

			rightPunched = true;
		}
		else
		{
			if (!owner.getBodyParts().lArm()) // Left arm must be present to punch
			{
				return("Left arm not present!");
			}

			if (leftPunched) // Can only punch once per turn
			{ 
				return("Already punched with left arm!");
			}

			if (!firingArc.equalsIgnoreCase("Front") && !firingArc.equalsIgnoreCase("Left")) // Can't punch targets not in front or right
			{
				return("Target outside firing arc!");
			}

			leftPunched = true;
		}

		int roll = Rules.rollDice();
		int difficulty = Rules.getPhysicalAttackDifficulty(owner, target, 4);

		if (roll < difficulty) // Punch missed
		{ 
			return("Punch missed! Difficulty: " + difficulty + " Roll: " + roll);
		}

		String output = "Punch hit! Difficulty: " + difficulty + " Roll: " + roll;
		String hitArc = Rules.getArc(target.getFacing(), target.getLocation(), owner.getLocation());
		int damage = owner.getMass() / 10;

		if ((target.getElevation() > owner.getElevation()) && (target.isProne())) // Target is higher and prone
		{ output = output + "\n" + target.takeDamage(damage, hitArc); }
		else if (target.getElevation() > owner.getElevation()) // Target is higher and standing
		{ output = output + "\n" + target.takeKickDamage(damage, hitArc); }
		else
		{ output = output + "\n" + target.takePunchDamage(damage, hitArc); } // Target is same level and standing

		return(output);
	}

	/** Kick specified target. */
	public synchronized String kick(MechRemote target) throws RemoteException
	{	
		int range = (int) Rules.getDistance(owner.getLocation(), target.getLocation()) / owner.getMap().getMapGridSize();
		String firingArc = Rules.getArc(owner.getFacing(), owner.getLocation(), target.getLocation());

		if (owner.getMW().isUnconscious())
		{ return("MechWarrior is unconscious!"); }

		if (owner.isShutdown())
		{ return("Mech shutdown!"); }

		if (!owner.isActive()) // Can't kick when shutdown or not my turn yet
		{ 
			return("Mech not active!");
		}

		if (owner.isProne())
		{
			return("Cannot kick while prone!");
		}

		if (owner.getWeapons().legWeaponsFired())
		{
			return("Leg weapons already fired!");
		}

		if (rightPunched || leftPunched)
		{
			return("Cannot punch and kick in same turn!");
		}

		if (1 < range) // If target is out of range, do not kick
		{ 
			return("Target out of range!");
		}

		if (target.getElevation() < owner.getElevation() - 1)
		{
			return("Target too low!");
		}

		if ((target.getElevation() == owner.getElevation() - 1) && (target.isProne()))
		{
			return("Target too low!");
		}

		if (target.getElevation() > owner.getElevation())
		{
			return("Target too high!");
		}

		if (owner.getBodyParts().legs() != 2) // Need both legs to kick
		{
			return("Need both legs to kick!");
		}

		if (kicked) // Can only kick once per turn
		{ 
			return("Already kicked!");
		}

		if (!firingArc.equalsIgnoreCase("Front")) // Can't kick targets not in front
		{
			return("Target outside firing arc!");
		}

		kicked = true;
		int roll = Rules.rollDice();
		int difficulty = Rules.getPhysicalAttackDifficulty(owner, target, 3);

		if (roll < difficulty)  // Kick missed
		{
			String output = "Kick missed! Difficulty: " + difficulty + " Roll: " + roll;
			boolean avoidFall = Rules.rollPiloting(0, owner);
			if (avoidFall)
			{ return(output + "\nMech avoided fall!"); }
			else
			{ return(output + "\nMech fell!"); }
		}

		String output = "Kick hit! Difficulty: " + difficulty + " Roll: " + roll;
		String hitArc = Rules.getArc(target.getFacing(), target.getLocation(), owner.getLocation());
		int damage = owner.getMass() / 5;

		if ((target.getElevation() == owner.getElevation()) && (target.isProne())) // Target is same level and prone
		{ output = output + "\n" + target.takeDamage(damage, hitArc); }
		else if (target.getElevation() == owner.getElevation()) // Target is same level and standing
		{ output = output + "\n" + target.takeKickDamage(damage, hitArc); }
		else
		{ output = output + "\n" + target.takePunchDamage(damage, hitArc); } // Target is lower and standing

		boolean avoidFall = Rules.rollPiloting(0, target);

		if (avoidFall)
		{ return(output + "\nTarget avoided fall!"); }
		else
		{ return(output + "\nTarget fell!"); }
	}
}