import java.util.*;

public class Node
{
	String name;
	String type;
	LinkedList values = new LinkedList();
	LinkedList parents = new LinkedList();
	HashMap probs = new HashMap();
	HashMap utils = new HashMap();

	StringTokenizer ST;
	int[] parentValueNums;

	public Node(String name, String type)
	{
		this.name = name;
		this.type = type;
	}

	public void addValues(StringTokenizer ST)
	{
		while (ST.hasMoreTokens()) { values.add(ST.nextToken()); }
	}

	public void addParents(LinkedList parents)
	{
		this.parents = parents;
		parentValueNums = new int[parents.size()];
		for (int count = 0; count < parents.size(); count++) { parentValueNums[count] = 0; }
	}

	public void addProbs(String data)
	{
		ST = new StringTokenizer(data);

		for (int valueNum = 0; valueNum < values.size(); valueNum++)
		{
			String value = (String) values.get(valueNum);
			Double prob = new Double(ST.nextToken());

			// If this node has parents, need to make probability conditional on parents' values

			if (parents.size() > 0)
			{
				value += " |";

				for (int parentNum = 0; parentNum < parents.size(); parentNum++)
				{
					Node parent = (Node) parents.get(parentNum);
					int parentValueNum = parentValueNums[parentNum];
					String parentValue = (String) parent.values.get(parentValueNum);
					value += " " + parentValue;
				}
			}

			probs.put(value, prob);
		}

		// Increment parentValueNums[] so that we fetch the right parent values next time around

		if (parents.size() > 0) { incrementParentValueNums(); } // Do this only if this node has parents
	}

	public void addUtils(String data)
	{
		String value = "";
		Double util = new Double(data);

		for (int parentNum = 0; parentNum < parents.size(); parentNum++)
		{
			Node parent = (Node) parents.get(parentNum);
			int parentValueNum = parentValueNums[parentNum];
			String parentValue = (String) parent.values.get(parentValueNum);
			value += " " + parentValue;
		}

		utils.put(value, util);

		// Increment parentValueNums[] so that we fetch the right parent values next time around

		incrementParentValueNums();
	}

	public void incrementParentValueNums()
	{
		parentValueNums[0]++;

		for (int count = 0; count < parents.size() - 1; count++)
		{
			Node parent = (Node) parents.get(count);

			if (parentValueNums[count] == parent.values.size())
			{
				parentValueNums[count] = 0;
				parentValueNums[count+1]++;
			}
		}
	}

	public double getProb(TreeMap e)
	{
		String evidence = (String) e.get(name);

		if (parents.size() > 0)
		{
			evidence += " |";

			for (int parentNum = 0; parentNum < parents.size(); parentNum++)
			{
				Node parent = (Node) parents.get(parentNum);
				evidence += " " + (String) e.get(parent.name);
			}
		}

		Double prob = (Double) probs.get(evidence);

		return prob.doubleValue();
	}

	public String getBestAction(TreeMap p)
	{
		String bestAction = "";
		double bestUtility = 0;

		// Reset parentValueNums

		for (int count = 0; count < parents.size(); count++) { parentValueNums[count] = 0; }

		// Calculate each action's utility

		for (int i = 0; i < utils.size(); i++)
		{
			String values = "";
			double totalProb = 1;
			String action = "";

			for (int parentNum = 0; parentNum < parents.size(); parentNum++)
			{
				Node parent = (Node) parents.get(parentNum);
				int parentValueNum = parentValueNums[parentNum];
				String parentValue = (String) parent.values.get(parentValueNum);
				values += " " + parentValue;

				if (parent.type.equals("Decision"))
				{
					action = parentValue;
				}
				else
				{
					String key = parent.name + " " + parentValue;
					Double prob = (Double) p.get(key);
					totalProb *= prob.doubleValue();
				}
			}

			Double u = (Double) utils.get(values);
			double utility = u.doubleValue() * totalProb;
			incrementParentValueNums();

			// Compare current action's utility with the best so far

			if (utility > bestUtility)
			{
				bestAction = action;
				bestUtility = utility;
			}
		}

		return bestAction;
	}

	public void print()
	{
		System.out.println();
		System.out.println(name);
		System.out.println(type);

		if (type.equals("Chance")) { System.out.println(probs); }
		if (type.equals("Decision")) { System.out.println(values); }
		if (type.equals("Utility")) { System.out.println(utils); }
	}
}