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

import static ch.powerunit.extensions.matchers.common.CommonUtils.addPrefix;
import static java.util.stream.Collectors.joining;

import java.util.Arrays;
import java.util.Optional;

import javax.lang.model.element.TypeElement;

import ch.powerunit.extensions.matchers.provideprocessor.fields.AbstractFieldDescription;

public abstract class ProvidesMatchersAnnotatedElementMatcherMirror
		extends ProvidesMatchersAnnotatedElementFieldMatcherMirror {

	private static final String PRIVATE_IMPLEMENTATION_END = "\n\n    @Override\n    public _PARENT end() {\n      return _parentBuilder;\n    }\n\n\n";

	private static final String NEXTMATCHERS_DESCRIBETO = "      for(org.hamcrest.Matcher nMatcher : nextMatchers) {\n        description.appendText(\"[object itself \").appendDescriptionOf(nMatcher).appendText(\"]\\n\");\n      }\n    }\n";

	private static final String PARENT_DESCRIBETO = "      description.appendText(\"[\").appendDescriptionOf(_parent).appendText(\"]\\n\");\n";

	private static final String PARENT_VALIDATION = "      if(!_parent.matches(actual)) {\n        mismatchDescription.appendText(\"[\"); _parent.describeMismatch(actual,mismatchDescription); mismatchDescription.appendText(\"]\\n\");\n        result=false;\n      }\n";

	private static final String NEXTMATCHERS_VALIDATION = "      for(org.hamcrest.Matcher nMatcher : nextMatchers) {\n        if(!nMatcher.matches(actual)) {\n          mismatchDescription.appendText(\"[object itself \"); nMatcher.describeMismatch(actual,mismatchDescription); mismatchDescription.appendText(\"]\\n\");\n        result=false;\n        }\n      }\n      return result;\n    }\n\n";

	private static final String JAVADOC_ANDWITHAS = "    /**\n     * Add a matcher on the object itself and not on a specific field, but convert the object before passing it to the matcher.\n     * <p>\n     * <i>This method, when used more than once, just add more matcher to the list.</i>\n     * @param converter the function to convert the object.\n     * @param otherMatcher the matcher on the converter object itself.\n     * @param <_TARGETOBJECT> the type of the target object\n     * @return the DSL to continue\n     */\n";

	private static final String JAVADOC_ANDWITH = "    /**\n     * Add a matcher on the object itself and not on a specific field.\n     * <p>\n     * <i>This method, when used more than once, just add more matcher to the list.</i>\n     * @param otherMatcher the matcher on the object itself.\n     * @return the DSL to continue\n     */\n";

	private final String dslInterfaceDescription;

	public ProvidesMatchersAnnotatedElementMatcherMirror(TypeElement typeElement, RoundMirror roundMirror) {
		super(typeElement, roundMirror);
		this.dslInterfaceDescription = "DSL interface for matcher on " + getDefaultLinkForAnnotatedClass();
	}

	public String generatePublicInterface() {
		return new StringBuilder().append(generateMainBuildPublicInterface())
				.append(generateMainParentPublicInterface()).append(generateExposedPublicInterface()).toString();

	}

	private String generateExposedPublicInterface() {
		String simpleName = simpleNameOfGeneratedInterfaceMatcher;
		return new StringBuilder(addPrefix("  ",
				generateDefaultJavaDoc(Optional.empty(), Optional.empty(), Optional.empty(), true))).append("\n")
						.append("  public static interface ").append(simpleName).append(getFullGenericParent())
						.append(" extends org.hamcrest.Matcher<")
						.append(getFullyQualifiedNameOfClassAnnotatedWithProvideMatcherWithGeneric()).append(">,")
						.append(simpleName).append("BuildSyntaxicSugar ").append(generic).append(",").append(simpleName)
						.append("EndSyntaxicSugar ").append(getGenericParent()).append(" {\n")
						.append(fields.stream().map(AbstractFieldDescription::getDslInterface)
								.map(s -> addPrefix("    ", s)).collect(joining("\n")))
						.append("\n\n").append(generateAsPublicInterface()).append("  }\n").toString();
	}

	private String generateAsPublicInterface() {
		String fully = getFullyQualifiedNameOfClassAnnotatedWithProvideMatcherWithGeneric();
		String otherMatcher = "org.hamcrest.Matcher<? super " + fully + "> otherMatcher";
		String interfaceWithGeneric = getSimpleNameOfGeneratedInterfaceMatcherWithGenericParent();

		StringBuilder sb = new StringBuilder(JAVADOC_ANDWITH).append("    ").append(interfaceWithGeneric)
				.append(" andWith(").append(otherMatcher).append(");\n\n");

		sb.append(JAVADOC_ANDWITHAS).append(String.format(
				"    default <_TARGETOBJECT> %1$s andWithAs(java.util.function.Function<%2$s,_TARGETOBJECT> converter,org.hamcrest.Matcher<? super _TARGETOBJECT> otherMatcher) {\n      return andWith(asFeatureMatcher(\" <object is converted> \",converter,otherMatcher));\n    }\n\n",
				interfaceWithGeneric, fully));

		sb.append(addPrefix("  ",
				generateJavaDocWithoutParamNeitherParent(
						"Method that return the matcher itself and accept one single Matcher on the object itself.",
						JAVADOC_WARNING_SYNTAXIC_SUGAR_NO_CHANGE_ANYMORE,
						Optional.of("otherMatcher the matcher on the object itself."), Optional.of("the matcher"))))
				.append(String.format(
						"\n    default org.hamcrest.Matcher<%1$s> buildWith(%2$s) {\n      return andWith(otherMatcher);\n    }\n\n",
						fully, otherMatcher));

		sb.append(addPrefix("  ", generateJavaDocWithoutParamNeitherParent(
				"Method that return the parent builder and accept one single Matcher on the object itself.",
				JAVADOC_WARNING_PARENT_MAY_BE_VOID, Optional.of("otherMatcher the matcher on the object itself."),
				Optional.of("the parent builder or null if not applicable"))))
				.append(String.format(
						"    default _PARENT endWith(%1$s){\n      return andWith(otherMatcher).end();\n    }\n",
						otherMatcher));
		return sb.toString();
	}

	private String generateMainParentPublicInterface() {
		StringBuilder sb = new StringBuilder();
		sb.append(
				addPrefix("  ", generateJavaDoc(dslInterfaceDescription + " to support the end syntaxic sugar", true)))
				.append("\n");
		sb.append("  public static interface " + simpleNameOfGeneratedInterfaceMatcher + "EndSyntaxicSugar"
				+ getFullGenericParent() + " extends org.hamcrest.Matcher<"
				+ getFullyQualifiedNameOfClassAnnotatedWithProvideMatcherWithGeneric() + "> {\n");
		sb.append(addPrefix("  ",
				generateJavaDocWithoutParamNeitherParent("Method that return the parent builder",
						JAVADOC_WARNING_PARENT_MAY_BE_VOID, Optional.empty(),
						Optional.of("the parent builder or null if not applicable"))))
				.append("\n    _PARENT end();\n  }\n");
		return sb.toString();
	}

	private String generateMainBuildPublicInterface() {
		String fullyWithGeneric = getFullyQualifiedNameOfClassAnnotatedWithProvideMatcherWithGeneric();
		return new StringBuilder(addPrefix("  ",
				generateJavaDoc(dslInterfaceDescription + " to support the build syntaxic sugar", false)))
						.append("\n  public static interface ").append(simpleNameOfGeneratedInterfaceMatcher)
						.append("BuildSyntaxicSugar").append(fullGeneric).append(" extends org.hamcrest.Matcher<")
						.append(fullyWithGeneric).append("> {\n")
						.append(addPrefix("  ",
								generateJavaDocWithoutParamNeitherParent("Method that return the matcher itself.",
										JAVADOC_WARNING_SYNTAXIC_SUGAR_NO_CHANGE_ANYMORE, Optional.empty(),
										Optional.of("the matcher"))))
						.append("\n    default org.hamcrest.Matcher<").append(fullyWithGeneric)
						.append("> build() {\n      return this;\n    }\n  }\n").toString();
	}

	public String generatePrivateImplementationConstructor(String argument, String... body) {
		return String.format("    public %1$s(%2$s) {\n%3$s    }", getSimpleNameOfGeneratedImplementationMatcher(),
				argument, Arrays.stream(body).map(l -> "      " + l).collect(joining("\n")));
	}

	protected String generatePrivateImplementation() {
		return new StringBuilder("  /* package protected */ static class ")
				.append(getSimpleNameOfGeneratedImplementationMatcher()).append(getFullGenericParent())
				.append(" extends org.hamcrest.TypeSafeDiagnosingMatcher<")
				.append(getFullyQualifiedNameOfClassAnnotatedWithProvideMatcherWithGeneric()).append("> implements ")
				.append(getSimpleNameOfGeneratedInterfaceMatcherWithGenericParent() + " {\n    ")
				.append(fields.stream().map(AbstractFieldDescription::asMatcherField).collect(joining("\n    ")))
				.append("\n    private final _PARENT _parentBuilder;\n\n    private final java.util.List<org.hamcrest.Matcher> nextMatchers = new java.util.ArrayList<>();\n")
				.append(generateParentEntry())
				.append(fields.stream().map(AbstractFieldDescription::getImplementationInterface)
						.map(s -> addPrefix("    ", s)).collect(joining("\n")))
				.append("\n").append(generatePrivateImplementationForMatchersSafely()).append("\n")
				.append(generatedPrivateImplementationForDescribeTo()).append(PRIVATE_IMPLEMENTATION_END)
				.append(generatePrivateImplementationForAndWith()).append("\n  }\n").toString();
	}

	private String generateParentEntry() {
		return fullyQualifiedNameOfSuperClassOfClassAnnotatedWithProvideMatcher
				.map(p -> "    private SuperClassMatcher _parent;\n\n"
						+ generatePrivateImplementationConstructor("org.hamcrest.Matcher<? super " + p + "> parent",
								"this._parent=new SuperClassMatcher(parent);", "this._parentBuilder=null;")
						+ "\n\n"
						+ generatePrivateImplementationConstructor(
								"org.hamcrest.Matcher<? super " + p + "> parent,_PARENT parentBuilder",
								"this._parent=new SuperClassMatcher(parent);", "this._parentBuilder=parentBuilder;")
						+ "\n\n")
				.orElseGet(() -> generatePrivateImplementationConstructor("", "this._parentBuilder=null;") + "\n\n"
						+ generatePrivateImplementationConstructor("_PARENT parentBuilder",
								"this._parentBuilder=parentBuilder;")
						+ "\n\n");
	}

	private String generatePrivateImplementationForMatchersSafely() {
		StringBuilder sb = new StringBuilder(String.format(
				"    @Override\n    protected boolean matchesSafely(%1$s actual, org.hamcrest.Description mismatchDescription) {\n      boolean result=true;\n",
				getFullyQualifiedNameOfClassAnnotatedWithProvideMatcher()));
		if (fullyQualifiedNameOfSuperClassOfClassAnnotatedWithProvideMatcher.isPresent()) {
			sb.append(PARENT_VALIDATION);
		}
		fields.stream().map(f -> addPrefix("      ", f.asMatchesSafely() + "\n")).forEach(sb::append);
		sb.append(NEXTMATCHERS_VALIDATION);
		return sb.toString();
	}

	private String generatedPrivateImplementationForDescribeTo() {
		StringBuilder sb = new StringBuilder(String.format(
				"    @Override\n    public void describeTo(org.hamcrest.Description description) {\n      description.appendText(\"an instance of %1$s with\\n\");\n",
				getFullyQualifiedNameOfClassAnnotatedWithProvideMatcher()));
		if (fullyQualifiedNameOfSuperClassOfClassAnnotatedWithProvideMatcher.isPresent()) {
			sb.append(PARENT_DESCRIBETO);
		}
		fields.stream().map(f -> addPrefix("      ", f.asDescribeTo() + "\n")).forEach(sb::append);
		sb.append(NEXTMATCHERS_DESCRIBETO);
		return sb.toString();
	}

	private String generatePrivateImplementationForAndWith() {
		return String.format(
				"    @Override\n    public %1$s andWith(org.hamcrest.Matcher<? super %2$s> otherMatcher) {\n      nextMatchers.add(java.util.Objects.requireNonNull(otherMatcher,\"A matcher is expected\"));\n      return this;\n    }\n",
				getSimpleNameOfGeneratedInterfaceMatcherWithGenericParent(),
				getFullyQualifiedNameOfClassAnnotatedWithProvideMatcherWithGeneric());
	}

}
