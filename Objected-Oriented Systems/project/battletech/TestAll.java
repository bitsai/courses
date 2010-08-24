package battletech;

import junit.framework.*;
import junit.extensions.*;

/** This class represents suite of all JUnit tests. */

public class TestAll extends TestCase
{
	public static Test suite()
	{
		TestSuite suite = new TestSuite();

		suite.addTest(TestAmmo.suite());
		suite.addTest(TestBattleTech.suite());
		suite.addTest(TestBodyPart.suite());
		suite.addTest(TestComponent.suite());
		suite.addTest(TestMech.suite());
		suite.addTest(TestRules.suite());
		suite.addTest(TestWeapon.suite());
		suite.addTest(TestMap.suite());

		return suite;
	}

	public static void main(String args[])
	{
		junit.textui.TestRunner.run(suite());
	}
}