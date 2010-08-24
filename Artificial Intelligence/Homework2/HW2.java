import java.util.*;
import java.io.*;

public class HW2
{
	// Global variable holding tokens to be parsed

	static StringTokenizer ST;

	static LinkedList literals = new LinkedList();
	static LinkedList models = new LinkedList();
	static TreeMap modelMapping;

	public static void main( String args[])
	{
		String input = ReadFile("testinput.txt");
		LinkedList expression = stringToLinkedList(input);

		enumerateModels();
		testExpression(input);
	}

	static LinkedList tokensToLinkedList()
	{
		LinkedList list = new LinkedList();
		String token = ST.nextToken(); // if none, will throw exception

		// Make a LinkedList out of tokens until ) is seen; then return the LinkedList

		while(!token.matches("[)]"))
		{
			if (token.matches("[(]")) list.add(tokensToLinkedList());
			else list.add(token);

			// See if this token is in fact a literal

			addLiteral(token);

			token = ST.nextToken();
		}

		return list;
	}

	static LinkedList stringToLinkedList(String input)
	{

		// Add spaces around the parens (could also do this around other ops)

		String line = expandParens(input);

		// Tokenize it

		ST = new StringTokenizer(line);

		// Pop the first left paren off .. since that is what the recursive call assumes

		ST.nextToken();

		// Make a LinkedList of the rest

		return tokensToLinkedList();
	}

	// Just adds blanks on each side of parents so that tokenizing works

	static String expandParens(String input)
	{
		String output = input;
		output = output.replaceAll("[(]"," ( ");
		output = output.replaceAll("[)]"," ) ");

		return output;
	}

	// Read the entire file into a single string

	static String ReadFile(String fn)
	{
		String bigLine = "";

		try
		{
			BufferedReader inStream = new BufferedReader(new FileReader(fn));
			String newLine = inStream.readLine();

			while (newLine != null)
			{
				if (newLine.length() > 0) bigLine = bigLine + " " + newLine;
				newLine = inStream.readLine();
			}

			bigLine = " ( " + bigLine + " ) ";
		}
		catch (Exception e)
		{
			System.out.println("Error");
			System.out.println(e.getMessage());
		}

		return bigLine;
	}

	// My own code

	static void addLiteral(String token)
	{
		if (token.matches("[(~^v]") || token.equals("->") || token.equals("<->")) return;
		if (literals.contains(token)) return;
		literals.add(token);
	}

	static boolean evaluate(LinkedList expression)
	{
		Object first = expression.removeFirst();

		if (first.getClass().toString().equals("class java.util.LinkedList"))
		{
			return evaluate((LinkedList) first);
		}

		if (first.getClass().toString().equals("class java.lang.String"))
		{
			if (first.equals("~")) return evaluateNOT(expression);
			if (first.equals("^")) return evaluateAND(expression);
			if (first.equals("v")) return evaluateOR(expression);
			if (first.equals("->")) return evaluateIF(expression);
			if (first.equals("<->")) return evaluateIFF(expression);

			if (first.equals("TRUE")) return true;
			if (first.equals("FALSE")) return false;

			String value = (String) modelMapping.get(first);
			if (value.equals("TRUE")) return true;
			if (value.equals("FALSE")) return false;
		}

		return true;
	}

	static boolean evaluateNOT(LinkedList argument)
	{
		return !evaluate(argument);
	}

	static boolean evaluateAND(LinkedList arguments)
	{
		while (arguments.size() > 0)
		{
			if (evaluate(arguments) == false) return false;
		}

		return true;
	}

	static boolean evaluateOR(LinkedList arguments)
	{
		while (arguments.size() > 0)
		{
			if (evaluate(arguments) == true) return true;
		}

		return false;
	}

	static boolean evaluateIF(LinkedList arguments)
	{
		boolean antecedent = evaluate(arguments);
		boolean consequent = evaluate(arguments);

		if (antecedent == true && consequent == false) return false;
		return true;
	}

	static boolean evaluateIFF(LinkedList arguments)
	{
		boolean precedent = evaluate(arguments);

		while (arguments.size() > 0)
		{
			if (evaluate(arguments) != precedent) return false;
		}

		return true;
	}

	static void enumerateModels()
	{
		Random rand = new Random();
		int numModels = (int) Math.pow(2, literals.size());

		while (models.size() < numModels)
		{
			LinkedList model = new LinkedList();

			for (int literalNumber = 0; literalNumber < literals.size(); literalNumber++)
			{
				int value = rand.nextInt(2);

				if (value == 0) model.add("TRUE");
				else model.add("FALSE");
			}

			if (!models.contains(model))
			{
				models.add(model);
			}
		}
	}

	static void testExpression(String input)
	{
		int satisfyingModels = 0;

		for (int modelNumber = 0; modelNumber < models.size(); modelNumber++)
		{
			modelMapping = new TreeMap();
			LinkedList model = (LinkedList) models.get(modelNumber);

			for (int literalNumber = 0; literalNumber < literals.size(); literalNumber++)
			{
				String literal = (String) literals.get(literalNumber);
				String value = (String) model.get(literalNumber);

				modelMapping.put(literal, value);
			}

			LinkedList expression = stringToLinkedList(input);

			if (evaluate(expression) == true)
			{
				printModelMapping();
				satisfyingModels++;
			}
		}

		if (satisfyingModels == 0) System.out.println("unsatisfiable!");
		if (satisfyingModels == models.size()) System.out.println("tautological!");
	}

	static void printModelMapping()
	{
		String output = "";

		Set keys = modelMapping.keySet();
		Iterator keysIterator = keys.iterator();

		while(keysIterator.hasNext())
		{
			String key = (String) keysIterator.next();
			String value = (String) modelMapping.get(key);

			if (!output.equals("")) { output += ", "; }

			if (value.equals("TRUE")) { output += key; }
			else {output += "~ " + key; }
		}

		System.out.println(output);
	}
}
