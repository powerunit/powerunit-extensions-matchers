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

import ch.powerunit.Test;
import ch.powerunit.TestSuite;

public class PojoClassMatchersTest implements TestSuite {
	@Test
	public void testClass() {
		PojoClass ut = new PojoClass();
		ut.clazzNoGeneric = Object.class;
		ut.clazzWildcard = String.class;
		ut.clazzExtends = Integer.class;
		ut.clazzSuper = Comparable.class;
		assertThat(ut).is(PojoClassMatchers.pojoClassWith().clazzNoGeneric(Object.class).clazzWildcard(String.class)
				.clazzExtends(Integer.class).clazzSuper(Comparable.class));
	}
}
