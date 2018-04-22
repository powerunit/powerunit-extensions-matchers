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
package ch.powerunit.extensions.matchers.provideprocessor.fields;

import java.util.Collection;
import java.util.Collections;

import ch.powerunit.extensions.matchers.provideprocessor.ProvidesMatchersAnnotatedElementData;

public class SupplierFieldDescription extends DefaultFieldDescription {

	public SupplierFieldDescription(ProvidesMatchersAnnotatedElementData containingElementMirror,
			FieldDescriptionMirror mirror) {
		super(containingElementMirror, mirror);
	}

	@Override
	protected Collection<FieldDSLMethod> getSpecificFieldDslMethodFor() {
		String tgeneric = this.generic;
		return Collections.singleton(getDslMethodBuilder()
				.withDeclaration("SupplierResult", "org.hamcrest.Matcher<? super " + tgeneric + "> matcherOnResult")
				.withJavaDoc(
						" Validate that the result of the supplier is accepted by another matcher (the result of the execution must be stable)",
						"matcherOnResult a Matcher on result of the supplier execution")
				.havingDefault("asFeatureMatcher(\"with supplier result\",(java.util.function.Supplier<" + tgeneric
						+ "> s) -> s.get(),matcherOnResult)"));
	}

}
