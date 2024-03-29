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

import static java.util.Optional.ofNullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import ch.powerunit.extensions.matchers.provideprocessor.Matchable;
import ch.powerunit.extensions.matchers.provideprocessor.ProvidesMatchersAnnotatedElementData;

public class OptionalFieldDescription extends DefaultFieldDescription {

	public OptionalFieldDescription(ProvidesMatchersAnnotatedElementData containingElementMirror,
			FieldDescriptionMirror mirror) {
		super(containingElementMirror, mirror);
	}

	@Override
	protected Collection<FieldDSLMethod> getSpecificFieldDslMethodFor() {
		Collection<FieldDSLMethod> dsl = new ArrayList<>();
		dsl.addAll(getPresentDSL());
		dsl.addAll(getAbsentDSL());
		return dsl;
	}

	private Collection<FieldDSLMethod> getPresentDSL() {
		String fieldType = getFieldType();
		return Arrays.asList(
				getDslMethodBuilder().withSuffixDeclarationJavadocAndDefault("IsPresent", "with a present optional",
						"new org.hamcrest.CustomTypeSafeMatcher<" + fieldType
								+ ">(\"optional is present\"){ public boolean matchesSafely(" + fieldType
								+ " o) {return o.isPresent();}}"),
				getDslMethodBuilder().withDeclaration("IsPresentAndIs", generic + " value")
						.withJavaDoc("with a present optional having a specific value",
								"value the value the optional must have")
						.havingDefault("new org.hamcrest.TypeSafeMatcher<" + fieldType
								+ ">(){ public boolean matchesSafely(" + fieldType
								+ " o) {return o.isPresent() && o.get().equals(value);} public void describeTo(org.hamcrest.Description description) {description.appendText(\"optional is present and is \").appendValue(value);}}"),
				getDslMethodBuilder().withDeclaration("IsPresentAndIs", "org.hamcrest.Matcher<" + generic + "> matcher")
						.withJavaDoc("with a present optional matching a specified matcher",
								"matcher the matcher that must accept the optional value")
						.havingDefault("new org.hamcrest.TypeSafeMatcher<" + fieldType
								+ ">(){ public boolean matchesSafely(" + fieldType
								+ " o) {return o.isPresent() && matcher.matches(o.get());} public void describeTo(org.hamcrest.Description description) {description.appendText(\"optional is present and [\").appendDescriptionOf(matcher).appendText(\"]\");}}"));
	}

	private Collection<FieldDSLMethod> getAbsentDSL() {
		String fieldType = getFieldType();
		return Arrays.asList(
				getDslMethodBuilder().withSuffixDeclarationJavadocAndDefault("IsNotPresent",
						"with a not present optional",
						"new org.hamcrest.CustomTypeSafeMatcher<" + fieldType
								+ ">(\"optional is not present\"){ public boolean matchesSafely(" + fieldType
								+ " o) {return !o.isPresent();}}"),
				getDslMethodBuilder().withSuffixDeclarationJavadocAndDefault("IsAbsent", "with an absent optional",
						"new org.hamcrest.CustomTypeSafeMatcher<" + fieldType
								+ ">(\"optional is not present\"){ public boolean matchesSafely(" + fieldType
								+ " o) {return !o.isPresent();}}"));
	}

	@Override
	public String getFieldCopy(String lhs, String rhs, String ignore) {
		if (!"".equals(generic)) {
			return getFieldCopyForOptional(lhs, rhs, ignore);
		}
		return super.getFieldCopy(lhs, rhs, ignore);
	}

	public String getFieldCopyForOptional(String lhs, String rhs, String paramForHasSameValue) {
		String fieldAccessor = getFieldAccessor();
		String fieldName = getFieldName();
		return "if(" + rhs + "." + fieldAccessor + "==null) {\n  " + lhs + "." + fieldName + "(" + MATCHERS
				+ ".nullValue());\n} else if (!" + rhs + "." + fieldAccessor + ".isPresent()) {\n  " + lhs + "."
				+ fieldName + "IsAbsent();\n} else {\n  " + lhs + "." + fieldName + "IsPresentAndIs("
				+ generateMatcherBuilderReferenceFor(generic, rhs + "." + fieldAccessor + ".get()",
						paramForHasSameValue)
				+ ");\n}";
	}

	public String generateMatcherBuilderReferenceFor(String generic, String accessor, String paramForHasSameValue) {
		return ofNullable(getByName(generic.replaceAll("@[^ ]+ ",""))).filter(Matchable::hasWithSameValue).map(
				t -> t.getWithSameValue(false) + "(" + accessor + (t.supportIgnore() ? paramForHasSameValue : "") + ")")
				.orElse(MATCHERS + ".is(" + accessor + ")");
	}

}
