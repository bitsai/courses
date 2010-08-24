import java.util.*;
import java.io.*;

public class BuildFeatures {
	static ImageSet is;
	static ImageSet ss;
	static java.text.DecimalFormat format = new java.text.DecimalFormat(",##0.0000");

	static public void main(String[] args) {
		try {
			is = ImageSet.read_cmdline(args);
			ss = ImageSet.read_cmdline(new String[] {"standards.txt"});
			writeImages();
		}
		catch (Exception e) {
			System.err.println(e.getMessage());
			System.exit(1);
			return;
		}
	}

	static void writeImages() {
		System.out.println(is.images.size() + " " + (is.rows * is.cols * 2 + 10 + 1));

		for (int i = 0; i < is.images.size(); i++) {
			System.out.print("  ");
			printFeatures((float[][]) is.images.get(i));
			System.out.println(is.labels.get(i));
		}
	}

	static double scale(float value) {
		return (value + 1.0) / 2.0;
	}

	static double blur(double[][] array, int row, int col) {
		double output = 0;
		int num_cells = 0;
		int row_min = row - 1, row_max = row + 1;
		int col_min = col - 1, col_max = col + 1;

		if (row == 0) { row_min = row; }
		if (col == 0) { col_min = col; }
		if (row == is.rows - 1) { row_max = row; }
		if (col == is.cols - 1) { col_max = col; }

		for (int r = row_min; r <= row_max; r++) {
			for (int c = col_min; c <= col_max; c++) {
				output += array[r][c];
				num_cells++;
			}
		}

		output = output / num_cells;

		return output;
	}

	static void printFeatures(float[][] image) {
		double[][] scaled_array = new double[is.rows][is.cols];
		double[][] blurred_array = new double[is.rows][is.cols];
		double[] difference_array = new double[10];

    	// Just print the pixels in the original order.
		for (int row = 0; row < is.rows; row++) {
			for (int col = 0; col < is.cols; col++) {
				System.out.print(format.format(image[row][col]) + " ");
			}
		}

		// Scale greyscale values (between 0.0 and 1.0).
		for (int row = 0; row < is.rows; row++) {
			for (int col = 0; col < is.cols; col++) {
				scaled_array[row][col] = scale(image[row][col]);
			}
		}

		// Blur scaled greyscale values
		for (int row = 0; row < is.rows; row++) {
			for (int col = 0; col < is.cols; col++) {
				blurred_array[row][col] = blur(scaled_array, row, col);
				System.out.print(format.format(blurred_array[row][col]) + " ");
			}
		}

		// Calculate difference between blurred, scaled greyscale values and standard drawings
		for (int i = 0; i < 10; i++) {
			float[][] standard = (float[][]) ss.images.get(i);

			for (int row = 0; row < is.rows; row++) {
				for (int col = 0; col < is.cols; col++) {
					blurred_array[row][col] = blur(scaled_array, row, col);
					double difference = Math.abs(blurred_array[row][col] - standard[row][col]);
					difference_array[i] += difference;
				}
			}
		}

		for (int i = 0; i < 10; i++) {
			System.out.print(format.format(difference_array[i]) + " ");
		}
	}
}
