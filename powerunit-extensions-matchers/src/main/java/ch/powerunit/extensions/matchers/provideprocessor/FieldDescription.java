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
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;

import ch.powerunit.extensions.matchers.AddToMatcher;
import ch.powerunit.extensions.matchers.IgnoreInMatcher;
import ch.powerunit.extensions.matchers.ProvideMatchers;
import ch.powerunit.extensions.matchers.provideprocessor.xml.GeneratedMatcherField;

public class FieldDescription {

	private static final String SEE_TEXT_FOR_IS_MATCHER = "org.hamcrest.Matchers#is(java.lang.Object)";
	private static final String SEE_TEXT_FOR_HAMCREST_MATCHER = "org.hamcrest.Matchers The main class from hamcrest that provides default matchers.";

	public static enum Type {
		NA, ARRAY, COLLECTION, LIST, SET, OPTIONAL, COMPARABLE, STRING, SUPPLIER
	}

	private final String fieldAccessor;
	private final String fieldName;
	private final String methodFieldName;
	private final String fieldType;
	private final String fullyQualifiedNameMatcherInSameRound;
	private final Type type;
	private final List<Function<String, String>> implGenerator;
	private final List<Function<String, String>> dslGenerator;
	private final ProcessingEnvironment processingEnv;
	private final ProvideMatchersAnnotatedElementMirror containingElementMirror;
	private final boolean ignore;
	private final Element fieldElement;
	private final String generic;
	private final String defaultReturnMethod;

	public static final String computeGenericInformation(TypeMirror fieldTypeMirror) {
		if (fieldTypeMirror instanceof DeclaredType) {
			DeclaredType dt = ((DeclaredType) fieldTypeMirror);
			return dt.getTypeArguments().stream().map(Object::toString).collect(Collectors.joining(","));
		}
		return "";
	}

	public static final String computeFullyQualifiedNameMatcherInSameRound(ProcessingEnvironment processingEnv,
			boolean isInSameRound, String fieldType) {
		if (isInSameRound) {
			TypeElement typeElement = processingEnv.getElementUtils().getTypeElement(fieldType);
			if (typeElement != null) {
				String simpleName = typeElement.getSimpleName().toString() + "Matchers";
				String packageName = processingEnv.getElementUtils().getPackageOf(typeElement).getQualifiedName()
						.toString();
				String fullyQualifiedNameMatcher = typeElement.getQualifiedName().toString() + "Matchers";
				ProvideMatchers pm = typeElement.getAnnotation(ProvideMatchers.class);
				if (!"".equals(pm.matchersClassName())) {
					fullyQualifiedNameMatcher = fullyQualifiedNameMatcher.replaceAll(simpleName + "$",
							pm.matchersClassName());
					simpleName = pm.matchersClassName();
				}
				if (!"".equals(pm.matchersPackageName())) {
					fullyQualifiedNameMatcher = fullyQualifiedNameMatcher.replaceAll("^" + packageName,
							pm.matchersPackageName());
					packageName = pm.matchersPackageName();
				}
				return fullyQualifiedNameMatcher;
			}
		}
		return null;
	}

	public FieldDescription(ProvideMatchersAnnotatedElementMirror containingElementMirror, String fieldAccessor,
			String fieldName, String methodFieldName, String fieldType, Type type, boolean isInSameRound,
			ProcessingEnvironment processingEnv, boolean ignore, Element fieldElement, TypeMirror fieldTypeMirror) {
		this.containingElementMirror = containingElementMirror;
		this.fieldAccessor = fieldAccessor;
		this.fieldName = fieldName;
		this.methodFieldName = methodFieldName;
		this.fieldType = fieldType;
		this.type = type;
		this.processingEnv = processingEnv;
		this.ignore = ignore;
		this.fieldElement = fieldElement;
		this.defaultReturnMethod = containingElementMirror.getDefaultReturnMethod();
		this.generic = computeGenericInformation(fieldTypeMirror);
		this.fullyQualifiedNameMatcherInSameRound = computeFullyQualifiedNameMatcherInSameRound(processingEnv,
				isInSameRound, fieldType);
		List<Function<String, String>> tmp1 = new ArrayList<>();
		List<Function<String, String>> tmp2 = new ArrayList<>();
		tmp1.add(this::getImplementationForDefault);
		tmp2.add(this::getDslForDefault);
		if (fullyQualifiedNameMatcherInSameRound != null
				&& processingEnv.getElementUtils().getTypeElement(fieldType).getTypeParameters().isEmpty()) {
			tmp1.add(this::getImplementationForDefaultChaining);
			tmp2.add(this::getDslForDefaultChaining);
		}
		switch (type) {
		case ARRAY:
			tmp2.add(this::getDslForArray);
			break;
		case OPTIONAL:
			tmp1.add(this::getImplementationForOptional);
			tmp2.add(this::getDslForOptional);
			break;
		case COMPARABLE:
			tmp2.add(this::getDslForComparable);
			break;
		case STRING:
			tmp2.add(this::getDslForComparable);
			tmp2.add(this::getDslForString);
			break;
		case COLLECTION:
		case LIST:
		case SET:
			tmp2.add(this::getDslForIterable);
			tmp2.add(this::getDslForCollection);
			break;
		case SUPPLIER:
			tmp2.add(this::getDslForSupplier);
			break;
		default:
			// Nothing
		}
		AddToMatcher addToMatchers[] = fieldElement.getAnnotationsByType(AddToMatcher.class);
		tmp1.addAll(Arrays.stream(addToMatchers).map(this::generateFunctionForImplementation).filter(t -> t != null)
				.collect(Collectors.toList()));
		tmp2.addAll(Arrays.stream(addToMatchers).map(this::generateFunctionForDSL).filter(t -> t != null)
				.collect(Collectors.toList()));
		implGenerator = Collections.unmodifiableList(tmp1);
		dslGenerator = Collections.unmodifiableList(tmp2);
	}

	public Function<String, String> generateFunctionForDSL(AddToMatcher a) {
		return prefix -> buildDsl(prefix, getJavaDocFor(Optional.empty(), Optional.empty(), Optional.empty()),
				generateDeclaration(a.suffix(), a.argument()));
	}

	public Function<String, String> generateFunctionForImplementation(AddToMatcher a) {
		return prefix -> buildImplementation(prefix, generateDeclaration(a.suffix(), a.argument()),
				Arrays.stream(a.body()).map(l -> prefix + l).collect(Collectors.joining("\n")) + "\n" + prefix
						+ "return this;");
	}

	public String getJavaDocFor(Optional<String> addToDescription, Optional<String> param, Optional<String> see) {
		String linkToAccessor = "{@link "
				+ containingElementMirror.getFullyQualifiedNameOfClassAnnotatedWithProvideMatcher() + "#"
				+ getFieldAccessor() + " This field is accessed by using this approach}.";
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
		param.ifPresent(t -> sb.append(" * @param ").append(t).append(".").append("\n"));
		sb.append(" * @return the DSL to continue the construction of the matcher.").append("\n");
		see.ifPresent(t -> sb.append(" * @see ").append(t).append("\n"));
		sb.append(" */");
		return sb.toString();
	}

	public String buildImplementation(String prefix, String declaration, String body) {
		return new StringBuilder().append(prefix).append("@Override").append("\n").append(prefix).append("public ")
				.append(declaration).append(" {\n").append(prefix).append("  ")
				.append(body.replaceAll("\\R", "\n" + prefix + "  ")).append("\n").append(prefix).append("}")
				.append("\n").toString();
	}

	public String buildDsl(String prefix, String javadoc, String declaration) {
		return new StringBuilder().append(prefix).append(javadoc.replaceAll("\\R", "\n" + prefix)).append("\n")
				.append(prefix).append(declaration).append(";\n").toString();
	}

	public String buildDefaultDsl(String prefix, String javadoc, String declaration, String innerMatcher) {
		return new StringBuilder().append(prefix).append(javadoc.replaceAll("\\R", "\n" + prefix)).append("\n")
				.append(prefix).append("default ").append(declaration).append("{\n").append(prefix).append(prefix)
				.append("return ").append(fieldName).append("(").append(innerMatcher).append(");\n").append(prefix)
				.append("}").toString();
	}

	public String generateDeclaration(String postFix, String arguments) {
		return new StringBuilder().append(defaultReturnMethod).append(" ").append(fieldName).append(postFix).append("(")
				.append(arguments).append(")").toString();
	}

	public String getImplementationForDefault(String prefix) {
		StringBuilder sb = new StringBuilder();
		sb.append(buildImplementation(prefix,
				generateDeclaration("", "org.hamcrest.Matcher<? super " + fieldType + "> matcher"),
				fieldName + "= new " + methodFieldName + "Matcher(matcher);\nreturn this;"));

		return sb.toString();
	}

	public String getImplementationForDefaultChaining(String prefix) {
		// Can't use buildDeclaration here
		TypeElement targetElement = processingEnv.getElementUtils().getTypeElement(fieldType);
		String name = targetElement.getSimpleName().toString();
		String lname = name.substring(0, 1).toLowerCase() + name.substring(1);
		return buildImplementation(prefix,
				fullyQualifiedNameMatcherInSameRound + "." + name + "Matcher" + "<" + defaultReturnMethod + "> "
						+ fieldName + "With()",
				fullyQualifiedNameMatcherInSameRound + "." + name + "Matcher tmp = "
						+ fullyQualifiedNameMatcherInSameRound + "." + lname + "WithParent(this);\n" + fieldName
						+ "(tmp);\nreturn tmp;");
	}

	public String getImplementationForOptional(String prefix) {
		StringBuilder sb = new StringBuilder();
		sb.append(buildImplementation(prefix, generateDeclaration("IsPresent", ""),
				fieldName + " = " + methodFieldName + "Matcher.isPresent();\nreturn this;"));

		sb.append(buildImplementation(prefix, generateDeclaration("IsNotPresent", ""),
				fieldName + " = " + methodFieldName + "Matcher.isNotPresent();\nreturn this;"));

		return sb.toString();
	}

	public String getImplementationInterface(String prefix) {
		return implGenerator.stream().map(g -> g.apply(prefix)).collect(Collectors.joining("\n"));
	}

	public String getDslForSupplier(String prefix) {
		return buildDefaultDsl(prefix,
				getJavaDocFor(
						Optional.of(
								" Validate that the result of the supplier is accepted by another matcher (the result of the execution must be stable)"),
						Optional.of("matcherOnResult a Matcher on result of the supplier execution"), Optional.empty()),
				generateDeclaration("SupplierResult", "org.hamcrest.Matcher<? super " + generic + "> matcherOnResult"),
				"new " + methodFieldName + "MatcherSupplier(matcherOnResult)");
	}

	public String getDslForDefault(String prefix) {
		StringBuilder sb = new StringBuilder();
		sb.append(buildDsl(prefix,
				getJavaDocFor(Optional.empty(), Optional.of("matcher a Matcher on the field"),
						Optional.of(SEE_TEXT_FOR_HAMCREST_MATCHER)),
				generateDeclaration("", "org.hamcrest.Matcher<? super " + fieldType + "> matcher")));

		sb.append(buildDefaultDsl(prefix,
				getJavaDocFor(Optional.empty(),
						Optional.of(
								"value an expected value for the field, which will be compared using the is matcher"),
						Optional.of(SEE_TEXT_FOR_IS_MATCHER)),
				generateDeclaration("", fieldType + " value"), "org.hamcrest.Matchers.is(value)"));

		return sb.toString();
	}

	public String getDslForDefaultChaining(String prefix) {
		// can'ut use generateDeclaration here
		TypeElement targetElement = processingEnv.getElementUtils().getTypeElement(fieldType);
		String name = targetElement.getSimpleName().toString();
		return buildDsl(prefix,
				getJavaDocFor(Optional.of("by starting a matcher for this field"), Optional.empty(), Optional.empty()),
				fullyQualifiedNameMatcherInSameRound + "." + name + "Matcher" + "<" + defaultReturnMethod + "> "
						+ fieldName + "With()");
	}

	public String getDslForString(String prefix) {

		StringBuilder sb = new StringBuilder();

		sb.append(buildDefaultDsl(prefix,
				getJavaDocFor(Optional.of("that the string contains another one"),
						Optional.of("other the string is contains in the other one"),
						Optional.of("org.hamcrest.Matchers#containsString(java.lang.String)")),
				generateDeclaration("ContainsString", "String other"), "org.hamcrest.Matchers.containsString(other)"));

		sb.append(buildDefaultDsl(prefix,
				getJavaDocFor(Optional.of("that the string starts with another one"),
						Optional.of("other the string to use to compare"),
						Optional.of("org.hamcrest.Matchers#startsWith(java.lang.String)")),
				generateDeclaration("StartsWith", "String other"), "org.hamcrest.Matchers.startsWith(other)"));

		sb.append(buildDefaultDsl(prefix,
				getJavaDocFor(Optional.of("that the string ends with another one"),
						Optional.of("other the string to use to compare"),
						Optional.of("org.hamcrest.Matchers#endsWith(java.lang.String)")),
				generateDeclaration("EndsWith", "String other"), "org.hamcrest.Matchers.endsWith(other)"));

		return sb.toString();
	}

	public String getDslForIterable(String prefix) {
		StringBuilder sb = new StringBuilder();
		sb.append(buildDefaultDsl(prefix,
				getJavaDocFor(Optional.of("that the iterable is empty"), Optional.empty(), Optional.empty()),
				generateDeclaration("IsEmptyIterable", ""),
				"(org.hamcrest.Matcher)org.hamcrest.Matchers.emptyIterable()"));

		if (!"".equals(generic)) {
			sb.append(buildDefaultDsl(prefix,
					getJavaDocFor(Optional.of("that the iterable contains the received elements"),
							Optional.of("elements the elements"),
							Optional.of("org.hamcrest.Matchers#contains(java.lang.Object[])")),
					generateDeclaration("Contains", generic + "... elements"),
					"org.hamcrest.Matchers.contains(elements)"));

			sb.append(buildDefaultDsl(prefix,
					getJavaDocFor(Optional.of("that the iterable contains the received elements, using matchers"),
							Optional.of("matchersOnElements the matchers on the elements"),
							Optional.of("org.hamcrest.Matchers#contains(org.hamcrest.Matcher[])")),
					generateDeclaration("Contains", "org.hamcrest.Matcher<" + generic + ">... matchersOnElements"),
					"org.hamcrest.Matchers.contains(matchersOnElements)"));

			sb.append(buildDefaultDsl(prefix,
					getJavaDocFor(Optional.of("that the iterable contains the received elements in any order"),
							Optional.of("elements the elements"),
							Optional.of("org.hamcrest.Matchers#containsInAnyOrder(java.lang.Object[])")),
					generateDeclaration("ContainsInAnyOrder", generic + "... elements"),
					"org.hamcrest.Matchers.containsInAnyOrder(elements)"));

			sb.append(buildDefaultDsl(prefix,
					getJavaDocFor(
							Optional.of(
									"that the iterable contains the received elements, using matchers in any order"),
							Optional.of("matchersOnElements the matchers on the elements"),
							Optional.of("org.hamcrest.Matchers#containsInAnyOrder(org.hamcrest.Matcher[])")),
					generateDeclaration("ContainsInAnyOrder",
							"org.hamcrest.Matcher<" + generic + ">... matchersOnElements"),
					"org.hamcrest.Matchers.containsInAnyOrder(matchersOnElements)"));

			sb.append(
					buildDefaultDsl(prefix,
							getJavaDocFor(
									Optional.of(
											"that the iterable contains the received elements, using list of matcher"),
									Optional.of("matchersOnElements the matchers on the elements"),
									Optional.of("org.hamcrest.Matchers#contains(java.util.List)")),
							generateDeclaration("Contains",
									"java.util.List<org.hamcrest.Matcher<? super " + generic + ">> matchersOnElements"),
					"org.hamcrest.Matchers.contains(matchersOnElements)"));
		}

		return sb.toString();
	}

	public String getDslForArray(String prefix) {
		return buildDefaultDsl(prefix,
				getJavaDocFor(Optional.of("that the array is empty"), Optional.empty(), Optional.empty()),
				generateDeclaration("IsEmpty", ""), "(org.hamcrest.Matcher)org.hamcrest.Matchers.emptyArray()");
	}

	public String getDslForCollection(String prefix) {
		return buildDefaultDsl(prefix,
				getJavaDocFor(Optional.of("that the collection is empty"), Optional.empty(), Optional.empty()),
				generateDeclaration("IsEmpty", ""), "(org.hamcrest.Matcher)org.hamcrest.Matchers.empty()");
	}

	public String getDslForOptional(String prefix) {
		StringBuilder sb = new StringBuilder();

		sb.append(buildDsl(prefix,
				getJavaDocFor(Optional.of("with a present optional"), Optional.empty(), Optional.empty()),
				generateDeclaration("IsPresent", "")));

		sb.append(buildDsl(prefix,
				getJavaDocFor(Optional.of("with a not present optional"), Optional.empty(), Optional.empty()),
				generateDeclaration("IsNotPresent", "")));

		return sb.toString();
	}

	public String getDslForComparable(String prefix) {
		StringBuilder sb = new StringBuilder();
		sb.append(buildDefaultDsl(prefix,
				getJavaDocFor(Optional.of("that this field is equals to another one, using the compareTo method"),
						Optional.of("value the value to compare with"),
						Optional.of("org.hamcrest.Matchers#comparesEqualTo(java.lang.Comparable)")),
				generateDeclaration("ComparesEqualTo", fieldType + " value"),
				"org.hamcrest.Matchers.comparesEqualTo(value)"));

		sb.append(buildDefaultDsl(prefix,
				getJavaDocFor(Optional.of("that this field is less than another value"),
						Optional.of("value the value to compare with"),
						Optional.of("org.hamcrest.Matchers#lessThan(java.lang.Comparable)")),
				generateDeclaration("LessThan", fieldType + " value"), "org.hamcrest.Matchers.lessThan(value)"));

		sb.append(buildDefaultDsl(prefix,
				getJavaDocFor(Optional.of("that this field is less or equal than another value"),
						Optional.of("value the value to compare with"),
						Optional.of("org.hamcrest.Matchers#lessThanOrEqualTo(java.lang.Comparable)")),
				generateDeclaration("LessThanOrEqualTo", fieldType + " value"),
				"org.hamcrest.Matchers.lessThanOrEqualTo(value)"));

		sb.append(buildDefaultDsl(prefix,
				getJavaDocFor(Optional.of("that this field is greater than another value"),
						Optional.of("value the value to compare with"),
						Optional.of("org.hamcrest.Matchers#greaterThan(java.lang.Comparable)")),
				generateDeclaration("GreaterThan", fieldType + " value"), "org.hamcrest.Matchers.greaterThan(value)"));

		sb.append(buildDefaultDsl(prefix,
				getJavaDocFor(Optional.of("that this field is greater or equal than another value"),
						Optional.of("value the value to compare with"),
						Optional.of("org.hamcrest.Matchers#greaterThanOrEqualTo(java.lang.Comparable)")),
				generateDeclaration("GreaterThanOrEqualTo", fieldType + " value"),
				"org.hamcrest.Matchers.greaterThanOrEqualTo(value)"));

		return sb.toString();
	}

	public String getDslInterface(String prefix) {
		return dslGenerator.stream().map(g -> g.apply(prefix)).collect(Collectors.joining("\n"));
	}

	public String getMatcherForField(String prefix) {
		StringBuilder sb = new StringBuilder();
		sb.append(prefix)
				.append("private static class " + methodFieldName + "Matcher" + containingElementMirror.getFullGeneric()
						+ " extends org.hamcrest.FeatureMatcher<"
						+ containingElementMirror.getFullyQualifiedNameOfClassAnnotatedWithProvideMatcher()
						+ containingElementMirror.getGeneric() + "," + fieldType + "> {")
				.append("\n");
		sb.append(prefix).append(
				"  public " + methodFieldName + "Matcher(org.hamcrest.Matcher<? super " + fieldType + "> matcher) {")
				.append("\n");
		sb.append(prefix).append("    super(matcher,\"" + fieldName + "\",\"" + fieldName + "\");").append("\n");
		sb.append(prefix).append("  }").append("\n");
		switch (type) {
		case OPTIONAL:
			sb.append(prefix).append("  public static " + methodFieldName + "Matcher isPresent() {").append("\n");
			sb.append(prefix).append("    return new " + methodFieldName
					+ "Matcher(new org.hamcrest.CustomTypeSafeMatcher<" + fieldType + ">(\"optional is present\"){")
					.append("\n");
			sb.append(prefix).append("      public boolean matchesSafely(" + fieldType + " o) {return o.isPresent();}")
					.append("\n");
			sb.append(prefix).append("    });").append("\n");
			sb.append(prefix).append("  }").append("\n");
			sb.append(prefix).append("  public static " + methodFieldName + "Matcher isNotPresent() {").append("\n");
			sb.append(prefix).append("    return new " + methodFieldName
					+ "Matcher(new org.hamcrest.CustomTypeSafeMatcher<" + fieldType + ">(\"optional is not present\"){")
					.append("\n");
			sb.append(prefix).append("      public boolean matchesSafely(" + fieldType + " o) {return !o.isPresent();}")
					.append("\n");
			sb.append(prefix).append("    });").append("\n");
			sb.append(prefix).append("  }").append("\n");
			break;
		default:
			// Nothing
		}
		sb.append(prefix)
				.append("  protected " + fieldType + " featureValueOf("
						+ containingElementMirror.getFullyQualifiedNameOfClassAnnotatedWithProvideMatcher()
						+ containingElementMirror.getGeneric() + " actual) {")
				.append("\n");
		sb.append(prefix).append("    return actual." + fieldAccessor + ";").append("\n");
		sb.append(prefix).append("  }").append("\n");
		sb.append(prefix).append("}").append("\n");
		switch (type) {
		case SUPPLIER:
			sb.append(prefix)
					.append("private static class " + methodFieldName + "MatcherSupplier"
							+ containingElementMirror.getFullGeneric()
							+ " extends org.hamcrest.FeatureMatcher<java.util.function.Supplier<" + generic + ">,"
							+ generic + "> {")
					.append("\n");
			sb.append(prefix).append("  public " + methodFieldName + "MatcherSupplier(org.hamcrest.Matcher<? super "
					+ generic + "> matcher) {").append("\n");
			sb.append(prefix).append("    super(matcher,\"with supplier result\",\"with supplier result\");")
					.append("\n");
			sb.append(prefix).append("  }").append("\n");
			sb.append(prefix).append(
					"  protected " + generic + " featureValueOf(java.util.function.Supplier<" + generic + "> actual) {")
					.append("\n");
			sb.append(prefix).append("    return actual.get();").append("\n");
			sb.append(prefix).append("  }").append("\n");
			sb.append(prefix).append("}").append("\n");
			break;
		default:
			// NOTHING
		}
		return sb.toString();
	}

	public String getFieldCopyDefault(String lhs, String rhs) {
		return lhs + "." + fieldName + "(org.hamcrest.Matchers.is(" + rhs + "." + fieldAccessor + "))";
	}

	public String getSameValueMatcherFor(String target, TypeElement targetElement) {
		String name = targetElement.getSimpleName().toString();
		String lname = name.substring(0, 1).toLowerCase() + name.substring(1);
		return fullyQualifiedNameMatcherInSameRound + "." + lname + "WithSameValue(" + target + ")";
	}

	public String getFieldCopySameRound(String lhs, String rhs, TypeElement targetElement) {
		return lhs + "." + fieldName + "(" + rhs + "." + fieldAccessor + "==null?org.hamcrest.Matchers.nullValue():"
				+ getSameValueMatcherFor(rhs + "." + fieldAccessor, targetElement) + ")";
	}

	public String generateMatcherBuilderReferenceFor(String generic) {
		ProvideMatchersAnnotatedElementMirror target = containingElementMirror.findMirrorFor(generic);
		if (target != null) {
			return target.getFullyQualifiedNameOfGeneratedClass() + "::" + target.getMethodShortClassName()
					+ "WithSameValue";
		}

		return "org.hamcrest.Matchers::is";
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

		if (fullyQualifiedNameMatcherInSameRound != null
				&& processingEnv.getElementUtils().getTypeElement(fieldType).getTypeParameters().isEmpty()) {
			return getFieldCopySameRound(lhs, rhs, processingEnv.getElementUtils().getTypeElement(fieldType));
		}
		return getFieldCopyDefault(lhs, rhs);
	}

	public String asMatchesSafely(String prefix) {
		return new StringBuilder().append(prefix).append("if(!").append(fieldName).append(".matches(actual)) {\n")
				.append(prefix).append("  mismatchDescription.appendText(\"[\"); ").append(fieldName)
				.append(".describeMismatch(actual,mismatchDescription); mismatchDescription.appendText(\"]\\n\");\n")
				.append(prefix).append("  result=false;\n").append(prefix).append("}").toString();
	}

	public String asDescribeTo(String prefix) {
		return new StringBuilder().append(prefix)
				.append("description.appendText(\"[\").appendDescriptionOf(" + fieldName + ").appendText(\"]\\n\");")
				.toString();
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
