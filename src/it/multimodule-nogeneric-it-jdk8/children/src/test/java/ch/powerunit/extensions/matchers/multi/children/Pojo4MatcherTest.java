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
package ch.powerunit.extensions.matchers.multi.children;

import ch.powerunit.TestDelegate;
import ch.powerunit.TestSuite;
import ch.powerunit.extensions.matchers.multi.parent.Pojo1Matchers;
import ch.powerunit.matchers.MatcherTester;
import static ch.powerunit.matchers.MatcherTester.matcher;
import static ch.powerunit.matchers.MatcherTester.value;

public class Pojo4MatcherTest implements TestSuite {

	//@formatter:off
	@TestDelegate
	public final MatcherTester<?> tester = testerOfMatcher(Pojo4Matchers.Pojo4MatcherImpl.class)
			.with(
					matcher((Pojo4Matchers.Pojo4MatcherImpl) Pojo4Matchers.pojo4With().msg2ContainsString("12"))
					.describedAs("an instance of ch.powerunit.extensions.matchers.multi.children.Pojo4 with\n[parent ANYTHING]\n[msg2 a string containing \"12\"]\n")
					.nullRejected("was null")
					.accepting(
							new Pojo4("12"), 
							new Pojo4("121"))
					.rejecting(
							value("").withMessage("was \"\""), 
							value(new Pojo4()).withMessage("[msg2 was null]\n"),
							value(new Pojo4("11")).withMessage("[msg2 was \"11\"]\n")),
					matcher((Pojo4Matchers.Pojo4MatcherImpl) Pojo4Matchers.pojo4With(Pojo1Matchers.pojo1With().msg1("x")).msg2ContainsString("12"))
					.describedAs("an instance of ch.powerunit.extensions.matchers.multi.children.Pojo4 with\n[parent an instance of ch.powerunit.extensions.matchers.multi.parent.Pojo1 with\n[msg1 is \"x\"]\n]\n[msg2 a string containing \"12\"]\n")
					.nullRejected("was null")
					.accepting(
							new Pojo4("x","12"), 
							new Pojo4("x","121"))
					.rejecting(
							value("").withMessage("was \"\""), 
							value(new Pojo4()).withMessage("[parent [msg1 was null]\n]\n[msg2 was null]\n"),
							value(new Pojo4("11")).withMessage("[parent [msg1 was null]\n]\n[msg2 was \"11\"]\n"),
							value(new Pojo4("z","11")).withMessage("[parent [msg1 was \"z\"]\n]\n[msg2 was \"11\"]\n")),
					matcher((Pojo4Matchers.Pojo4MatcherImpl) Pojo4Matchers.pojo4WithSameValue(new Pojo4("x")))
					.describedAs("an instance of ch.powerunit.extensions.matchers.multi.children.Pojo4 with\n[parent an instance of ch.powerunit.extensions.matchers.multi.parent.Pojo1 with\n[msg1 is null]\n]\n[msg2 is \"x\"]\n")
					.nullRejected("was null")
					.accepting(
							new Pojo4("x"))
					.rejecting(
							value("").withMessage("was \"\""), 
							value(new Pojo4()).withMessage("[msg2 was null]\n"),
							value(new Pojo4("11")).withMessage("[msg2 was \"11\"]\n")),
					matcher((Pojo4Matchers.Pojo4MatcherImpl) Pojo4Matchers.pojo4WithSameValue(new Pojo4("y","x")))
					.describedAs("an instance of ch.powerunit.extensions.matchers.multi.children.Pojo4 with\n[parent an instance of ch.powerunit.extensions.matchers.multi.parent.Pojo1 with\n[msg1 is \"y\"]\n]\n[msg2 is \"x\"]\n")
					.nullRejected("was null")
					.accepting(
							new Pojo4("y","x"))
					.rejecting(
							value("").withMessage("was \"\""), 
							value(new Pojo4()).withMessage("[parent [msg1 was null]\n]\n[msg2 was null]\n"),
							value(new Pojo4("11")).withMessage("[parent [msg1 was null]\n]\n[msg2 was \"11\"]\n"),
							value(new Pojo4("z","x")).withMessage("[parent [msg1 was \"z\"]\n]\n")));
	//@formatter:on
}
