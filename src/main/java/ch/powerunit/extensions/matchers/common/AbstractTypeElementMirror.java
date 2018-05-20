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
package ch.powerunit.extensions.matchers.common;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.joining;

import java.lang.annotation.Annotation;
import java.util.Optional;

import javax.lang.model.element.TypeElement;

/**
 * @author borettim
 *
 */
public abstract class AbstractTypeElementMirror<A extends Annotation, R extends AbstractRoundMirrorReferenceToProcessingEnv>
		extends AbstractElementMirror<TypeElement, A, R> {

	protected final String generic;
	protected final String fullGeneric;
	protected final Optional<String> fullyQualifiedNameOfSuperClassOfClassAnnotated;

	public AbstractTypeElementMirror(Class<A> annotationType, R roundMirror, TypeElement element) {
		super(annotationType, roundMirror, element);
		this.generic = parseGeneric(element);
		this.fullGeneric = parseFullGeneric(element);
		if (!roundMirror.getProcessingEnv().getElementUtils().getTypeElement("java.lang.Object").asType()
				.equals(element.getSuperclass())) {
			this.fullyQualifiedNameOfSuperClassOfClassAnnotated = Optional
					.ofNullable(element.getSuperclass().toString());
		} else {
			this.fullyQualifiedNameOfSuperClassOfClassAnnotated = Optional.empty();
		}
	}

	private static String parseFullGeneric(TypeElement typeElement) {
		return typeElement.getTypeParameters().stream().map(
				t -> t.toString() + " extends " + t.getBounds().stream().map(b -> b.toString()).collect(joining("&")))
				.collect(collectingAndThen(joining(","), r -> r.isEmpty() ? "" : ("<" + r + ">")));
	}

	private static String parseGeneric(TypeElement typeElement) {
		return typeElement.getTypeParameters().stream().map(t -> t.toString())
				.collect(collectingAndThen(joining(","), r -> r.isEmpty() ? "" : ("<" + r + ">")));
	}

	public String getFullyQualifiedNameOfClassAnnotated() {
		return element.getQualifiedName().toString();
	}

	public String getSimpleNameOfClassAnnotated() {
		return element.getSimpleName().toString();
	}

	protected String getDefaultLinkForAnnotatedClass() {
		return "{@link " + getFullyQualifiedNameOfClassAnnotated() + " " + getSimpleNameOfClassAnnotated() + "}";
	}

	public String getFullGeneric() {
		return fullGeneric;
	}

	public String getGeneric() {
		return generic;
	}

}
