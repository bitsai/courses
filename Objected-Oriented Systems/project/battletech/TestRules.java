package battletech;

import java.util.*;
import junit.framework.*;

/** This class represents Rules class JUnit tests. */

public class TestRules extends TestCase
{
	public void testAngle1()
	{
		Point origin = new Point(40, 50);
		Point destination = new Point(0, 45);
		assertTrue(Rules.getAngle(origin, destination) == 173);
	}
	
	public void testAngle2()
	{
		Point origin = new Point(40, 50);
		Point destination = new Point(35, 0);
		assertTrue(Rules.getAngle(origin, destination) == 96);
	}
	
	public void testAngle3()
	{
		Point origin = new Point(40, 50);
		Point destination = new Point(100, 55);
		assertTrue(Rules.getAngle(origin, destination) == 355);
	}
	
	public void testAngle4()
	{
		Point origin = new Point(40, 50);
		Point destination = new Point(35, 100);
		assertTrue(Rules.getAngle(origin, destination) == 264);
	}
	
   public void testGetDistance()
   {
      Point destination = new Point(100, 0);
      Point origin = new Point(0, 0);
      double distance = Rules.getDistance(origin, destination);
      assertTrue(distance == 100);
   }

   public void testCheckFacing1() // Test facing east
   {
      Point destination = new Point(100, 50);
      Point origin = new Point(50, 50);
      int facing = 0;
      assertTrue(Rules.checkMovementFacing(1, facing, origin, destination) == true);
   }

   public void testCheckFacing2() // Test facing northeast
   {
      Point destination = new Point(100, 0);
      Point origin = new Point(50, 50);
      int facing = 45;
      assertTrue(Rules.checkMovementFacing(1, facing, origin, destination) == true);
   }

   public void testCheckFacing3() // Test facing north
   {
      Point destination = new Point(50, 0);
      Point origin = new Point(50, 50);
      int facing = 90;
      assertTrue(Rules.checkMovementFacing(1, facing, origin, destination) == true);
   }

   public void testCheckFacing4() // Test facing northwest
   {
      Point destination = new Point(0, 0);
      Point origin = new Point(50, 50);
      int facing = 135;
      assertTrue(Rules.checkMovementFacing(1, facing, origin, destination) == true);
   }

   public void testCheckFacing5() // Test facing west
   {
      Point destination = new Point(0, 50);
      Point origin = new Point(50, 50);
      int facing = 180;
      assertTrue(Rules.checkMovementFacing(1, facing, origin, destination) == true);
   }

   public void testCheckFacing6() // Test facing southwest
   {
      Point destination = new Point(0, 100);
      Point origin = new Point(50, 50);
      int facing = 225;
      assertTrue(Rules.checkMovementFacing(1, facing, origin, destination) == true);
   }

   public void testCheckFacing7() // Test facing south
   {
      Point destination = new Point(50, 100);
      Point origin = new Point(50, 50);
      int facing = 270;
      assertTrue(Rules.checkMovementFacing(1, facing, origin, destination) == true);
   }

   public void testCheckFacing8() // Test facing southeast
   {
      Point destination = new Point(100, 100);
      Point origin = new Point(50, 50);
      int facing = 315;
      assertTrue(Rules.checkMovementFacing(1, facing, origin, destination) == true);
   }

	public void testGetHitArc1() // Test target facing east
	{
		Point shooterLocation = new Point(50, 0);
		Point targetLocation = new Point(0, 0);
		int facing = 0;
		String hitArc = Rules.getArc(facing, targetLocation, shooterLocation);

		assertTrue(hitArc.equalsIgnoreCase("Front"));
	}

	public void testGetHitArc2() // Test target facing north
	{
		Point shooterLocation = new Point(50, 0);
		Point targetLocation = new Point(0, 0);
		int facing = 90;
		String hitArc = Rules.getArc(facing, targetLocation, shooterLocation);

		assertTrue(hitArc.equalsIgnoreCase("Right"));
	}

	public void testGetHitArc3() // Test target facing west
	{
		Point shooterLocation = new Point(50, 0);
		Point targetLocation = new Point(0, 0);
		int facing = 180;
		String hitArc = Rules.getArc(facing, targetLocation, shooterLocation);

		assertTrue(hitArc.equalsIgnoreCase("Rear"));
	}

	public void testGetHitArc4() // Test target facing south
	{
		Point shooterLocation = new Point(50, 0);
		Point targetLocation = new Point(0, 0);
		int facing = 270;
		String hitArc = Rules.getArc(facing, targetLocation, shooterLocation);

		assertTrue(hitArc.equalsIgnoreCase("Left"));
	}

	public void testGetHitArc5() // Test target facing northeast
	{
		Point shooterLocation = new Point(50, 0);
		Point targetLocation = new Point(0, 0);
		int facing = 45;
		String hitArc = Rules.getArc(facing, targetLocation, shooterLocation);

		assertTrue(hitArc.equalsIgnoreCase("Front"));
	}

	public void testGetHitArc6() // Test target facing northwest
	{
		Point shooterLocation = new Point(50, 0);
		Point targetLocation = new Point(0, 0);
		int facing = 135;
		String hitArc = Rules.getArc(facing, targetLocation, shooterLocation);

		assertTrue(hitArc.equalsIgnoreCase("Right"));
	}

	public void testGetHitArc7() // Test target facing southwest
	{
		Point shooterLocation = new Point(50, 0);
		Point targetLocation = new Point(0, 0);
		int facing = 225;
		String hitArc = Rules.getArc(facing, targetLocation, shooterLocation);

		assertTrue(hitArc.equalsIgnoreCase("Left"));
	}

	public void testGetHitArc8() // Test target facing southeast
	{
		Point shooterLocation = new Point(50, 0);
		Point targetLocation = new Point(0, 0);
		int facing = 315;
		String hitArc = Rules.getArc(facing, targetLocation, shooterLocation);

		assertTrue(hitArc.equalsIgnoreCase("Front"));
	}

	public void testGetFiringArc1() // Test target facing east
	{
		Point shooterLocation = new Point(0, 0);
		Point targetLocation = new Point(0, 50);
		int facing = 0;
		String hitArc = Rules.getArc(facing, shooterLocation, targetLocation);

		assertTrue(hitArc.equalsIgnoreCase("Right"));
	}

	public void testGetFiringArc2() // Test target facing northeast
	{
		Point shooterLocation = new Point(0, 0);
		Point targetLocation = new Point(0, 50);
		int facing = 45;
		String hitArc = Rules.getArc(facing, shooterLocation, targetLocation);

		assertTrue(hitArc.equalsIgnoreCase("Right"));
	}

	public void testGetFiringArc3() // Test target facing north
	{
		Point shooterLocation = new Point(0, 0);
		Point targetLocation = new Point(0, 50);
		int facing = 90;
		String hitArc = Rules.getArc(facing, shooterLocation, targetLocation);

		assertTrue(hitArc.equalsIgnoreCase("Rear"));
	}

	public void testGetFiringArc4() // Test target facing northwest
	{
		Point shooterLocation = new Point(0, 0);
		Point targetLocation = new Point(0, 50);
		int facing = 135;
		String hitArc = Rules.getArc(facing, shooterLocation, targetLocation);

		assertTrue(hitArc.equalsIgnoreCase("Left"));
	}

	public void testGetFiringArc5() // Test target facing west
	{
		Point shooterLocation = new Point(0, 0);
		Point targetLocation = new Point(0, 50);
		int facing = 180;
		String hitArc = Rules.getArc(facing, shooterLocation, targetLocation);

		assertTrue(hitArc.equalsIgnoreCase("Left"));
	}

	public void testGetFiringArc6() // Test target facing southwest
	{
		Point shooterLocation = new Point(0, 0);
		Point targetLocation = new Point(0, 50);
		int facing = 225;
		String hitArc = Rules.getArc(facing, shooterLocation, targetLocation);

		assertTrue(hitArc.equalsIgnoreCase("Front"));
	}

	public void testGetFiringArc7() // Test target facing south
	{
		Point shooterLocation = new Point(0, 0);
		Point targetLocation = new Point(0, 50);
		int facing = 270;
		String hitArc = Rules.getArc(facing, shooterLocation, targetLocation);

		assertTrue(hitArc.equalsIgnoreCase("Front"));
	}

	public void testGetFiringArc8() // Test target facing south
	{
		Point shooterLocation = new Point(0, 0);
		Point targetLocation = new Point(0, 50);
		int facing = 315;
		String hitArc = Rules.getArc(facing, shooterLocation, targetLocation);

		assertTrue(hitArc.equalsIgnoreCase("Front"));
	}

   public void testRollInitiative()
   {
      int test = Rules.rollInitiative();
      assertTrue((test == 1) || (test == 2));
   }

   public static Test suite()
   {
      return new TestSuite(TestRules.class);
   }

   public static void main(String args[])
   {
      junit.textui.TestRunner.run(suite());
   }

}