package ch.powerunit.extensions.matchers.samples.extensions.hamcrestutility;

import java.util.ArrayList;

import ch.powerunit.Test;
import ch.powerunit.TestSuite;

public class HamcrestUtililtyPojoMatchersTest implements TestSuite {

	@Test
	public void testHamcrestUtility() {
		HamcrestUtililtyPojo pojo = new HamcrestUtililtyPojo();
		pojo.myList = new ArrayList<>();
		assertThat(pojo).is(HamcrestUtililtyPojoMatchers.hamcrestUtililtyPojoWith().myListHasNoDuplicates());
	}

}
