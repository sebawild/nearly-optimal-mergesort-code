package wildinter.net.mergesort;

/**
 * Straight-insertion sort implementations
 * @author Sebastian Wild (wild@uwaterloo.ca)
 * */
public class Insertionsort {

	/** Sort A[left..right] by straight-insertion sort (both endpoints inclusive) */
	public static void insertionsort(int[] A, int left, int right) {
		for (int i = left + 1; i <= right; ++i) {
			int j = i-1; final int v = A[i];
			while (v < A[j]) {
				A[j+1] = A[j];	--j;
				if (j < left) break;
			}
			A[j+1] = v;
		}
	}

	/**
	 * Sort A[left..right] by straight-insertion sort (both endpoints
	 * inclusive), assuming the leftmost nPresorted elements form a weakly
	 * increasing run
	 */
	public static void insertionsort(int[] A, int left, int right, int nPresorted) {
		assert right >= left;
		assert right - left + 1 >= nPresorted;
		for (int i = left + nPresorted; i <= right ; ++i) {
			int j = i - 1;
			final int v = A[i];
			while (v < A[j]) {
				A[j+1] = A[j];
				--j;
				if (j < left) break;
			}
			A[j+1] = v;
		}
	}


	/**
	 * Sort A[left..right] by straight-insertion sort (both endpoints
	 * inclusive), assuming the rightmost nPresorted elements form a weakly
	 * increasing run
	 */
	public static void insertionsortRight(int[] A, int left, int right, int nPresorted) {
		assert right >= left;
		assert right - left + 1 >= nPresorted;
		for (int i = right - nPresorted; i >= left; --i) {
			int j = i + 1;
			final int v = A[i];
			while (v > A[j]) {
				A[j-1] = A[j];
				++j;
				if (j > right) break;
			}
			A[j-1] = v;
		}
	}

}
