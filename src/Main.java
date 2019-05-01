import java.util.Arrays;

public class Main {
	private static class Library {
		private static final SearchPoint INSTANCE = new SearchPoint();

		public SearchPoint getGbest() {
			return INSTANCE;
		}
	};

	private static class SearchPoint {
		private static final double[] INSTANCE = new double[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 };

		public double[] getLocation() {
			return INSTANCE;
		}

		@Override
		public String toString() {
			return Arrays.toString(INSTANCE);
		}
	};

	private static class DesignSpace {
		public int getDimension() {
			return 10;
		}
	}

	private static class ProblemEncoder {
		private static final DesignSpace INSTANCE = new DesignSpace();

		public DesignSpace getDesignSpace() {
			return INSTANCE;
		}
	};

	private static class BasicPoint {
		public double[] getLocation() {
			return new double[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 };
		}
	};

	private static class RandomGenerator {
		public static int intRangeRandom(int a, int b) {
			return (int) (a + Math.random() * (b - a));
		}
	};

	private static double FACTOR = 0.5D;

	private static double CR = 0.9D;

	private static final Library socialLib = new Library();

	private static final SearchPoint pbest_t = new SearchPoint();

	private static final SearchPoint gbest_t = new SearchPoint();

	private static final BasicPoint[] POINTS = new BasicPoint[] { new BasicPoint(), new BasicPoint(), new BasicPoint(),
			new BasicPoint(), new BasicPoint(), new BasicPoint(), new BasicPoint(), new BasicPoint(), new BasicPoint(),
			new BasicPoint() };

	private BasicPoint[] getReferPoints() {
		return POINTS;
	}

	/**
	 * Crossover and mutation for a single vector element done in a single step.
	 * 
	 * @param index
	 *            Index of the trial vector element to be changed.
	 * @param trialVector
	 *            Trial vector reference.
	 * @param globalBestVector
	 *            Global best found vector reference.
	 * @param differenceVectors
	 *            List of vectors used for difference delta calculation.
	 */
	private void crossoverAndMutation(int index, double trialVector[], double globalBestVector[],
			BasicPoint differenceVectors[]) {
		double delta = 0D;

		for (int i = 0; i < differenceVectors.length; i++) {
			delta += (i % 2 == 0 ? +1D : -1D) * differenceVectors[i].getLocation()[index];
		}

		trialVector[index] = globalBestVector[index] + FACTOR * delta;
	}

	public void generateBehavior2(SearchPoint trailPoint, ProblemEncoder problemEncoder) {
		SearchPoint gbest_t = socialLib.getGbest();

		BasicPoint[] referPoints = getReferPoints();
		int DIMENSION = problemEncoder.getDesignSpace().getDimension();
		int guaranteeIndex = RandomGenerator.intRangeRandom(0, DIMENSION - 1);

		/* Handle first part of the trial vector. */
		for (int index = 0; index < guaranteeIndex; index++) {
			if (CR <= Math.random()) {
				trailPoint.getLocation()[index] = pbest_t.getLocation()[index];
				continue;
			}

			crossoverAndMutation(index, trailPoint.getLocation(), gbest_t.getLocation(), referPoints);
		}

		/* Guarantee for at least one change in the trial vector. */
		crossoverAndMutation(guaranteeIndex, trailPoint.getLocation(), gbest_t.getLocation(), referPoints);

		/* Handle second part of the trial vector. */
		for (int index = guaranteeIndex + 1; index < DIMENSION; index++) {
			if (CR <= Math.random()) {
				trailPoint.getLocation()[index] = pbest_t.getLocation()[index];
				continue;
			}

			crossoverAndMutation(index, trailPoint.getLocation(), gbest_t.getLocation(), referPoints);
		}
	}

	public void generateBehavior(SearchPoint trailPoint, ProblemEncoder problemEncoder) {
		SearchPoint gbest_t = socialLib.getGbest();

		BasicPoint[] referPoints = getReferPoints();
		int DIMENSION = problemEncoder.getDesignSpace().getDimension();
		int rj = RandomGenerator.intRangeRandom(0, DIMENSION - 1);
		for (int k = 0; k < DIMENSION; k++) {
			if (Math.random() < CR || k == DIMENSION - 1) {
				double Dabcd = 0;
				for (int i = 0; i < referPoints.length; i++) {
					Dabcd += Math.pow(-1, i % 2) * referPoints[i].getLocation()[rj];
				}
				trailPoint.getLocation()[rj] = gbest_t.getLocation()[rj] + FACTOR * Dabcd;
			} else {
				trailPoint.getLocation()[rj] = pbest_t.getLocation()[rj];
			}
			rj = (rj + 1) % DIMENSION;
		}
	}

	public static void main(String[] args) {
		final long NUMBER_OF_EXPERIMENTS = 100_000L;

		Main app = new Main();

		/* First algorithm. */ {
			long start = System.currentTimeMillis();
			SearchPoint point = new SearchPoint();
			ProblemEncoder encoder = new ProblemEncoder();
			System.out.println(point);
			app.generateBehavior(point, encoder);
			System.out.println(point);
			for (long g = 0; g < NUMBER_OF_EXPERIMENTS; g++) {
				app.generateBehavior(point, encoder);
			}
			long stop = System.currentTimeMillis();
			System.out.println(stop - start);
		}

		/* Second algorithm. */ {
			long start = System.currentTimeMillis();
			SearchPoint point = new SearchPoint();
			ProblemEncoder encoder = new ProblemEncoder();
			System.out.println(point);
			app.generateBehavior(point, encoder);
			System.out.println(point);
			for (long g = 0; g < NUMBER_OF_EXPERIMENTS; g++) {
				app.generateBehavior2(point, encoder);
			}
			long stop = System.currentTimeMillis();
			System.out.println(stop - start);
		}
	}
}
