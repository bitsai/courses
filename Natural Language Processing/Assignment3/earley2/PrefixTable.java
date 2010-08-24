package earley2;
import java.util.*;

public class PrefixTable
{
	public HashMap rules = new HashMap();

	public void addRule(String key, Rule rule)
	{
		if (hasRules(key))
		{
			LinkedList ruleGroup = (LinkedList) rules.get(key);
			ruleGroup.addLast(rule);
		}
		else
		{
			LinkedList ruleGroup = new LinkedList();
			ruleGroup.addLast(rule);
			rules.put(key, ruleGroup);
		}
	}

	public ListIterator getRules(String key)
	{
		LinkedList ruleGroup = (LinkedList) rules.get(key);
		ListIterator li = ruleGroup.listIterator();
		return li;
	}

	public boolean hasRules(String key)
	{
		return rules.containsKey(key);
	}

	public String toString()
	{
		String output = "";
		Iterator keys = rules.keySet().iterator();

		while (keys.hasNext())
		{
			String key = (String) keys.next();
			ListIterator li = getRules(key);

			while (li.hasNext())
			{
				Rule rule = (Rule) li.next();
				output += "\nKEY: " + key + " RULE: " + rule.toString();
			}
		}

		return output;
	}
}