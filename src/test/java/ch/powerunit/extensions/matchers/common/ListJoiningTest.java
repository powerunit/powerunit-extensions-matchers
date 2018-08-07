package ch.powerunit.extensions.matchers.common;

import ch.powerunit.Test;
import ch.powerunit.extensions.matchers.TestSuiteSupport;

public class ListJoiningTest implements TestSuiteSupport {

	@Test(fastFail = false)
	public void testAsString() {
		assertThat(ListJoining.accepting(String.class).withMapper(s -> s + ";").withDelimiter("\n")
				.withPrefixAndSuffix("{", "}").asString("x", "y")).is("{x;\ny;}");
	}
}
