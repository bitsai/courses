import java.util.*;

public class TagDictionary
{
	HashMap table = new HashMap();

	public ArrayList getTags(String word)
	{
		if (!table.containsKey(word)) { return (ArrayList) table.get("OOV"); }
		return (ArrayList) table.get(word);
	}

	public void addTag(String word, String tag)
	{
		if (!table.containsKey(word))
		{
			ArrayList tags = new ArrayList();
			tags.add(tag);
			table.put(word, tags);
		}
		else
		{
			ArrayList tags = getTags(word);
			int index = tags.indexOf(tag);
			if (index == -1) { tags.add(tag); }
		}
	}
}
