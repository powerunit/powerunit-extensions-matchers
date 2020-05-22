package ch.powerunit.extensions.matchers.samples.extensions.spotify;

import com.spotify.hamcrest.jackson.JsonMatchers;

import ch.powerunit.Test;
import ch.powerunit.TestSuite;

public class SpotifySampleMatchersTest implements TestSuite {
	@Test
	public void testAfter() {
		SpotifySample ss = new SpotifySample();
		ss.setTmp("{\"a\":\"x\"}");
		assertThat(ss)
				.is(SpotifySampleMatchers.spotifySampleWith().tmpAsJson(JsonMatchers.jsonObject().where("a", JsonMatchers.jsonText("x"))));
	}
}
