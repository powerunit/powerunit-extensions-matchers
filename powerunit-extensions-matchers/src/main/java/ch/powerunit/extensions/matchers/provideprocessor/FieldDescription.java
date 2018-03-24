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
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;

import ch.powerunit.extensions.matchers.IgnoreInMatcher;
import ch.powerunit.extensions.matchers.ProvideMatchers;
import ch.powerunit.extensions.matchers.provideprocessor.xml.GeneratedMatcherField;

public class FieldDescription {

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
	private final Elements elementsUtils;
	private final ProvideMatchersAnnotatedElementMirror containingElementMirror;
	private final boolean ignore;
	private final Element fieldElement;
	private final TypeMirror fieldTypeMirror;
	private final String generic;

	public FieldDescription(ProvideMatchersAnnotatedElementMirror containingElementMirror, String fieldAccessor,
			String fieldName, String methodFieldName, String fieldType, Type type, boolean isInSameRound,
			Elements elementsUtils, boolean ignore, Element fieldElement, TypeMirror fieldTypeMirror) {
		this.containingElementMirror = containingElementMirror;
		this.fieldAccessor = fieldAccessor;
		this.fieldName = fieldName;
		this.methodFieldName = methodFieldName;
		this.fieldType = fieldType;
		this.type = type;
		this.elementsUtils = elementsUtils;
		this.ignore = ignore;
		this.fieldElement = fieldElement;
		this.fieldTypeMirror = fieldTypeMirror;
		if (fieldTypeMirror instanceof DeclaredType) {
			DeclaredType dt = ((DeclaredType) fieldTypeMirror);
			this.generic = dt.getTypeArguments().stream().map(Object::toString).collect(Collectors.joining(","));
		} else {
			this.generic = "";
		}
		if (isInSameRound) {
			TypeElement typeElement = elementsUtils.getTypeElement(fieldType);
			if (typeElement != null) {
				String simpleName = typeElement.getSimpleName().toString() + "Matchers";
				String packageName = elementsUtils.getPackageOf(typeElement).getQualifiedName().toString();
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
				this.fullyQualifiedNameMatcherInSameRound = fullyQualifiedNameMatcher;
			} else {
				this.fullyQualifiedNameMatcherInSameRound = null;
			}
		} else {
			this.fullyQualifiedNameMatcherInSameRound = null;
		}
		List<Function<String, String>> tmp1 = new ArrayList<>();
		List<Function<String, String>> tmp2 = new ArrayList<>();
		tmp1.add(this::getImplementationForDefault);
		tmp2.add(this::getDslForDefault);
		if (fullyQualifiedNameMatcherInSameRound != null
				&& elementsUtils.getTypeElement(fieldType).getTypeParameters().isEmpty()) {
			tmp1.add(this::getImplementationForDefaultChaining);
			tmp2.add(this::getDslForDefaultChaining);
		}
		switch (type) {
		case ARRAY:
			tmp1.add(this::getImplementationForArray);
			tmp2.add(this::getDslForArray);
			break;
		case OPTIONAL:
			tmp1.add(this::getImplementationForOptional);
			tmp2.add(this::getDslForOptional);
			break;
		case COMPARABLE:
			tmp1.add(this::getImplementationForComparable);
			tmp2.add(this::getDslForComparable);
			break;
		case STRING:
			tmp1.add(this::getImplementationForComparable);
			tmp2.add(this::getDslForComparable);
			tmp1.add(this::getImplementationForString);
			tmp2.add(this::getDslForString);
			break;
		case COLLECTION:
			tmp1.add(this::getImplementationForIterable);
			tmp2.add(this::getDslForIterable);
			tmp1.add(this::getImplementationForCollection);
			tmp2.add(this::getDslForCollection);
			break;
		case LIST:
			tmp1.add(this::getImplementationForIterable);
			tmp2.add(this::getDslForIterable);
			tmp1.add(this::getImplementationForCollection);
			tmp2.add(this::getDslForCollection);
			break;
		case SET:
			tmp1.add(this::getImplementationForIterable);
			tmp2.add(this::getDslForIterable);
			tmp1.add(this::getImplementationForCollection);
			tmp2.add(this::getDslForCollection);
			break;
		case SUPPLIER:
			tmp1.add(this::getImplementationForSupplier);
			tmp2.add(this::getDslForSupplier);
		default:
			// Nothing
		}
		implGenerator = Collections.unmodifiableList(tmp1);
		dslGenerator = Collections.unmodifiableList(tmp2);
	}

	private String getImplementationForSupplier(String prefix) {
		StringBuilder sb = new StringBuilder();
		sb.append(prefix).append("@Override").append("\n");
		sb.append(prefix)
				.append("public " + containingElementMirror.getDefaultReturnMethod() + " " + fieldName
						+ "SupplierResult(org.hamcrest.Matcher<? super " + generic + "> matcherOnResult) {")
				.append("\n");
		sb.append(prefix)
				.append("  return " + fieldName + "(new " + methodFieldName + "MatcherSupplier(matcherOnResult));")
				.append("\n");
		sb.append(prefix).append("}").append("\n");

		return sb.toString();
	}

	private String getImplementationForDefault(String prefix) {
		StringBuilder sb = new StringBuilder();
		sb.append(prefix).append("@Override").append("\n");
		sb.append(prefix).append("public " + containingElementMirror.getDefaultReturnMethod() + " " + fieldName
				+ "(org.hamcrest.Matcher<? super " + fieldType + "> matcher) {").append("\n");
		sb.append(prefix).append("  " + fieldName + "= new " + methodFieldName + "Matcher(matcher);").append("\n");
		sb.append(prefix).append("  return this;").append("\n");
		sb.append(prefix).append("}").append("\n");

		sb.append(prefix).append("@Override").append("\n");
		sb.append(prefix).append("public " + containingElementMirror.getDefaultReturnMethod() + " " + fieldName + "("
				+ fieldType + " value) {").append("\n");
		sb.append(prefix).append("  return " + fieldName + "(org.hamcrest.Matchers.is(value));").append("\n");
		sb.append(prefix).append("}").append("\n");

		sb.append(prefix).append("\n");

		return sb.toString();
	}

	private String getImplementationForDefaultChaining(String prefix) {
		StringBuilder sb = new StringBuilder();
		TypeElement targetElement = elementsUtils.getTypeElement(fieldType);
		String name = targetElement.getSimpleName().toString();
		String lname = name.substring(0, 1).toLowerCase() + name.substring(1);
		sb.append(prefix).append("@Override").append("\n");
		sb.append(prefix)
				.append("public " + fullyQualifiedNameMatcherInSameRound + "." + name + "Matcher" + "<"
						+ containingElementMirror.getDefaultReturnMethod() + "> " + fieldName + "With() {")
				.append("\n");
		sb.append(prefix).append("  " + fullyQualifiedNameMatcherInSameRound + "." + name + "Matcher tmp = "
				+ fullyQualifiedNameMatcherInSameRound + "." + lname + "WithParent(this);").append("\n");
		sb.append(prefix).append("  " + fieldName + "(tmp);").append("\n");
		sb.append(prefix).append("  return tmp;").append("\n");
		sb.append(prefix).append("}").append("\n");
		sb.append(prefix).append("\n");

		return sb.toString();
	}

	private String getImplementationForString(String prefix) {
		StringBuilder sb = new StringBuilder();
		sb.append(prefix).append("@Override").append("\n");
		sb.append(prefix).append("public " + containingElementMirror.getDefaultReturnMethod() + " " + fieldName
				+ "ContainsString(String other) {").append("\n");
		sb.append(prefix).append("  return " + fieldName + "(org.hamcrest.Matchers.containsString(other));")
				.append("\n");
		sb.append(prefix).append("}").append("\n");

		sb.append(prefix).append("@Override").append("\n");
		sb.append(prefix).append("public " + containingElementMirror.getDefaultReturnMethod() + " " + fieldName
				+ "StartsWith(String other) {").append("\n");
		sb.append(prefix).append("  return " + fieldName + "(org.hamcrest.Matchers.startsWith(other));").append("\n");
		sb.append(prefix).append("}").append("\n");

		sb.append(prefix).append("@Override").append("\n");
		sb.append(prefix).append("public " + containingElementMirror.getDefaultReturnMethod() + " " + fieldName
				+ "EndsWith(String other) {").append("\n");
		sb.append(prefix).append("  return " + fieldName + "(org.hamcrest.Matchers.endsWith(other));").append("\n");
		sb.append(prefix).append("}").append("\n");

		return sb.toString();
	}

	private String getImplementationForIterable(String prefix) {
		StringBuilder sb = new StringBuilder();
		sb.append(prefix).append("@Override").append("\n");
		sb.append(prefix).append(
				"public " + containingElementMirror.getDefaultReturnMethod() + " " + fieldName + "IsEmptyIterable() {")
				.append("\n");
		sb.append(prefix)
				.append("  return " + fieldName + "((org.hamcrest.Matcher)org.hamcrest.Matchers.emptyIterable());")
				.append("\n");
		sb.append(prefix).append("}").append("\n");
		return sb.toString();
	}

	private String getImplementationForArray(String prefix) {
		StringBuilder sb = new StringBuilder();
		sb.append(prefix).append("@Override").append("\n");
		sb.append(prefix)
				.append("public " + containingElementMirror.getDefaultReturnMethod() + " " + fieldName + "IsEmpty() {")
				.append("\n");
		sb.append(prefix)
				.append("  return " + fieldName + "((org.hamcrest.Matcher)org.hamcrest.Matchers.emptyArray());")
				.append("\n");
		sb.append(prefix).append("}").append("\n");
		return sb.toString();
	}

	private String getImplementationForCollection(String prefix) {
		StringBuilder sb = new StringBuilder();
		sb.append(prefix).append("@Override").append("\n");
		sb.append(prefix)
				.append("public " + containingElementMirror.getDefaultReturnMethod() + " " + fieldName + "IsEmpty() {")
				.append("\n");
		sb.append(prefix).append("  return " + fieldName + "((org.hamcrest.Matcher)org.hamcrest.Matchers.empty());")
				.append("\n");
		sb.append(prefix).append("}").append("\n");
		return sb.toString();
	}

	private String getImplementationForOptional(String prefix) {
		StringBuilder sb = new StringBuilder();
		sb.append(prefix).append("@Override").append("\n");
		sb.append(prefix).append(
				"public " + containingElementMirror.getDefaultReturnMethod() + " " + fieldName + "IsPresent() {")
				.append("\n");
		sb.append(prefix).append("  " + fieldName + " = " + methodFieldName + "Matcher.isPresent();").append("\n");
		sb.append(prefix).append("  return this;").append("\n");
		sb.append(prefix).append("}").append("\n");

		sb.append(prefix).append("@Override").append("\n");
		sb.append(prefix).append(
				"public " + containingElementMirror.getDefaultReturnMethod() + " " + fieldName + "IsNotPresent() {")
				.append("\n");
		sb.append(prefix).append("  " + fieldName + " = " + methodFieldName + "Matcher.isNotPresent();").append("\n");
		sb.append(prefix).append("  return this;").append("\n");
		sb.append(prefix).append("}").append("\n");
		return sb.toString();
	}

	private String getImplementationForComparable(String prefix) {
		StringBuilder sb = new StringBuilder();
		sb.append(prefix).append("@Override").append("\n");
		sb.append(prefix).append("public " + containingElementMirror.getDefaultReturnMethod() + " " + fieldName
				+ "ComparesEqualTo(" + fieldType + " value) {").append("\n");
		sb.append(prefix).append("  return " + fieldName + "(org.hamcrest.Matchers.comparesEqualTo(value));")
				.append("\n");
		sb.append(prefix).append("}").append("\n");
		sb.append(prefix).append("@Override").append("\n");
		sb.append(prefix).append("public " + containingElementMirror.getDefaultReturnMethod() + " " + fieldName
				+ "LessThan(" + fieldType + " value) {").append("\n");
		sb.append(prefix).append("  return " + fieldName + "(org.hamcrest.Matchers.lessThan(value));").append("\n");
		sb.append(prefix).append("}").append("\n");
		sb.append(prefix).append("@Override").append("\n");
		sb.append(prefix).append("public " + containingElementMirror.getDefaultReturnMethod() + " " + fieldName
				+ "LessThanOrEqualTo(" + fieldType + " value) {").append("\n");
		sb.append(prefix).append("  return " + fieldName + "(org.hamcrest.Matchers.lessThanOrEqualTo(value));")
				.append("\n");
		sb.append(prefix).append("}").append("\n");
		sb.append(prefix).append("@Override").append("\n");
		sb.append(prefix).append("public " + containingElementMirror.getDefaultReturnMethod() + " " + fieldName
				+ "GreaterThan(" + fieldType + " value) {").append("\n");
		sb.append(prefix).append("  return " + fieldName + "(org.hamcrest.Matchers.greaterThan(value));").append("\n");
		sb.append(prefix).append("}").append("\n");
		sb.append(prefix).append("@Override").append("\n");
		sb.append(prefix).append("public " + containingElementMirror.getDefaultReturnMethod() + " " + fieldName
				+ "GreaterThanOrEqualTo(" + fieldType + " value) {").append("\n");
		sb.append(prefix).append("  return " + fieldName + "(org.hamcrest.Matchers.greaterThanOrEqualTo(value));")
				.append("\n");
		sb.append(prefix).append("}").append("\n");
		return sb.toString();
	}

	public String getImplementationInterface(String prefix) {
		return implGenerator.stream().map(g -> g.apply(prefix)).collect(Collectors.joining("\n"));
	}

	private String getJavaDocFor(String prefix, Optional<String> addToDescription, Optional<String> param,
			Optional<String> see) {
		String linkToAccessor = "{@link "
				+ containingElementMirror.getFullyQualifiedNameOfClassAnnotatedWithProvideMatcher() + "#"
				+ getFieldAccessor() + " This field is accessed by using this approach}.";
		StringBuilder sb = new StringBuilder();
		sb.append(prefix).append("/**").append("\n");
		sb.append(prefix).append(" * Add a validation on the field `").append(fieldName).append("`");
		addToDescription.ifPresent(t -> sb.append(" ").append(t));
		sb.append(".").append("\n");
		sb.append(prefix).append(" * <p>").append("\n");
		sb.append(prefix).append(" *").append("\n");
		sb.append(prefix).append(" * <i>").append(linkToAccessor).append("</i>").append("\n");
		sb.append(prefix).append(" * <p>").append("\n");
		sb.append(prefix)
				.append(" * <b>In case method specifing a matcher on a fields are used several times, only the last setted matcher will be used.</b> ")
				.append("\n");
		sb.append(prefix)
				.append(" * When several control must be done on a single field, hamcrest itself provides a way to combine several matchers (See for instance {@link org.hamcrest.Matchers#both(org.hamcrest.Matcher)}.")
				.append("\n");
		sb.append(prefix).append(" *").append("\n");
		param.ifPresent(t -> sb.append(prefix).append(" * @param ").append(t).append(".").append("\n"));
		sb.append(prefix).append(" * @return the DSL to continue the construction of the matcher.").append("\n");
		see.ifPresent(t -> sb.append(prefix).append(" * @see ").append(t).append("\n"));
		sb.append(prefix).append(" */").append("\n");
		return sb.toString();
	}

	public String getDslForSupplier(String prefix) {
		StringBuilder sb = new StringBuilder();
		sb.append(getJavaDocFor(prefix,
				Optional.of(" Validate that the result of the supplier is accepted by another matcher"),
				Optional.of("matcherOnResult a Matcher on result of the supplier execution"), Optional.empty()));
		sb.append(prefix)
				.append(containingElementMirror.getDefaultReturnMethod() + " " + fieldName
						+ "SupplierResult(org.hamcrest.Matcher<? super " + generic + "> matcherOnResult);")
				.append("\n");

		return sb.toString();
	}

	public String getDslForDefault(String prefix) {
		StringBuilder sb = new StringBuilder();
		sb.append(getJavaDocFor(prefix, Optional.empty(), Optional.of("matcher a Matcher on the field"),
				Optional.of("org.hamcrest.Matchers The main class from hamcrest that provides default matchers.")));
		sb.append(prefix).append(containingElementMirror.getDefaultReturnMethod()).append(fieldName)
				.append("(org.hamcrest.Matcher<? super " + fieldType + "> matcher);").append("\n");

		sb.append(getJavaDocFor(prefix, Optional.empty(),
				Optional.of("value an expected value for the field, which will be compared using the is matcher"),
				Optional.of("org.hamcrest.Matchers#is(java.lang.Object)")));
		sb.append(prefix).append(containingElementMirror.getDefaultReturnMethod()).append(fieldName)
				.append("(" + fieldType + " value);").append("\n");

		return sb.toString();
	}

	public String getDslForDefaultChaining(String prefix) {
		StringBuilder sb = new StringBuilder();
		TypeElement targetElement = elementsUtils.getTypeElement(fieldType);
		String name = targetElement.getSimpleName().toString();
		sb.append(getJavaDocFor(prefix, Optional.of("by starting a matcher for this field"), Optional.empty(),
				Optional.empty()));
		sb.append(prefix).append(fullyQualifiedNameMatcherInSameRound + "." + name + "Matcher" + "<"
				+ containingElementMirror.getDefaultReturnMethod() + "> " + fieldName + "With();").append("\n");

		return sb.toString();
	}

	private String getDslForString(String prefix) {
		StringBuilder sb = new StringBuilder();
		sb.append(getJavaDocFor(prefix, Optional.of("that the string contains another one"),
				Optional.of("other the string is contains in the other one"),
				Optional.of("org.hamcrest.Matchers#containsString(java.lang.String)")));
		sb.append(prefix).append(containingElementMirror.getDefaultReturnMethod()).append(fieldName)
				.append("ContainsString(String other);").append("\n");

		sb.append(getJavaDocFor(prefix, Optional.of("that the string starts with another one"),
				Optional.of("other the string to use to compare"),
				Optional.of("org.hamcrest.Matchers#startsWith(java.lang.String)")));
		sb.append(prefix).append(containingElementMirror.getDefaultReturnMethod()).append(fieldName)
				.append("StartsWith(String other);").append("\n");

		sb.append(getJavaDocFor(prefix, Optional.of("that the string ends with another one"),
				Optional.of("other the string to use to compare"),
				Optional.of("org.hamcrest.Matchers#endsWith(java.lang.String)")));
		sb.append(prefix).append(containingElementMirror.getDefaultReturnMethod()).append(fieldName)
				.append("EndsWith(String other);").append("\n");

		return sb.toString();
	}

	private String getDslForIterable(String prefix) {
		StringBuilder sb = new StringBuilder();
		sb.append(getJavaDocFor(prefix, Optional.of("that the iterable is empty"), Optional.empty(), Optional.empty()));
		sb.append(prefix).append(containingElementMirror.getDefaultReturnMethod()).append(fieldName)
				.append("IsEmptyIterable();").append("\n");

		return sb.toString();
	}

	private String getDslForArray(String prefix) {
		StringBuilder sb = new StringBuilder();
		sb.append(getJavaDocFor(prefix, Optional.of("that the array is empty"), Optional.empty(), Optional.empty()));
		sb.append(prefix).append(containingElementMirror.getDefaultReturnMethod()).append(fieldName)
				.append("IsEmpty();").append("\n");

		return sb.toString();
	}

	private String getDslForCollection(String prefix) {
		StringBuilder sb = new StringBuilder();
		sb.append(
				getJavaDocFor(prefix, Optional.of("that the collection is empty"), Optional.empty(), Optional.empty()));
		sb.append(prefix).append(containingElementMirror.getDefaultReturnMethod()).append(fieldName)
				.append("IsEmpty();").append("\n");

		return sb.toString();
	}

	private String getDslForOptional(String prefix) {
		StringBuilder sb = new StringBuilder();
		sb.append(getJavaDocFor(prefix, Optional.of("with a present optional"), Optional.empty(), Optional.empty()));
		sb.append(prefix).append(containingElementMirror.getDefaultReturnMethod()).append(fieldName)
				.append("IsPresent();").append("\n");

		sb.append(
				getJavaDocFor(prefix, Optional.of("with a not present optional"), Optional.empty(), Optional.empty()));
		sb.append(prefix).append(containingElementMirror.getDefaultReturnMethod()).append(fieldName)
				.append("IsNotPresent();").append("\n");

		return sb.toString();
	}

	private String getDslForComparable(String prefix) {
		StringBuilder sb = new StringBuilder();
		sb.append(getJavaDocFor(prefix,
				Optional.of("that this field is equals to another one, using the compareTo method"),
				Optional.of("value the value to compare with"),
				Optional.of("org.hamcrest.Matchers#comparesEqualTo(java.lang.Comparable)")));
		sb.append(prefix).append(containingElementMirror.getDefaultReturnMethod()).append(fieldName)
				.append("ComparesEqualTo(" + fieldType + " value);").append("\n");

		sb.append(getJavaDocFor(prefix, Optional.of("that this field is less than another value"),
				Optional.of("value the value to compare with"),
				Optional.of("org.hamcrest.Matchers#lessThan(java.lang.Comparable)")));
		sb.append(prefix).append(containingElementMirror.getDefaultReturnMethod()).append(fieldName)
				.append("LessThan(" + fieldType + " value);").append("\n");

		sb.append(getJavaDocFor(prefix, Optional.of("that this field is less or equal than another value"),
				Optional.of("value the value to compare with"),
				Optional.of("org.hamcrest.Matchers#lessThanOrEqualTo(java.lang.Comparable)")));
		sb.append(prefix).append(containingElementMirror.getDefaultReturnMethod()).append(fieldName)
				.append("LessThanOrEqualTo(" + fieldType + " value);").append("\n");

		sb.append(getJavaDocFor(prefix, Optional.of("that this field is greater than another value"),
				Optional.of("value the value to compare with"),
				Optional.of("org.hamcrest.Matchers#greaterThan(java.lang.Comparable)")));
		sb.append(prefix).append(containingElementMirror.getDefaultReturnMethod()).append(fieldName)
				.append("GreaterThan(" + fieldType + " value);").append("\n");

		sb.append(getJavaDocFor(prefix, Optional.of("that this field is greater or equal than another value"),
				Optional.of("value the value to compare with"),
				Optional.of("org.hamcrest.Matchers#greaterThanOrEqualTo(java.lang.Comparable)")));
		sb.append(prefix).append(containingElementMirror.getDefaultReturnMethod()).append(fieldName)
				.append("GreaterThanOrEqualTo(" + fieldType + " value);").append("\n");

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
		return gmf;
	}

}
