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

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation marks a field or a method as to be ignored in the generated
 * matcher.
 * <p>
 * <b>With this option, the field will be marked as ignored in the resulting
 * matcher.</b>
 * <p>
 * <i>The annotation must be used on the public getter or the field (it this one
 * is public). Having a public field and a public getter should be avoided as in
 * this case it is not sure that this annotation will be detected.</i>
 * 
 * @author borettim
 * @since 0.0.7
 */
@Documented
@Retention(RetentionPolicy.SOURCE)
@Target({ ElementType.FIELD, ElementType.METHOD, ElementType.RECORD_COMPONENT })
@Inherited
public @interface IgnoreInMatcher {

	/**
	 * This attribute may be used to add a comments inside the generated matcher
	 * report indicating why (for example) this field is ignored.
	 * 
	 * @return the comments or an empty string if not set.
	 */
	String comments() default "";

}
