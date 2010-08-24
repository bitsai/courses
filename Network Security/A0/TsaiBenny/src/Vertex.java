package graph;

/**
 * A <code>Vertex</code> object is simply a wrapper for a
 * <code>long</code>. In this package, vertices are represented by
 * (long) integers. 
 * @author Edward Scheinerman
 */

public class Vertex extends GraphElement { 

    /**
     * Create a new vertex object.
     * @param name name (long) of this vertex
     */
    public Vertex(long name) {
	super(name);
    }

    /**
     * Copy constructor.
     * @param that the vertex we wish to copy
     */
    public Vertex(Vertex that) {
	this(that.name);
    }
    
    public String toString() {
	return ""+name;
    }
}
