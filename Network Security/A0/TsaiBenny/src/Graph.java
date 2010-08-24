package graph;
import java.util.*;

/**
 * Represents a general graph that can have loops, parallel edges, and
 * directed and/or undirected edges. 
 * @author Edward Scheinerman
 */


public class Graph {

    protected GraphElementSet V;    // The vertex set (set of Vertex)
    protected GraphElementSet E;    // The edge set (set of Edge)
    private TreeMap VR;           // Lookup vertex records

    /* 
     * How this stuff works. There are two sets, V and E, representing
     * the Vertex and Edge sets of the graph. There is a map VR which
     * maps vertices to VertexRecords (an inner class below). The
     * VertexRecord records information about degree and incident
     * edges. 
     */

    
    /**
     * Basic constructor.
     *
     * Creates a new empty graph (no vertices or edges).
     */
    public Graph() {
	V = new GraphElementSet();
	E = new GraphElementSet();
	VR = new TreeMap();
    }


    /**
     * Create an edgeless graph with a specified number of vertices.
     * @param n the number of vertices. The vertices will be numbered
     * 0 through n-1.
     */
    public Graph(int n) {
	this();
	for(int k=0; k<n; k++) {
	    addVertex();
	}
    }


    /**
     * How many vertices does this graph have?
     * @return The number of vertices
     */
    public final int nV() { return V.size(); }


    /** 
     * How many edges does this graph have?
     * @return The number of edges (of all types).
     */
    public final int nE() { return E.size(); }




    /**
     * Array of the vertices (of type Vertex)
     * @return An array of all the vertices in this graph, or null if
     * there are no vertices.
     */
    public Vertex[] vertexArray() {
	int n = nV();
	if (n==0) return null;
	Vertex[] ans = new Vertex[n];
	Iterator it = V.iterator();
	int k=0;
	while (it.hasNext()) {
	    ans[k] = (Vertex) it.next();
	    k++;
	}
	return ans;
    }



    /**
     * Array of the edges (of type Edge)
     * @return An array of all teh edges in this graph, or null if
     * there are no edges.
     */
    public Edge[] edgeArray() {
	int m = nE();
	if (m==0) return null;
	Edge[] ans = new Edge[m];
	Iterator it = E.iterator();
	int k=0;
	while (it.hasNext()) {
	    ans[k] = (Edge) it.next();
	    k++;
	}
	return ans;
    }

	
	



    // Methods to check if the graph has named vertices/ edges

    /**
     * Is this vertex in this graph?
     * @param name Name of the vertex we seek 
     * @return true if the vertex is a member of this graph
     */
    public final boolean hasVertex(long name) {
	return V.has(name);
    }

   /**
     * Is this vertex in this graph?
     * @param v the vertex we seek 
     * @return true if the vertex is a member of this graph
     */
    public final boolean hasVertex(Vertex v) {
	return V.has(v);
    }

    /**
     * Is this vertex in this graph?
     * @param v the vertex we seek 
     * @return true if the vertex is a member of this graph
     */
    public final boolean has(Vertex v) { 
	return hasVertex(v);
    }


    /**
     * Is this edge in this graph?
     * @param name Name of the edge  we seek 
     * @return true if the edge is a member of this graph
     */
    public final boolean hasEdge(long name) {
	return E.has(name);
    }

    /**
     * Is this edge in this graph?
     * @param e  the edge  we seek 
     * @return true if the edge is a member of this graph
     */
    public final boolean hasEdge(Edge e) {
	return E.has(e);
    }


    /**
     * Is this edge in this graph?
     * @param e  the edge  we seek 
     * @return true if the edge is a member of this graph
     */
    public final boolean has(Edge e) {
	return hasEdge(e);
    }




    // VERTEX ADDITION METHODS

    /**
     * Add a new vertex to this graph. 
     * @param name The name of the new vertex
     * @return true if succesful; false, if not. The usual reason for
     * returning false is that this graph already had a vertex by the
     * given name. 
     */
    public boolean addVertex(long name) {
	if (V.has(name)) return false;
	Vertex v = new Vertex(name);
	V.add(v);
	VertexRecord vr = new VertexRecord();
	VR.put(v, vr);
	return true; 
    }


    /**
     * Add a new vertex to this graph. 
     * @param v The vertex we wish to add.
     * @return true if succesful; false, if not. The usual reason for
     * returning false is that this graph already had a vertex by the
     * given name. 
     */
    public final boolean addVertex(Vertex v) {
	return addVertex(v.getName());
    }


    /**
     * Add a new vertex to this graph. 
     * @param v The vertex we wish to add.
     * @return true if succesful; false, if not. The usual reason for
     * returning false is that this graph already had a vertex by the
     * given name. 
     */
    public final boolean add(Vertex v) {
	return addVertex(v);
    }
    
    /**
     * Add a new vertex without specifying a name
     * @return The vertex added. The name of this new vertex will not
     * conflict with any already held by this graph.
     * @see graph.GraphElementSet#findUnusedName()
     */
    public final Vertex addVertex() {
	long name = V.findUnusedName();
	addVertex(name);
	return new Vertex(name);
    }


    /**
     * Retrieve the VertexRecord associated with a given name.
     * @param name the name of the vertex we wish to access.
     * @return The vertex record associated with this name, or null if
     * there is no such vertex.
     */
    final VertexRecord getVertexRecord(long name) { 
	if (V.has(name)) {
	    Vertex v = new Vertex(name);
	    return (VertexRecord) VR.get(v);
	}
	return null;
    }




    /**
     * Method to look up vertex record of a given vertex
     * @param v Name of vertex we wish to access
     * @return The vertex record associated with this vertex.
     */
    final VertexRecord getVertexRecord(Vertex v) {
	return getVertexRecord(v.getName());
    }



    // EDGE ADDITION METHODS

    /**
     * Add an edge to this graph
     *
     * This method assumes the edge is already "built". If the
     * endpoints of this edge are not in the graph, they are
     * automatically added to the graph. 
     * @param e The edge we wish to add
     * @return true if succesful; false, otherwise. This will fail
     * (and return false) if there is already an edge with the given
     * name in the graph.
     */
    public boolean addEdge(Edge e) { 
	// First check if there is already an edge by this name.
	if (E.has(e)) return false;

	// Now we're good to go on adding this edge.
	// First add it to the master edge set E

	E.add(e);

	// Next extract the endpoints
	long t = e.getTail().getName();
	long h = e.getHead().getName();

	// Check that these vertices exist. If not, add them.
	if (!hasVertex(t)) {
	    addVertex(t);
	}
	if (!hasVertex(h)) {
	    addVertex(h);
	}
	

	// And the corresponding VertexRecords
	VertexRecord vrt = getVertexRecord(t);
	VertexRecord vrh = getVertexRecord(h);

	// Add edge to incident edge sets
	vrt.I.add(e);
	vrh.I.add(e);

	// Now we update degrees
	if (e.isDirected()) {
	    vrt.outDegree++;
	    vrh.inDegree++;
	}
	else {
	    vrh.uDegree++;
	    vrt.uDegree++;
	}
	
	return true;
    }

    /**
     * Add an edge to this graph
     *
     * This method assumes the edge is already "built". If the
     * endpoints of this edge are not in the graph, they are
     * automatically added to the graph. 
     * @param e The edge we wish to add
     * @return true if succesful; false, otherwise. This will fail
     * (and return false) if there is already an edge with the given
     * name in the graph.
     */
    public final boolean add(Edge e) {
	return addEdge(e);
    }

    /**
     * Add an undirected edge to the graph.
     *
     * Given the names of two vertices, add an undirected edge. If the
     * vertices are not already in the graph, they are added. 
     * @param a One endpoint
     * @param b Another endpoint (may be same as a for a loop)
     * @return An Edge object representing the edge that was added.
     */
    public Edge addEdge(long a, long b) {
	long name = E.findUnusedName();
	Edge e = new Edge(name,a,b,false);
	addEdge(e);
	return e;
    }

    /**
     * Add an undirected edge to the graph.
     *
     * Given the names of two vertices, add an undirected edge. If the
     * vertices are not already in the graph, they are added. 
     * @param a One endpoint
     * @param b Another endpoint (may be same as a for a loop)
     * @return An Edge object representing the edge that was added.
     */
    public Edge addEdge(Vertex a, Vertex b) {
	return addEdge(a.getName(), b.getName());
    }

    /**
     * Add a directed edge to this graph.
     *
     * Given the names of two vertices, add a directed edge. If the
     * vertices are not already in the graph, they are added.
     * @param tail Tail (source) of the added directed edge.
     * @param head Head (destination) of the added directed edge.
     * @return An Edge object representing the edge that was added.
     */
    public Edge addDirectedEdge(long tail, long head) {
	long name = E.findUnusedName();
	Edge e = new Edge(name,tail,head,true);
	addEdge(e);
	return e;
    }

    /**
     * Add a directed edge to this graph.
     *
     * Given the names of two vertices, add a directed edge. If the
     * vertices are not already in the graph, they are added.
     * @param tail Tail (source) of the added directed edge.
     * @param head Head (destination) of the added directed edge.
     * @return An Edge object representing the edge that was added.
     */
    public Edge addDirectedEdge(Vertex tail, Vertex head) {
	return addDirectedEdge(tail.getName(), head.getName());
    }

    /**
     * Add a directed edge (arc) to this graph.
     *
     * Given the names of two vertices, add a directed edge. If the
     * vertices are not already in the graph, they are added.
     * @param tail Tail (source) of the added directed edge.
     * @param head Head (destination) of the added directed edge.
     * @return An Edge object representing the edge that was added.
     */
    public final Edge addArc(long tail, long head) {
	return addDirectedEdge(tail, head);
    }

    /**
     * Add a directed edge (arc) to this graph.
     *
     * Given the names of two vertices, add a directed edge. If the
     * vertices are not already in the graph, they are added.
     * @param tail Tail (source) of the added directed edge.
     * @param head Head (destination) of the added directed edge.
     * @return An Edge object representing the edge that was added.
     */
    public final Edge addArc(Vertex tail, Vertex head) {
	return addDirectedEdge(tail.getName(), head.getName());
    }




    // ADJACENCY TESTING METHODS

    /**
     * Find all edges between two vertices
     * @param a One vertex
     * @param b Another vertex
     * @return A GraphElementSet containing all the edges incident
     * with both vertices. If either vertex is not in this graph, an
     * empty set is returned. 
     */
    public GraphElementSet findAllEdges(long a, long b) {

	// If either endpoint is missing, return false
	if ( (!V.has(a)) || (!V.has(b)) ) return new GraphElementSet();

	// If the end points are different, then we simply want to
	// intersect the two sets of edges incident thereon

	if (a != b) {
	    VertexRecord vra = getVertexRecord(a);
	    VertexRecord vrb = getVertexRecord(b);
	    return  vra.I.intersect(vrb.I);
	}

	// Otherwise (a==b) then we want all the loops 
	VertexRecord vra = getVertexRecord(a);
	GraphElementSet ans = new GraphElementSet();
	Iterator it = vra.I.iterator();
	while (it.hasNext()) {
	    Edge e = (Edge) it.next();
	    if (e.isLoop()) ans.add(e);
	}
	return ans;

    }

    /**
     * Find all edges between two vertices
     * @param a One vertex
     * @param b Another vertex
     * @return A GraphElementSet containing all the edges incident
     * with both vertices. If either vertex is not in this graph, an
     * empty set is returned. 
     */
    public final GraphElementSet findAllEdges(Vertex a, Vertex b) {
	return findAllEdges(a.getName(), b.getName());
    }


    /**
     * Find all edges incident with a given vertex 
     * @param a The vertex
     * @return The set of all edges incident with a. If a is not in
     * the graph, an emptyset is returned.
     */

    public GraphElementSet findAllEdges(long a) {
	if (!hasVertex(a)) return new GraphElementSet();
	VertexRecord vra = getVertexRecord(a);
	GraphElementSet ans = new GraphElementSet();
	Iterator it = vra.I.iterator();
	while(it.hasNext()) {
	    ans.add( (Edge) it.next());
	}
	return ans;
    }

    /**
     * Find all edges incident with a given vertex 
     * @param a The vertex
     * @return The set of all edges incident with a. If a is not in
     * the graph, an emptyset is returned.
     */
    public final GraphElementSet findAllEdges(Vertex a) {
	return findAllEdges(a.getName());
    }


    /**
     * Find any edge between two vertices
     * @param a a vertex
     * @param b a vertex
     * @return An ab-edge if one exists; null, otherwise.
     */
    public Edge findAnyEdge(long a, long b) {
	GraphElementSet ab = findAllEdges(a,b);
	if (ab.isEmpty()) return null;
	return (Edge) ab.first();
    }


    /**
     * Find any edge between two vertices
     * @param a a vertex
     * @param b a vertex
     * @return An ab-edge if one exists; null, otherwise.
     */
    public final Edge findAnyEdge(Vertex a, Vertex b) {
	return findAnyEdge(a.getName(), b.getName());
    }


    /**
     * Test if there is any edge joining two given vertices
     * @param a One vertex
     * @param b Another vertex
     * @return true If there is any edge whose endpoints are a and b. 
     */
    public boolean hasEdge(long a, long b) {
	GraphElementSet S = findAllEdges(a,b);
	return !S.isEmpty();
    }

    /**
     * Test if there is any edge joining two given vertices
     * @param a One vertex
     * @param b Another vertex
     * @return true If there is any edge whose endpoints are a and b. 
     */
    public final boolean hasEdge(Vertex a, Vertex b) {
	return hasEdge(a.getName(), b.getName());
    }


    /** 
     * Test if there is a directed edge joining two vertices
     * @param tail the tail of the directed edge we seek
     * @param head the head of the directed edge we seek
     * @return true if there is a directed edge from tail to head.
     */
    
    public boolean hasDirectedEdge(long tail, long head) {
	GraphElementSet S = findAllEdges(tail,head);
	Iterator it = S.iterator();
	while(it.hasNext()) {
	    Edge e = (Edge) it.next();
	    if (
		(e.isDirected()) && 
		(e.getTailLong() == tail) &&
		(e.getHeadLong() == head)
		) 
		return true;
	}
	return false;
    }
	
    /** 
     * Test if there is a directed edge joining two vertices
     * @param tail the tail of the directed edge we seek
     * @param head the head of the directed edge we seek
     * @return true if there is a directed edge from tail to head.
     */
    public final boolean hasDirectedEdge(Vertex tail, Vertex head) {
	return hasDirectedEdge(tail.getName(), head.getName());
    }




    /** 
     * Test if there is an undirected edge joining two vertices
     * @param a one end of the edge we seek
     * @param b the other end
     * @return true if there is an undirected edge joining a and b.
     */
    
    public boolean hasUndirectedEdge(long a, long b) {
	GraphElementSet S = findAllEdges(a,b);
	Iterator it = S.iterator();
	while(it.hasNext()) {
	    Edge e = (Edge) it.next();
	    if (!e.isDirected()) {
		if ( (e.getHeadLong()==a) && (e.getTailLong()==b) ) 
		    return true;
		if ( (e.getHeadLong()==b) && (e.getTailLong()==a) ) 
		    return true;
	    }
		
	}
	return false;
    }
	
    /** 
     * Test if there is an undirected edge joining two vertices
     * @param a one end of the edge we seek
     * @param b the other end
     * @return true if there is an undirected edge joining a and b.
     */
    public final boolean hasUndirectedEdge(Vertex a, Vertex b) {
	return hasUndirectedEdge(a.getName(), b.getName());
    }





    /// NEIGHBORHOOD CALCULATION ///

    /**
     * Find the neighborhood of a vertex.
     * @param a a vertex
     * @return the set of all vertices adjancent to a. This includes a
     * if and only if there is a loop at a. 
     */

    public GraphElementSet neighborhood(long a) {
	GraphElementSet edges = findAllEdges(a);
	GraphElementSet ans = new GraphElementSet();
	Iterator it = edges.iterator();
	while (it.hasNext()) {
	    Edge e = (Edge) it.next();
	    if (e.isLoop()) {
		ans.add(new Vertex(a));
	    }
	    else {
		long s = e.getTailLong();
		long t = e.getHeadLong();
		Vertex b;
		if (s==a) {
		    b = new Vertex(t);
		}
		else {
		    b = new Vertex(s);
		}
		ans.add(b);
	    }
	}
	return ans;
    }


    /**
     * Find the neighborhood of a vertex.
     * @param a a vertex
     * @return the set of all vertices adjancent to a. This includes a
     * if and only if there is a loop at a. 
     */
    public final GraphElementSet neighborhood(Vertex a) {
	return neighborhood(a.getName());
    }



    //// EDGE DELETION /////

    /**
     * Delete an edge from this graph.
     * @param e The edge we wish to delete
     */

    public void deleteEdge(Edge e) {
	if (!has(e)) return;  // no such edge
	
	Vertex a = e.getTail();
	Vertex b = e.getHead();


	// Delete e from a's records
	VertexRecord vra = getVertexRecord(a);
	vra.I.remove(e);
	if (e.isDirected()) {
	    vra.outDegree--;
	}
	else {
	    vra.uDegree--;
	}

	// Delete e from b's records
	VertexRecord vrb = getVertexRecord(b);
	vrb.I.remove(e);
	if (e.isDirected()) {
	    vrb.inDegree--;
	}
	else {
	    vrb.uDegree--;
	}

	// Delete e from the master list
	E.remove(e);

    }
	

    /**
     * Delete an edge from this graph.
     * @param e The edge we wish to delete
     */

    public final void delete(Edge e) {
	deleteEdge(e);
    }


    /** 
     * Delete all edges between a pair of given vertices.
     * @param a one vertex
     * @param b the other vertex
     */
    public void deleteAllEdges(long a, long b) {
	
	GraphElementSet Eab = findAllEdges(a,b);
	Iterator it = Eab.iterator();
	while (it.hasNext()) {
	    Edge e = (Edge) it.next();
	    deleteEdge(e);
	}
    }
	
	
    /** 
     * Delete all edges between a pair of given vertices.
     * @param a one vertex
     * @param b the other vertex
     */
    public void deleteAllEdges(Vertex a, Vertex b) {
	deleteAllEdges(a.getName(), b.getName());
    }




    /**
     * Delete all edge incident with a given vertex.
     * @param a A vertex
     */
    public void deleteAllEdges(long a) {
	if (!hasVertex(a)) return;
	GraphElementSet Ea = findAllEdges(a);
	Iterator it = Ea.iterator();
	while (it.hasNext()) {
	    deleteEdge( (Edge) it.next());
	}
    }
    
    /**
     * Delete all edge incident with a given vertex.
     * @param a A vertex
     */
    public final void deleteAllEdges(Vertex a) {
	deleteAllEdges(a.getName());
    }


   
    ////////////////// VERTEX DELETION //////////////////


    /**
     * Delete a vertex from this graph.
     *
     * When a vertex is deleted, all edges indicident with that vertex
     * are deleted as well.
     * @param a the vertex we wish to delete.
     */
    public void deleteVertex(long a) {
	if (!hasVertex(a)) return;
	deleteAllEdges(a);
	Vertex A = new Vertex(a);
	V.remove(A);
	VR.remove(A);
    }
	

    /**
     * Delete a vertex from this graph.
     *
     * When a vertex is deleted, all edges indicident with that vertex
     * are deleted as well.
     * @param a the vertex we wish to delete.
     */
    public final void deleteVertex(Vertex a) {
	deleteVertex(a.getName());
    }

    /**
     * Delete a vertex from this graph.
     *
     * When a vertex is deleted, all edges indicident with that vertex
     * are deleted as well.
     * @param a the vertex we wish to delete.
     */
    public final void delete(Vertex a) {
	deleteVertex(a.getName());
    }



    // CONVERT A GENERAL GRAPH INTO A SIMPLE GRAPH //

    /**
     * Convert this graph into a simple graph.
     *
     * All loops are deleted. If there are any edges between a pair of
     * distinct vertices, they are replaced with a single, undirected
     * edge.
     */
    public void simplify() {
	Vertex[] vlist = vertexArray();
	
	// Phase I: Kill the loops
	for (int k=0; k<vlist.length; k++) {
	    deleteAllEdges(vlist[k], vlist[k]);
	}

	// Phase II: Replace all ab edges with a single, undirected ab
	// edge.
	for (int k=0; k<vlist.length-1; k++) {
	    for (int j=k+1; j<vlist.length; j++) {
		Vertex a = vlist[k];
		Vertex b = vlist[j];
		if (hasEdge(a,b)) {
		    deleteAllEdges(a,b);
		    addEdge(a,b);
		}
	    }
	}
    }
    


    //// DEGREE METHODS ////

    /**
     * What is the degree of this vertex?
     * @param v the vertex
     * @return the number of edges incident with this vertex (loops
     * counting double). Return -1 if the vertex is not in the graph.
     */
    public final int degree(long v) {
	if (!hasVertex(v)) return -1;
	VertexRecord vr = getVertexRecord(v);
	return vr.uDegree + vr.outDegree + vr.inDegree;
    }

    /**
     * What is the degree of this vertex?
     * @param v the vertex
     * @return the number of edges incident with this vertex (loops
     * counting double). Return -1 if the vertex is not in the graph.
     */
    public final int degree(Vertex v) {
	return degree(v.getName());
    }

    /**
     * What is the outDegree of this vertex?
     * @param v the vertex
     * @return the number of directed edges leaving this vertex.
     * Return -1 if the vertex is not in the graph.
     */
    public final int outDegree(long v) {
	if (!hasVertex(v)) return -1;
	VertexRecord vr = getVertexRecord(v);
	return vr.outDegree;
    }
    
    /**
     * What is the outDegree of this vertex?
     * @param v the vertex
     * @return the number of directed edges leaving this vertex.
     * Return -1 if the vertex is not in the graph.
     */
    public final int outDegree(Vertex v) {
	return outDegree(v.getName());
    }

    /**
     * What is the inDegree of this vertex?
     * @param v the vertex
     * @return the number of directed edges entering this vertex.
     * Return -1 if the vertex is not in the graph.
     */
    public final int inDegree(long v) {
	if (!hasVertex(v)) return -1;
	VertexRecord vr = getVertexRecord(v);
	return vr.inDegree;
    }
    
    /**
     * What is the inDegree of this vertex?
     * @param v the vertex
     * @return the number of directed edges entering this vertex.
     * Return -1 if the vertex is not in the graph.
     */
    public final int inDegree(Vertex v) {
	return inDegree(v.getName());
    }

    /**
     * What is the undirected Degree of this vertex?
     * @param v the vertex
     * @return the number of undirected edges incident with this vertex.
     * Return -1 if the vertex is not in the graph.
     */
    public final int undirectedDegree(long v) {
	if (!hasVertex(v)) return -1;
	VertexRecord vr = getVertexRecord(v);
	return vr.uDegree;
    }

    /**
     * What is the undirected Degree of this vertex?
     * @param v the vertex
     * @return the number of undirected edges incident with this vertex.
     * Return -1 if the vertex is not in the graph.
     */   
    public final int undirectedDegree(Vertex v) {
	return undirectedDegree(v.getName());
    }



    /**
     * Find distance between two vertices.
     * @param a a vertex.
     * @param b a vertex.
     * @return The distance from a to b. If no ab path exists, -1 is
     * returned. If either a or b is not in this graph, -2 is
     * returned.
     */
    public int distance(long a, long b) {
	if (!hasVertex(a)) return -2;
	if (!hasVertex(b)) return -2;
	
	Graph P = new Path(this,a,b);
	if (P.nE() == 0) return -1;
	return P.nE();
    }
	
    /**
     * Find distance between two vertices.
     * @param a a vertex.
     * @param b a vertex.
     * @return The distance from a to b. If no ab path exists, -1 is
     * returned. If either a or b is not in this graph, -2 is
     * returned.
     */
    public final int distance(Vertex a, Vertex b) {
	return distance(a.getName(), b.getName());
    }

    /**
     * Print out lots of information about this graph
     */
    public void print() {
	System.out.println("====================================");
	System.out.println("*** GRAPH PRINTOUT ***");
	System.out.println("nV = " + nV());
	System.out.println("nE = " + nE());
	System.out.println("--- Listing All Vertices ---");
	Iterator it = V.iterator();
	while (it.hasNext()) {
	    Vertex v = (Vertex) it.next();
	    System.out.println("Vertex " + v);
	    System.out.println("Neighborhood: " + neighborhood(v));
	    VertexRecord vr = getVertexRecord(v);
	    vr.print();
	    System.out.println("------------------------------------");
	}
	System.out.println("*** END OF GRAPH ***");
	System.out.println("====================================");
    }
	
    

}



/**
 * A VertexRecord is an extended version of Vertex that we use to
 * store the vertices of this graph. It includes extra information
 * that makes Graph operations more efficient. 
 */

class VertexRecord {
    
    int uDegree;      // number of undirected edges incident
    int outDegree;    // number of arcs leaving
    int inDegree;     // number or arcs entering
    
    GraphElementSet I;    // Edges incident with this vertex

    /**
     * Create a new VertexRecord from a given name
     * @param name the name of this new vertex record.
     */
    VertexRecord() {
	uDegree = 0;
	outDegree = 0;
	inDegree = 0;
	I = new GraphElementSet();
    }


    /**
     * Print out information about this VertexRecord
     */
    void print() {
	System.out.println("Undirected degree: " + uDegree);
	System.out.println("Out degree:        " + outDegree);
	System.out.println("In degree:         " + inDegree);
	System.out.println("Incident edges: " + I);
    }


}
	
