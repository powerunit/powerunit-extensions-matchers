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

import ch.powerunit.extensions.matchers.ComplementaryExpositionMethod;
import ch.powerunit.extensions.matchers.provideprocessor.ProvidesMatchersAnnotatedElementData;
import ch.powerunit.extensions.matchers.provideprocessor.dsl.DSLMethod;

public class AnyOfExtension implements DSLExtension {

	public static final String ANYOF_MATCHER = "org.hamcrest.Matchers.anyOf";

	private static final String JAVADOC_DESCRIPTION = "Generate a anyOf matcher for this Object, which provide a simple way to valid that something is one of the many supplied instance.";

	@Override
	public ComplementaryExpositionMethod supportedEnum() {
		return ComplementaryExpositionMethod.ANY_OF;
	}

	@Override
	public Collection<Supplier<DSLMethod>> getDSLMethodFor(ProvidesMatchersAnnotatedElementData element) {
		String targetName = element.getFullyQualifiedNameOfClassAnnotatedWithProvideMatcherWithGeneric();
		String returnType = element.getFullGeneric() + " org.hamcrest.Matcher<" + targetName + ">";
		String methodName = element.generateDSLMethodName("anyOf");
		String targetMethodName = element.generateDSLWithSameValueMethodName();
		return new AnyOfSupplier(targetName, returnType, methodName, targetMethodName).asSuppliers();
	}

	public class AnyOfSupplier extends AbstractDSLExtensionSupplier {

		public AnyOfSupplier(String targetName, String returnType, String methodName, String targetMethodName) {
			super(targetName, returnType, methodName, targetMethodName);
		}

		@Override
		public Collection<Supplier<DSLMethod>> asSuppliers() {
			return Arrays.asList(this::generateAnyOf);
		}

		public DSLMethod generateAnyOf() {
			return of(returnType + " " + methodName).withArguments(getSeveralParameter(true, "items"))
					.withImplementation("return " + ANYOF_MATCHER + "(java.util.Arrays.stream(items).map(v->"
							+ targetMethodName
							+ "(v)).collect(java.util.stream.Collectors.toList()).toArray(new org.hamcrest.Matcher[0]));")
					.withJavadoc(JAVADOC_DESCRIPTION, "@param items the items to be matched", "@return the Matcher.");
		}
	}

}