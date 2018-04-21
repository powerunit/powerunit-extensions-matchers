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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;

import ch.powerunit.extensions.matchers.AddToMatcher;
import ch.powerunit.extensions.matchers.IgnoreInMatcher;
import ch.powerunit.extensions.matchers.common.CommonUtils;
import ch.powerunit.extensions.matchers.provideprocessor.ProvideMatchersMirror;
import ch.powerunit.extensions.matchers.provideprocessor.ProvidesMatchersAnnotatedElementData;
import ch.powerunit.extensions.matchers.provideprocessor.RoundMirror;
import ch.powerunit.extensions.matchers.provideprocessor.xml.GeneratedMatcherField;

public abstract class AbstractFieldDescription {

	public static final String SEE_TEXT_FOR_IS_MATCHER = "org.hamcrest.Matchers#is(java.lang.Object)";
	public static final String SEE_TEXT_FOR_HAMCREST_MATCHER = "org.hamcrest.Matchers The main class from hamcrest that provides default matchers.";
	public static final String MATCHERS = "org.hamcrest.Matchers";

	private final List<FieldDSLMethod> dsl;
	private final RoundMirror roundMirror;
	private final boolean ignore;
	protected final String generic;
	protected final String defaultReturnMethod;
	private final String fullyQualifiedNameEnclosingClassOfField;
	private final String enclosingClassOfFieldFullGeneric;
	private final String enclosingClassOfFieldGeneric;
	protected final String fullyQualifiedNameMatcherInSameRound;
	protected final FieldDescriptionMirror mirror;

	public static final String computeGenericInformation(TypeMirror fieldTypeMirror) {
		if (fieldTypeMirror instanceof DeclaredType) {
			DeclaredType dt = ((DeclaredType) fieldTypeMirror);
			return dt.getTypeArguments().stream().map(Object::toString).collect(joining(","));
		}
		return "";
	}

	public static final String computeFullyQualifiedNameMatcherInSameRound(RoundMirror roundMirror,
			Element fieldElement, TypeElement fieldTypeAsTypeElement) {
		ProcessingEnvironment processingEnv = roundMirror.getProcessingEnv();
		if (roundMirror.isInSameRound(processingEnv.getTypeUtils().asElement(fieldElement.asType()))
				&& fieldTypeAsTypeElement != null) {
			return new ProvideMatchersMirror(processingEnv, fieldTypeAsTypeElement)
					.getFullyQualifiedNameOfGeneratedClass();
		}
		return null;
	}

	public AbstractFieldDescription(ProvidesMatchersAnnotatedElementData containingElementMirror,
			FieldDescriptionMirror mirror) {
		this.mirror = mirror;
		this.roundMirror = containingElementMirror.getFullData().getRoundMirror();
		TypeMirror fieldTypeMirror = (mirror.getFieldElement() instanceof ExecutableElement)
				? ((ExecutableElement) mirror.getFieldElement()).getReturnType() : mirror.getFieldElement().asType();
		this.enclosingClassOfFieldFullGeneric = containingElementMirror.getFullGeneric();
		this.enclosingClassOfFieldGeneric = containingElementMirror.getGeneric();
		this.fullyQualifiedNameEnclosingClassOfField = containingElementMirror
				.getFullyQualifiedNameOfClassAnnotatedWithProvideMatcher();
		this.ignore = mirror.getFieldElement().getAnnotation(IgnoreInMatcher.class) != null;
		this.defaultReturnMethod = containingElementMirror.getDefaultReturnMethod();
		this.generic = computeGenericInformation(fieldTypeMirror);
		this.fullyQualifiedNameMatcherInSameRound = computeFullyQualifiedNameMatcherInSameRound(roundMirror,
				mirror.getFieldElement(), mirror.getFieldTypeAsTypeElement());

		List<FieldDSLMethod> tmp = new ArrayList<>();

		tmp.addAll(getFieldDslMethodFor());
		AddToMatcher addToMatchers[] = mirror.getFieldElement().getAnnotationsByType(AddToMatcher.class);
		Arrays.stream(addToMatchers)
				.map(a -> FieldDSLMethod.of(this).withDeclaration(a.suffix(), a.argument()).withDefaultJavaDoc()
						.havingImplementation(Arrays.stream(a.body()).collect(joining("\n")) + "\nreturn this;"))
				.filter(Objects::nonNull).forEach(tmp::add);
		this.dsl = Collections.unmodifiableList(tmp);
	}

	protected abstract Collection<? extends FieldDSLMethod> getFieldDslMethodFor();

	public String getImplementationInterface() {
		return dsl.stream().map(FieldDSLMethod::asImplementationMethod).collect(joining("\n"));
	}

	public String getDslInterface() {
		return dsl.stream().map(FieldDSLMethod::asDSLMethod).collect(joining("\n"));

	}

	public String getMatcherForField() {
		String methodFieldName = mirror.getMethodFieldName();
		StringBuilder sb = new StringBuilder();
		sb.append("private static class " + methodFieldName + "Matcher" + enclosingClassOfFieldFullGeneric
				+ " extends org.hamcrest.FeatureMatcher<" + fullyQualifiedNameEnclosingClassOfField
				+ enclosingClassOfFieldGeneric + "," + mirror.getFieldType() + "> {\n");
		sb.append("  public " + methodFieldName + "Matcher(org.hamcrest.Matcher<? super " + mirror.getFieldType()
				+ "> matcher) {\n");
		sb.append("    super(matcher,\"" + mirror.getFieldName() + "\",\"" + mirror.getFieldName() + "\");\n");
		sb.append("  }\n");

		sb.append("  protected " + mirror.getFieldType() + " featureValueOf(" + fullyQualifiedNameEnclosingClassOfField
				+ enclosingClassOfFieldGeneric + " actual) {\n");
		sb.append("    return actual." + mirror.getFieldAccessor() + ";\n");
		sb.append("  }\n");
		sb.append("}\n");
		return sb.toString();
	}

	public String getFieldCopyDefault(String lhs, String rhs) {
		return lhs + "." + mirror.getFieldName() + "(" + MATCHERS + ".is(" + rhs + "." + mirror.getFieldAccessor()
				+ "))";
	}

	public String getSameValueMatcherFor(String target) {
		String name = mirror.getFieldTypeAsTypeElement().getSimpleName().toString();
		String lname = name.substring(0, 1).toLowerCase() + name.substring(1);
		return fullyQualifiedNameMatcherInSameRound + "." + lname + "WithSameValue(" + target + ")";
	}

	public String getFieldCopySameRound(String lhs, String rhs) {
		String fieldAccessor = mirror.getFieldAccessor();
		return lhs + "." + mirror.getFieldName() + "(" + rhs + "." + fieldAccessor + "==null?" + MATCHERS
				+ ".nullValue():" + getSameValueMatcherFor(rhs + "." + fieldAccessor) + ")";
	}

	public String generateMatcherBuilderReferenceFor(String generic) {
		return Optional.ofNullable(roundMirror.getByName(generic)).map(
				t -> t.getFullyQualifiedNameOfGeneratedClass() + "::" + t.getMethodShortClassName() + "WithSameValue")
				.orElse(MATCHERS + "::is");
	}

	public String getFieldCopyForList(String lhs, String rhs) {
		String fieldAccessor = mirror.getFieldAccessor();
		return "if(" + rhs + "." + fieldAccessor + "==null) {" + lhs + "." + mirror.getFieldName() + "(" + MATCHERS
				+ ".nullValue()); } else if (" + rhs + "." + fieldAccessor + ".isEmpty()) {" + lhs + "."
				+ mirror.getFieldName() + "IsEmptyIterable(); } else {" + lhs + "." + mirror.getFieldName()
				+ "Contains(" + rhs + "." + fieldAccessor + ".stream().map("
				+ generateMatcherBuilderReferenceFor(generic) + ").collect(java.util.stream.Collectors.toList())); }";
	}

	public String getFieldCopy(String lhs, String rhs) {
		if (fullyQualifiedNameMatcherInSameRound != null
				&& mirror.getFieldTypeAsTypeElement().getTypeParameters().isEmpty()) {
			return getFieldCopySameRound(lhs, rhs);
		}
		return getFieldCopyDefault(lhs, rhs);
	}

	public String asMatchesSafely() {
		return String.format(
				"if(!%1$s.matches(actual)) {\n  mismatchDescription.appendText(\"[\"); %1$s.describeMismatch(actual,mismatchDescription); mismatchDescription.appendText(\"]\\n\");\n  result=false;\n}",
				mirror.getFieldName());
	}

	public String asDescribeTo() {
		return "description.appendText(\"[\").appendDescriptionOf(" + mirror.getFieldName() + ").appendText(\"]\\n\");";
	}

	public String asMatcherField() {
		return String
				.format("private %1$sMatcher %2$s = new %1$sMatcher(%3$s.anything(%4$s));",
						mirror.getMethodFieldName(), mirror
								.getFieldName(),
						MATCHERS, ignore ? ("\"This field is ignored \"+"
								+ CommonUtils.toJavaSyntax(getDescriptionForIgnoreIfApplicable())) : "");
	}

	public boolean isIgnore() {
		return ignore;
	}

	public boolean isNotIgnore() {
		return !ignore;
	}

	public String getDescriptionForIgnoreIfApplicable() {
		return Optional.ofNullable(mirror.getFieldElement().getAnnotation(IgnoreInMatcher.class)).map(i -> i.comments())
				.orElse("");
	}

	public String getFullyQualifiedNameEnclosingClassOfField() {
		return fullyQualifiedNameEnclosingClassOfField;
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

	public GeneratedMatcherField asGeneratedMatcherField() {
		GeneratedMatcherField gmf = new GeneratedMatcherField();
		gmf.setFieldIsIgnored(ignore);
		gmf.setFieldName(mirror.getFieldName());
		gmf.setFieldAccessor(mirror.getFieldAccessor());
		gmf.setGenericDetails(generic);
		return gmf;
	}

}
