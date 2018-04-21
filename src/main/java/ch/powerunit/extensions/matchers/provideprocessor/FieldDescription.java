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
package ch.powerunit.extensions.matchers.provideprocessor;

import static java.util.stream.Collectors.joining;

import java.util.ArrayList;
import java.util.Arrays;
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
import ch.powerunit.extensions.matchers.provideprocessor.xml.GeneratedMatcherField;

public class FieldDescription extends FieldDescriptionMirror {

	private static final String DEFAULT_JAVADOC_MATCHER_ON_ELEMENTS = "matchersOnElements the matchers on the elements";
	private static final String SEE_TEXT_FOR_IS_MATCHER = "org.hamcrest.Matchers#is(java.lang.Object)";
	private static final String SEE_TEXT_FOR_HAMCREST_MATCHER = "org.hamcrest.Matchers The main class from hamcrest that provides default matchers.";
	private static final String MATCHERS = "org.hamcrest.Matchers";

	private final List<FieldDSLMethod> dsl;
	private final RoundMirror roundMirror;
	private final boolean ignore;
	private final String generic;
	private final String defaultReturnMethod;
	private final String fullyQualifiedNameEnclosingClassOfField;
	private final String enclosingClassOfFieldFullGeneric;
	private final String enclosingClassOfFieldGeneric;
	private final String fullyQualifiedNameMatcherInSameRound;

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

	public static final List<FieldDSLMethod> getFieldDslMethodForComparable(FieldDescription target) {
		return Arrays.asList(
				FieldDSLMethod.of(target).withDeclaration("ComparesEqualTo", target.getFieldType() + " value")
						.withJavaDoc("that this field is equals to another one, using the compareTo method",
								"value the value to compare with", MATCHERS + "#comparesEqualTo(java.lang.Comparable)")
				.havingDefault(MATCHERS + ".comparesEqualTo(value)"),
				FieldDSLMethod.of(target).withDeclaration("LessThan", target.getFieldType() + " value")
						.withJavaDoc("that this field is less than another value", "value the value to compare with",
								MATCHERS + "#lessThan(java.lang.Comparable)")
						.havingDefault(MATCHERS + ".lessThan(value)"),
				FieldDSLMethod.of(target).withDeclaration("lessThanOrEqualTo", target.getFieldType() + " value")
						.withJavaDoc("that this field is less or equal than another value",
								"value the value to compare with",
								MATCHERS + "#lessThanOrEqualTo(java.lang.Comparable)")
						.havingDefault(MATCHERS + ".lessThanOrEqualTo(value)"),
				FieldDSLMethod.of(target).withDeclaration("GreaterThan", target.getFieldType() + " value")
						.withJavaDoc("that this field is greater than another value", "value the value to compare with",
								MATCHERS + "#greaterThan(java.lang.Comparable)")
						.havingDefault(MATCHERS + ".greaterThan(value)"),
				FieldDSLMethod.of(target).withDeclaration("GreaterThanOrEqualTo", target.getFieldType() + " value")
						.withJavaDoc("that this field is greater or equal than another value",
								"value the value to compare with",
								MATCHERS + "#greaterThanOrEqualTo(java.lang.Comparable)")
						.havingDefault(MATCHERS + ".greaterThanOrEqualTo(value)"));

	}

	public static final List<FieldDSLMethod> getFieldDslMethodForString(FieldDescription target) {
		return Arrays.asList(
				FieldDSLMethod.of(target).withDeclaration("ContainsString", "String other")
						.withJavaDoc("that the string contains another one",
								"other the string is contains in the other one",
								MATCHERS + "#containsString(java.lang.String)")
				.havingDefault(MATCHERS + ".containsString(other)"),
				FieldDSLMethod.of(target).withDeclaration("StartsWith", "String other")
						.withJavaDoc("that the string starts with another one", "other the string to use to compare",
								MATCHERS + "#startsWith(java.lang.String)")
						.havingDefault(MATCHERS + ".startsWith(other)"),
				FieldDSLMethod.of(target).withDeclaration("EndsWith", "String other")
						.withJavaDoc("that the string ends with another one", "other the string to use to compare",
								MATCHERS + "#endsWith(java.lang.String)")
						.havingDefault(MATCHERS + ".endsWith(other)"));
	}

	public static final List<FieldDSLMethod> getFieldDslMethodForIterableAndComparable(FieldDescription target) {
		return Arrays.asList(
				FieldDSLMethod.of(target).withDeclaration("IsEmptyIterable", "")
						.withJavaDoc("that the iterable is empty")
						.havingDefault("(org.hamcrest.Matcher)" + MATCHERS + ".emptyIterable()"),
				FieldDSLMethod.of(target).withDeclaration("IsEmpty", "").withJavaDoc("that the collection is empty")
						.havingDefault("(org.hamcrest.Matcher)" + MATCHERS + ".empty()"));
	}

	public static final List<FieldDSLMethod> getDslForIterableWithGeneric(FieldDescription target) {
		return Arrays.asList(
				FieldDSLMethod.of(target).withDeclaration("Contains", target.generic + "... elements")
						.withJavaDoc("that the iterable contains the received elements", "elements the elements",
								MATCHERS + "#contains(java.lang.Object[])")
				.havingDefault(MATCHERS + ".contains(elements)"),
				FieldDSLMethod.of(target)
						.withDeclaration("Contains",
								"org.hamcrest.Matcher<" + target.generic + ">... matchersOnElements")
						.withJavaDoc("that the iterable contains the received elements, using matchers",
								DEFAULT_JAVADOC_MATCHER_ON_ELEMENTS, MATCHERS + "#contains(org.hamcrest.Matcher[])")
						.havingDefault(MATCHERS + ".contains(matchersOnElements)"),
				FieldDSLMethod.of(target).withDeclaration("ContainsInAnyOrder", target.generic + "... elements")
						.withJavaDoc("that the iterable contains the received elements in any order",
								"elements the elements", MATCHERS + "#containsInAnyOrder(java.lang.Object[])")
						.havingDefault(MATCHERS + ".containsInAnyOrder(elements)"),
				FieldDSLMethod.of(target)
						.withDeclaration("ContainsInAnyOrder",
								"org.hamcrest.Matcher<" + target.generic + ">... matchersOnElements")
						.withJavaDoc("that the iterable contains the received elements, using matchers in any order",
								DEFAULT_JAVADOC_MATCHER_ON_ELEMENTS,
								MATCHERS + "#containsInAnyOrder(org.hamcrest.Matcher[])")
						.havingDefault(MATCHERS + ".containsInAnyOrder(matchersOnElements)"),
				FieldDSLMethod.of(target)
						.withDeclaration("Contains",
								"java.util.List<org.hamcrest.Matcher<? super " + target.generic
										+ ">> matchersOnElements")
						.withJavaDoc("that the iterable contains the received elements, using list of matcher",
								DEFAULT_JAVADOC_MATCHER_ON_ELEMENTS, MATCHERS + "#contains(java.util.List)")
						.havingDefault(MATCHERS + ".contains(matchersOnElements)"));
	}

	public static final List<FieldDSLMethod> getFieldDslMethodFor(FieldDescription target, Type type) {
		switch (type) {
		case ARRAY:
			return Collections.singletonList(
					FieldDSLMethod.of(target).withDeclaration("IsEmpty", "").withJavaDoc("that the array is empty")
							.havingDefault("(org.hamcrest.Matcher)" + MATCHERS + ".emptyArray()"));
		case OPTIONAL:
			return Arrays.asList(
					FieldDSLMethod.of(target).withDeclaration("IsPresent", "").withJavaDoc("with a present optional")
							.havingDefault("new org.hamcrest.CustomTypeSafeMatcher<" + target.getFieldType()
									+ ">(\"optional is present\"){ public boolean matchesSafely("
									+ target.getFieldType() + " o) {return o.isPresent();}}"),
					FieldDSLMethod.of(target).withDeclaration("IsNotPresent", "")
							.withJavaDoc("with a not present optional")
							.havingDefault("new org.hamcrest.CustomTypeSafeMatcher<" + target.getFieldType()
									+ ">(\"optional is not present\"){ public boolean matchesSafely("
									+ target.getFieldType() + " o) {return !o.isPresent();}}"));
		case COMPARABLE:
			return getFieldDslMethodForComparable(target);
		case STRING:
			List<FieldDSLMethod> tmp1 = new ArrayList<>();
			tmp1.addAll(getFieldDslMethodForComparable(target));
			tmp1.addAll(getFieldDslMethodForString(target));
			return tmp1;
		case COLLECTION:
		case LIST:
		case SET:
			List<FieldDSLMethod> tmp2 = new ArrayList<>(getFieldDslMethodForIterableAndComparable(target));
			if (!"".equals(target.generic)) {
				tmp2.addAll(getDslForIterableWithGeneric(target));
			}
			return tmp2;
		case SUPPLIER:
			return Collections.singletonList(FieldDSLMethod.of(target)
					.withDeclaration("SupplierResult",
							"org.hamcrest.Matcher<? super " + target.generic + "> matcherOnResult")
					.withJavaDoc(
							" Validate that the result of the supplier is accepted by another matcher (the result of the execution must be stable)",
							"matcherOnResult a Matcher on result of the supplier execution")
					.havingDefault("asFeatureMatcher(\"with supplier result\",(java.util.function.Supplier<"
							+ target.generic + "> s) -> s.get(),matcherOnResult)"));
		default:
			return Collections.emptyList();
		}
	}

	public FieldDescription(ProvidesMatchersAnnotatedElementData containingElementMirror, String fieldName,
			String fieldType, Element fieldElement) {
		super(containingElementMirror, fieldName, fieldType, fieldElement);
		this.roundMirror = containingElementMirror.getFullData().getRoundMirror();
		TypeMirror fieldTypeMirror = (fieldElement instanceof ExecutableElement)
				? ((ExecutableElement) fieldElement).getReturnType() : fieldElement.asType();
		this.enclosingClassOfFieldFullGeneric = containingElementMirror.getFullGeneric();
		this.enclosingClassOfFieldGeneric = containingElementMirror.getGeneric();
		this.fullyQualifiedNameEnclosingClassOfField = containingElementMirror
				.getFullyQualifiedNameOfClassAnnotatedWithProvideMatcher();
		this.ignore = fieldElement.getAnnotation(IgnoreInMatcher.class) != null;
		this.defaultReturnMethod = containingElementMirror.getDefaultReturnMethod();
		this.generic = computeGenericInformation(fieldTypeMirror);
		this.fullyQualifiedNameMatcherInSameRound = computeFullyQualifiedNameMatcherInSameRound(roundMirror,
				fieldElement, fieldTypeAsTypeElement);

		List<FieldDSLMethod> tmp = new ArrayList<>();

		tmp.add(FieldDSLMethod.of(this).withDeclaration("org.hamcrest.Matcher<? super " + fieldType + "> matcher")
				.withJavaDoc("", "matcher a Matcher on the field", SEE_TEXT_FOR_HAMCREST_MATCHER)
				.havingImplementation(fieldName + "= new " + getMethodFieldName() + "Matcher(matcher);\nreturn this;"));

		tmp.add(FieldDSLMethod.of(this).withDeclaration(fieldType + " value")
				.withJavaDoc("", "value an expected value for the field, which will be compared using the is matcher",
						SEE_TEXT_FOR_IS_MATCHER)
				.havingDefault(MATCHERS + ".is(value)"));

		tmp.add(FieldDSLMethod.of(this)
				.withGenericDeclaration("<_TARGETFIELD>", "As",
						"java.util.function.Function<" + fieldType
								+ ",_TARGETFIELD> converter,org.hamcrest.Matcher<? super _TARGETFIELD> matcher")
				.withJavaDoc("by converting the received field before validat it",
						"converter a function to convert the field\nmatcher a matcher on the resulting\n<_TARGETFIELD> The type which this field must be converter")
				.havingDefault("asFeatureMatcher(\" <field is converted> \",converter,matcher)"));

		if (fullyQualifiedNameMatcherInSameRound != null && fieldTypeAsTypeElement.getTypeParameters().isEmpty()) {
			String name = fieldTypeAsTypeElement.getSimpleName().toString();
			String lname = name.substring(0, 1).toLowerCase() + name.substring(1);

			tmp.add(FieldDSLMethod.of(this)
					.withExplicitDeclaration(fullyQualifiedNameMatcherInSameRound + "." + name + "Matcher" + "<"
							+ defaultReturnMethod + "> " + fieldName + "With()")
					.withJavaDoc("by starting a matcher for this field")
					.havingImplementation(fullyQualifiedNameMatcherInSameRound + "." + name + "Matcher tmp = "
							+ fullyQualifiedNameMatcherInSameRound + "." + lname + "WithParent(this);\n" + fieldName
							+ "(tmp);\nreturn tmp;"));
		}
		tmp.addAll(getFieldDslMethodFor(this, type));
		AddToMatcher addToMatchers[] = fieldElement.getAnnotationsByType(AddToMatcher.class);
		Arrays.stream(addToMatchers)
				.map(a -> FieldDSLMethod.of(this).withDeclaration(a.suffix(), a.argument()).withDefaultJavaDoc()
						.havingImplementation(Arrays.stream(a.body()).collect(joining("\n")) + "\nreturn this;"))
				.filter(Objects::nonNull).forEach(tmp::add);
		this.dsl = Collections.unmodifiableList(tmp);
	}

	public String getImplementationInterface() {
		return dsl.stream().map(FieldDSLMethod::asImplementationMethod).collect(joining("\n"));
	}

	public String getDslInterface() {
		return dsl.stream().map(FieldDSLMethod::asDSLMethod).collect(joining("\n"));

	}

	public String getMatcherForField() {
		String methodFieldName = getMethodFieldName();
		StringBuilder sb = new StringBuilder();
		sb.append("private static class " + methodFieldName + "Matcher" + enclosingClassOfFieldFullGeneric
				+ " extends org.hamcrest.FeatureMatcher<" + fullyQualifiedNameEnclosingClassOfField
				+ enclosingClassOfFieldGeneric + "," + fieldType + "> {\n");
		sb.append(
				"  public " + methodFieldName + "Matcher(org.hamcrest.Matcher<? super " + fieldType + "> matcher) {\n");
		sb.append("    super(matcher,\"" + fieldName + "\",\"" + fieldName + "\");\n");
		sb.append("  }\n");

		sb.append("  protected " + fieldType + " featureValueOf(" + fullyQualifiedNameEnclosingClassOfField
				+ enclosingClassOfFieldGeneric + " actual) {\n");
		sb.append("    return actual." + getFieldAccessor() + ";\n");
		sb.append("  }\n");
		sb.append("}\n");
		return sb.toString();
	}

	public String getFieldCopyDefault(String lhs, String rhs) {
		return lhs + "." + fieldName + "(" + MATCHERS + ".is(" + rhs + "." + getFieldAccessor() + "))";
	}

	public String getSameValueMatcherFor(String target) {
		String name = fieldTypeAsTypeElement.getSimpleName().toString();
		String lname = name.substring(0, 1).toLowerCase() + name.substring(1);
		return fullyQualifiedNameMatcherInSameRound + "." + lname + "WithSameValue(" + target + ")";
	}

	public String getFieldCopySameRound(String lhs, String rhs) {
		String fieldAccessor = getFieldAccessor();
		return lhs + "." + fieldName + "(" + rhs + "." + fieldAccessor + "==null?" + MATCHERS + ".nullValue():"
				+ getSameValueMatcherFor(rhs + "." + fieldAccessor) + ")";
	}

	public String generateMatcherBuilderReferenceFor(String generic) {
		return Optional.ofNullable(roundMirror.getByName(generic)).map(
				t -> t.getFullyQualifiedNameOfGeneratedClass() + "::" + t.getMethodShortClassName() + "WithSameValue")
				.orElse(MATCHERS + "::is");
	}

	public String getFieldCopyForList(String lhs, String rhs) {
		String fieldAccessor = getFieldAccessor();
		return "if(" + rhs + "." + fieldAccessor + "==null) {" + lhs + "." + fieldName + "(" + MATCHERS
				+ ".nullValue()); } else if (" + rhs + "." + fieldAccessor + ".isEmpty()) {" + lhs + "." + fieldName
				+ "IsEmptyIterable(); } else {" + lhs + "." + fieldName + "Contains(" + rhs + "." + fieldAccessor
				+ ".stream().map(" + generateMatcherBuilderReferenceFor(generic)
				+ ").collect(java.util.stream.Collectors.toList())); }";
	}

	public String getFieldCopy(String lhs, String rhs) {
		if ((type == Type.LIST || type == Type.SET || type == Type.COLLECTION) && !"".equals(generic)) {
			return getFieldCopyForList(lhs, rhs);
		}

		if (fullyQualifiedNameMatcherInSameRound != null && fieldTypeAsTypeElement.getTypeParameters().isEmpty()) {
			return getFieldCopySameRound(lhs, rhs);
		}
		return getFieldCopyDefault(lhs, rhs);
	}

	public String asMatchesSafely() {
		return String.format(
				"if(!%1$s.matches(actual)) {\n  mismatchDescription.appendText(\"[\"); %1$s.describeMismatch(actual,mismatchDescription); mismatchDescription.appendText(\"]\\n\");\n  result=false;\n}",
				fieldName);
	}

	public String asDescribeTo() {
		return "description.appendText(\"[\").appendDescriptionOf(" + fieldName + ").appendText(\"]\\n\");";
	}

	public String asMatcherField() {
		return String.format("private %1$sMatcher %2$s = new %1$sMatcher(%3$s.anything(%4$s));", getMethodFieldName(),
				fieldName, MATCHERS, ignore ? ("\"This field is ignored \"+"
						+ CommonUtils.toJavaSyntax(getDescriptionForIgnoreIfApplicable())) : "");
	}

	public boolean isIgnore() {
		return ignore;
	}

	public boolean isNotIgnore() {
		return !ignore;
	}

	public String getDescriptionForIgnoreIfApplicable() {
		return Optional.ofNullable(fieldElement.getAnnotation(IgnoreInMatcher.class)).map(i -> i.comments()).orElse("");
	}

	public String getFullyQualifiedNameEnclosingClassOfField() {
		return fullyQualifiedNameEnclosingClassOfField;
	}

	public String getDefaultReturnMethod() {
		return defaultReturnMethod;
	}

	public GeneratedMatcherField asGeneratedMatcherField() {
		GeneratedMatcherField gmf = new GeneratedMatcherField();
		gmf.setFieldIsIgnored(ignore);
		gmf.setFieldName(fieldName);
		gmf.setFieldCategory(type.name());
		gmf.setFieldAccessor(getFieldAccessor());
		gmf.setGenericDetails(generic);
		return gmf;
	}

}
