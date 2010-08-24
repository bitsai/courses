package battletech;

import junit.framework.*;
import junit.extensions.*;
import gameserver.*;

/** This class represents Weapon class JUnit tests. */

public class TestWeapon extends TestCase
{
	private Server server;
	private BattleTechRemote game;

	private Map map;
	private MechRemote target;
	private MechRemote shooter;
	private Weapon mgun;
	private Weapon mlaser;
	private Weapon srm6;

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

			map = game.getMap();

			shooter = MechMaker.sampleMech(game); // The sample Battlemaster
			shooter.setLocation(0, 0);
			target = MechMaker.sampleMech(game); // The sample Battlemaster
			target.setLocation(5, 0);

			shooter.startTurn();
			shooter.setMovementMode(1);

			mlaser = shooter.getWeapons().getWeapon(1);
			mgun = shooter.getWeapons().getWeapon(7); // Should be a machine gun
			srm6 = shooter.getWeapons().getWeapon(8);
		}
		catch(java.io.IOException e)
		{
			System.out.println("TestWeapon IOException!");
		}
	}

	public void testAmmoCount()
	{
		initialize();
		assertTrue(mgun.getAmmo() == 200);
	}

	public void testHasAmmo()
	{
		initialize();
		assertTrue(mgun.hasAmmo());
	}

	public void testCheckFiringArc()
	{
		initialize();
		assertTrue(mgun.checkFiringArc(target));
	}

	public void testDestroy()
	{
		try
		{
			initialize();
			int sizeBefore = shooter.getWeapons().getSize();
			mgun.destroy();
			int sizeAfter = shooter.getWeapons().getSize();
			assertTrue(sizeAfter == (sizeBefore - 1));
		}
		catch(java.rmi.RemoteException e)
		{
			System.out.println("TestWeapon RemoteException!");
		}
	}

	public void testInflictDamage()
	{
		try
		{
			initialize();
			int armorBefore = target.getBodyParts().getArmor();

			mgun.inflictDamage(target);

			int armorAfter = target.getBodyParts().getArmor();
			assertTrue(armorAfter == (armorBefore - 2));
		}
		catch(java.rmi.RemoteException e)
		{
			System.out.println("TestWeapon RemoteException!");
		}
	}

	public void testAmmoUse()
	{
		initialize();
		mgun.fireWeapon(target);
		assertTrue(mgun.getAmmo() == 199);
	}

	public void testEnergyWeaponAmmo()
	{
		initialize();
		assertTrue(mlaser.hasAmmo());
	}

	public void testMissileWeaponDamage()
	{
		try
		{
			initialize();

			int armorBefore = target.getBodyParts().getArmor();
			srm6.inflictDamage(target);
			int armorAfter = target.getBodyParts().getArmor();

			assertTrue(armorAfter <= (armorBefore - 2));
		}
		catch(java.rmi.RemoteException e)
		{
			System.out.println("TestWeapon RemoteException!");
		}
	}

	public static Test suite()
	{
		return new TestSuite(TestWeapon.class);
	}

	public static void main(String args[])
	{
		junit.textui.TestRunner.run(suite());
	}
}