package graph;

/**
 * Represents an edge of a graph. This may be either directed or
 * undirected, and it might or might not be a loop.
 * @author Edward Scheinerman
 */

public class Edge extends GraphElement {
    
    long    head;
    long    tail;
    boolean directed;

    /**
     * Fundamental constructor. Create a new edge with a given name,
     * head, tail, and determine if it's a directed edge.
     * @param name the name of this edge
     * @param tail the tail (source) of this edge
     * @param head the head (destination) of this edge
     * @param directed true if a directed edge, false if undirected.
     */
    public Edge(long name, long tail, long head, boolean directed)
    {
	super(name);
	this.head = head;
	this.tail = tail;
	this.directed = directed;
	sort();
    }

    /**
     * Constructor in which head and tail are specified as type
     * <code>Vertex</code>.
     * @param name the name of this edge
     * @param tail the tail (source) of this edge
     * @param head the head (destination) of this edge
     * @param directed true if a directed edge, false if undirected.
     */
    public Edge(long name, Vertex tail, Vertex head, boolean directed)
    {
	this(name, tail.getName(), head.getName(), directed);
    }

    /**
     * Copy constructor.
     * @param that an edge we wish to clone.
     */
    public Edge(Edge that) {
	this(that.name, that.head, that.tail, that.directed);
    }


    /**
     * Check if this edge is a loop.
     * @return true if the head and tail of this edge are the same,
     * false otherwise.
     */
    public final boolean isLoop() { return head == tail; }
    

    /**
     * Is this edge a directed edge?
     * @return true if this is a directed edge
     */
    public final boolean isDirected() { return directed; }

    /**
     * Decide whether or not this edge is a directed edge.
     * @param directed if this is true, the edge is set to directed;
     * if this is false, the edge is set to undirected.
     */
    public final void setDirected(boolean directed) {
	this.directed = directed;
    }


    /**
     * What is the head of this edge?
     * @return the head of this edge (type Vertex)
     */
    public final Vertex getHead() {
	return new Vertex(head);
    }

    /**
     * What is the head of this edge?
     * @return the head of this edge (type long)
     */
    public final long getHeadLong() {
	return head;
    }


    /**
     * What is the tail of this edge?
     * @return the tail of this edge (type Vertex)
     */
    public final Vertex getTail() {
	return new Vertex(tail);
    }

    /**
     * What is the tail of this edge?
     * @return the tail of this edge (type long)
     */
    public final long getTailLong() {
	return tail;
    }


    /**
     * If the edge is undirected, we make the tail (src) vertex the
     * one with the smaller label
     */

    private final void sort() {
	if(!directed) {
	    if (tail > head) {
		long tmp = tail;
		tail = head;
		head = tmp;
	    }
	}
    }



    /**
     * String representation.
     *
     * Undirected edges are output in the form <code>{a,b}</code>.
     * Directed edges are output in the form <code>(a,b)</code>.
     * In both cases <code>a</code> is the tail vertex.
     * @return String representation of this edge.
     */

    public String toString() {
	if (isDirected()) {
	    return "(" + tail + "," + head + ")";
	}
	else {
	    return "{" + tail + "," + head + "}";
	}
    }



    
}
