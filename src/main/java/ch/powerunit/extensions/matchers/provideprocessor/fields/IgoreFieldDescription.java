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
import java.util.Optional;

import ch.powerunit.extensions.matchers.IgnoreInMatcher;
import ch.powerunit.extensions.matchers.common.CommonUtils;
import ch.powerunit.extensions.matchers.provideprocessor.ProvidesMatchersAnnotatedElementData;
import ch.powerunit.extensions.matchers.provideprocessor.xml.GeneratedMatcherField;

public final class IgoreFieldDescription extends AbstractFieldDescription {

	public IgoreFieldDescription(ProvidesMatchersAnnotatedElementData containingElementMirror,
			FieldDescriptionMirror mirror) {
		super(containingElementMirror, mirror);
	}

	@Override
	protected final Collection<FieldDSLMethod> getFieldDslMethodFor() {
		return Collections.emptyList();
	}

	@Override
	public String getImplementationInterface() {
		return "";
	}

	@Override
	public String getDslInterface() {
		return "";
	}

	@Override
	public String asMatcherField() {
		return String.format("private %1$sMatcher %2$s = new %1$sMatcher(%3$s.anything(%4$s));",
				getMirror().getMethodFieldName(), getFieldName(), MATCHERS,
				"\"This field is ignored \"+" + CommonUtils.toJavaSyntax(getDescriptionForIgnoreIfApplicable()));
	}

	@Override
	public GeneratedMatcherField asGeneratedMatcherField() {
		GeneratedMatcherField gm = super.asGeneratedMatcherField();
		gm.setFieldIsIgnored(true);
		return gm;
	}

	@Override
	public String getFieldCopy(String lhs, String rhs) {
		return "/* ignored - " + getFieldName() + " */";
	}

	public String getDescriptionForIgnoreIfApplicable() {
		return Optional.ofNullable(getFieldElement().getAnnotation(IgnoreInMatcher.class)).map(i -> i.comments())
				.orElse("");
	}

}
