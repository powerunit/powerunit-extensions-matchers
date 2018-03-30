package ch.powerunit.extensions.matchers.factoryprocessor;

import org.mockito.Mock;

import ch.powerunit.Rule;
import ch.powerunit.Test;
import ch.powerunit.TestRule;
import ch.powerunit.TestSuite;

public class FactoryGroupTest implements TestSuite {

	@Mock
	private FactoryAnnotationsProcessor factoryAnnotationProcessor;

	@Rule
	public final TestRule rules = mockitoRule();

	@Test
	public void testConstructorWithOneEntry() {
		FactoryGroup underTest = new FactoryGroup(factoryAnnotationProcessor, ".*:target");
		assertThat(underTest.getFullyQualifiedTargetName()).is("target");
		assertThat(underTest.getAcceptingRegex()).is(arrayContaining(".*"));
	}

	@Test
	public void testConstructorWithTwoEntry() {
		FactoryGroup underTest = new FactoryGroup(factoryAnnotationProcessor, "a.*,b.*:target");
		assertThat(underTest.getFullyQualifiedTargetName()).is("target");
		assertThat(underTest.getAcceptingRegex()).is(arrayContaining("a.*", "b.*"));
	}

}
