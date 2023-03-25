package ch.powerunit.extensions.matchers.samples.extensions.hamcrestdate;

import java.time.LocalTime;

import ch.powerunit.extensions.matchers.api.ProvideMatchers;

@ProvideMatchers
public class SampleLocalTime {
	private LocalTime opt;

	public LocalTime getOpt() {
		return opt;
	}

	public void setOpt(LocalTime opt) {
		this.opt = opt;
	}
}
