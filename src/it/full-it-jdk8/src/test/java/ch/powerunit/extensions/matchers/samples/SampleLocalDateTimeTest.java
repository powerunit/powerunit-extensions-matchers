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

import java.time.LocalDateTime;

import ch.powerunit.Test;
import ch.powerunit.TestSuite;

public class SampleLocalDateTimeTest implements TestSuite {
	@Test
	public void testOKMatcherForComparable() {
		SampleLocalDateTime p = new SampleLocalDateTime();
		p.setOpt(LocalDateTime.of(2012, 1, 1, 9, 5));
		assertThat(p).is(
				SampleLocalDateTimeMatchers.sampleLocalDateTimeWith().optLessThan(LocalDateTime.of(2013, 1, 1, 9, 5)));
	}
}
