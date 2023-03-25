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
package ch.powerunit.extensions.matchers.api;

/**
 * Enumeration usable to specify more DSL method for an Object.
 * 
 * @author borettim
 * @since 0.1.0
 */
public enum ComplementaryExpositionMethod {
	/**
	 * This can be used to indicate that, for the annotated element, method named
	 * {@code containsXXX} that returns Matcher for {@code Iterable} must be
	 * created.
	 * <p>
	 * For example, for a class {@code Pojo1}, this will add the following elements
	 * to the generated classes :
	 * 
	 * <pre>
	 * 
	 * &#64;org.hamcrest.Factory
	 * public static org.hamcrest.Matcher&lt;java.lang.Iterable&lt;? extends ch.powerunit.extensions.matchers.samples.Pojo1&gt;&gt; containsPojo1(
	 * 		ch.powerunit.extensions.matchers.samples.Pojo1 first) {
	 * 	return org.hamcrest.Matchers.contains(pojo1WithSameValue(first));
	 * }
	 * 
	 * &#64;org.hamcrest.Factory
	 * public static org.hamcrest.Matcher&lt;java.lang.Iterable&lt;? extends ch.powerunit.extensions.matchers.samples.Pojo1&gt;&gt; containsPojo1(
	 * 		ch.powerunit.extensions.matchers.samples.Pojo1 first,
	 * 		ch.powerunit.extensions.matchers.samples.Pojo1 second) {
	 * 	return org.hamcrest.Matchers.contains(pojo1WithSameValue(first), pojo1WithSameValue(second));
	 * }
	 * 
	 * &#64;org.hamcrest.Factory
	 * public static org.hamcrest.Matcher&lt;java.lang.Iterable&lt;? extends ch.powerunit.extensions.matchers.samples.Pojo1&gt;&gt; containsPojo1(
	 * 		ch.powerunit.extensions.matchers.samples.Pojo1 first,
	 * 		ch.powerunit.extensions.matchers.samples.Pojo1 second,
	 * 		ch.powerunit.extensions.matchers.samples.Pojo1 third) {
	 * 	return org.hamcrest.Matchers.contains(pojo1WithSameValue(first), pojo1WithSameValue(second),
	 * 			pojo1WithSameValue(third));
	 * }
	 * 
	 * &#64;org.hamcrest.Factory
	 * public static org.hamcrest.Matcher&lt;java.lang.Iterable&lt;? extends ch.powerunit.extensions.matchers.samples.Pojo1&gt;&gt; containsPojo1(
	 * 		ch.powerunit.extensions.matchers.samples.Pojo1 first,
	 * 		ch.powerunit.extensions.matchers.samples.Pojo1 second,
	 * 		ch.powerunit.extensions.matchers.samples.Pojo1 third,
	 * 		ch.powerunit.extensions.matchers.samples.Pojo1... last) {
	 * 	java.util.List&lt;org.hamcrest.Matcher&lt;ch.powerunit.extensions.matchers.samples.Pojo1&gt;&gt; tmp = new java.util.ArrayList&lt;&gt;(
	 * 			java.util.Arrays.asList(pojo1WithSameValue(first), pojo1WithSameValue(second),
	 * 					pojo1WithSameValue(third)));
	 * 	tmp.addAll(java.util.Arrays.stream(last).map(v -&gt; pojo1WithSameValue(v))
	 * 			.collect(java.util.stream.Collectors.toList()));
	 * 	return org.hamcrest.Matchers.contains(tmp.toArray(new org.hamcrest.Matcher[0]));
	 * }
	 * 
	 * &#64;org.hamcrest.Factory
	 * public static org.hamcrest.Matcher&lt;java.lang.Iterable&lt;? extends ch.powerunit.extensions.matchers.samples.Pojo1&gt;&gt; containsInAnyOrderPojo1(
	 * 		ch.powerunit.extensions.matchers.samples.Pojo1 first) {
	 * 	return org.hamcrest.Matchers.containsInAnyOrder(pojo1WithSameValue(first));
	 * }
	 * 
	 * &#64;org.hamcrest.Factory
	 * public static org.hamcrest.Matcher&lt;java.lang.Iterable&lt;? extends ch.powerunit.extensions.matchers.samples.Pojo1&gt;&gt; containsInAnyOrderPojo1(
	 * 		ch.powerunit.extensions.matchers.samples.Pojo1 first,
	 * 		ch.powerunit.extensions.matchers.samples.Pojo1 second) {
	 * 	return org.hamcrest.Matchers.containsInAnyOrder(pojo1WithSameValue(first), pojo1WithSameValue(second));
	 * }
	 * 
	 * &#64;org.hamcrest.Factory
	 * public static org.hamcrest.Matcher&lt;java.lang.Iterable&lt;? extends ch.powerunit.extensions.matchers.samples.Pojo1&gt;&gt; containsInAnyOrderPojo1(
	 * 		ch.powerunit.extensions.matchers.samples.Pojo1 first,
	 * 		ch.powerunit.extensions.matchers.samples.Pojo1 second,
	 * 		ch.powerunit.extensions.matchers.samples.Pojo1 third) {
	 * 	return org.hamcrest.Matchers.containsInAnyOrder(pojo1WithSameValue(first), pojo1WithSameValue(second),
	 * 			pojo1WithSameValue(third));
	 * }
	 * 
	 * &#64;org.hamcrest.Factory
	 * public static org.hamcrest.Matcher&lt;java.lang.Iterable&lt;? extends ch.powerunit.extensions.matchers.samples.Pojo1&gt;&gt; containsInAnyOrderPojo1(
	 * 		ch.powerunit.extensions.matchers.samples.Pojo1 first,
	 * 		ch.powerunit.extensions.matchers.samples.Pojo1 second,
	 * 		ch.powerunit.extensions.matchers.samples.Pojo1 third,
	 * 		ch.powerunit.extensions.matchers.samples.Pojo1... last) {
	 * 	java.util.List&lt;org.hamcrest.Matcher&lt;ch.powerunit.extensions.matchers.samples.Pojo1&gt;&gt; tmp = new java.util.ArrayList&lt;&gt;(
	 * 			java.util.Arrays.asList(pojo1WithSameValue(first), pojo1WithSameValue(second),
	 * 					pojo1WithSameValue(third)));
	 * 	tmp.addAll(java.util.Arrays.stream(last).map(v -&gt; pojo1WithSameValue(v))
	 * 			.collect(java.util.stream.Collectors.toList()));
	 * 	return org.hamcrest.Matchers.containsInAnyOrder(tmp.toArray(new org.hamcrest.Matcher[0]));
	 * }
	 * 
	 * </pre>
	 * 
	 */
	CONTAINS, //
	/**
	 * This can be used to indicate that, for the annotated element, method named
	 * {@code arrayContainingXXX} that returns Matcher for array must be created.
	 * <p>
	 * For example, for a class {@code Pojo1}, this will add the following elements
	 * to the generated classes :
	 * 
	 * <pre>
	 * &#64;org.hamcrest.Factory
	 * public static org.hamcrest.Matcher&lt;ch.powerunit.extensions.matchers.samples.Pojo1[]&gt; arrayContainingPojo1(
	 * 		ch.powerunit.extensions.matchers.samples.Pojo1 first) {
	 * 	return org.hamcrest.Matchers.arrayContaining(pojo1WithSameValue(first));
	 * }
	 *
	 * &#64;org.hamcrest.Factory
	 * public static org.hamcrest.Matcher&lt;ch.powerunit.extensions.matchers.samples.Pojo1[]&gt; arrayContainingPojo1(
	 * 		ch.powerunit.extensions.matchers.samples.Pojo1 first,
	 * 		ch.powerunit.extensions.matchers.samples.Pojo1 second) {
	 * 	return org.hamcrest.Matchers.arrayContaining(pojo1WithSameValue(first), pojo1WithSameValue(second));
	 * }
	 *
	 * &#64;org.hamcrest.Factory
	 * public static org.hamcrest.Matcher&lt;ch.powerunit.extensions.matchers.samples.Pojo1[]&gt; arrayContainingPojo1(
	 * 		ch.powerunit.extensions.matchers.samples.Pojo1 first,
	 * 		ch.powerunit.extensions.matchers.samples.Pojo1 second,
	 * 		ch.powerunit.extensions.matchers.samples.Pojo1 third) {
	 * 	return org.hamcrest.Matchers.arrayContaining(pojo1WithSameValue(first), pojo1WithSameValue(second),
	 * 			pojo1WithSameValue(third));
	 * }
	 *
	 * &#64;org.hamcrest.Factory
	 * public static org.hamcrest.Matcher&lt;ch.powerunit.extensions.matchers.samples.Pojo1[]&gt; arrayContainingPojo1(
	 * 		ch.powerunit.extensions.matchers.samples.Pojo1 first,
	 * 		ch.powerunit.extensions.matchers.samples.Pojo1 second,
	 * 		ch.powerunit.extensions.matchers.samples.Pojo1 third,
	 * 		ch.powerunit.extensions.matchers.samples.Pojo1... last) {
	 * 	java.util.List&lt;org.hamcrest.Matcher&lt;ch.powerunit.extensions.matchers.samples.Pojo1&gt;&gt; tmp = new java.util.ArrayList&lt;&gt;(
	 * 			java.util.Arrays.asList(pojo1WithSameValue(first), pojo1WithSameValue(second),
	 * 					pojo1WithSameValue(third)));
	 * 	tmp.addAll(java.util.Arrays.stream(last).map(v -&gt; pojo1WithSameValue(v))
	 * 			.collect(java.util.stream.Collectors.toList()));
	 * 	return org.hamcrest.Matchers.arrayContaining(tmp.toArray(new org.hamcrest.Matcher[0]));
	 * }
	 *
	 * &#64;org.hamcrest.Factory
	 * public static org.hamcrest.Matcher&lt;ch.powerunit.extensions.matchers.samples.Pojo1[]&gt; arrayContainingInAnyOrderPojo1(
	 * 		ch.powerunit.extensions.matchers.samples.Pojo1 first) {
	 * 	return org.hamcrest.Matchers.arrayContainingInAnyOrder(pojo1WithSameValue(first));
	 * }
	 *
	 * &#64;org.hamcrest.Factory
	 * public static org.hamcrest.Matcher&lt;ch.powerunit.extensions.matchers.samples.Pojo1[]&gt; arrayContainingInAnyOrderPojo1(
	 * 		ch.powerunit.extensions.matchers.samples.Pojo1 first,
	 * 		ch.powerunit.extensions.matchers.samples.Pojo1 second) {
	 * 	return org.hamcrest.Matchers.arrayContainingInAnyOrder(pojo1WithSameValue(first),
	 * 			pojo1WithSameValue(second));
	 * }
	 *
	 * &#64;org.hamcrest.Factory
	 * public static org.hamcrest.Matcher&lt;ch.powerunit.extensions.matchers.samples.Pojo1[]&gt; arrayContainingInAnyOrderPojo1(
	 * 		ch.powerunit.extensions.matchers.samples.Pojo1 first,
	 * 		ch.powerunit.extensions.matchers.samples.Pojo1 second,
	 * 		ch.powerunit.extensions.matchers.samples.Pojo1 third) {
	 * 	return org.hamcrest.Matchers.arrayContainingInAnyOrder(pojo1WithSameValue(first), pojo1WithSameValue(second),
	 * 			pojo1WithSameValue(third));
	 * }
	 *
	 * &#64;org.hamcrest.Factory
	 * public static org.hamcrest.Matcher&lt;ch.powerunit.extensions.matchers.samples.Pojo1[]&gt; arrayContainingInAnyOrderPojo1(
	 * 		ch.powerunit.extensions.matchers.samples.Pojo1 first,
	 * 		ch.powerunit.extensions.matchers.samples.Pojo1 second,
	 * 		ch.powerunit.extensions.matchers.samples.Pojo1 third,
	 * 		ch.powerunit.extensions.matchers.samples.Pojo1... last) {
	 * 	java.util.List&lt;org.hamcrest.Matcher&lt;ch.powerunit.extensions.matchers.samples.Pojo1&gt;&gt; tmp = new java.util.ArrayList&lt;&gt;(
	 * 			java.util.Arrays.asList(pojo1WithSameValue(first), pojo1WithSameValue(second),
	 * 					pojo1WithSameValue(third)));
	 * 	tmp.addAll(java.util.Arrays.stream(last).map(v -&gt; pojo1WithSameValue(v))
	 * 			.collect(java.util.stream.Collectors.toList()));
	 * 	return org.hamcrest.Matchers.arrayContainingInAnyOrder(tmp.toArray(new org.hamcrest.Matcher[0]));
	 * }
	 * </pre>
	 * 
	 */
	ARRAYCONTAINING, //

	/**
	 * This can be used to indicate that, for the annotated element, method name
	 * {@code hasItemsXXX} that return hasItems matcher must be created.
	 * <p>
	 * For example, for a class {@code Pojo1}, this will add the following elements
	 * to the generated classes :
	 * 
	 * <pre>
	 * &#64;org.hamcrest.Factory
	 * public static org.hamcrest.Matcher&lt;java.lang.Iterable&lt;ch.powerunit.extensions.matchers.samples.Pojo1&gt;&gt; hasItemsPojo1(
	 * 		ch.powerunit.extensions.matchers.samples.Pojo1... item) {
	 * 	return org.hamcrest.Matchers.hasItems(java.util.Arrays.stream(item).map(v -&gt; pojo1WithSameValue(v))
	 * 			.collect(java.util.stream.Collectors.toList()).toArray(new org.hamcrest.Matcher[0]));
	 * }
	 * </pre>
	 */
	HAS_ITEMS, //

	/**
	 * This can be used to indicate that, for the annotated element, method name
	 * {@code anyOfXXX} that return anyOf matcher must be created.
	 * <p>
	 * For example, for a class {@code Pojo1}, this will add the following elements
	 * to the generated classes :
	 * 
	 * <pre>
	 * &#64;org.hamcrest.Factory
	 * public static org.hamcrest.Matcher&lt;ch.powerunit.extensions.matchers.samples.Pojo1&gt; anyOfPojo1(
	 * 		ch.powerunit.extensions.matchers.samples.Pojo1... items) {
	 * 	return org.hamcrest.Matchers.anyOf(java.util.Arrays.stream(items).map(v -&gt; pojo1WithSameValue(v))
	 * 			.collect(java.util.stream.Collectors.toList()).toArray(new org.hamcrest.Matcher[0]));
	 * }
	 * </pre>
	 */
	ANY_OF, //

	/**
	 * This can be used to indicate that, for the annotated element, method name
	 * {@code noneOf} that return not(anyOf) matcher must be created.
	 * <p>
	 * For example, for a class {@code Pojo1}, this will add the following elements
	 * to the generated classes :
	 * 
	 * <pre>
	 * &#64;org.hamcrest.Factory
	 * public static org.hamcrest.Matcher&lt;ch.powerunit.extensions.matchers.samples.Pojo1&gt; noneOfPojo1(
	 * 		ch.powerunit.extensions.matchers.samples.Pojo1... items) {
	 * 	return org.hamcrest.Matchers
	 * 			.not(org.hamcrest.Matchers.anyOf(java.util.Arrays.stream(items).map(v -&gt; pojo1WithSameValue(v))
	 * 					.collect(java.util.stream.Collectors.toList()).toArray(new org.hamcrest.Matcher[0])));
	 * }
	 * </pre>
	 * 
	 * @since 0.2.0
	 */
	NONE_OF
}
