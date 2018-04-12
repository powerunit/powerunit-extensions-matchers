package ch.powerunit.extensions.matchers.common;

import ch.powerunit.Test;
import ch.powerunit.TestSuite;

public class CommonUtilsTest implements TestSuite {
	@Test
	public void testGenerateStaticDSL() {
		assertThatFunction(CommonUtils::generateStaticDSL, "clazzName").is(
				"  /**\n   * Use this static field to access all the DSL syntax, without be required to implements this interface.\n  */\n  public static final clazzName DSL = new clazzName() {};\n\n");
	}

	@Test
	public void testToJavaSyntax() {
		assertThatFunction(CommonUtils::toJavaSyntax, "<\"><\r><\t><\n>").is("\"<\\\"><\\r><\\t><\\n>\"");
	}

	@Test
	public void testAddPrefix() {
		assertThatBiFunction(CommonUtils::addPrefix, " ", "x\ny").is("\n x\n y\n");
	}
}
