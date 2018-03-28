package ch.powerunit.extensions.matchers.samples;

import java.util.Optional;

import ch.powerunit.extensions.matchers.IgnoreInMatcher;
import ch.powerunit.extensions.matchers.ProvideMatchers;

@ProvideMatchers
public class SampleOptional {
	// only once must be added
	public Optional<String> opt;

	public Optional<String> getOpt() {
		return opt;
	}

	public void setOpt(Optional<String> opt) {
		this.opt = opt;
	}

	@IgnoreInMatcher(comments = "Why not?")
	public String ignoreMe;

	public String getIgnoreMe() {
		return ignoreMe;
	}

	public void setIgnoreMe(String ignoreMe) {
		this.ignoreMe = ignoreMe;
	}

}
