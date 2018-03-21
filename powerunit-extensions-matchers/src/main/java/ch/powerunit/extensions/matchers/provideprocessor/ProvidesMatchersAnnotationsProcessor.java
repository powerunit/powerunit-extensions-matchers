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
import java.util.Set;
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
			Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(ProvideMatchers.class);
			for (Element e : elements) {
				if (!roundEnv.getRootElements().contains(e)) {
					break;
				}
				TypeElement te = e.accept(new ProvidesMatchersElementVisitor(this, elementsUtils, filerUtils,
						typesUtils, messageUtils, provideMatchersTE), null);
				if (te != null) {
					factories.add(processOneTypeElement(elementsUtils, filerUtils, typesUtils, messageUtils, te,
							objectTE, elements));
				}
			}
		} else if (factory != null) {
			try {
				messageUtils.printMessage(Kind.NOTE, "The interface `"+factory+"` will be generated as a factory interface.");
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
					wjfo.println("@javax.annotation.Generated(value=\""
							+ ProvidesMatchersAnnotationsProcessor.class.getName() + "\",date=\""
							+ Instant.now().toString() + "\")");
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
				messageUtils.printMessage(Kind.ERROR, "Unable to create the file containing the target class `"
						+ factory + "`, because of " + e1.getMessage());
			}
		}
		return true;
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
		StringBuilder factories = new StringBuilder();
		Name inputClassName = te.getQualifiedName();
		Name packageName = elementsUtils.getPackageOf(te).getQualifiedName();
		String outputClassName = inputClassName + "Matchers";
		String outputSimpleName = te.getSimpleName().toString() + "Matchers";
		ProvideMatchers pm = te.getAnnotation(ProvideMatchers.class);
		if (!"".equals(pm.matchersClassName())) {
			outputClassName=outputClassName.replaceAll(outputSimpleName+"$", pm.matchersClassName());
			outputSimpleName=pm.matchersClassName();
		}
		String shortClassName = te.getSimpleName().toString();
		String methodShortClassName = shortClassName.substring(0, 1).toLowerCase() + shortClassName.substring(1);
		boolean hasParent = !objectTE.asType().equals(te.getSuperclass());
		boolean hasParentInSameRound = elements.stream().filter(e -> typesUtils.isSameType(e.asType(), te.asType()))
				.findAny().isPresent();
		String generic = "";
		String fullGeneric = "";
		if (te.getTypeParameters().size() > 0) {
			generic = "<" + te.getTypeParameters().stream().map(t -> t.toString()).collect(Collectors.joining(","))
					+ ">";
			fullGeneric = "<" + te.getTypeParameters().stream()
					.map(t -> t.toString() + " extends "
							+ t.getBounds().stream().map(b -> b.toString()).collect(Collectors.joining("&")))
					.collect(Collectors.joining(",")) + ">";
		}
		try {
			messageUtils.printMessage(Kind.NOTE, "The class `"+outputClassName+"` will be generated as a Matchers class.",te);
			JavaFileObject jfo = filerUtils.createSourceFile(outputClassName, te);
			try (PrintWriter wjfo = new PrintWriter(jfo.openWriter());) {
				wjfo.println("package " + packageName + ";");
				wjfo.println();
				wjfo.println("/**");
				wjfo.println(" * This class provides matchers for the class {@link " + shortClassName + "}.");
				wjfo.println(" * ");
				wjfo.println(" * @see " + shortClassName + " The class for which matchers are provided.");
				wjfo.println(" */");
				wjfo.println(
						"@javax.annotation.Generated(value=\"" + ProvidesMatchersAnnotationsProcessor.class.getName()
								+ "\",date=\"" + Instant.now().toString() + "\")");
				wjfo.println("public final class " + outputSimpleName + " {");
				wjfo.println();
				wjfo.println("  private " + outputSimpleName + "() {}");
				wjfo.println();
				List<FieldDescription> fields = generateAndExtractFieldAndParentPrivateMatcher(elementsUtils,
						filerUtils, typesUtils, messageUtils, te, shortClassName, hasParent, generic, fullGeneric,
						wjfo);
				generatePublicInterface(inputClassName, shortClassName, generic, fullGeneric, wjfo, fields);
				wjfo.println();
				generatePrivateImplementation(te, inputClassName, shortClassName, hasParent, generic, fullGeneric, wjfo,
						fields);

				wjfo.println();

				factories.append(generateDSLStarter(packageName.toString(), outputSimpleName ,typesUtils, te, inputClassName,
						shortClassName, methodShortClassName, hasParent, hasParentInSameRound, generic, fullGeneric,
						wjfo, fields));
				wjfo.println("}");
			}
		} catch (IOException e1) {
			messageUtils.printMessage(Kind.ERROR, "Unable to create the file containing the target class", te);
		}
		return factories.toString();
	}

	private List<FieldDescription> generateAndExtractFieldAndParentPrivateMatcher(Elements elementsUtils,
			Filer filerUtils, Types typesUtils, Messager messageUtils, TypeElement te, String shortClassName,
			boolean hasParent, String generic, String fullGeneric, PrintWriter wjfo) {
		List<FieldDescription> fields = te.getEnclosedElements()
				.stream().map(ie -> ie
						.accept(new ProvidesMatchersSubElementVisitor(elementsUtils, typesUtils, messageUtils), null))
				.filter(v -> v != null).collect(Collectors.toList());
		wjfo.println(fields.stream().map(f -> f.getMatcherForField(shortClassName, generic, fullGeneric, "  "))
				.collect(Collectors.joining("\n")));
		if (hasParent) {
			wjfo.println(
					"  private static class SuperClassMatcher" + fullGeneric + " extends org.hamcrest.FeatureMatcher<"
							+ shortClassName + "," + te.getSuperclass().toString() + "> {");
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

	private String generateDSLStarter(String packageName,String outputSimpleName, Types typesUtils, TypeElement te, Name inputClassName,
			String shortClassName, String methodShortClassName, boolean hasParent, boolean hasParentInSameRound,
			String generic, String fullGeneric, PrintWriter wjfo, List<FieldDescription> fields) {
		StringBuilder factories = new StringBuilder();
		{
			StringBuilder javadoc = new StringBuilder();
			String methodName = fullGeneric + " " + shortClassName + "Matcher" + generic + " " + methodShortClassName
					+ "With()";
			javadoc.append("  /**").append("\n");
			javadoc.append("   * Start a DSL matcher for the {@link " + inputClassName + " " + shortClassName + "}.")
					.append("\n");
			javadoc.append("   * ").append("\n");
			javadoc.append("   * @return the DSL matcher.").append("\n");
			javadoc.append("   */").append("\n");
			wjfo.println(javadoc.toString());
			factories.append(javadoc.toString());

			wjfo.println("  @org.hamcrest.Factory");
			wjfo.println("  public static " + methodName + " {");
			factories
					.append("  default " + fullGeneric + " " + packageName + "." + outputSimpleName + "."
							+ shortClassName + "Matcher" + generic + " " + methodShortClassName + "With()" + " {")
					.append("\n");
			factories.append(
					"    return " + packageName + "." + outputSimpleName  + "." + methodShortClassName + "With();")
					.append("\n");
			factories.append("  }").append("\n");
			if (hasParent) {
				wjfo.println("    return new " + shortClassName + "MatcherImpl(org.hamcrest.Matchers.anything());");
			} else {
				wjfo.println("    return new " + shortClassName + "MatcherImpl" + generic + "();");
			}
			wjfo.println("  }");
		}
		if (hasParent) {
			StringBuilder javadoc = new StringBuilder();
			javadoc.append("  /**").append("\n");
			javadoc.append("   * Start a DSL matcher for the {@link " + inputClassName + " " + shortClassName + "}.")
					.append("\n");
			javadoc.append("   * ").append("\n");
			javadoc.append("   * @param matcherOnParent the matcher on the parent data.").append("\n");
			javadoc.append("   * @return the DSL matcher.").append("\n");
			javadoc.append("   */").append("\n");
			wjfo.println(javadoc.toString());
			factories.append(javadoc.toString());

			wjfo.println("  @org.hamcrest.Factory");
			wjfo.println("  public static " + fullGeneric + " " + shortClassName + "Matcher" + generic + " "
					+ methodShortClassName + "With(org.hamcrest.Matcher<? super " + te.getSuperclass().toString()
					+ "> matcherOnParent) {");
			wjfo.println("    return new " + shortClassName + "MatcherImpl" + generic + "(matcherOnParent);");
			wjfo.println("  }");

			factories.append("  default " + fullGeneric + " " + packageName + "." + outputSimpleName  + "."
					+ shortClassName + "Matcher" + generic + " " + methodShortClassName
					+ "With(org.hamcrest.Matcher<? super " + te.getSuperclass().toString() + "> matcherOnParent)"
					+ " {").append("\n");
			factories.append("    return " + packageName + "." + outputSimpleName  + "." + methodShortClassName
					+ "With(matcherOnParent);").append("\n");
			factories.append("  }").append("\n");
		}

		wjfo.println();

		if (!hasParent) {
			StringBuilder javadoc = new StringBuilder();
			javadoc.append("  /**").append("\n");
			javadoc.append("   * Start a DSL matcher for the {@link " + inputClassName + " " + shortClassName + "}.")
					.append("\n");
			javadoc.append("   * ").append("\n");
			javadoc.append("   * @param other the other object to be used as a reference.").append("\n");
			javadoc.append("   * @return the DSL matcher.").append("\n");
			javadoc.append("   */").append("\n");
			wjfo.println(javadoc.toString());
			factories.append(javadoc.toString());
			wjfo.println("  @org.hamcrest.Factory");
			wjfo.println("  public static " + fullGeneric + " " + shortClassName + "Matcher" + generic + " "
					+ methodShortClassName + "WithSameValue(" + shortClassName + " " + generic + " other) {");
			wjfo.println("    " + shortClassName + "Matcher" + generic + " m=new " + shortClassName + "MatcherImpl"
					+ generic + "();");

			for (FieldDescription f : fields) {
				wjfo.println("    m." + f.getFieldName() + "(org.hamcrest.Matchers.is(other." + f.getFieldAccessor()
						+ "));");
			}
			wjfo.println("    return m;");
			wjfo.println("  }");

			factories.append("  default " + fullGeneric + " " + packageName + "." + outputSimpleName  + "."
					+ shortClassName + "Matcher" + generic + " " + methodShortClassName + "WithSameValue(" + packageName
					+ "." + shortClassName + " " + generic + " other)" + " {").append("\n");
			factories.append("    return " + packageName + "." + outputSimpleName  + "." + methodShortClassName
					+ "WithSameValue(other);").append("\n");
			factories.append("  }").append("\n");
		}
		if (hasParent && hasParentInSameRound) {
			StringBuilder javadoc = new StringBuilder();
			javadoc.append("  /**").append("\n");
			javadoc.append("   * Start a DSL matcher for the {@link " + inputClassName + " " + shortClassName + "}.")
					.append("\n");
			javadoc.append("   * ").append("\n");
			javadoc.append("   * @param other the other object to be used as a reference.").append("\n");
			javadoc.append("   * @return the DSL matcher.").append("\n");
			javadoc.append("   */").append("\n");
			wjfo.println(javadoc.toString());
			factories.append(javadoc.toString());
			wjfo.println("  @org.hamcrest.Factory");
			String pname = typesUtils.asElement(te.getSuperclass()).getSimpleName().toString();
			TypeElement typeElementParent = (TypeElement)typesUtils.asElement(te.getSuperclass());
			String fpname = typeElementParent.getQualifiedName().toString()+"Matchers";
			ProvideMatchers panntation = typeElementParent.getAnnotation(ProvideMatchers.class);
			if (!"".equals(panntation.matchersClassName())) {
				fpname=fpname.replaceAll(pname+"Matchers$", panntation.matchersClassName());
			}
			wjfo.println("  public static " + fullGeneric + " " + shortClassName + "Matcher" + generic + " "
					+ methodShortClassName + "WithSameValue(" + shortClassName + " " + generic + " other) {");
			wjfo.println("    " + shortClassName + "Matcher" + generic + " m=new " + shortClassName + "MatcherImpl"
					+ generic + "(" + fpname + "."
					+ pname.substring(0, 1).toLowerCase() + pname.substring(1) + "WithSameValue(other));");

			for (FieldDescription f : fields) {
				wjfo.println("    m." + f.getFieldName() + "(org.hamcrest.Matchers.is(other." + f.getFieldAccessor()
						+ "));");
			}
			wjfo.println("    return m;");
			wjfo.println("  }");

			factories.append("  default " + fullGeneric + " " + packageName + "." + outputSimpleName  + "."
					+ shortClassName + "Matcher" + generic + " " + methodShortClassName + "WithSameValue(" + packageName
					+ "." + shortClassName + " " + generic + " other)" + " {").append("\n");
			factories.append("    return " + packageName + "." + outputSimpleName  + "." + methodShortClassName
					+ "WithSameValue(other);").append("\n");
			factories.append("  }").append("\n");
		}
		return factories.toString();
	}

	private String generateMethodReturn(List<FieldDescription> fields, String shortClassName, String generic) {
		if (fields.size() == 1) {
			return "org.hamcrest.Matcher<" + shortClassName + generic + "> ";
		} else {
			return shortClassName + "Matcher" + generic + " ";
		}
	}

	private void generatePrivateImplementation(TypeElement te, Name inputClassName, String shortClassName,
			boolean hasParent, String generic, String fullGeneric, PrintWriter wjfo, List<FieldDescription> fields) {
		wjfo.println("  /* package protected */ static class " + shortClassName + "MatcherImpl" + fullGeneric
				+ " extends org.hamcrest.TypeSafeDiagnosingMatcher<" + shortClassName + generic + "> implements "
				+ shortClassName + "Matcher" + generic + " {");
		for (FieldDescription f : fields) {
			wjfo.println("    private " + f.getMethodFieldName() + "Matcher " + f.getFieldName() + " = new "
					+ f.getMethodFieldName() + "Matcher(org.hamcrest.Matchers.anything());");
		}
		wjfo.println();
		if (hasParent) {
			wjfo.println("    private final SuperClassMatcher parent;");
			wjfo.println();
			wjfo.println("    public " + shortClassName + "MatcherImpl(org.hamcrest.Matcher<? super "
					+ te.getSuperclass().toString() + "> parent) {");
			wjfo.println("      this.parent=new SuperClassMatcher(parent);");
			wjfo.println("    }");
			wjfo.println();
		}

		String returnMethod = generateMethodReturn(fields, shortClassName, generic);
		wjfo.println(
				fields.stream().map(f -> f.getImplementationInterface(inputClassName.toString(), returnMethod, "    "))
						.collect(Collectors.joining("\n")));

		wjfo.println("    @Override");
		wjfo.println("    protected boolean matchesSafely(" + shortClassName
				+ " actual, org.hamcrest.Description mismatchDescription) {");
		wjfo.println("      boolean result=true;");
		if (hasParent) {
			wjfo.println("      if(!parent.matches(actual)) {");
			wjfo.println(
					"        mismatchDescription.appendText(\"[\"); parent.describeMismatch(actual,mismatchDescription); mismatchDescription.appendText(\"]\\n\");");
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
			wjfo.println("        description.appendText(\"[\").appendDescriptionOf(parent).appendText(\"]\\n\");");
		}
		for (FieldDescription f : fields) {
			wjfo.println("        description.appendText(\"[\").appendDescriptionOf(" + f.getFieldName()
					+ ").appendText(\"]\\n\");");
		}
		wjfo.println("    }");

		wjfo.println("  }");
	}

	private void generatePublicInterface(Name inputClassName, String shortClassName, String generic, String fullGeneric,
			PrintWriter wjfo, List<FieldDescription> fields) {
		wjfo.println("  /**");
		wjfo.println("   * DSL interface for matcher on {@link " + inputClassName + " " + shortClassName + "}.");
		wjfo.println("   */");
		wjfo.println("  public static interface " + shortClassName + "Matcher" + fullGeneric
				+ " extends org.hamcrest.Matcher<" + shortClassName + generic + "> {");
		String returnMethod = generateMethodReturn(fields, shortClassName, generic);
		wjfo.println(fields.stream().map(f -> f.getDslInterface(inputClassName.toString(), returnMethod, "    "))
				.collect(Collectors.joining("\n")));
		wjfo.println("  }");
	}

	AnnotationMirror getProvideMatchersAnnotation(TypeElement provideMatchersTE,
			Collection<? extends AnnotationMirror> annotations) {
		for (AnnotationMirror a : annotations) {
			if (a.getAnnotationType().equals(provideMatchersTE.asType())) {
				return a;
			}
		}
		return null;
	}
}
