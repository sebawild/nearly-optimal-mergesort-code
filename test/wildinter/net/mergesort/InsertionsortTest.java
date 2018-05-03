package wildinter.net.mergesort;

import junit.framework.TestCase;

import static wildinter.net.mergesort.HarnessSort.harnessOnlySmall;

/**
 * @author Sebastian Wild (wild@uwaterloo.ca)
 */
public class InsertionsortTest extends TestCase {
	public void testInsertionsort() throws Exception {
		harnessOnlySmall(Insertionsort::insertionsort);
		harnessOnlySmall((A, left, right) ->
				Insertionsort.insertionsort(A, left, right, 1));
		harnessOnlySmall((A, left, right) ->
				Insertionsort.insertionsortRight(A, left, right, 1));

	}

}
