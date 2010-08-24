package earley2;
import java.util.*;

public class ParentTable
{
	public HashMap As = new HashMap();

	public void addA(String B, String A)
	{
		if (hasAs(B))
		{
			LinkedList AGroup = (LinkedList) As.get(B);
			AGroup.addLast(A);
		}
		else
		{
			LinkedList AGroup = new LinkedList();
			AGroup.addLast(A);
			As.put(B, AGroup);
		}
	}

	public ListIterator getAs(String B)
	{
		if (As.containsKey(B))
		{
			LinkedList AGroup = (LinkedList) As.get(B);
			ListIterator li = AGroup.listIterator();
			return li;
		}
		else
		{
			return null;
		}
	}

	public boolean hasAs(String B)
	{
		return As.containsKey(B);
	}

	public String toString()
	{
		String output = "";
		Iterator keys = As.keySet().iterator();

		while (keys.hasNext())
		{
			String key = (String) keys.next();
			ListIterator li = getAs(key);

			while (li.hasNext())
			{
				String A = (String) li.next();
				output += "\nB: " + key + " A: " + A;
			}
		}

		return output;
	}
}