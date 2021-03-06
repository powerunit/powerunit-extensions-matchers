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

import javax.tools.Diagnostic.Kind;

@FunctionalInterface
public interface ProvidesMatchersAnnotatedElementData extends RoundMirrorSupport {
	ProvidesMatchersAnnotatedElementMatcherMirror getFullData();

	default RoundMirror getRoundMirror() {
		return getFullData().getRoundMirror();
	}

	default String getFullyQualifiedNameOfClassAnnotatedWithProvideMatcherWithGeneric() {
		return getFullData().getFullyQualifiedNameOfClassAnnotatedWithProvideMatcherWithGeneric();
	}

	default String getFullyQualifiedNameOfClassAnnotatedWithProvideMatcher() {
		return getFullData().getFullyQualifiedNameOfClassAnnotated();
	}

	default String getFullGeneric() {
		return getFullData().getFullGeneric();
	}

	default String getGeneric() {
		return getFullData().getGeneric();
	}

	default String getSimpleNameOfClassAnnotatedWithProvideMatcher() {
		return getFullData().getSimpleNameOfClassAnnotated();
	}

	default String getDefaultReturnMethod() {
		return getFullData().getDefaultReturnMethod();
	}

	default String generateDSLMethodName(String prefix) {
		return prefix + getSimpleNameOfClassAnnotatedWithProvideMatcher();
	}

	default String generateDSLWithSameValueMethodName() {
		return getFullData().getMethodNameDSLWithSameValue();
	}

	default boolean hasWithSameValue() {
		return getFullData().hasWithSameValue();
	}

	default void printWarningMessage(String message) {
		getFullData().getMessager().printMessage(Kind.MANDATORY_WARNING, message, getFullData().getElement());
	}

}
