package gameserver;

import java.util.*;

/** A simple queue class for storing messages that a client hasn't retreived yet.  Uses an internal java.util.LinkedList. */
public class MessageQueue
{
   private LinkedList ln;

   public MessageQueue()
      {
      ln = new LinkedList();
      }

   /** Returns the number of messages in the queue */
   public int size()
      {
      return ln.size();
      }

   /** Returns true if there are no messages in the queue, false otherwise. */
   public boolean isEmpty()
      {
      return (ln.size() == 0);
      }

   /** Returns the first message in the queue. */
   public GameMessage dequeue() throws NoSuchElementException
      {
      return (GameMessage)(ln.removeFirst());
      }
      
   
   /** 	Adds a message to the queue.
   		@param newMsg The message to be enqueued
   	*/
   public void enqueue(GameMessage newMsg)
      {
      ln.addLast(newMsg);
      }
}



