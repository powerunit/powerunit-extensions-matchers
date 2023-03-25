package ch.powerunit.extensions.matchers.samples.extensions.spotify;

import ch.powerunit.extensions.matchers.api.ProvideMatchers;

@ProvideMatchers(extensions=ProvideMatchers.JSON_EXTENSION)
public class SpotifySample {
	private String tmp;

	public String getTmp() {
		return tmp;
	}

	public void setTmp(String tmp) {
		this.tmp = tmp;
	}
}
