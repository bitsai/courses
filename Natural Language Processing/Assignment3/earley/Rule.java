package earley;
import java.util.*;

public class Rule
{
	public float weight;
	public String LHS;
	public LinkedList RHS = new LinkedList();

	public Rule(String rule)
	{
		StringTokenizer st = new StringTokenizer(rule);
		weight = Float.valueOf(st.nextToken()).floatValue();
		LHS = st.nextToken();

		while(st.hasMoreTokens())
		{
			RHS.addLast(st.nextToken());
		}
	}

	public String afterDot(int dotPosition)
	{
		if (dotPosition < RHS.size())
		{
			return (String) RHS.get(dotPosition);
		}
		else
		{
			return "NONE";
		}
	}

	public String toString()
	{
		String output = "";
		output += LHS + " -> ";
		ListIterator li = RHS.listIterator();

		while(li.hasNext())
		{
			output += " " + li.next();
		}

		return output;
	}
}