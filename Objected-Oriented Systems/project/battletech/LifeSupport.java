package battletech;

/** This class represents the life support of a Mech. */

public class LifeSupport extends Component implements java.io.Serializable
{
	public LifeSupport(BodyPart _location, MechRemote _owner)
	{
		super("LifeSupport", "LifeSupport", 2, _location, _owner);
	}
}