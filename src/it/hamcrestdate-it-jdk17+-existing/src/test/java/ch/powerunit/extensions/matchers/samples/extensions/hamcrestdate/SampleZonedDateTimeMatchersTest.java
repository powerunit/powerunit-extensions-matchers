package ch.powerunit.extensions.matchers.samples.extensions.hamcrestdate;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import ch.powerunit.Test;
import ch.powerunit.TestSuite;

public class SampleZonedDateTimeMatchersTest implements TestSuite {
	@Test
	public void testAfter() {
		SampleZonedDateTime sld = new SampleZonedDateTime();
		sld.setOpt(ZonedDateTime.of(LocalDateTime.of(1987, 12, 11, 11, 23), ZoneId.systemDefault()));
		assertThat(sld).is(SampleZonedDateTimeMatchers.sampleZonedDateTimeWith()
				.optAfter(ZonedDateTime.of(LocalDateTime.of(1930, 12, 11, 11, 23), ZoneId.systemDefault())));
	}
}
