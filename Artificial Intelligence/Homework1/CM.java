import java.io.*;
import java.util.*;

public class CM
{
	static int nodes = 0;

	public static void main (String[] args)
	{
		try
		{
			BufferedReader BR = new BufferedReader(new InputStreamReader(System.in));

			System.out.print("Enter Number of Cannibals: ");
			int C = Integer.parseInt(BR.readLine());

			System.out.print("Enter Number of Missionaries: ");
			int M = Integer.parseInt(BR.readLine());

			LinkedList fringe = new LinkedList();
			LinkedList solution = GraphSearch(C, M, fringe);

			if (solution == null)
			{ System.out.println("No solution!"); }
			else
			{
				while (solution.size() > 0)
				{ System.out.println(solution.removeFirst()); }
			}

			System.out.println("Nodes Expanded: " + nodes);
		}
		catch (Exception e)
		{
			System.out.println(e);
		}
	}

	public static LinkedList GraphSearch(int C, int M, LinkedList fringe)
	{
		HashSet closed = new HashSet();
		Insert(fringe, new Node("", null, 0, 0, 0, 0, C, M));

		while (2 == 2)
		{
			if (fringe.size() == 0)
			{ return null; }

			Node node = (Node) fringe.removeFirst();

			if (GoalTest(node))
			{ return Solution(node); }

			if (closed.contains(node.toString()) == false)
			{
				closed.add(node.toString());
				InsertAll(fringe, Expand(node));
			}
		}
	}

	public static void Insert(LinkedList fringe, Node node)
	{
		if (node.Cannibalism() == false)
		{ fringe.addFirst(node); }
	}

	public static boolean GoalTest(Node node)
	{
		if (node.Right_Cannibals + node.Right_Missionaries == 0 && node.Boat_Location == 1)
		{ return true; }

		return false;
	}

	public static LinkedList Solution(Node node)
	{
		LinkedList solution = new LinkedList();
		Node currentNode = node;

		while (currentNode != null)
		{
			if (currentNode.depth > 0)
			{ solution.addFirst(currentNode.depth + ". " + currentNode.action); }

			currentNode = currentNode.parent;
		}

		return solution;
	}

	public static void InsertAll(LinkedList fringe, LinkedList nodes)
	{
		if (nodes != null)
		{
			while (nodes.size() > 0)
			{
				Node node = (Node) nodes.removeFirst();
				Insert(fringe, node);
			}
		}
	}

	public static LinkedList Expand(Node node)
	{
		nodes++;
		LinkedList nodes = new LinkedList();

		if (node.Boat_Location == 0) // Moving Left
		{
			if (node.Right_Cannibals > 0 && node.Right_Missionaries > 0)
			{ nodes.addFirst(node.MoveLeft(1, 1)); }
			if (node.Right_Cannibals > 1)
			{ nodes.addFirst(node.MoveLeft(2, 0)); }
			if (node.Right_Missionaries > 1)
			{ nodes.addFirst(node.MoveLeft(0, 2)); }

			if (node.Right_Cannibals > 0)
			{ nodes.addFirst(node.MoveLeft(1, 0)); }
			if (node.Right_Missionaries > 0)
			{ nodes.addFirst(node.MoveLeft(0, 1)); }
		}
		else // Moving Right
		{
			if (node.Left_Cannibals > 0 && node.Left_Missionaries > 0)
			{ nodes.addFirst(node.MoveRight(1, 1)); }
			if (node.Left_Cannibals > 1)
			{ nodes.addFirst(node.MoveRight(2, 0)); }
			if (node.Left_Missionaries > 1)
			{ nodes.addFirst(node.MoveRight(0, 2)); }

			if (node.Left_Cannibals > 0)
			{ nodes.addFirst(node.MoveRight(1, 0)); }
			if (node.Left_Missionaries > 0)
			{ nodes.addFirst(node.MoveRight(0, 1)); }
		}

		return nodes;
	}
}
