package earley2;
import java.util.*;

public class Column
{
	public int columnIndex;
	public CustomerTable customerTable;
	public ArrayList entries;

	public Column(int columnIndex, CustomerTable customerTable)
	{
		this.columnIndex = columnIndex;
		this.customerTable = customerTable;
		this.entries = new ArrayList();
	}

	public void addEntry(Entry entry)
	{
		customerTable.addCustomer(columnIndex, entry.afterDot(), entry);
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