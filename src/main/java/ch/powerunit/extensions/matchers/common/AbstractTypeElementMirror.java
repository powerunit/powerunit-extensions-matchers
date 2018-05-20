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

import static java.util.stream.Collectors.joining;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.UnaryOperator;

import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;

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
		this.fullyQualifiedNameOfSuperClassOfClassAnnotated = extractSuper(element);
	}

	private Optional<String> extractSuper(TypeElement element) {
		TypeMirror superObject = element.getSuperclass();
		if (!roundMirror.getObject().equals(superObject)) {
			return Optional.ofNullable(superObject.toString());
		} else {
			return Optional.empty();
		}
	}

	private static String encapsulateString(String input) {
		return input.isEmpty() ? "" : ("<" + input + ">");
	}

	private static <E> String listToString(List<E> input, String delimiter) {
		return listToString(input, Object::toString, delimiter);
	}

	private static <E> String listToString(List<E> input, Function<E, String> mapper, String delimiter) {
		return input.stream().map(mapper).collect(joining(delimiter));
	}

	private static <E> String listToStringWithPostProcessing(List<E> input, String delimiter,
			UnaryOperator<String> postProcessing) {
		return listToStringWithPostProcessing(input, Object::toString, delimiter, postProcessing);
	}

	private static <E> String listToStringWithPostProcessing(List<E> input, Function<E, String> mapper, String delimiter,
			UnaryOperator<String> postProcessing) {
		return postProcessing.apply(listToString(input, mapper, delimiter));
	}

	private static String parseFullGeneric(TypeElement typeElement) {
		return listToStringWithPostProcessing(typeElement.getTypeParameters(),
				t -> t.toString() + " extends " + listToString(t.getBounds(), "&"), ",",
				AbstractTypeElementMirror::encapsulateString);
	}

	private static String parseGeneric(TypeElement typeElement) {
		return listToStringWithPostProcessing(typeElement.getTypeParameters(), ",",
				AbstractTypeElementMirror::encapsulateString);
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
