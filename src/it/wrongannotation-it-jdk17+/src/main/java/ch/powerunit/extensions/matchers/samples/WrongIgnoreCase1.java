package ch.powerunit.extensions.matchers.samples;

import ch.powerunit.extensions.matchers.api.AddToMatcher;
import ch.powerunit.extensions.matchers.api.AddToMatchers;
import ch.powerunit.extensions.matchers.api.IgnoreInMatcher;

public class WrongIgnoreCase1 {
	@IgnoreInMatcher
	public String msg1;

	@IgnoreInMatcher
	public static String msg2;

	@AddToMatcher(argument = "", body = {}, suffix = "")
	public String msg3;

	@AddToMatchers({ @AddToMatcher(argument = "", body = {}, suffix = "") })
	public String msg4;
}
