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
package ch.powerunit.extensions.matchers.samples;

import ch.powerunit.Ignore;
import ch.powerunit.Test;
import ch.powerunit.TestSuite;

public class Pojo3MatcherTest implements TestSuite {
	@Test
	public void testOKMatcher() {
		Pojo3<String, String> p = new Pojo3<>();
		assertThat(p).is(Pojo3Matchers.pojo3With());
	}

	@Test
	@Ignore
	public void testKOMatcher() {
		Pojo3<String, String> p = new Pojo3<>();
		assertThat(p).is(Pojo3Matchers.<String, String> pojo3With().msg1("x"));
	}
}
