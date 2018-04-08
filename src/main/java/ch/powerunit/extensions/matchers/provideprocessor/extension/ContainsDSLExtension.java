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
import ch.powerunit.extensions.matchers.provideprocessor.ProvidesMatchersAnnotatedElementMirror;

public class ContainsDSLExtension implements DSLExtension {

	@Override
	public ComplementaryExpositionMethod supportedEnum() {
		return ComplementaryExpositionMethod.CONTAINS;
	}

	@Override
	public Collection<Supplier<DSLMethod>> getDSLMethodFor(ProvidesMatchersAnnotatedElementMirror element) {
		String targetName = element.getFullyQualifiedNameOfClassAnnotatedWithProvideMatcherWithGeneric();
		String returnType = element.getFullGeneric() + " org.hamcrest.Matcher<java.lang.Iterable<? extends "
				+ targetName + ">>";
		String methodName = "contains" + element.getSimpleNameOfClassAnnotatedWithProvideMatcher();
		String targetMethodName = element.getMethodShortClassName() + "WithSameValue";
		return Arrays.asList(() -> generateContains1(targetName, targetMethodName, returnType, methodName),
				() -> generateContains2(targetName, targetMethodName, returnType, methodName),
				() -> generateContains3(targetName, targetMethodName, returnType, methodName));
	}

	public static String[] getOneParameter(String targetName, String name) {
		return new String[] { targetName, name };
	}

	public static String getOneWith(String targetMethodName, String name) {
		return targetMethodName + "(" + name + ")";
	}

	public DSLMethod generateContains1(String targetName, String targetMethodName, String returnType,
			String methodName) {
		return new DSLMethod(
				new String[] { "Generate a contains matcher for this Object.",
						"@param first the element contained inside the target iterable", "@return the Matcher." },
				returnType + " " + methodName, getOneParameter(targetName, "first"),
				"return org.hamcrest.Matchers.contains(" + getOneWith(targetMethodName, "first") + ");");
	}

	public DSLMethod generateContains2(String targetName, String targetMethodName, String returnType,
			String methodName) {
		return new DSLMethod(
				new String[] { "Generate a contains matcher for this Object.",
						"@param first the first element contained inside the target iterable",
						"@param second the second element contained inside the target iterable",
						"@return the Matcher." },
				returnType + " " + methodName,
				new String[][] { getOneParameter(targetName, "first"), getOneParameter(targetName, "second") },
				"return org.hamcrest.Matchers.contains(" + getOneWith(targetMethodName, "first") + ","
						+ getOneWith(targetMethodName, "second") + ");");
	}

	public DSLMethod generateContains3(String targetName, String targetMethodName, String returnType,
			String methodName) {
		return new DSLMethod(
				new String[] { "Generate a contains matcher for this Object.",
						"@param first the first element contained inside the target iterable",
						"@param second the second element contained inside the target iterable",
						"@param third the third element contained inside the target iterable", "@return the Matcher." },
				returnType + " " + methodName,
				new String[][] { getOneParameter(targetName, "first"), getOneParameter(targetName, "second"),
						getOneParameter(targetName, "third") },
				"return org.hamcrest.Matchers.contains(" + getOneWith(targetMethodName, "first") + ","
						+ getOneWith(targetMethodName, "second") + "," + getOneWith(targetMethodName, "third") + ");");
	}

}