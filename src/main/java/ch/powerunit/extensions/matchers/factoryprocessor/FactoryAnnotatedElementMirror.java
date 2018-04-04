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

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.PackageElement;
import javax.lang.model.type.TypeKind;

class FactoryAnnotatedElementMirror {

	private final ExecutableElement element;

	private final Optional<String> doc;

	private final String surroundingFullyQualifiedName;

	private final FactoryAnnotationsProcessor factoryAnnotationsProcessor;

	public FactoryAnnotatedElementMirror(FactoryAnnotationsProcessor factoryAnnotationsProcessor,
			ExecutableElement element) {
		this.element = element;
		this.factoryAnnotationsProcessor = factoryAnnotationsProcessor;
		this.doc = Optional.ofNullable(factoryAnnotationsProcessor.getElementUtils().getDocComment(element));
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
		sb.append(factoryAnnotationsProcessor.getElementUtils().getPackageOf(element.getEnclosingElement())
				.getQualifiedName()).append(".").append(element.getEnclosingElement().getSimpleName().toString())
				.append("#").append(element.getSimpleName().toString()).append("(");
		sb.append(element.getParameters().stream().map((ve) -> {
			Element e = factoryAnnotationsProcessor.getTypeUtils().asElement(ve.asType());
			if (e == null) {
				return factoryAnnotationsProcessor.getTypeUtils().erasure(ve.asType()).toString();
			} else {
				if (ve.asType().getKind() == TypeKind.TYPEVAR) {
					return factoryAnnotationsProcessor.getTypeUtils().erasure(ve.asType()).toString();
				}

				PackageElement pe = factoryAnnotationsProcessor.getElementUtils().getPackageOf(e);
				return pe.toString() + "."
						+ factoryAnnotationsProcessor.getTypeUtils().asElement(ve.asType()).getSimpleName();
			}
		}).collect(joining(",")));
		sb.append(")");
		String result = sb.toString();
		if (element.isVarArgs()) {
			result = result.replaceAll("\\[\\](\\s[0-9a-zA-Z_]*$)??", "...");
		}
		return result;
	}

	public String generateFactory() {
		StringBuilder sb = new StringBuilder();
		sb.append("  // " + element.getSimpleName()).append("\n");
		sb.append("  /**\n   * " + doc.map(t -> t.replaceAll("\n", "\n   * ").replaceAll("  * $", "\n"))
				.orElse("No javadoc found from the source method.")).append("\n");
		sb.append("   * @see " + getSeeValue() + "\n   */").append("\n");
		sb.append("  default ");
		if (!element.getTypeParameters().isEmpty()) {
			sb.append("<");
			sb.append(
					element.getTypeParameters().stream()
							.map((ve) -> ve.getSimpleName().toString() + (ve.getBounds().isEmpty() ? ""
									: (" extends "
											+ ve.getBounds().stream().map((b) -> b.toString()).collect(joining("&")))))
							.collect(joining(",")));
			sb.append("> ");
		}
		sb.append(element.getReturnType().toString());
		sb.append(" ");
		sb.append(element.getSimpleName().toString());
		sb.append("(");
		String param = element.getParameters().stream()
				.map((ve) -> ve.asType().toString() + " " + ve.getSimpleName().toString()).collect(joining(","));
		sb.append(element.isVarArgs() ? param.replaceAll("\\[\\](\\s[0-9a-zA-Z_]*$)??", "...") : param);
		sb.append(") {").append("\n");
		if (TypeKind.VOID != element.getReturnType().getKind()) {
			sb.append("    return ");
		} else {
			sb.append("    ");
		}
		sb.append(factoryAnnotationsProcessor.getElementUtils().getPackageOf(element.getEnclosingElement())
				.getQualifiedName().toString());
		sb.append(".");
		sb.append(element.getEnclosingElement().getSimpleName().toString());
		sb.append(".");
		sb.append(element.getSimpleName().toString());
		sb.append("(");
		sb.append(element.getParameters().stream().map((ve) -> ve.getSimpleName().toString()).collect(joining(",")));
		sb.append(");").append("\n");
		sb.append("  }").append("\n");
		sb.append("\n");
		return sb.toString();
	}

}