import java.util.*;

public class Particle
{
	double M;
	double Q;
	double S;
	double B;

	double vx;
	double vy;

	double timeAlive;
	double Tau;

	HashMap decayProducts;
	LinkedList decayModes;
	LinkedList decayProbs;

	Random random = new Random();

	public LinkedList getDecayProducts()
	{
		double threshold = random.nextDouble();

		for (int i = 0; i < decayProbs.size(); i++)
		{
			Double dpi = (Double) decayProbs.get(i);
			double prob = dpi.doubleValue();

			if (prob >= threshold)
			{
				String decayMode = (String) decayModes.get(i);
				return (LinkedList) decayProducts.get(decayMode);
			}
		}

		return null;
	}
}