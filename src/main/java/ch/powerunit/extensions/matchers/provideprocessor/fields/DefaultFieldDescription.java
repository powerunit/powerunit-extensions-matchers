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

import ch.powerunit.extensions.matchers.provideprocessor.ProvidesMatchersAnnotatedElementData;
import ch.powerunit.extensions.matchers.provideprocessor.fields.lang.BuilderDeclaration;

public class DefaultFieldDescription extends AbstractFieldDescription {

	public DefaultFieldDescription(ProvidesMatchersAnnotatedElementData containingElementMirror,
			FieldDescriptionMirror mirror) {
		super(containingElementMirror, mirror);
	}

	protected Collection<FieldDSLMethod> getSpecificFieldDslMethodFor() {
		return Collections.emptyList();
	}

	protected final BuilderDeclaration getDslMethodBuilder() {
		return FieldDSLMethodBuilder.of(this);
	}

	@Override
	protected final Collection<FieldDSLMethod> getFieldDslMethodFor() {
		String ft = mirror.getFieldType();
		String fn = mirror.getFieldName();
		List<FieldDSLMethod> tmp = new ArrayList<>();
		tmp.add(FieldDSLMethodBuilder.of(this).withDeclaration("org.hamcrest.Matcher<? super " + ft + "> matcher")
				.withJavaDoc("", "matcher a Matcher on the field", SEE_TEXT_FOR_HAMCREST_MATCHER)
				.havingImplementation(fn + "= new " + mirror.getMethodFieldName() + "Matcher(matcher);\nreturn this;"));
		tmp.add(FieldDSLMethodBuilder.of(this).withDeclaration(ft + " value")
				.withJavaDoc("", "value an expected value for the field, which will be compared using the is matcher",
						SEE_TEXT_FOR_IS_MATCHER)
				.havingDefault("(org.hamcrest.Matcher)"+MATCHERS + ".is((java.lang.Object)value)"));
		tmp.add(FieldDSLMethodBuilder.of(this)
				.withGenericDeclaration("<_TARGETFIELD>", "As",
						"java.util.function.Function<" + ft
								+ ",_TARGETFIELD> converter,org.hamcrest.Matcher<? super _TARGETFIELD> matcher")
				.withJavaDoc("by converting the received field before validat it",
						"converter a function to convert the field\nmatcher a matcher on the resulting\n<_TARGETFIELD> The type which this field must be converter")
				.havingDefault("asFeatureMatcher(\" <field is converted> \",converter,matcher)"));
		TypeElement te = mirror.getFieldTypeAsTypeElement();
		if (fullyQualifiedNameMatcherInSameRound != null && te.getTypeParameters().isEmpty()) {
			String name = te.getSimpleName().toString();
			String lname = name.substring(0, 1).toLowerCase() + name.substring(1);
			tmp.add(FieldDSLMethodBuilder.of(this)
					.withExplicitDeclaration(fullyQualifiedNameMatcherInSameRound + "." + name + "Matcher" + "<"
							+ defaultReturnMethod + "> " + fn + "With()")
					.withJavaDoc("by starting a matcher for this field")
					.havingImplementation(fullyQualifiedNameMatcherInSameRound + "." + name + "Matcher tmp = "
							+ fullyQualifiedNameMatcherInSameRound + "." + lname + "WithParent(this);\n" + fn
							+ "(tmp);\nreturn tmp;"));
		}
		tmp.addAll(getSpecificFieldDslMethodFor());
		return tmp;
	}

}
