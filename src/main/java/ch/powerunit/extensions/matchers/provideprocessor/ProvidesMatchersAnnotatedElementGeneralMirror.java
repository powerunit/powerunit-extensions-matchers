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

import javax.lang.model.element.TypeElement;

public abstract class ProvidesMatchersAnnotatedElementGeneralMirror
		extends ProvidesMatchersAnnotatedElementGenericMirror implements RoundMirrorSupport {

	protected final TypeElement typeElementForClassAnnotatedWithProvideMatcher;
	protected final String methodShortClassName;
	protected final Optional<String> fullyQualifiedNameOfSuperClassOfClassAnnotatedWithProvideMatcher;
	protected final RoundMirror roundMirror;

	public ProvidesMatchersAnnotatedElementGeneralMirror(TypeElement typeElement, RoundMirror roundMirror) {
		super(typeElement, roundMirror);
		this.roundMirror = roundMirror;
		this.typeElementForClassAnnotatedWithProvideMatcher = typeElement;
		this.methodShortClassName = simpleNameOfClassAnnotatedWithProvideMatcher.substring(0, 1).toLowerCase()
				+ simpleNameOfClassAnnotatedWithProvideMatcher.substring(1);
		if (!roundMirror.getProcessingEnv().getElementUtils().getTypeElement("java.lang.Object").asType()
				.equals(typeElement.getSuperclass())) {
			this.fullyQualifiedNameOfSuperClassOfClassAnnotatedWithProvideMatcher = Optional
					.ofNullable(typeElement.getSuperclass().toString());
		} else {
			this.fullyQualifiedNameOfSuperClassOfClassAnnotatedWithProvideMatcher = Optional.empty();
		}

	}

	public String getDefaultReturnMethod() {
		return simpleNameOfClassAnnotatedWithProvideMatcher + "Matcher" + getGenericParent();
	}

	public TypeElement getTypeElementForClassAnnotatedWithProvideMatcher() {
		return typeElementForClassAnnotatedWithProvideMatcher;
	}

	public String getMethodShortClassName() {
		return methodShortClassName;
	}

	public String getSimpleNameOfGeneratedImplementationMatcher() {
		return simpleNameOfClassAnnotatedWithProvideMatcher + "MatcherImpl";
	}

	public String getSimpleNameOfGeneratedImplementationMatcherWithGenericNoParent() {
		return getSimpleNameOfGeneratedImplementationMatcher() + getGenericNoParent();
	}

	public String getSimpleNameOfGeneratedImplementationMatcherWithGenericParent() {
		return getSimpleNameOfGeneratedImplementationMatcher() + getGenericParent();
	}

	public RoundMirror getRoundMirror() {
		return roundMirror;
	}

}
