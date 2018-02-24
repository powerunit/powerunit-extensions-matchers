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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
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
import ch.powerunit.extensions.matchers.provideprocessor.FieldDescription.Type;

/**
 * @author borettim
 *
 */
@SupportedAnnotationTypes({ "ch.powerunit.extensions.matchers.ProvideMatchers" })
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class ProvidesMatchersAnnotationsProcessor extends AbstractProcessor {

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

		Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(ProvideMatchers.class);
		for (Element e : elements) {
			if (!roundEnv.getRootElements().contains(e)) {
				break;
			}
			TypeElement te = e.accept(new ProvidesMatchersElementVisitor(this, elementsUtils, filerUtils, typesUtils,
					messageUtils, provideMatchersTE), null);
			if (te != null) {
				processOneTypeElement(elementsUtils, filerUtils, typesUtils, messageUtils, te, objectTE);
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
	 */
	private void processOneTypeElement(Elements elementsUtils, Filer filerUtils, Types typesUtils,
			Messager messageUtils, TypeElement te, TypeElement objectTE) {
		Name inputClassName = te.getQualifiedName();
		Name packageName = elementsUtils.getPackageOf(te).getQualifiedName();
		String outputClassName = inputClassName + "Matchers";
		String outputSimpleName = te.getSimpleName().toString() + "Matchers";
		String shortClassName = te.getSimpleName().toString();
		String methodShortClassName = shortClassName.substring(0, 1).toLowerCase() + shortClassName.substring(1);
		boolean hasParent = !objectTE.asType().equals(te.getSuperclass());
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
			JavaFileObject jfo = filerUtils.createSourceFile(outputClassName, te);
			try (PrintWriter wjfo = new PrintWriter(jfo.openWriter());) {
				wjfo.println("package " + packageName + ";");
				wjfo.println();
				wjfo.println("/**");
				wjfo.println(" * This class provides matchers for the class {@link " + shortClassName + "}.");
				wjfo.println(" * ");
				wjfo.println(" * @see " + shortClassName + " The class for which matchers are provided.");
				wjfo.println(" */");
				wjfo.println("@javax.annotation.Generated(\"" + ProvidesMatchersAnnotationsProcessor.class.getName()
						+ "\")");
				wjfo.println("public final class " + outputSimpleName + " {");
				wjfo.println();
				wjfo.println("  private " + outputSimpleName + "() {}");
				wjfo.println();
				List<FieldDescription> fields = new ArrayList<>();
				for (Element ie : te.getEnclosedElements()) {
					FieldDescription f = ie.accept(new ProvidesMatchersSubElementVisitor(this, elementsUtils,
							filerUtils, typesUtils, messageUtils, te, generic, fullGeneric, wjfo), null);
					if (f != null) {
						fields.add(f);
					}
				}
				if (hasParent) {
					wjfo.println("  private static class SuperClassMatcher" + fullGeneric
							+ " extends org.hamcrest.FeatureMatcher<" + shortClassName + ","
							+ te.getSuperclass().toString() + "> {");
					wjfo.println();
					wjfo.println("    public SuperClassMatcher(org.hamcrest.Matcher<? super "
							+ te.getSuperclass().toString() + "> matcher) {");
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
				wjfo.println("  /**");
				wjfo.println(
						"   * DSL interface for matcher on {@link " + inputClassName + " " + shortClassName + "}.");
				wjfo.println("   */");
				wjfo.println("  public static interface " + shortClassName + "Matcher" + fullGeneric
						+ " extends org.hamcrest.Matcher<" + shortClassName + generic + "> {");
				for (FieldDescription f : fields) {
					// V1
					wjfo.println("    /**");
					wjfo.println("     * Add a validation on the field " + f.getFieldName() + ".");
					wjfo.println("     *");
					wjfo.println("     * {@link " + inputClassName + "#" + f.getFieldAccessor()
							+ " This field is accessed by using this approach}.");
					wjfo.println("     *");
					wjfo.println("     * @param matcher a Matcher on the field.");
					wjfo.println("     * @return the DSL to continue the construction of the matcher.");
					wjfo.println("     */");
					if (fields.size() == 1) {
						wjfo.println("    org.hamcrest.Matcher<" + shortClassName + generic + "> " + f.getFieldName()
								+ "(org.hamcrest.Matcher<? super " + f.getFieldType() + "> matcher);");
					} else {
						wjfo.println("    " + shortClassName + "Matcher" + generic + " " + f.getFieldName()
								+ "(org.hamcrest.Matcher<? super " + f.getFieldType() + "> matcher);");
					}
					// V2
					wjfo.println("    /**");
					wjfo.println("     * Add a validation on the field " + f.getFieldName() + ".");
					wjfo.println("     *");
					wjfo.println("     * {@link " + inputClassName + "#" + f.getFieldAccessor()
							+ " This field is accessed by using this approach}.");
					wjfo.println("     *");
					wjfo.println(
							"     * @param value an expected value for the field, which will be compared using the is matcher.");
					wjfo.println("     * @return the DSL to continue the construction of the matcher.");
					wjfo.println("     */");
					if (fields.size() == 1) {
						wjfo.println("    org.hamcrest.Matcher<" + shortClassName + generic + "> " + f.getFieldName()
								+ "(" + f.getFieldType() + " value);");
					} else {
						wjfo.println("    " + shortClassName + "Matcher" + generic + " " + f.getFieldName() + "("
								+ f.getFieldType() + " value);");
					}
					// V3
					if (f.getType() == Type.ARRAY) {
						wjfo.println("    /**");
						wjfo.println("     * Add a validation on the field " + f.getFieldName()
								+ " that the array is empty.");
						wjfo.println("     *");
						wjfo.println("     * {@link " + inputClassName + "#" + f.getFieldAccessor()
								+ " This field is accessed by using this approach}.");
						wjfo.println("     *");
						wjfo.println("     * @return the DSL to continue the construction of the matcher.");
						wjfo.println("     */");
						if (fields.size() == 1) {
							wjfo.println("    org.hamcrest.Matcher<" + shortClassName + generic + "> "
									+ f.getFieldName() + "IsEmpty();");
						} else {
							wjfo.println("    " + shortClassName + "Matcher" + generic + " " + f.getFieldName()
									+ "IsEmpty();");
						}
					}
				}
				wjfo.println("  }");
				wjfo.println();
				wjfo.println("  private static class " + shortClassName + "MatcherImpl" + fullGeneric
						+ " extends org.hamcrest.TypeSafeDiagnosingMatcher<" + shortClassName + generic
						+ "> implements " + shortClassName + "Matcher" + generic + " {");
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

				for (FieldDescription f : fields) {
					// V1
					wjfo.println("    @Override");
					if (fields.size() == 1) {
						wjfo.println(
								"    public org.hamcrest.Matcher<" + shortClassName + generic + "> " + f.getFieldName()
										+ "(org.hamcrest.Matcher<? super " + f.getFieldType() + "> matcher) {");
					} else {
						wjfo.println("    public " + shortClassName + "Matcher" + generic + " " + f.getFieldName()
								+ "(org.hamcrest.Matcher<? super " + f.getFieldType() + "> matcher) {");
					}
					wjfo.println("      " + f.getFieldName() + "= new " + f.getMethodFieldName() + "Matcher(matcher);");
					wjfo.println("      return this;");
					wjfo.println("    }");
					wjfo.println();
					// V2
					wjfo.println("    @Override");
					if (fields.size() == 1) {
						wjfo.println("    public org.hamcrest.Matcher<" + shortClassName + generic + "> "
								+ f.getFieldName() + "(" + f.getFieldType() + " value) {");
					} else {
						wjfo.println("    public " + shortClassName + "Matcher" + generic + " " + f.getFieldName() + "("
								+ f.getFieldType() + " value) {");
					}
					wjfo.println("      return " + f.getFieldName() + "(org.hamcrest.Matchers.is(value));");
					wjfo.println("    }");
					wjfo.println();
					// V3
					if (f.getType() == Type.ARRAY) {
						wjfo.println("    @Override");
						if (fields.size() == 1) {
							wjfo.println("    public org.hamcrest.Matcher<" + shortClassName + generic + "> "
									+ f.getFieldName() + "IsEmpty() {");
						} else {
							wjfo.println("    public " + shortClassName + "Matcher" + generic + " " + f.getFieldName()
									+ "IsEmpty() {");
						}
						wjfo.println("      return " + f.getFieldName() + "((org.hamcrest.Matcher)org.hamcrest.Matchers.emptyArray());");
						wjfo.println("    }");
						wjfo.println();
					}
				}
				wjfo.println("    @Override");
				wjfo.println("    protected boolean matchesSafely(" + shortClassName
						+ " actual, org.hamcrest.Description mismatchDescription) {");
				wjfo.println("      boolean result=true;");
				if (hasParent) {
					wjfo.println("      if(!parent.matches(actual)) {");
					wjfo.println("        mismatchDescription.appendText(\"[\");");
					wjfo.println("        parent.describeMismatch(actual,mismatchDescription);");
					wjfo.println("        mismatchDescription.appendText(\"]\\n\");");
					wjfo.println("        result=false;");
					wjfo.println("      }");
				}
				for (FieldDescription f : fields) {
					wjfo.println("      if(!" + f.getFieldName() + ".matches(actual)) {");
					wjfo.println("        mismatchDescription.appendText(\"[\");");
					wjfo.println("        " + f.getFieldName() + ".describeMismatch(actual,mismatchDescription);");
					wjfo.println("        mismatchDescription.appendText(\"]\\n\");");
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
					wjfo.println("        description.appendText(\"[\");");
					wjfo.println("        description.appendDescriptionOf(parent);");
					wjfo.println("        description.appendText(\"]\\n\");");
				}
				for (FieldDescription f : fields) {
					wjfo.println("        description.appendText(\"[\");");
					wjfo.println("        description.appendDescriptionOf(" + f.getFieldName() + ");");
					wjfo.println("        description.appendText(\"]\\n\");");
				}
				wjfo.println("    }");

				wjfo.println("  }");

				wjfo.println();

				wjfo.println("  /**");
				wjfo.println("   * Start a DSL matcher for the {@link " + inputClassName + " " + shortClassName + "}.");
				wjfo.println("   * ");
				wjfo.println("   * @return the DSL matcher.");
				wjfo.println("   */");
				wjfo.println("  @org.hamcrest.Factory");
				wjfo.println("  public static " + fullGeneric + " " + shortClassName + "Matcher" + generic + " "
						+ methodShortClassName + "With() {");
				if (hasParent) {
					wjfo.println("    return new " + shortClassName + "MatcherImpl(org.hamcrest.Matchers.anything());");
				} else {
					wjfo.println("    return new " + shortClassName + "MatcherImpl" + generic + "();");
				}
				wjfo.println("  }");
				if (hasParent) {
					wjfo.println("  /**");
					wjfo.println(
							"   * Start a DSL matcher for the {@link " + inputClassName + " " + shortClassName + "}.");
					wjfo.println("   * ");
					wjfo.println("   * @param matcherOnParent the matcher on the parent data.");
					wjfo.println("   * @return the DSL matcher.");
					wjfo.println("   */");
					wjfo.println("  @org.hamcrest.Factory");
					wjfo.println("  public static " + fullGeneric + " " + shortClassName + "Matcher" + generic + " "
							+ methodShortClassName + "With(org.hamcrest.Matcher<? super "
							+ te.getSuperclass().toString() + "> matcherOnParent) {");
					wjfo.println("    return new " + shortClassName + "MatcherImpl" + generic + "(matcherOnParent);");
					wjfo.println("  }");
				}

				wjfo.println();

				if (!hasParent) {
					wjfo.println("  /**");
					wjfo.println(
							"   * Start a DSL matcher for the {@link " + inputClassName + " " + shortClassName + "}.");
					wjfo.println("   * ");
					wjfo.println("   * @param other the other object to be used as a reference.");
					wjfo.println("   * @return the DSL matcher.");
					wjfo.println("   */");
					wjfo.println("  @org.hamcrest.Factory");
					wjfo.println("  public static " + fullGeneric + " " + shortClassName + "Matcher" + generic + " "
							+ methodShortClassName + "WithSameValue(" + shortClassName + " " + generic + " other) {");
					wjfo.println("    " + shortClassName + "Matcher" + generic + " m=new " + shortClassName
							+ "MatcherImpl" + generic + "();");

					for (FieldDescription f : fields) {
						wjfo.println("    m." + f.getFieldName() + "(org.hamcrest.Matchers.is(other."
								+ f.getFieldAccessor() + "));");
					}
					wjfo.println("    return m;");
					wjfo.println("  }");
				}
				wjfo.println("}");
			}
		} catch (IOException e1) {
			messageUtils.printMessage(Kind.ERROR, "Unable to create the file containing the target class", te);
		}
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
