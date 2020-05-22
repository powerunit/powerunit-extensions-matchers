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

import java.util.Optional;

import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;

/**
 * @author borettim
 *
 */
public abstract class AbstractTypeElementMirror<R extends AbstractRoundMirrorReferenceToProcessingEnv>
		extends AbstractElementMirror<TypeElement, R> implements ElementHelper {

	protected final String generic;
	protected final String fullGeneric;
	protected final Optional<String> fullyQualifiedNameOfSuperClassOfClassAnnotated;

	public AbstractTypeElementMirror(String annotationType, R roundMirror, TypeElement element) {
		super(annotationType, roundMirror, element);
		this.generic = getGeneric(element);
		this.fullGeneric = getFullGeneric(element);
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

	public String getFullyQualifiedNameOfClassAnnotated() {
		return getQualifiedName(element);
	}

	public String getSimpleNameOfClassAnnotated() {
		return getSimpleName(element);
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
