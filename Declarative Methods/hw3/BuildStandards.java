import java.util.*;
import java.io.*;

public class BuildStandards {
	static ImageSet is;
	static java.text.DecimalFormat format = new java.text.DecimalFormat(",##0.0000");
	static Vector standards = new Vector();
	static Vector counts = new Vector();

	static public void main(String[] args) {
		is = ImageSet.read_cmdline(args);
		buildStandards();
		writeStandards();
	}

	static void buildStandards() {
		double[][] scaled_array = new double[is.rows][is.cols];
		double[][] blurred_array = new double[is.rows][is.cols];

		// Initialize standard greyscale values and counts
		for (int number = 0; number < 10; number++) {
			float[][] standard = new float[is.rows][is.cols];
			standards.add(standard);
			counts.add(new Integer(0));
		}

		// Build blurred, scaled greyscale value sums, and count examples in each class
		for (int i = 0; i < is.images.size(); i++) {
			float[][] image = (float[][]) is.images.get(i);
			int number = ((Integer) is.labels.get(i)).intValue();
			float[][] standard = (float[][]) standards.get(number);

			// Scale
			for (int row = 0; row < is.rows; row++) {
				for (int col = 0; col < is.cols; col++) {
					scaled_array[row][col] = scale(image[row][col]);
				}
			}

			// Blur
			for (int row = 0; row < is.rows; row++) {
				for (int col = 0; col < is.cols; col++) {
					blurred_array[row][col] = blur(scaled_array, row, col);
				}
			}

			// Sum
			for (int row = 0; row < is.rows; row++) {
				for (int col = 0; col < is.cols; col++) {
					standard[row][col] += blurred_array[row][col];
				}
			}

			// Count
			int old_count = ((Integer) counts.get(number)).intValue();
			Integer new_count = new Integer(old_count + 1);
			counts.insertElementAt(new_count, number);
		}

		// Average out greyscale value sums for each class
		for (int number = 0; number < 10; number++) {
			float[][] standard = (float[][]) standards.get(number);
			int count = ((Integer) counts.get(number)).intValue();

			for (int row = 0; row < is.rows; row++) {
				for (int col = 0; col < is.cols; col++) {
					standard[row][col] = standard[row][col] / count;
				}
			}
		}
	}

	static void writeStandards() {
		System.out.println(standards.size() + " " + (is.rows * is.cols + 1));

		for (int i = 0; i < standards.size(); i++) {
			float[][] standard = (float[][]) standards.get(i);
			System.out.print("  ");

			for (int row = 0; row < is.rows; row++) {
				for (int col = 0; col < is.cols; col++) {
					System.out.print(format.format(standard[row][col]) + " ");
				}
			}

			System.out.println(i);
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
}
