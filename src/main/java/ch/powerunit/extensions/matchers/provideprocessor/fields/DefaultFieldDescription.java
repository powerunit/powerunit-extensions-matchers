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
import ch.powerunit.extensions.matchers.provideprocessor.Matchable;
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
		List<FieldDSLMethod> tmp = new ArrayList<>(super.getFieldDslMethodFor());
		mirror.getMatchable(containingElementMirror.getRoundMirror())
				.filter(a -> mirror.getFieldTypeAsTypeElement().getTypeParameters().isEmpty())
				.map(this::createWithParent).ifPresent(tmp::add);
		tmp.add(FieldDSLMethodBuilder.of(this).withDeclaration("IsSameInstance", mirror.getFieldType() + " value")
				.withJavaDoc("",
						"value an expected value for the field, which will be compared that it is the same instance.",
						SEE_TEXT_FOR_IS_MATCHER)
				.havingDefault("(org.hamcrest.Matcher)" + MATCHERS + ".sameInstance((java.lang.Object)value)"));
		tmp.addAll(getSpecificFieldDslMethodFor());
		return tmp;
	}

	private FieldDSLMethod createWithParent(Matchable target) {
		FieldDescriptionMirror fm = mirror;
		String fn = fm.getFieldName();
		TypeElement te = fm.getFieldTypeAsTypeElement();
		String name = getSimpleName(te);
		String targetMatcher = target.getFullyQualifiedNameOfGeneratedClass();
		return FieldDSLMethodBuilder.of(this).withExplicitDeclarationJavadocAndImplementation(
				targetMatcher + "." + name + "Matcher" + "<" + defaultReturnMethod + "> " + fn + "With()",
				"by starting a matcher for this field", targetMatcher + "." + name + "Matcher tmp = " + targetMatcher
						+ "." + target.getMethodNameDSLWithParent() + "(this);\n" + fn + "(tmp);\nreturn tmp;");
	}

}
