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
import static ch.powerunit.extensions.matchers.common.CommonUtils.generateGeneratedAnnotation;
import static ch.powerunit.extensions.matchers.common.CommonUtils.traceErrorAndDump;
import static ch.powerunit.extensions.matchers.common.FileObjectHelper.processFileWithIOExceptionAndResult;
import static ch.powerunit.extensions.matchers.provideprocessor.dsl.DSLMethod.of;
import static java.util.Collections.unmodifiableList;
import static java.util.stream.Collectors.toList;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic.Kind;

import ch.powerunit.extensions.matchers.provideprocessor.dsl.DSLMethod;

public class ProvidesMatchersAnnotatedElementMirror extends ProvidesMatchersAnnotatedElementMatcherMirror {

	private final Collection<Supplier<DSLMethod>> dslProvider;

	public ProvidesMatchersAnnotatedElementMirror(TypeElement typeElement, RoundMirror roundMirror) {
		super(typeElement, roundMirror);
		boolean hasParentInSameRound = roundMirror.isInSameRound(typeElement);
		List<Supplier<DSLMethod>> tmp = new ArrayList<>(
				Arrays.asList(this::generateDefaultDSLStarter, this::generateDefaultForChainingDSLStarter));
		if (fullyQualifiedNameOfSuperClassOfClassAnnotated.isPresent()) {
			tmp.add(this::generateParentDSLStarter);
			if (hasParentInSameRound) {
				tmp.add(this::generateParentValueDSLStarter);
				if (((TypeElement) roundMirror.getTypeUtils().asElement(element.getSuperclass())).getTypeParameters()
						.isEmpty()) {
					tmp.add(this::generateParentInSameRoundWithChaningDSLStarter);
				}
			}
		} else {
			tmp.add(() -> generatParentValueDSLStarter(""));
		}
		tmp.addAll(Optional.ofNullable(getDSLExtension()).orElseGet(Collections::emptyList).stream()
				.map(t -> t.getDSLMethodFor(() -> this)).flatMap(Collection::stream).collect(toList()));
		tmp.addAll(roundMirror.getDSLMethodFor(() -> this));
		this.dslProvider = unmodifiableList(tmp);
	}

	public Collection<DSLMethod> process() {
		Element te = element;
		String simpleName = getSimpleNameOfGeneratedClass();
		return processFileWithIOExceptionAndResult(
				() -> getFiler().createSourceFile(getFullyQualifiedNameOfGeneratedClass(), te),
				jfo -> new PrintWriter(jfo.openWriter()), wjfo -> {
					wjfo.println("package " + getPackageNameOfGeneratedClass() + ";");
					wjfo.println();
					wjfo.println(generateMainJavaDoc());
					wjfo.println(generateGeneratedAnnotation(ProvidesMatchersAnnotationsProcessor.class,
							annotation.get().comments()));
					wjfo.println("public final class " + simpleName + " {");
					wjfo.println();
					wjfo.println("  private " + simpleName + "() {}");
					wjfo.println();
					wjfo.println(generateMatchers());
					wjfo.println();
					wjfo.println(generatePublicInterface());
					wjfo.println();
					wjfo.println(generatePrivateImplementation());
					wjfo.println();
					Collection<DSLMethod> tmp = generateDSLStarter();
					tmp.stream().map(m -> addPrefix("  ", m.asStaticImplementation())).forEach(wjfo::println);
					wjfo.println();
					wjfo.println();
					wjfo.println(generateMetadata());
					wjfo.println("}");
					return tmp;
				}, e -> traceErrorAndDump(getMessager(), getFiler(), e, te));
	}

	public Collection<DSLMethod> generateDSLStarter() {
		return dslProvider.stream().map(Supplier::get).filter(Objects::nonNull).collect(toList());
	}

	public String getDefaultStarterBody(boolean withParentBuilder) {
		String targetImpl = withParentBuilder ? getSimpleNameOfGeneratedImplementationMatcherWithGenericParent()
				: getSimpleNameOfGeneratedImplementationMatcherWithGenericNoParent();
		boolean withSuper = fullyQualifiedNameOfSuperClassOfClassAnnotated.isPresent();
		if (withParentBuilder) {
			return withSuper ? ("return new " + targetImpl + "(org.hamcrest.Matchers.anything(),parentBuilder);")
					: ("return new " + targetImpl + "(parentBuilder);");
		} else {
			return withSuper ? ("return new " + targetImpl + "(org.hamcrest.Matchers.anything());")
					: ("return new " + targetImpl + "();");
		}
	}

	public String generateDefaultJavaDocWithDSLStarter(Optional<String> param, String returnDescription,
			boolean withParent) {
		return generateDefaultJavaDoc(Optional.of(getJavadocForDSLStarter()), param, returnDescription, withParent);
	}

	public String generateDefaultJavaDocWithoutDSLStarter(Optional<String> param, String returnDescription,
			boolean withParent) {
		return generateDefaultJavaDoc(Optional.empty(), param, returnDescription, withParent);
	}

	public DSLMethod generateDefaultDSLStarter() {
		return of(fullGeneric + " " + getFullyQualifiedNameOfGeneratedClass() + "."
				+ getSimpleNameOfGeneratedInterfaceMatcherWithGenericNoParent() + " " + methodShortClassName + "With")
						.withImplementation(getDefaultStarterBody(false))
						.withJavadoc(generateDefaultJavaDocWithDSLStarter(Optional.empty(), "the DSL matcher", false));
	}

	private String getJavadocForDSLStarter() {
		return "The returned builder (which is also a Matcher), at this point accepts any object that is a "
				+ getDefaultLinkForAnnotatedClass() + ".";
	}

	public DSLMethod generateDefaultForChainingDSLStarter() {
		return of(getFullGenericParent() + " " + getFullyQualifiedNameOfGeneratedClass() + "."
				+ getSimpleNameOfGeneratedInterfaceMatcherWithGenericParent() + " " + methodShortClassName
				+ "WithParent").withOneArgument("_PARENT", "parentBuilder")
						.withImplementation(getDefaultStarterBody(true)).withJavadoc(
								generateDefaultJavaDocWithDSLStarter(Optional.of("parentBuilder the parentBuilder."),
										"the DSL matcher", true));
	}

	public DSLMethod generateParentDSLStarter() {
		String mscn = methodShortClassName;
		String fqngc = getFullyQualifiedNameOfGeneratedClass();
		return of(fullGeneric + " " + fqngc + "." + getSimpleNameOfGeneratedInterfaceMatcherWithGenericNoParent() + " "
				+ mscn + "With").withOneArgument(
						"org.hamcrest.Matcher<? super " + fullyQualifiedNameOfSuperClassOfClassAnnotated.get() + ">",
						"matcherOnParent").withImplementation("return " + fqngc + "." + mscn + "With(matcherOnParent);")
						.withJavadoc(generateDefaultJavaDocWithoutDSLStarter(
								Optional.of("matcherOnParent the matcher on the parent data."), "the DSL matcher",
								false));
	}

	public DSLMethod generatParentValueDSLStarter(String argumentForParentBuilder) {
		String genericNoParent = getSimpleNameOfGeneratedInterfaceMatcherWithGenericNoParent();
		String javadoc = generateDefaultJavaDocWithoutDSLStarter(
				Optional.of("other the other object to be used as a reference."), "the DSL matcher", false);
		List<String> lines = new ArrayList<>();
		lines.add(genericNoParent + " m=new " + getSimpleNameOfGeneratedImplementationMatcherWithGenericNoParent() + "("
				+ argumentForParentBuilder + ");");
		lines.addAll(fields.stream().map(f -> f.getFieldCopy("m", "other") + ";").collect(toList()));
		lines.add("return m;");
		return of(fullGeneric + " " + getFullyQualifiedNameOfGeneratedClass() + "." + genericNoParent + " "
				+ methodShortClassName + "WithSameValue")
						.withOneArgument(getFullyQualifiedNameOfClassAnnotatedWithProvideMatcherWithGeneric(), "other")
						.withImplementation(lines).withJavadoc(javadoc);
	}

	public Optional<ProvidesMatchersAnnotatedElementMirror> getParentMirror() {
		RoundMirror rm = roundMirror;
		return Optional.ofNullable(
				rm.getByName(getQualifiedName(((TypeElement) rm.getTypeUtils().asElement(element.getSuperclass())))));
	}

	public DSLMethod generateParentValueDSLStarterWeak() {
		if (allowWeakWithSameValue) {
			Optional<AnnotationMirror> am = getAnnotationMirror();
			Optional<? extends AnnotationValue> av = am.map(a -> a.getElementValues().entrySet().stream()
					.filter(kv -> kv.getKey().getSimpleName().toString().equals("allowWeakWithSameValue"))
					.map(Entry::getValue).findAny().orElse(null));
			getMessager().printMessage(Kind.MANDATORY_WARNING,
					"This class use the option allowWeakWithSameValue and a weak WithSameValue is detected. The generated WithSameValue DSL may not be able to fully control all the field of this class",
					element, am.orElse(null), av.orElse(null));

			return generatParentValueDSLStarter("org.hamcrest.Matchers.anything()");
		}
		return null;
	}

	public DSLMethod generateParentValueDSLStarter() {
		return getParentMirror()
				.map(parentMirror -> generatParentValueDSLStarter(parentMirror.getFullyQualifiedNameOfGeneratedClass()
						+ "." + parentMirror.methodShortClassName + "WithSameValue(other)"))
				.orElse(generateParentValueDSLStarterWeak());
	}

	public DSLMethod generateParentInSameRoundWithChaningDSLStarter() {
		String implGenericNoParent = getSimpleNameOfGeneratedImplementationMatcherWithGenericNoParent();
		return getParentMirror().map(parentMirror -> {
			String pmfqngc = parentMirror.getFullyQualifiedNameOfGeneratedClass();
			String parentSimpleName = parentMirror.simpleNameOfGeneratedInterfaceMatcher;
			return of(fullGeneric + " " + pmfqngc + "." + parentSimpleName + genericForChaining + " "
					+ methodShortClassName + "WithParent")
							.withImplementation(
									implGenericNoParent + " m=new " + implGenericNoParent
											+ "(org.hamcrest.Matchers.anything());",
									pmfqngc + "." + parentSimpleName + " tmp = " + pmfqngc + "."
											+ parentMirror.methodShortClassName + "WithParent(m);",
									"m._parent = new SuperClassMatcher(tmp);", "return tmp;")
							.withJavadoc(generateDefaultJavaDoc(Optional.empty(), Optional.empty(), "the DSL matcher",
									false));
		}).orElse(null);
	}

}
