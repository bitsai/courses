import java.util.*;

public class LineComparator implements Comparator
{
	public int compare(Object o1, Object o2)
	{
		Line line1 = (Line) o1;
		Line line2 = (Line) o2;

		if (line1.points.size() == line2.points.size())
		{ return 0; }
		else if (line1.points.size() < line2.points.size())
		{ return -1; }
		return 1;
	}
}
