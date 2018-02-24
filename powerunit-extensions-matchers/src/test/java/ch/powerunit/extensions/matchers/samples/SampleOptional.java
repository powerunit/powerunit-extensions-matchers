package ch.powerunit.extensions.matchers.samples;

import java.util.Optional;

import ch.powerunit.extensions.matchers.ProvideMatchers;

@ProvideMatchers
public class SampleOptional {
	private Optional<String> opt;

	public Optional<String> getOpt() {
		return opt;
	}

	public void setOpt(Optional<String> opt) {
		this.opt = opt;
	}
	
	
	
}
