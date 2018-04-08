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
package ch.powerunit.extensions.matchers.provideprocessor.extension;

import java.util.Collection;
import java.util.function.Supplier;

import ch.powerunit.extensions.matchers.ComplementaryExpositionMethod;
import ch.powerunit.extensions.matchers.provideprocessor.DSLMethod;
import ch.powerunit.extensions.matchers.provideprocessor.ProvidesMatchersAnnotatedElementData;

public class ContainsDSLExtension implements DSLExtension {

	public static final String CONTAINS_MATCHER = "org.hamcrest.Matchers.contains";

	private static final String JAVADOC_DESCRIPTION = "Generate a contains matcher for this Object.";

	@Override
	public ComplementaryExpositionMethod supportedEnum() {
		return ComplementaryExpositionMethod.CONTAINS;
	}

	@Override
	public Collection<Supplier<DSLMethod>> getDSLMethodFor(ProvidesMatchersAnnotatedElementData element) {
		String targetName = element.getFullyQualifiedNameOfClassAnnotatedWithProvideMatcherWithGeneric();
		String returnType = element.getFullGeneric() + " org.hamcrest.Matcher<java.lang.Iterable<? extends "
				+ targetName + ">>";
		String methodName = element.generateDSLMethodName("contains");
		String targetMethodName = element.generateDSLWithSameValueMethodName();
		return new ContainsSupplier(targetName, returnType, methodName, targetMethodName).asSuppliers();
	}

	public class ContainsSupplier extends AbstractContainsDSLExtensionSupplier {

		public ContainsSupplier(String targetName, String returnType, String methodName, String targetMethodName) {
			super(targetName, returnType, methodName, targetMethodName);
		}

		@Override
		public String getJavaDocDescription() {
			return JAVADOC_DESCRIPTION;
		}

		@Override
		public String getMatcher() {
			return CONTAINS_MATCHER;
		}

	}

}