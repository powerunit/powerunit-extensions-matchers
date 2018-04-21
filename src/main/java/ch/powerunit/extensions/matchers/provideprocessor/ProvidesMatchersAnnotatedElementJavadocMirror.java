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

import java.util.Optional;
import java.util.function.Function;

import javax.lang.model.element.TypeElement;

public abstract class ProvidesMatchersAnnotatedElementJavadocMirror extends ProvideMatchersMirror {

	private static final String DEFAULT_PARAM_PARENT = " * @param <_PARENT> used to reference, if necessary, a parent for this builder. By default Void is used an indicate no parent builder.\n";

	public static final String JAVADOC_WARNING_SYNTAXIC_SUGAR_NO_CHANGE_ANYMORE = "<b>This method is a syntaxic sugar that end the DSL and make clear that the matcher can't be change anymore.</b>";

	public static final String JAVADOC_WARNING_PARENT_MAY_BE_VOID = "<b>This method only works in the context of a parent builder. If the real type is Void, then nothing will be returned.</b>";

	protected final String fullyQualifiedNameOfClassAnnotatedWithProvideMatcher;
	protected final String simpleNameOfClassAnnotatedWithProvideMatcher;
	protected final String paramJavadoc;

	public ProvidesMatchersAnnotatedElementJavadocMirror(TypeElement typeElement, RoundMirror roundMirror) {
		super(roundMirror.getProcessingEnv(), typeElement);
		this.fullyQualifiedNameOfClassAnnotatedWithProvideMatcher = typeElement.getQualifiedName().toString();
		this.simpleNameOfClassAnnotatedWithProvideMatcher = typeElement.getSimpleName().toString();
		this.paramJavadoc = extractParamCommentFromJavadoc(
				roundMirror.getProcessingEnv().getElementUtils().getDocComment(typeElement));
	}

	protected String getDefaultLinkForAnnotatedClass() {
		return "{@link " + fullyQualifiedNameOfClassAnnotatedWithProvideMatcher + " "
				+ simpleNameOfClassAnnotatedWithProvideMatcher + "}";
	}

	protected String generateJavaDocWithoutParamNeitherParent(String description, String moreDetails,
			Optional<String> param, Optional<String> returnDescription) {
		return generateJavaDoc(description, Optional.of(moreDetails), param, returnDescription, false, false);
	}

	private Function<String, String> asJavadocFormat(String prefix) {
		return t -> String.format("%1$s%2$s\n", prefix, t);
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

	private static String extractParamCommentFromJavadoc(String docComment) {
		if (docComment == null) {
			return " * \n";
		}
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

	public String getFullyQualifiedNameOfClassAnnotatedWithProvideMatcher() {
		return fullyQualifiedNameOfClassAnnotatedWithProvideMatcher;
	}

	public String getSimpleNameOfClassAnnotatedWithProvideMatcher() {
		return simpleNameOfClassAnnotatedWithProvideMatcher;
	}

}
