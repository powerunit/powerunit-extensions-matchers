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

import java.io.IOException;
import java.io.PrintWriter;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedOptions;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic.Kind;

import javax.tools.JavaFileObject;

import ch.powerunit.extensions.matchers.ProvideMatchers;

/**
 * @author borettim
 *
 */
@SupportedAnnotationTypes({ "ch.powerunit.extensions.matchers.ProvideMatchers" })
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedOptions({ "ch.powerunit.extensions.matchers.provideprocessor.ProvidesMatchersAnnotationsProcessor.factory" })
public class ProvidesMatchersAnnotationsProcessor extends AbstractProcessor {

	private String factory = null;

	private List<String> factories = new ArrayList<>();

	@Override
	public synchronized void init(ProcessingEnvironment processingEnv) {
		super.init(processingEnv);
		factory = processingEnv.getOptions().get(ProvidesMatchersAnnotationsProcessor.class.getName() + ".factory");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.annotation.processing.AbstractProcessor#process(java.util.Set,
	 * javax.annotation.processing.RoundEnvironment)
	 */
	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		Elements elementsUtils = processingEnv.getElementUtils();
		Filer filerUtils = processingEnv.getFiler();
		Types typesUtils = processingEnv.getTypeUtils();
		Messager messageUtils = processingEnv.getMessager();
		TypeElement provideMatchersTE = elementsUtils
				.getTypeElement("ch.powerunit.extensions.matchers.ProvideMatchers");
		TypeElement objectTE = elementsUtils.getTypeElement("java.lang.Object");

		if (!roundEnv.processingOver()) {
			processAnnotatedElements(roundEnv, elementsUtils, filerUtils, typesUtils, messageUtils, provideMatchersTE,
					objectTE);

		} else if (factory != null) {
			processFactory(filerUtils, messageUtils);
		}
		return true;
	}

	private void processFactory(Filer filerUtils, Messager messageUtils) {
		try {
			messageUtils.printMessage(Kind.NOTE,
					"The interface `" + factory + "` will be generated as a factory interface.");
			JavaFileObject jfo = filerUtils.createSourceFile(factory);
			try (PrintWriter wjfo = new PrintWriter(jfo.openWriter());) {
				wjfo.println("package " + factory.replaceAll("\\.[^.]+$", "") + ";");
				wjfo.println();
				wjfo.println("/**");
				wjfo.println(" * Factories generated.");
				wjfo.println(" * <p> ");
				wjfo.println(" * This DSL can be use in several way : ");
				wjfo.println(" * <ul> ");
				wjfo.println(
						" *  <li>By implementing this interface. In this case, all the methods of this interface will be available inside the implementing class.</li>");
				wjfo.println(
						" *  <li>By refering the static field named {@link #DSL} which expose all the DSL method.</li>");
				wjfo.println(" * </ul> ");
				wjfo.println(" */");
				wjfo.println(
						"@javax.annotation.Generated(value=\"" + ProvidesMatchersAnnotationsProcessor.class.getName()
								+ "\",date=\"" + Instant.now().toString() + "\")");
				String cName = factory.replaceAll("^([^.]+\\.)*", "");
				wjfo.println("public interface " + cName + " {");
				wjfo.println();
				wjfo.println("  /**");
				wjfo.println(
						"   * Use this static field to access all the DSL syntax, without be required to implements this interface.");
				wjfo.println("   */");
				wjfo.println("  public static final " + cName + " DSL = new " + cName + "() {};");
				wjfo.println();
				factories.stream().forEach(wjfo::println);
				wjfo.println("}");
			}
		} catch (IOException e1) {
			messageUtils.printMessage(Kind.ERROR, "Unable to create the file containing the target class `" + factory
					+ "`, because of " + e1.getMessage());
		}
	}

	private void processAnnotatedElements(RoundEnvironment roundEnv, Elements elementsUtils, Filer filerUtils,
			Types typesUtils, Messager messageUtils, TypeElement provideMatchersTE, TypeElement objectTE) {
		Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(ProvideMatchers.class);
		ProvidesMatchersElementVisitor providesMatchersElementVisitor = new ProvidesMatchersElementVisitor(this,
				elementsUtils, messageUtils, provideMatchersTE);
		factories.addAll(elements.stream().filter(e -> roundEnv.getRootElements().contains(e))
				.map(e -> e.accept(providesMatchersElementVisitor, null)).filter(Optional::isPresent)
				.map(te -> processOneTypeElement(elementsUtils, filerUtils, typesUtils, messageUtils, te.get(),
						objectTE, elements))
				.collect(Collectors.toList()));
	}

	private String toJavaSyntax(String unformatted) {
		StringBuilder sb = new StringBuilder();
		sb.append('"');
		for (char c : unformatted.toCharArray()) {
			sb.append(toJavaSyntax(c));
		}
		sb.append('"');
		return sb.toString();
	}

	private String toJavaSyntax(char ch) {
		switch (ch) {
		case '"':
			return "\\\"";
		case '\n':
			return "\\n";
		case '\r':
			return "\\r";
		case '\t':
			return "\\t";
		default:
			return "" + ch;
		}
	}

	private Predicate<Element> isInSameRound(Set<? extends Element> elements, Types typesUtils) {
		return t -> t == null ? false
				: elements.stream().filter(e -> typesUtils.isSameType(e.asType(), t.asType())).findAny().isPresent();
	}

	/**
	 * @param elementsUtils
	 * @param filerUtils
	 * @param messageUtils
	 * @param te
	 * @param objectTE
	 * @param elements
	 */
	private String processOneTypeElement(Elements elementsUtils, Filer filerUtils, Types typesUtils,
			Messager messageUtils, TypeElement te, TypeElement objectTE, Set<? extends Element> elements) {
		Predicate<Element> isInSameRound = isInSameRound(elements, typesUtils);
		StringBuilder factories = new StringBuilder();
		Name inputClassName = te.getQualifiedName();
		String packageName = elementsUtils.getPackageOf(te).getQualifiedName().toString();
		String outputClassName = inputClassName + "Matchers";
		String outputSimpleName = te.getSimpleName().toString() + "Matchers";
		ProvideMatchers pm = te.getAnnotation(ProvideMatchers.class);
		if (!"".equals(pm.matchersClassName())) {
			outputClassName = outputClassName.replaceAll(outputSimpleName + "$", pm.matchersClassName());
			outputSimpleName = pm.matchersClassName();
		}
		if (!"".equals(pm.matchersPackageName())) {
			outputClassName = outputClassName.replaceAll("^" + packageName, pm.matchersPackageName());
			packageName = pm.matchersPackageName();
		}
		String shortClassName = te.getSimpleName().toString();
		String methodShortClassName = shortClassName.substring(0, 1).toLowerCase() + shortClassName.substring(1);
		boolean hasParent = !objectTE.asType().equals(te.getSuperclass());
		boolean hasParentInSameRound = isInSameRound.test(te);
		String generic = "";
		String fullGeneric = "";
		if (te.getTypeParameters().size() > 0) {
			generic = "<" + te.getTypeParameters().stream().map(t -> t.toString()).collect(Collectors.joining(","))
					+ ">";
			fullGeneric = "<" + te.getTypeParameters().stream()
					.map(t -> t.toString() + " extends "
							+ t.getBounds().stream().map(b -> b.toString()).collect(Collectors.joining("&")))
					.collect(Collectors.joining(",")) + ">";
		} else {

		}
		try {
			messageUtils.printMessage(Kind.NOTE,
					"The class `" + outputClassName + "` will be generated as a Matchers class.", te);
			JavaFileObject jfo = filerUtils.createSourceFile(outputClassName, te);
			try (PrintWriter wjfo = new PrintWriter(jfo.openWriter());) {
				wjfo.println("package " + packageName + ";");
				wjfo.println();
				wjfo.println("/**");
				wjfo.println(
						" * This class provides matchers for the class {@link " + inputClassName.toString() + "}.");
				wjfo.println(" * ");
				wjfo.println(" * @see " + inputClassName.toString() + " The class for which matchers are provided.");
				wjfo.println(" */");
				wjfo.println("@javax.annotation.Generated(value=\""
						+ ProvidesMatchersAnnotationsProcessor.class.getName() + "\",date=\"" + Instant.now().toString()
						+ "\",comments=" + toJavaSyntax(pm.comments()) + ")");
				wjfo.println("public final class " + outputSimpleName + " {");
				wjfo.println();
				wjfo.println("  private " + outputSimpleName + "() {}");
				wjfo.println();
				List<FieldDescription> fields = generateAndExtractFieldAndParentPrivateMatcher(elementsUtils,
						filerUtils, typesUtils, messageUtils, te, inputClassName.toString(), shortClassName, hasParent,
						generic, fullGeneric, wjfo, isInSameRound);
				generatePublicInterface(inputClassName, shortClassName, generic, fullGeneric, wjfo, fields,
						elementsUtils, te);
				wjfo.println();
				generatePrivateImplementation(te, inputClassName, shortClassName, hasParent, generic, fullGeneric, wjfo,
						fields);

				wjfo.println();

				factories.append(generateDSLStarter(packageName, outputSimpleName, typesUtils, te, inputClassName,
						shortClassName, methodShortClassName, hasParent, hasParentInSameRound, generic, fullGeneric,
						wjfo, fields, elementsUtils));
				wjfo.println("}");
			}
		} catch (IOException e1) {
			messageUtils.printMessage(Kind.ERROR, "Unable to create the file containing the target class", te);
		}
		return factories.toString();
	}

	private String extractParamFromJavadoc(String docComment) {
		if (docComment == null) {
			return " * \n";
		}
		boolean insideParam = false;
		StringBuilder sb = new StringBuilder();
		sb.append("   * \n");
		for (String line : docComment.split("\\R")) {
			if (insideParam && line.matches("^\\s*@.*$")) {
				insideParam = false;
			}
			if (line.matches("^\\s*@param.*$")) {
				insideParam = true;
			}
			if (insideParam) {
				sb.append("   *" + line).append("\n");
			}
		}
		return sb.toString();
	}

	private List<FieldDescription> generateAndExtractFieldAndParentPrivateMatcher(Elements elementsUtils,
			Filer filerUtils, Types typesUtils, Messager messageUtils, TypeElement te, String fullClassName,
			String shortClassName, boolean hasParent, String generic, String fullGeneric, PrintWriter wjfo,
			Predicate<Element> isInSameRound) {
		ProvidesMatchersSubElementVisitor providesMatchersSubElementVisitor = new ProvidesMatchersSubElementVisitor(
				elementsUtils, typesUtils, messageUtils, isInSameRound);
		List<FieldDescription> fields = te.getEnclosedElements().stream()
				.map(ie -> ie.accept(providesMatchersSubElementVisitor, null)).filter(Optional::isPresent)
				.map(t -> t.get()).collect(Collectors.toList());
		wjfo.println(fields.stream()
				.map(f -> f.getMatcherForField(fullClassName, shortClassName, generic, fullGeneric, "  "))
				.collect(Collectors.joining("\n")));
		if (hasParent) {
			wjfo.println(
					"  private static class SuperClassMatcher" + fullGeneric + " extends org.hamcrest.FeatureMatcher<"
							+ fullClassName + "," + te.getSuperclass().toString() + "> {");
			wjfo.println();
			wjfo.println("    public SuperClassMatcher(org.hamcrest.Matcher<? super " + te.getSuperclass().toString()
					+ "> matcher) {");
			wjfo.println("      super(matcher,\"parent\",\"parent\");");
			wjfo.println("  }");
			wjfo.println();
			wjfo.println("    protected " + te.getSuperclass().toString() + " featureValueOf(" + shortClassName
					+ " actual) {");
			wjfo.println("      return actual;");
			wjfo.println("    }");
			wjfo.println();
			wjfo.println("  }");
			wjfo.println();
			wjfo.println();
		}
		return fields;
	}

	private String generateDSLStarter(String packageName, String outputSimpleName, Types typesUtils, TypeElement te,
			Name inputClassName, String shortClassName, String methodShortClassName, boolean hasParent,
			boolean hasParentInSameRound, String generic, String fullGeneric, PrintWriter wjfo,
			List<FieldDescription> fields, Elements elementsUtils) {
		StringBuilder factories = new StringBuilder();
		generateDefaultDSLStarter(packageName, outputSimpleName, te, inputClassName, shortClassName,
				methodShortClassName, hasParent, generic, fullGeneric, wjfo, elementsUtils, factories);

		generateDefaultForChainingDSLStarter(te, inputClassName, shortClassName, methodShortClassName, hasParent,
				generic, fullGeneric, wjfo, elementsUtils);

		if (hasParent) {
			generateParentDSLStarter(packageName, outputSimpleName, te, inputClassName, shortClassName,
					methodShortClassName, generic, fullGeneric, wjfo, elementsUtils, factories);
		}

		wjfo.println();

		if (!hasParent) {
			generateNoParentDSLStarter(packageName, outputSimpleName, te, inputClassName, shortClassName,
					methodShortClassName, generic, fullGeneric, wjfo, fields, elementsUtils, factories);
		}
		if (hasParent && hasParentInSameRound) {
			generateParentInSameRoundDSLStarter(packageName, outputSimpleName, typesUtils, te, inputClassName,
					shortClassName, methodShortClassName, generic, fullGeneric, wjfo, fields, elementsUtils, factories);
		}
		return factories.toString();
	}

	private void generateParentInSameRoundDSLStarter(String packageName, String outputSimpleName, Types typesUtils,
			TypeElement te, Name inputClassName, String shortClassName, String methodShortClassName, String generic,
			String fullGeneric, PrintWriter wjfo, List<FieldDescription> fields, Elements elementsUtils,
			StringBuilder factories) {
		generateParentInSameRoundSameValueDSLStarter(packageName, outputSimpleName, typesUtils, te, inputClassName,
				shortClassName, methodShortClassName, generic, fullGeneric, wjfo, fields, elementsUtils, factories);

		TypeElement typeElementParent = (TypeElement) typesUtils.asElement(te.getSuperclass());
		if (typeElementParent.getTypeParameters().isEmpty()) {
			generateParentInSameRoundWithChaningDSLStarter(packageName, outputSimpleName, typesUtils, te,
					inputClassName, shortClassName, methodShortClassName, generic, fullGeneric, wjfo, elementsUtils,
					factories, typeElementParent);
		}
	}

	private void generateParentInSameRoundWithChaningDSLStarter(String packageName, String outputSimpleName,
			Types typesUtils, TypeElement te, Name inputClassName, String shortClassName, String methodShortClassName,
			String generic, String fullGeneric, PrintWriter wjfo, Elements elementsUtils, StringBuilder factories,
			TypeElement typeElementParent) {
		StringBuilder javadoc = new StringBuilder();
		javadoc.append("  /**").append("\n");
		javadoc.append("   * Start a DSL matcher for the {@link " + inputClassName + " " + shortClassName + "}.")
				.append("\n");
		javadoc.append("   * ").append("\n");
		javadoc.append("   * @return the DSL matcher.").append("\n");
		javadoc.append(extractParamFromJavadoc(elementsUtils.getDocComment(te)));
		javadoc.append("   */").append("\n");
		wjfo.println(javadoc.toString());
		factories.append(javadoc.toString());
		wjfo.println("  @org.hamcrest.Factory");
		String pname = typesUtils.asElement(te.getSuperclass()).getSimpleName().toString();
		String fpname = typeElementParent.getQualifiedName().toString() + "Matchers";
		ProvideMatchers panntation = typeElementParent.getAnnotation(ProvideMatchers.class);
		if (!"".equals(panntation.matchersClassName())) {
			fpname = fpname.replaceAll(pname + "Matchers$", panntation.matchersClassName());
		}
		if (!"".equals(panntation.matchersPackageName())) {
			fpname = fpname.replaceAll("^([^.]+\\.)+", panntation.matchersPackageName() + ".");
		}
		wjfo.println(
				"  public static " + fullGeneric + " " + fpname + "." + typeElementParent.getSimpleName() + "Matcher"
						+ addParentToGeneric(generic).replaceAll("^<_PARENT",
								"<" + shortClassName + "Matcher" + addNoParentToGeneric(generic))
						+ " " + methodShortClassName + "WithParent() {");
		wjfo.println(
				"    " + shortClassName + "MatcherImpl" + addNoParentToGeneric(generic) + " m=new " + shortClassName
						+ "MatcherImpl" + addNoParentToGeneric(generic) + "(org.hamcrest.Matchers.anything());");

		wjfo.println("    " + fpname + "." + pname + "Matcher tmp = " + fpname + "."
				+ pname.substring(0, 1).toLowerCase() + pname.substring(1) + "WithParent(m);");
		wjfo.println("    m._parent = new SuperClassMatcher(tmp);");
		wjfo.println("    return tmp;");
		wjfo.println("  }");

		factories.append("  default " + fullGeneric + " " + fpname + "." + typeElementParent.getSimpleName() + "Matcher"
				+ addParentToGeneric(generic).replaceAll("^<_PARENT",
						"<" + packageName + "." + outputSimpleName + "." + shortClassName + "Matcher"
								+ addNoParentToGeneric(generic))
				+ " " + methodShortClassName + "WithParent()" + " {").append("\n");
		factories.append(
				"    return " + packageName + "." + outputSimpleName + "." + methodShortClassName + "WithParent();")
				.append("\n");
		factories.append("  }").append("\n");
	}

	private void generateParentInSameRoundSameValueDSLStarter(String packageName, String outputSimpleName,
			Types typesUtils, TypeElement te, Name inputClassName, String shortClassName, String methodShortClassName,
			String generic, String fullGeneric, PrintWriter wjfo, List<FieldDescription> fields, Elements elementsUtils,
			StringBuilder factories) {
		StringBuilder javadoc = new StringBuilder();
		javadoc.append("  /**").append("\n");
		javadoc.append("   * Start a DSL matcher for the {@link " + inputClassName + " " + shortClassName + "}.")
				.append("\n");
		javadoc.append("   * ").append("\n");
		javadoc.append("   * @param other the other object to be used as a reference.").append("\n");
		javadoc.append("   * @return the DSL matcher.").append("\n");
		javadoc.append(extractParamFromJavadoc(elementsUtils.getDocComment(te)));
		javadoc.append("   */").append("\n");
		wjfo.println(javadoc.toString());
		factories.append(javadoc.toString());
		wjfo.println("  @org.hamcrest.Factory");
		String pname = typesUtils.asElement(te.getSuperclass()).getSimpleName().toString();
		TypeElement typeElementParent = (TypeElement) typesUtils.asElement(te.getSuperclass());
		String fpname = typeElementParent.getQualifiedName().toString() + "Matchers";
		ProvideMatchers panntation = typeElementParent.getAnnotation(ProvideMatchers.class);
		if (!"".equals(panntation.matchersClassName())) {
			fpname = fpname.replaceAll(pname + "Matchers$", panntation.matchersClassName());
		}
		if (!"".equals(panntation.matchersPackageName())) {
			fpname = fpname.replaceAll("^([^.]+\\.)+", panntation.matchersPackageName() + ".");
		}
		wjfo.println("  public static " + fullGeneric + " " + shortClassName + "Matcher" + addNoParentToGeneric(generic)
				+ " " + methodShortClassName + "WithSameValue(" + shortClassName + " " + generic + " other) {");
		wjfo.println("    " + shortClassName + "Matcher" + addNoParentToGeneric(generic) + " m=new " + shortClassName
				+ "MatcherImpl" + addNoParentToGeneric(generic) + "(" + fpname + "."
				+ pname.substring(0, 1).toLowerCase() + pname.substring(1) + "WithSameValue(other));");

		fields.stream().map(
				f -> "    m." + f.getFieldName() + "(org.hamcrest.Matchers.is(other." + f.getFieldAccessor() + "));")
				.forEach(wjfo::println);
		wjfo.println("    return m;");
		wjfo.println("  }");

		factories.append("  default " + fullGeneric + " " + packageName + "." + outputSimpleName + "." + shortClassName
				+ "Matcher" + addNoParentToGeneric(generic) + " " + methodShortClassName + "WithSameValue("
				+ packageName + "." + shortClassName + " " + generic + " other)" + " {").append("\n");
		factories.append("    return " + packageName + "." + outputSimpleName + "." + methodShortClassName
				+ "WithSameValue(other);").append("\n");
		factories.append("  }").append("\n");
	}

	private void generateNoParentDSLStarter(String packageName, String outputSimpleName, TypeElement te,
			Name inputClassName, String shortClassName, String methodShortClassName, String generic, String fullGeneric,
			PrintWriter wjfo, List<FieldDescription> fields, Elements elementsUtils, StringBuilder factories) {
		StringBuilder javadoc = new StringBuilder();
		javadoc.append("  /**").append("\n");
		javadoc.append("   * Start a DSL matcher for the {@link " + inputClassName + " " + shortClassName + "}.")
				.append("\n");
		javadoc.append("   * ").append("\n");
		javadoc.append("   * @param other the other object to be used as a reference.").append("\n");
		javadoc.append("   * @return the DSL matcher.").append("\n");
		javadoc.append(extractParamFromJavadoc(elementsUtils.getDocComment(te)));
		javadoc.append("   */").append("\n");
		wjfo.println(javadoc.toString());
		factories.append(javadoc.toString());
		wjfo.println("  @org.hamcrest.Factory");
		wjfo.println("  public static " + fullGeneric + " " + shortClassName + "Matcher" + addNoParentToGeneric(generic)
				+ " " + methodShortClassName + "WithSameValue(" + inputClassName.toString() + " " + generic
				+ " other) {");
		wjfo.println("    " + shortClassName + "Matcher" + addNoParentToGeneric(generic) + " m=new " + shortClassName
				+ "MatcherImpl" + addNoParentToGeneric(generic) + "();");

		fields.stream().map(
				f -> "    m." + f.getFieldName() + "(org.hamcrest.Matchers.is(other." + f.getFieldAccessor() + "));")
				.forEach(wjfo::println);
		wjfo.println("    return m;");
		wjfo.println("  }");

		factories.append("  default " + fullGeneric + " " + packageName + "." + outputSimpleName + "." + shortClassName
				+ "Matcher" + addNoParentToGeneric(generic) + " " + methodShortClassName + "WithSameValue("
				+ inputClassName.toString() + " " + generic + " other)" + " {").append("\n");
		factories.append("    return " + packageName + "." + outputSimpleName + "." + methodShortClassName
				+ "WithSameValue(other);").append("\n");
		factories.append("  }").append("\n");
	}

	private void generateParentDSLStarter(String packageName, String outputSimpleName, TypeElement te,
			Name inputClassName, String shortClassName, String methodShortClassName, String generic, String fullGeneric,
			PrintWriter wjfo, Elements elementsUtils, StringBuilder factories) {
		StringBuilder javadoc = new StringBuilder();
		javadoc.append("  /**").append("\n");
		javadoc.append("   * Start a DSL matcher for the {@link " + inputClassName + " " + shortClassName + "}.")
				.append("\n");
		javadoc.append("   * ").append("\n");
		javadoc.append("   * @param matcherOnParent the matcher on the parent data.").append("\n");
		javadoc.append("   * @return the DSL matcher.").append("\n");
		javadoc.append(extractParamFromJavadoc(elementsUtils.getDocComment(te)));
		javadoc.append("   */").append("\n");
		wjfo.println(javadoc.toString());
		factories.append(javadoc.toString());

		wjfo.println("  @org.hamcrest.Factory");
		wjfo.println("  public static " + fullGeneric + " " + shortClassName + "Matcher" + addNoParentToGeneric(generic)
				+ " " + methodShortClassName + "With(org.hamcrest.Matcher<? super " + te.getSuperclass().toString()
				+ "> matcherOnParent) {");
		wjfo.println("    return new " + shortClassName + "MatcherImpl" + addNoParentToGeneric(generic)
				+ "(matcherOnParent);");
		wjfo.println("  }");

		factories.append("  default " + fullGeneric + " " + packageName + "." + outputSimpleName + "." + shortClassName
				+ "Matcher" + addNoParentToGeneric(generic) + " " + methodShortClassName
				+ "With(org.hamcrest.Matcher<? super " + te.getSuperclass().toString() + "> matcherOnParent)" + " {")
				.append("\n");
		factories.append("    return " + packageName + "." + outputSimpleName + "." + methodShortClassName
				+ "With(matcherOnParent);").append("\n");
		factories.append("  }").append("\n");
	}

	private void generateDefaultForChainingDSLStarter(TypeElement te, Name inputClassName, String shortClassName,
			String methodShortClassName, boolean hasParent, String generic, String fullGeneric, PrintWriter wjfo,
			Elements elementsUtils) {
		StringBuilder javadoc = new StringBuilder();
		String methodName = addParentToGeneric(fullGeneric) + " " + shortClassName + "Matcher"
				+ addParentToGeneric(generic) + " " + methodShortClassName + "WithParent(_PARENT parentBuilder)";
		javadoc.append("  /**").append("\n");
		javadoc.append("   * Start a DSL matcher for the {@link " + inputClassName + " " + shortClassName + "}.")
				.append("\n");
		javadoc.append("   * <p>").append("\n");
		javadoc.append(
				"   * The returned builder (which is also a Matcher), at this point accepts any object that is a {@link "
						+ inputClassName + " " + shortClassName + "}.")
				.append("\n");
		javadoc.append("   * ").append("\n");
		javadoc.append("   * @param parentBuilder the parentBuilder.").append("\n");
		javadoc.append("   * @return the DSL matcher.").append("\n");
		javadoc.append(extractParamFromJavadoc(elementsUtils.getDocComment(te)));
		javadoc.append("   * @param <_PARENT> The parent builder").append("\n");
		javadoc.append("   */").append("\n");
		wjfo.println(javadoc.toString());

		wjfo.println("  public static " + methodName + " {");
		if (hasParent) {
			wjfo.println("    return new " + shortClassName + "MatcherImpl" + addParentToGeneric(generic)
					+ "(org.hamcrest.Matchers.anything(),parentBuilder);");
		} else {
			wjfo.println("    return new " + shortClassName + "MatcherImpl" + addParentToGeneric(generic)
					+ "(parentBuilder);");
		}
		wjfo.println("  }");
	}

	private void generateDefaultDSLStarter(String packageName, String outputSimpleName, TypeElement te,
			Name inputClassName, String shortClassName, String methodShortClassName, boolean hasParent, String generic,
			String fullGeneric, PrintWriter wjfo, Elements elementsUtils, StringBuilder factories) {
		StringBuilder javadoc = new StringBuilder();
		String methodName = fullGeneric + " " + shortClassName + "Matcher" + addNoParentToGeneric(generic) + " "
				+ methodShortClassName + "With()";
		javadoc.append("  /**").append("\n");
		javadoc.append("   * Start a DSL matcher for the {@link " + inputClassName + " " + shortClassName + "}.")
				.append("\n");
		javadoc.append("   * <p>").append("\n");
		javadoc.append(
				"   * The returned builder (which is also a Matcher), at this point accepts any object that is a {@link "
						+ inputClassName + " " + shortClassName + "}.")
				.append("\n");
		javadoc.append("   * ").append("\n");
		javadoc.append("   * @return the DSL matcher.").append("\n");
		javadoc.append(extractParamFromJavadoc(elementsUtils.getDocComment(te)));
		javadoc.append("   */").append("\n");
		wjfo.println(javadoc.toString());
		factories.append(javadoc.toString());

		wjfo.println("  @org.hamcrest.Factory");
		wjfo.println("  public static " + methodName + " {");
		factories
				.append("  default " + fullGeneric + " " + packageName + "." + outputSimpleName + "." + shortClassName
						+ "Matcher" + addNoParentToGeneric(generic) + " " + methodShortClassName + "With()" + " {")
				.append("\n");
		factories.append("    return " + packageName + "." + outputSimpleName + "." + methodShortClassName + "With();")
				.append("\n");
		factories.append("  }").append("\n");
		if (hasParent) {
			wjfo.println("    return new " + shortClassName + "MatcherImpl" + addNoParentToGeneric(generic)
					+ "(org.hamcrest.Matchers.anything());");
		} else {
			wjfo.println("    return new " + shortClassName + "MatcherImpl" + addNoParentToGeneric(generic) + "();");
		}
		wjfo.println("  }");
	}

	private String generateMethodReturn(List<FieldDescription> fields, String fullClassName, String shortClassName,
			String generic) {
		return shortClassName + "Matcher" + addParentToGeneric(generic) + " ";
	}

	private void generatePrivateImplementation(TypeElement te, Name inputClassName, String shortClassName,
			boolean hasParent, String generic, String fullGeneric, PrintWriter wjfo, List<FieldDescription> fields) {
		wjfo.println("  /* package protected */ static class " + shortClassName + "MatcherImpl"
				+ addParentToGeneric(fullGeneric) + " extends org.hamcrest.TypeSafeDiagnosingMatcher<"
				+ inputClassName.toString() + generic + "> implements " + shortClassName + "Matcher"
				+ addParentToGeneric(generic) + " {");
		fields.stream().map(f -> "    private " + f.getMethodFieldName() + "Matcher " + f.getFieldName() + " = new "
				+ f.getMethodFieldName() + "Matcher(org.hamcrest.Matchers.anything());").forEach(wjfo::println);
		wjfo.println("    private final _PARENT _parentBuilder;");
		if (hasParent) {
			wjfo.println("    private SuperClassMatcher _parent;");
			wjfo.println();
			wjfo.println("    public " + shortClassName + "MatcherImpl(org.hamcrest.Matcher<? super "
					+ te.getSuperclass().toString() + "> parent) {");
			wjfo.println("      this._parent=new SuperClassMatcher(parent);");
			wjfo.println("      this._parentBuilder=null;");
			wjfo.println("    }");
			wjfo.println();
			wjfo.println();
			wjfo.println("    public " + shortClassName + "MatcherImpl(org.hamcrest.Matcher<? super "
					+ te.getSuperclass().toString() + "> parent,_PARENT parentBuilder) {");
			wjfo.println("      this._parent=new SuperClassMatcher(parent);");
			wjfo.println("      this._parentBuilder=parentBuilder;");
			wjfo.println("    }");
			wjfo.println();
		} else {
			wjfo.println();
			wjfo.println("    public " + shortClassName + "MatcherImpl() {");
			wjfo.println("      this._parentBuilder=null;");
			wjfo.println("    }");
			wjfo.println();
			wjfo.println();
			wjfo.println("    public " + shortClassName + "MatcherImpl(_PARENT parentBuilder) {");
			wjfo.println("      this._parentBuilder=parentBuilder;");
			wjfo.println("    }");
			wjfo.println();
		}

		String returnMethod = generateMethodReturn(fields, inputClassName.toString(), shortClassName, generic);
		wjfo.println(
				fields.stream().map(f -> f.getImplementationInterface(inputClassName.toString(), returnMethod, "    "))
						.collect(Collectors.joining("\n")));

		wjfo.println("    @Override");
		wjfo.println("    protected boolean matchesSafely(" + inputClassName.toString()
				+ " actual, org.hamcrest.Description mismatchDescription) {");
		wjfo.println("      boolean result=true;");
		if (hasParent) {
			wjfo.println("      if(!_parent.matches(actual)) {");
			wjfo.println(
					"        mismatchDescription.appendText(\"[\"); _parent.describeMismatch(actual,mismatchDescription); mismatchDescription.appendText(\"]\\n\");");
			wjfo.println("        result=false;");
			wjfo.println("      }");
		}
		for (FieldDescription f : fields) {
			wjfo.println("      if(!" + f.getFieldName() + ".matches(actual)) {");
			wjfo.println("        mismatchDescription.appendText(\"[\"); " + f.getFieldName()
					+ ".describeMismatch(actual,mismatchDescription); mismatchDescription.appendText(\"]\\n\");");
			wjfo.println("        result=false;");
			wjfo.println("      }");
		}
		wjfo.println("      return result;");
		wjfo.println("    }");
		wjfo.println();
		wjfo.println("    @Override");
		wjfo.println("    public void describeTo(org.hamcrest.Description description) {");
		wjfo.println("        description.appendText(\"an instance of " + inputClassName + " with\\n\");");
		if (hasParent) {
			wjfo.println("        description.appendText(\"[\").appendDescriptionOf(_parent).appendText(\"]\\n\");");
		}
		fields.stream().map(f -> "        description.appendText(\"[\").appendDescriptionOf(" + f.getFieldName()
				+ ").appendText(\"]\\n\");").forEach(wjfo::println);
		wjfo.println("    }");
		wjfo.println();
		wjfo.println("    @Override");
		wjfo.println("    public _PARENT end() {");
		wjfo.println("      return _parentBuilder;");
		wjfo.println("    }");

		wjfo.println("  }");
	}

	private String addParentToGeneric(String generic) {
		if ("".equals(generic)) {
			return "<_PARENT>";
		} else {
			return generic.replaceFirst("<", "<_PARENT,");
		}
	}

	private String addNoParentToGeneric(String generic) {
		if ("".equals(generic)) {
			return "<Void>";
		} else {
			return generic.replaceFirst("<", "<Void,");
		}
	}

	private void generatePublicInterface(Name inputClassName, String shortClassName, String generic, String fullGeneric,
			PrintWriter wjfo, List<FieldDescription> fields, Elements elementsUtils, TypeElement te) {
		wjfo.println("  /**");
		wjfo.println("   * DSL interface for matcher on {@link " + inputClassName + " " + shortClassName + "}.");
		wjfo.print(extractParamFromJavadoc(elementsUtils.getDocComment(te)));
		wjfo.println(
				"   * @param <_PARENT> used to reference, if necessary, a parent for this builder. By default Void is used an indicate no parent builder");
		wjfo.println("   */");
		wjfo.println("  public static interface " + shortClassName + "Matcher" + addParentToGeneric(fullGeneric)
				+ " extends org.hamcrest.Matcher<" + inputClassName.toString() + generic + "> {");
		String returnMethod = generateMethodReturn(fields, inputClassName.toString(), shortClassName, generic);
		wjfo.println(fields.stream().map(f -> f.getDslInterface(inputClassName.toString(), returnMethod, "    "))
				.collect(Collectors.joining("\n")));
		wjfo.println();
		wjfo.println("    /**");
		wjfo.println("     * Method that return the parent builder.");
		wjfo.println("     * <p>");
		wjfo.println(
				"     * <b>This method only works in the contexte of a parent builder. If the real type is Void, then nothing will be returned.</b>");
		wjfo.println("     * @return the parent builder or null if not applicable");
		wjfo.println("     */");
		wjfo.println("    _PARENT end();");
		wjfo.println("  }");
	}

	AnnotationMirror getProvideMatchersAnnotation(TypeElement provideMatchersTE,
			Collection<? extends AnnotationMirror> annotations) {
		return annotations.stream().filter(a -> a.getAnnotationType().equals(provideMatchersTE.asType())).findAny()
				.orElse(null);
	}
}
