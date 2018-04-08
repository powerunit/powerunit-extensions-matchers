package ch.powerunit.extensions.matchers.provideprocessor;

import ch.powerunit.Test;
import ch.powerunit.TestSuite;

public class ProvidesMatchersAnnotationsProcessorTest implements TestSuite {
	@Test
	public void testAddPrefix() {
		assertThatBiFunction(ProvidesMatchersAnnotationsProcessor::addPrefix, " ", "x\ny").is("\n x\n y\n");
	}
}
