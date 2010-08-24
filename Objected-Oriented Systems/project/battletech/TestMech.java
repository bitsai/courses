package battletech;

import junit.framework.*;
import junit.extensions.*;
import gameserver.*;

/** This class represents Mech class JUnit tests. */

public class TestMech extends TestCase
{
	private Server server;
	private BattleTechRemote game;

	private MechRemote testMech;
	private Map map1;

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

			map1 = game.getMap();

			testMech = MechMaker.sampleMech(game);
			testMech.startTurn();

			testMech.setMovementMode(1);
			testMech.setLocation(0, 0);

		}
		catch (java.io.IOException e)
		{
			System.out.println("TestMech IOException!");
		}
	}

// Damage tests

	public void testHitArmor()
	{
		try
		{
			initialize();
			testMech.getBodyParts().damageBodyPart("RArm", 5, "Front");
			assertTrue(testMech.getBodyParts().getArmor("RArm", "Front") == 19);
		}
		catch(java.rmi.RemoteException e)
		{
			System.out.println("TestMech RemoteException!");
		}
	}

	public void testHitInternalStructure()
	{
		try
		{
			initialize();
			testMech.getBodyParts().damageBodyPart("RTorso", 30, "Front");
			assertTrue(testMech.getBodyParts().getIS("RTorso") == 16);
		}
		catch(java.rmi.RemoteException e)
		{
			System.out.println("TestMech RemoteException!");
		}
	}

	public void testTransferDamage()
	{
		try
		{
			initialize();
			testMech.getBodyParts().destroyBodyPart("RArm");
			testMech.getBodyParts().damageBodyPart("RArm", 2, "Front");
			assertTrue(testMech.getBodyParts().getArmor("RTorso", "Front") == 26);
		}
		catch(java.rmi.RemoteException e)
		{
			System.out.println("TestMech RemoteException!");
		}
	}

// Movement tests

	public void testMoveMech() // Move short distance, test correct endpoint and MP usage
	{
		try
		{
			initialize();

			Point start = new Point(0, 0);
			Point end = new Point(5, 0);

			assertTrue(testMech.getLocation().equals(start));
			assertTrue(testMech.getMovementPoints() == 240);

			testMech.moveMech(end);

			assertTrue(testMech.getLocation().equals(end));
			assertTrue(testMech.getMovementPoints() == 215);
		}
		catch(java.rmi.RemoteException e)
		{
			System.out.println("TestMech RemoteException!");
		}
	}

	public void testMoveMechNorthEast() // Move short distance, test correct endpoint and MP usage
	{
		try
		{
			initialize();

			Point start = new Point(40, 50);
			Point end = new Point(54, 37);

			testMech.setLocation(40, 50);
			testMech.setFacing(45);

			assertTrue(testMech.getLocation().equals(start));
			assertTrue(testMech.getMovementPoints() == 240);

			testMech.moveMech(end);

			assertTrue(testMech.getLocation().equals(end));
			assertTrue(testMech.getMovementPoints() == 140);
		}
		catch(java.rmi.RemoteException e)
		{
			System.out.println("TestMech RemoteException!");
		}
	}

	public void testMoveMechNorthWest() // Move short distance, test correct endpoint and MP usage
	{
		try
		{
			initialize();

			Point start = new Point(100, 100);
			Point end = new Point(85, 85);

			testMech.setLocation(100, 100);
			testMech.setFacing(135);

			assertTrue(testMech.getLocation().equals(start));
			assertTrue(testMech.getMovementPoints() == 240);

			testMech.moveMech(end);

			assertTrue(testMech.getLocation().equals(end));
			assertTrue(testMech.getMovementPoints() == 140);
		}
		catch(java.rmi.RemoteException e)
		{
			System.out.println("TestMech RemoteException!");
		}
	}

	public void testNoMP() // see if movement ends properly when MP's run out
	{
		try
		{
			initialize();
			testMech.setLocation(0, 0);
			Point end = new Point(50, 0);
			Point shouldEnd = new Point(48, 0);

			assertTrue(testMech.getLocation().equals(new Point(0, 0)));
			assertTrue(testMech.getMovementPoints() == 240);

			testMech.moveMech(end);

			assertTrue(testMech.getLocation().equals(shouldEnd));
			assertTrue(testMech.getMovementPoints() == 0);
		}
		catch(java.rmi.RemoteException e)
		{
			System.out.println("TestMech RemoteException!");
		}
	}

	public void testMPUsage() // test additional MP's being used due to terrain
	{
		try
		{
			initialize();
			testMech.setLocation(75, 45);
			assertTrue(testMech.getMovementPoints() == 240);
			Point end = new Point(75, 55);

			testMech.turnMech(-90);

			assertTrue(testMech.getFacing() == 270);
			assertTrue(testMech.getMovementPoints() == 150);

			testMech.moveMech(end);

			assertTrue(testMech.getLocation().equals(end));
			assertTrue(testMech.getMovementPoints() == 75);
		}
		catch(java.rmi.RemoteException e)
		{
			System.out.println("TestMech RemoteException!");
		}
	}

	public void testTerrainStoppage1() // west to east
	{
		try
		{
			initialize();
			testMech.setLocation(40, 50);

			Point end = new Point(60, 50);
			Point shouldEnd = new Point(47, 50);

			assertTrue(testMech.getLocation().equals(new Point(40, 50)));
			assertTrue(testMech.getMovementPoints() == 240);

			testMech.moveMech(end);

			assertTrue(testMech.getLocation().equals(shouldEnd));
			assertTrue(testMech.getMovementPoints() == 205);
		}
		catch(java.rmi.RemoteException e)
		{
			System.out.println("TestMech RemoteException!");
		}
	}

	public void testTerrainStoppage2() // east to west
	{
		try
		{
			initialize();
			testMech.setLocation(60, 50);

			Point end = new Point(40, 50);
			Point shouldEnd = new Point(53, 50);

			assertTrue(testMech.getLocation().equals(new Point(60, 50)));
			assertTrue(testMech.getMovementPoints() == 240);

			testMech.moveMech(end);
			assertTrue(testMech.getLocation().equals(shouldEnd));
			assertTrue(testMech.getMovementPoints() == 205);
		}
		catch(java.rmi.RemoteException e)
		{
			System.out.println("TestMech RemoteException!");
		}
	}

	public void testTerrainStoppage3() // north to south
	{
		try
		{
			initialize();
			testMech.setLocation(50, 40);
			testMech.setFacing(270);

			Point end = new Point(50, 60);
			Point shouldEnd = new Point(50, 47);

			assertTrue(testMech.getLocation().equals(new Point(50, 40)));
			assertTrue(testMech.getMovementPoints() == 240);

			testMech.moveMech(end);
			assertTrue(testMech.getLocation().equals(shouldEnd));
			assertTrue(testMech.getMovementPoints() == 205);
		}
		catch(java.rmi.RemoteException e)
		{
			System.out.println("TestMech RemoteException!");
		}
	}

	public void testTerrainStoppage4() // south to north
	{
		try
		{
			initialize();
			testMech.setLocation(50, 60);
			testMech.setFacing(90);

			Point end = new Point(50, 40);
			Point shouldEnd = new Point(50, 53);

			assertTrue(testMech.getLocation().equals(new Point(50, 60)));
			assertTrue(testMech.getMovementPoints() == 240);

			testMech.moveMech(end);
			assertTrue(testMech.getLocation().equals(shouldEnd));
			assertTrue(testMech.getMovementPoints() == 205);
		}
		catch(java.rmi.RemoteException e)
		{
			System.out.println("TestMech RemoteException!");
		}
	}

	public void testTurnMech() // Successful turn
	{
		try
		{
			initialize();
			assertTrue(testMech.getFacing() == 0);

			testMech.turnMech(-30);

			assertTrue(testMech.getFacing() == 330);
			assertTrue(testMech.getMovementPoints() == 210);
		}
		catch(java.rmi.RemoteException e)
		{
			System.out.println("TestMech RemoteException!");
		}
	}

	public void testTorsoTwist()
	{
		try
		{
			initialize();

			testMech.turnTorso(80);

			assertTrue(testMech.getTorsoFacing() == 0); // Too large a torso twist; shouldn't happen

			testMech.turnTorso(60);

			assertTrue(testMech.getTorsoFacing() == 60);
		}
		catch(java.rmi.RemoteException e)
		{
			System.out.println("TestMech RemoteException!");
		}
	}

// Heat tests

	public void testHeat()
	{
		try
		{
			initialize();
			assertTrue(testMech.getCurrentHeat() == 1);

			testMech.addHeat(20);
			assertTrue(testMech.getCurrentHeat() == 21); // Extra 1 because of walking

			testMech.processHeat();
			assertTrue(testMech.getCurrentHeat() == 3); // remove heat from 18 working heatsinks
		}
		catch(java.rmi.RemoteException e)
		{
			System.out.println("TestMech RemoteException!");
		}
	}

	public void testAutoRestart()
	{
		try
		{
			initialize();
			testMech.shutdown();

			assertTrue(testMech.isShutdown());

			testMech.processHeat();
			assertTrue(!testMech.isShutdown());
		}
		catch(java.rmi.RemoteException e)
		{
			System.out.println("TestMech RemoteException!");
		}
	}

// Junit stuff

	public static Test suite()
	{
		return new TestSuite(TestMech.class);
	}

	public static void main(String args[])
	{
		junit.textui.TestRunner.run(suite());
	}
}