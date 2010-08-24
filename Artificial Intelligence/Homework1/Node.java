public class Node
{
	public String action;
	public Node parent;
	public int depth;

	public int Boat_Location; // 0 for right, 1 for left
	public int Left_Cannibals;
	public int Left_Missionaries;
	public int Right_Cannibals;
	public int Right_Missionaries;

	public Node (String A, Node P, int D, int BL, int LC, int LM, int RC, int RM)
	{
		action = A;
		parent = P;
		depth = D;

		Boat_Location = BL;
		Left_Cannibals = LC;
		Left_Missionaries = LM;
		Right_Cannibals = RC;
		Right_Missionaries = RM;
	}

	public String toString()
	{
		return Left_Cannibals * 1000 + Left_Missionaries * 100 + Right_Cannibals * 10 + Right_Missionaries + " " + Boat_Location;
	}

	public boolean Cannibalism()
	{
		if (Left_Missionaries > 0 && Left_Cannibals > Left_Missionaries)
		{ return true; }
		if (Right_Missionaries > 0 && Right_Cannibals > Right_Missionaries)
		{ return true; }

		return false;
	}

	public Node MoveLeft(int C, int M)
	{
		return (new Node("Move Left " + C + " Cannibals, " + M + " Missionaries", this, depth + 1, 1, Left_Cannibals + C, Left_Missionaries + M, Right_Cannibals - C, Right_Missionaries - M));
	}

	public Node MoveRight(int C, int M)
	{
		return (new Node("Move Right " + C + " Cannibals, " + M + " Missionaries", this, depth + 1, 0, Left_Cannibals - C, Left_Missionaries - M, Right_Cannibals + C, Right_Missionaries + M));
	}
}