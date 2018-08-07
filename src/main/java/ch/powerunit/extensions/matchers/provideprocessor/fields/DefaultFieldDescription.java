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
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.lang.model.element.TypeElement;

import ch.powerunit.extensions.matchers.common.ElementHelper;
import ch.powerunit.extensions.matchers.provideprocessor.ProvidesMatchersAnnotatedElementData;

public class DefaultFieldDescription extends PrimitiveFieldDescription implements ElementHelper {

	public DefaultFieldDescription(ProvidesMatchersAnnotatedElementData containingElementMirror,
			FieldDescriptionMirror mirror) {
		super(containingElementMirror, mirror);
	}

	protected Collection<FieldDSLMethod> getSpecificFieldDslMethodFor() {
		return Collections.emptyList();
	}

	@Override
	protected Collection<FieldDSLMethod> getFieldDslMethodFor() {
		FieldDescriptionMirror fm = mirror;
		String ft = fm.getFieldType();
		String fn = fm.getFieldName();
		List<FieldDSLMethod> tmp = new ArrayList<>(super.getFieldDslMethodFor());
		TypeElement te = fm.getFieldTypeAsTypeElement();
		String nameInSameRound = fullyQualifiedNameMatcherInSameRound;
		if (nameInSameRound != null && te.getTypeParameters().isEmpty()) {
			String name = getSimpleName(te);
			String lname = name.substring(0, 1).toLowerCase() + name.substring(1);
			tmp.add(FieldDSLMethodBuilder.of(this).withExplicitDeclarationJavadocAndImplementation(
					nameInSameRound + "." + name + "Matcher" + "<" + defaultReturnMethod + "> " + fn + "With()",
					"by starting a matcher for this field", nameInSameRound + "." + name + "Matcher tmp = "
							+ nameInSameRound + "." + lname + "WithParent(this);\n" + fn + "(tmp);\nreturn tmp;"));
		}
		tmp.add(FieldDSLMethodBuilder.of(this).withDeclaration("IsSameInstance", ft + " value")
				.withJavaDoc("",
						"value an expected value for the field, which will be compared that it is the same instance.",
						SEE_TEXT_FOR_IS_MATCHER)
				.havingDefault("(org.hamcrest.Matcher)" + MATCHERS + ".sameInstance((java.lang.Object)value)"));

		tmp.addAll(getSpecificFieldDslMethodFor());
		return tmp;
	}

}
