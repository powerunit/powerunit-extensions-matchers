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

import static ch.powerunit.extensions.matchers.common.CommonUtils.asStandardMethodName;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.function.Function;

import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;

import ch.powerunit.extensions.matchers.api.ProvideMatchers;
import ch.powerunit.extensions.matchers.provideprocessor.extension.DSLExtension;

public class ProvideMatchersMirror extends ProvideMatchersAnnotationMirror implements Matchable {

	private static final String DEFAULT_PARAM_PARENT = " * @param <_PARENT> used to reference, if necessary, a parent for this builder. By default Void is used an indicate no parent builder.\n";

	protected final String simpleNameOfGeneratedClass;
	protected final String packageNameOfGeneratedClass;
	protected final String simpleNameOfGeneratedInterfaceMatcher;

	protected final String methodShortClassName;

	public ProvideMatchersMirror(RoundMirror roundMirror, TypeElement annotatedElement) {
		super(roundMirror, annotatedElement);
		this.simpleNameOfGeneratedClass = generateSimpleNameOfGeneratedClass(annotatedElement);
		this.packageNameOfGeneratedClass = generatePackageNameOfGeneratedClass(annotatedElement,
				getProcessingEnv().getElementUtils());
		this.simpleNameOfGeneratedInterfaceMatcher = getSimpleNameOfClassAnnotated() + "Matcher";
		String simplename = getSimpleNameOfClassAnnotated();
		this.methodShortClassName = asStandardMethodName(simplename);
	}

	private String generateSimpleNameOfGeneratedClass(TypeElement annotatedElement) {
		ProvideMatchers pm = realAnnotation;
		if ("".equals(pm.matchersClassName())) {
			return getSimpleName(annotatedElement) + "Matchers";
		} else {
			return pm.matchersClassName();
		}
	}

	private String generatePackageNameOfGeneratedClass(TypeElement annotatedElement, Elements elements) {
		ProvideMatchers pm = realAnnotation;
		if ("".equals(pm.matchersPackageName())) {
			return getQualifiedName(elements.getPackageOf(annotatedElement));
		} else {
			return pm.matchersPackageName();
		}
	}

	public String getMethodShortClassName() {
		return methodShortClassName;
	}

	public final String getSimpleNameOfGeneratedClass() {
		return simpleNameOfGeneratedClass;
	}

	public final String getFullyQualifiedNameOfGeneratedClass() {
		return packageNameOfGeneratedClass + "." + simpleNameOfGeneratedClass;
	}

	public final String getPackageNameOfGeneratedClass() {
		return packageNameOfGeneratedClass;
	}

	public final Collection<DSLExtension> getDSLExtension() {
		return DSLExtension.EXTENSION.stream().filter(e -> e.accept(moreMethod()))
				.collect(collectingAndThen(toList(), Collections::unmodifiableList));
	}

	private Function<String, String> asJavadocFormat(String prefix) {
		return t -> String.format("%1$s%2$s\n", prefix, t);
	}

	private String paramToJavadoc(Optional<String> param) {
		return param.map(p -> stream(p.split("\n"))).map(s -> s.map(asJavadocFormat(" * @param ")))
				.map(s -> s.collect(joining())).orElse("");
	}

	public String generateDefaultJavaDoc(Optional<String> moreDetails, Optional<String> param, String returnDescription,
			boolean withParent) {
		return String.format("/**\n * %1$s.\n%2$s%3$s%4$s * \n%5$s * @return %6$s\n */\n",
				getDefaultDescriptionForDsl(), moreDetails.map(asJavadocFormat(" * <p>\n * ")).orElse(""),
				paramToJavadoc(param), getParamComment(), withParent ? DEFAULT_PARAM_PARENT : "", returnDescription);
	}

	private String getDefaultDescriptionForDsl() {
		return "Start a DSL matcher for the " + getDefaultLinkForAnnotatedClass();
	}

	public Optional<Matchable> getParentMirror() {
		RoundMirror rm = getRoundMirror();
		return Optional.ofNullable(
				rm.getByName(getQualifiedName(((TypeElement) rm.getTypeUtils().asElement(element.getSuperclass())))));
	}

	public boolean hasWithSameValue() {
		return !hasSuperClass() || getParentMirror().isPresent() || allowWeakWithSameValue;
	}

	/**
	 * @return the simpleNameOfGeneratedInterfaceMatcher
	 */
	public String getSimpleNameOfGeneratedInterfaceMatcher() {
		return simpleNameOfGeneratedInterfaceMatcher;
	}

}
