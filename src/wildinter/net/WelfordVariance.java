package wildinter.net;

/**
 * Simple implementation of Welford's algorithm for
 * online-computation of the variance of a stream.
 *
 * see http://jonisalonen.com/2013/deriving-welfords-method-for-computing-variance/
 *
 * @author Sebastian Wild (wild@uwaterloo.ca)
 */
public class WelfordVariance {
	private int nSamples = 0;
	private double mean = 0, squaredError = 0;

	public void addSample(double x) {
		++nSamples;
		double oldMean = mean;
		mean += (x - mean) / nSamples;
		squaredError += (x - mean) * (x - oldMean);
	}

	public double mean() {
		return mean;
	}

	public int nSamples() {
		return nSamples;
	}

	public double variance() {
		return squaredError / (nSamples - 1);
	}

	public double stdev() {
		return Math.sqrt(variance());
	}

	@Override
	public String toString() {
		return "(" +
				"n=" + nSamples +
				", µ=" + (float) mean() +
				", σ=" + (float) stdev() +
				')';
	}

	public static void main(String[] args) {
		WelfordVariance v = new WelfordVariance();
		for (int i : new int[]{1, 2, 2, 2, 3, 3, 4, 4, 4, 4, 4, 5, 5, 6, 6, 7, 8, 89,10000,100001,00,101,}) {
			v.addSample(i);
		}
		System.out.println("v.mean() = " + v.mean());
		System.out.println("v.variance() = " + v.variance());
		System.out.println("v.stdev() = " + v.stdev());
	}

}
