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
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation may be use to add DSL syntax for this field to the generated
 * matcher.
 * 
 * @author borettim
 * @since 0.0.7
 */
@Documented
@Retention(RetentionPolicy.SOURCE)
@Target({ ElementType.FIELD, ElementType.METHOD })
@Inherited
@Repeatable(AddToMatchers.class)
public @interface AddToMatcher {
	/**
	 * Specify a suffix to be used after the name generated by the framework.
	 * <p>
	 * The name used inside the DSL will be composed of the fieldName (starting
	 * with a lower case), followed by the value of this suffix. Use an empty
	 * String when no suffix is required.
	 * 
	 * @return the suffix to be used
	 */
	String suffix();

	/**
	 * Specify the argument to be used.
	 * 
	 * @return the argument.
	 */
	String argument();

	/**
	 * Specify the body to be used.
	 * <p>
	 * This method must not return anything ; A {@code return this;} will be
	 * added by the framework at the end.
	 * 
	 * @return the body.
	 */
	String[] body();
}
