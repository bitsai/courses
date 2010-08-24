import java.util.*;

public class Table
{
	public HashMap table = new HashMap();

	public Table() {}

	public Table(Table otherTable)
	{
		table.clear();
		table.putAll(otherTable.table);
	}

	public void copy(Table otherTable)
	{
		table.clear();
		table.putAll(otherTable.table);
	}

	public double getLogProb(String key)
	{
		if (!table.containsKey(key)) { return Math.log(0); }
		return ((Double) table.get(key)).doubleValue();
	}

	public void putLogProb(String key, double value)
	{
		table.put(key, new Double(value));
	}

	public double getCount(String key)
	{
		if (!table.containsKey(key)) { return 0; }
		return ((Double) table.get(key)).doubleValue();
	}

	public void putCount(String key, double value)
	{
		table.put(key, new Double(value));
	}

	public void increment(String key)
	{
		if (!table.containsKey(key)) { table.put(key, new Double(1)); }
		else { putCount(key, getCount(key) + 1); }
	}

	public void decrement(String key)
	{
		putCount(key, getCount(key) - 1);
	}

	public String getString(String key)
	{
		return (String) table.get(key);
	}

	public void putString(String key, String string)
	{
		table.put(key, string);
	}

	public ArrayList getTagDictionary(String key)
	{
		if (!table.containsKey(key)) { return (ArrayList) table.get("TagDictionary OOV"); }
		return (ArrayList) table.get(key);
	}

	public void putTagDictionary(String key, String tag)
	{
		if (!table.containsKey(key))
		{
			ArrayList tags = new ArrayList();
			tags.add(tag);
			table.put(key, tags);
		}
		else
		{
			ArrayList tags = getTagDictionary(key);
			int index = tags.indexOf(tag);
			if (index == -1) { tags.add(tag); }
		}
	}
}
