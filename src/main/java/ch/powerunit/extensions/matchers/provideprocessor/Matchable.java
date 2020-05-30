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
package ch.powerunit.extensions.matchers.provideprocessor;

public interface Matchable {
	String getMethodShortClassName();

	boolean hasWithSameValue();

	String getFullyQualifiedNameOfGeneratedClass();

	String getSimpleNameOfGeneratedInterfaceMatcher();

	default long getCompatibility() {
		// 0x01 : withSameValue also provides a version to ignore fields
		return 0x01;
	}

	default String getMethodNameDSLWithSameValue() {
		return getMethodShortClassName() + "WithSameValue";
	}

	default String getMethodNameDSLWithParent() {
		return getMethodShortClassName() + "WithParent";
	}

	default String getWithSameValue(boolean hasReference) {
		return getFullyQualifiedNameOfGeneratedClass() + (hasReference ? "::" : ".") + getMethodNameDSLWithSameValue();
	}

	default boolean supportIgnore() {
		return (getCompatibility() & 0x01) == 0x01;
	}

	static Matchable of(String fullName, String methodName, String interfaceName, boolean hasWithSameValue,
			long compatibility) {
		return new Matchable() {

			@Override
			public boolean hasWithSameValue() {
				return hasWithSameValue;
			}

			@Override
			public String getSimpleNameOfGeneratedInterfaceMatcher() {
				return interfaceName;
			}

			@Override
			public String getMethodShortClassName() {
				return methodName;
			}

			@Override
			public String getFullyQualifiedNameOfGeneratedClass() {
				return fullName;
			}

			@Override
			public long getCompatibility() {
				return compatibility;
			}
		};
	}
}
