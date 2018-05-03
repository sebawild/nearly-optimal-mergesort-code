package wildinter.net.mergesort;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Random;

import static wildinter.net.mergesort.MergesAndRuns.overwriteWithRandomRunsWithOffset;
import static wildinter.net.mergesort.Util.shuffle;

/**
 * @author Sebastian Wild (wild@uwaterloo.ca)
 */
public class Inputs {

	public static InputGenerator RANDOM_PERMUTATIONS_WITH_SENTINEL = new InputGenerator() {
		@Override
		public int[] newInstance(final int n, final Random random) {
			return randomPermutation(n, random);
		}

		@Override
		public int[] reuseInstance(final int n, final Random random, final int[] A) {
			shuffle(A, 0, n - 1, random);
			return A;
		}

   		@Override public String toString() { return "random-permutations"; }
   	};
	private static LinkedList<Integer> RTimCache = null;
	private static int RTimCacheN = -1;

	public static int[] randomRunsWithOffsets(int n, int expRunLen, Random random) {
		int[] A = new int[n+1];
		overwriteWithRandomRunsWithOffset(A, n, expRunLen, random);
		return A;
	}

	public static int[] randomRuns(int n, int expRunLen, Random random) {
		int[] A = randomPermutation(n, random);
		sortRandomRuns(A, 0, n-1, expRunLen, random);
		return A;
	}

	public static void sortRandomRuns(final int[] A, final int left, int right, final int expRunLen, final Random random) {
		for (int i = left; i < right;) {
			int j = 1;
			while (random.nextInt(expRunLen) != 0) ++j;
			j = Math.min(right,i+j);
			Arrays.sort(A, i, j+1);
			i = j+1;
		}
	}

	public static int[] rankSpaceReduce(int[] A) {
		int[] r = new int[A.length];
		for (int i = 0; i < A.length; ++i) {
			for (int x : A) if (x < A[i]) ++r[i];
		}
		return r;
	}

	public static int[] timsortDrag(int n, int minRunLen, Random random) {
		int[] res = randomPermutation(n, random);
		permuteToTimsortDrag(res, 0, n / minRunLen, minRunLen, random);
		return res;
	}

	// Note: Buss and Kolp 2018 use runs of length >= 1, but
	// Timsort detects runs of a minimal length.
	// Even without explicit extension of runs, we always
	// have runs of length >= 2 since descending is also allowed.
	public static void permuteToTimsortDrag(int[] A, int left, int n, int minRunLen, Random random) {
		int N = n * minRunLen;
		if (n <= 3) {
			Arrays.sort(A, left, left + N);
			MergesAndRuns.reverseRange(A, left, left + N-1);
			makeNextBigger(A, left + N, random);
		} else {
			int nPrime = n/2;
			int nPrimePrime = n - nPrime - (nPrime-1);
			permuteToTimsortDrag(A, left, nPrime, minRunLen, random);
			permuteToTimsortDrag(A, left + nPrime * minRunLen,
					nPrime-1, minRunLen, random);
			permuteToTimsortDrag(A, left + (nPrime + nPrime - 1) * minRunLen,
					nPrimePrime, minRunLen, random);
		}
	}

	private static void makeNextSmaller(final int[] A, final int i, Random random) {
		if (i < A.length) A[i] = A[i-1] - 1 - random.nextInt(A[i-1]);
	}

	private static void makeNextBigger(final int[] A, final int i, Random random) {
		if (i < A.length) A[i] = A[i-1] + 1 + random.nextInt(A[i-1]);
	}

	public static LinkedList<Integer> timsortDragRunlengths(int n) {
		LinkedList<Integer> res;
		if (n <= 3) {
			res = new LinkedList<Integer>();
			res.add(n);
		} else {
			int nPrime = n/2;
			int nPrimePrime = n - nPrime - (nPrime-1);
			res = timsortDragRunlengths(nPrime);
			res.addAll(timsortDragRunlengths(nPrime-1));
			res.add(nPrimePrime);
		}
		return res;
	}

	public static void fillWithTimsortDrag(int[] A, int minRunLen, Random random) {
		int N = A.length-1;
		int n = N / minRunLen;
		if (RTimCacheN != n || RTimCache == null) {
			RTimCacheN = n;
			RTimCache = timsortDragRunlengths(n);
		}
		LinkedList<Integer> RTim = RTimCache;
//		System.out.println(RTim);
		A[0] = Integer.MIN_VALUE;
		for (int i = 1; i < N; ++i) A[i] = i;
		shuffle(A, 1, N, random);
		boolean reverse = false;
		int i = 1;
		for (int l : RTim) {
			int L = l * minRunLen;
			Arrays.sort(A, i-1, i+L);
			if (reverse) MergesAndRuns.reverseRange(A, i-1, i+L-1);
			reverse = !reverse;
			i += L;
		}
	}

	public static int[] randomPermutation(final int len, Random random) {
		int[] res = new int[len];
		for (int i = 1; i <= len; ++i) res[i - 1] = i;
		for (int i = len; i > 1; i--)
			Util.swap(res, i - 1, random.nextInt(i));
		return res;
	}

	public static int[] randomBinaryArray(final int len, Random random) {
		int res[] = new int[len];
		for (int i = 0; i < res.length; i++) {
			res[i] = random.nextInt(2);
		}
		return res;
	}

	public static int[] randomTernaryArray(final int len, Random random) {
		int res[] = new int[len];
		for (int i = 0; i < res.length; i++) {
			res[i] = random.nextInt(3);
		}
		return res;
	}

	public static int[] randomUaryArray(final int u, final int len, Random random) {
		int res[] = new int[len];
		for (int i = 0; i < res.length; i++) {
			res[i] = random.nextInt(u)+1;
		}
		return res;
	}

	public static double[] intArray2doubleArray(final int[] a) {
		final double[] copy = new double[a.length];
		for (int i = 0; i < a.length; copy[i] = a[i++]) ;
		return copy;
	}

	public interface InputGenerator {
		/**
		 * Generate next (random) input. If A == null, creates a new
		 * array of the given length n. If A != null
		 */
		default int[] next(int n, Random random, int[] A) {
			return A == null || A.length < n ?
					newInstance(n, random) :
					reuseInstance(n, random, A);
		}

		int[] newInstance(int n, Random random);

		default int[] reuseInstance(int n, Random random, int[] A) {
			return newInstance(n, random);
		}
	}

	public static InputGenerator randomRunsGenerator(final int runLen) {
		return new InputGenerator() {

			@Override
			public int[] newInstance(final int n, final Random random) {
				return randomRuns(n, runLen, random);
			}

			@Override
			public int[] reuseInstance(final int n, final Random random, final int[] A) {
				shuffle(A, 1, n, random);
				sortRandomRuns(A, 1, n, runLen, random);
				return A;
			}

			@Override public String toString() { return "runs-with-exp-len-" + runLen; }
		};
	}

	public static InputGenerator timsortDrag(final int minRunLen) {
		return new InputGenerator() {

			@Override
			public int[] newInstance(final int n, final Random random) {
				int[] A = new int[n+1];
				reuseInstance(n, random, A);
				return A;
			}

			@Override
			public int[] reuseInstance(final int n, final Random random, final int[] A) {
				fillWithTimsortDrag(A, minRunLen, random);
				return A;
			}

			@Override public String toString() { return "timsort-drag-minRunLen-" + minRunLen; }
		};
	}

	public static InputGenerator randomIidInts(final int max) {
		return new InputGenerator() {

			@Override
			public int[] newInstance(final int n, final Random random) {
				return randomUaryArray(max, n, random);
			}

			@Override
			public int[] reuseInstance(final int n, final Random random, final int[] A) {
				for (int i = 1; i < A.length; i++)
					A[i] = random.nextInt(max) + 1;
				return A;
			}

			@Override
			public String toString() { return "iid-max-"+max; }
		};
	}

	public static InputGenerator randomRunsWithSentinelInputDelta(final int runLen) {
		return new InputGenerator() {

			@Override
			public int[] newInstance(final int n, final Random random) {
				return randomRunsWithOffsets(n, runLen, random);
			}

			@Override
			public int[] reuseInstance(final int n, final Random random, final int[] A) {
				overwriteWithRandomRunsWithOffset(A, n, runLen, random);
				return A;
			}

			@Override public String toString() { return "runs-delta-with-exp-len-" + runLen; }
		};
	}
}
