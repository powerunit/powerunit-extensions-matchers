package ch.powerunit.extensions.matchers.samples;

import java.util.List;
import java.util.function.Supplier;

import ch.powerunit.extensions.matchers.ProvideMatchers;

@ProvideMatchers
public class SampleSupplier {
	public Supplier<String> s1;

	public Supplier<List<String>> s2;
}
