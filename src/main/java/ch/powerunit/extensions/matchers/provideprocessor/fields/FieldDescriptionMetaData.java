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

import static java.util.stream.Collectors.joining;

import java.util.Optional;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;

import ch.powerunit.extensions.matchers.common.CommonUtils;
import ch.powerunit.extensions.matchers.provideprocessor.ProvidesMatchersAnnotatedElementData;
import ch.powerunit.extensions.matchers.provideprocessor.ProvidesMatchersAnnotatedElementMirror;
import ch.powerunit.extensions.matchers.provideprocessor.RoundMirror;
import ch.powerunit.extensions.matchers.provideprocessor.xml.GeneratedMatcherField;

public abstract class FieldDescriptionMetaData extends AbstractFieldDescriptionContainerMetaData {

	public static final String SEE_TEXT_FOR_IS_MATCHER = "org.hamcrest.Matchers#is(java.lang.Object)";
	public static final String SEE_TEXT_FOR_HAMCREST_MATCHER = "org.hamcrest.Matchers The main class from hamcrest that provides default matchers.";
	public static final String MATCHERS = "org.hamcrest.Matchers";

	protected final String generic;
	protected final String defaultReturnMethod;
	protected final String fullyQualifiedNameMatcherInSameRound;
	protected final FieldDescriptionMirror mirror;

	public static final String computeGenericInformation(TypeMirror fieldTypeMirror) {
		if (fieldTypeMirror instanceof DeclaredType) {
			DeclaredType dt = ((DeclaredType) fieldTypeMirror);
			return dt.getTypeArguments().stream().map(Object::toString).collect(joining(","));
		}
		return "";
	}

	public FieldDescriptionMetaData(ProvidesMatchersAnnotatedElementData containingElementMirror,
			FieldDescriptionMirror mirror) {
		super(containingElementMirror);
		this.mirror = mirror;
		RoundMirror roundMirror = containingElementMirror.getRoundMirror();
		TypeMirror fieldTypeMirror = (mirror.getFieldElement() instanceof ExecutableElement)
				? ((ExecutableElement) mirror.getFieldElement()).getReturnType()
				: mirror.getFieldElement().asType();
		this.defaultReturnMethod = containingElementMirror.getDefaultReturnMethod();
		this.generic = computeGenericInformation(fieldTypeMirror);
		this.fullyQualifiedNameMatcherInSameRound = mirror.computeFullyQualifiedNameMatcherInSameRound(roundMirror);
	}

	public String getMatcherForField() {
		return String.format(
				"private static class %1$sMatcher%2$s extends org.hamcrest.FeatureMatcher<%3$s%4$s,%5$s> {\n  public %1$sMatcher(org.hamcrest.Matcher<? super %5$s> matcher) {\n    super(matcher,\"%6$s\",\"%6$s\");\n  }\n  protected %5$s featureValueOf(%3$s%4$s actual) {\n    return actual.%7$s;\n  }\n}\n",
				mirror.getMethodFieldName(), containingElementMirror.getFullGeneric(),
				getFullyQualifiedNameEnclosingClassOfField(), containingElementMirror.getGeneric(), getFieldType(),
				getFieldName(), getFieldAccessor());
	}

	public String getFieldCopyDefault(String lhs, String rhs) {
		return lhs + "." + getFieldName() + "((org.hamcrest.Matcher)" + MATCHERS + ".is((java.lang.Object)" + rhs + "."
				+ getFieldAccessor() + "))";
	}

	public String getSameValueMatcherFor(String target) {
		String name = getSimpleName(mirror.getFieldTypeAsTypeElement());
		String lname = name.substring(0, 1).toLowerCase() + name.substring(1);
		return fullyQualifiedNameMatcherInSameRound + "." + lname + "WithSameValue(" + target + ")";
	}

	public String getFieldCopySameRound(String lhs, String rhs) {
		String fieldAccessor = getFieldAccessor();
		return lhs + "." + getFieldName() + "(" + rhs + "." + fieldAccessor + "==null?" + MATCHERS + ".nullValue():"
				+ getSameValueMatcherFor(rhs + "." + fieldAccessor) + ")";
	}

	public String getFieldCopy(String lhs, String rhs) {
		if (fullyQualifiedNameMatcherInSameRound != null
				&& mirror.getFieldTypeAsTypeElement().getTypeParameters().isEmpty()
				&& Optional.ofNullable(containingElementMirror.getRoundMirror().getByName(getFieldType()))
						.map(ProvidesMatchersAnnotatedElementMirror::hasWithSameValue).orElse(false)) {
			return getFieldCopySameRound(lhs, rhs);
		}
		return getFieldCopyDefault(lhs, rhs);
	}

	public String asMatchesSafely() {
		return String.format(
				"if(!%1$s.matches(actual)) {\n  mismatchDescription.appendText(\"[\"); %1$s.describeMismatch(actual,mismatchDescription); mismatchDescription.appendText(\"]\\n\");\n  result=false;\n}",
				getFieldName());
	}

	public String asDescribeTo() {
		return "description.appendText(\"[\").appendDescriptionOf(" + getFieldName() + ").appendText(\"]\\n\");";
	}

	public String asMatcherField() {
		return String.format("private %1$sMatcher %2$s = new %1$sMatcher(%3$s.anything(%4$s));",
				mirror.getMethodFieldName(), getFieldName(), MATCHERS, "");
	}

	public String getFullyQualifiedNameEnclosingClassOfField() {
		return containingElementMirror.getFullyQualifiedNameOfClassAnnotatedWithProvideMatcher();
	}

	public String getDefaultReturnMethod() {
		return defaultReturnMethod;
	}

	public String getFieldAccessor() {
		return mirror.getFieldAccessor();
	}

	public String getFieldName() {
		return mirror.getFieldName();
	}

	public String getFieldType() {
		return mirror.getFieldType();
	}

	public Element getFieldElement() {
		return mirror.getFieldElement();
	}

	public FieldDescriptionMirror getMirror() {
		return mirror;
	}

	public String getGeneric() {
		return generic;
	}

	public GeneratedMatcherField asGeneratedMatcherField() {
		GeneratedMatcherField gmf = new GeneratedMatcherField();
		gmf.setFieldIsIgnored(true);
		gmf.setFieldName(getFieldName());
		gmf.setFieldAccessor(getFieldAccessor());
		gmf.setGenericDetails(generic);
		return gmf;
	}

	public String generateMetadata(String className) {
		return "new " + className + "(" + CommonUtils.toJavaSyntax(getFieldName()) + ","
				+ CommonUtils.toJavaSyntax(getFieldType()) + "," + CommonUtils.toJavaSyntax(getFieldAccessor()) + ","
				+ CommonUtils.toJavaSyntax(getClass().getSimpleName()) + ","
				+ Boolean.toString(this instanceof IgoreFieldDescription) + ")";
	}

}
