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

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.function.Function;

import javax.lang.model.element.TypeElement;

import ch.powerunit.extensions.matchers.ProvideMatchers;
import ch.powerunit.extensions.matchers.common.AbstractElementMirror;
import ch.powerunit.extensions.matchers.provideprocessor.extension.AnyOfExtension;
import ch.powerunit.extensions.matchers.provideprocessor.extension.ArrayContainingDSLExtension;
import ch.powerunit.extensions.matchers.provideprocessor.extension.ArrayContainingInAnyOrderDSLExtension;
import ch.powerunit.extensions.matchers.provideprocessor.extension.ContainsDSLExtension;
import ch.powerunit.extensions.matchers.provideprocessor.extension.ContainsInAnyOrderDSLExtension;
import ch.powerunit.extensions.matchers.provideprocessor.extension.DSLExtension;
import ch.powerunit.extensions.matchers.provideprocessor.extension.HasItemsExtension;

public class ProvideMatchersMirror extends AbstractElementMirror<TypeElement, ProvideMatchers, RoundMirror> {

	private static final String DEFAULT_PARAM_PARENT = " * @param <_PARENT> used to reference, if necessary, a parent for this builder. By default Void is used an indicate no parent builder.\n";

	public static final String JAVADOC_WARNING_SYNTAXIC_SUGAR_NO_CHANGE_ANYMORE = "<b>This method is a syntaxic sugar that end the DSL and make clear that the matcher can't be change anymore.</b>";

	public static final String JAVADOC_WARNING_PARENT_MAY_BE_VOID = "<b>This method only works in the context of a parent builder. If the real type is Void, then nothing will be returned.</b>";

	private static final Collection<DSLExtension> EXTENSION = Collections.unmodifiableList(Arrays.asList(
			new ContainsDSLExtension(), new ArrayContainingDSLExtension(), new HasItemsExtension(),
			new ContainsInAnyOrderDSLExtension(), new ArrayContainingInAnyOrderDSLExtension(), new AnyOfExtension()));

	protected final String simpleNameOfGeneratedClass;
	protected final String packageNameOfGeneratedClass;
	protected final String paramJavadoc;

	public ProvideMatchersMirror(RoundMirror roundMirror, TypeElement annotatedElement) {
		super(ProvideMatchers.class, roundMirror, annotatedElement);
		ProvideMatchers pm = annotation.get();
		if ("".equals(pm.matchersClassName())) {
			simpleNameOfGeneratedClass = annotatedElement.getSimpleName().toString() + "Matchers";
		} else {
			simpleNameOfGeneratedClass = pm.matchersClassName();
		}
		if ("".equals(pm.matchersPackageName())) {
			packageNameOfGeneratedClass = getProcessingEnv().getElementUtils().getPackageOf(annotatedElement)
					.getQualifiedName().toString();
		} else {
			packageNameOfGeneratedClass = pm.matchersPackageName();
		}
		this.paramJavadoc = doc.map(ProvideMatchersMirror::extractParamCommentFromJavadoc).orElse(" * \n");
	}

	public final String getComments() {
		return annotation.get().comments();
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
		return EXTENSION.stream().filter(e -> e.accept(annotation.get().moreMethod()))
				.collect(collectingAndThen(toList(), Collections::unmodifiableList));
	}

	public String getFullyQualifiedNameOfClassAnnotatedWithProvideMatcher() {
		return element.getQualifiedName().toString();
	}

	public String getSimpleNameOfClassAnnotatedWithProvideMatcher() {
		return element.getSimpleName().toString();
	}

	protected String getDefaultLinkForAnnotatedClass() {
		return "{@link " + getFullyQualifiedNameOfClassAnnotatedWithProvideMatcher() + " "
				+ getSimpleNameOfClassAnnotatedWithProvideMatcher() + "}";
	}

	protected String generateJavaDocWithoutParamNeitherParent(String description, String moreDetails,
			Optional<String> param, Optional<String> returnDescription) {
		return generateJavaDoc(description, Optional.of(moreDetails), param, returnDescription, false, false);
	}

	private Function<String, String> asJavadocFormat(String prefix) {
		return t -> String.format("%1$s%2$s\n", prefix, t);
	}

	protected String generateDefaultJavaDoc(Optional<String> moreDetails, Optional<String> param,
			Optional<String> returnDescription, boolean withParam, boolean withParent) {
		return generateJavaDoc(getDefaultDescriptionForDsl(), moreDetails, param, returnDescription, withParam,
				withParent);
	}

	protected String generateJavaDoc(String description, Optional<String> moreDetails, Optional<String> param,
			Optional<String> returnDescription, boolean withParam, boolean withParent) {
		StringBuilder sb = new StringBuilder("/**\n * ").append(description).append(".\n")
				.append(moreDetails.map(asJavadocFormat(" * <p>\n * ")).orElse(""))
				.append(param.map(asJavadocFormat(" * @param ")).orElse(""));
		if (withParam) {
			sb.append(paramJavadoc).append(" * \n");
		}
		if (withParent) {
			sb.append(DEFAULT_PARAM_PARENT);
		}
		sb.append(returnDescription.map(asJavadocFormat(" * @return ")).orElse("")).append(" */\n");
		return sb.toString();
	}

	public String generateMainJavaDoc() {
		return String.format(
				"/**\n* This class provides matchers for the class {@link %1$s}.\n * \n * @see %1$s The class for which matchers are provided.\n */\n",
				getFullyQualifiedNameOfClassAnnotatedWithProvideMatcher());
	}

	private String getDefaultDescriptionForDsl() {
		return "Start a DSL matcher for the " + getDefaultLinkForAnnotatedClass();
	}

	private static String extractParamCommentFromJavadoc(String docComment) {
		boolean insideParam = false;
		StringBuilder sb = new StringBuilder(" * \n");
		for (String line : docComment.split("\\R")) {
			if (insideParam && line.matches("^\\s*@.*$")) {
				insideParam = false;
			}
			if (line.matches("^\\s*@param.*$")) {
				insideParam = true;
			}
			if (insideParam) {
				sb.append(" *").append(line).append("\n");
			}
		}
		return sb.toString().replaceAll("\\R", "\n");
	}

}
