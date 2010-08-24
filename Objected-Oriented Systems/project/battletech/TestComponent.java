package battletech;

import junit.framework.*;
import junit.extensions.*;
import gameserver.*;

/** This class represents Component class JUnit tests. */

public class TestComponent extends TestCase
{
	private Server server;
	private BattleTechRemote game;

	private MechRemote mech;
	private Component component;

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
			component = mech.getComponents().getComponent("Engine");
		}
		catch(java.io.IOException e)
		{
			System.out.println("TestComponent IOException!");
		}
	}

	public void testDestroy()
	{
		initialize();

		try
		{
			int numBefore = mech.getComponents().getSize();
			component.destroy();
			int numAfter = mech.getComponents().getSize();
		
			assertTrue(numAfter == (numBefore - 1));
		}
		catch(java.rmi.RemoteException e)
		{
			System.out.println("TestComponent RemoteException!");
		}
	}

	public void testEngineHits()
	{
		initialize();

		try
		{
			component.destroy();
		
			assertTrue(mech.getComponents().getEngineHits() == 3);
		}
		catch(java.rmi.RemoteException e)
		{
			System.out.println("TestComponent RemoteException!");
		}
	}

	public static Test suite()
	{
		return new TestSuite(TestComponent.class);
	}

	public static void main(String args[])
	{
		junit.textui.TestRunner.run(suite());
	}
}