package wildinter.net.mergesort;


import java.util.Arrays;
import java.util.Random;

import static java.lang.Math.min;
import static wildinter.net.mergesort.MergesAndRuns.mergeRuns;

/**
 * Simple bottom-up mergesort implementation.
 * Merging starts after forming runs of length minRunLen.
 * If doSortedCheck is true, we check if two runs are by chance already
 * in sorted order before two runs are merged (compare last of left run with
 * first of right run)
 *
 * @author Sebastian Wild (wild@uwaterloo.ca)
 */
public class BottomUpMergesort implements Sorter {

	public BottomUpMergesort(final int minRunLen, final boolean doSortedCheck) {
		this.myMinRunLen = minRunLen;
		this.doSortedCheck = doSortedCheck;
	}

	@Override
	public void sort(final int[] A, final int left, final int right) {
		minRunLen = myMinRunLen;
		if(doSortedCheck)
			mergesortCheckSorted(A, left, right);
		else
			mergesort(A, left, right);
	}

	private final int myMinRunLen;
	private final boolean doSortedCheck;

	private static int minRunLen = 24;

	public static void mergesort(int[] A, int left, int right) {
		int n = right - left + 1;
		int[] B = new int[n];
		if (minRunLen != 1)
			for (int len = minRunLen, i = left; i <= right; i += len)
				Insertionsort.insertionsort(A, i, min(i + len-1, right));
		for (int len = minRunLen; len < n; len *= 2)
			for (int i = left; i <= right - len; i += len + len)
				mergeRuns(A, i, i + len, min(i + len + len - 1, right), B);
	}

	public static void mergesortCheckSorted(int[] A, int left, int right) {
		int n = right - left + 1;
		int[] B = new int[n];
		if (minRunLen != 1)
			for (int len = minRunLen, i = left; i <= right; i += len)
				Insertionsort.insertionsort(A, i, min(i + len-1, right));
		for (int len = minRunLen; len < n; len *= 2)
			for (int i = left; i <= right - len; i += len + len)
				if (A[i+len-1] > A[i+len])
					mergeRuns(A, i, i + len, min(i + len + len - 1, right), B);
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "+minRunLen=" + myMinRunLen + "+checkSorted="+doSortedCheck;
	}

	public static void main(String[] args) {
		int[] A = Inputs.randomPermutation(30, new Random());
//		A = new int[] {3, 7, 1, 2, 6, 9, 5, 8, 4};
		System.out.println(Arrays.toString(A));
		mergesort(A, 0, A.length-1);
		System.out.println(Arrays.toString(A));
	}
}
