import java.util.*;

public class Firm
{
	int ID;

	static double A = 1;
	static double B = 1;

	Agent founder;
	LinkedList agentList;
	int size; // not needed
	double growth;
	double lastOutput;

	public Firm(int id)
	{
		ID = id;

		initialize();
	}

	public void initialize()
	{
		agentList = new LinkedList();
		growth = 0;
		lastOutput = 0;
	}

	public double computeTotalEffort()
	{
		double sum = 0;

		for (int agentNum = 0; agentNum < agentList.size(); agentNum++)
		{
			Agent agent = (Agent) agentList.get(agentNum);
			sum += agent.effort_level;
		}

		return sum;
	}

	public double computeAverageEffort()
	{
		return (computeTotalEffort() / agentList.size());
	}

	public double computeOutput()
	{
		double effort = computeTotalEffort();
		return (A * effort) + (B * effort * effort);
	}

	public void allocateIncomeToAgents()
	{
		double salary = computeOutput() / agentList.size();

		for (int agentNum = 0; agentNum < agentList.size(); agentNum++)
		{
			Agent agent = (Agent) agentList.get(agentNum);
			agent.last_income = salary;
			agent.wealth += salary;
		}
	}

	public void addAgentToFirm(Agent agent)
	{
		// if this is the first agent in the firm, he's the founder
		if (agentList.size() == 0)
		{
			founder = agent;
		}

		agentList.add(agent);
	}

	public void removeAgentFromFirm(Agent agent)
	{
		if (agentList.contains(agent))
		{
			agentList.remove(agent);
		}

		if (founder.equals(agent))
		{
			if (agentList.size() > 0)
			{
				founder = (Agent) agentList.get(0);
			}
			else
			{
				founder = null;
			}
		}
	}

	public void draw() {} // don't need this

	public void dispose() {}

	public boolean equals(Firm other)
	{
		return (ID == other.ID);
	}
}