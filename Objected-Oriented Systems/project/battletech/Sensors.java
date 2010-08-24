package battletech;

/** This class represents the sensors of a Mech. */

public class Sensors extends Component implements java.io.Serializable
{
	public Sensors(BodyPart _location, MechRemote _owner)
	{
		super("Sensors", "Sensors", 2, _location, _owner);
	}

	public void removeSlot()
	{
		hits++;

		if (hits == 2) // Max = 2
		{ destroy(); }
	}
}