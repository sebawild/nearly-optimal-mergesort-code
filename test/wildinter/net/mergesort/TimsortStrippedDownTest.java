package wildinter.net.mergesort;

import junit.framework.TestCase;

import static wildinter.net.mergesort.HarnessSort.harness;

/**
 * @author Sebastian Wild (wild@uwaterloo.ca)
 */
public class TimsortStrippedDownTest extends TestCase {
	public void testSort() throws Exception {
		harness(TimsortStrippedDown.INSTANCE);
	}

}
