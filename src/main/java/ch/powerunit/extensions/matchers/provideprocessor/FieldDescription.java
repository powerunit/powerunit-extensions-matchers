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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVariable;
import javax.lang.model.util.TypeKindVisitor8;
import javax.tools.Diagnostic.Kind;

import ch.powerunit.extensions.matchers.AddToMatcher;
import ch.powerunit.extensions.matchers.IgnoreInMatcher;
import ch.powerunit.extensions.matchers.common.CommonUtils;
import ch.powerunit.extensions.matchers.provideprocessor.xml.GeneratedMatcherField;

public class FieldDescription {

	private static final String SEE_TEXT_FOR_IS_MATCHER = "org.hamcrest.Matchers#is(java.lang.Object)";
	private static final String SEE_TEXT_FOR_HAMCREST_MATCHER = "org.hamcrest.Matchers The main class from hamcrest that provides default matchers.";

	public static enum Type {
		NA, ARRAY, COLLECTION, LIST, SET, OPTIONAL, COMPARABLE, STRING, SUPPLIER
	}

	private final String fieldAccessor;
	private final ProcessingEnvironment processingEnv;
	private final String fieldName;
	private final String methodFieldName;
	private final String fieldType;
	private final String fullyQualifiedNameMatcherInSameRound;
	private final Type type;
	private final List<Supplier<String>> implGenerator;
	private final List<Supplier<String>> dslGenerator;
	private final ProvidesMatchersAnnotatedElementMirror containingElementMirror;
	private final boolean ignore;
	private final Element fieldElement;
	private final String generic;
	private final String defaultReturnMethod;
	private final String fullyQualifiedNameEnclosingClassOfField;
	private final String enclosingClassOfFieldFullGeneric;
	private final String enclosingClassOfFieldGeneric;
	private final TypeElement fieldTypeAsTypeElement;

	public static final class ExtracTypeVisitor extends TypeKindVisitor8<Type, ProcessingEnvironment> {

		@Override
		protected Type defaultAction(TypeMirror t, ProcessingEnvironment processingEnv) {
			return Type.NA;
		}

		@Override
		public Type visitArray(ArrayType t, ProcessingEnvironment processingEnv) {
			return Type.ARRAY;
		}

		@Override
		public Type visitDeclared(DeclaredType t, ProcessingEnvironment processingEnv) {
			if (processingEnv.getTypeUtils().isAssignable(t, processingEnv.getTypeUtils()
					.erasure(processingEnv.getElementUtils().getTypeElement("java.util.Optional").asType()))) {
				return Type.OPTIONAL;
			}
			if (processingEnv.getTypeUtils().isAssignable(t, processingEnv.getTypeUtils()
					.erasure(processingEnv.getElementUtils().getTypeElement("java.util.Set").asType()))) {
				return Type.SET;
			}
			if (processingEnv.getTypeUtils().isAssignable(t, processingEnv.getTypeUtils()
					.erasure(processingEnv.getElementUtils().getTypeElement("java.util.List").asType()))) {
				return Type.LIST;
			}
			if (processingEnv.getTypeUtils().isAssignable(t, processingEnv.getTypeUtils()
					.erasure(processingEnv.getElementUtils().getTypeElement("java.util.Collection").asType()))) {
				return Type.COLLECTION;
			}
			if (processingEnv.getTypeUtils().isAssignable(t, processingEnv.getTypeUtils()
					.erasure(processingEnv.getElementUtils().getTypeElement("java.lang.String").asType()))) {
				return Type.STRING;
			}
			if (processingEnv.getTypeUtils().isAssignable(t, processingEnv.getTypeUtils()
					.erasure(processingEnv.getElementUtils().getTypeElement("java.lang.Comparable").asType()))) {
				return Type.COMPARABLE;
			}
			if (processingEnv.getTypeUtils().isAssignable(t, processingEnv.getTypeUtils()
					.erasure(processingEnv.getElementUtils().getTypeElement("java.util.function.Supplier").asType()))) {
				return Type.SUPPLIER;
			}
			return Type.NA;
		}

		@Override
		public Type visitTypeVariable(TypeVariable t, ProcessingEnvironment processingEnv) {
			return Type.NA;
		}

		@Override
		public Type visitUnknown(TypeMirror t, ProcessingEnvironment processingEnv) {
			processingEnv.getMessager().printMessage(Kind.MANDATORY_WARNING, "Unsupported type element");
			return Type.NA;
		}
	}

	public static final String computeGenericInformation(TypeMirror fieldTypeMirror) {
		if (fieldTypeMirror instanceof DeclaredType) {
			DeclaredType dt = ((DeclaredType) fieldTypeMirror);
			return dt.getTypeArguments().stream().map(Object::toString).collect(Collectors.joining(","));
		}
		return "";
	}

	public static final String computeFullyQualifiedNameMatcherInSameRound(ProcessingEnvironment processingEnv,
			boolean isInSameRound, TypeElement fieldTypeAsTypeElement) {
		if (isInSameRound && fieldTypeAsTypeElement != null) {
			return new ProvideMatchersMirror(processingEnv, fieldTypeAsTypeElement)
					.getFullyQualifiedNameOfGeneratedClass();
		}
		return null;
	}

	public static final List<Supplier<String>> getDslSupplierFor(FieldDescription target, Type type, String generic) {
		List<Supplier<String>> tmp2 = new ArrayList<>();
		switch (type) {
		case ARRAY:
			tmp2.add(target::getDslForArray);
			break;
		case OPTIONAL:
			tmp2.add(target::getDslForOptional);
			break;
		case COMPARABLE:
			tmp2.add(target::getDslForComparable);
			break;
		case STRING:
			tmp2.add(target::getDslForComparable);
			tmp2.add(target::getDslForString);
			break;
		case COLLECTION:
		case LIST:
		case SET:
			tmp2.add(target::getDslForIterable);
			tmp2.add(target::getDslForCollection);
			if (!"".equals(generic)) {
				tmp2.add(target::getDslForIterableWithGeneric);
			}
			break;
		case SUPPLIER:
			tmp2.add(target::getDslForSupplier);
			break;
		default:
			// Nothing
		}
		return tmp2;
	}

	public FieldDescription(ProvidesMatchersAnnotatedElementMirror containingElementMirror, String fieldName,
			String fieldType, boolean isInSameRound, Element fieldElement) {
		TypeMirror fieldTypeMirror = (fieldElement instanceof ExecutableElement)
				? ((ExecutableElement) fieldElement).getReturnType() : fieldElement.asType();
		this.containingElementMirror = containingElementMirror;
		this.enclosingClassOfFieldFullGeneric = containingElementMirror.getFullGeneric();
		this.enclosingClassOfFieldGeneric = containingElementMirror.getGeneric();
		this.fullyQualifiedNameEnclosingClassOfField = containingElementMirror
				.getFullyQualifiedNameOfClassAnnotatedWithProvideMatcher();
		this.processingEnv = containingElementMirror.getProcessingEnv();
		this.fieldAccessor = fieldElement.getSimpleName().toString()
				+ ((fieldElement instanceof ExecutableElement) ? "()" : "");
		this.fieldName = fieldName;
		this.methodFieldName = fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
		this.fieldType = fieldType;
		this.type = new ExtracTypeVisitor().visit(fieldTypeMirror, processingEnv);
		this.ignore = fieldElement.getAnnotation(IgnoreInMatcher.class) != null;
		this.fieldElement = fieldElement;
		this.defaultReturnMethod = containingElementMirror.getDefaultReturnMethod();
		this.generic = computeGenericInformation(fieldTypeMirror);
		this.fieldTypeAsTypeElement = processingEnv.getElementUtils().getTypeElement(fieldType);
		this.fullyQualifiedNameMatcherInSameRound = computeFullyQualifiedNameMatcherInSameRound(processingEnv,
				isInSameRound, fieldTypeAsTypeElement);
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
				Arrays.stream(a.body()).map(l -> l).collect(Collectors.joining("\n")) + "\n" + "return this;");
	}

	public String getJavaDocFor(Optional<String> addToDescription, Optional<String> param, Optional<String> see) {
		String linkToAccessor = "{@link " + fullyQualifiedNameEnclosingClassOfField + "#" + getFieldAccessor()
				+ " This field is accessed by using this approach}.";
		StringBuilder sb = new StringBuilder();
		sb.append("/**").append("\n");
		sb.append(" * Add a validation on the field `").append(fieldName).append("`");
		addToDescription.ifPresent(t -> sb.append(" ").append(t));
		sb.append(".").append("\n");
		sb.append(" * <p>").append("\n");
		sb.append(" *").append("\n");
		sb.append(" * <i>").append(linkToAccessor).append("</i>").append("\n");
		sb.append(" * <p>").append("\n");
		sb.append(
				" * <b>In case method specifing a matcher on a fields are used several times, only the last setted matcher will be used.</b> ")
				.append("\n");
		sb.append(
				" * When several control must be done on a single field, hamcrest itself provides a way to combine several matchers (See for instance {@link org.hamcrest.Matchers#both(org.hamcrest.Matcher)}.")
				.append("\n");
		sb.append(" *").append("\n");
		param.ifPresent(t -> Arrays.stream(t.split("\n"))
				.forEach(l -> sb.append(" * @param ").append(l).append(".").append("\n")));
		sb.append(" * @return the DSL to continue the construction of the matcher.").append("\n");
		see.ifPresent(t -> sb.append(" * @see ").append(t).append("\n"));
		sb.append(" */");
		return sb.toString();
	}

	public String buildImplementation(String declaration, String body) {
		return new StringBuilder().append("@Override").append("\n").append("public ").append(declaration).append(" {\n")
				.append("  ").append(body.replaceAll("\\R", "\n" + "  ")).append("\n").append("}").append("\n")
				.toString();
	}

	public String buildDsl(String javadoc, String declaration) {
		return new StringBuilder().append(javadoc.replaceAll("\\R", "\n")).append("\n").append(declaration)
				.append(";\n").toString();
	}

	public String buildDefaultDsl(String javadoc, String declaration, String innerMatcher) {
		return new StringBuilder().append(javadoc.replaceAll("\\R", "\n")).append("\n").append("default ")
				.append(declaration).append("{\n").append("  ").append("return ").append(fieldName).append("(")
				.append(innerMatcher).append(");\n").append("}").toString();
	}

	public String generateDeclaration(String postFix, String arguments) {
		return new StringBuilder().append(defaultReturnMethod).append(" ").append(fieldName).append(postFix).append("(")
				.append(arguments).append(")").toString();
	}

	public String getImplementationForDefault() {
		return buildImplementation(generateDeclaration("", "org.hamcrest.Matcher<? super " + fieldType + "> matcher"),
				fieldName + "= new " + methodFieldName + "Matcher(matcher);\nreturn this;");
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
		return implGenerator.stream().map(g -> g.get()).collect(Collectors.joining("\n"));
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
				generateDeclaration("", fieldType + " value"), "org.hamcrest.Matchers.is(value)")).append("\n");

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
		return buildDsl(
				getJavaDocFor(Optional.of("by starting a matcher for this field"), Optional.empty(), Optional.empty()),
				fullyQualifiedNameMatcherInSameRound + "." + name + "Matcher" + "<" + defaultReturnMethod + "> "
						+ fieldName + "With()");
	}

	public String getDslForString() {
		StringBuilder sb = new StringBuilder();

		sb.append(buildDefaultDsl(
				getJavaDocFor(Optional.of("that the string contains another one"),
						Optional.of("other the string is contains in the other one"),
						Optional.of("org.hamcrest.Matchers#containsString(java.lang.String)")),
				generateDeclaration("ContainsString", "String other"), "org.hamcrest.Matchers.containsString(other)"));

		sb.append(buildDefaultDsl(
				getJavaDocFor(Optional.of("that the string starts with another one"),
						Optional.of("other the string to use to compare"),
						Optional.of("org.hamcrest.Matchers#startsWith(java.lang.String)")),
				generateDeclaration("StartsWith", "String other"), "org.hamcrest.Matchers.startsWith(other)"));

		sb.append(buildDefaultDsl(
				getJavaDocFor(Optional.of("that the string ends with another one"),
						Optional.of("other the string to use to compare"),
						Optional.of("org.hamcrest.Matchers#endsWith(java.lang.String)")),
				generateDeclaration("EndsWith", "String other"), "org.hamcrest.Matchers.endsWith(other)"));

		return sb.toString();
	}

	public String getDslForIterable() {
		StringBuilder sb = new StringBuilder();
		sb.append(buildDefaultDsl(
				getJavaDocFor(Optional.of("that the iterable is empty"), Optional.empty(), Optional.empty()),
				generateDeclaration("IsEmptyIterable", ""),
				"(org.hamcrest.Matcher)org.hamcrest.Matchers.emptyIterable()"));

		return sb.toString();
	}

	public String getDslForIterableWithGeneric() {
		StringBuilder sb = new StringBuilder();

		sb.append(buildDefaultDsl(
				getJavaDocFor(Optional.of("that the iterable contains the received elements"),
						Optional.of("elements the elements"),
						Optional.of("org.hamcrest.Matchers#contains(java.lang.Object[])")),
				generateDeclaration("Contains", generic + "... elements"), "org.hamcrest.Matchers.contains(elements)"));

		sb.append(buildDefaultDsl(
				getJavaDocFor(Optional.of("that the iterable contains the received elements, using matchers"),
						Optional.of("matchersOnElements the matchers on the elements"),
						Optional.of("org.hamcrest.Matchers#contains(org.hamcrest.Matcher[])")),
				generateDeclaration("Contains", "org.hamcrest.Matcher<" + generic + ">... matchersOnElements"),
				"org.hamcrest.Matchers.contains(matchersOnElements)"));

		sb.append(buildDefaultDsl(
				getJavaDocFor(Optional.of("that the iterable contains the received elements in any order"),
						Optional.of("elements the elements"),
						Optional.of("org.hamcrest.Matchers#containsInAnyOrder(java.lang.Object[])")),
				generateDeclaration("ContainsInAnyOrder", generic + "... elements"),
				"org.hamcrest.Matchers.containsInAnyOrder(elements)"));

		sb.append(
				buildDefaultDsl(
						getJavaDocFor(
								Optional.of(
										"that the iterable contains the received elements, using matchers in any order"),
								Optional.of("matchersOnElements the matchers on the elements"),
								Optional.of("org.hamcrest.Matchers#containsInAnyOrder(org.hamcrest.Matcher[])")),
						generateDeclaration("ContainsInAnyOrder",
								"org.hamcrest.Matcher<" + generic + ">... matchersOnElements"),
				"org.hamcrest.Matchers.containsInAnyOrder(matchersOnElements)"));

		sb.append(buildDefaultDsl(
				getJavaDocFor(Optional.of("that the iterable contains the received elements, using list of matcher"),
						Optional.of("matchersOnElements the matchers on the elements"),
						Optional.of("org.hamcrest.Matchers#contains(java.util.List)")),
				generateDeclaration("Contains",
						"java.util.List<org.hamcrest.Matcher<? super " + generic + ">> matchersOnElements"),
				"org.hamcrest.Matchers.contains(matchersOnElements)"));

		return sb.toString();
	}

	public String getDslForArray() {
		return buildDefaultDsl(
				getJavaDocFor(Optional.of("that the array is empty"), Optional.empty(), Optional.empty()),
				generateDeclaration("IsEmpty", ""), "(org.hamcrest.Matcher)org.hamcrest.Matchers.emptyArray()");
	}

	public String getDslForCollection() {
		return buildDefaultDsl(
				getJavaDocFor(Optional.of("that the collection is empty"), Optional.empty(), Optional.empty()),
				generateDeclaration("IsEmpty", ""), "(org.hamcrest.Matcher)org.hamcrest.Matchers.empty()");
	}

	public String getDslForOptional() {
		StringBuilder sb = new StringBuilder();

		sb.append(buildDefaultDsl(
				getJavaDocFor(Optional.of("with a present optional"), Optional.empty(), Optional.empty()),
				generateDeclaration("IsPresent", ""),
				"new org.hamcrest.CustomTypeSafeMatcher<" + fieldType
						+ ">(\"optional is present\"){ public boolean matchesSafely(" + fieldType
						+ " o) {return o.isPresent();}}"));

		sb.append(buildDefaultDsl(
				getJavaDocFor(Optional.of("with a not present optional"), Optional.empty(), Optional.empty()),
				generateDeclaration("IsNotPresent", ""),
				"new org.hamcrest.CustomTypeSafeMatcher<" + fieldType
						+ ">(\"optional is not present\"){ public boolean matchesSafely(" + fieldType
						+ " o) {return !o.isPresent();}}"));

		return sb.toString();
	}

	public String getDslForComparable() {
		StringBuilder sb = new StringBuilder();
		sb.append(buildDefaultDsl(
				getJavaDocFor(Optional.of("that this field is equals to another one, using the compareTo method"),
						Optional.of("value the value to compare with"),
						Optional.of("org.hamcrest.Matchers#comparesEqualTo(java.lang.Comparable)")),
				generateDeclaration("ComparesEqualTo", fieldType + " value"),
				"org.hamcrest.Matchers.comparesEqualTo(value)"));

		sb.append(buildDefaultDsl(
				getJavaDocFor(Optional.of("that this field is less than another value"),
						Optional.of("value the value to compare with"),
						Optional.of("org.hamcrest.Matchers#lessThan(java.lang.Comparable)")),
				generateDeclaration("LessThan", fieldType + " value"), "org.hamcrest.Matchers.lessThan(value)"));

		sb.append(buildDefaultDsl(
				getJavaDocFor(Optional.of("that this field is less or equal than another value"),
						Optional.of("value the value to compare with"),
						Optional.of("org.hamcrest.Matchers#lessThanOrEqualTo(java.lang.Comparable)")),
				generateDeclaration("LessThanOrEqualTo", fieldType + " value"),
				"org.hamcrest.Matchers.lessThanOrEqualTo(value)"));

		sb.append(buildDefaultDsl(
				getJavaDocFor(Optional.of("that this field is greater than another value"),
						Optional.of("value the value to compare with"),
						Optional.of("org.hamcrest.Matchers#greaterThan(java.lang.Comparable)")),
				generateDeclaration("GreaterThan", fieldType + " value"), "org.hamcrest.Matchers.greaterThan(value)"));

		sb.append(buildDefaultDsl(
				getJavaDocFor(Optional.of("that this field is greater or equal than another value"),
						Optional.of("value the value to compare with"),
						Optional.of("org.hamcrest.Matchers#greaterThanOrEqualTo(java.lang.Comparable)")),
				generateDeclaration("GreaterThanOrEqualTo", fieldType + " value"),
				"org.hamcrest.Matchers.greaterThanOrEqualTo(value)"));

		return sb.toString();
	}

	public String getDslInterface() {
		return dslGenerator.stream().map(g -> g.get()).collect(Collectors.joining("\n"));
	}

	public String getMatcherForField() {
		StringBuilder sb = new StringBuilder();
		sb.append("private static class " + methodFieldName + "Matcher" + enclosingClassOfFieldFullGeneric
				+ " extends org.hamcrest.FeatureMatcher<" + fullyQualifiedNameEnclosingClassOfField
				+ enclosingClassOfFieldGeneric + "," + fieldType + "> {").append("\n");
		sb.append("  public " + methodFieldName + "Matcher(org.hamcrest.Matcher<? super " + fieldType + "> matcher) {")
				.append("\n");
		sb.append("    super(matcher,\"" + fieldName + "\",\"" + fieldName + "\");").append("\n");
		sb.append("  }").append("\n");

		sb.append("  protected " + fieldType + " featureValueOf(" + fullyQualifiedNameEnclosingClassOfField
				+ enclosingClassOfFieldGeneric + " actual) {").append("\n");
		sb.append("    return actual." + fieldAccessor + ";").append("\n");
		sb.append("  }").append("\n");
		sb.append("}").append("\n");

		return sb.toString();
	}

	public String getFieldCopyDefault(String lhs, String rhs) {
		return lhs + "." + fieldName + "(org.hamcrest.Matchers.is(" + rhs + "." + fieldAccessor + "))";
	}

	public String getSameValueMatcherFor(String target) {
		String name = fieldTypeAsTypeElement.getSimpleName().toString();
		String lname = name.substring(0, 1).toLowerCase() + name.substring(1);
		return fullyQualifiedNameMatcherInSameRound + "." + lname + "WithSameValue(" + target + ")";
	}

	public String getFieldCopySameRound(String lhs, String rhs) {
		return lhs + "." + fieldName + "(" + rhs + "." + fieldAccessor + "==null?org.hamcrest.Matchers.nullValue():"
				+ getSameValueMatcherFor(rhs + "." + fieldAccessor) + ")";
	}

	public String generateMatcherBuilderReferenceFor(String generic) {
		return containingElementMirror.findMirrorFor(generic).map(
				t -> t.getFullyQualifiedNameOfGeneratedClass() + "::" + t.getMethodShortClassName() + "WithSameValue")
				.orElse("org.hamcrest.Matchers::is");
	}

	public String getFieldCopyForList(String lhs, String rhs) {
		return "if(" + rhs + "." + fieldAccessor + "==null) {" + lhs + "." + fieldName
				+ "(org.hamcrest.Matchers.nullValue()); } else if (" + rhs + "." + fieldAccessor + ".isEmpty()) {" + lhs
				+ "." + fieldName + "IsEmptyIterable(); } else {" + lhs + "." + fieldName + "Contains(" + rhs + "."
				+ fieldAccessor + ".stream().map(" + generateMatcherBuilderReferenceFor(generic)
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
		return new StringBuilder().append("if(!").append(fieldName).append(".matches(actual)) {\n")
				.append("  mismatchDescription.appendText(\"[\"); ").append(fieldName)
				.append(".describeMismatch(actual,mismatchDescription); mismatchDescription.appendText(\"]\\n\");\n")
				.append("  result=false;\n").append("}").toString();
	}

	public String asDescribeTo() {
		return "description.appendText(\"[\").appendDescriptionOf(" + fieldName + ").appendText(\"]\\n\");";
	}

	public String asMatcherField() {
		return "private " + methodFieldName + "Matcher " + fieldName + " = new " + methodFieldName
				+ "Matcher(org.hamcrest.Matchers.anything(" + (ignore ? "\"This field is ignored \"+"
						+ CommonUtils.toJavaSyntax(getDescriptionForIgnoreIfApplicable()) : "")
				+ "));";
	}

	public String getFieldAccessor() {
		return fieldAccessor;
	}

	public String getFieldName() {
		return fieldName;
	}

	public String getMethodFieldName() {
		return methodFieldName;
	}

	public String getFieldType() {
		return fieldType;
	}

	public Type getType() {
		return type;
	}

	public boolean isIgnore() {
		return ignore;
	}

	public boolean isNotIgnore() {
		return !ignore;
	}

	public Element getFieldElement() {
		return fieldElement;
	}

	public String getDescriptionForIgnoreIfApplicable() {
		return Optional.ofNullable(fieldElement.getAnnotation(IgnoreInMatcher.class)).map(i -> i.comments()).orElse("");
	}

	public GeneratedMatcherField asGeneratedMatcherField() {
		GeneratedMatcherField gmf = new GeneratedMatcherField();
		gmf.setFieldIsIgnored(ignore);
		gmf.setFieldName(fieldName);
		gmf.setFieldCategory(type.name());
		gmf.setFieldAccessor(fieldAccessor);
		gmf.setGenericDetails(generic);
		return gmf;
	}

}
