package ch.powerunit.extensions.matchers.samples;

import ch.powerunit.extensions.matchers.IgnoreInMatcher;
import ch.powerunit.extensions.matchers.ProvideMatchers;

@ProvideMatchers
public class WrongIgnoreCase2 {
	@IgnoreInMatcher
	public static String msg1;
	
	@IgnoreInMatcher
	private static String msg2;
}
