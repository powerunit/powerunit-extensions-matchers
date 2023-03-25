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
package ch.powerunit.extensions.matchers.multi.parent;

import ch.powerunit.TestDelegate;
import ch.powerunit.TestSuite;
import ch.powerunit.matchers.MatcherTester;
import static ch.powerunit.matchers.MatcherTester.matcher;
import static ch.powerunit.matchers.MatcherTester.value;

public class Pojo1MatcherTest implements TestSuite {

	//@formatter:off
	@TestDelegate
	public final MatcherTester<?> tester = testerOfMatcher(Pojo1Matchers.Pojo1MatcherImpl.class)
			.with(
					matcher((Pojo1Matchers.Pojo1MatcherImpl) Pojo1Matchers.pojo1With().msg1ContainsString("12"))
					.describedAs("an instance of ch.powerunit.extensions.matchers.multi.parent.Pojo1 with\n[msg1 a string containing \"12\"]\n")
					.nullRejected("was null")
					.accepting(
							new Pojo1("12"), 
							new Pojo1("121"))
					.rejecting(
							value("").withMessage("was \"\""), 
							value(new Pojo1()).withMessage("[msg1 was null]\n"),
							value(new Pojo1("11")).withMessage("[msg1 was \"11\"]\n")),
					
					matcher((Pojo1Matchers.Pojo1MatcherImpl) Pojo1Matchers.pojo1WithSameValue(new Pojo1("x")))
					.describedAs("an instance of ch.powerunit.extensions.matchers.multi.parent.Pojo1 with\n[msg1 is \"x\"]\n")
					.nullRejected("was null")
					.accepting(
							new Pojo1("x"))
					.rejecting(
							value("").withMessage("was \"\""), 
							value(new Pojo1()).withMessage("[msg1 was null]\n"),
							value(new Pojo1("11")).withMessage("[msg1 was \"11\"]\n")));
	//@formatter:on
}
