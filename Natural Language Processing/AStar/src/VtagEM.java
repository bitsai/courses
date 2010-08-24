import java.io.*;
import java.text.*;
import java.util.*;

public class VtagEM
{
	public static Table table = new Table();
	public static Table currentCountTable = new Table();
	public static Table originalCountTable = new Table();

	public static ArrayList knownVocabulary = new ArrayList();
	public static ArrayList seenVocabulary = new ArrayList();

	public static double knownCorrect = 0;
	public static double knownIncorrect = 0;
	public static double seenCorrect = 0;
	public static double seenIncorrect = 0;
	public static double novelCorrect = 0;
	public static double novelIncorrect = 0;

	public static int totalLength = 0;
	public static double totalProb = 0;

	public static void main(String[] args)
	{
		try
		{
			String trainingFile = args[0];
			String testFile = args[1];
			String rawFile = args[2];

			FileReader fr = new FileReader(trainingFile);
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

			fr = new FileReader(rawFile);
			br = new BufferedReader(fr);

			line = null;
			ArrayList rawWords = new ArrayList();

			while ((line = br.readLine()) != null)
			{
				rawWords.add(line);
			}

			processRawData(rawWords);

			fr = new FileReader(testFile);
			br = new BufferedReader(fr);

			line = null;
			words = new ArrayList();
			tags = new ArrayList();

			while ((line = br.readLine()) != null)
			{
				int split = line.indexOf("/");
				words.add(line.substring(0, split));
				tags.add(line.substring(split + 1));
			}

			int N = knownVocabulary.size() + seenVocabulary.size();

			ArrayList viterbiTags = tag(words, N);
			printViterbiPerformance(words, tags, viterbiTags);

			for (int iteration = 0; iteration < 3; iteration++)
			{
				double S = reestimate(rawWords, originalCountTable, currentCountTable, N);
				printEMPerformance(iteration, S, rawWords.size() - 1);

				viterbiTags = tag(words, N);
				printViterbiPerformance(words, tags, viterbiTags);
			}
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

			if (currentCountTable.getCount("Word " + currentWord) == 0) { knownVocabulary.add(currentWord); }
			currentCountTable.increment("Tokens");

			currentCountTable.increment("TT " + previousTag + " " + currentTag);
			currentCountTable.increment("TW " + currentTag + " " + currentWord);

			if (currentCountTable.getCount("TT " + previousTag + " " + currentTag) == 1) { table.increment("SingTT " + previousTag); }
			if (currentCountTable.getCount("TT " + previousTag + " " + currentTag) == 2) { table.decrement("SingTT " + previousTag); }
			if (currentCountTable.getCount("TW " + currentTag + " " + currentWord) == 1) { table.increment("SingTW " + currentTag); }
			if (currentCountTable.getCount("TW " + currentTag + " " + currentWord) == 2) { table.decrement("SingTW " + currentTag); }

			currentCountTable.increment("Tag " + currentTag);
			currentCountTable.increment("Word " + currentWord);
		}

		originalCountTable.copy(currentCountTable);
	}

	public static void processRawData(ArrayList words)
	{
		String currentWord = "###";

		for (int position = 1; position < words.size(); position++)
		{
			currentWord = (String) words.get(position);

			if (currentCountTable.getCount("Word " + currentWord) == 0 && currentCountTable.getCount("Seen " + currentWord) == 0)
			{
				seenVocabulary.add(currentWord);
			}

			currentCountTable.increment("Seen " + currentWord);
		}
	}

	public static void printViterbiPerformance(ArrayList words, ArrayList tags, ArrayList viterbiTags)
	{
		int n = words.size() - 1;

		for (int index = 1; index < n; index++)
		{
			String word = (String) words.get(index);
			String tag = (String) tags.get(index);
			String viterbiTag = (String) viterbiTags.get(index);

			if (!tag.equals("###"))
			{
				if (knownVocabulary.contains(word))
				{
					if (!tag.equals(viterbiTag)) { knownIncorrect++; }
					else { knownCorrect++; }
				}
				else if (seenVocabulary.contains(word))
				{
					if (!tag.equals(viterbiTag)) { seenIncorrect++; }
					else { seenCorrect++; }
				}
				else
				{
					if (!tag.equals(viterbiTag)) { novelIncorrect++; }
					else { novelCorrect++; }
				}
			}
		}

		double knownAccuracy = knownCorrect / (knownCorrect + knownIncorrect);
		double seenAccuracy = seenCorrect / (seenCorrect + seenIncorrect);
		double novelAccuracy = novelCorrect / (novelCorrect + novelIncorrect);
		double totalAccuracy = (knownCorrect + seenCorrect + novelCorrect) / (knownCorrect + seenCorrect + novelCorrect + knownIncorrect + seenIncorrect + novelIncorrect);

		NumberFormat percent = NumberFormat.getPercentInstance();
		NumberFormat number = NumberFormat.getNumberInstance();

		percent.setMinimumFractionDigits(2);
		number.setMinimumFractionDigits(3);

		System.out.println("Tagging accuracy: " + percent.format(totalAccuracy) + " (known: " + percent.format(knownAccuracy) + " seen: " + percent.format(seenAccuracy) + " novel: " + percent.format(novelAccuracy) + ")");
		System.out.println("Perplexity per tagged test word: " + number.format(Math.pow(Math.E, -(totalProb / totalLength))));
	}

	public static void printEMPerformance(int iteration, double S, double n)
	{
		double perplexity = Math.pow(Math.E, -S / n);
		NumberFormat number = NumberFormat.getNumberInstance();
		number.setMinimumFractionDigits(3);
		System.out.println("Iteration " + iteration + ": Perplexity per untagged raw word: " + number.format(perplexity));
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

			if (currentCountTable.getCount("Word " + previousWord) == 0) { previousWord = "OOV"; }
			if (currentCountTable.getCount("Word " + currentWord) == 0) { currentWord = "OOV"; }

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
		double count = currentCountTable.getCount("Tag " + currentTag);
		double n = currentCountTable.getCount("Tokens");
		double prob = count / n;
		return prob;
	}

	public static double getProbBackoffTW(String tag, String word, int N)
	{
		double count = currentCountTable.getCount("Word " + word);
		double n = currentCountTable.getCount("Tokens");
		double prob = (count + 1) / (n + N + 1);
		return prob;
	}

	public static double getLogProbTT(String previousTag, String currentTag)
	{
		double countTT = currentCountTable.getCount("TT " + previousTag + " " + currentTag);
		double lambda = table.getCount("SingTT " + previousTag);

		if (lambda == 0) { lambda = Math.pow(10, -100); }

		double probBackoffTT = getProbBackoffTT(previousTag, currentTag);
		double countTag = currentCountTable.getCount("Tag " + previousTag);

		double logProb = Math.log((countTT + lambda * probBackoffTT) / (countTag + lambda));
		return logProb;
	}

	public static double getLogProbTW(String tag, String word, int N)
	{
		double countTW = currentCountTable.getCount("TW " + tag +  " " + word);
		double lambda = table.getCount("SingTW " + tag);

		if (tag.equals("###")) { lambda = 0; }
		else if (lambda == 0) { lambda = Math.pow(10, -100); }

		double probBackoffTW = getProbBackoffTW(tag, word, N);
		double countTag = currentCountTable.getCount("Tag " + tag);

		double logProb = Math.log((countTW + lambda * probBackoffTW) / (countTag + lambda));
		return logProb;
	}

	public static double reestimate(ArrayList words, Table originalCountTable, Table currentCountTable, int N)
	{
		Table emTable = new Table();
		Table newCountTable = new Table(originalCountTable);
		emTable.putLogProb("Alpha 0 ###", Math.log(1));

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
					double alpha = logAdd(emTable.getLogProb("Alpha " + position + " " + currentTag), emTable.getLogProb("Alpha " + (position - 1) + " " + previousTag) + logProb);
					emTable.putLogProb("Alpha " + position + " " + currentTag, alpha);
				}
			}
		}

		double S = emTable.getLogProb("Alpha " + (words.size() - 1) + " ###");
		emTable.putLogProb("Beta " + (words.size() - 1) + " ###", Math.log(1));

		for (int position = words.size() - 1; position > 0; position--)
		{
			String previousWord = (String) words.get(position - 1);
			String currentWord = (String) words.get(position);

			ArrayList previousTags = table.getTagDictionary("TagDictionary " + previousWord);
			ArrayList currentTags = table.getTagDictionary("TagDictionary " + currentWord);

			for (int currentTagNumber = 0; currentTagNumber < currentTags.size(); currentTagNumber++)
			{
				String currentTag = (String) currentTags.get(currentTagNumber);
				double newCountTW = newCountTable.getCount("TW " + currentTag + " " + currentWord) + Math.pow(Math.E, (emTable.getLogProb("Alpha " + position + " " + currentTag) + emTable.getLogProb("Beta " + position + " " + currentTag) - S));
				newCountTable.putCount("TW " + currentTag + " " + currentWord, newCountTW);

				for (int previousTagNumber = 0; previousTagNumber < previousTags.size(); previousTagNumber++)
				{
					String previousTag = (String) previousTags.get(previousTagNumber);
					double logProb = getLogProbTT(previousTag, currentTag) + getLogProbTW(currentTag, currentWord, N);
					double beta = logAdd(emTable.getLogProb("Beta " + (position - 1) + " " + previousTag), emTable.getLogProb("Beta " + position + " " + currentTag) + logProb);
					emTable.putLogProb("Beta " + (position - 1) + " " + previousTag, beta);
					double newCountTT = newCountTable.getCount("TT " + previousTag + " " + currentTag) + Math.pow(Math.E, (emTable.getLogProb("Alpha " + (position - 1) + " " + previousTag) + emTable.getLogProb("Beta " + position + " " + currentTag) + logProb - S));
					newCountTable.putCount("TT " + previousTag + " " + currentTag, newCountTT);
				}
			}
		}

		for (int position = 1; position < words.size(); position++)
		{
			String currentWord = (String) words.get(position);
			ArrayList currentTags = table.getTagDictionary("TagDictionary " + currentWord);

			newCountTable.increment("Word " + currentWord);
			newCountTable.increment("Tokens");

			for (int currentTagNumber = 0; currentTagNumber < currentTags.size(); currentTagNumber++)
			{
				String currentTag = (String) currentTags.get(currentTagNumber);

				double alpha = emTable.getLogProb("Alpha " + position + " " + currentTag);
				double beta = emTable.getLogProb("Beta " + position + " " + currentTag);
				double logProb = alpha + beta - S;
				double newCount = newCountTable.getCount("Tag " + currentTag) + Math.pow(Math.E, logProb);

				newCountTable.putCount("Tag " + currentTag, newCount);
			}
		}

		currentCountTable.copy(newCountTable);

		return S;
	}

	public static double logAdd(double x, double y)
	{
		double result = 0;

		if (y <= x)
		{
			double exp = Math.pow(Math.E, y - x);
			result = x + Math.log(1 + exp);
		}
		else
		{
			double exp = Math.pow(Math.E, x - y);
			result = y + Math.log(1 + exp);
		}

		return result;
	}
}
