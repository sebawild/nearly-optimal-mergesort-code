package wildinter.net.mergesort;

import java.util.Arrays;

/**
 * Merging procedures and some related helpers
 * @author Sebastian Wild (wild@uwaterloo.ca)
 */
public class MergesAndRuns {

	/** turns on the counting of merge costs */
	public static final boolean COUNT_MERGE_COSTS = true;
	/** total merge costs of all merge calls */
	public static long totalMergeCosts = 0;


	/**
	 * Merges runs A[l..m-1] and A[m..r] in-place into A[l..r]
	 * with Sedgewick's bitonic merge (Program 8.2 in Algorithms in C++)
	 * using B as temporary storage.
	 * B.length must be at least r+1.
	 */
	public static void mergeRuns(int[] A, int l, int m, int r, int[] B) {
		--m;// mismatch in convention with Sedgewick
		int i, j;
		assert B.length >= r+1;
		if (COUNT_MERGE_COSTS) totalMergeCosts += (r-l+1);
		for (i = m+1; i > l; --i) B[i-1] = A[i-1];
		for (j = m; j < r; ++j) B[r+m-j] = A[j+1];
		for (int k = l; k <= r; ++k)
			A[k] = B[j] < B[i] ? B[j--] : B[i++];
	}

	/**
	 * Merges runs A[l..m-1] and A[m..r] in-place into A[l..r]
	 * by copying the shorter run into temporary storage B and
	 * merging back into A.
	 * B.length must be at least min(m-l,r-m+1)
	 */
	public static void mergeRunsCopyHalf(int[] A, int l, int m, int r, int[] B) {
		int n1 = m-l, n2 = r-m+1;
		if (COUNT_MERGE_COSTS) totalMergeCosts += (n1+n2);
		assert B.length >= n1 || B.length >= n2;
		if (n1 <= n2) {
			System.arraycopy(A, l, B, 0, n1);
			int i1 = 0, i2 = m, o = l;
			while (i1 < n1 && i2 <= r)
				A[o++] = B[i1] <= A[i2] ? B[i1++] : A[i2++];
			while (i1 < n1) A[o++] = B[i1++];
		} else {
			System.arraycopy(A, m, B, 0, n2);
			int i1 = m-1, i2 = n2-1, o = r;
			while (i1 >= l && i2 >= 0)
				A[o--] = A[i1] <= B[i2] ? B[i2--] : A[i1--];
			while (i2 >= 0) A[o--] = B[i2--];
		}
	}

	/**
	 * Reverse the specified range of the specified array.
	 *
	 * @param a  the array in which a range is to be reversed
	 * @param lo the index of the first element in the range to be
	 *           reversed
	 * @param hi the index of the last element in the range to be
	 *           reversed
	 */
	public static void reverseRange(int[] a, int lo, int hi) {
		while (lo < hi) {
			int t = a[lo]; a[lo++] = a[hi]; a[hi--] = t;
		}
	}


	public static int extendWeaklyIncreasingRunLeft(final int[] A, int i, final int left) {
		while (i > left && A[i-1] <= A[i]) --i;
		return i;
	}

	public static int extendWeaklyIncreasingRunRight(final int[] A, int i, final int right) {
		while (i < right && A[i+1] >= A[i]) ++i;
		return i;
	}

	public static int extendStrictlyDecreasingRunLeft(final int[] A, int i, final int left) {
		while (i > left && A[i-1] > A[i]) --i;
		return i;
	}

	public static int extendStrictlyDecreasingRunRight(final int[] A, int i, final int right) {
		while (i < right && A[i+1] < A[i]) ++i;
		return i;
	}


	public static int extendAndReverseRunRight(final int[] A, int i, final int right) {
		assert i <= right;
		int j = i;
		if (j == right) return j;
		// Find end of run, and reverse range if descending
		if (A[j] > A[++j]) { // Strictly Descending
			while (j < right && A[j+1] < A[j]) ++j;
			reverseRange(A, i, j);
		} else { // Weakly Ascending
			while (j < right && A[j+1] >= A[j]) ++j;
		}
		return j;
	}

	public static int extendAndReverseRunLeft(final int[] A, final int j, final int left) {
		assert j >= left;
		int i = j;
		if (i == left) return i;
		// Find end of run, and reverse range if descending
		if (A[i] < A[--i]) { // Strictly Descending
			while (i > left && A[i-1] > A[i]) --i;
			reverseRange(A, i, j);
		} else { // Weakly Ascending
			while (i > left && A[i-1] <= A[i]) --i;
		}
		return i;
	}


	public static void main(String[] args) {

		//         0  1  2  3  4  5 6 7 8 9  10
		int[] A = {10,20,30,40,50,5,6,7,8,100,120} ;
		System.out.println(Arrays.toString(A));
		mergeRuns(A,0,5,10, new int[11]);
		System.out.println(Arrays.toString(A));

		//            0  1  2  3  4  5 6  7  8
		A = new int[]{10,20,30,40,50,15,15,25,28};
		System.out.println(Arrays.toString(A));
		mergeRunsCopyHalf(A,0,5,8, new int[4]);
		System.out.println(Arrays.toString(A));
	}
}
