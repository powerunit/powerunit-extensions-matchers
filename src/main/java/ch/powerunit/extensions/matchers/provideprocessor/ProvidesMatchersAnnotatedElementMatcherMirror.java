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
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.reducing;
import static java.util.stream.Collectors.toList;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.TypeElement;

import ch.powerunit.extensions.matchers.provideprocessor.fields.AbstractFieldDescription;
import ch.powerunit.extensions.matchers.provideprocessor.fields.FieldDescriptionMetaData;
import ch.powerunit.extensions.matchers.provideprocessor.fields.IgoreFieldDescription;

public abstract class ProvidesMatchersAnnotatedElementMatcherMirror
		extends ProvidesMatchersAnnotatedElementGenericMirror {

	private static final String PRIVATE_IMPLEMENTATION_END = "\n\n    @Override\n    public _PARENT end() {\n      return _parentBuilder;\n    }\n\n\n";

	private static final String NEXTMATCHERS_DESCRIBETO = "      for(org.hamcrest.Matcher nMatcher : nextMatchers) {\n        description.appendText(\"[object itself \").appendDescriptionOf(nMatcher).appendText(\"]\\n\");\n      }\n    }\n";

	private static final String PARENT_DESCRIBETO = "      description.appendText(\"[\").appendDescriptionOf(_parent).appendText(\"]\\n\");\n";

	private static final String PARENT_VALIDATION = "      if(!_parent.matches(actual)) {\n        mismatchDescription.appendText(\"[\"); _parent.describeMismatch(actual,mismatchDescription); mismatchDescription.appendText(\"]\\n\");\n        result=false;\n      }\n";

	private static final String NEXTMATCHERS_VALIDATION = "      for(org.hamcrest.Matcher nMatcher : nextMatchers) {\n        if(!nMatcher.matches(actual)) {\n          mismatchDescription.appendText(\"[object itself \"); nMatcher.describeMismatch(actual,mismatchDescription); mismatchDescription.appendText(\"]\\n\");\n        result=false;\n        }\n      }\n      return result;\n    }\n\n";

	private static final String JAVADOC_ANDWITHAS = "    /**\n     * Add a matcher on the object itself and not on a specific field, but convert the object before passing it to the matcher.\n     * <p>\n     * <i>This method, when used more than once, just add more matcher to the list.</i>\n     * @param converter the function to convert the object.\n     * @param otherMatcher the matcher on the converter object itself.\n     * @param <_TARGETOBJECT> the type of the target object\n     * @return the DSL to continue\n     */\n";

	private static final String JAVADOC_ANDWITH = "    /**\n     * Add a matcher on the object itself and not on a specific field.\n     * <p>\n     * <i>This method, when used more than once, just add more matcher to the list.</i>\n     * @param otherMatcher the matcher on the object itself.\n     * @return the DSL to continue\n     */\n";

	public static final String DEFAULT_FEATUREMATCHER_FORCONVERTER = "\n  private static <_TARGET,_SOURCE> org.hamcrest.Matcher<_SOURCE> asFeatureMatcher(String msg,java.util.function.Function<_SOURCE,_TARGET> converter,org.hamcrest.Matcher<? super _TARGET> matcher) {\n   return new org.hamcrest.FeatureMatcher<_SOURCE,_TARGET>(matcher, msg, msg) {\n     protected _TARGET featureValueOf(_SOURCE actual) {\n      return converter.apply(actual);\n    }};\n  }\n\n";

	protected final TypeElement typeElementForClassAnnotatedWithProvideMatcher;
	protected final String methodShortClassName;
	protected final Optional<String> fullyQualifiedNameOfSuperClassOfClassAnnotatedWithProvideMatcher;
	protected final List<AbstractFieldDescription> fields;
	protected final RoundMirror roundMirror;

	private List<AbstractFieldDescription> generateFields(TypeElement typeElement,
			ProvidesMatchersSubElementVisitor providesMatchersSubElementVisitor) {
		return typeElement.getEnclosedElements().stream()
				.map(ie -> ie.accept(providesMatchersSubElementVisitor, this)).filter(
						Optional::isPresent)
				.map(Optional::get).collect(
						collectingAndThen(
								groupingBy(FieldDescriptionMetaData::getFieldName,
										reducing(null,
												(v1, v2) -> v1 == null ? v2
														: v1 instanceof IgoreFieldDescription ? v1 : v2)),
								c -> c == null ? emptyList() : c.values().stream().collect(toList())));
	}

	public ProvidesMatchersAnnotatedElementMatcherMirror(TypeElement typeElement, RoundMirror roundMirror) {
		super(typeElement, roundMirror);
		this.roundMirror = roundMirror;
		this.typeElementForClassAnnotatedWithProvideMatcher = typeElement;
		this.methodShortClassName = simpleNameOfClassAnnotatedWithProvideMatcher.substring(0, 1).toLowerCase()
				+ simpleNameOfClassAnnotatedWithProvideMatcher.substring(1);
		if (!roundMirror.getProcessingEnv().getElementUtils().getTypeElement("java.lang.Object").asType()
				.equals(typeElement.getSuperclass())) {
			this.fullyQualifiedNameOfSuperClassOfClassAnnotatedWithProvideMatcher = Optional
					.ofNullable(typeElement.getSuperclass().toString());
		} else {
			this.fullyQualifiedNameOfSuperClassOfClassAnnotatedWithProvideMatcher = Optional.empty();
		}

		this.fields = generateFields(typeElement, new ProvidesMatchersSubElementVisitor(roundMirror));
	}

	public String generateMatchers() {
		StringBuilder sb = new StringBuilder();
		sb.append(DEFAULT_FEATUREMATCHER_FORCONVERTER);
		sb.append(generateFieldsMatcher());
		sb.append(fullyQualifiedNameOfSuperClassOfClassAnnotatedWithProvideMatcher.map(this::generateParentMatcher)
				.orElse(""));
		return sb.toString();
	}

	public String generateFieldsMatcher() {
		return fields.stream().map(FieldDescriptionMetaData::getMatcherForField).map(f -> addPrefix("  ", f))
				.collect(joining("\n")) + "\n";
	}

	public String generateParentMatcher(String parent) {
		return String.format(
				"  private static class SuperClassMatcher%1$s extends org.hamcrest.FeatureMatcher<%2$s,%3$s> {\n\n    public SuperClassMatcher(org.hamcrest.Matcher<? super %3$s> matcher) {\n      super(matcher,\"parent\",\"parent\");\n  }\n\n\n    protected %3$s featureValueOf(%2$s actual) {\n      return actual;\n    }\n\n  }\n\n\n",
				fullGeneric, fullyQualifiedNameOfClassAnnotatedWithProvideMatcher, parent);
	}

	public String generatePublicInterface() {
		return new StringBuilder().append(generateMainBuildPublicInterface())
				.append(generateMainParentPublicInterface()).append(generateExposedPublicInterface()).toString();

	}

	private String generateExposedPublicInterface() {
		StringBuilder sb = new StringBuilder();
		sb.append(addPrefix("  ",
				generateDefaultJavaDoc(Optional.empty(), Optional.empty(), Optional.empty(), true, true))).append("\n")
				.append("  public static interface ").append(simpleNameOfGeneratedInterfaceMatcher)
				.append(getFullGenericParent()).append(" extends org.hamcrest.Matcher<")
				.append(getFullyQualifiedNameOfClassAnnotatedWithProvideMatcherWithGeneric()).append(">,")
				.append(simpleNameOfGeneratedInterfaceMatcher).append("BuildSyntaxicSugar ").append(generic).append(",")
				.append(simpleNameOfGeneratedInterfaceMatcher).append("EndSyntaxicSugar ").append(getGenericParent())
				.append(" {\n");

		sb.append(fields.stream().map(AbstractFieldDescription::getDslInterface).map(s -> addPrefix("    ", s))
				.collect(joining("\n"))).append("\n\n");

		sb.append(generateAsPublicInterface());
		sb.append("  }\n");
		return sb.toString();
	}

	private String getDslInterfaceMatcherDescription() {
		return "DSL interface for matcher on " + getDefaultLinkForAnnotatedClass();
	}

	private String generateAsPublicInterface() {
		String fully = getFullyQualifiedNameOfClassAnnotatedWithProvideMatcherWithGeneric();
		String otherMatcher = "org.hamcrest.Matcher<? super " + fully + "> otherMatcher";

		StringBuilder sb = new StringBuilder(JAVADOC_ANDWITH).append("    ")
				.append(getSimpleNameOfGeneratedInterfaceMatcherWithGenericParent()).append(" andWith(")
				.append(otherMatcher).append(");\n\n");

		sb.append(JAVADOC_ANDWITHAS).append("    default <_TARGETOBJECT> ")
				.append(getSimpleNameOfGeneratedInterfaceMatcherWithGenericParent())
				.append(" andWithAs(java.util.function.Function<").append(fully)
				.append(",_TARGETOBJECT> converter,org.hamcrest.Matcher<? super _TARGETOBJECT> otherMatcher) {\n")
				.append("      return andWith(asFeatureMatcher(\" <object is converted> \",converter,otherMatcher));\n")
				.append("    }\n\n");

		sb.append(addPrefix("  ",
				generateJavaDocWithoutParamNeitherParent(
						"Method that return the matcher itself and accept one single Matcher on the object itself.",
						JAVADOC_WARNING_SYNTAXIC_SUGAR_NO_CHANGE_ANYMORE,
						Optional.of("otherMatcher the matcher on the object itself."), Optional.of("the matcher"))))
				.append("\n").append("    default org.hamcrest.Matcher<").append(fully).append("> buildWith(")
				.append(otherMatcher).append(") {\n      return andWith(otherMatcher);\n    }\n\n");

		sb.append(addPrefix("  ", generateJavaDocWithoutParamNeitherParent(
				"Method that return the parent builder and accept one single Matcher on the object itself.",
				JAVADOC_WARNING_PARENT_MAY_BE_VOID, Optional.of("otherMatcher the matcher on the object itself."),
				Optional.of("the parent builder or null if not applicable")))).append("    default _PARENT endWith(")
				.append(otherMatcher).append("){\n      return andWith(otherMatcher).end();\n    }\n");
		return sb.toString();
	}

	private String generateMainParentPublicInterface() {
		StringBuilder sb = new StringBuilder();
		sb.append(addPrefix("  ",
				generateJavaDoc(getDslInterfaceMatcherDescription() + " to support the end syntaxic sugar",
						Optional.empty(), Optional.empty(), Optional.empty(), true, true)))
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
		return new StringBuilder(addPrefix("  ",
				generateJavaDoc(getDslInterfaceMatcherDescription() + " to support the build syntaxic sugar",
						Optional.empty(), Optional.empty(), Optional.empty(), true, false)))
								.append("\n  public static interface ").append(simpleNameOfGeneratedInterfaceMatcher)
								.append("BuildSyntaxicSugar").append(fullGeneric)
								.append(" extends org.hamcrest.Matcher<")
								.append(getFullyQualifiedNameOfClassAnnotatedWithProvideMatcherWithGeneric()).append(
										"> {\n")
						.append(addPrefix("  ",
								generateJavaDocWithoutParamNeitherParent("Method that return the matcher itself.",
										JAVADOC_WARNING_SYNTAXIC_SUGAR_NO_CHANGE_ANYMORE, Optional.empty(),
										Optional.of("the matcher"))))
								.append("\n    default org.hamcrest.Matcher<")
								.append(getFullyQualifiedNameOfClassAnnotatedWithProvideMatcherWithGeneric())
								.append("> build() {\n      return this;\n    }\n  }\n").toString();
	}

	public String generatePrivateImplementationConstructor(String argument, String... body) {
		return String.format("    public %1$s(%2$s) {\n%3$s    }", getSimpleNameOfGeneratedImplementationMatcher(),
				argument, Arrays.stream(body).map(l -> "      " + l).collect(joining("\n")));
	}

	protected String generatePrivateImplementation() {
		String parentEntry = fullyQualifiedNameOfSuperClassOfClassAnnotatedWithProvideMatcher
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
		return new StringBuilder("  /* package protected */ static class ")
				.append(getSimpleNameOfGeneratedImplementationMatcher()).append(getFullGenericParent())
				.append(" extends org.hamcrest.TypeSafeDiagnosingMatcher<")
				.append(getFullyQualifiedNameOfClassAnnotatedWithProvideMatcherWithGeneric()).append("> implements ")
				.append(getSimpleNameOfGeneratedInterfaceMatcherWithGenericParent() + " {\n    ")
				.append(fields.stream().map(AbstractFieldDescription::asMatcherField).collect(joining("\n    ")))
				.append("\n    private final _PARENT _parentBuilder;\n\n    private final java.util.List<org.hamcrest.Matcher> nextMatchers = new java.util.ArrayList<>();\n")
				.append(parentEntry)
				.append(fields.stream().map(AbstractFieldDescription::getImplementationInterface)
						.map(s -> addPrefix("    ", s)).collect(joining("\n")))
				.append("\n").append(generatePrivateImplementationForMatchersSafely()).append("\n")
				.append(generatedPrivateImplementationForDescribeTo()).append(PRIVATE_IMPLEMENTATION_END)
				.append(generatePrivateImplementationForAndWith()).append("\n  }\n").toString();
	}

	private String generatePrivateImplementationForMatchersSafely() {
		StringBuilder sb = new StringBuilder("    @Override\n    protected boolean matchesSafely(")
				.append(fullyQualifiedNameOfClassAnnotatedWithProvideMatcher)
				.append(" actual, org.hamcrest.Description mismatchDescription) {\n")
				.append("      boolean result=true;\n");
		if (fullyQualifiedNameOfSuperClassOfClassAnnotatedWithProvideMatcher.isPresent()) {
			sb.append(PARENT_VALIDATION);
		}
		fields.stream().map(f -> f.asMatchesSafely() + "\n").map(f -> addPrefix("      ", f)).forEach(sb::append);

		sb.append(NEXTMATCHERS_VALIDATION);
		return sb.toString();
	}

	private String generatedPrivateImplementationForDescribeTo() {
		StringBuilder sb = new StringBuilder(
				"    @Override\n    public void describeTo(org.hamcrest.Description description) {\n")
						.append("      description.appendText(\"an instance of ")
						.append(fullyQualifiedNameOfClassAnnotatedWithProvideMatcher).append(" with\\n\");\n");
		if (fullyQualifiedNameOfSuperClassOfClassAnnotatedWithProvideMatcher.isPresent()) {
			sb.append(PARENT_DESCRIBETO);
		}
		fields.stream().map(f -> f.asDescribeTo() + "\n").map(f -> addPrefix("      ", f)).forEach(sb::append);
		sb.append(NEXTMATCHERS_DESCRIBETO);
		return sb.toString();
	}

	private String generatePrivateImplementationForAndWith() {
		return String.format(
				"    @Override\n    public %1$s andWith(org.hamcrest.Matcher<? super %2$s> otherMatcher) {\n      nextMatchers.add(java.util.Objects.requireNonNull(otherMatcher,\"A matcher is expected\"));\n      return this;\n    }\n",
				getSimpleNameOfGeneratedInterfaceMatcherWithGenericParent(),
				getFullyQualifiedNameOfClassAnnotatedWithProvideMatcherWithGeneric());
	}

	public String getDefaultReturnMethod() {
		return simpleNameOfClassAnnotatedWithProvideMatcher + "Matcher" + getGenericParent();
	}

	public TypeElement getTypeElementForClassAnnotatedWithProvideMatcher() {
		return typeElementForClassAnnotatedWithProvideMatcher;
	}

	public String getMethodShortClassName() {
		return methodShortClassName;
	}

	public String getSimpleNameOfGeneratedImplementationMatcher() {
		return simpleNameOfClassAnnotatedWithProvideMatcher + "MatcherImpl";
	}

	public String getSimpleNameOfGeneratedImplementationMatcherWithGenericNoParent() {
		return getSimpleNameOfGeneratedImplementationMatcher() + getGenericNoParent();
	}

	public RoundMirror getRoundMirror() {
		return roundMirror;
	}

}
