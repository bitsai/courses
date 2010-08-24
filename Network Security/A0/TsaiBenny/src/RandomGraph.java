package graph;
import java.util.*;

/**
 * An Erdos-Renyi style random graph. 
 * @author Edward Scheinerman
 */

public class RandomGraph extends Graph {
    

    /**
     * Create a new random graph.
     * @param n the number of vertices
     * @param p the edge probability
     */
    public RandomGraph(int n, double p) {
	super(n);
	Random R = new Random();
	
	for(int a=0; a<n-1; a++) {
	    for(int b=a+1; b<n; b++) {
		if (R.nextDouble() <= p) {
		    addEdge(a,b);
		}
	    }
	}
    }


    /**
     * Create a new random graph with edge probability equal to 1/2. 
     * @param n the number of vertices.
     */
    public RandomGraph(int n) {
	this(n, 0.5);
    }


    /**
     * Simple main for testing
     */
    public static void main(String args[]) {
	RandomGraph G = new RandomGraph(7);
	G.print();
    }
}
