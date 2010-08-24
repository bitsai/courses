package battletech;

import junit.framework.*;
import junit.extensions.*;
import gameserver.*;

/** This class represents BattleTech class JUnit tests. */

public class TestBattleTech extends TestCase
{
	Server server;
	BattleTechRemote btr;

	MechRemote mech1;
	MechRemote mech2;
	MechRemote tempmech;

	public void initialize()
	{
		try
		{
			server = new Server("128.220.2.42");
			BattleTech bt = new BattleTech();
			bt.playGame(server, "Benny", "Fred"); // Player 1 = Benny, Player 2 = Fred

			btr = (BattleTechRemote) bt;
			btr.initialize("testmap", "Benny", "BattleMaster");
			btr.initialize("testmap", "Fred", "BattleMaster");
			mech1 = btr.getOwnMech("Benny");
			mech2 = btr.getOwnMech("Fred");
		}
		catch(java.rmi.RemoteException e)
		{
			System.out.println("TestBattleTech RemoteException!");
		}
	}

	public void testName()
	{
		initialize();

		try
		{
			String name = btr.getName();
			assertTrue(name.equalsIgnoreCase("BattleTech"));
		}
		catch(java.rmi.RemoteException e)
		{
			System.out.println("TestBattleTech RemoteException!");
		}	
	}

	public void testGetOwnMech()
	{
		initialize();

		try
		{
			assertTrue(mech1.getMass() == 85);
		}
		catch(java.rmi.RemoteException e)
		{
			System.out.println("TestBattleTech RemoteException!");
		}	
	}

	public void testGetOtherMech()
	{
		initialize();

		try
		{
			tempmech = btr.getOtherMech("Benny");
			assertTrue(tempmech.getMass() == 85);
		}
		catch(java.rmi.RemoteException e)
		{
			System.out.println("TestBattleTech RemoteException!");
		}
	}

	public void testWhoseTurn()
	{
		initialize();

		try
		{
			String currentplayer = btr.getWhoseTurn();
			assertTrue(currentplayer.equalsIgnoreCase("Benny"));
		}
		catch(java.rmi.RemoteException e)
		{
			System.out.println("TestBattleTech RemoteException!");
		}
	}

	public void testGameStarted()
	{
		initialize();

		try
		{
			assertTrue(mech1.getMovementMode() == -1);
			assertTrue(mech2.getMovementMode() == 0);
		}
		catch(java.rmi.RemoteException e)
		{
			System.out.println("TestBattleTech RemoteException!");
		}
	}

	public void testEndTurn()
	{
		initialize();

		try
		{
			btr.endTurn();
//			assertTrue(mech1.getMovementMode() == -1);
			assertTrue(mech2.getMovementMode() == -1);
		}
		catch(java.rmi.RemoteException e)
		{
			System.out.println("TestBattleTech RemoteException!");
		}
	}

	public static Test suite()
	{
		return new TestSuite(TestBattleTech.class);
	}

	public static void main(String args[])
	{
		junit.textui.TestRunner.run(suite());
	}
}