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

	protected final String methodShortClassName;
	protected final Optional<String> fullyQualifiedNameOfSuperClassOfClassAnnotatedWithProvideMatcher;

	public ProvidesMatchersAnnotatedElementGeneralMirror(TypeElement typeElement, RoundMirror roundMirror) {
		super(typeElement, roundMirror);
		this.methodShortClassName = getSimpleNameOfClassAnnotatedWithProvideMatcher().substring(0, 1).toLowerCase()
				+ getSimpleNameOfClassAnnotatedWithProvideMatcher().substring(1);
		if (!roundMirror.getProcessingEnv().getElementUtils().getTypeElement("java.lang.Object").asType()
				.equals(typeElement.getSuperclass())) {
			this.fullyQualifiedNameOfSuperClassOfClassAnnotatedWithProvideMatcher = Optional
					.ofNullable(typeElement.getSuperclass().toString());
		} else {
			this.fullyQualifiedNameOfSuperClassOfClassAnnotatedWithProvideMatcher = Optional.empty();
		}

	}

	public String getDefaultReturnMethod() {
		return getSimpleNameOfClassAnnotatedWithProvideMatcher() + "Matcher" + getGenericParent();
	}

	public String getMethodShortClassName() {
		return methodShortClassName;
	}

	public String getSimpleNameOfGeneratedImplementationMatcher() {
		return getSimpleNameOfClassAnnotatedWithProvideMatcher() + "MatcherImpl";
	}

	public String getSimpleNameOfGeneratedImplementationMatcherWithGenericNoParent() {
		return getSimpleNameOfGeneratedImplementationMatcher() + getGenericNoParent();
	}

	public String getSimpleNameOfGeneratedImplementationMatcherWithGenericParent() {
		return getSimpleNameOfGeneratedImplementationMatcher() + getGenericParent();
	}

}
