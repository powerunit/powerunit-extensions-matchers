package ch.powerunit.extensions.matchers.samples.third;

import ch.powerunit.Test;
import ch.powerunit.TestSuite;
import ch.powerunit.extensions.matchers.samples.Pojo1;

public class OneFunnyOneMatchersTest implements TestSuite {

	@Test
	public void testMatcherWithSubMatcherDSL() {
		OneFunnyOne obj = new OneFunnyOne();
		obj.onePojo1 = new Pojo1();
		obj.onePojo1.msg2 = "12";
		assertThat(obj).is(OneFunnyOneMatchers.oneFunnyOneWith().onePojo1With().msg2("12").end());

	}
}
