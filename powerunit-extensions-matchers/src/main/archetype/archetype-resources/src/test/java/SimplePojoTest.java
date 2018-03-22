package \${package};

import ch.powerunit.Test;
import ch.powerunit.TestSuite;

//implements the AllMatchers interface to have access to the generated matchers
//TestSuite provide the DSL for powerunit
public class SimplePojoTest implements TestSuite, AllMatchers {

	@Test
	public void testPojoValueWithGeneratedMatcher() {
		SimplePojo p = new SimplePojo();
		p.oneField = "x";
		assertThat(p).is(simplePojoWith().oneField("x"));
	}
}
