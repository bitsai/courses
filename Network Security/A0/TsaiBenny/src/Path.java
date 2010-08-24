package graph;
import java.util.*;
import utility.*;

/**
 * Used to create path graphs and to find shortest paths in graphs.
 *
 * <p>
 * Once a path is created, it <ital>can</ital> be modified using the
 * usual <code>Graph</code> methods, but the end result might not be a
 * path. This might be bad.
 * @author Edward Scheinerman
 */


public class Path extends Graph {

    /**
     * Create a simple path on a given number of vertices
     * @param n The number of vertices
     */
    public Path(int n) {
	super();
	for(int k=0; k<n-1; k++) {
	    addEdge(k,k+1);
	}
    }

    /**
     * Find a shortest path between given vertices in a graph.
     *
     * If the vertices do not exist in the graph, or if there is no
     * path possible, an empty graph is created.
     * @param G The graph in which to find the shortest path.
     * @param a A vertex.
     * @param b Another vertex.
     */
    public Path(Graph G, long a, long b) {
	super();
	if (!G.hasVertex(a)) return;
	if (!G.hasVertex(b)) return;

	if (a==b) {
	    addVertex(a);
	    return;
	}

	// f will be a function from V to the Natural numbers
	TreeMap dist = new TreeMap();      // distance label
	TreeMap traceBack = new TreeMap(); // Way to go back
	Vertex A = new Vertex(a);
	dist.put(A , new Integer(0));

	Queue1 Q = new Queue1();
	Q.push(A);


    explore:
	while (!Q.isEmpty()) {
	    Vertex v = (Vertex) Q.pop();
	    int val = ( (Integer) dist.get(v) ).intValue();
	    GraphElementSet N = G.neighborhood(v);
	    Iterator it = N.iterator();
	    while (it.hasNext()) {
		Vertex w = (Vertex) it.next();
		if (!dist.containsKey(w)) {
		    dist.put(w, new Integer(val+1));
		    Q.push(w);
		    traceBack.put(w,v);
		}
		if (w.getName() == b) break explore;
	    }
	}



	// If no route back from b, then there's no path
	if (traceBack.get(new Vertex(b)) == null) {
	    return;
	}


	// Otherwise, trace a route backwards

	Vertex current = new Vertex(b);
	addVertex(current);
	while(true) {
	    Vertex prev = (Vertex) traceBack.get(current);
	    if (prev == null) break;
	    addVertex(prev);
	    Edge e = G.findAnyEdge(prev,current);
	    addEdge(e);
	    current = prev;
	}

    }

    /**
     * Print this path on the standard output.
     */
    public void pathPrint() {
	if (nV() == 0) {
	    System.out.println("No vertices in this path");
	    return;
	}

	if (nV() == 1) {
	    System.out.print("Path: ");
	    Vertex v = (Vertex) V.first();
	    System.out.println(v);
	}

	// Find a vertex of degree 1
	Iterator it = V.iterator();
	Vertex v = null;
	while (it.hasNext()) {
	    v = (Vertex) it.next();
	    if (degree(v)==1) break;
	}

	if (degree(v) != 1) {
	    System.err.println("Path corrupted -- no leaf");
	    return;
	}

	// Trace the path starting at v...

	Object[] nbs = neighborhood(v).toArray();
	Vertex w = (Vertex) nbs[0];

	System.out.print(v + " -- ");

	while(true) {
	    System.out.print(w);
	    nbs = neighborhood(w).toArray();
	    if (nbs.length == 1) break;

	    Vertex ww = w;

	    // choose new w
	    w = (Vertex) nbs[0];
	    if (w.getName() == v.getName()) {
		w = (Vertex) nbs[1];
	    }
	    System.out.print(" -- ");
	    v = ww;
	}
	System.out.print("\n");
    }


    /**
     * Simple main for debugging.
     */
    public static void main(String args[]) {
	Graph G  =  new RandomGraph(100,.02);
	Path P = new Path(G,0,1);
	System.out.println("Found path of length " + P.nE());
	P.pathPrint();
    }


}
