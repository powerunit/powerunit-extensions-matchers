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

import ch.powerunit.extensions.matchers.provideprocessor.DSLMethod;
import ch.powerunit.extensions.matchers.provideprocessor.ProvidesMatchersAnnotatedElementData;

public class ArrayContaingDSLExtensionSupplier extends AbstractDSLExtensionSupplier {

	private final String javadoc;
	private final String matcher;

	public ArrayContaingDSLExtensionSupplier(ProvidesMatchersAnnotatedElementData element, String methodName,
			String javadoc, String matcher) {
		super(element.getFullyQualifiedNameOfClassAnnotatedWithProvideMatcherWithGeneric(),
				element.getFullGeneric() + " org.hamcrest.Matcher<"
						+ element.getFullyQualifiedNameOfClassAnnotatedWithProvideMatcherWithGeneric() + "[]>",
				methodName, element.generateDSLWithSameValueMethodName());
		this.javadoc = javadoc;
		this.matcher = matcher;
	}

	public String getJavaDocDescription() {
		return javadoc;
	}

	public String getMatcher() {
		return matcher;
	}

	@Override
	public Collection<Supplier<DSLMethod>> asSuppliers() {
		return Arrays.asList(this::generateArrayContains1, this::generateArrayContains2, this::generateArrayContains3,
				this::generateArrayContainsN);
	}

	public DSLMethod generateArrayContains1() {
		return generateSimpleDSLMethodFor(new String[] { getJavaDocDescription(),
				"@param first the element contained inside the target array", "@return the Matcher." }, getMatcher(),
				"first");
	}

	public DSLMethod generateArrayContains2() {
		return generateSimpleDSLMethodFor(
				new String[] { getJavaDocDescription(), "@param first the element contained inside the target array",
						"@param second the second element contained inside the target array", "@return the Matcher." },
				getMatcher(), "first", "second");
	}

	public DSLMethod generateArrayContains3() {
		return generateSimpleDSLMethodFor(
				new String[] { getJavaDocDescription(), "@param first the element contained inside the target array",
						"@param second the second element contained inside the target array",
						"@param third the third element contained inside the target array", "@return the Matcher." },
				getMatcher(), "first", "second", "third");
	}

	public DSLMethod generateArrayContainsN() {
		return new DSLMethod(
				new String[] { getJavaDocDescription(),
						"@param first the first element contained inside the target array",
						"@param second the second element contained inside the target array",
						"@param third the third element contained inside the target array",
						"@param last the next element", "@return the Matcher." },
				returnType + " " + methodName, getSeveralParameter(true, "first", "second", "third", "last"),
				new String[] {
						"java.util.List<org.hamcrest.Matcher<" + targetName
								+ ">> tmp = new java.util.ArrayList<>(java.util.Arrays.asList("
								+ getSeveralWith("first", "second", "third") + "));",
						"tmp.addAll(java.util.Arrays.stream(last).map(v->" + targetMethodName
								+ "(v)).collect(java.util.stream.Collectors.toList()));",
						"return " + getMatcher() + "(tmp.toArray(new org.hamcrest.Matcher[0]));" });
	}

}