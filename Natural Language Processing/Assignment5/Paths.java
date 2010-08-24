import java.util.*;

public class Paths
{
	public TreeMap pathsByProb = new TreeMap();
	public TreeMap pathsByPath = new TreeMap();
	public ArrayList outsideProbs;
	public int n;

	public Paths(ArrayList outsideProbs, int n)
	{
		this.outsideProbs = outsideProbs;
		this.n = n;
	}

	public void push(Path path)
	{
		Double newProb = new Double(path.prob);

		if (path.position < n)
		{
			newProb = new Double(path.prob + ((Double) outsideProbs.get(path.position)).doubleValue());
		}

		String newPath = path.position + " " + path.tag;

		if (pathsByPath.containsKey(newPath))
		{
			Double oldProb = (Double) pathsByPath.get(newPath);

			if (newProb.doubleValue() > oldProb.doubleValue())
			{
				pathsByPath.put(newPath, newProb);
				pathsByProb.put(newProb, path);
			}
		}
		else
		{
			if (pathsByProb.containsKey(newProb))
			{ newProb = new Double(newProb.doubleValue() + Math.pow(10, -10)); }
			pathsByPath.put(newPath, newProb);
			pathsByProb.put(newProb, path);
		}
	}

	public Path pop()
	{
		Double lastKey = (Double) pathsByProb.lastKey();
		Path path = (Path) pathsByProb.get(lastKey);
		pathsByProb.remove(lastKey);
		return path;
	}
}