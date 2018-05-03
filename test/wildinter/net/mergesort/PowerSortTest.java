package wildinter.net.mergesort;

import junit.framework.TestCase;

import static wildinter.net.mergesort.HarnessSort.harness;

/**
 * @author Sebastian Wild (wild@uwaterloo.ca)
 */
public class PowerSortTest extends TestCase {
	public void testPowersort() throws Exception {
		harness(new PowerSort(true, false, 16));
		harness(new PowerSort(true, true, 1));
		harness(new PowerSort(false, false, 1));
	}

}
