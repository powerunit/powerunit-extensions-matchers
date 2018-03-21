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
package ch.powerunit.extensions.matchers;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation can be used on a java class, to mark this class as supporting
 * generation of hamcrest matcher.
 * <p>
 * <b>This annotation is not supported on interface and enum. A warning will be
 * displayed in this case.</b>
 * <p>
 * This annotation is processed by an annotation processor, in order to generate
 * :
 * <ul>
 * <li>One class for each annotated classes, that will contains Hamcrest
 * Matchers for the class.</li>
 * <li>In case the annotation processor parameter
 * "{@code ch.powerunit.extensions.matchers.provideprocessor.ProvidesMatchersAnnotationsProcessor.factory}"
 * is set, this value define the fully qualified name of a interface that will
 * be generated and will contains all <i>start method</i> allowing to create
 * instance of the various matchers.</li>
 * </ul>
 * <p>
 * <i>The generated classes are related with the hamcrest framework ; This
 * library will be required in the classpath in order to compile or run the
 * generated classes.</i>
 * <p>
 * <b>Concept regarding the generated Matchers</b>
 * <p>
 * Hamcrest Matchers can be used, for example, with test framework (JUnit,
 * PowerUnit, etc.) to validate expectation on object. Hamcrest provides several
 * matchers to validate some information of an object (is an instance of, is, an
 * array contains some value, etc.), but can't provide ready to use matcher for
 * your own object. When trying to validate properties of object, no syntaxic
 * sugar (ie. autocompletion) are available and only the generic method can be
 * used.
 * <p>
 * With this annotation, it is possible to provide <i>builder-like</i> method,
 * based on hamcrest, to validate fields of an object. To do so, the annotation
 * processor do the following :
 * <ul>
 * <li>For each public field or public method starting with {@code get} or
 * {@code is}, generated a private matcher based on the
 * {@link org.hamcrest.FeatureMatcher} for this field ; this will provide a way
 * to validate the value of one specific <i>property</i>.</li>
 * <li>Generate an interface and the related implementation of a matcher (which
 * is also a builder) on the annotated classes itself, which will validate all
 * of the <i>properties</i>.</li>
 * <li>Generate various methods, with a name based on the annotated class, to
 * start the creation of the matcher.</li>
 * </ul>
 * <i>First example</i>
 * <p>
 * Let's assume the following class, containing one single field, will be
 * processed by the annotation processor :
 * 
 * <pre>
 * package ch.powerunit.extensions.matchers.samples;
 *
 * import ch.powerunit.extensions.matchers.ProvideMatchers;
 *
 * &#64;ProvideMatchers
 * public class SimplePojo {
 * 	public String oneField;
 * }
 * </pre>
 * 
 * In this case a class named {@code SimplePojoMatchers} will be generated. As a
 * public interface, the following methods will be available :
 * <ul>
 * <li>{@code public static SimplePojoMatcher simplePojoWith()}: This will
 * return a matcher (see below), which by default matches any instance of the
 * SimplePojo class.</li>
 * <li>
 * {@code public static SimplePojoMatcher simplePojoWithSameValue(SimplePojo  other)}
 * : This will return a matcher, which by default matches an instance of the
 * SimplePojo having the field {@code oneField} matching (Matcher {@code is} of
 * hamcrest) of the reference object.</li>
 * </ul>
 * The returned interface is already a correct hamcrest matcher. This interface
 * provide method that set the expectating on the various fields. As in this
 * case, where is only one field, the returned interface ensure that once the
 * expected is defined, it is not possible to modify it. Depending of the type
 * of the field, various methods are generated to define the expectation :
 * <ul>
 * <li>Two standards methods are defined for all type of fields : {@code Matcher
 * <SimplePojo> oneField(Matcher<? super java.lang.String> matcher)} and
 * {@code Matcher<SimplePojo> oneField(String value)}. The second one is a
 * shortcut to validate the field with the {@code is} Matcher and the first one
 * accept another matcher ; The method with matcher parameter ensures that it is
 * possible to combine any other matcher provided by hamcrest or any others
 * extensions.
 * <li>As the field is a String, others special expectation (shortcut) are
 * provided, for example : {@code oneFieldComparesEqualTo},
 * {@code oneFieldLessThan}, {@code oneFieldStartsWith}, etc.</li>
 * </ul>
 * <i>Second example</i> In case the annotated contains several fields, the
 * generated <i>DSL</i> provide chained methods, for example
 * {@code TwoFieldsPojoMatcher firstField(Matcher<? super String> matcher)} and
 * {@code TwoFieldsPojoMatcher secondField(Matcher<? super String> matcher)}.
 * 
 * Also, depending on the class, other <i>with</i> methods may be provided.
 * <p>
 * <i>Usage example</i> Assuming powerunit as a test framework, the usage of the
 * matcher will look like :
 * 
 * <pre>
 * &#64;Test
 * public void testOKMatcherWithComparable() {
 * 	Pojo1 p = new Pojo1();
 * 	p.msg2 = "12";
 * 	assertThat(p).is(Pojo1Matchers.pojo1With().msg2ComparesEqualTo("12"));
 * }
 * 
 * </pre>
 * 
 * Assuming the {@code msg2} is change to the value {code 11}, the resulting
 * unit test error will look like (the Pojo1 classes contains several fields) :
 * 
 * <pre>
 * expecting an instance of ch.powerunit.extensions.matchers.samples.Pojo1 with
 * [msg2 a value equal to "12"]
 * [msg3 ANYTHING]
 * [msg4 ANYTHING]
 * [msg5 ANYTHING]
 * [msg6 ANYTHING]
 * [msg7 ANYTHING]
 * [msg8 ANYTHING]
 * [msg9 ANYTHING]
 * [msg12 ANYTHING]
 * [msg1 ANYTHING]
 * [myBoolean ANYTHING]
 * [oneBoolean ANYTHING]
 * but [msg2 "11" was less than "12"]
 * 
 * </pre>
 * 
 * <hr>
 * <p>
 * <b>Overriding the way the matchers are generated</b>
 * <ul>
 * <li>The attribute {@link #matchersClassName() matchersClassName} may be used
 * to change the simple name (<b>NOT THE FULLY QUALIFIED NAME</b>) of the
 * generated class.</li>
 * </ul>
 * 
 * @author borettim
 *
 */
@Documented
@Retention(RetentionPolicy.SOURCE)
@Target({ ElementType.TYPE })
@Inherited
public @interface ProvideMatchers {
	/**
	 * This attribute may be used to override the default class name that will
	 * contains the generated matchers.
	 * <p>
	 * <i>By default, this attribute is an empty string, which indicate to use
	 * the default construction pattern.</i>
	 * <p>
	 * By default, the Matchers class name is the name of the annotated class,
	 * followed by {@code Matchers}.
	 * 
	 * @return the name of the matchers class or an empty string if this is not
	 *         overloaded.
	 */
	String matchersClassName() default "";

}