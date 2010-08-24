package utility;
import java.util.*;

/**
 * Represents FIFO queue.
 * @author Edward Scheinerman
 */

public class Queue1 extends LinkedList {

    /**
     * Create a new, empty queue.
     */
    public Queue1() {
	super();
    }

    /**
     * Push an element into the (end of the) queue
     * @param o The object to insert
     */
    public void push(Object o) {
	addFirst(o);
    }


    /**
     * Get (and remove) the next element (head) of the queue
     * @return The object at the head of the queue (first in) or null
     * if the queue is empty.
     */
    public Object pop() {
	if (isEmpty()) return null;
	return removeLast();
    }

    /**
     * Get a reference to (but do not remove) the object at the head
     * of the queue.
     * @return The object at the head of the queue, or null if the
     * queue is empty.
     */
    public Object peek() {
	if (isEmpty()) return null;
	return getLast();
    }
}
