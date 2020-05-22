package ch.powerunit.extensions.matchers.samples.extensions.hamcrestdate;

import java.time.LocalDateTime;

import ch.powerunit.extensions.matchers.ProvideMatchers;

@ProvideMatchers
public class SampleLocalDateTimeV2 {
	private LocalDateTime opt;

	public LocalDateTime getOpt() {
		return opt;
	}

	public void setOpt(LocalDateTime opt) {
		this.opt = opt;
	}
}
