package ch.powerunit.extensions.matchers.samples;

import java.time.LocalDateTime;

import ch.powerunit.extensions.matchers.ProvideMatchers;

@ProvideMatchers
public class SampleLocalDateTime {
	private LocalDateTime opt;

	public LocalDateTime getOpt() {
		return opt;
	}

	public void setOpt(LocalDateTime opt) {
		this.opt = opt;
	}
}
