package wildinter.net.mergesort;


/**
 * Timsort without minRunLen and galloping mode in merge.
 *
 * @author Sebastian Wild (wild@uwaterloo.ca)
 */
public class TimsortStrippedDown {
	public static final Sorter INSTANCE = new Sorter() {
		@Override
		public void sort(final int[] A, final int left, final int right) {
			TimsortStrippedDown.sort(A, left, right +1);
		}
        @Override
       	public String toString() {
            return TimsortStrippedDown.class.getSimpleName();
        }
	};


    /**
     * The array being sorted.
     */
    private final int[] a;


    /**
     * A stack of pending runs yet to be merged.  Run i starts at
     * address base[i] and extends for len[i] elements.  It's always
     * true (so long as the indices are in bounds) that:
     *
     *     runBase[i] + runLen[i] == runBase[i + 1]
     *
     * so we could cut the storage for this, but it's a minor amount,
     * and keeping all the info explicit simplifies the code.
     */
    private int stackSize = 0;  // Number of pending runs on stack
    private final int[] runBase;
    private final int[] runLen;

    private final int[] buffer;


    /**
     * Creates a TimSort instance to maintain the state of an ongoing sort.
     *
     * @param a the array to be sorted
     */
    private TimsortStrippedDown(int[] a) {
        this.a = a;

        // Allocate temp storage (which may be increased later if necessary)
        int len = a.length;

        /*
         * Allocate runs-to-be-merged stack (which cannot be expanded).  The
         * stack length requirements are described in listsort.txt.  The C
         * version always uses the same stack length (85), but this was
         * measured to be too expensive when sorting "mid-sized" arrays (e.g.,
         * 100 elements) in Java.  Therefore, we use smaller (but sufficiently
         * large) stack lengths for smaller arrays.  The "magic numbers" in the
         * computation below must be changed if MIN_MERGE is decreased.  See
         * the MIN_MERGE declaration above for more information.
         * The maximum value of 49 allows for an array up to length
         * Integer.MAX_VALUE-4, if array is filled by the worst case stack size
         * increasing scenario. More explanations are given in section 4 of:
         * http://envisage-project.eu/wp-content/uploads/2015/02/sorting.pdf
         */
        int stackLen = (len <    120  ?  5 :
                        len <   1542  ? 10 :
                        len < 119151  ? 24 : 49);
        stackLen *= 2; // NOTE conservatively increased this since we removed minRunLen!
        runBase = new int[stackLen];
        runLen = new int[stackLen];

        this.buffer = new int[len];
    }

    /*
     * The next method (package private and static) constitutes the
     * entire API of this class.
     */

    /**
     * Sorts the given range, using the given workspace array slice
     * for temp storage when possible. This method is designed to be
     * invoked from public methods (in class Arrays) after performing
     * any necessary array bounds checks and expanding parameters into
     * the required forms.
     *
     * @param a the array to be sorted
     * @param lo the index of the first element, inclusive, to be sorted
     * @param hi the index of the last element, exclusive, to be sorted
     * @since 1.8
     */
    static void sort(int[] a, int lo, int hi) {
        assert a != null && lo >= 0 && lo <= hi && hi <= a.length;

        int nRemaining  = hi - lo;
        if (nRemaining < 2)
            return;  // Arrays of size 0 and 1 are always sorted

        /*
         * March over the array once, left to right, finding natural runs,
         * extending short natural runs to minRun elements, and merging runs
         * to maintain stack invariant.
         */
        TimsortStrippedDown ts = new TimsortStrippedDown(a);
        do {
            // Identify next run
            int runLen = countRunAndMakeAscending(a, lo, hi);

//            System.out.println();
//            for (int i = 0; i < ts.stackSize; ++i) System.out.print(ts.runLen[i] + "  "); System.out.println();
            // Push run onto pending-run stack, and maybe merge
            ts.pushRun(lo, runLen);
//            for (int i = 0; i < ts.stackSize; ++i) System.out.print(ts.runLen[i] + "  "); System.out.println();
            ts.mergeCollapse();
//            for (int i = 0; i < ts.stackSize; ++i) System.out.print(ts.runLen[i] + "  "); System.out.println();

            // Advance to find next run
            lo += runLen;
            nRemaining -= runLen;
        } while (nRemaining != 0);

        // Merge all remaining runs to complete sort
        assert lo == hi;
        ts.mergeForceCollapse();
        assert ts.stackSize == 1;
    }

	/**
     * Returns the length of the run beginning at the specified position in
     * the specified array and reverses the run if it is descending (ensuring
     * that the run will always be ascending when the method returns).
     *
     * A run is the longest ascending sequence with:
     *
     *    a[lo] <= a[lo + 1] <= a[lo + 2] <= ...
     *
     * or the longest descending sequence with:
     *
     *    a[lo] >  a[lo + 1] >  a[lo + 2] >  ...
     *
     * For its intended use in a stable mergesort, the strictness of the
     * definition of "descending" is needed so that the call can safely
     * reverse a descending sequence without violating stability.
     *
     * @param a the array in which a run is to be counted and possibly reversed
     * @param lo index of the first element in the run
     * @param hi index after the last element that may be contained in the run.
              It is required that {@code lo < hi}.
     * @return  the length of the run beginning at the specified position in
     *          the specified array
     */
    private static int countRunAndMakeAscending(int[] a, int lo, int hi) {
        assert lo < hi;
        int runHi = lo + 1;
        if (runHi == hi)
            return 1;

        // Find end of run, and reverse range if descending
        if (a[runHi++] < a[lo]) { // Descending
            while (runHi < hi && a[runHi] < a[runHi - 1])
                runHi++;
            reverseRange(a, lo, runHi);
        } else {                              // Ascending
            while (runHi < hi && a[runHi] >= a[runHi - 1])
                runHi++;
        }

        return runHi - lo;
    }

    /**
     * Reverse the specified range of the specified array.
     *
     * @param a the array in which a range is to be reversed
     * @param lo the index of the first element in the range to be reversed
     * @param hi the index after the last element in the range to be reversed
     */
    private static void reverseRange(int[] a, int lo, int hi) {
        hi--;
        while (lo < hi) {
            int t = a[lo];
            a[lo++] = a[hi];
            a[hi--] = t;
        }
    }

	/**
     * Pushes the specified run onto the pending-run stack.
     *
     * @param runBase index of the first element in the run
     * @param runLen  the number of elements in the run
     */
    private void pushRun(int runBase, int runLen) {
        this.runBase[stackSize] = runBase;
        this.runLen[stackSize] = runLen;
        stackSize++;
    }

    /**
     * Examines the stack of runs waiting to be merged and merges adjacent runs
     * until the stack invariants are reestablished:
     *
     *     1. runLen[i - 3] > runLen[i - 2] + runLen[i - 1]
     *     2. runLen[i - 2] > runLen[i - 1]
     *
     * This method is called each time a new run is pushed onto the stack,
     * so the invariants are guaranteed to hold for i < stackSize upon
     * entry to the method.
     */
    private void mergeCollapse() {
        while (stackSize > 1) {
            int n = stackSize - 2;
            if (n > 0 && runLen[n-1] <= runLen[n] + runLen[n+1]) {
                if (runLen[n - 1] < runLen[n + 1])
                    n--;
                mergeAt(n);
            } else if (runLen[n] <= runLen[n + 1]) {
                mergeAt(n);
            } else {
                break; // Invariant is established
            }
        }
    }

    /**
     * Merges all runs on the stack until only one remains.  This method is
     * called once, to complete the sort.
     */
    private void mergeForceCollapse() {
        while (stackSize > 1) {
            int n = stackSize - 2;
            if (n > 0 && runLen[n - 1] < runLen[n + 1])
                n--;
            mergeAt(n);
        }
    }

    /**
     * Merges the two runs at stack indices i and i+1.  Run i must be
     * the penultimate or antepenultimate run on the stack.  In other words,
     * i must be equal to stackSize-2 or stackSize-3.
     *
     * @param i stack index of the first of the two runs to merge
     */
    private void mergeAt(int i) {
        assert stackSize >= 2;
        assert i >= 0;
        assert i == stackSize - 2 || i == stackSize - 3;

        int base1 = runBase[i];
        int len1 = runLen[i];
        int base2 = runBase[i + 1];
        int len2 = runLen[i + 1];
        assert len1 > 0 && len2 > 0;
        assert base1 + len1 == base2;

        /*
         * Record the length of the combined runs; if i is the 3rd-last
         * run now, also slide over the last run (which isn't involved
         * in this merge).  The current run (i+1) goes away in any case.
         */
        runLen[i] = len1 + len2;
        if (i == stackSize - 3) {
            runBase[i + 1] = runBase[i + 2];
            runLen[i + 1] = runLen[i + 2];
        }
        stackSize--;

//        /*
//         * Find where the first element of run2 goes in run1. Prior elements
//         * in run1 can be ignored (because they're already in place).
//         */
//        int k = gallopRight(a[base2], a, base1, len1, 0);
//        assert k >= 0;
//        base1 += k;
//        len1 -= k;
//        if (len1 == 0)
//            return;
//
//        /*
//         * Find where the last element of run1 goes in run2. Subsequent elements
//         * in run2 can be ignored (because they're already in place).
//         */
//        len2 = gallopLeft(a[base1 + len1 - 1], a, base2, len2, len2 - 1);
//        assert len2 >= 0;
//        if (len2 == 0)
//            return;

	    // Merge remaining runs, using tmp array with min(len1, len2) elements
	    MergesAndRuns.mergeRuns(this.a, base1, base2, base2 + len2 - 1, this.buffer);
//        if (len1 <= len2)
//            mergeLo(base1, len1, base2, len2);
//        else
//            mergeHi(base1, len1, base2, len2);
    }

	public static void main(String[] args) {

    	int[] A = new int[]{31, 25, 24, 22, 43, 41, 7, 27, 9, 19, 4, 1, 40, 26, 16, 36, 15, 28, 10, 8, 5, 14, 32, 13, 38, 42, 21, 35, 18, 17, 11, 23, 33, 37, 2, 12, 39, 6, 20, 30, 29, 44, 34, 3};
    	sort(A,0,A.length);
	}

}
