package wildinter.net.mergesort;


import java.util.Arrays;
import java.util.Random;

import static wildinter.net.mergesort.MergesAndRuns.*;

/**
 * Implementation of peeksort as described in the paper.
 *
 * If the subproblem has size at most insertionsortThreshold,
 * it is sorted by straight insertion sort instead of merging.
 * If onlyIncreasingRuns is true, we only find weakly increasing runs
 * while peeking into the middle. That simplifies run detection a bit,
 * but it does not detect descending runs.
 *
 * @author Sebastian Wild (wild@uwaterloo.ca)
 */
public class PeekSort implements Sorter {

	private final int myInsertionsortThreshold;
	private final boolean onlyIncreasingRuns;

	public PeekSort(final int insertionSortThreshold, final boolean onlyIncreasingRuns) {
		this.myInsertionsortThreshold = insertionSortThreshold;
		this.onlyIncreasingRuns = onlyIncreasingRuns;
	}

	@Override
	public void sort(final int[] A, final int left, final int right) {
		insertionSortThreshold = myInsertionsortThreshold;
		if (onlyIncreasingRuns)
			peeksortOnlyIncreasing(A, left, right);
		else
			peeksort(A, left, right);
	}

	public static void peeksort(final int[] a, final int l, final int r) {
		int n = r - l + 1;
		peeksort(a, l, r, l, r, new int[n]);
	}

	public static void peeksortOnlyIncreasing(final int[] a, final int l, final int r) {
		int n = r - l + 1;
		peeksortOnlyIncreasing(a, l, r, l, r, new int[n]);
	}

	private static int insertionSortThreshold = 10;

	public static void peeksort(int[] A, int left, int right, int leftRunEnd, int rightRunStart, final int[] B) {
		if (leftRunEnd == right || rightRunStart == left) return;
		if (right - left + 1 <= insertionSortThreshold) {
			// Possible optimization: use insertionsortRight of right run longer.
			Insertionsort.insertionsort(A, left, right, leftRunEnd - left + 1);
			return;
		}
		int mid = left + ((right - left) >> 1);
		if (mid <= leftRunEnd) {
			// |XXXXXXXX|XX     X|
			peeksort(A, leftRunEnd+1, right, leftRunEnd+1,rightRunStart, B);
			mergeRuns(A, left, leftRunEnd+1, right, B);
		} else if (mid >= rightRunStart) {
			// |XX     X|XXXXXXXX|
			peeksort(A, left, rightRunStart-1, leftRunEnd, rightRunStart-1, B);
			mergeRuns(A, left, rightRunStart, right, B);
		} else {
			// find middle run
			final int i, j;
			if (A[mid] <= A[mid+1]) {
				i = extendWeaklyIncreasingRunLeft(A, mid, leftRunEnd + 1);
				j = mid+1 == rightRunStart ? mid : extendWeaklyIncreasingRunRight(A, mid+1, rightRunStart - 1);
			} else {
				i = extendStrictlyDecreasingRunLeft(A, mid, leftRunEnd + 1);
				j = mid+1 == rightRunStart ? mid : extendStrictlyDecreasingRunRight(A, mid+1,rightRunStart - 1);
				reverseRange(A, i, j);
			}
			if (i == left && j == right) return;
			if (mid - i < j - mid) {
				// |XX     x|xxxx   X|
				peeksort(A, left, i-1, leftRunEnd, i-1, B);
				peeksort(A, i, right, j, rightRunStart, B);
				mergeRuns(A,left, i, right, B);
			} else {
				// |XX   xxx|x      X|
				peeksort(A, left, j, leftRunEnd, i, B);
				peeksort(A, j+1, right, j+1, rightRunStart, B);
				mergeRuns(A,left, j+1, right, B);
			}
		}
	}

	public static void peeksortOnlyIncreasing(int[] A, int left, int right, int leftRunEnd, int rightRunStart, final int[] B) {
		if (leftRunEnd == right || rightRunStart == left) return;
		if (right - left + 1 <= insertionSortThreshold) {
			Insertionsort.insertionsort(A, left, right);
			return;
		}
		int mid = left + ((right - left) >> 1);
		if (mid <= leftRunEnd) {
			// |XXXXXXXX|XX     X|
			peeksortOnlyIncreasing(A, leftRunEnd+1, right, leftRunEnd+1,rightRunStart, B);
			mergeRuns(A, left, leftRunEnd+1, right, B);
		} else if (mid >= rightRunStart) {
			// |XX     X|XXXXXXXX|
			peeksortOnlyIncreasing(A, left, rightRunStart-1, leftRunEnd, rightRunStart-1, B);
			mergeRuns(A, left, rightRunStart, right, B);
		} else {
			// find middle run
			int i = extendWeaklyIncreasingRunLeft(A, mid, leftRunEnd+1);
			int j = extendWeaklyIncreasingRunRight(A, mid, rightRunStart-1) ;
			if (i == left && j == right) return;
			if (mid - i < j - mid) {
				// |XX     x|xxxx   X|
				peeksortOnlyIncreasing(A, left, i-1, leftRunEnd, i-1, B);
				peeksortOnlyIncreasing(A, i, right, j, rightRunStart, B);
				mergeRuns(A,left, i, right, B);
			} else {
				// |XX   xxx|x      X|
				peeksortOnlyIncreasing(A, left, j, leftRunEnd, i, B);
				peeksortOnlyIncreasing(A, j+1, right, j+1, rightRunStart, B);
				mergeRuns(A,left, j+1, right, B);
			}
		}
	}

	public static void main(String[] args) {
		int[] A = Inputs.randomPermutation(30, new Random());
		A = new int[] {2, 5, 8, 4, 3, 10, 12, 13, 11, 6, 7, 1, 9};
		System.out.println(Arrays.toString(A));
		insertionSortThreshold = 1;
		peeksort(A, 0, A.length-1);

//		System.exit(1);

	}

	@Override
	public String toString() {
		return getClass().getSimpleName()
				+ "+iscutoff=" + myInsertionsortThreshold
				+ "+onlyIncRuns=" + onlyIncreasingRuns;
	}
}
