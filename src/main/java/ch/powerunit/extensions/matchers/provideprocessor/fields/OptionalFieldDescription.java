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

import java.util.Arrays;
import java.util.Collection;

import ch.powerunit.extensions.matchers.provideprocessor.ProvidesMatchersAnnotatedElementData;

public class OptionalFieldDescription extends DefaultFieldDescription {

	public OptionalFieldDescription(ProvidesMatchersAnnotatedElementData containingElementMirror,
			FieldDescriptionMirror mirror) {
		super(containingElementMirror, mirror);
	}

	@Override
	protected Collection<FieldDSLMethod> getSpecificFieldDslMethodFor() {
		String fieldType = getFieldType();
		return Arrays.asList(
				getDslMethodBuilder().withSuffixDeclarationJavadocAndDefault("IsPresent", "with a present optional",
						"new org.hamcrest.CustomTypeSafeMatcher<" + fieldType
								+ ">(\"optional is present\"){ public boolean matchesSafely(" + fieldType
								+ " o) {return o.isPresent();}}"),
				getDslMethodBuilder().withSuffixDeclarationJavadocAndDefault("IsNotPresent",
						"with a not present optional",
						"new org.hamcrest.CustomTypeSafeMatcher<" + fieldType
								+ ">(\"optional is not present\"){ public boolean matchesSafely(" + fieldType
								+ " o) {return !o.isPresent();}}"));
	}

}
