package graph;

/**
 * A GraphElement is a superclass for named graph objects, namely
 * vertices and edges.
 * @see Vertex
 * @see Edge
 * @author Edward Scheinerman
 */

public class GraphElement implements Comparable {

    /**
     * The name of this graph element.
     */
    long name; 

    /**
     * Basic constructor.
     * @param name Name of this new GraphElement.
     */
    
    public GraphElement(long name) {
	this.name = name;
    }


    /**
     * Default constructor. Equivalent to <code>GraphElement(0)</code>
     */
    public GraphElement() {
	this(0);
    }


    /**
     * What is the name (long integer) of this object?
     * @return the (long) name of this object.
     */
    public final long getName() { return name; }


    /**
     * Compare the names of two GraphElements.
     *
     * This method is required by the Comparable interface.
     * @param that another GraphElement to which we compare this
     * @return negative, zero, or positive depending on whether this
     * is less, equal, or greater than that.
     */

    public int compareTo(Object that) {
	GraphElement That = (GraphElement) that;
	long diff = this.name - That.name;

	if (diff < 0) return -1;
	if (diff > 0) return 1;
	return 0;
    }
}
