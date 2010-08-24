import java.util.*;

public class Node
{
	LinkedList tags;
	double insideProb;
	double outsideProb;

	public Node(LinkedList T, double IP, double OP)
	{
		tags = T;
		insideProb = IP;
		outsideProb = OP;
	}

	public LinkedList tags()
	{
		return tags;
	}

	public double insideProb()
	{
		return insideProb;
	}

	public double outsideProb()
	{
		return outsideProb;
	}

	public double totalProb()
	{
		return insideProb + outsideProb;
	}

	public int position()
	{
		return tags.size();
	}

	public String lastTag()
	{
		return (String) tags.getLast();
	}

	public Node extend(String newTag, double newInsideProb, double newOutsideProb)
	{
		LinkedList newTags = (LinkedList) tags.clone();
		newTags.addLast(newTag);
		Node newNode = new Node(newTags, newInsideProb, newOutsideProb);
		return newNode;
	}

	public String toString()
	{
		return tags.toString();
	}
}