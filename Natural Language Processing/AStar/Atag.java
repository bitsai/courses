import java.io.*;
import java.text.*;
import java.util.*;

public class Atag
{
	static TagDictionary tagDictionary = new TagDictionary();
	static Table singTable = new Table();
	static Table countTable = new Table();
	static Table probTable = new Table();

	static ArrayList vocabulary = new ArrayList();
	static ArrayList outsideProbs = new ArrayList();

	static double novelCorrect = 0;
	static double novelIncorrect = 0;
	static double knownCorrect = 0;
	static double knownIncorrect = 0;

	static int totalLength = 0;
	static double totalProb = 0;
	static int tokens = 0;
	static int expanded = 0;

	public static void main(String[] args)
	{
		try
		{
			String trainingData = args[0];
			String testData = args[1];

			FileReader fr = new FileReader(trainingData);
			BufferedReader br = new BufferedReader(fr);

			String line;
			ArrayList words = new ArrayList();
			ArrayList tags = new ArrayList();

			while ((line = br.readLine()) != null)
			{
				int split = line.indexOf("/");
				words.add(line.substring(0, split));
				tags.add(line.substring(split + 1));
			}

			processTrainingData(words, tags);
			calculateProbs();

			fr = new FileReader(testData);
			br = new BufferedReader(fr);

			words = new ArrayList();
			tags = new ArrayList();

			while ((line = br.readLine()) != null)
			{
				int split = line.indexOf("/");
				words.add(line.substring(0, split));
				tags.add(line.substring(split + 1));

				if (line.equals("###/###"))
				{
					if (words.size() > 1 && tags.size() > 1)
					{
						words.add(0, "###");
						tags.add(0, "###");

						outsideProbs = new ArrayList();
						calculateOutsideProbs(words);

//						LinkedList MyTags = Vtag(words);

						TreeMap fringe = new TreeMap();
						LinkedList MyTags = TreeSearch(words, fringe);

//						System.out.println(MyTags);
						evaluatePerformance(words, tags, MyTags);
					}

					words = new ArrayList();
					tags = new ArrayList();
				}
			}

			printPerformance();
		}
		catch(Exception e) { System.out.println(e); }
	}

	public static void processTrainingData(ArrayList words, ArrayList tags)
	{
		for (int position = 1; position < words.size(); position++)
		{
			String previousWord = (String) words.get(position - 1);
			String previousTag = (String) tags.get(position - 1);

			String currentWord = (String) words.get(position);
			String currentTag = (String) tags.get(position);

			tagDictionary.addTag(currentWord, currentTag);
			if (!currentTag.equals("###")) { tagDictionary.addTag("OOV", currentTag); }

			if (countTable.getCount("Word", currentWord) == 0) { vocabulary.add(currentWord); }
			tokens++;

			countTable.increment("TT", previousTag + " " + currentTag);
			countTable.increment("TW", currentTag + " " + currentWord);

			if (countTable.getCount("TT", previousTag + " " + currentTag) == 1) { singTable.increment("TT", previousTag); }
			if (countTable.getCount("TT", previousTag + " " + currentTag) == 2) { singTable.decrement("TT", previousTag); }
			if (countTable.getCount("TW", currentTag + " " + currentWord) == 1) { singTable.increment("TW", currentTag); }
			if (countTable.getCount("TW", currentTag + " " + currentWord) == 2) { singTable.decrement("TW", currentTag); }

			countTable.increment("Tag", currentTag);
			countTable.increment("Word", currentWord);
		}
	}

	public static void calculateProbs()
	{
		ArrayList tags = (ArrayList) tagDictionary.getTags("OOV").clone();
		tags.add("###");

		for (int currentTagNumber = 0; currentTagNumber < tags.size(); currentTagNumber++)
		{
			String currentTag = (String) tags.get(currentTagNumber);

			for (int previousTagNumber = 0; previousTagNumber < tags.size(); previousTagNumber++)
			{
				String previousTag = (String) tags.get(previousTagNumber);
				double probTT = getLogProbTT(previousTag, currentTag);

				double bestProbTT = probTable.getLogProb("BestProbTT", currentTag);
				if (probTT > bestProbTT) { probTable.putLogProb("BestProbTT", currentTag, probTT); }
			}
		}

		ArrayList words = (ArrayList) vocabulary.clone();
		words.add("OOV");

		for (int currentWordNumber = 0; currentWordNumber < words.size(); currentWordNumber++)
		{
			String word = (String) words.get(currentWordNumber);

			for (int currentTagNumber = 0; currentTagNumber < tags.size(); currentTagNumber++)
			{
				String tag = (String) tags.get(currentTagNumber);
				double bestProbTT = probTable.getLogProb("BestProbTT", tag);

				double probTW = getLogProbTW(tag, word);
				double bestProbWord = probTable.getLogProb("BestProbWord", word);

				if (probTW + bestProbTT > bestProbWord)
				{ probTable.putLogProb("BestProbWord", word, probTW + bestProbTT); }
			}
		}
	}

	public static void calculateOutsideProbs(ArrayList words)
	{
		for (int currentWordNumber = words.size() - 1; currentWordNumber > 0; currentWordNumber--)
		{
			String word = (String) words.get(currentWordNumber);
			if (countTable.getCount("Word", word) == 0) { word = "OOV"; }
			double bestProbWord = probTable.getLogProb("BestProbWord", word);

			if (currentWordNumber < words.size() - 1)
			{
				double previousSum = getOutsideProb(0);
				bestProbWord += previousSum;
			}

			outsideProbs.add(0, new Double(bestProbWord));
		}
	}

	public static double getOutsideProb(int position)
	{
//		Double outsideProb = (Double) outsideProbs.get(position);
//		double result = outsideProb.doubleValue();
//		return result;

		return 0;
	}

	public static LinkedList TreeSearch(ArrayList words, TreeMap fringe)
	{
		Insert(fringe, MakeNode("###", Math.log(1), getOutsideProb(0)));

		while (1 == 1)
		{
			if (fringe.size() == 0)
			{ return null; }

			Node node = RemoveFirst(fringe);

			if (GoalTest(words, node))
			{ return Solution(node); }

			InsertAll(Expand(node, words), fringe);
		}
	}

	public static Node MakeNode(String tag, double insideProb, double outsideProb)
	{
		LinkedList tags = new LinkedList();
		tags.addFirst(tag);
		Node newNode = new Node(tags, insideProb, outsideProb);
		return newNode;
	}

	public static void Insert(TreeMap fringe, Node node)
	{
		double totalProb = node.totalProb();
		Double key = new Double(totalProb);

		if (fringe.containsKey(key))
		{ key = new Double(totalProb - Math.pow(1, -10)); }

		fringe.put(key, node);
	}

	public static Node RemoveFirst(TreeMap fringe)
	{
		Object key = fringe.lastKey();
		Node node = (Node) fringe.remove(key);
		return node;
	}

	public static boolean GoalTest(ArrayList words, Node node)
	{
		if (words.size() == node.position())
		{ return true; }

		return false;
	}

	public static LinkedList Solution(Node node)
	{
		totalLength += node.position() - 1;
		totalProb += node.totalProb();
		return node.tags();
	}

	public static void InsertAll(ArrayList nodes, TreeMap fringe)
	{
		while (nodes.size() > 0)
		{
			Node node = (Node) nodes.remove(0);
			Insert(fringe, node);
		}
	}

	public static ArrayList Expand(Node node, ArrayList words)
	{
		expanded++;

		ArrayList successors = new ArrayList();
		int currentPosition = node.position();
		String currentWord = (String) words.get(currentPosition);
		String previousTag = node.lastTag();
		ArrayList possibleTags = tagDictionary.getTags(currentWord);

		for (int index = 0; index < possibleTags.size(); index++)
		{
			String possibleTag = (String) possibleTags.get(index);

			double logProbTT = getLogProbTT(previousTag, possibleTag);
			double logProbTW = getLogProbTW(possibleTag, currentWord);
			double newInsideProb = node.insideProb() + logProbTT + logProbTW;
			double newOutsideProb = getOutsideProb(currentPosition - 1);
			Node newNode = node.extend(possibleTag, newInsideProb, newOutsideProb);

			successors.add(0, newNode);
		}

		return successors;
	}

	public static LinkedList Vtag(ArrayList words)
	{
		Table tagTable = new Table();
		LinkedList bestTagSequence = new LinkedList();
		tagTable.putLogProb("BestPathProb", "0 ###", Math.log(1));

		for (int position = 1; position < words.size(); position++)
		{
			String previousWord = (String) words.get(position - 1);
			String currentWord = (String) words.get(position);

			ArrayList previousTags = tagDictionary.getTags(previousWord);
			ArrayList currentTags = tagDictionary.getTags(currentWord);

			for (int currentTagNumber = 0; currentTagNumber < currentTags.size(); currentTagNumber++)
			{
				String currentTag = (String) currentTags.get(currentTagNumber);

				for (int previousTagNumber = 0; previousTagNumber < previousTags.size(); previousTagNumber++)
				{
					String previousTag = (String) previousTags.get(previousTagNumber);
					double logProb = getLogProbTT(previousTag, currentTag) + getLogProbTW(currentTag, currentWord);
					double logBestPathProb = tagTable.getLogProb("BestPathProb", (position - 1) + " " + previousTag) + logProb;

					if (logBestPathProb > tagTable.getLogProb("BestPathProb", position + " " + currentTag))
					{
						tagTable.putLogProb("BestPathProb", position + " " + currentTag, logBestPathProb);
						tagTable.putString("Backpointer " + position + " " + currentTag, previousTag);
					}
				}
			}
		}

		for (int index = 0; index < words.size(); index++) { bestTagSequence.add("###"); }

		for (int index = words.size() - 1; index > 0; index--)
		{
			String currentTag = (String) bestTagSequence.get(index);
			String previousTag = tagTable.getString("Backpointer " + index + " " + currentTag);
			bestTagSequence.set(index - 1, previousTag);
		}

		totalLength += words.size() - 1;
		totalProb += tagTable.getLogProb("BestPathProb", (words.size() - 1) + " ###");

		return bestTagSequence;
	}

	public static double getProbBackoffTT(String previousTag, String currentTag)
	{
		double count = countTable.getCount("Tag", currentTag);
		double n = tokens;
		double prob = count / n;
		return prob;
	}

	public static double getProbBackoffTW(String tag, String word)
	{
		int N = vocabulary.size();

		double count = countTable.getCount("Word", word);
		double n = tokens;
		double prob = (count + 1) / (n + N + 1);
		return prob;
	}

	public static double getLogProbTT(String previousTag, String currentTag)
	{
		double countTT = countTable.getCount("TT", previousTag + " " + currentTag);
		double lambda = singTable.getCount("TT", previousTag);

		if (lambda == 0) { lambda = Math.pow(10, -100); }

		double probBackoffTT = getProbBackoffTT(previousTag, currentTag);
		double countTag = countTable.getCount("Tag", previousTag);

		double logProb = Math.log((countTT + lambda * probBackoffTT) / (countTag + lambda));
		return logProb;
	}

	public static double getLogProbTW(String tag, String word)
	{
		int N = vocabulary.size();

		double countTW = countTable.getCount("TW", tag + " " + word);
		double lambda = singTable.getCount("TW", tag);

		if (tag.equals("###")) { lambda = 0; }
		else if (lambda == 0) { lambda = Math.pow(10, -100); }

		double probBackoffTW = getProbBackoffTW(tag, word);
		double countTag = countTable.getCount("Tag", tag);

		double logProb = Math.log((countTW + lambda * probBackoffTW) / (countTag + lambda));
		return logProb;
	}

	public static void evaluatePerformance(ArrayList words, ArrayList tags, LinkedList MyTags)
	{
		int n = words.size() - 1;

		for (int index = 1; index < n; index++)
		{
			String word = (String) words.get(index);
			String tag = (String) tags.get(index);
			String MyTag = (String) MyTags.get(index);

			if (!tag.equals("###"))
			{
				if (!vocabulary.contains(word))
				{
					if (!tag.equals(MyTag)) { novelIncorrect++; }
					else { novelCorrect++; }
				}
				else
				{
					if (!tag.equals(MyTag)) { knownIncorrect++; }
					else { knownCorrect++; }
				}
			}
		}
	}

	public static void printPerformance()
	{
		double totalAccuracy = (novelCorrect + knownCorrect) / (novelCorrect + knownCorrect + novelIncorrect + knownIncorrect);
		double novelAccuracy = novelCorrect / (novelCorrect + novelIncorrect);
		double knownAccuracy = knownCorrect / (knownCorrect + knownIncorrect);

		NumberFormat percent = NumberFormat.getPercentInstance();
		NumberFormat number = NumberFormat.getNumberInstance();

		percent.setMinimumFractionDigits(2);
		number.setMinimumFractionDigits(3);

		System.out.println("Tagging accuracy: " + percent.format(totalAccuracy) + " (known: " + percent.format(knownAccuracy) + " novel: " + percent.format(novelAccuracy) + ")");
		System.out.println("Perplexity per tagged test word: " + number.format(Math.pow(Math.E, -(totalProb / totalLength))));

		System.out.println("Expanded Nodes: " + expanded);

		System.out.println(totalProb + " " + totalLength);
	}
}
