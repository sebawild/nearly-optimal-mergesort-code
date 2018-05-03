package wildinter.net.mergesort;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Random;

import static wildinter.net.mergesort.Inputs.randomPermutation;
import static wildinter.net.mergesort.Inputs.randomUaryArray;
import static wildinter.net.mergesort.Util.isOneUpToN;
import static wildinter.net.mergesort.Util.isSorted;

/**
 * @author Sebastian Wild (wild@uwaterloo.ca)
 */
public class HarnessSort {

	public static void harness(Sorter sort) {
		harnessRandomPermutations(sort);
		harnessUary(sort);
		harnessBigRandomPermutations(sort);
	}

	public static void harnessOnlySmall(Sorter sort) {
		harnessRandomPermutations(sort);
		harnessUary(sort);
	}


	public static void harnessRandomPermutations(Sorter sort) {
		System.setOut(new PrintStream(new OutputStream() {
			@Override public void write(final int b) throws IOException { }
		}));
		final Random random = new Random();
		for (int N = 3; N < 1000; N += 1) {
			for (int iter = 0; iter < 10; ++iter) {
				System.out.println(
					  "\n\n\n#############################################\n\n\n");
				final int[] original = randomPermutation(N, random);
				System.out.println("original = " + Arrays.toString(original));
				final int[] clone1 = original.clone();
				final int[] clone2 = original.clone();
				final int[] clone3 = original.clone();
				try {
					sort.sort(clone1, 0, N - 1);
					if (!isOneUpToN(clone1)) {
						System.err.println("ERROR in quicksortRuntime!");
						System.err.println("original input  = " + Arrays.toString(original));
						System.err.println("'quicksortRuntime' output = " + Arrays.toString(
							  clone1));
						System.exit(1);
					}
				} catch (UnsupportedOperationException e) {
					// skip unsupported operations ...
				} catch (RuntimeException e) {
					System.err.println("EXCEPTION!");
					System.err.println("original input  = " + Arrays.toString(original));
					throw e;
				}
			}
		}
	}

	public static void harnessBigRandomPermutations(Sorter sort) {
		System.setOut(new PrintStream(new OutputStream() {
			@Override public void write(final int b) throws IOException { }
		}));
		final Random random = new Random();
		int N = 1_000_000;
		for (int iter = 0; iter < 10; ++iter) {
			System.out.println(
					"\n\n\n#############################################\n\n\n");
			final int[] original = randomPermutation(N, random);
			System.out.println("original = " + Arrays.toString(original));
			final int[] clone1 = original.clone();
			final int[] clone2 = original.clone();
			final int[] clone3 = original.clone();
			try {
				sort.sort(clone1, 0, N - 1);
				if (!isOneUpToN(clone1)) {
					System.err.println("ERROR in quicksortRuntime!");
					System.err.println("original input  = " + Arrays.toString(original));
					System.err.println("'quicksortRuntime' output = " + Arrays.toString(
							clone1));
					System.exit(1);
				}
			} catch (UnsupportedOperationException e) {
				// skip unsupported operations ...
			} catch (RuntimeException e) {
				System.err.println("EXCEPTION!");
				System.err.println("original input  = " + Arrays.toString(original));
				throw e;
			}
		}

	}


	public static void harnessUary(Sorter sort) {
		System.setOut(new PrintStream(new OutputStream() {
			@Override public void write(final int b) throws IOException { }
		}));
		final Random random = new Random();
		for (int N = 1; N < 1000; N += 1) {
			for (int iter = 0; iter < 10; ++iter) {
				int u = random.nextInt(7)+2;
				System.out.println(
					  "\n\n\n#############################################\n\n\n");
				final int[] a = randomUaryArray(u, N, random);
				System.out.println("a = " + Arrays.toString(a));
				final int[] clone = a.clone();
				try {
					sort.sort(a, 0, N - 1);
				} catch (RuntimeException e) {
					System.err.println("ERROR!");
					e.printStackTrace(System.err);
					System.err.println("input  = " + Arrays.toString(clone));
					System.exit(1);
				}
				System.out.println(Arrays.toString(a));
				if (!isSorted(a)) {
					System.err.println("ERROR!");
					System.err.println("original input  = " + Arrays.toString(clone));
					System.err.println("'sorted' output = " + Arrays.toString(a));
					System.exit(1);
				}
			}
		}
	}

}
