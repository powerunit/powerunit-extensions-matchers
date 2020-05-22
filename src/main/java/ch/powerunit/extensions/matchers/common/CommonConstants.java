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
package ch.powerunit.extensions.matchers.common;

public final class CommonConstants {

	public static String DEFAULT_JAVADOC_FOR_FACTORY = "/**\n * Factories generated.\n * <p> \n * This DSL can be use in several way : \n"
			+ " * <ul> \n * <li>By implementing this interface. In this case, all the methods of this interface will be available inside the implementing class.</li>\n"
			+ " * <li>By refering the static field named {@link #DSL} which expose all the DSL method.</li>\n * </ul> \n */";

	private CommonConstants() {
	}
}
