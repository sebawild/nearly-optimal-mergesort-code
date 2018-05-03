package wildinter.net.mergesort;

import junit.framework.TestCase;

import static wildinter.net.mergesort.HarnessSort.harness;

/**
 * @author Sebastian Wild (wild@uwaterloo.ca)
 */
public class TimsortTrotTest extends TestCase {
	public void testSort() throws Exception {
//		TimsortTrot.USE_BINARY_INSERTIONSORT = true;
//		harness(TimsortTrot.INSTANCE);
		TimsortTrot.USE_BINARY_INSERTIONSORT = false;
		harness(TimsortTrot.INSTANCE);
	}

}
