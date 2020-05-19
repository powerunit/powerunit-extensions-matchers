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

public class NoneOfExtension extends AnyOfExtension {

	public static final String NOT_MATCHER = "org.hamcrest.Matchers.not";

	private static final String JAVADOC_DESCRIPTION = "Generate a notOf matcher for this Object, which provide a simple way to valid that something is none of the many supplied instance.";

	@Override
	public ComplementaryExpositionMethod supportedEnum() {
		return ComplementaryExpositionMethod.NONE_OF;
	}

	@Override
	public Collection<Supplier<DSLMethod>> getDSLMethodFor(ProvidesMatchersAnnotatedElementData element) {
		String methodName = element.generateDSLMethodName("noneOf");
		return new NoneOfSupplier(element, methodName).asSuppliers();
	}

	public class NoneOfSupplier extends AnyOfSupplier {

		public NoneOfSupplier(ProvidesMatchersAnnotatedElementData element, String methodName) {
			super(element, methodName);
		}

		@Override
		public Collection<Supplier<DSLMethod>> asSuppliers() {
			return Arrays.asList(this::generateNoneOf);
		}

		public DSLMethod generateNoneOf() {
			if (element.hasWithSameValue()) {
				return of(returnType + " " + methodName).withArguments(getSeveralParameter(true, "items"))
						.withImplementation("return " + NOT_MATCHER + "(" + innerMatcher() + ");")
						.withJavadoc(JAVADOC_DESCRIPTION, "@param items the items to be not matched",
								"@return the Matcher.");
			} else {
				element.printWarningMessage("Unable to apply the " + supportedEnum().name()
						+ " extension ; The target class doesn't support the WithSameValue() matcher");
				return null;
			}
		}
	}

}