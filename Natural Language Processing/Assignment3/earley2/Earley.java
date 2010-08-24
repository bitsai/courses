package earley2;
import java.util.*;
import java.io.*;

public class Earley
{
	public static RuleTable ruleTable = new RuleTable();
	public static EntryTable entryTable;

	public static FileReader fr;
	public static BufferedReader br;

	public static void main(String[] args)
	{
		try
		{
			String input;
			FileWriter fw = new FileWriter("OUTPUT");

			String grammar = args[0];
			String sentences = args[1];

			fr = new FileReader(grammar);
			br = new BufferedReader(fr);

			while ((input = br.readLine()) != null)
			{
				ruleTable.addRule(input);
			}

			fr = new FileReader(sentences);
			br = new BufferedReader(fr);

			while ((input = br.readLine()) != null)
			{
				entryTable = new EntryTable(ruleTable);
				Runtime.getRuntime().gc();
				String output = entryTable.processSentence(input);
				fw.write(output);
			}

			fw.close();
		}
		catch (Exception e)
		{
			System.out.println(e);
		}
	}
}