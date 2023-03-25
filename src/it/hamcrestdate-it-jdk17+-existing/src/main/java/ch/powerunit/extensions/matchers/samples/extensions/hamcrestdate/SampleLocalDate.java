package ch.powerunit.extensions.matchers.samples.extensions.hamcrestdate;

import java.time.LocalDate;

import ch.powerunit.extensions.matchers.api.ProvideMatchers;

@ProvideMatchers
public class SampleLocalDate {
	private LocalDate opt;

	public LocalDate getOpt() {
		return opt;
	}

	public void setOpt(LocalDate opt) {
		this.opt = opt;
	}
}
