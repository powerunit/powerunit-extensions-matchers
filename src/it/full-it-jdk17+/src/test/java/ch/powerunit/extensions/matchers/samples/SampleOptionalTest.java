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

import java.util.Optional;

import ch.powerunit.Test;
import ch.powerunit.TestSuite;

public class SampleOptionalTest implements TestSuite {
	@Test
	public void testOKMatcherForOptional1() {
		SampleOptional p = new SampleOptional();
		p.setOpt(Optional.empty());
		assertThat(p).is(SampleOptionalMatchers.sampleOptionalWith().optIsNotPresent());
	}

	@Test
	public void testOKMatcherForOptional2() {
		SampleOptional p = new SampleOptional();
		p.setOpt(Optional.of("x"));
		assertThat(p).is(SampleOptionalMatchers.sampleOptionalWith().optIsPresent());
	}
	
	@Test
	public void testOKMatcherForOptional3() {
		SampleOptional p = new SampleOptional();
		p.setOpt(Optional.empty());
		assertThat(p).is(SampleOptionalMatchers.sampleOptionalWith().optIsAbsent());
	}
	
	@Test
	public void testOKMatcherForOptional4() {
		SampleOptional p = new SampleOptional();
		p.setOpt(Optional.of("x"));
		assertThat(p).is(SampleOptionalMatchers.sampleOptionalWith().optIsPresentAndIs("x"));
	}
	
	@Test
	public void testOKMatcherForOptional5() {
		SampleOptional p = new SampleOptional();
		p.setOpt(Optional.of("x"));
		assertThat(p).is(SampleOptionalMatchers.sampleOptionalWith().optIsPresentAndIs(is("x")));
	}
	
	@Test
	public void testOKMatcherForOptional6() {
		SampleOptional p = new SampleOptional();
		p.setOpt(Optional.of("x"));
		SampleOptional p2 = new SampleOptional();
		p2.setOpt(Optional.of("x"));
		assertThat(p).is(SampleOptionalMatchers.sampleOptionalWithSameValue(p2));
	}
}
