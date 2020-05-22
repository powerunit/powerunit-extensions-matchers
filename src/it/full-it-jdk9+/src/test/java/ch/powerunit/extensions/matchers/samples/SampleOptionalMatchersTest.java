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

import java.util.Optional;

import ch.powerunit.TestDelegate;
import ch.powerunit.TestSuite;
import ch.powerunit.extensions.matchers.samples.extension.MyTestWithoutGeneric;
import ch.powerunit.extensions.matchers.samples.extension.MyTestWithoutGeneric2;
import ch.powerunit.matchers.MatcherTester;

/**
 * @author borettim
 *
 */
public class SampleOptionalMatchersTest implements TestSuite {

	private static final Object OTHER_TYPE = "";

	private static final SampleOptional ALL_NULL = new SampleOptional();

	private static final SampleOptional ALL_EMPTY = new SampleOptional(Optional.empty(), null, Optional.empty(),
			Optional.empty(), Optional.empty());

	private static final SampleOptional ALL_EMPTY_BUT_IGNORE = new SampleOptional(Optional.empty(), "",
			Optional.empty(), Optional.empty(), Optional.empty());

	private static final SampleOptional ALL_NOT_EMPTY = new SampleOptional(Optional.of("1"), "",
			Optional.of(new Pojo1()), Optional.of(new MyTestWithoutGeneric()),
			Optional.of(new MyTestWithoutGeneric2()));

	private static final SampleOptional ALL_NOT_EMPTY_V2 = new SampleOptional(Optional.of("2"), "",
			Optional.of(new Pojo1()), Optional.of(new MyTestWithoutGeneric()),
			Optional.of(new MyTestWithoutGeneric2("x")));

	@TestDelegate
	public final MatcherTester<?> testMatcher = testerOfMatcher(//
			SampleOptionalMatchers.SampleOptionalMatcher.class)//
					.with(//
							matcher(//
									(SampleOptionalMatchers.SampleOptionalMatcher) SampleOptionalMatchers
											.sampleOptionalWithSameValue(ALL_EMPTY)).//
													describedAs(
															"an instance of ch.powerunit.extensions.matchers.samples.SampleOptional with\n[ignoreMe This field is ignored Why not?]\n[opt optional is not present]\n[opt2 optional is not present]\n[opt3 optional is not present]\n[opt4 optional is not present]\n")
													.//
													nullRejected("was null").//
													accepting(ALL_EMPTY).//
													accepting(ALL_EMPTY_BUT_IGNORE).//
													rejecting(//
															value(OTHER_TYPE).//
																	withMessage("was \"\""), //
															value(ALL_NOT_EMPTY).//
																	withMessage(
																			"[opt was <Optional[1]>]\n[opt2 was <Optional[Pojo1 [msg1=null, msg2=null, msg3=0, msg4=null, msg5=null, msg6=null, msg7=null, msg8=null, msg9=null, msg12=null, myBoolean=false, oneBoolean=false]]>]\n[opt3 was <Optional[]>]\n[opt4 was <Optional[]>]\n"), //
															value(ALL_NULL).//
																	withMessage(
																			"[opt was null]\n[opt2 was null]\n[opt3 was null]\n[opt4 was null]\n")), //
							matcher(//
									(SampleOptionalMatchers.SampleOptionalMatcher) SampleOptionalMatchers
											.sampleOptionalWithSameValue(ALL_NULL)).//
													describedAs(
															"an instance of ch.powerunit.extensions.matchers.samples.SampleOptional with\n[ignoreMe This field is ignored Why not?]\n[opt null]\n[opt2 null]\n[opt3 null]\n[opt4 null]\n")
													.//
													nullRejected("was null").//
													accepting(ALL_NULL).//
													rejecting(//
															value(OTHER_TYPE).//
																	withMessage("was \"\""), //
															value(ALL_NOT_EMPTY).//
																	withMessage(
																			"[opt was <Optional[1]>]\n[opt2 was <Optional[Pojo1 [msg1=null, msg2=null, msg3=0, msg4=null, msg5=null, msg6=null, msg7=null, msg8=null, msg9=null, msg12=null, myBoolean=false, oneBoolean=false]]>]\n[opt3 was <Optional[]>]\n[opt4 was <Optional[]>]\n"), //
															value(ALL_EMPTY_BUT_IGNORE).//
																	withMessage(
																			"[opt was <Optional.empty>]\n[opt2 was <Optional.empty>]\n[opt3 was <Optional.empty>]\n[opt4 was <Optional.empty>]\n")), //
							matcher(//
									(SampleOptionalMatchers.SampleOptionalMatcher) SampleOptionalMatchers
											.sampleOptionalWithSameValue(ALL_NOT_EMPTY)).//
													describedAs(
															"an instance of ch.powerunit.extensions.matchers.samples.SampleOptional with\n[ignoreMe This field is ignored Why not?]\n[opt optional is present and [is \"1\"]]\n[opt2 optional is present and [an instance of ch.powerunit.extensions.matchers.samples.Pojo1 with\n[msg1 is null]\n[msg12 null]\n[msg2 is null]\n[msg3 is <0>]\n[msg4 is null]\n[msg5 is null]\n[msg6 null]\n[msg7 null]\n[msg8 is null]\n[msg9 null]\n[myBoolean is <false>]\n[oneBoolean is <false>]\n]]\n[opt3 optional is present and [is <>]]\n[opt4 optional is present and [an instance of ch.powerunit.extensions.matchers.samples.extension.MyTestWithoutGeneric2 with\n[parent ANYTHING]\n[test is null]\n]]\n")
													.//
													nullRejected("was null").//
													accepting(ALL_NOT_EMPTY).//
													rejecting(//
															value(OTHER_TYPE).//
																	withMessage("was \"\""), //
															value(ALL_NULL).//
																	withMessage(
																			"[opt was null]\n[opt2 was null]\n[opt3 was null]\n[opt4 was null]\n"), //
															value(ALL_EMPTY_BUT_IGNORE).//
																	withMessage(
																			"[opt was <Optional.empty>]\n[opt2 was <Optional.empty>]\n[opt3 was <Optional.empty>]\n[opt4 was <Optional.empty>]\n"), //
															value(ALL_NOT_EMPTY_V2).//
																	withMessage(
																			"[opt was <Optional[2]>]\n[opt4 was <Optional[]>]\n")) //

					);

}