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
import java.util.function.Supplier;

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

	private final List<Supplier<String>> implGenerator;
	private final List<Supplier<String>> dslGenerator;
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

	public static final List<Supplier<String>> getDslSupplierFor(FieldDescription target, Type type, String generic) {
		switch (type) {
		case ARRAY:
			return Collections.singletonList(target::getDslForArray);
		case OPTIONAL:
			return Collections.singletonList(target::getDslForOptional);
		case COMPARABLE:
			return Collections.singletonList(target::getDslForComparable);
		case STRING:
			return Arrays.asList(target::getDslForComparable, target::getDslForString);
		case COLLECTION:
		case LIST:
		case SET:
			if (!"".equals(generic)) {
				return Arrays.asList(target::getDslForIterable, target::getDslForCollection,
						target::getDslForIterableWithGeneric);
			}
			return Arrays.asList(target::getDslForIterable, target::getDslForCollection);
		case SUPPLIER:
			return Collections.singletonList(target::getDslForSupplier);
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
		List<Supplier<String>> tmp1 = new ArrayList<>(Arrays.asList(this::getImplementationForDefault));
		List<Supplier<String>> tmp2 = new ArrayList<>(Arrays.asList(this::getDslForDefault));
		if (fullyQualifiedNameMatcherInSameRound != null && fieldTypeAsTypeElement.getTypeParameters().isEmpty()) {
			tmp1.add(this::getImplementationForDefaultChaining);
			tmp2.add(this::getDslForDefaultChaining);
		}
		tmp2.addAll(getDslSupplierFor(this, type, generic));
		AddToMatcher addToMatchers[] = fieldElement.getAnnotationsByType(AddToMatcher.class);
		Arrays.stream(addToMatchers).map(this::generateFunctionForImplementation).filter(Objects::nonNull)
				.forEach(tmp1::add);
		Arrays.stream(addToMatchers).map(this::generateFunctionForDSL).filter(Objects::nonNull).forEach(tmp2::add);
		implGenerator = Collections.unmodifiableList(tmp1);
		dslGenerator = Collections.unmodifiableList(tmp2);
	}

	public Supplier<String> generateFunctionForDSL(AddToMatcher a) {
		return () -> buildDsl(getJavaDocFor(Optional.empty(), Optional.empty(), Optional.empty()),
				generateDeclaration(a.suffix(), a.argument()));
	}

	public Supplier<String> generateFunctionForImplementation(AddToMatcher a) {
		return () -> buildImplementation(generateDeclaration(a.suffix(), a.argument()),
				Arrays.stream(a.body()).collect(joining("\n")) + "\nreturn this;");
	}

	public String getJavaDocFor(String addToDescription) {
		return getJavaDocFor(Optional.of(addToDescription), Optional.empty(), Optional.empty());
	}

	public String getJavaDocFor(String addToDescription, String param, String see) {
		return getJavaDocFor(Optional.of(addToDescription), Optional.of(param), Optional.of(see));
	}

	public String getJavaDocFor(Optional<String> addToDescription, Optional<String> param, Optional<String> see) {
		String linkToAccessor = "{@link " + fullyQualifiedNameEnclosingClassOfField + "#" + getFieldAccessor()
				+ " This field is accessed by using this approach}.";
		StringBuilder sb = new StringBuilder();
		sb.append("/**\n * Add a validation on the field `").append(fieldName).append("`");
		addToDescription.ifPresent(t -> sb.append(" ").append(t));
		sb.append(".\n * <p>").append("\n *\n * <i>").append(linkToAccessor).append("</i>\n * <p>\n");
		sb.append(
				" * <b>In case method specifing a matcher on a fields are used several times, only the last setted matcher will be used.</b> \n");
		sb.append(
				" * When several control must be done on a single field, hamcrest itself provides a way to combine several matchers (See for instance {@link "
						+ MATCHERS + "#both(org.hamcrest.Matcher)}.\n");
		sb.append(" *\n");
		param.ifPresent(
				t -> Arrays.stream(t.split("\n")).forEach(l -> sb.append(" * @param ").append(l).append(".\n")));
		sb.append(" * @return the DSL to continue the construction of the matcher.\n");
		see.ifPresent(t -> sb.append(" * @see ").append(t).append("\n"));
		sb.append(" */");
		return sb.toString();
	}

	public static String buildImplementation(String declaration, String body) {
		return String.format("@Override\npublic %1$s {\n  %2$s\n}\n", declaration, body.replaceAll("\\R", "\n" + "  "));
	}

	public static String buildDsl(String javadoc, String declaration) {
		return String.format("%1$s\n%2$s;\n", javadoc.replaceAll("\\R", "\n"), declaration);
	}

	public String buildDefaultDsl(String javadoc, String declaration, String innerMatcher) {
		return String.format("%1$s\ndefault %2$s{\n  return %3$s(%4$s);\n}", javadoc.replaceAll("\\R", "\n"),
				declaration, fieldName, innerMatcher);

	}

	public String generateDeclaration(String postFix, String arguments) {
		return String.format("%1$s %2$s%3$s(%4$s)", defaultReturnMethod, fieldName, postFix, arguments);
	}

	public String getImplementationForDefault() {
		return buildImplementation(generateDeclaration("", "org.hamcrest.Matcher<? super " + fieldType + "> matcher"),
				fieldName + "= new " + getMethodFieldName() + "Matcher(matcher);\nreturn this;");
	}

	public String getImplementationForDefaultChaining() {
		// Can't use buildDeclaration here
		String name = fieldTypeAsTypeElement.getSimpleName().toString();
		String lname = name.substring(0, 1).toLowerCase() + name.substring(1);
		return buildImplementation(
				fullyQualifiedNameMatcherInSameRound + "." + name + "Matcher" + "<" + defaultReturnMethod + "> "
						+ fieldName + "With()",
				fullyQualifiedNameMatcherInSameRound + "." + name + "Matcher tmp = "
						+ fullyQualifiedNameMatcherInSameRound + "." + lname + "WithParent(this);\n" + fieldName
						+ "(tmp);\nreturn tmp;");
	}

	public String getImplementationInterface() {
		return implGenerator.stream().map(Supplier::get).collect(joining("\n"));
	}

	public String getDslForSupplier() {
		return buildDefaultDsl(
				getJavaDocFor(
						Optional.of(
								" Validate that the result of the supplier is accepted by another matcher (the result of the execution must be stable)"),
						Optional.of("matcherOnResult a Matcher on result of the supplier execution"), Optional.empty()),
				generateDeclaration("SupplierResult", "org.hamcrest.Matcher<? super " + generic + "> matcherOnResult"),
				"asFeatureMatcher(\"with supplier result\",(java.util.function.Supplier<" + generic
						+ "> s) -> s.get(),matcherOnResult)");
	}

	public String getDslForDefault() {
		StringBuilder sb = new StringBuilder();
		sb.append(buildDsl(
				getJavaDocFor(Optional.empty(), Optional.of("matcher a Matcher on the field"),
						Optional.of(SEE_TEXT_FOR_HAMCREST_MATCHER)),
				generateDeclaration("", "org.hamcrest.Matcher<? super " + fieldType + "> matcher"))).append("\n");

		sb.append(buildDefaultDsl(
				getJavaDocFor(Optional.empty(),
						Optional.of(
								"value an expected value for the field, which will be compared using the is matcher"),
						Optional.of(SEE_TEXT_FOR_IS_MATCHER)),
				generateDeclaration("", fieldType + " value"), MATCHERS + ".is(value)")).append("\n");

		sb.append(buildDefaultDsl(
				getJavaDocFor(Optional.of("by converting the received field before validat it"),
						Optional.of(
								"converter a function to convert the field\nmatcher a matcher on the resulting\n<_TARGETFIELD> The type which this field must be converter"),
				Optional.empty()),
				"<_TARGETFIELD> " + generateDeclaration("As",
						"java.util.function.Function<" + fieldType
								+ ",_TARGETFIELD> converter,org.hamcrest.Matcher<? super _TARGETFIELD> matcher"),
				"asFeatureMatcher(\" <field is converted> \",converter,matcher)")).append("\n");

		return sb.toString();
	}

	public String getDslForDefaultChaining() {
		// can'ut use generateDeclaration here
		String name = fieldTypeAsTypeElement.getSimpleName().toString();
		return buildDsl(getJavaDocFor("by starting a matcher for this field"), fullyQualifiedNameMatcherInSameRound
				+ "." + name + "Matcher" + "<" + defaultReturnMethod + "> " + fieldName + "With()");
	}

	public String getDslForString() {
		StringBuilder sb = new StringBuilder();
		sb.append(buildDefaultDsl(
				getJavaDocFor("that the string contains another one", "other the string is contains in the other one",
						MATCHERS + "#containsString(java.lang.String)"),
				generateDeclaration("ContainsString", "String other"), MATCHERS + ".containsString(other)"));
		sb.append(buildDefaultDsl(
				getJavaDocFor("that the string starts with another one", "other the string to use to compare",
						MATCHERS + "#startsWith(java.lang.String)"),
				generateDeclaration("StartsWith", "String other"), MATCHERS + ".startsWith(other)"));
		sb.append(buildDefaultDsl(
				getJavaDocFor("that the string ends with another one", "other the string to use to compare",
						MATCHERS + "#endsWith(java.lang.String)"),
				generateDeclaration("EndsWith", "String other"), MATCHERS + ".endsWith(other)"));
		return sb.toString();
	}

	public String getDslForIterable() {
		return buildDefaultDsl(getJavaDocFor("that the iterable is empty"), generateDeclaration("IsEmptyIterable", ""),
				"(org.hamcrest.Matcher)" + MATCHERS + ".emptyIterable()");

	}

	public String getDslForIterableWithGeneric() {
		StringBuilder sb = new StringBuilder();
		sb.append(buildDefaultDsl(
				getJavaDocFor("that the iterable contains the received elements", "elements the elements",
						MATCHERS + "#contains(java.lang.Object[])"),
				generateDeclaration("Contains", generic + "... elements"), MATCHERS + ".contains(elements)"));
		sb.append(buildDefaultDsl(
				getJavaDocFor("that the iterable contains the received elements, using matchers",
						DEFAULT_JAVADOC_MATCHER_ON_ELEMENTS, MATCHERS + "#contains(org.hamcrest.Matcher[])"),
				generateDeclaration("Contains", "org.hamcrest.Matcher<" + generic + ">... matchersOnElements"),
				MATCHERS + ".contains(matchersOnElements)"));
		sb.append(buildDefaultDsl(
				getJavaDocFor("that the iterable contains the received elements in any order", "elements the elements",
						MATCHERS + "#containsInAnyOrder(java.lang.Object[])"),
				generateDeclaration("ContainsInAnyOrder", generic + "... elements"),
				MATCHERS + ".containsInAnyOrder(elements)"));
		sb.append(buildDefaultDsl(
				getJavaDocFor("that the iterable contains the received elements, using matchers in any order",
						DEFAULT_JAVADOC_MATCHER_ON_ELEMENTS, MATCHERS + "#containsInAnyOrder(org.hamcrest.Matcher[])"),
				generateDeclaration("ContainsInAnyOrder",
						"org.hamcrest.Matcher<" + generic + ">... matchersOnElements"),
				MATCHERS + ".containsInAnyOrder(matchersOnElements)"));
		sb.append(buildDefaultDsl(
				getJavaDocFor("that the iterable contains the received elements, using list of matcher",
						DEFAULT_JAVADOC_MATCHER_ON_ELEMENTS, MATCHERS + "#contains(java.util.List)"),
				generateDeclaration("Contains",
						"java.util.List<org.hamcrest.Matcher<? super " + generic + ">> matchersOnElements"),
				MATCHERS + ".contains(matchersOnElements)"));
		return sb.toString();
	}

	public String getDslForArray() {
		return buildDefaultDsl(getJavaDocFor("that the array is empty"), generateDeclaration("IsEmpty", ""),
				"(org.hamcrest.Matcher)" + MATCHERS + ".emptyArray()");
	}

	public String getDslForCollection() {
		return buildDefaultDsl(getJavaDocFor("that the collection is empty"), generateDeclaration("IsEmpty", ""),
				"(org.hamcrest.Matcher)" + MATCHERS + ".empty()");
	}

	public String getDslForOptional() {
		StringBuilder sb = new StringBuilder();
		sb.append(buildDefaultDsl(getJavaDocFor("with a present optional"), generateDeclaration("IsPresent", ""),
				"new org.hamcrest.CustomTypeSafeMatcher<" + fieldType
						+ ">(\"optional is present\"){ public boolean matchesSafely(" + fieldType
						+ " o) {return o.isPresent();}}"));
		sb.append(buildDefaultDsl(getJavaDocFor("with a not present optional"), generateDeclaration("IsNotPresent", ""),
				"new org.hamcrest.CustomTypeSafeMatcher<" + fieldType
						+ ">(\"optional is not present\"){ public boolean matchesSafely(" + fieldType
						+ " o) {return !o.isPresent();}}"));
		return sb.toString();
	}

	public String getDslForComparable() {
		StringBuilder sb = new StringBuilder();
		sb.append(buildDefaultDsl(
				getJavaDocFor("that this field is equals to another one, using the compareTo method",
						"value the value to compare with", MATCHERS + "#comparesEqualTo(java.lang.Comparable)"),
				generateDeclaration("ComparesEqualTo", fieldType + " value"), MATCHERS + ".comparesEqualTo(value)"));
		sb.append(buildDefaultDsl(
				getJavaDocFor("that this field is less than another value", "value the value to compare with",
						MATCHERS + "#lessThan(java.lang.Comparable)"),
				generateDeclaration("LessThan", fieldType + " value"), MATCHERS + ".lessThan(value)"));
		sb.append(buildDefaultDsl(
				getJavaDocFor("that this field is less or equal than another value", "value the value to compare with",
						MATCHERS + "#lessThanOrEqualTo(java.lang.Comparable)"),
				generateDeclaration("LessThanOrEqualTo", fieldType + " value"),
				MATCHERS + ".lessThanOrEqualTo(value)"));
		sb.append(buildDefaultDsl(
				getJavaDocFor("that this field is greater than another value", "value the value to compare with",
						MATCHERS + "#greaterThan(java.lang.Comparable)"),
				generateDeclaration("GreaterThan", fieldType + " value"), MATCHERS + ".greaterThan(value)"));
		sb.append(buildDefaultDsl(
				getJavaDocFor("that this field is greater or equal than another value",
						"value the value to compare with", MATCHERS + "#greaterThanOrEqualTo(java.lang.Comparable)"),
				generateDeclaration("GreaterThanOrEqualTo", fieldType + " value"),
				MATCHERS + ".greaterThanOrEqualTo(value)"));
		return sb.toString();
	}

	public String getDslInterface() {
		return dslGenerator.stream().map(Supplier::get).collect(joining("\n"));
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
