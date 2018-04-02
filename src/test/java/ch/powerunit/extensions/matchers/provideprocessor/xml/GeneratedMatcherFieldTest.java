package ch.powerunit.extensions.matchers.provideprocessor.xml;

import ch.powerunit.Test;
import ch.powerunit.TestSuite;
import static com.google.code.beanmatchers.BeanMatchers.*;

public class GeneratedMatcherFieldTest implements TestSuite {
	@Test
	public void testIsOK() {
		assertThat(GeneratedMatcherField.class).is(allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
	}
}
