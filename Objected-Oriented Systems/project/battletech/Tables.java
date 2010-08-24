package battletech;

/** This class represents various BattleTech tables. */

public class Tables implements java.io.Serializable
{
// Heat stuff

	/** Returns firing modifer due to heat. */
	public static int getHeatAttackModifier(int heat)
	{
		int modifier = 0;

		if (heat >= 24) { modifier = 4; }
		else if (heat >= 17) { modifier = 3; }
		else if (heat >= 13) { modifier = 2; }
		else if (heat >= 8) { modifier = 1; }

		return(modifier);
	}

	/** Returns movement modifer due to heat. */
	public static int getHeatMovementModifier(int heat)
	{
		int modifier = 0;

		if (heat >= 25) { modifier = -5; }
		else if (heat >= 20) { modifier = -4; }
		else if (heat >= 15) { modifier = -3; }
		else if (heat >= 10) { modifier = -2; }
		else if (heat >= 5) { modifier = -1; }

		return(modifier);
	}

	/** Returns shutdown avoid number due to heat. */
	public static int getShutdownAvoid(int heat)
	{
		int avoid = 0;

		if (heat >= 26) { avoid = 10; }
		else if (heat >= 22) { avoid = 8; }
		else if (heat >= 18) { avoid = 6; }
		else if (heat >= 14) { avoid = 4; }

		return(avoid);
	}

	/** Returns ammunition explosion avoid number due to heat. */
	public static int getAmmoExplodeAvoid(int heat)
	{
		int avoid = 0;

		if (heat >= 28) { avoid = 8; }
		else if (heat >= 23) { avoid = 6; }
		else if (heat >= 19) { avoid = 4; }

		return(avoid);
	}

	/** Returns number of missiles that hit. */
	public static int missileHits(int _missiles)
	{
		int roll = Rules.rollDice();
		int missiles = 0;

		switch(roll)
		{
			case 2:
				if (_missiles == 2) { missiles = 1; }
				if (_missiles == 4) { missiles = 1; }
				if (_missiles == 6) { missiles = 1; }
				if (_missiles == 5) { missiles = 2; }
				if (_missiles == 10) { missiles = 3; }
				if (_missiles == 15) { missiles = 5; }
				if (_missiles == 20) { missiles = 6; }
				break;
			case 3:
				if (_missiles == 2) { missiles = 1; }
				if (_missiles == 4) { missiles = 2; }
				if (_missiles == 6) { missiles = 2; }
				if (_missiles == 5) { missiles = 2; }
				if (_missiles == 10) { missiles = 3; }
				if (_missiles == 15) { missiles = 5; }
				if (_missiles == 20) { missiles = 6; }
				break;
			case 4:
				if (_missiles == 2) { missiles = 1; }
				if (_missiles == 4) { missiles = 2; }
				if (_missiles == 6) { missiles = 2; }
				if (_missiles == 5) { missiles = 3; }
				if (_missiles == 10) { missiles = 4; }
				if (_missiles == 15) { missiles = 6; }
				if (_missiles == 20) { missiles = 9; }
				break;
			case 5:
				if (_missiles == 2) { missiles = 1; }
				if (_missiles == 4) { missiles = 2; }
				if (_missiles == 6) { missiles = 3; }
				if (_missiles == 5) { missiles = 3; }
				if (_missiles == 10) { missiles = 6; }
				if (_missiles == 15) { missiles = 9; }
				if (_missiles == 20) { missiles = 12; }
				break;
			case 6:
				if (_missiles == 2) { missiles = 1; }
				if (_missiles == 4) { missiles = 2; }
				if (_missiles == 6) { missiles = 3; }
				if (_missiles == 5) { missiles = 4; }
				if (_missiles == 10) { missiles = 6; }
				if (_missiles == 15) { missiles = 9; }
				if (_missiles == 20) { missiles = 12; }
				break;
			case 7:
				if (_missiles == 2) { missiles = 1; }
				if (_missiles == 4) { missiles = 3; }
				if (_missiles == 6) { missiles = 3; }
				if (_missiles == 5) { missiles = 4; }
				if (_missiles == 10) { missiles = 6; }
				if (_missiles == 15) { missiles = 9; }
				if (_missiles == 20) { missiles = 12; }
				break;
			case 8:
				if (_missiles == 2) { missiles = 2; }
				if (_missiles == 4) { missiles = 3; }
				if (_missiles == 6) { missiles = 3; }
				if (_missiles == 5) { missiles = 4; }
				if (_missiles == 10) { missiles = 6; }
				if (_missiles == 15) { missiles = 9; }
				if (_missiles == 20) { missiles = 12; }
				break;
			case 9:
				if (_missiles == 2) { missiles = 2; }
				if (_missiles == 4) { missiles = 3; }
				if (_missiles == 6) { missiles = 4; }
				if (_missiles == 5) { missiles = 5; }
				if (_missiles == 10) { missiles = 8; }
				if (_missiles == 15) { missiles = 12; }
				if (_missiles == 20) { missiles = 16; }
				break;
			case 10:
				if (_missiles == 2) { missiles = 2; }
				if (_missiles == 4) { missiles = 3; }
				if (_missiles == 6) { missiles = 4; }
				if (_missiles == 5) { missiles = 5; }
				if (_missiles == 10) { missiles = 8; }
				if (_missiles == 15) { missiles = 12; }
				if (_missiles == 20) { missiles = 16; }
				break;
			case 11:
				if (_missiles == 2) { missiles = 2; }
				if (_missiles == 4) { missiles = 4; }
				if (_missiles == 6) { missiles = 5; }
				if (_missiles == 5) { missiles = 6; }
				if (_missiles == 10) { missiles = 10; }
				if (_missiles == 15) { missiles = 15; }
				if (_missiles == 20) { missiles = 20; }
				break;
			case 12:
				if (_missiles == 2) { missiles = 2; }
				if (_missiles == 4) { missiles = 4; }
				if (_missiles == 6) { missiles = 5; }
				if (_missiles == 5) { missiles = 6; }
				if (_missiles == 10) { missiles = 10; }
				if (_missiles == 15) { missiles = 15; }
				if (_missiles == 20) { missiles = 20; }
				break;
		}
	
		return(missiles);
	}

	/** Returns number of critical hits resulting from damage. */
	public static int criticalHits()
	{
		int roll = Rules.rollDice();

		if ((roll >= 2) && (roll <= 7))
		{ return(0); }
		if ((roll >= 8) && (roll <= 9))
		{ return(1); }
		if ((roll >= 10) && (roll <= 11))
		{ return(2); }
		if (roll == 12)
		{ return(3); }		

		return(-1);
	}

	/** Returns an upper body location. */
	public static String punchHitLocation(String hitArc)
	{
		int roll = Rules.rollDie();

		switch(roll)
		{
			case 1:
				if (hitArc.equalsIgnoreCase("Left"))
				{ return("LTorso"); }
				if (hitArc.equalsIgnoreCase("Right"))
				{ return("RTorso"); }
				return("LArm");
			case 2:
				if (hitArc.equalsIgnoreCase("Left"))
				{ return("LTorso"); }
				if (hitArc.equalsIgnoreCase("Right"))
				{ return("RTorso"); }
				return("LTorso");
			case 3:
				return("CTorso");
			case 4:
				if (hitArc.equalsIgnoreCase("Left"))
				{ return("LArm"); }
				if (hitArc.equalsIgnoreCase("Right"))
				{ return("RArm"); }
				return("RTorso");
			case 5:
				if (hitArc.equalsIgnoreCase("Left"))
				{ return("LArm"); }
				if (hitArc.equalsIgnoreCase("Right"))
				{ return("RArm"); }
				return("RArm");
			case 6:
				return("Head");
		}

		return("Error");
	}

	/** Returns a lower body location. */
	public static String kickHitLocation(String hitArc)
	{
		int roll = Rules.rollDie();

		if (hitArc.equalsIgnoreCase("Left"))
		{ return("LLeg"); }

		if (hitArc.equalsIgnoreCase("Right"))
		{ return("RLeg"); }

		if (roll >= 4) // Hitarc = "Center", roll between 4 and 6
		{ return("LLeg"); }

		return("RLeg"); // Hitarc = "Center", roll between 1 and 3
	}

	/** Returns a body location. */
	public static String hitLocation(String hitArc)
	{
		int roll = Rules.rollDice();

		switch(roll)
		{
			case 2:
				if (hitArc.equalsIgnoreCase("Left"))
				{ return("(C)LTorso"); }
				if (hitArc.equalsIgnoreCase("Right"))
				{ return("(C)RTorso"); }
				return("(C)CTorso");
			case 3:
				if (hitArc.equalsIgnoreCase("Left"))
				{ return("LLeg"); }
				if (hitArc.equalsIgnoreCase("Right"))
				{ return("RLeg"); }
				return("RArm");
			case 4:
				if (hitArc.equalsIgnoreCase("Left"))
				{ return("LArm"); }
				if (hitArc.equalsIgnoreCase("Right"))
				{ return("RArm"); }
				return("RArm");
			case 5:
				if (hitArc.equalsIgnoreCase("Left"))
				{ return("LArm"); }
				if (hitArc.equalsIgnoreCase("Right"))
				{ return("RArm"); }
				return("RLeg");
			case 6:
				if (hitArc.equalsIgnoreCase("Left"))
				{ return("LLeg"); }
				if (hitArc.equalsIgnoreCase("Right"))
				{ return("RLeg"); }
				return("RTorso");
			case 7:
				if (hitArc.equalsIgnoreCase("Left"))
				{ return("LTorso"); }
				if (hitArc.equalsIgnoreCase("Right"))
				{ return("RTorso"); }
				return("CTorso");
			case 8:
				if (hitArc.equalsIgnoreCase("Left"))
				{ return("CTorso"); }
				if (hitArc.equalsIgnoreCase("Right"))
				{ return("CTorso"); }
				return("LTorso");
			case 9:
				if (hitArc.equalsIgnoreCase("Left"))
				{ return("RTorso"); }
				if (hitArc.equalsIgnoreCase("Right"))
				{ return("LTorso"); }
				return("LLeg");
			case 10:
				if (hitArc.equalsIgnoreCase("Left"))
				{ return("RArm"); }
				if (hitArc.equalsIgnoreCase("Right"))
				{ return("LArm"); }
				return("LArm");
			case 11:
				if (hitArc.equalsIgnoreCase("Left"))
				{ return("RLeg"); }
				if (hitArc.equalsIgnoreCase("Right"))
				{ return("LLeg"); }
				return("LArm");
			case 12:
				return("Head");
		}
		return("Error");
	}

	/** Returns unconscious avoid number due to MechWarrior damage. */
	public static int mwConsciousness(int hits)
	{
		switch(hits)
		{
			case 1: return(3);
			case 2: return(5);
			case 3: return(7);
			case 4: return(9);
			case 5: return(11);
			case 6: return(13); // MechWarrior is dead; throw exception or something
		}
		return(-1);
	}

	/** Returns amount to turn the Mech by due to a fall. */
	public static int facingAfterFall1(int roll)
	{
		switch(roll)
		{
			case 1: return(0);
			case 2: return(60);
			case 3: return(120);
			case 4: return(180);
			case 5: return(-120);
			case 6: return(-60);
		}
		return(-1);
	}

	/** Returns side of the Mech to be damaged due to a fall. */
	public static String facingAfterFall2(int roll)
	{
		switch(roll)
		{
			case 1: return("Front");
			case 2: return("Right");
			case 3: return("Right");
			case 4: return("Rear");
			case 5: return("Left");
			case 6: return("Left");
		}
		return("Error");
	}
}