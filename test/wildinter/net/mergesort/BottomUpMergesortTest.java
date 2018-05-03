package wildinter.net.mergesort;

import junit.framework.TestCase;

import static wildinter.net.mergesort.HarnessSort.harness;

/**
 * @author Sebastian Wild (wild@uwaterloo.ca)
 */
public class BottomUpMergesortTest extends TestCase {

	public void testSort() throws Exception {
		harness(new BottomUpMergesort(1, true));
		harness(new BottomUpMergesort(5, false));
	}
}
