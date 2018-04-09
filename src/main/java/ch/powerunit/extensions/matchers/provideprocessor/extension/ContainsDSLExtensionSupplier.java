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

public class ContainsDSLExtensionSupplier extends AbstractDSLExtensionSupplier {

	private final String javadoc;
	private final String matcher;

	public ContainsDSLExtensionSupplier(ProvidesMatchersAnnotatedElementData element, String methodName, String javadoc,
			String matcher) {
		super(element.getFullyQualifiedNameOfClassAnnotatedWithProvideMatcherWithGeneric(),
				element.getFullGeneric() + " org.hamcrest.Matcher<java.lang.Iterable<? extends "
						+ element.getFullyQualifiedNameOfClassAnnotatedWithProvideMatcherWithGeneric() + ">>",
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
		return Arrays.asList(this::generateContains1, this::generateContains2, this::generateContains3,
				this::generateContainsN);
	}

	public DSLMethod generateContains1() {
		return generateSimpleDSLMethodFor(new String[] { getJavaDocDescription(),
				"@param first the element contained inside the target iterable", "@return the Matcher." }, getMatcher(),
				"first");
	}

	public DSLMethod generateContains2() {
		return generateSimpleDSLMethodFor(new String[] { getJavaDocDescription(),
				"@param first the element contained inside the target iterable",
				"@param second the second element contained inside the target iterable", "@return the Matcher." },
				getMatcher(), "first", "second");
	}

	public DSLMethod generateContains3() {
		return generateSimpleDSLMethodFor(
				new String[] { getJavaDocDescription(), "@param first the element contained inside the target iterable",
						"@param second the second element contained inside the target iterable",
						"@param third the third element contained inside the target iterable", "@return the Matcher." },
				getMatcher(), "first", "second", "third");
	}

	public DSLMethod generateContainsN() {
		return new DSLMethod(
				new String[] { getJavaDocDescription(),
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
						"return " + getMatcher() + "(tmp.toArray(new org.hamcrest.Matcher[0]));" });
	}

}