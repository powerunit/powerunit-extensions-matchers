package ch.powerunit.extensions.matchers.provideprocessor.xml;

import ch.powerunit.Test;
import ch.powerunit.TestSuite;
import static com.google.code.beanmatchers.BeanMatchers.*;

public class GeneratedMatchersTest implements TestSuite {
	@Test
	public void testIsOK() {
		assertThat(GeneratedMatchers.class).is(allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
	}
}
