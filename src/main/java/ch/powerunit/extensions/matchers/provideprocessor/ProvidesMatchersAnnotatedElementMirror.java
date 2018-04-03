package ch.powerunit.extensions.matchers.provideprocessor;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic.Kind;
import javax.tools.JavaFileObject;

import ch.powerunit.extensions.matchers.ProvideMatchers;
import ch.powerunit.extensions.matchers.common.CommonUtils;
import ch.powerunit.extensions.matchers.provideprocessor.xml.GeneratedMatcher;

public class ProvidesMatchersAnnotatedElementMirror {

	private final TypeElement typeElementForClassAnnotatedWithProvideMatcher;
	private final ProcessingEnvironment processingEnv;
	private final String fullyQualifiedNameOfClassAnnotatedWithProvideMatcher;
	private final String packageNameOfGeneratedClass;
	private final String fullyQualifiedNameOfGeneratedClass;
	private final String simpleNameOfGeneratedClass;
	private final String simpleNameOfClassAnnotatedWithProvideMatcher;
	private final String methodShortClassName;
	private final boolean hasParent;
	private final boolean hasParentInSameRound;
	private final String generic;
	private final String fullGeneric;
	private final String comments;
	private final String paramJavadoc;
	private final String genericParent;
	private final String genericNoParent;
	private final String fullGenericParent;
	private final String defaultReturnMethod;
	private final String fullyQualifiedNameOfSuperClassOfClassAnnotatedWithProvideMatcher;
	private final String simpleNameOfGeneratedInterfaceMatcher;
	private final String simpleNameOfGeneratedImplementationMatcher;
	private final TypeElement typeElementForSuperClassOfClassAnnotatedWithProvideMatcher;
	private final Function<String, ProvidesMatchersAnnotatedElementMirror> findMirrorForTypeName;
	private final String genericForChaining;
	private final Set<? extends Element> elementsWithOtherAnnotation[];
	private final List<FieldDescription> fields;

	private List<FieldDescription> generateFields(TypeElement typeElement,
			ProvidesMatchersSubElementVisitor providesMatchersSubElementVisitor) {
		return typeElement.getEnclosedElements().stream()
				.map(ie -> ie.accept(providesMatchersSubElementVisitor,
						this))
				.filter(Optional::isPresent).map(t -> t.get()).collect(
						Collectors.collectingAndThen(
								Collectors.groupingBy(t -> t.getFieldName(),
										Collectors.reducing(null,
												(v1, v2) -> v1 == null ? v2 : v1.isIgnore() ? v1 : v2)),
						c -> c == null ? Collections.emptyList() : c.values().stream().collect(Collectors.toList())));
	}

	public ProvidesMatchersAnnotatedElementMirror(TypeElement typeElement, ProcessingEnvironment processingEnv,
			Predicate<Element> isInSameRound,
			Function<String, ProvidesMatchersAnnotatedElementMirror> findMirrorForTypeName,
			Set<? extends Element>... elementsWithOtherAnnotation) {
		this.typeElementForClassAnnotatedWithProvideMatcher = typeElement;
		this.processingEnv = processingEnv;
		this.elementsWithOtherAnnotation = elementsWithOtherAnnotation;
		this.fullyQualifiedNameOfClassAnnotatedWithProvideMatcher = typeElement.getQualifiedName().toString();
		String tpackageName = processingEnv.getElementUtils().getPackageOf(typeElement).getQualifiedName().toString();
		String toutputClassName = fullyQualifiedNameOfClassAnnotatedWithProvideMatcher + "Matchers";
		String tsimpleNameOfGeneratedClass = typeElement.getSimpleName().toString() + "Matchers";
		ProvideMatchers pm = typeElement.getAnnotation(ProvideMatchers.class);
		this.comments = pm.comments();
		if (!"".equals(pm.matchersClassName())) {
			toutputClassName = toutputClassName.replaceAll(tsimpleNameOfGeneratedClass + "$", pm.matchersClassName());
			tsimpleNameOfGeneratedClass = pm.matchersClassName();
		}
		this.simpleNameOfGeneratedClass = tsimpleNameOfGeneratedClass;
		if (!"".equals(pm.matchersPackageName())) {
			toutputClassName = toutputClassName.replaceAll("^" + tpackageName, pm.matchersPackageName());
			tpackageName = pm.matchersPackageName();
		}
		this.fullyQualifiedNameOfGeneratedClass = toutputClassName;
		this.packageNameOfGeneratedClass = tpackageName;
		this.simpleNameOfClassAnnotatedWithProvideMatcher = typeElement.getSimpleName().toString();
		this.methodShortClassName = simpleNameOfClassAnnotatedWithProvideMatcher.substring(0, 1).toLowerCase()
				+ simpleNameOfClassAnnotatedWithProvideMatcher.substring(1);
		TypeElement objectTE = processingEnv.getElementUtils().getTypeElement("java.lang.Object");
		this.hasParent = !objectTE.asType().equals(typeElement.getSuperclass());
		this.hasParentInSameRound = isInSameRound.test(typeElement);
		this.fullyQualifiedNameOfSuperClassOfClassAnnotatedWithProvideMatcher = typeElement.getSuperclass().toString();
		this.typeElementForSuperClassOfClassAnnotatedWithProvideMatcher = (TypeElement) processingEnv.getTypeUtils()
				.asElement(typeElement.getSuperclass());

		if (typeElement.getTypeParameters().size() > 0) {
			this.generic = "<"
					+ typeElement.getTypeParameters().stream().map(t -> t.toString()).collect(Collectors.joining(","))
					+ ">";
			this.fullGeneric = "<" + typeElement.getTypeParameters().stream()
					.map(t -> t.toString() + " extends "
							+ t.getBounds().stream().map(b -> b.toString()).collect(Collectors.joining("&")))
					.collect(Collectors.joining(",")) + ">";
		} else {
			this.generic = "";
			this.fullGeneric = "";
		}
		this.paramJavadoc = extractParamCommentFromJavadoc(processingEnv.getElementUtils().getDocComment(typeElement));
		this.genericParent = getAddParentToGeneric(generic);
		this.genericNoParent = getAddNoParentToGeneric(generic);
		this.fullGenericParent = getAddParentToGeneric(fullGeneric);
		this.defaultReturnMethod = simpleNameOfClassAnnotatedWithProvideMatcher + "Matcher" + genericParent;
		this.simpleNameOfGeneratedInterfaceMatcher = simpleNameOfClassAnnotatedWithProvideMatcher + "Matcher";
		this.simpleNameOfGeneratedImplementationMatcher = simpleNameOfClassAnnotatedWithProvideMatcher + "MatcherImpl";
		this.findMirrorForTypeName = findMirrorForTypeName;
		this.genericForChaining = genericParent.replaceAll("^<_PARENT", "<" + fullyQualifiedNameOfGeneratedClass + "."
				+ simpleNameOfGeneratedInterfaceMatcher + genericNoParent);
		ProvidesMatchersSubElementVisitor providesMatchersSubElementVisitor = new ProvidesMatchersSubElementVisitor(
				processingEnv, isInSameRound);
		this.fields = generateFields(typeElement, providesMatchersSubElementVisitor);
	}

	public String getSimpleNameOfGeneratedInterfaceMatcherWithGenericParent() {
		return simpleNameOfGeneratedInterfaceMatcher + " " + genericParent;
	}

	public String getSimpleNameOfGeneratedInterfaceMatcherWithGenericNoParent() {
		return simpleNameOfGeneratedInterfaceMatcher + " " + genericNoParent;
	}

	public String getFullyQualifiedNameOfClassAnnotatedWithProvideMatcherWithGeneric() {
		return fullyQualifiedNameOfClassAnnotatedWithProvideMatcher + " " + generic;
	}

	public String process() {
		StringBuilder factories = new StringBuilder();

		try {
			processingEnv.getMessager().printMessage(Kind.NOTE,
					"The class `" + fullyQualifiedNameOfGeneratedClass + "` will be generated as a Matchers class.",
					typeElementForClassAnnotatedWithProvideMatcher);
			JavaFileObject jfo = processingEnv.getFiler().createSourceFile(fullyQualifiedNameOfGeneratedClass,
					typeElementForClassAnnotatedWithProvideMatcher);
			try (PrintWriter wjfo = new PrintWriter(jfo.openWriter());) {
				wjfo.println("package " + packageNameOfGeneratedClass + ";");
				wjfo.println();
				wjfo.println("/**");
				wjfo.println(" * This class provides matchers for the class {@link "
						+ fullyQualifiedNameOfClassAnnotatedWithProvideMatcher + "}.");
				wjfo.println(" * ");
				wjfo.println(" * @see " + fullyQualifiedNameOfClassAnnotatedWithProvideMatcher
						+ " The class for which matchers are provided.");
				wjfo.println(" */");
				wjfo.println("@javax.annotation.Generated(value=\""
						+ ProvidesMatchersAnnotationsProcessor.class.getName() + "\",date=\"" + Instant.now().toString()
						+ "\",comments=" + CommonUtils.toJavaSyntax(comments) + ")");
				wjfo.println("public final class " + simpleNameOfGeneratedClass + " {");
				wjfo.println();
				wjfo.println("  private " + simpleNameOfGeneratedClass + "() {}");
				wjfo.println();
				wjfo.println(generateAndExtractFieldAndParentPrivateMatcher());
				wjfo.println();
				wjfo.println(generatePublicInterface());
				wjfo.println();
				wjfo.println(generatePrivateImplementation());

				wjfo.println();

				factories.append(generateDSLStarter(wjfo));
				wjfo.println("}");
			}
		} catch (IOException e1) {
			processingEnv.getMessager().printMessage(Kind.ERROR,
					"Unable to create the file containing the target class",
					typeElementForClassAnnotatedWithProvideMatcher);
		}
		return factories.toString();
	}

	public String generateAndExtractFieldAndParentPrivateMatcher() {
		StringBuilder sb = new StringBuilder();
		sb.append("\n")
				.append("  private static <_TARGET,_SOURCE> org.hamcrest.Matcher<_SOURCE> asFeatureMatcher(String msg,java.util.function.Function<_SOURCE,_TARGET> converter,org.hamcrest.Matcher<? super _TARGET> matcher) {")
				.append("\n").append("   return new org.hamcrest.FeatureMatcher<_SOURCE,_TARGET>(matcher, msg, msg) {")
				.append("\n").append("     protected _TARGET featureValueOf(_SOURCE actual) {").append("\n")
				.append("      return converter.apply(actual);").append("\n").append("    }};").append("\n")
				.append("  }").append("\n").append("\n");

		sb.append(fields.stream().map(f -> f.getMatcherForField("  ")).collect(Collectors.joining("\n"))).append("\n");
		if (hasParent) {
			sb.append("  private static class SuperClassMatcher").append(fullGeneric)
					.append(" extends org.hamcrest.FeatureMatcher<")
					.append(fullyQualifiedNameOfClassAnnotatedWithProvideMatcher).append(",")
					.append(fullyQualifiedNameOfSuperClassOfClassAnnotatedWithProvideMatcher + "> {").append("\n\n")
					.append("    public SuperClassMatcher(org.hamcrest.Matcher<? super ")
					.append(fullyQualifiedNameOfSuperClassOfClassAnnotatedWithProvideMatcher).append("> matcher) {")
					.append("\n").append("      super(matcher,\"parent\",\"parent\");").append("\n").append("  }")
					.append("\n\n\n").append("    protected ")
					.append(fullyQualifiedNameOfSuperClassOfClassAnnotatedWithProvideMatcher).append(" featureValueOf(")
					.append(fullyQualifiedNameOfClassAnnotatedWithProvideMatcher).append(" actual) {").append("\n")
					.append("      return actual;").append("\n").append("    }").append("\n\n").append("  }")
					.append("\n\n\n");
		}
		return sb.toString();
	}

	public String generatePublicInterface() {
		StringBuilder sb = new StringBuilder();

		sb.append(generateMainBuildPublicInterface());

		sb.append(generateMainParentPublicInterface());

		sb.append(generateJavaDoc("  ",
				"DSL interface for matcher on {@link " + fullyQualifiedNameOfClassAnnotatedWithProvideMatcher + " "
						+ simpleNameOfClassAnnotatedWithProvideMatcher + "}",
				Optional.empty(), Optional.empty(), Optional.empty(), true, true)).append("\n")
				.append("  public static interface ").append(simpleNameOfGeneratedInterfaceMatcher)
				.append(fullGenericParent).append(" extends org.hamcrest.Matcher<")
				.append(getFullyQualifiedNameOfClassAnnotatedWithProvideMatcherWithGeneric()).append(">,")
				.append(simpleNameOfGeneratedInterfaceMatcher).append("BuildSyntaxicSugar ").append(generic).append(",")
				.append(simpleNameOfGeneratedInterfaceMatcher).append("EndSyntaxicSugar ").append(genericParent)
				.append(" {").append("\n");

		sb.append(fields.stream().filter(FieldDescription::isNotIgnore).map(f -> f.getDslInterface("    "))
				.collect(Collectors.joining("\n"))).append("\n\n");

		sb.append(generateAsPublicInterface());
		sb.append("  }").append("\n");

		return sb.toString();

	}

	private String generateAsPublicInterface() {
		StringBuilder sb = new StringBuilder();
		sb.append("    /**").append("\n");
		sb.append("     * Add a matcher on the object itself and not on a specific field.").append("\n");
		sb.append("     * <p>").append("\n");
		sb.append("     * <i>This method, when used more than once, just add more matcher to the list.</i>")
				.append("\n");
		sb.append("     * @param otherMatcher the matcher on the object itself.").append("\n");
		sb.append("     * @return the DSL to continue").append("\n");
		sb.append("     */").append("\n");
		sb.append("    " + getSimpleNameOfGeneratedInterfaceMatcherWithGenericParent()
				+ " andWith(org.hamcrest.Matcher<? super "
				+ getFullyQualifiedNameOfClassAnnotatedWithProvideMatcherWithGeneric() + "> otherMatcher);")
				.append("\n");
		sb.append("\n");

		sb.append("    /**").append("\n");
		sb.append(
				"     * Add a matcher on the object itself and not on a specific field, but convert the object before passing it to the matcher.")
				.append("\n");
		sb.append("     * <p>").append("\n");
		sb.append("     * <i>This method, when used more than once, just add more matcher to the list.</i>")
				.append("\n");
		sb.append("     * @param converter the function to convert the object.").append("\n");
		sb.append("     * @param otherMatcher the matcher on the converter object itself.").append("\n");
		sb.append("     * @param <_TARGETOBJECT> the type of the target object").append("\n");
		sb.append("     * @return the DSL to continue").append("\n");
		sb.append("     */").append("\n");
		sb.append("    default <_TARGETOBJECT> " + getSimpleNameOfGeneratedInterfaceMatcherWithGenericParent()
				+ " andWithAs(java.util.function.Function<"
				+ getFullyQualifiedNameOfClassAnnotatedWithProvideMatcherWithGeneric()
				+ ",_TARGETOBJECT> converter,org.hamcrest.Matcher<? super _TARGETOBJECT> otherMatcher) {").append("\n");
		sb.append("      return andWith(asFeatureMatcher(\" <object is converted> \",converter,otherMatcher));")
				.append("\n");
		sb.append("    }").append("\n");
		sb.append("\n");

		sb.append(generateJavaDoc("  ",
				"Method that return the matcher itself and accept one single Matcher on the object itself.",
				Optional.of(
						"<b>This method is a syntaxic sugar that end the DSL and make clear that the matcher can't be change anymore.</b>"),
				Optional.of("otherMatcher the matcher on the object itself."), Optional.of("the matcher"), false,
				false)).append("\n");
		sb.append("    default org.hamcrest.Matcher<"
				+ getFullyQualifiedNameOfClassAnnotatedWithProvideMatcherWithGeneric()
				+ "> buildWith(org.hamcrest.Matcher<? super "
				+ getFullyQualifiedNameOfClassAnnotatedWithProvideMatcherWithGeneric() + "> otherMatcher) {")
				.append("\n");
		sb.append("      return andWith(otherMatcher);").append("\n");
		sb.append("    }").append("\n");
		sb.append("\n");

		sb.append(generateJavaDoc("  ",
				"Method that return the parent builder and accept one single Matcher on the object itself.",
				Optional.of(
						"<b>This method only works in the contexte of a parent builder. If the real type is Void, then nothing will be returned.</b>"),
				Optional.of("otherMatcher the matcher on the object itself."),
				Optional.of("the parent builder or null if not applicable"), false, false));
		sb.append("    default _PARENT endWith(org.hamcrest.Matcher<? super "
				+ getFullyQualifiedNameOfClassAnnotatedWithProvideMatcherWithGeneric() + "> otherMatcher){")
				.append("\n");
		sb.append("      return andWith(otherMatcher).end();").append("\n");
		sb.append("    }").append("\n");
		return sb.toString();
	}

	private String generateMainParentPublicInterface() {
		StringBuilder sb = new StringBuilder();
		sb.append(generateJavaDoc("  ",
				"DSL interface for matcher on {@link " + fullyQualifiedNameOfClassAnnotatedWithProvideMatcher + " "
						+ simpleNameOfClassAnnotatedWithProvideMatcher + "} to support the end syntaxic sugar",
				Optional.empty(), Optional.empty(), Optional.empty(), true, true)).append("\n");
		sb.append("  public static interface " + simpleNameOfGeneratedInterfaceMatcher + "EndSyntaxicSugar"
				+ fullGenericParent + " extends org.hamcrest.Matcher<"
				+ getFullyQualifiedNameOfClassAnnotatedWithProvideMatcherWithGeneric() + "> {").append("\n");
		sb.append(generateJavaDoc("  ", "Method that return the parent builder",
				Optional.of(
						"<b>This method only works in the contexte of a parent builder. If the real type is Void, then nothing will be returned.</b>"),
				Optional.empty(), Optional.of("the parent builder or null if not applicable"), false, false))
				.append("\n");
		sb.append("    _PARENT end();").append("\n");
		sb.append("  }").append("\n");
		return sb.toString();
	}

	private String generateMainBuildPublicInterface() {
		StringBuilder sb = new StringBuilder();
		sb.append(generateJavaDoc("  ",
				"DSL interface for matcher on {@link " + fullyQualifiedNameOfClassAnnotatedWithProvideMatcher + " "
						+ simpleNameOfClassAnnotatedWithProvideMatcher + "} to support the build syntaxic sugar",
				Optional.empty(), Optional.empty(), Optional.empty(), true, false)).append("\n");
		sb.append("  public static interface " + simpleNameOfGeneratedInterfaceMatcher + "BuildSyntaxicSugar"
				+ fullGeneric + " extends org.hamcrest.Matcher<"
				+ getFullyQualifiedNameOfClassAnnotatedWithProvideMatcherWithGeneric() + "> {").append("\n");
		sb.append(generateJavaDoc("  ", "Method that return the matcher itself.",
				Optional.of(
						"<b>This method is a syntaxic sugar that end the DSL and make clear that the matcher can't be change anymore.</b>"),
				Optional.empty(), Optional.of("the matcher"), false, false)).append("\n");
		sb.append("    default org.hamcrest.Matcher<"
				+ getFullyQualifiedNameOfClassAnnotatedWithProvideMatcherWithGeneric() + "> build() {").append("\n");
		sb.append("      return this;").append("\n");
		sb.append("    }").append("\n");
		sb.append("  }").append("\n");
		return sb.toString();
	}

	public String generatePrivateImplementationConstructor(String argument, String... body) {
		return new StringBuilder().append("    public ").append(simpleNameOfGeneratedImplementationMatcher).append("(")
				.append(argument).append(") {\n")
				.append(Arrays.stream(body).map(l -> "      " + l).collect(Collectors.joining("\n"))).append("    }")
				.toString();
	}

	private String generatePrivateImplementation() {
		StringBuilder sb = new StringBuilder();
		sb.append("  /* package protected */ static class ").append(simpleNameOfGeneratedImplementationMatcher)
				.append(fullGenericParent).append(" extends org.hamcrest.TypeSafeDiagnosingMatcher<")
				.append(getFullyQualifiedNameOfClassAnnotatedWithProvideMatcherWithGeneric()).append("> implements ")
				.append(getSimpleNameOfGeneratedInterfaceMatcherWithGenericParent() + " {\n");
		sb.append("    " + fields.stream().map(FieldDescription::asMatcherField).collect(Collectors.joining("\n    ")))
				.append("\n");

		sb.append("    private final _PARENT _parentBuilder;\n\n").append(
				"    private final java.util.List<org.hamcrest.Matcher> nextMatchers = new java.util.ArrayList<>();\n");
		if (hasParent) {
			sb.append("    private SuperClassMatcher _parent;\n\n")
					.append(generatePrivateImplementationConstructor(
							"org.hamcrest.Matcher<? super "
									+ fullyQualifiedNameOfSuperClassOfClassAnnotatedWithProvideMatcher + "> parent",
							"this._parent=new SuperClassMatcher(parent);", "this._parentBuilder=null;"))
					.append("\n\n")
					.append(generatePrivateImplementationConstructor(
							"org.hamcrest.Matcher<? super "
									+ fullyQualifiedNameOfSuperClassOfClassAnnotatedWithProvideMatcher
									+ "> parent,_PARENT parentBuilder",
							"this._parent=new SuperClassMatcher(parent);", "this._parentBuilder=parentBuilder;"))
					.append("\n\n");
		} else {
			sb.append(generatePrivateImplementationConstructor("", "this._parentBuilder=null;")).append("\n\n")
					.append(generatePrivateImplementationConstructor("_PARENT parentBuilder",
							"this._parentBuilder=parentBuilder;"))
					.append("\n\n");
		}

		sb.append(fields.stream().filter(FieldDescription::isNotIgnore).map(f -> f.getImplementationInterface("    "))
				.collect(Collectors.joining("\n"))).append("\n");

		sb.append(generatePrivateImplementationForMatchersSafely()).append("\n")
				.append(generatedPrivateImplementationForDescribeTo()).append("\n\n")
				.append(generatePrivateImplementationForEnd()).append("\n")
				.append(generatePrivateImplementationForAndWith()).append("\n").append("  }\n");
		return sb.toString();
	}

	private String generatePrivateImplementationForMatchersSafely() {
		StringBuilder sb = new StringBuilder();
		sb.append("    @Override\n").append("    protected boolean matchesSafely(")
				.append(fullyQualifiedNameOfClassAnnotatedWithProvideMatcher)
				.append(" actual, org.hamcrest.Description mismatchDescription) {\n")
				.append("      boolean result=true;\n");
		if (hasParent) {
			sb.append("      if(!_parent.matches(actual)) {\n")
					.append("        mismatchDescription.appendText(\"[\"); _parent.describeMismatch(actual,mismatchDescription); mismatchDescription.appendText(\"]\\n\");\n")
					.append("        result=false;\n").append("      }\n");
		}
		fields.stream().map(f -> f.asMatchesSafely("      ") + "\n").forEach(sb::append);

		sb.append("      for(org.hamcrest.Matcher nMatcher : nextMatchers) {\n")
				.append("        if(!nMatcher.matches(actual)) {\n")
				.append("          mismatchDescription.appendText(\"[object itself \"); nMatcher.describeMismatch(actual,mismatchDescription); mismatchDescription.appendText(\"]\\n\");\n")
				.append("        result=false;\n").append("        }\n").append("      }\n")
				.append("      return result;\n").append("    }\n\n");
		return sb.toString();
	}

	private String generatePrivateImplementationForEnd() {
		return new StringBuilder().append("    @Override\n").append("    public _PARENT end() {\n")
				.append("      return _parentBuilder;\n").append("    }\n\n").toString();
	}

	private String generatedPrivateImplementationForDescribeTo() {
		StringBuilder sb = new StringBuilder();
		sb.append("    @Override\n").append("    public void describeTo(org.hamcrest.Description description) {\n")
				.append("      description.appendText(\"an instance of ")
				.append(fullyQualifiedNameOfClassAnnotatedWithProvideMatcher).append(" with\\n\");\n");
		if (hasParent) {
			sb.append("      description.appendText(\"[\").appendDescriptionOf(_parent).appendText(\"]\\n\");\n");
		}
		fields.stream().map(f -> f.asDescribeTo("      ") + "\n").forEach(sb::append);
		sb.append("      for(org.hamcrest.Matcher nMatcher : nextMatchers) {\n")
				.append("        description.appendText(\"[object itself \").appendDescriptionOf(nMatcher).appendText(\"]\\n\");\n")
				.append("      }\n").append("    }\n");
		return sb.toString();
	}

	private String generatePrivateImplementationForAndWith() {
		return new StringBuilder().append("    @Override\n").append("    public ")
				.append(getSimpleNameOfGeneratedInterfaceMatcherWithGenericParent())
				.append(" andWith(org.hamcrest.Matcher<? super ")
				.append(getFullyQualifiedNameOfClassAnnotatedWithProvideMatcherWithGeneric())
				.append("> otherMatcher) {\n")
				.append("      nextMatchers.add(java.util.Objects.requireNonNull(otherMatcher,\"A matcher is expected\"));\n")
				.append("      return this;\n").append("    }\n").toString();
	}

	private String generateDSLStarter(PrintWriter wjfo) {
		StringBuilder factories = new StringBuilder();
		factories.append(generateDefaultDSLStarter(wjfo));

		factories.append(generateDefaultForChainingDSLStarter(wjfo));

		if (hasParent) {
			factories.append(generateParentDSLStarter(wjfo));
		}

		wjfo.println();

		if (!hasParent) {
			factories.append(generateParentValueDSLStarter(wjfo, ""));
		}
		if (hasParent && hasParentInSameRound) {
			factories.append(generateParentInSameRoundDSLStarter(wjfo));
		}
		return factories.toString();
	}

	private String generateDefaultDSLStarter(PrintWriter wjfo) {
		StringBuilder factories = new StringBuilder();
		StringBuilder javadoc = new StringBuilder();
		String methodName = fullGeneric + " " + getSimpleNameOfGeneratedInterfaceMatcherWithGenericNoParent() + " "
				+ methodShortClassName + "With()";

		javadoc.append(generateJavaDoc("  ",
				"Start a DSL matcher for the {@link " + fullyQualifiedNameOfClassAnnotatedWithProvideMatcher + " "
						+ simpleNameOfClassAnnotatedWithProvideMatcher + "}",
				Optional.of(
						"The returned builder (which is also a Matcher), at this point accepts any object that is a {@link "
								+ fullyQualifiedNameOfClassAnnotatedWithProvideMatcher + " "
								+ simpleNameOfClassAnnotatedWithProvideMatcher + "}."),
				Optional.empty(), Optional.of("the DSL matcher"), true, false));

		wjfo.println(javadoc.toString());
		factories.append(javadoc.toString());

		wjfo.println("  @org.hamcrest.Factory");
		wjfo.println("  public static " + methodName + " {");
		factories.append("  default " + fullGeneric + " " + fullyQualifiedNameOfGeneratedClass + "."
				+ getSimpleNameOfGeneratedInterfaceMatcherWithGenericNoParent() + " " + methodShortClassName + "With()"
				+ " {").append("\n");
		factories.append("    return " + fullyQualifiedNameOfGeneratedClass + "." + methodShortClassName + "With();")
				.append("\n");
		factories.append("  }").append("\n");
		if (hasParent) {
			wjfo.println("    return new " + simpleNameOfGeneratedImplementationMatcher + genericNoParent
					+ "(org.hamcrest.Matchers.anything());");
		} else {
			wjfo.println("    return new " + simpleNameOfGeneratedImplementationMatcher + genericNoParent + "();");
		}
		wjfo.println("  }");
		return factories.toString();
	}

	private String generateDefaultForChainingDSLStarter(PrintWriter wjfo) {
		StringBuilder factories = new StringBuilder();
		StringBuilder javadoc = new StringBuilder();
		String methodName = fullGenericParent + " " + getSimpleNameOfGeneratedInterfaceMatcherWithGenericParent() + " "
				+ methodShortClassName + "WithParent(_PARENT parentBuilder)";
		javadoc.append(generateJavaDoc("  ",
				"Start a DSL matcher for the {@link " + fullyQualifiedNameOfClassAnnotatedWithProvideMatcher + " "
						+ simpleNameOfClassAnnotatedWithProvideMatcher + "}",
				Optional.of(
						"The returned builder (which is also a Matcher), at this point accepts any object that is a {@link "
								+ fullyQualifiedNameOfClassAnnotatedWithProvideMatcher + " "
								+ simpleNameOfClassAnnotatedWithProvideMatcher + "}."),
				Optional.of("parentBuilder the parentBuilder."), Optional.of("the DSL matcher"), true, true));

		wjfo.println(javadoc.toString());

		wjfo.println("  public static " + methodName + " {");
		if (hasParent) {
			wjfo.println("    return new " + simpleNameOfGeneratedImplementationMatcher + genericParent
					+ "(org.hamcrest.Matchers.anything(),parentBuilder);");
		} else {
			wjfo.println("    return new " + simpleNameOfGeneratedImplementationMatcher + genericParent
					+ "(parentBuilder);");
		}
		wjfo.println("  }");
		return factories.toString();
	}

	private String generateParentDSLStarter(PrintWriter wjfo) {
		StringBuilder factories = new StringBuilder();
		StringBuilder javadoc = new StringBuilder();
		javadoc.append(generateJavaDoc("  ",
				"Start a DSL matcher for the {@link " + fullyQualifiedNameOfClassAnnotatedWithProvideMatcher + " "
						+ simpleNameOfClassAnnotatedWithProvideMatcher + "}",
				Optional.empty(), Optional.of("matcherOnParent the matcher on the parent data."),
				Optional.of("the DSL matcher"), true, false));

		wjfo.println(javadoc.toString());
		factories.append(javadoc.toString());

		wjfo.println("  @org.hamcrest.Factory");
		wjfo.println(
				"  public static " + fullGeneric + " " + getSimpleNameOfGeneratedInterfaceMatcherWithGenericNoParent()
						+ " " + methodShortClassName + "With(org.hamcrest.Matcher<? super "
						+ fullyQualifiedNameOfSuperClassOfClassAnnotatedWithProvideMatcher + "> matcherOnParent) {");
		wjfo.println("    return new " + simpleNameOfGeneratedImplementationMatcher + genericNoParent
				+ "(matcherOnParent);");
		wjfo.println("  }");

		factories.append("  default " + fullGeneric + " " + fullyQualifiedNameOfGeneratedClass + "."
				+ getSimpleNameOfGeneratedInterfaceMatcherWithGenericNoParent() + " " + methodShortClassName
				+ "With(org.hamcrest.Matcher<? super "
				+ fullyQualifiedNameOfSuperClassOfClassAnnotatedWithProvideMatcher + "> matcherOnParent)" + " {")
				.append("\n");
		factories.append("    return " + fullyQualifiedNameOfGeneratedClass + "." + methodShortClassName
				+ "With(matcherOnParent);").append("\n");
		factories.append("  }").append("\n");
		return factories.toString();
	}

	private String generateParentInSameRoundDSLStarter(PrintWriter wjfo) {
		StringBuilder factories = new StringBuilder();
		ProvidesMatchersAnnotatedElementMirror parentMirror = findMirrorForTypeName
				.apply(typeElementForSuperClassOfClassAnnotatedWithProvideMatcher.getQualifiedName().toString());
		factories.append(generateParentValueDSLStarter(wjfo, parentMirror.fullyQualifiedNameOfGeneratedClass + "."
				+ parentMirror.methodShortClassName + "WithSameValue(other)"));

		if (typeElementForSuperClassOfClassAnnotatedWithProvideMatcher.getTypeParameters().isEmpty()) {
			factories.append(generateParentInSameRoundWithChaningDSLStarter(wjfo, parentMirror));
		}
		return factories.toString();
	}

	private String generateParentValueDSLStarter(PrintWriter wjfo, String argumentForParentBuilder) {
		StringBuilder factories = new StringBuilder();
		StringBuilder javadoc = new StringBuilder();
		javadoc.append(generateJavaDoc("  ",
				"Start a DSL matcher for the {@link " + fullyQualifiedNameOfClassAnnotatedWithProvideMatcher + " "
						+ simpleNameOfClassAnnotatedWithProvideMatcher + "}",
				Optional.empty(), Optional.of("other the other object to be used as a reference."),
				Optional.of("the DSL matcher"), true, false));
		wjfo.println(javadoc.toString());
		factories.append(javadoc.toString());
		wjfo.println("  @org.hamcrest.Factory");
		wjfo.println(
				"  public static " + fullGeneric + " " + getSimpleNameOfGeneratedInterfaceMatcherWithGenericNoParent()
						+ " " + methodShortClassName + "WithSameValue("
						+ getFullyQualifiedNameOfClassAnnotatedWithProvideMatcherWithGeneric() + " other) {");
		wjfo.println("    " + getSimpleNameOfGeneratedInterfaceMatcherWithGenericNoParent() + " m=new "
				+ simpleNameOfGeneratedImplementationMatcher + genericNoParent + "(" + argumentForParentBuilder + ");");

		fields.stream().filter(FieldDescription::isNotIgnore).map(f -> "    " + f.getFieldCopy("m", "other") + ";")
				.forEach(wjfo::println);
		wjfo.println("    return m;");
		wjfo.println("  }");

		factories.append("  default " + fullGeneric + " " + fullyQualifiedNameOfGeneratedClass + "."
				+ getSimpleNameOfGeneratedInterfaceMatcherWithGenericNoParent() + " " + methodShortClassName
				+ "WithSameValue(" + getFullyQualifiedNameOfClassAnnotatedWithProvideMatcherWithGeneric() + " other)"
				+ " {").append("\n");
		factories.append("    return " + fullyQualifiedNameOfGeneratedClass + "." + methodShortClassName
				+ "WithSameValue(other);").append("\n");
		factories.append("  }").append("\n");
		return factories.toString();
	}

	private String generateParentInSameRoundWithChaningDSLStarter(PrintWriter wjfo,
			ProvidesMatchersAnnotatedElementMirror parentMirror) {
		StringBuilder factories = new StringBuilder();
		StringBuilder javadoc = new StringBuilder();
		javadoc.append(generateJavaDoc("  ",
				"Start a DSL matcher for the {@link " + fullyQualifiedNameOfClassAnnotatedWithProvideMatcher + " "
						+ simpleNameOfClassAnnotatedWithProvideMatcher + "}",
				Optional.empty(), Optional.empty(), Optional.of("the DSL matcher"), true, false));

		wjfo.println(javadoc.toString());
		factories.append(javadoc.toString());
		wjfo.println("  @org.hamcrest.Factory");
		wjfo.println("  public static " + fullGeneric + " " + parentMirror.fullyQualifiedNameOfGeneratedClass + "."
				+ parentMirror.simpleNameOfGeneratedInterfaceMatcher + genericForChaining + " " + methodShortClassName
				+ "WithParent() {");
		wjfo.println("    " + simpleNameOfGeneratedImplementationMatcher + genericNoParent + " m=new "
				+ simpleNameOfGeneratedImplementationMatcher + genericNoParent + "(org.hamcrest.Matchers.anything());");

		wjfo.println("    " + parentMirror.fullyQualifiedNameOfGeneratedClass + "."
				+ parentMirror.simpleNameOfGeneratedInterfaceMatcher + " tmp = "
				+ parentMirror.fullyQualifiedNameOfGeneratedClass + "." + parentMirror.methodShortClassName
				+ "WithParent(m);");
		wjfo.println("    m._parent = new SuperClassMatcher(tmp);");
		wjfo.println("    return tmp;");
		wjfo.println("  }");

		factories.append("  default " + fullGeneric + " " + parentMirror.fullyQualifiedNameOfGeneratedClass + "."
				+ parentMirror.simpleNameOfGeneratedInterfaceMatcher + genericForChaining + " " + methodShortClassName
				+ "WithParent()" + " {").append("\n");
		factories.append(
				"    return " + fullyQualifiedNameOfGeneratedClass + "." + methodShortClassName + "WithParent();")
				.append("\n");
		factories.append("  }").append("\n");
		return factories.toString();
	}

	private String generateJavaDoc(String prefix, String description, Optional<String> moreDetails,
			Optional<String> param, Optional<String> returnDescription, boolean withParam, boolean withParent) {
		StringBuilder sb = new StringBuilder();
		sb.append(prefix).append("/**").append("\n");
		sb.append(prefix).append(" * ").append(description).append(".\n");
		moreDetails.ifPresent(
				t -> sb.append(prefix).append(" * <p>\n").append(prefix).append(" * ").append(t).append("\n"));
		param.ifPresent(t -> sb.append(prefix).append(" * @param ").append(t).append("\n"));
		if (withParam) {
			sb.append(prefix).append(paramJavadoc.replaceAll("\\R", "\n" + prefix)).append(" * \n");
		}
		if (withParent) {
			sb.append(prefix)
					.append(" * @param <_PARENT> used to reference, if necessary, a parent for this builder. By default Void is used an indicate no parent builder")
					.append("\n");
		}
		returnDescription.ifPresent(t -> sb.append(prefix).append(" * @return ").append(t).append("\n"));
		sb.append(prefix).append(" */").append("\n");
		return sb.toString();
	}

	private static String extractParamCommentFromJavadoc(String docComment) {
		if (docComment == null) {
			return " * \n";
		}
		boolean insideParam = false;
		StringBuilder sb = new StringBuilder();
		sb.append(" * \n");
		for (String line : docComment.split("\\R")) {
			if (insideParam && line.matches("^\\s*@.*$")) {
				insideParam = false;
			}
			if (line.matches("^\\s*@param.*$")) {
				insideParam = true;
			}
			if (insideParam) {
				sb.append(" *" + line).append("\n");
			}
		}
		return sb.toString();
	}

	private static String getAddParentToGeneric(String generic) {
		if ("".equals(generic)) {
			return "<_PARENT>";
		} else {
			return generic.replaceFirst("<", "<_PARENT,");
		}
	}

	private static String getAddNoParentToGeneric(String generic) {
		if ("".equals(generic)) {
			return "<Void>";
		} else {
			return generic.replaceFirst("<", "<Void,");
		}
	}

	public String getFullyQualifiedNameOfClassAnnotatedWithProvideMatcher() {
		return fullyQualifiedNameOfClassAnnotatedWithProvideMatcher;
	}

	public String getFullyQualifiedNameOfGeneratedClass() {
		return fullyQualifiedNameOfGeneratedClass;
	}

	public String getDefaultReturnMethod() {
		return defaultReturnMethod;
	}

	public String getFullGeneric() {
		return fullGeneric;
	}

	public String getGeneric() {
		return generic;
	}

	public void removeFromIgnoreList(Element e) {
		Arrays.stream(elementsWithOtherAnnotation).forEach(t -> t.remove(e));
	}

	public boolean isInsideIgnoreList(Element e) {
		return Arrays.stream(elementsWithOtherAnnotation).map(t -> t.contains(e)).filter(t -> t).findAny()
				.orElse(false);
	}

	public TypeElement getTypeElementForClassAnnotatedWithProvideMatcher() {
		return typeElementForClassAnnotatedWithProvideMatcher;
	}

	public ProvidesMatchersAnnotatedElementMirror findMirrorFor(String name) {
		return findMirrorForTypeName.apply(name);
	}

	public String getMethodShortClassName() {
		return methodShortClassName;
	}

	public GeneratedMatcher asXml() {
		GeneratedMatcher gm = new GeneratedMatcher();
		gm.setFullyQualifiedNameGeneratedClass(fullyQualifiedNameOfGeneratedClass);
		gm.setFullyQualifiedNameInputClass(fullyQualifiedNameOfClassAnnotatedWithProvideMatcher);
		gm.setSimpleNameGeneratedClass(simpleNameOfGeneratedClass);
		gm.setSimpleNameInputClass(simpleNameOfClassAnnotatedWithProvideMatcher);
		gm.setDslMethodNameStart(methodShortClassName);
		gm.setGeneratedMatcherField(
				fields.stream().map(FieldDescription::asGeneratedMatcherField).collect(Collectors.toList()));
		gm.setMirror(this);
		return gm;
	}

	public ProcessingEnvironment getProcessingEnv() {
		return processingEnv;
	}

}
