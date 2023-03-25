package ch.powerunit.extensions.matchers.samples.extensions.hamcrestdate;

import java.time.ZonedDateTime;

import ch.powerunit.extensions.matchers.api.ProvideMatchers;

@ProvideMatchers
public class SampleZonedDateTime {
	private ZonedDateTime opt;

	public ZonedDateTime getOpt() {
		return opt;
	}

	public void setOpt(ZonedDateTime opt) {
		this.opt = opt;
	}
}
