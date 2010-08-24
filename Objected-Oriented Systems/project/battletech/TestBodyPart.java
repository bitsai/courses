package battletech;

import junit.framework.*;
import junit.extensions.*;
import gameserver.*;

/** This class represents BodyPart class JUnit tests. */

public class TestBodyPart extends TestCase
{
	private Server server;
	private BattleTechRemote game;

	private MechRemote mech;
	private BodyPart bp;

	public void initialize()
	{
		try
		{
			server = new Server("128.220.2.42");
			BattleTech bt = new BattleTech();
			bt.playGame(server, "Benny", "Fred");

			game = (BattleTechRemote) bt;
			game.initialize("testmap", "Benny", "BattleMaster");
			game.initialize("testmap", "Fred", "BattleMaster");

			mech = MechMaker.sampleMech(game);
			bp = mech.getBodyParts().getBodyPart("RArm");
		}
		catch(java.io.IOException e)
		{
			System.out.println("TestBodyPart IOException!");
		}
	}

	public void testDestroy()
	{
		initialize();

		try
		{
			bp.destroy();
		
			assertTrue(!mech.getBodyParts().rArm());
		}
		catch(java.rmi.RemoteException e)
		{
			System.out.println("TestBodyPart RemoteException!");
		}
	}

	public void testTransferCriticalHit()
	{
		initialize();

		try
		{
			int numBefore = mech.getComponents().getSize();
			// Hitting PPC
			bp.criticalHit();
			bp.criticalHit();
			bp.criticalHit();
			// Should hit a MLaser
			bp.criticalHit();
			int numAfter = mech.getComponents().getSize();
		
			assertTrue(numAfter == (numBefore - 2));
		}
		catch(java.rmi.RemoteException e)
		{
			System.out.println("TestBodyPart RemoteException!");
		}
	}

	public static Test suite()
	{
		return new TestSuite(TestBodyPart.class);
	}

	public static void main(String args[])
	{
		junit.textui.TestRunner.run(suite());
	}
}