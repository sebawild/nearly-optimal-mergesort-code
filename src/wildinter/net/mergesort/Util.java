/*
 * This file is part of MaLiJAn-GUI.
 *
 * MaLiJAn-GUI is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MaLiJAn-GUI is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MaLiJAn-GUI.  If not, see <http://www.gnu.org/licenses/>.
 */

package wildinter.net.mergesort;


import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

/**
 * Miscellaneous utility functions.
 *
 * @author Sebastian Wild (wild@uwaterloo.ca)
 * */
public class Util {
	public static void swap(int[] a, int i, int j) {
		final int tmp = a[i];
		a[i] = a[j];
		a[j] = tmp;
	}


	public static void swap(int[] A, int i, int[] B, int j) {
		final int tmp = A[i];
		A[i] = B[j];
		B[j] = tmp;
	}


	public static void rot3(int[] a, int i, int j, int k) {
		final int tmp = a[i];
		a[i] = a[j];
		a[j] = a[k];
		a[k] = tmp;
	}

	public static boolean isOneUpToN(int[] a) {
		final int offset = a[0] == Integer.MIN_VALUE ? 1 : 0;
		for (int i = 0; i + offset < a.length; i++) {
			if (a[i + offset] != i + 1) return false;
		}
		return true;
	}

	public static boolean isSorted(int[] a) {
		for (int i = 0; i < a.length - 1; i++) {
			if (a[i + 1] < a[i]) return false;
		}
		return true;
	}


	public static void shuffle(final int[] A, final int left, final int right, final Random random) {
		int n = right - left + 1;
		for (int i = n; i > 1; i--)
			swap(A, left + i - 1, left + random.nextInt(i));
	}

	public static void printA(final int[] A, final int left, final int right) {
		int i = 0;
		System.out.print("[");
		for (final int ai : A) {
			if (i >= left && i <= right)
				if (ai == Integer.MIN_VALUE) {
					System.out.print(" -∞ ");
				} else {
					System.out.printf("%3d ",ai);
				}
			else {
				System.out.print("    ");
			}
			++i;
		}
		System.out.println("]");
	}

	public static void printPointers(final int[] A, final int left, final int right, int ... pointers) {

//		Map<Integer,Integer> points = new TreeMap<Integer, Integer>();
//		// Show number of pointers
//		for (final int pointer : pointers) {
//			final Integer old = points.get(pointer);
//			points.put(pointer,old == null ? 1 : old+1);
//		}
		Map<Integer,String> points = new TreeMap<>();
		for (int j = 0; j < pointers.length; ++j) {
			final int pointer = pointers[j];
			final String old = points.get(pointer);
			points.put(pointer, old == null ? ""+j : old + j);
		}
		int i = 0;
		System.out.print("[");
		for (final int ai : A) {
			if (i >= left && i <= right)
				if (!points.containsKey(i)) {
					System.out.print("    ");
				} else {
					System.out.printf("%3s ",points.get(i));
				}
			else {
				System.out.print("    ");
			}
			++i;
		}
		System.out.println("]");
	}
}
