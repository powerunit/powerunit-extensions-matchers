package ch.powerunit.extensions.matchers.samples.extensions.hamcrestdate;

import java.time.LocalTime;

import ch.powerunit.Test;
import ch.powerunit.TestSuite;

public class SampleLocalTimeMatchersTest implements TestSuite {
	@Test
	public void testAfter() {
		SampleLocalTime sld = new SampleLocalTime();
		sld.setOpt(LocalTime.of(8, 1));
		assertThat(sld).is(SampleLocalTimeMatchers.sampleLocalTimeWith().optAfter(LocalTime.of(7, 1)));
	}
}
