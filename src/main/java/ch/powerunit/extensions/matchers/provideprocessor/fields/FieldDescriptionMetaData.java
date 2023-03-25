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

import static ch.powerunit.extensions.matchers.common.CommonUtils.toJavaSyntax;
import static java.lang.String.format;
import static java.util.stream.Collectors.joining;

import java.util.Optional;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;

import ch.powerunit.extensions.matchers.provideprocessor.Matchable;
import ch.powerunit.extensions.matchers.provideprocessor.ProvidesMatchersAnnotatedElementData;
import ch.powerunit.extensions.matchers.provideprocessor.helper.FeatureMatcher;

public abstract class FieldDescriptionMetaData extends AbstractFieldDescriptionContainerMetaData {

	public static final String SEE_TEXT_FOR_IS_MATCHER = "org.hamcrest.Matchers#is(java.lang.Object)";
	public static final String SEE_TEXT_FOR_HAMCREST_MATCHER = "org.hamcrest.Matchers The main class from hamcrest that provides default matchers.";
	public static final String MATCHERS = "org.hamcrest.Matchers";

	protected final String generic;
	protected final String defaultReturnMethod;
	protected final FieldDescriptionMirror mirror;

	public static final String computeGenericInformation(TypeMirror fieldTypeMirror) {
		if (fieldTypeMirror instanceof DeclaredType dt) {
			return dt.getTypeArguments().stream().map(Object::toString).map(s->s.replaceAll("@[^ ]+ ", "")).collect(joining(","));
		}
		return "";
	}

	public FieldDescriptionMetaData(ProvidesMatchersAnnotatedElementData containingElementMirror,
			FieldDescriptionMirror mirror) {
		super(containingElementMirror);
		this.mirror = mirror;
		TypeMirror fieldTypeMirror = mirror.getFieldTypeMirror();
		this.defaultReturnMethod = containingElementMirror.getDefaultReturnMethod();
		this.generic = computeGenericInformation(fieldTypeMirror);
	}

	public String getMatcherForField() {
		return new FeatureMatcher(mirror.getMethodFieldName(), containingElementMirror.getFullGeneric(),
				getFullyQualifiedNameEnclosingClassOfField(), containingElementMirror.getGeneric(), getFieldType(),
				getFieldName(), "actual." + getFieldAccessor()).toString();
	}

	public String getFieldCopy(String lhs, String rhs, String ignore) {
		return getTargetAsMatchable().filter(a -> mirror.getFieldTypeAsTypeElement().getTypeParameters().isEmpty())
				.filter(Matchable::hasWithSameValue)
				.map(p -> format(
						"%1$s.%6$s(%2$s.%3$s == null ? org.hamcrest.Matchers.nullValue() : %4$s(%2$s.%3$s%5$s));", lhs,
						rhs, getFieldAccessor(), p.getWithSameValue(false), p.supportIgnore() ? ignore : "",
						getFieldName()))
				.orElseGet(() -> format(
						"%1$s.%4$s((org.hamcrest.Matcher)org.hamcrest.Matchers.is((java.lang.Object)%2$s.%3$s));", lhs,
						rhs, getFieldAccessor(), getFieldName()));
	}

	public String asMatchesSafely() {
		return format(
				"if(!%1$s.matches(actual)) {\n  mismatchDescription.appendText(\"[\"); %1$s.describeMismatch(actual,mismatchDescription); mismatchDescription.appendText(\"]\\n\");\n  result=false;\n}",
				getFieldName());
	}

	public String asDescribeTo() {
		return "_description.appendText(\"[\").appendDescriptionOf(this." + getFieldName() + ").appendText(\"]\\n\");";
	}

	public String asMatcherField() {
		return format("private %1$sMatcher %2$s = new %1$sMatcher(%3$s.anything(%4$s));", mirror.getMethodFieldName(),
				getFieldName(), MATCHERS, "");
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

	public Optional<Matchable> getTargetAsMatchable() {
		return mirror.getMatchable(containingElementMirror.getRoundMirror());
	}

	public String getGeneric() {
		return generic;
	}

	public String generateMetadata(String className) {
		return format("""
				new %1$s(
				        %2$s,
				        %3$s,
				        %4$s,
				        %5$s,
				        %6$s,
				        %7$s,
				        "%8$s",
				        %9$s)""",
				className,
				toJavaSyntax(getFieldName()),
				toJavaSyntax(getFieldType()),
				toJavaSyntax(getFieldAccessor()),
				(getFieldElement().getKind().equals(ElementKind.FIELD)?"o->o.":getFullyQualifiedNameEnclosingClassOfField()+"::")+getFieldAccessor().replaceAll("[()]+", ""),
				toJavaSyntax(getClass().getSimpleName()),
				Boolean.toString(this instanceof IgnoreFieldDescription),
				getFieldElement().getKind().name(),
				getTargetAsMatchable().map(m->m.getFullyQualifiedNameOfGeneratedClass()+".METADATA").orElse("null"));

	}

}
