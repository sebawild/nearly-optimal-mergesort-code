package wildinter.net.mergesort;

import java.util.Arrays;

/**
 * @author Sebastian Wild (wild@uwaterloo.ca)
 */
public interface Sorter {
	/** Sorts A[left..right] (both endpoints inclusive) */
	void sort(int[] A, int left, int right);

	default void sort(int[] A) {
		sort(A, 0, A.length - 1);
	}

	public static Sorter SYSTEMSORT = new Sorter() {
		@Override
		public void sort(final int[] A, final int left, final int right) {
			Arrays.sort(A, left, right+1);
		}

		@Override
		public String toString() {
			return "Arrays.sort(int[])";
		}
	};
}
