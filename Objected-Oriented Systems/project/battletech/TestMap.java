package battletech;

import junit.framework.*;
import junit.extensions.*;
import gameserver.*;

/** This class represents Map class JUnit tests. */

public class TestMap extends TestCase
{
	private Server server;
	private BattleTechRemote game;

	private Map m1;
	private MechRemote mech1;
	private MechRemote mech2;

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

			m1 = game.getMap();
			mech1 = MechMaker.sampleMech(game);
			mech2 = MechMaker.sampleMech(game);
		}
		catch(java.io.IOException e)
		{
			System.out.println("TestMap IOException!");
		}
	}

	public void testDimensions()
	{
		initialize();
		assertTrue(m1.getWidth() == 100);
		assertTrue(m1.getLength() == 100);
		assertTrue(m1.getMapGridSize() == 5);
	}

	public void testLoad()
	{
		initialize();
		assertTrue(m1.getNearestGrid(50, 50).getElevation() == 3);
	}

	public void testLOSOK()
	{
		try
		{
			initialize();
			mech1.setLocation(0, 0);
			mech2.setLocation(0, 5);
			assertTrue(m1.lineOfSight(mech1, mech2));
		}
		catch(java.rmi.RemoteException e)
		{
			System.out.println("TestMap RemoteException!");
		}
	}

	public void testHillBlockLOS()
	{
		try
		{
			initialize();
			mech1.setLocation(50, 0);
			mech2.setLocation(45, 99);
			assertTrue(m1.lineOfSight(mech1, mech2) == false);
		}
		catch(java.rmi.RemoteException e)
		{
			System.out.println("TestMap RemoteException!");
		}
	}

	public void testLightWoodsBlockLOS()
	{
		try
		{
			initialize();
			mech1.setLocation(75, 0);
			mech2.setLocation(75, 99);
			assertTrue(m1.lineOfSight(mech1, mech2) == false);
		}
		catch(java.rmi.RemoteException e)
		{
			System.out.println("TestMap RemoteException!");
		}
	}

	public void testHeavyWoodsBlockLOS()
	{
		try
		{
			initialize();
			mech1.setLocation(25, 0);
			mech2.setLocation(25, 99);
			assertTrue(m1.lineOfSight(mech1, mech2) == false);
		}
		catch(java.rmi.RemoteException e)
		{
			System.out.println("TestMap RemoteException!");
		}
	}

	public static Test suite()
	{
		return new TestSuite(TestMap.class);
	}

	public static void main(String args[])
	{
		junit.textui.TestRunner.run(suite());
	}
}