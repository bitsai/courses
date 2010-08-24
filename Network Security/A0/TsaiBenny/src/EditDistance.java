package utility;

/**
 * Class used to compute the Levenshtein (edit) distance betwen two
 * strings.
 *
 * This code was found at
 * <br>
 * <code>http://www.merriampark.com/ld.htm</code>
 * <p>
 * Lightly modified by Ed Scheinerman.
 * @author Michael Gilleland
 */



public class EditDistance {

    //****************************
    // Get minimum of three values
    //****************************
    
    private static int Minimum (int a, int b, int c) {
	int mi;
	
	mi = a;
	if (b < mi) {
	    mi = b;
	}
	if (c < mi) {
	    mi = c;
	}
	return mi;
	
    }
    
    //*****************************
    // Compute Levenshtein distance
    //*****************************

    /**
     * Compute the Levenshtein distance between two strings.
     * @param s first string
     * @param t second string
     * @return the edit distance between the strings
     */
    
    public static int dist(String s, String t) {
	int d[][]; // matrix
	int n; // length of s
	int m; // length of t
	int i; // iterates through s
	int j; // iterates through t
	char s_i; // ith character of s
	char t_j; // jth character of t
	int cost; // cost
	
	// Step 1
	
	n = s.length ();
	m = t.length ();
	if (n == 0) {
	    return m;
	}
	if (m == 0) {
	    return n;
	}
	d = new int[n+1][m+1];
	
	// Step 2
	
	for (i = 0; i <= n; i++) {
	    d[i][0] = i;
	}
	
	for (j = 0; j <= m; j++) {
	    d[0][j] = j;
	}
	
	// Step 3
	
	for (i = 1; i <= n; i++) {
	    
	    s_i = s.charAt (i - 1);
	    
	    // Step 4
	    
	    for (j = 1; j <= m; j++) {
		
		t_j = t.charAt (j - 1);
		
		// Step 5
		
		if (s_i == t_j) {
		    cost = 0;
		}
		else {
		    cost = 1;
		}
		
		// Step 6

		d[i][j] = Minimum (d[i-1][j]+1, d[i][j-1]+1, d[i-1][j-1] + cost);

	    }
	    
	}
	
	// Step 7
	
	return d[n][m];
	
    }


    /**
     * Main for demonstration purposes.
     * <p>
     * Usage: <code> java utility.EditDistance <i>str1 str2</i>
     */

    public static void main(String[] args) {
	if (args.length < 2) {
	    System.err.println("Usage: java utility.EditDistance str1 str2");
	    System.exit(1);
	}
	String st1 = args[0].toUpperCase();
	String st2 = args[1].toUpperCase();
	System.out.println(
			   "The distance between " + st1 +
			   " and " + st2 + 
			   " is " + EditDistance.dist(st1,st2)
			   );
    }
			   
    
}
