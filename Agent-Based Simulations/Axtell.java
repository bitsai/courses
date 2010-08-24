import java.util.*;

public class Axtell
{
	static Random rand = new Random();

	static int numAgents;
	static int numFirms;
	static int M;
	static int numRounds;

	static LinkedList agents = new LinkedList();
	static LinkedList firms = new LinkedList();

	public static void main(String[] args)
	{
		// Billy check

		if (args.length < 3)
		{
			System.out.println("Usage: java Axtell <number of Agents> <M> <number of rounds>");
			System.exit(0);
		}

		// parsing arguments

		numAgents = Integer.parseInt(args[0]);
		numFirms = numAgents;
		M = Integer.parseInt(args[1]);
		numRounds = Integer.parseInt(args[2]);

		// initialize agents and firms

		for (int count = 0; count < numAgents; count++)
		{
			Agent agent = new Agent(count);
			Firm firm = new Firm(count);

			agent.moveToNewFirm(firm);
			firm.addAgentToFirm(agent);

			agents.add(agent);
			firms.add(firm);
		}

		// main loop

		for (int round = 0; round < numRounds; round++)
		{
			// making all agents clean again

			for (int count = 0; count < agents.size(); count++)
			{
				Agent agent = (Agent) agents.get(count);

				agent.dirty = false;
			}

			// Real work happens

			for (int count = 0; count < M; count++)
			{
				int randNum = rand.nextInt(numAgents);
				Agent agent = (Agent) agents.get(randNum);

				while (agent.dirty == true)
				{
					randNum++;
					randNum %= M;
					agent = (Agent) agents.get(randNum);
				}

				agent.dirty = true;

				// choose the correct firms

				int currentFirmNum = firms.indexOf(agent.firm);

				int previousFirmNum = currentFirmNum - 1;
				int nextFirmNum = currentFirmNum + 1;

				if (currentFirmNum == 0) { previousFirmNum = firms.size() - 1; }
				if (currentFirmNum == firms.size() - 1) { nextFirmNum = 0; }

				double leastEffort;
				Firm bestFirm;

				// compute effort that maximizes welfare at current firm
				Firm currentFirm = agent.firm;
				double bestEffortAtCurrentFirm = agent.computeOptimalEffortInPresentFirm();

				leastEffort = bestEffortAtCurrentFirm;
				bestFirm = agent.firm;

				// compute effort that maximizes welfare at neighboring firms
				Firm previousFirm = (Firm) firms.get(previousFirmNum);
				double bestEffortAtPreviousFirm = agent.computeOptimalEffortInOtherFirm(previousFirm);

				if (bestEffortAtPreviousFirm < leastEffort)
				{
					leastEffort = bestEffortAtPreviousFirm;
					bestFirm = previousFirm;
				}

				Firm nextFirm = (Firm) firms.get(nextFirmNum);
				double bestEffortAtNextFirm = agent.computeOptimalEffortInOtherFirm(nextFirm);

				if (bestEffortAtNextFirm < leastEffort)
				{
					leastEffort = bestEffortAtNextFirm;
					bestFirm = nextFirm;
				}

				// compute effort that maximizes welfare at start-up firm
				Firm newFirm = new Firm(-1);
				double bestEffortAtStartupFirm = agent.computeOptimalEffortInOtherFirm(newFirm);

				if (bestEffortAtStartupFirm < leastEffort)
				{
					leastEffort = bestEffortAtStartupFirm;
					bestFirm = newFirm;
				}

				// move to firm where welfare is greatest
				currentFirm.removeAgentFromFirm(agent);
				bestFirm.addAgentToFirm(agent);
				agent.leavePresentFirm();
				agent.moveToNewFirm(bestFirm);

				// draw agent(?)
			}

			for (int firmNum = 0; firmNum < firms.size(); firmNum++)
			{
				// compute output for selected firm

				Firm firm = (Firm) firms.get(firmNum);

				// allocate income

				// compute welfare

				firm.allocateIncomeToAgents();
			}

			// compute statistics

			int actualFirms = 0;

			for (int firmNum = 0; firmNum < firms.size(); firmNum++)
			{
				Firm firm = (Firm) firms.get(firmNum);

				if (firm.agentList.size() > 0)
				{ actualFirms++; }
			}

			System.out.println(actualFirms);

			// check for user input... or not
		}
	}
}