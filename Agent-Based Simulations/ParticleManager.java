import java.util.*;

public class ParticleManager
{
	// We will have a list of particles containing all known particles and their properties, call it pKnown
	// Set aside mUse of the E for mass of new particles.
	// Varying mUse will allow us to observe high, medium, or low Pt productions

	static double Etot; //The sum is over all initial particles
	static double Eavail;
	static double px;
	static double py;
	static double Qi;
	static double Si;
	static double Bi;
	static double Qf = 0;
	static double Sf = 0;
	static double Bf = 0;

	static LinkedList pKnown;
	static double mUse;
	static Random random = new Random();
	static LinkedList particles;

// *** How do we know about existing particles? ***

	public static void setVars()
	{
		for (int i = 0; i < particles.size(); i++)
		{
			Particle pi = (Particle) particles.get(i);

			Etot = Math.sqrt(pi.M * pi.M + (0.5 * pi.M * pi.vx * pi.vx) * (0.5 * pi.M * pi.vx * pi.vx) + (0.5 * pi.M * pi.vy * pi.vy));
			px = pi.M * pi.vx; // Sum over all initial particles
			py = pi.M * pi.vy;
			Qi = pi.Q;
			Si = pi.S;
			Bi = pi.B;
		}

		Eavail = mUse * Etot;
	}

// -----------------------------------------------------------
// Particle Creation

	public static void particleCreation()
	{
		LinkedList newParticles = new LinkedList();

		boolean done = false;

		while (!done)
		{
			if (!particleAvail(Eavail))
			{
				// pop two partices or restart from zero, your choice, I don't know which will work better

				newParticles = new LinkedList();
			}

			// Select rand particle from pKnown with m < Eavail call it pnew;

			Particle pnew = getParticle(Eavail);

			// This is so that if 1/2 of the available energy has been used in particle creation, all new creations must converge towards our final Q, S, and B
			// There are definately better ways to do this, if you think of any, go for it

			if (Eavail <= 0.5 * mUse * Etot)
			{
				if (Math.abs(Qf + pnew.Q - Qi) + Math.abs(Sf + pnew.S - Si) + Math.abs(Bf + pnew.B - Bi) >= Math.abs(Qf - Qi) + Math.abs(Sf - Si) + Math.abs(Bf - Bi))
				{
					// do not use the particle
				}
				else
				{
					// create instance of that particle, push onto stack of created particles (p)

					newParticles.add(pnew);

					quantUpdate(pnew);

					if (Qf == Qi && Sf == Si && Bf == Bi)
					{
			 			done = true;
					}
				}
			}
		}

		balanceMom(newParticles, px, py, Eavail + Etot * (1 - mUse));
	}

// -----------------------------------------------------------
// Particle decay
// Each particle will have a mean lifetime (Tau) before decay as well as a list of particle modes (just a set of particles) it can decay into and the probability of each decay mode and should known how many rounds it has been alive (timeAlive)

	public static void particleDecay(LinkedList particles)
	{
		// Each round loop over all particles and do:

		for (int i = 0; i < particles.size(); i++)
		{
			double willDecay = random.nextDouble();
			Particle pi = (Particle) particles.get(i);

			if (willDecay > Math.pow(Math.E, (-pi.timeAlive / pi.Tau)))
			{
				double v = Math.sqrt(pi.vx * pi.vx + pi.vy * pi.vy);
				double E = Math.sqrt((0.5 * pi.M * v * v) * (0.5 * pi.M * v * v) + pi.M * pi.M);

				// Randomly select one of the decay modes which will have n particles
				// create the particles as LinkedList decayProducts;

				LinkedList decayProducts = pi.getDecayProducts();
				double sum = 0;

				for (int j = 0; j < decayProducts.size(); j++)
				{
					Particle pj = (Particle) decayProducts.get(j);
					sum += pj.M * pj.M;
				}

				E += -sum; // Sum is over all particles in decayProducts
				Eavail = E / decayProducts.size();

				px = pi.M * pi.vx;
				py = pi.M * pi.vy;

				for (int j = 0; j < decayProducts.size(); j++)
				{
					Particle pj = (Particle) decayProducts.get(j);
					double vassign = Math.sqrt(2 * Eavail / pj.M);
					pj.vx = vassign * random.nextDouble();
					pj.vy = vassign - pj.vx;
				}

				balanceMom(decayProducts, px, py, 0);
			}
		}
	}

// --------------------------------------------------------------
// Balance Mom(entum)

	public static void balanceMom(LinkedList particles, double px, double py, double Eavail)
	{
		double ppx = 0;
		double ppy = 0;

		for (int k = 0; k < particles.size(); k++)
		{
			Particle pk = (Particle) particles.get(k);
			ppx += pk.M * pk.vx;
			ppy += pk.M * pk.vy;
		}

		// you may want to make this a bit more hazy so if ppx is very close to px that's good enough or make a "no infinite loop" clause.

		while ((ppx != px) || (ppy != py))
		{
			if (ppx != px)
			{
				double direction = (px - ppx) / Math.abs(px - ppx);

				// choose a random particle i

				int i = random.nextInt(particles.size());
				Particle pi = (Particle) particles.get(i);
				pi.vx += direction;

				while (Eavail < pi.M)
				{
					// chose at random a particle j with (p[j].vx/p[i].vx) < 0

					int j = random.nextInt(particles.size());
					Particle pj = (Particle) particles.get(j);

					while (pj.vx / pi.vx >= 0)
					{
						j = random.nextInt(particles.size());
						pj = (Particle) particles.get(j);
					}

					pj.vx += -1;
					Eavail = Math.sqrt(Eavail * Eavail + pj.M * pj.M);
				}

				Eavail = Math.sqrt(Eavail * Eavail - pi.M * pi.M);
			}

			if (ppy != py)
			{
				double direction = (py - ppy) / Math.abs(py - ppy);

				// choose at random a particle i

				int i = random.nextInt(particles.size());
				Particle pi = (Particle) particles.get(i);
				pi.vy += direction;

				while (Eavail < pi.M)
				{
					// chose at random a particle j with (p[j].vy/p[i].vy) < 0

					int j = random.nextInt(particles.size());
					Particle pj = (Particle) particles.get(j);

					while (pj.vy / pi.vy >= 0)
					{
						j = random.nextInt(particles.size());
						pj = (Particle) particles.get(j);
					}

					pj.vy += -1;
					Eavail = Math.sqrt(Eavail * Eavail + pj.M * pj.M);
				}

				Eavail = Math.sqrt(Eavail * Eavail - pi.M * pi.M);
			}

			for (int k = 0; k < particles.size(); k++)
			{
				Particle pk = (Particle) particles.get(k);
				ppx += pk.M * pk.vx;
				ppy += pk.M * pk.vy;
			}
		}
	}

// --------------------------------------------------------------

	public static void quantUpdate(Particle p)
	{
	     Eavail = Math.sqrt(Eavail * Eavail - p.M * p.M);
	     Qf += p.Q;
	     Sf += p.S;
	     Bf += p.B;
	}

	public static boolean particleAvail(double Eavail)
	{
		for (int i = 0; i < pKnown.size(); i++)
		{
			Particle pi = (Particle) pKnown.get(i);

			if (pi.M <= Eavail)
			{
				return true;
			}
		}

		return false;
	}

	public static Particle getParticle(double Eavail)
	{
		LinkedList temp = new LinkedList();

		for (int i = 0; i < pKnown.size(); i++)
		{
			Particle pi = (Particle) pKnown.get(i);

			if (pi.M < Eavail)
			{
				temp.add(pi);
			}
		}

		int i = random.nextInt(temp.size());

		return (Particle) temp.get(i);
	}
}