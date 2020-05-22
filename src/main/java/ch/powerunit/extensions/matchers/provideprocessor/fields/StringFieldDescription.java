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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import ch.powerunit.extensions.matchers.provideprocessor.ProvidesMatchersAnnotatedElementData;

public class StringFieldDescription extends ComparableFieldDescription {

	public StringFieldDescription(ProvidesMatchersAnnotatedElementData containingElementMirror,
			FieldDescriptionMirror mirror) {
		super(containingElementMirror, mirror);
	}

	@Override
	protected Collection<FieldDSLMethod> getSpecificFieldDslMethodFor() {
		List<FieldDSLMethod> tmp = new ArrayList<>(super.getSpecificFieldDslMethodFor());
		tmp.addAll(Arrays.asList(getDslMethodBuilder().withDeclaration("ContainsString", "String other")
				.withJavaDoc("that the string contains another one", "other the string is contains in the other one",
						MATCHERS + "#containsString(java.lang.String)")
				.havingDefault(MATCHERS + ".containsString(other)"),
				getDslMethodBuilder().withDeclaration("StartsWith", "String other")
						.withJavaDoc("that the string starts with another one", "other the string to use to compare",
								MATCHERS + "#startsWith(java.lang.String)")
						.havingDefault(MATCHERS + ".startsWith(other)"),
				getDslMethodBuilder().withDeclaration("EndsWith", "String other")
						.withJavaDoc("that the string ends with another one", "other the string to use to compare",
								MATCHERS + "#endsWith(java.lang.String)")
						.havingDefault(MATCHERS + ".endsWith(other)")));
		return tmp;
	}

}
