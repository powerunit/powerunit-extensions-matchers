package ch.powerunit.extensions.matchers.samples.extensions.hamcrestdate;

import java.time.LocalDateTime;

import ch.powerunit.Test;
import ch.powerunit.TestSuite;

public class SampleLocalDateTimeV2MatchersTest implements TestSuite {
	@Test
	public void testAfter() {
		SampleLocalDateTimeV2 sld = new SampleLocalDateTimeV2();
		sld.setOpt(LocalDateTime.of(2003, 10, 1, 12, 34));
		assertThat(sld).is(SampleLocalDateTimeV2Matchers.sampleLocalDateTimeV2With()
				.optAfter(LocalDateTime.of(1987, 12, 11, 11, 23)));
	}
}
