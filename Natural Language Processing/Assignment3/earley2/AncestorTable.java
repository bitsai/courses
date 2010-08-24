package earley2;
import java.util.*;

public class AncestorTable
{
	public RuleTable ruleTable;
	public HashMap Bs = new HashMap();

	public AncestorTable(RuleTable ruleTable)
	{
		this.ruleTable = ruleTable;
	}

	public void addB(String A, String B)
	{
		if (hasBs(A))
		{
			LinkedList BGroup = (LinkedList) Bs.get(A);
			BGroup.add(B);
		}
		else
		{
			LinkedList BGroup = new LinkedList();
			BGroup.add(B);
			Bs.put(A, BGroup);

			ruleTable.process(A);
		}
	}

	public ListIterator getBs(String A)
	{
		LinkedList BGroup = (LinkedList) Bs.get(A);
		ListIterator li = BGroup.listIterator();
		return li;
	}

	public boolean hasBs(String A)
	{
		return Bs.containsKey(A);
	}

	public void clear(String A)
	{
		Bs.remove(A);
	}

	public void clear()
	{
		Bs.clear();
	}

	public String toString()
	{
		String output = "";
		Iterator keys = Bs.keySet().iterator();

		while (keys.hasNext())
		{
			String key = (String) keys.next();
			ListIterator li = getBs(key);

			while (li.hasNext())
			{
				String B = (String) li.next();
				output += "\nA: " + key + " B: " + B;
			}
		}

		return output;
	}
}