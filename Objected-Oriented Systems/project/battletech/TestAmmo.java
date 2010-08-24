package battletech;

import junit.framework.*;
import junit.extensions.*;
import gameserver.*;

/** This class represents Ammo class JUnit tests. */

public class TestAmmo extends TestCase
{
	private Server server;
	private BattleTechRemote game;

	private MechRemote mech;
	private Ammo ammo;

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
			ammo = mech.getAmmos().getAmmo("MGunAmmo1");
		}
		catch(java.io.IOException e)
		{
			System.out.println("TestAmmo IOException!");
		}
	}

	public void testDestroy()
	{
		try
		{
			initialize();

			int numBefore = mech.getAmmos().getSize();
			ammo.destroy();
			int numAfter = mech.getAmmos().getSize();
		
			assertTrue(numAfter == (numBefore - 1));
		}
		catch(java.rmi.RemoteException e)
		{
			System.out.println("TestAmmo RemoteException!");
		}
	}

	public void testAmmoExplosion()
	{
		try
		{
			initialize();

			int numBefore = mech.getAmmos().getSize();
			ammo.criticalHit();
			int numAfter = mech.getAmmos().getSize();

			assertTrue(numAfter == (numBefore - 3));
		}
		catch(java.rmi.RemoteException e)
		{
			System.out.println("TestAmmo RemoteException!");
		}
	}

	public static Test suite()
	{
		return new TestSuite(TestAmmo.class);
	}

	public static void main(String args[])
	{
		junit.textui.TestRunner.run(suite());
	}
}