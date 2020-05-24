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

import ch.powerunit.extensions.matchers.ComplementaryExpositionMethod;
import ch.powerunit.extensions.matchers.ProvideMatchers;
import ch.powerunit.extensions.matchers.common.AbstractTypeElementMirror;

public class ProvideMatchersAnnotationMirror extends AbstractTypeElementMirror<RoundMirror> {

	protected final ProvideMatchers realAnnotation;
	protected final boolean allowWeakWithSameValue;

	public ProvideMatchersAnnotationMirror(RoundMirror roundMirror, TypeElement annotatedElement) {
		super("ch.powerunit.extensions.matchers.ProvideMatchers", roundMirror, annotatedElement);
		this.realAnnotation = annotatedElement.getAnnotation(ProvideMatchers.class);
		this.allowWeakWithSameValue = realAnnotation.allowWeakWithSameValue();
	}

	/**
	 * @return
	 * @see ch.powerunit.extensions.matchers.ProvideMatchers#comments()
	 */
	public String comments() {
		return realAnnotation.comments();
	}

	/**
	 * @return
	 * @see ch.powerunit.extensions.matchers.ProvideMatchers#moreMethod()
	 */
	public ComplementaryExpositionMethod[] moreMethod() {
		return realAnnotation.moreMethod();
	}

	/**
	 * @return
	 * @see ch.powerunit.extensions.matchers.ProvideMatchers#extensions()
	 */
	public String[] extensions() {
		return realAnnotation.extensions();
	}

}
