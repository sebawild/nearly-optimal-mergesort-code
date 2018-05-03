package wildinter.net.mergesort;

import static wildinter.net.mergesort.MergesAndRuns.mergeRuns;

/**
 * Simple top-down mergesort implementation.
 *
 * Recursion is stopped at subproblems of sizes at most insertionsortThreshold;
 * those are sorted by straight insertion sort.
 * If doSortedCheck is true, we check if two runs are by chance already
 * in sorted order before two runs are merged (compare last of left run with
 * first of right run).
 *
 * @author Sebastian Wild (wild@uwaterloo.ca)
 */
public class TopDownMergesort implements Sorter {

	private final int myInsertionsortThreshold;
	private final boolean doSortedCheck;

	public TopDownMergesort(final int insertionsortThreshold, final boolean doSortedCheck) {
		this.myInsertionsortThreshold = insertionsortThreshold;
		this.doSortedCheck = doSortedCheck;
	}

	@Override
	public void sort(final int[] A, final int left, final int right) {
		insertionsortThreshold = myInsertionsortThreshold;
		int[] buffer = new int[(right - left + 1)];
		if (doSortedCheck)
			mergesortCheckSorted(A, left, right, buffer);
		else
			mergesort(A, left, right, buffer);
	}

	private static int insertionsortThreshold = 24;

	public static void mergesortCheckSorted(int[] A, int left, int right, final int[] buffer) {
		int n = right - left + 1;
		if (n <= insertionsortThreshold) {
			Insertionsort.insertionsort(A, left, right);
			return;
		}
		int m = left + (n >> 1);
		mergesortCheckSorted(A, left, m-1, buffer);
		mergesortCheckSorted(A, m, right, buffer);
		if (A[m-1] > A[m])
			mergeRuns(A,left, m, right, buffer);
	}

	public static void mergesort(int[] A, int left, int right, final int[] buffer) {
		int n = right - left + 1;
		if (n <= insertionsortThreshold) {
			Insertionsort.insertionsort(A, left, right);
			return;
		}
		int m = left + (n >> 1);
		mergesort(A, left, m-1, buffer);
		mergesort(A, m, right, buffer);
		mergeRuns(A,left, m, right, buffer);
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "+iscutoff="+ myInsertionsortThreshold + "+checkSorted=" + doSortedCheck;
	}

	public static void main(String[] args) {
	}
}
