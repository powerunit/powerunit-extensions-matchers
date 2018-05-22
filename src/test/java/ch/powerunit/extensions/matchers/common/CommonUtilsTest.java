package ch.powerunit.extensions.matchers.common;

import ch.powerunit.Test;
import ch.powerunit.extensions.matchers.TestSuiteSupport;

public class CommonUtilsTest implements TestSuiteSupport {

	@Test
	public void testToJavaSyntax() {
		assertThatFunction(CommonUtils::toJavaSyntax, "<\"><\r><\t><\n>").is("\"<\\\"><\\r><\\t><\\n>\"");
	}

	@Test(fastFail = false)
	public void testAddPrefix() {
		assertThatBiFunction(CommonUtils::addPrefix, " ", "").is("\n \n");
		assertThatBiFunction(CommonUtils::addPrefix, " ", "x").is("\n x\n");
		assertThatBiFunction(CommonUtils::addPrefix, " ", "x\ny").is("\n x\n y\n");
	}

	@Test(fastFail = false)
	public void testGenerateGeneratedAnnotation() {
		assertThatBiFunction(CommonUtils::generateGeneratedAnnotation, Object.class, null)
				.is(both(containsString("@javax.annotation.Generated(value=\"java.lang.Object\",date"))
						.and(not(containsString("comments"))));
		assertThatBiFunction(CommonUtils::generateGeneratedAnnotation, Object.class, "x")
				.is(both(containsString("@javax.annotation.Generated(value=\"java.lang.Object\",date"))
						.and(containsString("comments=\"x\"")));
	}
}
