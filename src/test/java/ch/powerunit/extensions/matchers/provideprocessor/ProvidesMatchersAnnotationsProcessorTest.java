package ch.powerunit.extensions.matchers.provideprocessor;

import javax.annotation.processing.ProcessingEnvironment;

import org.mockito.Mock;

import ch.powerunit.Rule;
import ch.powerunit.Test;
import ch.powerunit.TestRule;
import ch.powerunit.TestSuite;

public class ProvidesMatchersAnnotationsProcessorTest implements TestSuite {

	@Mock
	ProcessingEnvironment processingEnv;

	private void prepare() {
		
	}

	@Rule
	public final TestRule rules = mockitoRule().around(before(this::prepare));

	@Test
	public void testInit() {
		ProvidesMatchersAnnotationsProcessor underTest = new ProvidesMatchersAnnotationsProcessor();
		underTest.init(processingEnv);
	}
}
