package graph;
import java.util.*;


/**
 * Create a (one-dimensional) random X-graph.
 *
 * <p>
 * A random X-graph is created as follows. We are given an array of n
 * real numbers in [0,1]. Then edges are created as follows: there is
 * an edge from i to j with probability x(i)*x(j). 
 * @author Edward Scheinerman
 */

public class Xgraph extends Graph {

    /**
     * Data structure for X-values
     */
    double[] X;

    /**
     * Create a new random X-graph from given X-values.
     * @param xvals a list of xvals. These should be between 0 and 1. 
     */

    public Xgraph(double[] xvals) {
	super();
	if (xvals == null) {
	    X = null;
	}
	

	
	int n = xvals.length;
	X = new double[n];


	for (int k=0; k<n; k++) {
	    X[k] = xvals[k];
	    addVertex(k);
	}
	
	Random R = new Random();
	
	for (int i=0; i<n-1; i++) {
	    for (int j=i+1; j<n; j++) {
		double xx = X[i]*X[j];
		if (R.nextDouble() < xx) {
		    addEdge(i,j);
		}
	    }
	}

    }


    /**
     * Generate a new random X-graph.
     *
     * @param n the number of vertices
     * @param t the x-values are t'th powers of uniforms.
     * @see #xgen
     */
    
    public Xgraph(int n, double t) {
	this(n,t,0.,1.);
    }


    /**
     * Generate a new random X-graph.
     *
     * Automatically generate uniform x-values.
     * 
     * @param n the number of vertices
     * @param t the power
     * @param a the left endpoint of the uniforms
     * @param b the right endpointof the uniforms
     */
    public Xgraph(int n, double t, double a, double b) {
	this(xgen(n,t,a,b));
    }


    /**
     * Create a list of doubles.
     *
     * Each value is a uniform [0,1] raised to a power, t.
     * @param n the length of the list
     * @param t the power to which the uniform values are raised
     * @return a length-n list of such doubles.
     */

    public final static double[] xgen(int n, double t) {
	return xgen(n,t,0.,1.);
    }


    /**
     * Create a list of doubles as powers of a uniform chosen from a
     * given interval.
     *
     * We create an array of double values by chosing a uniform value
     * in an interval [a,b] and raising that value to a power t.
     * @param n the number of doubles to create
     * @param t the power
     * @param a the left end point of the interval (before applying
     * the power).
     * @param b the right end point of the interval
     * @return a list of n random values in [a^t, b^t] created by
     * generating random values in [a,b] and raising them to the t.
     */
    public static double[] xgen(int n, double t, double a, double b) {
	if (n<=0) return null;
	double[] ans = new double[n];
	Random R = new Random();
	for (int k=0; k<n; k++) {
	    double u = R.nextDouble();
	    u = u*(b-a) + a;
	    ans[k] = Math.pow(u,t);
	}
	return ans;
    }
	

    /**
     * Get the X-value associated with a vertex.
     * @param v number for the vertex
     * @return the x-value of v.
     */
    public final double getX(int v) {
	return X[v];
    }



    /**
     * A sample main.
     *
     * This main generates a random X-graph and reports on the number
     * of vertices and edges, and the number of edges it expected to
     * find. <p>
     * Usage: <code> java graph.Xgraph n t </code> <br>
     * where n is the number of vertices and t is the power to which
     * the uniform values are raised to create the list of x values. 
     */

    public static void main(String[] args) {

	if (args.length < 2) {
	    System.out.println("Usage: java graph.Xgraph n t\n");
	    System.exit(1);
	}

	int n = 0;
	double t = 0.;

	try {
	    n = Integer.parseInt(args[0]);
	    t = Double.parseDouble(args[1]);
	}
	catch (NumberFormatException e) {
	    System.err.println(e);
	    System.exit(1);
	}

	System.out.println("n = " + n + " and t = " + t);

	Xgraph G = new Xgraph(n,t);
	System.out.print("X-graph with " + G.nV() + 
			 " vertices and ");
	System.out.println(G.nE()+ " edges");

	double xn = (double) n;
	double xpe = xn*(xn-1.) / (2 * (1+t)*(1+t));
	System.out.println("Expected number of edges is " + 
			   (float) xpe);

    }

}
