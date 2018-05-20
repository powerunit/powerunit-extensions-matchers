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

import javax.lang.model.element.TypeElement;

public abstract class ProvidesMatchersAnnotatedElementGeneralMirror
		extends ProvidesMatchersAnnotatedElementGenericMirror implements RoundMirrorSupport {

	protected final String methodShortClassName;

	public ProvidesMatchersAnnotatedElementGeneralMirror(TypeElement typeElement, RoundMirror roundMirror) {
		super(typeElement, roundMirror);
		String simplename = getSimpleNameOfClassAnnotated();
		this.methodShortClassName = simplename.substring(0, 1).toLowerCase() + simplename.substring(1);
	}

	public String getDefaultReturnMethod() {
		return getSimpleNameOfClassAnnotated() + "Matcher" + getGenericParent();
	}

	public String getMethodShortClassName() {
		return methodShortClassName;
	}

	public String getSimpleNameOfGeneratedImplementationMatcher() {
		return getSimpleNameOfClassAnnotated() + "MatcherImpl";
	}

	public String getSimpleNameOfGeneratedImplementationMatcherWithGenericNoParent() {
		return getSimpleNameOfGeneratedImplementationMatcher() + getGenericNoParent();
	}

	public String getSimpleNameOfGeneratedImplementationMatcherWithGenericParent() {
		return getSimpleNameOfGeneratedImplementationMatcher() + getGenericParent();
	}

}
