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

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import org.hamcrest.Factory;

import ch.powerunit.extensions.matchers.common.AbstractElementMirror;

class FactoryAnnotatedElementMirror extends AbstractElementMirror<ExecutableElement, Factory, RoundMirror> {

	private static final String VAR_ARG_REGEX = "\\[\\](\\s[0-9a-zA-Z_]*$)??";

	private final String surroundingFullyQualifiedName;

	public FactoryAnnotatedElementMirror(RoundMirror roundMirror, ExecutableElement element) {
		super(Factory.class, roundMirror, element);
		this.surroundingFullyQualifiedName = element.getEnclosingElement().asType().toString();
	}

	public String getSurroundingFullyQualifiedName() {
		return surroundingFullyQualifiedName;
	}

	public String getSeeValue() {
		StringBuilder sb = new StringBuilder();
		sb.append(getQualifiedName(getProcessingEnv().getElementUtils().getPackageOf(element.getEnclosingElement())))
				.append(".").append(getSimpleName(element.getEnclosingElement())).append("#")
				.append(getSimpleName(element)).append("(");
		sb.append(element.getParameters().stream().map(this::convertParameterForSee).collect(joining(",")));
		sb.append(")");
		String result = sb.toString();
		if (element.isVarArgs()) {
			result = result.replaceAll(VAR_ARG_REGEX, "...");
		}
		return result;
	}

	public String getParam() {
		String param = element.getParameters().stream().map(ve -> ve.asType().toString() + " " + getSimpleName(ve))
				.collect(joining(","));
		return element.isVarArgs() ? param.replaceAll(VAR_ARG_REGEX, "...") : param;
	}

	private String convertParameterForSee(VariableElement ve) {
		Types types = getProcessingEnv().getTypeUtils();
		Elements elements = getProcessingEnv().getElementUtils();
		Element e = types.asElement(ve.asType());
		if (e == null || ve.asType().getKind() == TypeKind.TYPEVAR) {
			return types.erasure(ve.asType()).toString();
		}
		return elements.getPackageOf(e).toString() + "." + types.asElement(ve.asType()).getSimpleName();
	}

	private String getJavadoc() {
		return new StringBuilder("  /**\n   * " + doc.map(t -> t.replaceAll("\n", "\n   * ").replaceAll("  * $", "\n"))
				.orElse("No javadoc found from the source method.")).append("\n   * @see " + getSeeValue() + "\n   */")
						.append("\n").toString();
	}

	private String getGeneric() {
		if (!element.getTypeParameters().isEmpty()) {
			return new StringBuilder("<").append(element.getTypeParameters().stream()
					.map(ve -> getSimpleName(ve) + (ve.getBounds().isEmpty() ? ""
							: (" extends " + ve.getBounds().stream().map(Object::toString).collect(joining("&")))))
					.collect(joining(","))).append("> ").toString();
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
				.append(getQualifiedName(
						getProcessingEnv().getElementUtils().getPackageOf(element.getEnclosingElement())))
				.append(".").append(getSimpleName(element.getEnclosingElement())).append(".")
				.append(getSimpleName(element)).append("(")
				.append(element.getParameters().stream().map((ve) -> getSimpleName(ve)).collect(joining(",")))
				.append(");\n  }\n\n").toString();
	}

}