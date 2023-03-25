package ch.powerunit.extensions.matchers.samples;

import java.util.Optional;

import ch.powerunit.extensions.matchers.api.IgnoreInMatcher;
import ch.powerunit.extensions.matchers.api.ProvideMatchers;
import ch.powerunit.extensions.matchers.samples.extension.MyTestWithoutGeneric;
import ch.powerunit.extensions.matchers.samples.extension.MyTestWithoutGeneric2;

@ProvideMatchers
public class SampleOptional {
	public SampleOptional() {
	}

	public SampleOptional(Optional<String> opt, String ignoreMe, Optional<Pojo1> opt2,
			Optional<MyTestWithoutGeneric> opt3, Optional<MyTestWithoutGeneric2> opt4) {
		this.opt = opt;
		this.ignoreMe = ignoreMe;
		this.opt2 = opt2;
		this.opt3 = opt3;
		this.opt4 = opt4;
	}

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

	public Optional<Pojo1> opt2;

	public Optional<MyTestWithoutGeneric> opt3;

	public Optional<MyTestWithoutGeneric2> opt4;

}
