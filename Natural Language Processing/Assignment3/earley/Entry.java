package earley;
import java.util.*;

public class Entry
{
	public int start;
	public Rule rule;
	public int dotPosition;
	public float weight;
	public Entry pointer1;
	public Entry pointer2;

	public Entry(int start, Rule rule, int dotPosition, float weight, Entry pointer1, Entry pointer2)
	{
		this.start = start;
		this.rule = rule;
		this.dotPosition = dotPosition;
		this.weight = weight;
		this.pointer1 = pointer1;
		this.pointer2 = pointer2;
	}

	public String afterDot()
	{
		return rule.afterDot(dotPosition);
	}

	public String toString()
	{
		return start + " " + rule.toString() + " " + dotPosition + " " + weight;
	}

	public static Entry predictEntry(int in_start, Rule in_rule)
	{
		int start = in_start;
		Rule rule = in_rule;
		int dotPosition = 0;
		float weight = rule.weight;
		Entry pointer1 = null;
		Entry pointer2 = null;

		Entry newEntry = new Entry(start, rule, dotPosition, weight, pointer1, pointer2);
		return newEntry;
	}

	public static Entry scanEntry(Entry oldEntry)
	{
		int start = oldEntry.start;
		Rule rule = oldEntry.rule;
		int dotPosition = oldEntry.dotPosition + 1;
		float weight = oldEntry.weight;
		Entry pointer1 = oldEntry;
		Entry pointer2 = null;

		Entry newEntry = new Entry(start, rule, dotPosition, weight, pointer1, pointer2);
		return newEntry;
	}

	public static Entry attachEntry(Entry oldEntry, Entry curEntry)
	{
		int start = oldEntry.start;
		Rule rule = oldEntry.rule;
		int dotPosition = oldEntry.dotPosition + 1;
		float weight = oldEntry.weight + curEntry.weight;
		Entry pointer1 = oldEntry;
		Entry pointer2 = curEntry;

		Entry newEntry = new Entry(start, rule, dotPosition, weight, pointer1, pointer2);
		return newEntry;
	}

	public void replaceWith(Entry newSelf)
	{
		this.start = newSelf.start;
		this.rule = newSelf.rule;
		this.dotPosition = newSelf.dotPosition;
		this.weight = newSelf.weight;
		this.pointer1 = newSelf.pointer1;
		this.pointer2 = newSelf.pointer2;
	}
}