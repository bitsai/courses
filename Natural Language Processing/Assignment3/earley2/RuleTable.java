package earley2;
import java.util.*;

public class RuleTable
{
	public PrefixTable prefixTable = new PrefixTable();
	public ParentTable parentTable = new ParentTable();
	public AncestorTable ancestorTable = new AncestorTable(this);

	public void addRule(String input)
	{
		if (input.equals(""))
		{
			return;
		}

		Rule rule = new Rule(input);

		String A = rule.LHS;
		String B = rule.afterDot(0);
		String key = A + " " + B;

		if (prefixTable.hasRules(key) == false)
		{
			parentTable.addA(B, A);
		}

		prefixTable.addRule(key, rule);
	}

	public ArrayList getRules(String A)
	{
		ArrayList rules = new ArrayList();

		if (ancestorTable.hasBs(A))
		{
			ListIterator Bs = ancestorTable.getBs(A);

			while (Bs.hasNext())
			{
				String B = (String) Bs.next();
				ListIterator ABrules = prefixTable.getRules(A + " " + B);

				while (ABrules.hasNext())
				{
					Rule ABrule = (Rule) ABrules.next();
					rules.add(ABrule);
				}
			}

			ancestorTable.clear(A);
		}

		return rules;
	}

	public boolean isExpandable(String symbol)
	{
		return ancestorTable.hasBs(symbol);
	}

	public void constructAncestorTable()
	{
		ancestorTable = new AncestorTable(this);
	}

	public void process(String B)
	{
		if (parentTable.hasAs(B))
		{
			ListIterator As = parentTable.getAs(B);

			while (As.hasNext())
			{
				ancestorTable.addB((String) As.next(), B);
			}
		}
	}

	public String toString()
	{
		String output = "";
		output += "\nR(A, B): " + prefixTable.toString();
		output += "\nP(B): " + parentTable.toString();
		output += "\nS(A): " + ancestorTable.toString();
		return output;
	}
}