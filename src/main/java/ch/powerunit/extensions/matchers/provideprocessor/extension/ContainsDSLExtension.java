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

	public class ContainsSupplier extends AbstractDSLExtensionSupplier {

		public ContainsSupplier(String targetName, String returnType, String methodName, String targetMethodName) {
			super(targetName, returnType, methodName, targetMethodName);
		}

		@Override
		public Collection<Supplier<DSLMethod>> asSuppliers() {
			return Arrays.asList(this::generateContains1, this::generateContains2, this::generateContains3,
					this::generateContainsN);
		}

		public DSLMethod generateContains1() {
			return generateSimpleDSLMethodFor(new String[] { JAVADOC_DESCRIPTION,
					"@param first the element contained inside the target iterable", "@return the Matcher." },
					CONTAINS_MATCHER, "first");
		}

		public DSLMethod generateContains2() {
			return generateSimpleDSLMethodFor(new String[] { JAVADOC_DESCRIPTION,
					"@param first the element contained inside the target iterable",
					"@param second the second element contained inside the target iterable", "@return the Matcher." },
					CONTAINS_MATCHER, "first", "second");
		}

		public DSLMethod generateContains3() {
			return generateSimpleDSLMethodFor(new String[] { JAVADOC_DESCRIPTION,
					"@param first the element contained inside the target iterable",
					"@param second the second element contained inside the target iterable",
					"@param third the third element contained inside the target iterable", "@return the Matcher." },
					CONTAINS_MATCHER, "first", "second", "third");
		}

		public DSLMethod generateContainsN() {
			return new DSLMethod(
					new String[] { JAVADOC_DESCRIPTION,
							"@param first the first element contained inside the target iterable",
							"@param second the second element contained inside the target iterable",
							"@param third the third element contained inside the target iterable",
							"@param last the next element", "@return the Matcher." },
					returnType + " " + methodName, getSeveralParameter(true, "first", "second", "third", "last"),
					new String[] {
							"java.util.List<org.hamcrest.Matcher<" + targetName
									+ ">> tmp = new java.util.ArrayList<>(java.util.Arrays.asList("
									+ getSeveralWith("first", "second", "third") + "));",
							"tmp.addAll(java.util.Arrays.stream(last).map(v->" + targetMethodName
									+ "(v)).collect(java.util.stream.Collectors.toList()));",
							"return " + CONTAINS_MATCHER + "(tmp.toArray(new org.hamcrest.Matcher[0]));" });
		}
	}

}