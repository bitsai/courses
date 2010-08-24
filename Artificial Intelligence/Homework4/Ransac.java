import java.io.*;
import java.util.*;

public class Ransac
{
	static LinkedList points = new LinkedList();
	static LinkedList lines = new LinkedList();
	static LinkedList edges = new LinkedList();

	public static void main(String[] args)
	{
		try
		{
			BufferedReader inStream = new BufferedReader(new FileReader("datapts.txt"));
			String inputLine = inStream.readLine();

			while (inputLine != null)
			{
				points.add(new Point(inputLine));
				inputLine = inStream.readLine();
			}

			findLines();

			findEdges();

			findShape();
		}
		catch(Exception e)
		{
			System.out.println(e);
		}
	}

	public static void findLines()
	{
		int N = points.size() * points.size(); // Make N = (n)^2 for now
		Random rand = new Random();

		for (int count = 0; count < N; count++)
		{
			// Pick two random points

			int rand1 = rand.nextInt(points.size());
			int rand2 = rand.nextInt(points.size());

			while (rand2 == rand1) // Don't pick the same point twice
			{ rand2 = rand.nextInt(points.size()); }

			Point point1 = (Point) points.get(rand1);
			Point point2 = (Point) points.get(rand2);

			// Create a line from the two points and test its agreement with the data set

			Line newLine = new Line(point1, point2);
			setAgreement(newLine);

			// Add to line set if it's big enough and it's not already there

			if (newLine.points.size() > 2 && lines.indexOf(newLine) == -1)
			{ lines.add(newLine); }
		}
	}

	public static void setAgreement(Line line)
	{
		// For each point in the data set, test its agreement with the line

		for (int count = 0; count < points.size(); count++)
		{
			Point point = (Point) points.get(count);
			line.testAgreement(point);
		}
	}

	public static void findEdges()
	{
		// Sort the lines according to size

		Object[] lineArray = lines.toArray();
		Arrays.sort(lineArray, new LineComparator());
		int oldSize = lines.size();
		lines = new LinkedList();

		for (int count = oldSize - 1; count >= 0; count--)
		{
			lines.add(lineArray[count]);
		}

		// While there are points left...

		while (points.size() > 0)
		{
			Line line = (Line) lines.remove(0);	// Find the biggest remaining line

			for (int count = 0; count < line.points.size(); count++)	// Remove this line's points from data set
			{
				Point point = (Point) line.points.get(count);

				if (points.contains(point))
				{
					points.remove(point);
				}
			}

			edges.add(line); // Add this line to edge set
		}
	}

	public static void findShape()
	{
		// Choose largest line as our first line

		LinkedList candidates = new LinkedList();
		HashSet endPoints = new HashSet();
		Line candidate = (Line) edges.removeFirst();

		// Add its endpoints as unmatched endpoints

		candidates.add(candidate);
		endPoints.add(candidate.endPoint1.toString());
		endPoints.add(candidate.endPoint2.toString());

		while (endPoints.size() > 0 && edges.size() > 0)	// While there are still unmatched endpoints and edges left...
		{
			for (int count = 0; count < edges.size(); count++)
			{
				candidate = (Line) edges.get(count); // Look at remaining edges, see if it matches any endpoints

				if (endPoints.contains(candidate.endPoint1.toString()) && endPoints.contains(candidate.endPoint2.toString()))
				{
					endPoints.remove(candidate.endPoint1.toString());
					endPoints.remove(candidate.endPoint2.toString());

					candidates.add(candidate);
					edges.remove(candidate);
				}
				else if (endPoints.contains(candidate.endPoint1.toString()))
				{
					endPoints.remove(candidate.endPoint1.toString());
					endPoints.add(candidate.endPoint2.toString());

					candidates.add(candidate);
					edges.remove(candidate);
				}
				else if (endPoints.contains(candidate.endPoint2.toString()))
				{
					endPoints.remove(candidate.endPoint2.toString());
					endPoints.add(candidate.endPoint1.toString());

					candidates.add(candidate);
					edges.remove(candidate);
				}
			}
		}

		if (endPoints.size() > 0) // If there are no more edges left and still unmatched endpoints, we don't have a closed polygon
		{
			System.out.println("No recognizable shapes found");
			return;
		}

		if (candidates.size() == 3) // 3 sides make a triangle
		{ System.out.println("Triangle"); }
		else
		{
			boolean square = true;
			candidate = (Line) candidates.removeFirst();
			double firstLength = candidate.length;

			while (candidates.size() > 0) // See if all sides have same length
			{
				candidate = (Line) candidates.removeFirst();

				if (candidate.length != firstLength)
				{ square = false; }
			}

			if (square)
			{ System.out.println("Square"); }
			else
			{ System.out.println("Rectangle"); }
		}
	}
}