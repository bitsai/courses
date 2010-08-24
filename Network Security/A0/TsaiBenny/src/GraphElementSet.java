package graph;
import java.util.*;

/**
 * Represents a set of vertices or of edges.
 * @author Edward Scheinerman
 */

public class GraphElementSet extends TreeSet {

    /**
     * Basic constructor: builds an empty set.
     */
    public GraphElementSet() { super(); }


    /**
     * Write element names into an array of longs.
     * @return An array containing the names (as longs) of the
     * elements of this set. If the set is empty, <code>null</code> is
     * returned. 
     */
    public long[] toLongArray() { 
	if (size() == 0) return null;
	int n = size();
	long[] ans = new long[n];
	Iterator it = iterator();
	int k = 0;
	while(it.hasNext()) {
	    GraphElement e = (GraphElement) it.next();
	    ans[k] = e.getName();
	    k++;
	}
	return ans;
    }

    /**
     * Test for element membership.
     * @param e A graph element 
     * @return true if <code>e</code> is a member of this set; false,
     * otherwise. 
     */
    public final boolean has(GraphElement e) {
	return contains(e);
    }

    /**
     * Test for membership based on name.
     * @param name the name of a graph element whose membership we are
     * testing. 
     * @return true if there an element in this set whose name is
     * <code>name</code>; false, otherwise.
     */
    public final boolean has(long name) {
	return has(new GraphElement(name));
    }


    /**
     * Compute the intersection of sets.
     * @param that Another set
     * @return a new set whose elements are exactly those in common
     * with this and that.
     */

    public GraphElementSet intersect(GraphElementSet that) {
	GraphElementSet ans = new GraphElementSet();
	Iterator it = iterator();
	while (it.hasNext()) {
	    GraphElement e = (GraphElement) it.next();
	    if (that.has(e)) {
		ans.add(e);
	    }
	}
	return ans;
    }


    /**
     * Compute the intersection of two GraphElementSets.
     * @param a one GraphElementSet
     * @param b another GraphElementSet
     * @return A new GraphElementSet that is the intersection of
     * <code>a</code> and <code>b</code>.
     */
    public static GraphElementSet intersect(GraphElementSet a,
					    GraphElementSet b) {
	return a.intersect(b);
    }



    /**
     * String representation.
     * @return String representation of this set
     */
    public String toString() {
	String ans = "{ ";
	Iterator it = iterator();
	while (it.hasNext()) {
	    GraphElement e = (GraphElement) it.next();
	    ans += e + " " ;
	}
	ans += "}";
	return ans;
    }


    /**
     * Get name of largest element in this set.
     * @return The name of the largest element in this set. If this
     * set is empty, we return <code>Long.MIN_VALUE</code>
     */
    public long largestName() {
	if (isEmpty()) {
	    return Long.MIN_VALUE;
	}
	GraphElement e = (GraphElement) last();
	return e.getName();
    }

    /**
     * Find a name that is not in use in this set.
     * @return A label that is not used by any element in this set.
     * If this set is empty, the value 0 is returned. Otherwise, a
     * value one larger than the current maximum label is returned. 
     * In case the largest name in the set is Long.MAX_VALUE, then we
     * have a problem, and we simply look for an unused name one by
     * one (by wrapping to Long.MIN_VALUE).
     */
    public long findUnusedName() {
	if (isEmpty()) return 0L;
	long ans = largestName()+1;
	while (has(ans)) ans++;
	return ans;
    }



}
