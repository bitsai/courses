import java.util.*;

public class Line
{
	PointComparator PC = new PointComparator();

	double angle; // In radians
	Point endPoint1;
	Point endPoint2;
	LinkedList points = new LinkedList();
	double length;

	public Line(Point point1, Point point2)
	{
		updateEndpoints(point1, point2);

		points.add(endPoint1);
		points.add(endPoint2);

		angle = getAngle(endPoint1, endPoint2);
		length = getDistance(endPoint1, endPoint2);
	}

	public void updateEndpoints(Point point1, Point point2)
	{
		if (PC.compare(point1, point2) == -1)
		{
			endPoint1 = point1;
			endPoint2 = point2;
		}
		else
		{
			endPoint1 = point2;
			endPoint2 = point1;
		}
	}

	public static double getAngle(Point point1, Point point2)
	{
		double deltaY = point2.y - point1.y;
		double deltaX = point2.x - point1.x;

		double result = Math.atan2(deltaY, deltaX);
		return result;
	}

	public static double getDistance(Point point1, Point point2)
	{
		double deltaY = point2.y - point1.y;
		double deltaX = point2.x - point1.x;

		double y2 = Math.pow(deltaY, 2);
		double x2 = Math.pow(deltaX, 2);

		double result = Math.sqrt(x2 + y2);
		return result;
	}

	public void testAgreement(Point point)
	{
		Point point1 = null;
		Point point2 = null;

		// Compare first end point to the point

		if (PC.compare(endPoint1, point) == -1)
		{
			point1 = endPoint1;
			point2 = point;
		}
		else
		{
			point1 = point;
			point2 = endPoint1;
		}

		if (getAngle(point1, point2) == angle) // If the point is on the line...
		{
			if (points.indexOf(point) == -1) // Add the point to the line if it's not already there
			{ points.add(point); }

			// If the point extends the line's length, it becomes one of the new end points

			if (getDistance(endPoint1, point) > length || getDistance(point, endPoint2) > length)
			{
				if (getDistance(endPoint1, point) > getDistance(point, endPoint2))	// Replace endPoint2
				{
					length = getDistance(endPoint1, point);
					updateEndpoints(endPoint1, point);
				}
				else // Replace endPoint1
				{
					length = getDistance(point, endPoint2);
					updateEndpoints(point, endPoint2);
				}
			}
		}
	}

	public String printPoints()
	{
		String output = "";

		for (int count = 0; count < points.size(); count++)
		{
			Point point = (Point) points.get(count);
			output += point + " ";
		}

		return output;
	}

	public String toString()
	{
		return endPoint1 + " " + endPoint2;
	}

	public int hashCode()
	{
		return toString().hashCode();
	}

	public boolean equals(Object other)
	{
		if (hashCode() == other.hashCode())
		{ return true; }
		return false;
	}
}