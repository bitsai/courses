package gameserver;

import junit.framework.*;


/**
	Unit Tests for MessageQueue class.
*/
public class MessageQueueTest extends TestCase
{
	private MessageQueue myQueue;
	private GameMessage dummyMessage;
	
	protected void setUp() 
		{
		myQueue = new MessageQueue();
		dummyMessage = new TextMessage(null);
		}

	public void testIsEmpty()
		{
		assertTrue( myQueue.isEmpty() );
		myQueue.enqueue(dummyMessage);
		assertFalse( myQueue.isEmpty() );
		}
		
	public void testSize()
		{
		myQueue.enqueue(dummyMessage);
		myQueue.enqueue(dummyMessage);
		assertTrue( myQueue.size() == 2 );	
		myQueue.enqueue(dummyMessage);
		myQueue.dequeue();
		myQueue.enqueue(dummyMessage);
		assertTrue( myQueue.size() == 3 );	
		}
		
	public void testDequeue()
		{
		myQueue.enqueue(dummyMessage);
		assertSame(dummyMessage, myQueue.dequeue());
		}

	public static Test suite() 
		{
		return new TestSuite(MessageQueueTest.class);
		}
}
