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
import ch.powerunit.extensions.matchers.multi.parent.Pojo1;
import ch.powerunit.matchers.MatcherTester;
import static ch.powerunit.matchers.MatcherTester.matcher;
import static ch.powerunit.matchers.MatcherTester.value;

public class Pojo6MatcherTest implements TestSuite {

	//@formatter:off
	@TestDelegate
	public final MatcherTester<?> tester = testerOfMatcher(Pojo6Matchers.Pojo6MatcherImpl.class)
			.with(
					matcher((Pojo6Matchers.Pojo6MatcherImpl) Pojo6Matchers.pojo6With().field1With().msg1("x").end())
					.describedAs("an instance of ch.powerunit.extensions.matchers.multi.children.Pojo6 with\n[field1 an instance of ch.powerunit.extensions.matchers.multi.parent.Pojo1 with\n[msg1 is \"x\"]\n]\n")
					.nullRejected("was null")
					.accepting(
							new Pojo6(new Pojo1("x")))
					.rejecting(
							value("").withMessage("was \"\""), 
							value(new Pojo6()).withMessage("[field1 was null]\n"),
							value(new Pojo6(new Pojo1())).withMessage("[field1 [msg1 was null]\n]\n"),
							value(new Pojo6(new Pojo1("y"))).withMessage("[field1 [msg1 was \"y\"]\n]\n")),
					
					matcher((Pojo6Matchers.Pojo6MatcherImpl) Pojo6Matchers.pojo6WithSameValue(new Pojo6()))
					.describedAs("an instance of ch.powerunit.extensions.matchers.multi.children.Pojo6 with\n[field1 null]\n")
					.nullRejected("was null")
					.accepting(
							new Pojo6())
					.rejecting(
							value("").withMessage("was \"\""), 
							value(new Pojo6(new Pojo1())).withMessage("[field1 was <Pojo1 [msg1=null]>]\n"),
							value(new Pojo6(new Pojo1("x"))).withMessage("[field1 was <Pojo1 [msg1=x]>]\n")),
					matcher((Pojo6Matchers.Pojo6MatcherImpl) Pojo6Matchers.pojo6WithSameValue(new Pojo6(new Pojo1())))
					.describedAs("an instance of ch.powerunit.extensions.matchers.multi.children.Pojo6 with\n[field1 an instance of ch.powerunit.extensions.matchers.multi.parent.Pojo1 with\n[msg1 is null]\n]\n")
					.nullRejected("was null")
					.accepting(
							new Pojo6(new Pojo1()))
					.rejecting(
							value("").withMessage("was \"\""), 
							value(new Pojo6()).withMessage("[field1 was null]\n"),
							value(new Pojo6(new Pojo1("x"))).withMessage("[field1 [msg1 was \"x\"]\n]\n")),
					matcher((Pojo6Matchers.Pojo6MatcherImpl) Pojo6Matchers.pojo6WithSameValue(new Pojo6(new Pojo1("a"))))
					.describedAs("an instance of ch.powerunit.extensions.matchers.multi.children.Pojo6 with\n[field1 an instance of ch.powerunit.extensions.matchers.multi.parent.Pojo1 with\n[msg1 is \"a\"]\n]\n")
					.nullRejected("was null")
					.accepting(
							new Pojo6(new Pojo1("a")))
					.rejecting(
							value("").withMessage("was \"\""), 
							value(new Pojo6()).withMessage("[field1 was null]\n"),
							value(new Pojo6(new Pojo1())).withMessage("[field1 [msg1 was null]\n]\n"),
							value(new Pojo6(new Pojo1("t"))).withMessage("[field1 [msg1 was \"t\"]\n]\n")));
	//@formatter:on
}
