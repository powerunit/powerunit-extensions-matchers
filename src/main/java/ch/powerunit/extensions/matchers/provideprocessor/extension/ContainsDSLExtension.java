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
			return new DSLMethod(
					new String[] { "Generate a contains matcher for this Object.",
							"@param first the element contained inside the target iterable", "@return the Matcher." },
					returnType + " " + methodName, getOneParameter("first"),
					"return " + CONTAINS_MATCHER + "(" + getOneWith("first") + ");");
		}

		public DSLMethod generateContains2() {
			return new DSLMethod(
					new String[] { "Generate a contains matcher for this Object.",
							"@param first the first element contained inside the target iterable",
							"@param second the second element contained inside the target iterable",
							"@return the Matcher." },
					returnType + " " + methodName,
					new String[][] { getOneParameter("first"), getOneParameter("second") },
					"return " + CONTAINS_MATCHER + "(" + getOneWith("first") + "," + getOneWith("second") + ");");
		}

		public DSLMethod generateContains3() {
			return new DSLMethod(
					new String[] { "Generate a contains matcher for this Object.",
							"@param first the first element contained inside the target iterable",
							"@param second the second element contained inside the target iterable",
							"@param third the third element contained inside the target iterable",
							"@return the Matcher." },
					returnType + " " + methodName,
					new String[][] { getOneParameter("first"), getOneParameter("second"), getOneParameter("third") },
					"return " + CONTAINS_MATCHER + "(" + getOneWith("first") + "," + getOneWith("second") + ","
							+ getOneWith("third") + ");");
		}

		public DSLMethod generateContainsN() {
			String last[] = getOneParameter("last");
			last[0] += "...";
			return new DSLMethod(
					new String[] { "Generate a contains matcher for this Object.",
							"@param first the first element contained inside the target iterable",
							"@param second the second element contained inside the target iterable",
							"@param third the third element contained inside the target iterable",
							"@param last the next element", "@return the Matcher." },
					returnType + " " + methodName,
					new String[][] { getOneParameter("first"), getOneParameter("second"), getOneParameter("third"),
							last },
					new String[] {
							"java.util.List<org.hamcrest.Matcher<" + targetName
									+ ">> tmp = new java.util.ArrayList<>();",
							"tmp.add(" + getOneWith("first") + ");", "tmp.add(" + getOneWith("second") + ");",
							"tmp.add(" + getOneWith("third") + ");",
							"tmp.addAll(java.util.Arrays.stream(last).map(v->" + targetMethodName
									+ "(v)).collect(java.util.stream.Collectors.toList()));",
							"return " + CONTAINS_MATCHER + "(tmp.toArray(new org.hamcrest.Matcher[0]));" });
		}
	}

}