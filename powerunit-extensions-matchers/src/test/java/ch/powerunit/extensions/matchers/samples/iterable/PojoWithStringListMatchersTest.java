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
public class PojoWithStringListMatchersTest implements TestSuite {

	private static final Object OTHER_TYPE = "";

	private static final PojoWithStringList NULL_LIST = new PojoWithStringList(null);

	private static final PojoWithStringList EMPTY_LIST = new PojoWithStringList(Collections.emptyList());

	private static final PojoWithStringList SINGLE_LIST_WITH_A = new PojoWithStringList(Arrays.asList("A"));

	private static final PojoWithStringList SINGLE_LIST_WITH_B = new PojoWithStringList(Arrays.asList("B"));

	private static final PojoWithStringList SINGLE_LIST_WITH_A_B = new PojoWithStringList(Arrays.asList("A", "B"));

	private static final PojoWithStringList SINGLE_LIST_WITH_B_A = new PojoWithStringList(Arrays.asList("B", "A"));

	@TestDelegate
	public final MatcherTester<?> testMatcher = testerOfMatcher(//
			PojoWithStringListMatchers.PojoWithStringListMatcher.class)//
					.with(//
							matcher(//
									(PojoWithStringListMatchers.PojoWithStringListMatcher) PojoWithStringListMatchers
											.pojoWithStringListWithSameValue(NULL_LIST))
													.//
													describedAs(
															"an instance of ch.powerunit.extensions.matchers.samples.iterable.PojoWithStringList with\n[field null]\n")
													.//
													nullRejected("was null").//
													accepting(NULL_LIST).//
													rejecting(//
															value(OTHER_TYPE).//
																	withMessage("was \"\""), //
															value(EMPTY_LIST).//
																	withMessage("[field was <[]>]\n"), //
															value(SINGLE_LIST_WITH_A).//
																	withMessage("[field was <[A]>]\n")), //
							matcher(//
									(PojoWithStringListMatchers.PojoWithStringListMatcher) PojoWithStringListMatchers
											.pojoWithStringListWithSameValue(EMPTY_LIST))
													.//
													describedAs(
															"an instance of ch.powerunit.extensions.matchers.samples.iterable.PojoWithStringList with\n[field an empty iterable]\n")
													.//
													nullRejected("was null").//
													accepting(EMPTY_LIST).//
													rejecting(//
															value(OTHER_TYPE).//
																	withMessage("was \"\""), //
															value(NULL_LIST).//
																	withMessage("[field was null]\n"), //
															value(SINGLE_LIST_WITH_A).//
																	withMessage("[field [\"A\"]]\n")), //
							matcher(//
									(PojoWithStringListMatchers.PojoWithStringListMatcher) PojoWithStringListMatchers
											.pojoWithStringListWithSameValue(SINGLE_LIST_WITH_A))
													.//
													describedAs(
															"an instance of ch.powerunit.extensions.matchers.samples.iterable.PojoWithStringList with\n[field iterable containing [is \"A\"]]\n")
													.//
													nullRejected("was null").//
													accepting(SINGLE_LIST_WITH_A).//
													rejecting(//
															value(EMPTY_LIST).//
																	withMessage("[field No item matched: is \"A\"]\n"),
															value(OTHER_TYPE).//
																	withMessage("was \"\""), //
															value(NULL_LIST).//
																	withMessage("[field was null]\n"), //
															value(SINGLE_LIST_WITH_A_B).//
																	withMessage("[field Not matched: \"B\"]\n")), //
							matcher(//
									(PojoWithStringListMatchers.PojoWithStringListMatcher) PojoWithStringListMatchers
											.pojoWithStringListWithSameValue(SINGLE_LIST_WITH_A_B))
													.//
													describedAs(
															"an instance of ch.powerunit.extensions.matchers.samples.iterable.PojoWithStringList with\n[field iterable containing [is \"A\", is \"B\"]]\n")
													.//
													nullRejected("was null").//
													accepting(SINGLE_LIST_WITH_A_B).//
													rejecting(//
															value(EMPTY_LIST).//
																	withMessage("[field No item matched: is \"A\"]\n"),
															value(OTHER_TYPE).//
																	withMessage("was \"\""), //
															value(NULL_LIST).//
																	withMessage("[field was null]\n"), //
															value(SINGLE_LIST_WITH_A).//
																	withMessage("[field No item matched: is \"B\"]\n"), //
															value(SINGLE_LIST_WITH_B).//
																	withMessage("[field item 0: was \"B\"]\n"), //
															value(SINGLE_LIST_WITH_B_A).//
																	withMessage("[field item 0: was \"B\"]\n"))//
	);

}