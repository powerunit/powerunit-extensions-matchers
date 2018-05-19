package ch.powerunit.extensions.matchers.common;

import ch.powerunit.Test;
import ch.powerunit.extensions.matchers.TestSuiteSupport;

public class CommonUtilsTest implements TestSuiteSupport {

	@Test
	public void testToJavaSyntax() {
		assertThatFunction(CommonUtils::toJavaSyntax, "<\"><\r><\t><\n>").is("\"<\\\"><\\r><\\t><\\n>\"");
	}

	@Test
	public void testAddPrefix() {
		assertThatBiFunction(CommonUtils::addPrefix, " ", "x\ny").is("\n x\n y\n");
	}
}
