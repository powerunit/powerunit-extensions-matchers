package ch.powerunit.extensions.matchers.common;

import ch.powerunit.Test;
import ch.powerunit.extensions.matchers.TestSuiteSupport;

public class ElementHelperTest implements TestSuiteSupport, ElementHelper {

	@Test
	public void testGetSimpleName() {
		assertThatFunction(this::getSimpleName,
				generateMockitoProcessingEnvironment().getElementUtils().getTypeElement("java.lang.Object"))
						.is("Object");
	}

	@Test
	public void testGetQualifiedName() {
		assertThatFunction(this::getQualifiedName,
				generateMockitoProcessingEnvironment().getElementUtils().getTypeElement("java.lang.Object"))
						.is("java.lang.Object");
	}
}
