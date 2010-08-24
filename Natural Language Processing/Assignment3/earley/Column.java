package earley;
import java.util.*;

public class Column
{
	public int columnIndex;
	public EntryTable entryTable;
	public ArrayList entries = new ArrayList();

	public Column(int columnIndex, EntryTable entryTable)
	{
		this.columnIndex = columnIndex;
		this.entryTable = entryTable;
	}

	public void addEntry(Entry entry)
	{
		entries.add(entry);
	}

	public Entry getEntry(int entryIndex)
	{
		return (Entry) entries.get(entryIndex);
	}

	public int size()
	{
		return entries.size();
	}

	public String toString()
	{
		String output = "";

		for (int entryIndex = 0; entryIndex < size(); entryIndex++)
		{
			output += getEntry(entryIndex).toString() + "\n";
		}

		return output;
	}
}