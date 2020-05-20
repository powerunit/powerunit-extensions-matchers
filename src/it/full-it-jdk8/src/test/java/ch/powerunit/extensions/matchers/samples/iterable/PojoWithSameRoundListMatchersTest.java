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
package ch.powerunit.extensions.matchers.samples.iterable;

import static ch.powerunit.matchers.MatcherTester.matcher;
import static ch.powerunit.matchers.MatcherTester.value;

import java.util.Arrays;
import java.util.Collections;

import ch.powerunit.TestDelegate;
import ch.powerunit.TestSuite;
import ch.powerunit.matchers.MatcherTester;

/**
 * @author borettim
 *
 */
public class PojoWithSameRoundListMatchersTest implements TestSuite {

	private static final Object OTHER_TYPE = "";

	private static final PojoWithSameRoundList NULL_LIST = new PojoWithSameRoundList(null);

	private static final PojoWithSameRoundList EMPTY_LIST = new PojoWithSameRoundList(Collections.emptyList());

	private static final PojoWithSameRoundList SINGLE_LIST_WITH_A = new PojoWithSameRoundList(
			Arrays.asList(new PojoWithStringList(Arrays.asList("A"))));

	private static final PojoWithSameRoundList SINGLE_LIST_WITH_B = new PojoWithSameRoundList(
			Arrays.asList(new PojoWithStringList(Arrays.asList("B"))));

	private static final PojoWithSameRoundList SINGLE_LIST_WITH_A_B = new PojoWithSameRoundList(
			Arrays.asList(new PojoWithStringList(Arrays.asList("A")), new PojoWithStringList(Arrays.asList("B"))));

	private static final PojoWithSameRoundList SINGLE_LIST_WITH_B_A = new PojoWithSameRoundList(
			Arrays.asList(new PojoWithStringList(Arrays.asList("B")), new PojoWithStringList(Arrays.asList("A"))));

	@TestDelegate
	public final MatcherTester<?> testMatcher = testerOfMatcher(//
			PojoWithSameRoundListMatchers.PojoWithSameRoundListMatcher.class)//
					.with(//
							matcher(//
									(PojoWithSameRoundListMatchers.PojoWithSameRoundListMatcher) PojoWithSameRoundListMatchers
											.pojoWithSameRoundListWithSameValue(NULL_LIST))
													.//
													describedAs(
															"an instance of ch.powerunit.extensions.matchers.samples.iterable.PojoWithSameRoundList with\n[field null]\n")
													.//
													nullRejected("was null").//
													accepting(NULL_LIST).//
													rejecting(//
															value(OTHER_TYPE).//
																	withMessage("was \"\""), //
															value(EMPTY_LIST).//
																	withMessage("[field was <[]>]\n"), //
															value(SINGLE_LIST_WITH_A).//
																	withMessage(
																			"[field was <[PojoWithStringList [field=[A]]]>]\n")), //
							matcher(//
									(PojoWithSameRoundListMatchers.PojoWithSameRoundListMatcher) PojoWithSameRoundListMatchers
											.pojoWithSameRoundListWithSameValue(EMPTY_LIST))
													.//
													describedAs(
															"an instance of ch.powerunit.extensions.matchers.samples.iterable.PojoWithSameRoundList with\n[field an empty iterable]\n")
													.//
													nullRejected("was null").//
													accepting(EMPTY_LIST).//
													rejecting(//
															value(OTHER_TYPE).//
																	withMessage("was \"\""), //
															value(NULL_LIST).//
																	withMessage("[field was null]\n"), //
															value(SINGLE_LIST_WITH_A).//
																	withMessage(
																			"[field [<PojoWithStringList [field=[A]]>]]\n")), //
							matcher(//
									(PojoWithSameRoundListMatchers.PojoWithSameRoundListMatcher) PojoWithSameRoundListMatchers
											.pojoWithSameRoundListWithSameValue(SINGLE_LIST_WITH_A))
													.//
													describedAs(
															"an instance of ch.powerunit.extensions.matchers.samples.iterable.PojoWithSameRoundList with\n[field iterable containing [an instance of ch.powerunit.extensions.matchers.samples.iterable.PojoWithStringList with\n[field iterable containing [is \"A\"]]\n]]\n")
													.//
													nullRejected("was null").//
													accepting(SINGLE_LIST_WITH_A).//
													rejecting(//
															value(EMPTY_LIST).//
																	withMessage(
																			"[field No item matched: an instance of ch.powerunit.extensions.matchers.samples.iterable.PojoWithStringList with\n[field iterable containing [is \"A\"]]\n]\n"),
															value(OTHER_TYPE).//
																	withMessage("was \"\""), //
															value(NULL_LIST).//
																	withMessage("[field was null]\n"), //
															value(SINGLE_LIST_WITH_A_B).//
																	withMessage(
																			"[field Not matched: <PojoWithStringList [field=[B]]>]\n")), //
							matcher(//
									(PojoWithSameRoundListMatchers.PojoWithSameRoundListMatcher) PojoWithSameRoundListMatchers
											.pojoWithSameRoundListWithSameValue(SINGLE_LIST_WITH_A_B))
													.//
													describedAs(
															"an instance of ch.powerunit.extensions.matchers.samples.iterable.PojoWithSameRoundList with\n[field iterable containing [an instance of ch.powerunit.extensions.matchers.samples.iterable.PojoWithStringList with\n[field iterable containing [is \"A\"]]\n, an instance of ch.powerunit.extensions.matchers.samples.iterable.PojoWithStringList with\n[field iterable containing [is \"B\"]]\n]]\n")
													.//
													nullRejected("was null").//
													accepting(SINGLE_LIST_WITH_A_B).//
													rejecting(//
															value(EMPTY_LIST).//
																	withMessage(
																			"[field No item matched: an instance of ch.powerunit.extensions.matchers.samples.iterable.PojoWithStringList with\n[field iterable containing [is \"A\"]]\n]\n"),
															value(OTHER_TYPE).//
																	withMessage("was \"\""), //
															value(NULL_LIST).//
																	withMessage("[field was null]\n"), //
															value(SINGLE_LIST_WITH_A).//
																	withMessage(
																			"[field No item matched: an instance of ch.powerunit.extensions.matchers.samples.iterable.PojoWithStringList with\n[field iterable containing [is \"B\"]]\n]\n"), //
															value(SINGLE_LIST_WITH_B).//
																	withMessage(
																			"[field item 0: [field item 0: was \"B\"]\n]\n"), //
															value(SINGLE_LIST_WITH_B_A).//
																	withMessage(
																			"[field item 0: [field item 0: was \"B\"]\n]\n"))//
	);

}