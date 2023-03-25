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
import static java.util.Collections.singletonList;
import static java.util.Collections.unmodifiableList;
import static java.util.Optional.empty;
import static java.util.Optional.ofNullable;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Formatter;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

import ch.powerunit.extensions.matchers.common.ListJoining;
import ch.powerunit.extensions.matchers.common.RessourceLoaderHelper;
import ch.powerunit.extensions.matchers.provideprocessor.dsl.DSLMethod;
import ch.powerunit.extensions.matchers.provideprocessor.helper.ProvidesMatchersWithSameValueHelper;

public class ProvidesMatchersAnnotatedElementMirror extends ProvidesMatchersAnnotatedElementMatcherMirror {

	private final Collection<Supplier<Collection<DSLMethod>>> dslProvider;

	private static final String MATCHER_FORMAT = RessourceLoaderHelper
			.loadRessource(ProvidesMatchersAnnotatedElementMirror.class, "Matchers.txt");

	private static final String POSTPROCESSOR_FORMAT = addPrefix("  ",
			RessourceLoaderHelper.loadRessource(ProvidesMatchersAnnotatedElementMirror.class, "PostProcessor.txt"));

	private static final ListJoining<DSLMethod> JOIN_DSL_METHOD = ListJoining
			.joinWithMapperAndDelimiter(m -> addPrefix("  ", m.asStaticImplementation()), "\n");

	private static Supplier<Collection<DSLMethod>> asCollection(Supplier<DSLMethod> input) {
		return () -> singletonList(input.get());
	}

	public ProvidesMatchersAnnotatedElementMirror(TypeElement typeElement, RoundMirror roundMirror) {
		super(typeElement, roundMirror);
		List<Supplier<Collection<DSLMethod>>> tmp = new ArrayList<>(Arrays.asList(
				asCollection(this::generateDefaultDSLStarter), asCollection(this::generateDefaultForChainingDSLStarter),
				asCollection(this::generateMatcherClassMethod)));
		if (hasSuperClass()) {
			tmp.add(asCollection(this::generateParentDSLStarter));
			tmp.add(() -> ProvidesMatchersWithSameValueHelper.generateParentValueDSLStarter(this));
			if (((TypeElement) roundMirror.getTypeUtils().asElement(element.getSuperclass())).getTypeParameters()
					.isEmpty()) {
				tmp.add(asCollection(this::generateParentInSameRoundWithChaningDSLStarter));
			}
		} else {
			tmp.add(() -> ProvidesMatchersWithSameValueHelper.generateNoParentValueDSLStarter(this));
		}
		tmp.addAll(ofNullable(getDSLExtension()).orElseGet(Collections::emptyList).stream()
				.map(t -> t.getDSLMethodFor(() -> this)).flatMap(Collection::stream)
				.map(ProvidesMatchersAnnotatedElementMirror::asCollection).toList());
		tmp.addAll(roundMirror.getDSLMethodFor(() -> this).stream()
				.map(ProvidesMatchersAnnotatedElementMirror::asCollection).toList());
		this.dslProvider = unmodifiableList(tmp);
	}

	public Collection<DSLMethod> process() {
		Element te = element;
		return processFileWithIOExceptionAndResult(
				() -> getFiler().createSourceFile(getFullyQualifiedNameOfGeneratedClass(), te),
				jfo -> new Formatter(new PrintWriter(jfo.openWriter())), output -> {
					Collection<DSLMethod> tmp = generateDSLStarter();
					output.format(MATCHER_FORMAT, getPackageNameOfGeneratedClass(),
							getFullyQualifiedNameOfClassAnnotated(),
							generateGeneratedAnnotation(ProvidesMatchersAnnotationsProcessor.class, comments()),
							getSimpleNameOfGeneratedClass(), generateMatchers(), generatePublicInterface(),
							generatePrivateImplementation(), JOIN_DSL_METHOD.asString(tmp), POSTPROCESSOR_FORMAT,
							generateMetadata());
					output.flush();
					if (isDisableGenerationOfFactory() ) {
						return Collections.emptyList();
					}
					return tmp;
				}, e -> traceErrorAndDump(this, e, te));
	}

	public Collection<DSLMethod> generateDSLStarter() {
		return dslProvider.stream().map(Supplier::get).filter(Objects::nonNull).flatMap(Collection::stream)
				.filter(Objects::nonNull).toList();
	}

	public String getDefaultStarterBody(boolean withParentBuilder) {
		String targetImpl = withParentBuilder ? getSimpleNameOfGeneratedImplementationMatcherWithGenericParent()
				: getSimpleNameOfGeneratedImplementationMatcherWithGenericNoParent();
		boolean withSuper = hasSuperClass();
		return "return new " + targetImpl + "(" + (withSuper ? "org.hamcrest.Matchers.anything()" : "")
				+ (withSuper && withParentBuilder ? "," : "") + (withParentBuilder ? "parentBuilder" : "") + ");";
	}

	public String generateDefaultJavaDocWithDSLStarter(Optional<String> param, String returnDescription,
			boolean withParent) {
		return generateDefaultJavaDoc(Optional.of(getJavadocForDSLStarter()), param, returnDescription, withParent);
	}

	public String generateDefaultJavaDocWithoutDSLStarter(Optional<String> param, String returnDescription,
			boolean withParent) {
		return generateDefaultJavaDoc(empty(), param, returnDescription, withParent);
	}

	public DSLMethod generateDefaultDSLStarter() {
		return of(fullGeneric + " " + getFullyQualifiedNameOfGeneratedClass() + "."
				+ getSimpleNameOfGeneratedInterfaceMatcherWithGenericNoParent() + " " + methodShortClassName + "With")
						.withImplementation(getDefaultStarterBody(false))
						.withJavadoc(generateDefaultJavaDocWithDSLStarter(empty(), "the DSL matcher", false));
	}

	public String getJavadocForDSLStarter() {
		return "The returned builder (which is also a Matcher), at this point accepts any object that is a "
				+ getDefaultLinkForAnnotatedClass() + ".";
	}

	public DSLMethod generateDefaultForChainingDSLStarter() {
		return of(getFullGenericParent() + " " + getFullyQualifiedNameOfGeneratedClass() + "."
				+ getSimpleNameOfGeneratedInterfaceMatcherWithGenericParent() + " " + getMethodNameDSLWithParent())
						.withOneArgument("_PARENT", "parentBuilder").withImplementation(getDefaultStarterBody(true))
						.withJavadoc(generateDefaultJavaDocWithDSLStarter(
								Optional.of("parentBuilder the parentBuilder."), "the DSL matcher", true));
	}

	public DSLMethod generateParentDSLStarter() {
		String mscn = methodShortClassName;
		String fqngc = getFullyQualifiedNameOfGeneratedClass();
		return of(fullGeneric + " " + fqngc + "." + getSimpleNameOfGeneratedInterfaceMatcherWithGenericNoParent() + " "
				+ mscn + "With").withOneArgument(
						"org.hamcrest.Matcher<? super " + fullyQualifiedNameOfSuperClassOfClassAnnotated.get() + ">",
						"matcherOnParent")
						.withImplementation(
								"return new " + getSimpleNameOfGeneratedImplementationMatcherWithGenericNoParent()
										+ "(matcherOnParent);")
						.withJavadoc(generateDefaultJavaDocWithoutDSLStarter(
								Optional.of("matcherOnParent the matcher on the parent data."), "the DSL matcher",
								false));
	}

	public DSLMethod generateParentInSameRoundWithChaningDSLStarter() {
		String implGenericNoParent = getSimpleNameOfGeneratedImplementationMatcherWithGenericNoParent();
		return getParentMirror().map(parentMirror -> {
			String pmfqngc = parentMirror.getFullyQualifiedNameOfGeneratedClass();
			String parentSimpleName = parentMirror.getSimpleNameOfGeneratedInterfaceMatcher();
			return of(fullGeneric + " " + pmfqngc + "." + parentSimpleName + genericForChaining + " "
					+ getMethodNameDSLWithParent())
							.withImplementation(
									implGenericNoParent + " m=new " + implGenericNoParent
											+ "(org.hamcrest.Matchers.anything());",
									pmfqngc + "." + parentSimpleName + " tmp = " + pmfqngc + "."
											+ parentMirror.getMethodNameDSLWithParent() + "(m);",
									"m._parent = new SuperClassMatcher(tmp);", "return tmp;")
							.withJavadoc(generateDefaultJavaDoc(empty(), empty(), "the DSL matcher", false));
		}).orElse(null);
	}

	public DSLMethod generateMatcherClassMethod() {
		return of(getFullGenericParent()+" Class<" + getFullyQualifiedNameOfGeneratedClass() + "."
				+ getSimpleNameOfGeneratedInterfaceMatcherWithGenericParent() + "> "
				+ getMethodShortClassName() + "MatcherClass").withoutArgument()
						.withImplementation("return (Class)" + getSimpleNameOfGeneratedInterfaceMatcher() + ".class;")
						.withJavadoc(
								"/**\n * Helper method to retrieve the Class of the matcher interface.\n * @return the class.\n */\n");
	}

}
