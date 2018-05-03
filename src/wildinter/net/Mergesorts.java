package wildinter.net;

import wildinter.net.mergesort.BottomUpMergesort;
import wildinter.net.mergesort.Inputs;
import wildinter.net.mergesort.MergesAndRuns;
import wildinter.net.mergesort.PeekSort;
import wildinter.net.mergesort.PowerSort;
import wildinter.net.mergesort.Sorter;
import wildinter.net.mergesort.Timsort;
import wildinter.net.mergesort.TimsortStrippedDown;
import wildinter.net.mergesort.TimsortTrot;
import wildinter.net.mergesort.TopDownMergesort;
import wildinter.net.mergesort.Util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import static wildinter.net.mergesort.Util.shuffle;

/**
 * Main class for running time experiments.
 */
public class Mergesorts {

    public static boolean ABORT_IF_RESULT_IS_NOT_SORTED = true;
   	public static final boolean TIME_ALL_RUNS_IN_ONE_MEASUREMENT = false;

   	public static void main(String[] args) throws IOException {
   		if (args.length == 0) {
   			System.out.println("Usage: Mergesorts [reps] [n1,n2,n3] [seed] [inputs] [outfile]");
   		}

   		List<Sorter> algos = new ArrayList<>();
   		algos.add(new PowerSort(true, false, 24));
   		algos.add(new PeekSort(24, false));
   		algos.add(new PowerSort(true, false, 2));
   		algos.add(new PeekSort(2, false));
   		algos.add(new TopDownMergesort(24, true));
   		algos.add(new TopDownMergesort(2, true));
   		algos.add(TimsortTrot.INSTANCE);
   		algos.add(new BottomUpMergesort(24, true));
   		algos.add(new BottomUpMergesort(2, true));
   		algos.add(TimsortStrippedDown.INSTANCE);
   		algos.add(Timsort.INSTANCE);
   		algos.add(Sorter.SYSTEMSORT);
	    algos.add(new Nop());
	    algos.add(new Shuffle());

   		int reps = 100;

   		if (args.length >= 1) {
   			reps = Integer.parseInt(args[0]);
   		}

   		List<Integer> sizes = Arrays.asList(1_000_000);
   		if (args.length >= 2) {
   			sizes = new LinkedList<>();
   			for (final String size : args[1].split(",")) {
   				sizes.add(Integer.parseInt(size.replaceAll("\\D","")));
   			}
   		}

   		long seed = 42424242;
   		if (args.length >= 3) {
   			seed = Long.parseLong(args[2]);
   		}

   		Inputs.InputGenerator inputs = Inputs.RANDOM_PERMUTATIONS_WITH_SENTINEL;
   		if (args.length >= 4) {
   			if (args[3].equalsIgnoreCase("rp"))
   				inputs = Inputs.RANDOM_PERMUTATIONS_WITH_SENTINEL;
   			if (args[3].startsWith("runs"))
   				inputs = Inputs.randomRunsGenerator(Integer.parseInt(
   						args[3].substring(4).replaceAll("\\D","")));
   			if (args[3].startsWith("runsdelta"))
   				inputs = Inputs.randomRunsWithSentinelInputDelta(Integer.parseInt(args[3].substring(9)));
   			if (args[3].startsWith("iid"))
   				inputs = Inputs.randomIidInts(Integer.parseInt(args[3].substring(3)));
   			if (args[3].startsWith("killtim"))
   				inputs = Inputs.timsortDrag(Integer.parseInt(args[3].substring(7)));
   		}

   		String outFileName = "sorting";
   		if (args.length >= 5) outFileName = args[4];

   		timeSorts(algos, reps, sizes, seed, inputs, outFileName);
   	}

   	public static void timeSorts(final List<Sorter> algos, final int reps, final List<Integer> sizes, final long seed, final Inputs.InputGenerator inputs, String outFileName) throws IOException {
   		SimpleDateFormat format = new SimpleDateFormat("-yyyy-MM-dd_HH-mm-ss");
   		outFileName += format.format(new Date());
   		outFileName += "-reps" + reps;
   		outFileName += "-ns";
   		for (int n : sizes) outFileName += "-" + n;
   		outFileName += "-seed" + seed;
   		outFileName += ".csv";
   		File outFile = new File(outFileName);

   		System.out.println("algos = " + algos);
   		System.out.println("sizes = " + sizes);
   		System.out.println("reps  = " + reps);
   		System.out.println("seed  = " + seed);
   		System.out.println("inputs = " + inputs);
   		System.out.println("Writing to " + outFile.getAbsolutePath());

   		BufferedWriter out = new BufferedWriter(new FileWriter(outFile));

   		if (MergesAndRuns.COUNT_MERGE_COSTS) {
   			out.write("algo,ms,n,input,input-num,merge-cost\n");
   			System.out.println("Also counting merge cost in MergeUtil.mergeRuns");
   		} else {
   			out.write("algo,ms,n,input,input-num\n");
   			System.out.println("Not counting merge costs.");
   		}

   		int warmupRounds = 12_000;
   		System.out.println("Doing warmup (" + warmupRounds + " rounds)");
   		// warm up
   		Random random = new Random(seed);
   		for (int r = 0; r < warmupRounds; ++r) {
   			for (final Sorter algo : algos) {
   				for (final int size : new int[]{10000, 1000, 1000}) {
   					final int[] A = inputs.next(size, random, null);
   					algo.sort(A,0,size-1);
   				}
   			}
   		}
   		System.out.println("Warmup finished!\n");


   		System.out.println("\nRuns with individual timing (skips first run):");
   		for (final Sorter algo : algos) {
   			random = new Random(seed);
   			final String algoName = algo.toString();
   			for (final int size : sizes) {
   				final WelfordVariance samples = new WelfordVariance();
   				int total = 0;
   				int[] A = inputs.next(size, random, null);
   				for (int r = 0; r < reps; ++r) {
   					if (r != 0) A = inputs.next(size, random, A);
   					MergesAndRuns.totalMergeCosts = 0;
   					final long startNanos = System.nanoTime();
   					algo.sort(A, 0, size-1);
   					final long endNanos = System.nanoTime();
   					total += A[A.length/2];
   					if (ABORT_IF_RESULT_IS_NOT_SORTED && !Util.isSorted(A)) {
   						System.err.println("RESULT NOT SORTED!");
   						System.exit(3);
   					}
   					final double msDiff = (endNanos - startNanos) / 1e6;
   					if (r != 0) {
   						// Skip first iteration, often slower!
   						samples.addSample(msDiff);
   						if (MergesAndRuns.COUNT_MERGE_COSTS)
   							out.write(algoName+","+msDiff+","+size+","+inputs+","+r +","+ MergesAndRuns.totalMergeCosts+"\n");
   						else
   							out.write(algoName+","+msDiff+","+size+","+inputs+","+r + "\n");
   						out.flush();
   					}
   				}
   				System.out.println("avg-ms=" + (float) (samples.mean()) + ",\t algo=" + algoName + ", n=" + size + "     (" + total+")\t" + samples);
   			}
   		}
   		out.write("#finished: " + format.format(new Date()) + "\n");
   		out.close();

   		if (TIME_ALL_RUNS_IN_ONE_MEASUREMENT) {
   			System.out.println("\n\n\nRuns with overall timing (incl. input generation):");
   			for (final Sorter algo : algos) {
   				random = new Random(seed);
   				final String algoName = algo.toString();
   				for (final int size : sizes) {
   					int[] A = inputs.next(size, random, null);
   					final long startNanos = System.nanoTime();
   					int total = 0;
   					for (int r = 0; r < reps; ++r) {
   						if (r != 0) A = inputs.next(size, random, A);
   						algo.sort(A, 0, size - 1);
   						total += A[A.length / 2];
   //					if (!Util.isSorted(A)) throw new AssertionError();
   					}
   					final long endNanos = System.nanoTime();
   					final float msDiff = (endNanos - startNanos) / 1e6f;
   					System.out.println("avg-ms=" + (msDiff / reps) + ",\t algo=" + algoName + ", n=" + size + "    (" + total + ")");
   				}
   			}
   		}

   	}

	public static final class Nop implements Sorter {

   		@Override
   		public void sort(final int[] A, final int left, final int right) {
   			ABORT_IF_RESULT_IS_NOT_SORTED = false;
   		}

   		@Override
   		public String toString() {
   			return "nop";
   		}
   	}

   	private static class Shuffle implements Sorter {
   		@Override public void sort(final int[] A, final int left, final int right) {
   			ABORT_IF_RESULT_IS_NOT_SORTED = false;
   			shuffle(A, left, right, new Random());
   		}

   		@Override
   		public String toString() {
   			return "random-shuffle";
   		}

   	}
}
