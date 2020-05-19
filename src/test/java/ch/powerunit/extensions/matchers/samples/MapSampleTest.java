package ch.powerunit.extensions.matchers.samples;

import java.util.Collections;

import ch.powerunit.Test;
import ch.powerunit.TestSuite;

public class MapSampleTest implements TestSuite {
	@Test
	public void testSameValue() {
		MapSample<String, Long> ms = new MapSample<>();
		ms.map1 = Collections.singletonMap("x", 12);
		ms.map2 = Collections.singletonMap("y", 12L);
		ms.map3 = Collections.singletonMap("z", "a");
		ms.map4 = Collections.singletonMap("a", "b");
		assertThat(ms).is(MapSampleMatchers.<String, Long>mapSampleWith()
				.map1HasSameValues(Collections.singletonMap("x", 12)).build());
		assertThat(ms).isNot(MapSampleMatchers.<String, Long>mapSampleWith()
				.map1HasSameValues(Collections.emptyMap()).build());
	}
}
