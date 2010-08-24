import java.util.*;

public class Agent
{
	int ID;

	static double A = 1;
	static double B = 1;

	double preferences; // Theta
	LinkedList neighbors;
	double effort_level;
	double last_income;
	double last_utility;
	double wealth;
	Firm firm;
	Agent nextAgentInList; // don't need this
	Agent nextAgentInFirm; // don't need this

	boolean dirty = false;

	public Agent(int id)
	{
		ID = id;

		initialize();
	}

	public void initialize()
	{
		preferences = Math.random();
		neighbors = new LinkedList();
		effort_level = 0;
		last_income = 0;
		last_utility = 0;
		wealth = 0;
	}

	public double computeUtility()
	{
		return Math.pow(last_income, preferences) * Math.pow((1 - effort_level), (1 - preferences));
	}

	public double computeOptimalEffortInPresentFirm()
	{
		double othersEffort = firm.computeTotalEffort() - effort_level;

		double top = (-1 * A) - (2 * B) * (othersEffort - preferences);
		double squared = (A * A) + 4 * A * B * preferences * preferences * (1 + othersEffort) + 4 * B * B * preferences * preferences * (1 + othersEffort) * (1 + othersEffort);
		double bottom = 2 * B * (1 + preferences);

		return Math.max(0, (top + Math.sqrt(squared)) / bottom);
	}

	public double computeOptimalEffortInOtherFirm(Firm otherFirm)
	{
		double othersEffort = otherFirm.computeTotalEffort();

		double top = (-1 * A) - (2 * B) * (othersEffort - preferences);
		double squared = (A * A) + 4 * A * B * preferences * preferences * (1 + othersEffort) + 4 * B * B * preferences * preferences * (1 + othersEffort) * (1 + othersEffort);
		double bottom = 2 * B * (1 + preferences);

		return Math.max(0, (top + Math.sqrt(squared)) / bottom);
	}

	public void leavePresentFirm()
	{
		firm = null;
	}

	public void moveToNewFirm(Firm newFirm)
	{
		firm = newFirm;
	}

	public void draw() {} // don't need this

	public boolean equals(Agent other)
	{
		return (ID == other.ID);
	}
}