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

import java.lang.annotation.Annotation;
import java.util.Optional;

import javax.lang.model.element.Element;

/**
 * @author borettim
 *
 */
public abstract class AbstractElementMirror<E extends Element, A extends Annotation, R extends AbstractRoundMirrorReferenceToProcessingEnv>
		implements AbstractRoundMirrorSupport<R>, ElementHelper {

	protected final R roundMirror;
	protected final E element;
	protected final Optional<String> doc;
	protected final Optional<A> annotation;

	public AbstractElementMirror(Class<A> annotationType, R roundMirror, E element) {
		this.roundMirror = roundMirror;
		this.element = element;
		this.doc = Optional.ofNullable(roundMirror.getElementUtils().getDocComment(element));
		this.annotation = Optional.ofNullable(element.getAnnotation(annotationType));
	}

	@Override
	public R getRoundMirror() {
		return roundMirror;
	}

	public E getElement() {
		return element;
	}

	public Optional<String> getDoc() {
		return doc;
	}

	public Optional<A> getAnnotation() {
		return annotation;
	}

	public String getParamComment() {
		return doc.map(AbstractElementMirror::extractParamCommentFromJavadoc).orElse(" * \n");
	}

	private static String extractParamCommentFromJavadoc(String docComment) {
		boolean insideParam = false;
		StringBuilder sb = new StringBuilder(" * \n");
		for (String line : docComment.split("\\R")) {
			if (insideParam && line.matches("^\\s*@.*$")) {
				insideParam = false;
			}
			if (line.matches("^\\s*@param.*$")) {
				insideParam = true;
			}
			if (insideParam) {
				sb.append(" *").append(line).append("\n");
			}
		}
		return sb.toString();
	}

}
