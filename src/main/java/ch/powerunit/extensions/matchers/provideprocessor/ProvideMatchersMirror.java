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

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.function.Function;

import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;

import ch.powerunit.extensions.matchers.ProvideMatchers;
import ch.powerunit.extensions.matchers.common.AbstractTypeElementMirror;
import ch.powerunit.extensions.matchers.provideprocessor.extension.DSLExtension;

public class ProvideMatchersMirror extends AbstractTypeElementMirror<ProvideMatchers, RoundMirror> {

	private static final String DEFAULT_PARAM_PARENT = " * @param <_PARENT> used to reference, if necessary, a parent for this builder. By default Void is used an indicate no parent builder.\n";

	public static final String JAVADOC_WARNING_SYNTAXIC_SUGAR_NO_CHANGE_ANYMORE = "<b>This method is a syntaxic sugar that end the DSL and make clear that the matcher can't be change anymore.</b>";

	public static final String JAVADOC_WARNING_PARENT_MAY_BE_VOID = "<b>This method only works in the context of a parent builder. If the real type is Void, then nothing will be returned.</b>";

	protected final String simpleNameOfGeneratedClass;
	protected final String packageNameOfGeneratedClass;
	protected final String simpleNameOfGeneratedInterfaceMatcher;

	public ProvideMatchersMirror(RoundMirror roundMirror, TypeElement annotatedElement) {
		super(ProvideMatchers.class, roundMirror, annotatedElement);
		ProvideMatchers pm = annotation.get();
		this.simpleNameOfGeneratedClass = generateSimpleNameOfGeneratedClass(annotatedElement, pm);
		this.packageNameOfGeneratedClass = generatePackageNameOfGeneratedClass(annotatedElement, pm,
				getProcessingEnv().getElementUtils());
		this.simpleNameOfGeneratedInterfaceMatcher = getSimpleNameOfClassAnnotated() + "Matcher";
	}

	private String generateSimpleNameOfGeneratedClass(TypeElement annotatedElement, ProvideMatchers pm) {
		if ("".equals(pm.matchersClassName())) {
			return getSimpleName(annotatedElement) + "Matchers";
		} else {
			return pm.matchersClassName();
		}
	}

	private String generatePackageNameOfGeneratedClass(TypeElement annotatedElement, ProvideMatchers pm,
			Elements elements) {
		if ("".equals(pm.matchersPackageName())) {
			return getQualifiedName(elements.getPackageOf(annotatedElement));
		} else {
			return pm.matchersPackageName();
		}
	}

	public final String[] getExtension() {
		return annotation.get().extensions();

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
		return DSLExtension.EXTENSION.stream().filter(e -> e.accept(annotation.get().moreMethod()))
				.collect(collectingAndThen(toList(), Collections::unmodifiableList));
	}

	private Function<String, String> asJavadocFormat(String prefix) {
		return t -> String.format("%1$s%2$s\n", prefix, t);
	}

	private String paramToJavadoc(Optional<String> param) {
		return param.map(asJavadocFormat(" * @param ")).orElse("");
	}

	protected String generateJavaDocWithoutParamNeitherParent(String description, String moreDetails,
			Optional<String> param, Optional<String> returnDescription) {
		return String.format("/**\n * %1$s.\n * <p>\n * %2$s\n%3$s%4$s */\n", description, moreDetails,
				paramToJavadoc(param), returnDescription.map(asJavadocFormat(" * @return ")).orElse(""));
	}

	protected String generateDefaultJavaDoc() {
		return String.format("/**\n * %1$s.\n%2$s * \n%3$s * \n */\n", getDefaultDescriptionForDsl(), getParamComment(),
				DEFAULT_PARAM_PARENT);
	}

	protected String generateDefaultJavaDoc(Optional<String> moreDetails, Optional<String> param,
			String returnDescription, boolean withParent) {
		StringBuilder sb = new StringBuilder("/**\n * ").append(getDefaultDescriptionForDsl()).append(".\n")
				.append(moreDetails.map(asJavadocFormat(" * <p>\n * ")).orElse("")).append(paramToJavadoc(param))
				.append(getParamComment()).append(" * \n");
		if (withParent) {
			sb.append(DEFAULT_PARAM_PARENT);
		}
		return sb.append(" * @return ").append(returnDescription).append("\n */\n").toString();
	}

	protected String generateJavaDoc(String description, boolean withParent) {
		StringBuilder sb = new StringBuilder("/**\n * ").append(description).append(".\n").append(getParamComment())
				.append(" * \n");
		if (withParent) {
			sb.append(DEFAULT_PARAM_PARENT);
		}
		return sb.append(" */\n").toString();
	}

	public String generateMainJavaDoc() {
		return String.format(
				"/**\n* This class provides matchers for the class {@link %1$s}.\n * \n * @see %1$s The class for which matchers are provided.\n */\n",
				getFullyQualifiedNameOfClassAnnotated());
	}

	private String getDefaultDescriptionForDsl() {
		return "Start a DSL matcher for the " + getDefaultLinkForAnnotatedClass();
	}

}
