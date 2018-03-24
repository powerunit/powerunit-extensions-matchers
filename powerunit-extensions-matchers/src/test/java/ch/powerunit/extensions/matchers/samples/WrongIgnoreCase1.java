package ch.powerunit.extensions.matchers.samples;

import ch.powerunit.extensions.matchers.IgnoreInMatcher;

public class WrongIgnoreCase1 {
	@IgnoreInMatcher
	public String msg1;
	
	@IgnoreInMatcher
	public static String msg2;
}
