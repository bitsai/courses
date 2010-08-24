package battletech;

/** This class represents jumpjets of a Mech. */

public class JumpJet extends Component implements java.io.Serializable
{
	public JumpJet(BodyPart _location, MechRemote _owner)
	{
		super("JumpJet", "JumpJet", 1, _location, _owner);
	}
}