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
package ch.powerunit.extensions.matchers.factoryprocessor;

import static java.util.stream.Collectors.joining;

import java.util.Optional;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;

class FactoryAnnotatedElementMirror {

	private static final String VAR_ARG_REGEX = "\\[\\](\\s[0-9a-zA-Z_]*$)??";

	private final ExecutableElement element;

	private final Optional<String> doc;

	private final String surroundingFullyQualifiedName;

	private final ProcessingEnvironment processingEnv;

	public FactoryAnnotatedElementMirror(FactoryAnnotationsProcessor factoryAnnotationsProcessor,
			ExecutableElement element) {
		this.element = element;
		this.processingEnv = factoryAnnotationsProcessor;
		this.doc = Optional.ofNullable(processingEnv.getElementUtils().getDocComment(element));
		this.surroundingFullyQualifiedName = element.getEnclosingElement().asType().toString();
	}

	public ExecutableElement getElement() {
		return element;
	}

	public String getSurroundingFullyQualifiedName() {
		return surroundingFullyQualifiedName;
	}

	public String getSeeValue() {
		StringBuilder sb = new StringBuilder();
		sb.append(processingEnv.getElementUtils().getPackageOf(element.getEnclosingElement()).getQualifiedName())
				.append(".").append(element.getEnclosingElement().getSimpleName().toString()).append("#")
				.append(element.getSimpleName().toString()).append("(");
		sb.append(element.getParameters().stream().map(this::convertParameterForSee).collect(joining(",")));
		sb.append(")");
		String result = sb.toString();
		if (element.isVarArgs()) {
			result = result.replaceAll(VAR_ARG_REGEX, "...");
		}
		return result;
	}

	public String getParam() {
		String param = element.getParameters().stream()
				.map(ve -> ve.asType().toString() + " " + ve.getSimpleName().toString()).collect(joining(","));
		return element.isVarArgs() ? param.replaceAll(VAR_ARG_REGEX, "...") : param;
	}

	private String convertParameterForSee(VariableElement ve) {
		Element e = processingEnv.getTypeUtils().asElement(ve.asType());
		if (e == null || ve.asType().getKind() == TypeKind.TYPEVAR) {
			return processingEnv.getTypeUtils().erasure(ve.asType()).toString();
		}
		return processingEnv.getElementUtils().getPackageOf(e).toString() + "."
				+ processingEnv.getTypeUtils().asElement(ve.asType()).getSimpleName();
	}

	private String getJavadoc() {
		return new StringBuilder("  /**\n   * " + doc.map(t -> t.replaceAll("\n", "\n   * ").replaceAll("  * $", "\n"))
				.orElse("No javadoc found from the source method.")).append("\n   * @see " + getSeeValue() + "\n   */")
						.append("\n").toString();
	}

	private String getGeneric() {
		if (!element.getTypeParameters().isEmpty()) {
			return new StringBuilder("<")
					.append(element.getTypeParameters().stream()
							.map(ve -> ve.getSimpleName().toString()
									+ (ve.getBounds().isEmpty() ? ""
											: (" extends " + ve.getBounds().stream().map(Object::toString)
													.collect(joining("&")))))
							.collect(joining(",")))
					.append("> ").toString();
		}
		return "";
	}

	private String getDeclaration() {
		return String.format("%1$s%2$s %3$s(%4$s)", getGeneric(), element.getReturnType(), element.getSimpleName(),
				getParam());
	}

	public String generateFactory() {
		return new StringBuilder(getJavadoc()).append("  default ").append(getDeclaration()).append(" {\n")
				.append(TypeKind.VOID != element.getReturnType().getKind() ? "    return " : "    ")
				.append(processingEnv.getElementUtils().getPackageOf(element.getEnclosingElement()).getQualifiedName()
						.toString())
				.append(".").append(element.getEnclosingElement().getSimpleName().toString()).append(".")
				.append(element.getSimpleName().toString()).append("(").append(element.getParameters().stream()
						.map((ve) -> ve.getSimpleName().toString()).collect(joining(",")))
				.append(");\n  }\n\n").toString();
	}

}