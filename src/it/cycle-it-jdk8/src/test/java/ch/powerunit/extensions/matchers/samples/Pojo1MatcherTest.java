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

import static ch.powerunit.matchers.MatcherTester.matcher;
import static ch.powerunit.matchers.MatcherTester.value;

import ch.powerunit.TestDelegate;
import ch.powerunit.TestSuite;
import ch.powerunit.matchers.MatcherTester;

public class Pojo1MatcherTest implements TestSuite {

	private static final Pojo1 p1 = new Pojo1("x", null);

	private static final Pojo2 p2 = new Pojo2("y", null);

	private static final Pojo1 p12;

	private static final Pojo2 p21;

	static {
		p12 = new Pojo1("1", null);
		p21 = new Pojo2("2", p12);
		p12.pojo2 = p21;
	}

	//@formatter:off
	@TestDelegate
	public final MatcherTester<?> tester = testerOfMatcher(Pojo1Matchers.Pojo1MatcherImpl.class).with(
			matcher((Pojo1Matchers.Pojo1MatcherImpl) Pojo1Matchers.pojo1WithSameValue(p1))
				.describedAs("an instance of ch.powerunit.extensions.matchers.samples.Pojo1 with\n[msg1 is \"x\"]\n[pojo2 null]\n")
					.nullRejected("was null").
					accepting(
							new Pojo1("x",null))
					.rejecting(
							value("").withMessage("was \"\""), 
							value(new Pojo1(null,null)).withMessage("[msg1 was null]\n"),
							value(new Pojo1("x",p2)).withMessage("[pojo2 was <Pojo2 [msg2=y, pojo1=null]>]\n"),
							value(new Pojo1("y",null)).withMessage("[msg1 was \"y\"]\n")),
			matcher((Pojo1Matchers.Pojo1MatcherImpl) Pojo1Matchers.pojo1WithSameValue(p12))
			.describedAs("an instance of ch.powerunit.extensions.matchers.samples.Pojo1 with\n[msg1 is \"1\"]\n[pojo2 an instance of ch.powerunit.extensions.matchers.samples.Pojo2 with\n[msg2 is \"2\"]\n[pojo1 an instance of ch.powerunit.extensions.matchers.samples.Pojo1 with\n[msg1 ANYTHING]\n[pojo2 ANYTHING]\n[object itself Same instance control only. A cycle has been detected.]\n]\n]\n")
				.nullRejected("was null").
				accepting(
						p12)
				.rejecting(
						value("").withMessage("was \"\""), 
						value(new Pojo1(null,null)).withMessage("[msg1 was null]\n[pojo2 was null]\n"),
						value(new Pojo1("x",p2)).withMessage("[msg1 was \"x\"]\n[pojo2 [msg2 was \"y\"]\n[pojo1 was null]\n]\n"),
						value(new Pojo1("y",null)).withMessage("[msg1 was \"y\"]\n[pojo2 was null]\n")));
	//@formatter:off

}
