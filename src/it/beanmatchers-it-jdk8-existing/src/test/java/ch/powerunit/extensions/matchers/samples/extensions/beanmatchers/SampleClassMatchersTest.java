/**
 * 
 */
package ch.powerunit.extensions.matchers.samples.extensions.beanmatchers;

import ch.powerunit.Test;
import ch.powerunit.TestSuite;

/**
 * @author borettim
 *
 */
public class SampleClassMatchersTest implements TestSuite {
	@Test
	public void testBeanMatcher() {
		SampleClass sc = new SampleClass();
		sc.underTest = MyTestPojo.class;
		assertThat(sc).is(SampleClassMatchers.sampleClassWith().underTestIsAValidBean());
	}
}
