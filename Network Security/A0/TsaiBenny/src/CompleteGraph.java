package graph;

/**
 * Represents a complete (simple) graph
 * @author Edward Scheinerman
 */

public class CompleteGraph extends Graph {

    /**
     * Construct a complete graph on a given number of vertices
     * @param n Number of vertices
     */
    public CompleteGraph(int n) {
	super(n);
	for(int k=0; k<n-1; k++) {
	    for(int j=k+1; j<n ;j++) {
		addEdge(j,k);
	    }
	}
    }

    /**
     * A simple main for debugging
     */
    public static void main(String args[]) {
	Graph G = new CompleteGraph(5);
	G.print();
    }
}
