package wildinter.net.mergesort;

import junit.framework.TestCase;

import static wildinter.net.mergesort.HarnessSort.harness;

/**
 * @author Sebastian Wild (wild@uwaterloo.ca)
 */
public class PeekSortTest extends TestCase {
	public void testPeeksort() throws Exception {
		harness(new PeekSort(1, false));
		harness(new PeekSort(1, true));
	}

}
