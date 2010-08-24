package battletech;

/** This class represents the engine of a Mech. */

public class Engine extends Component implements java.io.Serializable
{
	public Engine(BodyPart _location, MechRemote _owner)
	{
		super("Engine", "Engine", 6, _location, _owner);
	}

	/** Inflict damage on the engine. */
	public void removeSlot()
	{
		hits++;

		if (hits == 3)
		{ destroy(); }
	}
}