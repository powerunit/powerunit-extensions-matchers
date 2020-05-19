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

public class HasItemsExtension implements DSLExtension {

	public static final String CONTAINS_MATCHER = "org.hamcrest.Matchers.hasItems";

	private static final String JAVADOC_DESCRIPTION = "Generate a hasItems matcher for this Object.";

	@Override
	public ComplementaryExpositionMethod supportedEnum() {
		return ComplementaryExpositionMethod.HAS_ITEMS;
	}

	@Override
	public Collection<Supplier<DSLMethod>> getDSLMethodFor(ProvidesMatchersAnnotatedElementData element) {
		return new HasItemSupplier(element).asSuppliers();
	}

	public class HasItemSupplier extends AbstractDSLExtensionSupplier {

		private final ProvidesMatchersAnnotatedElementData element;

		public HasItemSupplier(ProvidesMatchersAnnotatedElementData element) {
			super(element.getFullyQualifiedNameOfClassAnnotatedWithProvideMatcherWithGeneric(),
					element.getFullGeneric() + " org.hamcrest.Matcher<java.lang.Iterable<"
							+ element.getFullyQualifiedNameOfClassAnnotatedWithProvideMatcherWithGeneric() + ">>",
					element.generateDSLMethodName("hasItems"), element.generateDSLWithSameValueMethodName());
			this.element = element;
		}

		@Override
		public Collection<Supplier<DSLMethod>> asSuppliers() {
			return Arrays.asList(this::generateContainsN);
		}

		public DSLMethod generateContainsN() {
			if (element.hasWithSameValue()) {
				return of(returnType + " " + methodName).withArguments(getSeveralParameter(true, "item"))
						.withImplementation("return " + CONTAINS_MATCHER + "(java.util.Arrays.stream(item).map(v->"
								+ targetMethodName
								+ "(v)).collect(java.util.stream.Collectors.toList()).toArray(new org.hamcrest.Matcher[0]));")
						.withJavadoc(JAVADOC_DESCRIPTION, "@param item the item to be matched", "@return the Matcher.");
			} else {
				element.printWarningMessage("Unable to apply the " + supportedEnum().name()
						+ " extension ; The target class doesn't support the WithSameValue() matcher");
				return null;
			}
		}
	}

}