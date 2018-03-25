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

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;

import ch.powerunit.Ignore;
import ch.powerunit.Test;
import ch.powerunit.TestDelegate;
import ch.powerunit.TestSuite;
import ch.powerunit.matchers.MatcherTester;

public class Pojo1MatcherTest implements TestSuite {

	@TestDelegate
	public final MatcherTester<?> tester = testerOfMatcher(Pojo1Matchers.Pojo1MatcherImpl.class).with(
			matcher((Pojo1Matchers.Pojo1MatcherImpl) Pojo1Matchers.pojo1With().msg1ContainsString("12"))
					.describedAs(
							"an instance of ch.powerunit.extensions.matchers.samples.Pojo1 with\n[msg4 ANYTHING]\n[msg3 ANYTHING]\n[msg2 ANYTHING]\n[msg1 a string containing \"12\"]\n[msg8 ANYTHING]\n[msg7 ANYTHING]\n[msg6 ANYTHING]\n[msg12 ANYTHING]\n[msg5 ANYTHING]\n[oneBoolean ANYTHING]\n[myBoolean ANYTHING]\n[msg9 ANYTHING]\n")
					.nullRejected("was null").accepting(new Pojo1("12"), new Pojo1("121"))
					.rejecting(value("").withMessage("was \"\""), value(new Pojo1()).withMessage("[msg1 was null]\n"),
							value(new Pojo1("11")).withMessage("[msg1 was \"11\"]\n")),
			matcher((Pojo1Matchers.Pojo1MatcherImpl) Pojo1Matchers.pojo1With().msg6IsEmptyIterable())
					.describedAs(
							"an instance of ch.powerunit.extensions.matchers.samples.Pojo1 with\n[msg4 ANYTHING]\n[msg3 ANYTHING]\n[msg2 ANYTHING]\n[msg1 ANYTHING]\n[msg8 ANYTHING]\n[msg7 ANYTHING]\n[msg6 an empty iterable]\n[msg12 ANYTHING]\n[msg5 ANYTHING]\n[oneBoolean ANYTHING]\n[myBoolean ANYTHING]\n[msg9 ANYTHING]\n")
					.nullRejected("was null").accepting(new Pojo1(Collections.emptyList()))
					.rejecting(value(new Pojo1(Collections.singletonList("x"))).withMessage("[msg6 [\"x\"]]\n")));

	@Test
	public void testOKMatcher() {
		Pojo1 p = new Pojo1();
		assertThat(p).is(Pojo1Matchers.pojo1With());
	}

	@Test
	public void testOKMatcherWithLinkedMatcher() {
		Pojo1 p = new Pojo1();
		assertThat(p).is(Pojo1Matchers.pojo1With().andWith(notNullValue()).andWith(hasToString(notNullValue()))
				.buildWith(anything()));
	}

	@Test
	public void testOKMatcherWithComparable() {
		Pojo1 p = new Pojo1();
		p.msg2 = "12";
		assertThat(p).is(Pojo1Matchers.pojo1With().msg2ComparesEqualTo("12").build());
	}

	@Test
	public void testKOMatcherWithReference() {
		Pojo1 p1 = new Pojo1();
		p1.msg2 = "12";
		Pojo1 p2 = new Pojo1();
		p2.msg2 = "12";
		assertThat(p1).is(Pojo1Matchers.pojo1WithSameValue(p2));
	}

	@Test
	public void testKOMatcherWithEmptyArray() {
		Pojo1 p1 = new Pojo1();
		p1.msg8 = new List[] {};
		assertThat(p1).is(Pojo1Matchers.pojo1With().msg8IsEmpty());
	}

	@Test
	@Ignore
	public void testKOMatcher() {
		Pojo1 p = new Pojo1();
		assertThat(p).is(Pojo1Matchers.pojo1With().msg1("x"));
	}

	@Factory
	public static void test() {
	}

	@Factory
	public static void test(String name1) {
	}

	@Factory
	public static <T> T test(T input) {
		return input;
	}

	@Factory
	public static <T extends Iterable<K>, K> void test1(T t1, K t2) {
	}

	@Factory
	public static <T extends Iterable<K> & Serializable, K> void test2(T t1, K t2) {
	}

	@Factory
	public static <T extends Iterable<K> & Serializable, K> void test3(T t1, K... t2) {
	}

	@Factory
	public static <T extends String> void test4(T t1) {
	}

	@Factory
	public static <T> Matcher<T> fromMatcher(Matcher<T> input) {
		return null;
	}

	/**
	 * @param name1
	 * @param name2
	 */
	@Factory
	public static void test(String name1, String name2) {
	}

	@Factory
	public static void test(int x) {
	}
}
