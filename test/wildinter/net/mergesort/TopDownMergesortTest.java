package wildinter.net.mergesort;

import junit.framework.TestCase;

import static wildinter.net.mergesort.HarnessSort.harness;

/**
 * @author Sebastian Wild (wild@uwaterloo.ca)
 */
public class TopDownMergesortTest extends TestCase {
	public void testSort() throws Exception {
		harness(new TopDownMergesort(1, true));
		harness(new TopDownMergesort(1, false));
	}
}
