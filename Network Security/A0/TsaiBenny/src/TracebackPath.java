package graph;

import java.util.*;

/**
* Modified version of Path class from Dr. Scheinerman's graph package.
* Creates new getPath() method to return array of nodes on path.
* getPath() is essentially a hacked-up pathPrint().
*/
public class TracebackPath extends Path {
	/** Constructor method. */
	public TracebackPath(int n) { super(n); }

	/** Another constructor method. */
	public TracebackPath(Graph G, long a, long b) { super(G, a, b); }

	/**
	* The whole point of creating this class.
	* Based on pathPrint(), but returns array of nodes rather than printing.
	* Adds a node to return array wherever pathPrint() output a node.
	*/
    public Vector getPath(long id) {
		Vector nodes = new Vector();

		Vertex v = new Vertex(id);

		if (degree(v) != 1) { return nodes; }

		Object[] nbs = neighborhood(v).toArray();
		Vertex w = (Vertex) nbs[0];

		nodes.add(v);

		while(true) {
			nodes.add(w);
			nbs = neighborhood(w).toArray();
			if (nbs.length == 1) break;

			Vertex ww = w;

			w = (Vertex) nbs[0];
			if (w.getName() == v.getName()) { w = (Vertex) nbs[1]; }

			v = ww;
		}

		return nodes;
    }
}
