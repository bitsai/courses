/*
   A vector of grayscale images, each of which is an 2-D array of floats,
   together with a vector of labels, each of which is an integer.
*/

import java.util.*;
import java.io.*;

public class ImageSet {
	public int rows = 16, cols = 16;		// size of each image (hardcoded)
	public Vector images = new Vector();	// holds the images
	public Vector labels = new Vector();	// holds the corresponding labels

	// CONSTRUCTORS

	// read from file
  	public ImageSet() throws IOException, NumberFormatException {
		addFromStream(new InputStreamReader(System.in));
	}

	// read from standard input
	public ImageSet(String filename) throws FileNotFoundException, IOException, NumberFormatException {
		addFromStream(new FileReader(new File(filename)));
	}

	// CONSTRUCTOR-LIKE FUNCTION, convenient for programs that read
	// an ImageSet from the command line.

	public static ImageSet read_cmdline(String[] args) {
		try {
			if (args.length > 1)
				// error
				throw new Exception("Usage: At most one command-line argument.\nImage set can come from a named file or from the standard input.");
			else if (args.length == 1)
				// read from a file
				return new ImageSet(args[0]);
			else
				// read from standard input
				return new ImageSet();
		}
		catch (Exception e) {
			// crash if there's any problem at all
			System.err.println(e.getMessage());
			System.exit(1);
			return null;
		}
	}

	// IMPLEMENTATION

	void addFromStream(InputStreamReader isr) throws IOException, NumberFormatException {
		images = new Vector(10);
		labels = new Vector(10);
		BufferedReader br = new BufferedReader(isr);
		// throw away first line (just gives # images and # pixels/image)
		br.readLine();
		while (br.ready())
			addFromString(br.readLine());
		br.close();
	}

	void addFromString(String s) throws NumberFormatException {
		float[][] image = new float[rows][cols];
		StringTokenizer st = new StringTokenizer(s, " ");

		if (st.countTokens() != rows * cols + 1) {
			System.err.println("ERROR: line has wrong length: " + s);
			System.exit(0);
		}

		for (int row = 0; row < rows; row++) {
			for (int col = 0; col < cols; col++) {
				image[row][col] = Float.parseFloat(st.nextToken());
			}
		}

		images.add(image);
		labels.add(Integer.decode(st.nextToken()));
	}
}
