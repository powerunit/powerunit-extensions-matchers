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
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.Inherited;

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