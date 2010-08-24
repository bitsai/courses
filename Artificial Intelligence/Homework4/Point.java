import java.util.*;

public class Point
{
	int x;
	int y;

	public Point(String data)
	{
		StringTokenizer ST = new StringTokenizer(data);

		x = Integer.parseInt(ST.nextToken());
		y = Integer.parseInt(ST.nextToken());
	}

	public String toString()
	{
		return x + " " + y;
	}
}