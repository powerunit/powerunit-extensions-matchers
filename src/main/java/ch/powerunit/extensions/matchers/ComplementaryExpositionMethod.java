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

/**
 * Enumeration usable to specify more DSL method for an Object.
 * 
 * @author borettim
 * @since 0.1.0
 */
public enum ComplementaryExpositionMethod {
	/**
	 * This can be used to indicate that, for the annotated element, method
	 * named {@code containsXXX} that returns Matcher for {@code Iterable}.
	 * 
	 */
	CONTAINS, //
	/**
	 * This can be used to indicate that, for the annotated element, method
	 * named {@code arrayContainingXXX} that returns Matcher for array.
	 * 
	 */
	ARRAYCONTAINING
}
