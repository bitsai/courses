import java.io.*;
import java.text.*;
import java.util.*;

public class Vtag
{
	public static Table table = new Table();
	public static Table countTable = new Table();

	public static ArrayList vocabulary = new ArrayList();

	public static double novelCorrect = 0;
	public static double novelIncorrect = 0;
	public static double knownCorrect = 0;
	public static double knownIncorrect = 0;

	public static int totalLength = 0;
	public static double totalProb = 0;

	public static void main(String[] args)
	{
		try
		{
			String trainingData = args[0];
			String testData = args[1];

			FileReader fr = new FileReader(trainingData);
			BufferedReader br = new BufferedReader(fr);

			String line = null;
			ArrayList words = new ArrayList();
			ArrayList tags = new ArrayList();

			while ((line = br.readLine()) != null)
			{
				int split = line.indexOf("/");
				words.add(line.substring(0, split));
				tags.add(line.substring(split + 1));
			}

			processTrainingData(words, tags);
			int N = vocabulary.size();

			fr = new FileReader(testData);
			br = new BufferedReader(fr);

			line = null;
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

						ArrayList VTags = tag(words, N);
						evaluatePerformance(words, tags, VTags);
					}

					words.clear();
					tags.clear();
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

			table.putTagDictionary("TagDictionary " + currentWord, currentTag);
			if (!currentTag.equals("###")) { table.putTagDictionary("TagDictionary OOV", currentTag); }

			if (countTable.getCount("Word " + currentWord) == 0) { vocabulary.add(currentWord); }
			countTable.increment("Tokens");

			countTable.increment("TT " + previousTag + " " + currentTag);
			countTable.increment("TW " + currentTag + " " + currentWord);

			if (countTable.getCount("TT " + previousTag + " " + currentTag) == 1) { table.increment("SingTT " + previousTag); }
			if (countTable.getCount("TT " + previousTag + " " + currentTag) == 2) { table.decrement("SingTT " + previousTag); }
			if (countTable.getCount("TW " + currentTag + " " + currentWord) == 1) { table.increment("SingTW " + currentTag); }
			if (countTable.getCount("TW " + currentTag + " " + currentWord) == 2) { table.decrement("SingTW " + currentTag); }

			countTable.increment("Tag " + currentTag);
			countTable.increment("Word " + currentWord);
		}
	}

	public static void evaluatePerformance(ArrayList words, ArrayList tags, ArrayList VTags)
	{
		int n = words.size() - 1;

		for (int index = 1; index < n; index++)
		{
			String word = (String) words.get(index);
			String tag = (String) tags.get(index);
			String VTag = (String) VTags.get(index);

			if (!tag.equals("###"))
			{
				if (!vocabulary.contains(word))
				{
					if (!tag.equals(VTag)) { novelIncorrect++; }
					else { novelCorrect++; }
				}
				else
				{
					if (!tag.equals(VTag)) { knownIncorrect++; }
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
	}

	public static ArrayList tag(ArrayList words, int N)
	{
		Table tagTable = new Table();
		ArrayList bestTagSequence = new ArrayList();
		tagTable.putLogProb("BestPathProb 0 ###", Math.log(1));

		for (int position = 1; position < words.size(); position++)
		{
			String previousWord = (String) words.get(position - 1);
			String currentWord = (String) words.get(position);

			ArrayList previousTags = table.getTagDictionary("TagDictionary " + previousWord);
			ArrayList currentTags = table.getTagDictionary("TagDictionary " + currentWord);

			for (int currentTagNumber = 0; currentTagNumber < currentTags.size(); currentTagNumber++)
			{
				String currentTag = (String) currentTags.get(currentTagNumber);

				for (int previousTagNumber = 0; previousTagNumber < previousTags.size(); previousTagNumber++)
				{
					String previousTag = (String) previousTags.get(previousTagNumber);
					double logProb = getLogProbTT(previousTag, currentTag) + getLogProbTW(currentTag, currentWord, N);
					double logBestPathProb = tagTable.getLogProb("BestPathProb " + (position - 1) + " " + previousTag) + logProb;

					if (logBestPathProb > tagTable.getLogProb("BestPathProb " + position + " " + currentTag))
					{
						tagTable.putLogProb("BestPathProb " + position + " " + currentTag, logBestPathProb);
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
		totalProb += tagTable.getLogProb("BestPathProb " + (words.size() - 1) + " ###");

		return bestTagSequence;
	}

	public static double getProbBackoffTT(String previousTag, String currentTag)
	{
		double count = countTable.getCount("Tag " + currentTag);
		double n = countTable.getCount("Tokens");
		double prob = count / n;
		return prob;
	}

	public static double getProbBackoffTW(String tag, String word, int N)
	{
		double count = countTable.getCount("Word " + word);
		double n = countTable.getCount("Tokens");
		double prob = (count + 1) / (n + N + 1);
		return prob;
	}

	public static double getLogProbTT(String previousTag, String currentTag)
	{
		double countTT = countTable.getCount("TT " + previousTag + " " + currentTag);
		double lambda = table.getCount("SingTT " + previousTag);

		if (lambda == 0) { lambda = Math.pow(10, -100); }

		double probBackoffTT = getProbBackoffTT(previousTag, currentTag);
		double countTag = countTable.getCount("Tag " + previousTag);

		double logProb = Math.log((countTT + lambda * probBackoffTT) / (countTag + lambda));
		return logProb;
	}

	public static double getLogProbTW(String tag, String word, int N)
	{
		double countTW = countTable.getCount("TW " + tag +  " " + word);
		double lambda = table.getCount("SingTW " + tag);

		if (tag.equals("###")) { lambda = 0; }
		else if (lambda == 0) { lambda = Math.pow(10, -100); }

		double probBackoffTW = getProbBackoffTW(tag, word, N);
		double countTag = countTable.getCount("Tag " + tag);

		double logProb = Math.log((countTW + lambda * probBackoffTW) / (countTag + lambda));
		return logProb;
	}
}
