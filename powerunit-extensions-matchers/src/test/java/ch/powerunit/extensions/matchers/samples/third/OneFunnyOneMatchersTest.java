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
package ch.powerunit.extensions.matchers.samples.third;

import ch.powerunit.Test;
import ch.powerunit.TestSuite;
import ch.powerunit.extensions.matchers.samples.Pojo1;

public class OneFunnyOneMatchersTest implements TestSuite {

	@Test
	public void testMatcherWithSubMatcherDSL() {
		OneFunnyOne obj = new OneFunnyOne();
		obj.onePojo1 = new Pojo1();
		obj.onePojo1.msg2 = "12";
		assertThat(obj).is(OneFunnyOneMatchers.oneFunnyOneWith().onePojo1With().msg2("12").end());

	}

	@Test
	public void testMatcherWithSubMatcherDSLEndWith() {
		OneFunnyOne obj = new OneFunnyOne();
		obj.onePojo1 = new Pojo1();
		obj.onePojo1.msg2 = "12";
		assertThat(obj).is(
				OneFunnyOneMatchers.oneFunnyOneWith().onePojo1With().msg2("12").endWith(hasToString(notNullValue())));

	}
}
