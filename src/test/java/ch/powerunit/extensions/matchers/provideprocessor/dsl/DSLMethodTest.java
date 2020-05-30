/**
 * Powerunit - A JDK1.8 test framework
 * Copyright (C) 2014 Mathieu Boretti.
 *
 * This file is part of Powerunit
 *
 * Powerunit is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Powerunit is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Powerunit. If not, see <http://www.gnu.org/licenses/>.
 */
package ch.powerunit.extensions.matchers.provideprocessor.dsl;

import ch.powerunit.Test;
import ch.powerunit.TestSuite;

/**
 * @author borettim
 *
 */
public class DSLMethodTest implements TestSuite {

	@Test(fastFail = false)
	public void testDSLMethodTwoArgument() {
		DSLMethod m = DSLMethod.of("boolean isOK").addOneArgument("java.lang.String", "one")
				.addOneArgument("java.lang.String", "two").withImplementation("l1")
				.withJavadoc(new String[] { "l1", "l2" });
		assertThat(m.asStaticImplementation()).is(
				"/**\n * l1\n * l2\n */\npublic static boolean isOK(java.lang.String one,java.lang.String two) {\n  l1\n}\n");
		assertThat(m.asDefaultReference("target")).is(
				"/**\n * l1\n * l2\n */\ndefault boolean isOK(java.lang.String one,java.lang.String two) {\n  return target.isOK(one,two);\n}\n");
	}

	@Test(fastFail = false)
	public void testDSLMethodOneArgument() {
		DSLMethod m = DSLMethod.of("boolean isOK").addOneArgument("java.lang.String", "one").withImplementation("l1")
				.withJavadoc(new String[] { "l1", "l2" });
		assertThat(m.asStaticImplementation())
				.is("/**\n * l1\n * l2\n */\npublic static boolean isOK(java.lang.String one) {\n  l1\n}\n");
		assertThat(m.asDefaultReference("target")).is(
				"/**\n * l1\n * l2\n */\ndefault boolean isOK(java.lang.String one) {\n  return target.isOK(one);\n}\n");
	}

	@Test(fastFail = false)
	public void testDSLMethodZeroArgument() {
		DSLMethod m = DSLMethod.of("boolean isOK").withoutArgument().withImplementation("l1")
				.withJavadoc(new String[] { "l1", "l2" });
		assertThat(m.asStaticImplementation()).is("/**\n * l1\n * l2\n */\npublic static boolean isOK() {\n  l1\n}\n");
		assertThat(m.asDefaultReference("target"))
				.is("/**\n * l1\n * l2\n */\ndefault boolean isOK() {\n  return target.isOK();\n}\n");
	}
}
