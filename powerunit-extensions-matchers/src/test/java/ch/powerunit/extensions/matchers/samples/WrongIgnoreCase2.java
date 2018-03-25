package ch.powerunit.extensions.matchers.samples;

import ch.powerunit.extensions.matchers.AddToMatcher;
import ch.powerunit.extensions.matchers.AddToMatchers;
import ch.powerunit.extensions.matchers.IgnoreInMatcher;
import ch.powerunit.extensions.matchers.ProvideMatchers;

@ProvideMatchers
public class WrongIgnoreCase2 {
	@IgnoreInMatcher
	public static String msg1;

	@IgnoreInMatcher
	private static String msg2;

	@AddToMatcher(argument = "", body = {}, suffix = "")
	public static String msg3;

	@AddToMatchers({ @AddToMatcher(argument = "", body = {}, suffix = "") })
	public static String msg4;
}
