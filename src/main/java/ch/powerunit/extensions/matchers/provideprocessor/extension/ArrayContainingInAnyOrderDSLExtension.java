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

import java.util.Arrays;
import java.util.Collection;
import java.util.function.Supplier;

import ch.powerunit.extensions.matchers.ComplementaryExpositionMethod;
import ch.powerunit.extensions.matchers.provideprocessor.DSLMethod;
import ch.powerunit.extensions.matchers.provideprocessor.ProvidesMatchersAnnotatedElementData;

public class ArrayContainingInAnyOrderDSLExtension implements DSLExtension {

	public static final String ARRAYCONTAINS_MATCHER = "org.hamcrest.Matchers.arrayContainingInAnyOrder";

	private static final String JAVADOC_DESCRIPTION = "Generate an array contains in any order matcher for this Object.";

	@Override
	public ComplementaryExpositionMethod supportedEnum() {
		return ComplementaryExpositionMethod.ARRAYCONTAINING;
	}

	@Override
	public Collection<Supplier<DSLMethod>> getDSLMethodFor(ProvidesMatchersAnnotatedElementData element) {
		String targetName = element.getFullyQualifiedNameOfClassAnnotatedWithProvideMatcherWithGeneric();
		String returnType = element.getFullGeneric() + " org.hamcrest.Matcher<" + targetName + "[]>";
		String methodName = element.generateDSLMethodName("arrayContainingInAnyOrder");
		String targetMethodName = element.generateDSLWithSameValueMethodName();
		return new ArrrayContainsSupplier(targetName, returnType, methodName, targetMethodName).asSuppliers();
	}

	public class ArrrayContainsSupplier extends AbstractDSLExtensionSupplier {

		public ArrrayContainsSupplier(String targetName, String returnType, String methodName,
				String targetMethodName) {
			super(targetName, returnType, methodName, targetMethodName);
		}

		@Override
		public Collection<Supplier<DSLMethod>> asSuppliers() {
			return Arrays.asList(this::generateContains1, this::generateContains2, this::generateContains3,
					this::generateContainsN);
		}

		public DSLMethod generateContains1() {
			return generateSimpleDSLMethodFor(new String[] { JAVADOC_DESCRIPTION,
					"@param first the element contained inside the target array", "@return the Matcher." },
					ARRAYCONTAINS_MATCHER, "first");
		}

		public DSLMethod generateContains2() {
			return generateSimpleDSLMethodFor(new String[] { JAVADOC_DESCRIPTION,
					"@param first the element contained inside the target array",
					"@param second the second element contained inside the target array", "@return the Matcher." },
					ARRAYCONTAINS_MATCHER, "first", "second");
		}

		public DSLMethod generateContains3() {
			return generateSimpleDSLMethodFor(new String[] { JAVADOC_DESCRIPTION,
					"@param first the element contained inside the target array",
					"@param second the second element contained inside the target array",
					"@param third the third element contained inside the target array", "@return the Matcher." },
					ARRAYCONTAINS_MATCHER, "first", "second", "third");
		}

		public DSLMethod generateContainsN() {
			return new DSLMethod(
					new String[] { JAVADOC_DESCRIPTION,
							"@param first the first element contained inside the target array",
							"@param second the second element contained inside the target array",
							"@param third the third element contained inside the target array",
							"@param last the next element", "@return the Matcher." },
					returnType + " " + methodName, getSeveralParameter(true, "first", "second", "third", "last"),
					new String[] { "java.util.List<org.hamcrest.Matcher<" + targetName
							+ ">> tmp = new java.util.ArrayList<>(java.util.Arrays.asList("
							+ getSeveralWith("first", "second", "third") + "));",
							"tmp.addAll(java.util.Arrays.stream(last).map(v->" + targetMethodName
									+ "(v)).collect(java.util.stream.Collectors.toList()));",
							"return " + ARRAYCONTAINS_MATCHER + "(tmp.toArray(new org.hamcrest.Matcher[0]));" });
		}

	}

}