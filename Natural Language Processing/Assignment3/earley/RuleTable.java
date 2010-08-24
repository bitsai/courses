package earley;
import java.util.*;

public class RuleTable
{
	public HashMap rules = new HashMap();

	public void addRule(String rule)
	{
		if (rule.equals(""))
		{
			return;
		}

		Rule newRule = new Rule(rule);

		if (rules.containsKey(newRule.LHS))
		{
			LinkedList ruleGroup = (LinkedList) rules.get(newRule.LHS);
			ruleGroup.addLast(newRule);
		}
		else
		{
			LinkedList ruleGroup = new LinkedList();
			ruleGroup.addLast(newRule);
			rules.put(newRule.LHS, ruleGroup);
		}
	}

	public ListIterator getRules(String LHS)
	{
		LinkedList ruleGroup = (LinkedList) rules.get(LHS);
		ListIterator li = ruleGroup.listIterator();
		return li;
	}

	public boolean isExpandable(String LHS)
	{
		return rules.containsKey(LHS);
	}
}