package ch.powerunit.extensions.matchers.samples.extensions.hamcrestdate;

import java.time.LocalDate;

import ch.powerunit.Test;
import ch.powerunit.TestSuite;

public class SampleLocalDateMatchersTest implements TestSuite {
	@Test
	public void testAfter() {
		SampleLocalDate sld = new SampleLocalDate();
		sld.setOpt(LocalDate.of(2003, 10, 1));
		assertThat(sld).is(SampleLocalDateMatchers.sampleLocalDateWith().optAfter(LocalDate.of(1987, 12, 11)));
	}
}
