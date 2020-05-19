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

import static ch.powerunit.extensions.matchers.provideprocessor.dsl.DSLMethod.of;

import java.util.Arrays;
import java.util.Collection;
import java.util.function.Supplier;

import ch.powerunit.extensions.matchers.provideprocessor.ProvidesMatchersAnnotatedElementData;
import ch.powerunit.extensions.matchers.provideprocessor.dsl.DSLMethod;

public class ArrayContaingDSLExtensionSupplier extends AbstractDSLExtensionSupplier {

	private final String javadoc;
	private final String matcher;
	private final ProvidesMatchersAnnotatedElementData element;

	public ArrayContaingDSLExtensionSupplier(ProvidesMatchersAnnotatedElementData element, String methodName,
			String javadoc, String matcher) {
		super(element.getFullyQualifiedNameOfClassAnnotatedWithProvideMatcherWithGeneric(),
				element.getFullGeneric() + " org.hamcrest.Matcher<"
						+ element.getFullyQualifiedNameOfClassAnnotatedWithProvideMatcherWithGeneric() + "[]>",
				methodName, element.generateDSLWithSameValueMethodName());
		this.javadoc = javadoc;
		this.matcher = matcher;
		this.element = element;
	}

	public String getJavaDocDescription() {
		return javadoc;
	}

	public String getMatcher() {
		return matcher;
	}

	private Supplier<DSLMethod> onlyWithSameValue(Supplier<DSLMethod> input) {
		return () -> {
			if (element.hasWithSameValue()) {
				return input.get();
			} else {
				element.printWarningMessage(
						"Unable to generate ArrayContaining ; The target class doesn't support the WithSameValue() matcher");
				return null;
			}
		};
	}

	@Override
	public Collection<Supplier<DSLMethod>> asSuppliers() {
		return Arrays.asList(onlyWithSameValue(this::generateArrayContains1),
				onlyWithSameValue(this::generateArrayContains2), onlyWithSameValue(this::generateArrayContains3),
				onlyWithSameValue(this::generateArrayContainsN));
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
		return of(returnType + " " + methodName)
				.withArguments(getSeveralParameter(true, "first", "second", "third", "last"))
				.withImplementation(generateSeveralWithImplementation(getMatcher(), "first", "second", "third"))
				.withJavadoc(getJavaDocDescription(),
						"@param first the first element contained inside the target array",
						"@param second the second element contained inside the target array",
						"@param third the third element contained inside the target array",
						"@param last the next element", "@return the Matcher.");
	}

}