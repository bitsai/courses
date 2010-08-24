import java.util.*;
import java.io.*;

public class ListParser
{
	// Global variable holding tokens to be parsed

	static StringTokenizer ST;

	// Variables used to keep track of variable renaming

	static HashMap varRenaming = new HashMap();

	// Code

	public static void main( String args[])
	{
		String input1 = ReadFile("term1.txt");
		LinkedList expression1 = stringToLinkedList(input1);

		String input2 = ReadFile("term2.txt");
		LinkedList expression2 = stringToLinkedList(input2);

		HashMap substitution = new HashMap();
		substitution = Unify(expression1, expression2, substitution);

		if (substitution != null)
		{
			if (substitution.size() > 0) { PrintSubstitution(substitution); }
			else { System.out.println("Yes."); }
		}
		else { System.out.println("No."); }
	}

	static LinkedList tokensToLinkedList()
	{
		LinkedList list = new LinkedList();
		String token = ST.nextToken(); // if none, will throw exception

		// Make a LinkedList out of tokens until ) is seen; then return the LinkedList

		while(!token.matches("[)]"))
		{
			if (token.matches("[(]")) list.add(tokensToLinkedList());
			else
			{
				if (Variable(token))
				{
					String var = "!" + token.substring(1);
					varRenaming.put(var, token);
					list.add(var);
				}
				else
				{
					list.add(token);
				}
			}

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

	static HashMap Unify(Object x, Object y, HashMap substitution)
	{
		if (substitution == null) { return null; }
		else if (x.equals(y)) { return substitution; }
		else if (Variable(x)) { return UnifyVar(x, y, substitution); }
		else if (Variable(y)) { return UnifyVar(y, x, substitution); }
		else if (List(x) && List(y))
		{
			LinkedList listX = (LinkedList) x;
			LinkedList listY = (LinkedList) y;

			Object firstX = listX.removeFirst();
			Object firstY = listY.removeFirst();

			return Unify(listX, listY, Unify(firstX, firstY, substitution));
		}
		else { return null; }
	}

	static HashMap UnifyVar(Object var, Object x, HashMap substitution)
	{
		if (substitution.containsKey(var))
		{
			Object val = substitution.get(var);

			return Unify(val, x, substitution);
		}
		else if (substitution.containsKey(x))
		{
			Object val = substitution.get(x);

			return Unify(var, val, substitution);
		}
		else if (OccurCheck(var, x)) { return null; }
		else
		{
			substitution.put(var, x);

			return substitution;
		}
	}

	static boolean Variable(Object x)
	{
		if (x.getClass().toString().equals("class java.lang.String"))
		{
			String term = (String) x;

			if (term.charAt(0) == '?' || term.charAt(0) == '!') { return true; }
			else { return false; }
		}
		else { return false; }
	}

	static boolean List(Object x)
	{
		if (x.getClass().toString().equals("class java.util.LinkedList")) { return true; }
		else { return false; }
	}

	static boolean OccurCheck(Object var, Object x)
	{
		String varString = var.toString();
		String xString = x.toString();

		if (xString.indexOf(varString) > -1) { return true; }
		else { return false; }
	}

	static void PrintSubstitution(HashMap substitution)
	{
		Iterator subs = substitution.keySet().iterator();

		while (subs.hasNext())
		{
			Object var = subs.next();
			Object original = varRenaming.get(var);
			Object value = ChainSubstitute(var, substitution);
			if (value != null) { System.out.println(original + " -> " + value); }
		}
	}

	static Object ChainSubstitute(Object var, HashMap substitution)
	{
		Object varValue = substitution.get(var);
		String varValueString = varValue.toString();

		Iterator subs = substitution.keySet().iterator();

		while (subs.hasNext())
		{
			Object sub = subs.next();
			Object subValue = substitution.get(sub);

			String subString = sub.toString();
			String subValueString = subValue.toString();

			if (varValueString.indexOf(subString) > -1)
			{ varValueString = varValueString.replaceAll(subString, subValueString); }
		}

		return varValueString;
	}
}