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

import java.io.Serializable;
import java.util.List;

import org.hamcrest.Factory;
import org.hamcrest.Matcher;

import ch.powerunit.Ignore;
import ch.powerunit.Test;
import ch.powerunit.TestSuite;

public class Pojo1MatcherTest implements TestSuite {
	
	
	@Test
	public void testOKMatcher() {
		Pojo1 p = new Pojo1();
		assertThat(p).is(Pojo1Matchers.pojo1With());
	}
	
	@Test
	public void testKOMatcherWithReference() {
		Pojo1 p1 = new Pojo1();
		p1.msg2="12";
		Pojo1 p2 = new Pojo1();
		p2.msg2="12";
		assertThat(p1).is(Pojo1Matchers.pojo1WithSameValue(p2));
	}
	
	@Test
	public void testKOMatcherWithEmptyArray() {
		Pojo1 p1 = new Pojo1();
		p1.msg8=new List[]{};
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
	public static <T extends Iterable<K> & Serializable, K> void test2(T t1,
			K t2) {
	}

	@Factory
	public static <T extends Iterable<K> & Serializable, K> void test3(T t1,
			K... t2) {
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
