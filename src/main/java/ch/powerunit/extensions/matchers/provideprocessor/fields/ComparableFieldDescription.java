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

public class ComparableFieldDescription extends DefaultFieldDescription {

	public ComparableFieldDescription(ProvidesMatchersAnnotatedElementData containingElementMirror,
			FieldDescriptionMirror mirror) {
		super(containingElementMirror, mirror);
	}

	@Override
	protected Collection<? extends FieldDSLMethod> getFieldDslMethodFor() {
		List<FieldDSLMethod> tmp = new ArrayList<>(super.getFieldDslMethodFor());
		tmp.addAll(Arrays.asList(
				FieldDSLMethod.of(this).withDeclaration("ComparesEqualTo", this.getFieldType() + " value")
						.withJavaDoc("that this field is equals to another one, using the compareTo method",
								"value the value to compare with", MATCHERS + "#comparesEqualTo(java.lang.Comparable)")
				.havingDefault(MATCHERS + ".comparesEqualTo(value)"),
				FieldDSLMethod.of(this).withDeclaration("LessThan", this.getFieldType() + " value")
						.withJavaDoc("that this field is less than another value", "value the value to compare with",
								MATCHERS + "#lessThan(java.lang.Comparable)")
						.havingDefault(MATCHERS + ".lessThan(value)"),
				FieldDSLMethod.of(this).withDeclaration("lessThanOrEqualTo", this.getFieldType() + " value")
						.withJavaDoc("that this field is less or equal than another value",
								"value the value to compare with",
								MATCHERS + "#lessThanOrEqualTo(java.lang.Comparable)")
						.havingDefault(MATCHERS + ".lessThanOrEqualTo(value)"),
				FieldDSLMethod.of(this).withDeclaration("GreaterThan", this.getFieldType() + " value")
						.withJavaDoc("that this field is greater than another value", "value the value to compare with",
								MATCHERS + "#greaterThan(java.lang.Comparable)")
						.havingDefault(MATCHERS + ".greaterThan(value)"),
				FieldDSLMethod.of(this).withDeclaration("GreaterThanOrEqualTo", this.getFieldType() + " value")
						.withJavaDoc("that this field is greater or equal than another value",
								"value the value to compare with",
								MATCHERS + "#greaterThanOrEqualTo(java.lang.Comparable)")
						.havingDefault(MATCHERS + ".greaterThanOrEqualTo(value)")));
		return tmp;
	}

}
