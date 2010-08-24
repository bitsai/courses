package gameserver;

import junit.framework.*;


/**
	Unit tests for PlayerList class.
*/
public class PlayerListTest extends TestCase
{
	PlayerList myList;
	
	protected void setUp() 
		{
		myList = new PlayerList();
		}	

	public void testAdd()
		{
		myList.add("bob");
		assertNotNull( myList.lookup("bob") );
		assertNull( myList.lookup("phil") );
		}
		
	public void testRemove()
		{
		myList.add("bill");
		myList.add("mark");
		myList.remove( myList.lookup("mark") );
		assertNull( myList.lookup("mark") );
		assertNotNull( myList.lookup("bill") );
		assertEquals( myList.lookup("bill").getName(), "bill" );
		}


	public void testDatabase() throws java.io.IOException
		{
		myList.addToDatabase("Snarf", "pass");
		assertEquals(myList.lookupPassword("Snarf"), "pass");
		}
		

	public static Test suite() 
		{
		return new TestSuite(PlayerListTest.class);
		}
}
