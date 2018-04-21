package ch.powerunit.extensions.matchers.common;

import ch.powerunit.Test;
import ch.powerunit.TestSuite;

public class CommonUtilsTest implements TestSuite {

	@Test
	public void testToJavaSyntax() {
		assertThatFunction(CommonUtils::toJavaSyntax, "<\"><\r><\t><\n>").is("\"<\\\"><\\r><\\t><\\n>\"");
	}

	@Test
	public void testAddPrefix() {
		assertThatBiFunction(CommonUtils::addPrefix, " ", "x\ny").is("\n x\n y\n");
	}
}
