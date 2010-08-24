import java.io.*;
import java.util.*;

public class BNet
{
	static TreeMap variables = new TreeMap();
	static TreeMap evidence = new TreeMap();

	static Node decision;
	static Node utility;

	static StringTokenizer ST;
	static Node currentNode;

	public static void main(String[] args)
	{
		String mode = "default";

		try
		{
			BufferedReader inStream = new BufferedReader(new FileReader("BNet.txt"));
			String inputLine = inStream.readLine();

			while (inputLine != null)
			{
				if (inputLine.length() > 0 && inputLine.charAt(0) == '<')
				{
					if (inputLine.equals("< evidence >")) { evidence = new TreeMap(); } // Clear previous evidence
					mode = inputLine;
				}
				else if (inputLine.equals("")) {} // Do nothing for empty lines
				else
				{
					if (mode.equals("< nodelist >")) { nodelist(inputLine); }
					if (mode.equals("< graph >")) { graph(inputLine); }
					if (mode.equals("< decision >")) { decision(inputLine); }
					if (mode.equals("< utility >")) { utility(inputLine); }
					if (mode.equals("< evidence >")) { evidence(inputLine); }
					if (mode.equals("< query >")) { query(inputLine); }
				}

				inputLine = inStream.readLine();
			}
		}
		catch(Exception e) { System.out.println(e); }

//		test();
	}

	public static void nodelist(String data)
	{
		ST = new StringTokenizer(data);
		Node node = new Node(ST.nextToken(), "Chance");
		node.addValues(ST);

		variables.put(node.name, node);
	}

	public static void graph(String data)
	{
		if (data.matches("[a-zA-Z].*")) // Node parents info
		{
			ST = new StringTokenizer(data);
			Node node = (Node) variables.get(ST.nextToken());

			addParents(node, ST);
			currentNode = node;
		}
		else { currentNode.addProbs(data); } // Node probs info
	}

	public static void decision(String data)
	{
		ST = new StringTokenizer(data);
		decision = new Node(ST.nextToken(), "Decision");
		decision.addValues(ST);
	}

	public static void utility(String data)
	{
		if (data.matches("[a-zA-Z].*")) // Node parent info
		{
			ST = new StringTokenizer(data);
			utility = new Node(ST.nextToken(), "Utility");

			addParents(utility, ST);
			currentNode = utility;
		}
		else { currentNode.addUtils(data); } // Node utility info
	}

	public static void evidence(String data)
	{
		ST = new StringTokenizer(data);
		evidence.put(ST.nextToken(), ST.nextToken());
	}

	public static void query(String data)
	{
		if (variables.containsKey(data)) { System.out.println(enumerationAsk(data)); }
		else if (decision.name.equals(data))
		{
			TreeMap parentProbs = getParentProbs(utility);
			String bestAction = utility.getBestAction(parentProbs);
			System.out.println("Best Action: " + bestAction);
		}
	}

	public static void addParents(Node node, StringTokenizer ST)
	{
		LinkedList parents = new LinkedList();

		while (ST.hasMoreTokens())
		{
			String parentName = ST.nextToken();
			if (variables.containsKey(parentName)) { parents.add((Node) variables.get(parentName)); }
			else if (decision.name.equals(parentName)) { parents.add(decision); }
		}

		node.addParents(parents);
	}

	public static TreeMap getParentProbs(Node node)
	{
		TreeMap probs = new TreeMap();

		for (int i = 0; i < node.parents.size(); i++)
		{
			Node parent = (Node) node.parents.get(i);

			if (variables.containsKey(parent.name))
			{
				TreeMap parentProbs = enumerationAsk(parent.name);

				while (parentProbs.size() > 0)
				{
					String firstKey = (String) parentProbs.firstKey();
					Double firstProb = (Double) parentProbs.remove(firstKey);
					probs.put(parent.name + " " + firstKey, firstProb);
				}
			}
		}

		return probs;
	}

	public static TreeMap enumerationAsk(String queryName)
	{
		Node X = (Node) variables.get(queryName);

		TreeMap Q = new TreeMap();

		for (int i = 0; i < X.values.size(); i++)
		{
			// extend e with value xi for X

			String xi = (String) X.values.get(i);
			evidence.put(queryName, xi);

			// Q(xi) <- Enumerate-All(Vars[bn], e)

			Q.put(xi, new Double(enumerateAll(variables, evidence)));
		}

		return normalize(Q);
	}

	public static double enumerateAll(TreeMap vars, TreeMap e)
	{
		if (vars.size() == 0) { return (double) 1.0; }

		// Y <- First(vars)

		String firstKey = (String) vars.firstKey();
		Node Y = (Node) vars.get(firstKey);

		TreeMap rest = new TreeMap(vars.tailMap(firstKey));
		rest.remove(firstKey); // tailMap() is inclusive, this gets rid of the first element

		if (evidence.containsKey(Y.name)) { return (double) Y.getProb(e) * enumerateAll(rest, e); }

		// Else...

		double sum = (double) 0;
		TreeMap ey = new TreeMap(e);

		for (int i = 0; i < Y.values.size(); i++)
		{
			// extend e with value yi for Y

			String yi = (String) Y.values.get(i);
			ey.put(Y.name, yi);

			sum += Y.getProb(ey) * enumerateAll(rest, ey);
		}

		return sum;
	}

	public static TreeMap normalize(TreeMap Q)
	{
		TreeMap temp = new TreeMap();
		double total = 0;

		while (Q.size() > 0)
		{
			String firstKey = (String) Q.firstKey();
			Double firstValue = (Double) Q.remove(firstKey);
			total += firstValue.doubleValue();
			temp.put(firstKey, firstValue);
		}

		while (temp.size() > 0)
		{
			String firstKey = (String) temp.firstKey();
			Double firstValue = (Double) temp.remove(firstKey);
			firstValue = new Double(firstValue.doubleValue() / total);
			Q.put(firstKey, firstValue);
		}

		return Q;
	}

	public static void test()
	{
		System.out.println("\nYeehaw!\n");
		evidence("B true");
		query("C");
	}
}