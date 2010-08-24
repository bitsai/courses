import java.util.*;

public class PointComparator implements Comparator
{
	public int compare(Object o1, Object o2)
	{
		Point point1 = (Point) o1;
		Point point2 = (Point) o2;

		if (point1.x == point2.x && point1.y == point2.y)
		{ return 0; }
		else if (point1.x < point2.x)
		{ return -1; }
		else if (point1.x == point2.x && point1.y < point2.y)
		{ return -1; }
		return 1;
	}
}
