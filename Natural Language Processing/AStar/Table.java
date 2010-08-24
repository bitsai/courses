import java.util.*;

public class Table
{
	HashMap table = new HashMap();

	public double getLogProb(String type, String x)
	{
		String key = type + " " + x;

		if (!table.containsKey(key)) { return Math.log(0); }
		return ((Double) table.get(key)).doubleValue();
	}

	public void putLogProb(String type, String x, double value)
	{
		String key = type + " " + x;

		table.put(key, new Double(value));
	}

	public double getCount(String type, String x)
	{
		String key = type + " " + x;

		if (!table.containsKey(key)) { return 0; }
		return ((Double) table.get(key)).doubleValue();
	}

	public void putCount(String type, String x, double value)
	{
		String key = type + " " + x;

		table.put(key, new Double(value));
	}

	public void increment(String type, String x)
	{
		double newCount = getCount(type, x) + 1;

		putCount(type, x, newCount);
	}

	public void decrement(String type, String x)
	{
		double newCount = getCount(type, x) - 1;

		putCount(type, x, newCount);
	}

	public String getString(String key)
	{
		return (String) table.get(key);
	}

	public void putString(String key, String string)
	{
		table.put(key, string);
	}
}
