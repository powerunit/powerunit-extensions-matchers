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

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;

/**
 * This interface can be used by element with returns (supports)
 * {@link AbstractRoundMirrorReferenceToProcessingEnv}.
 * 
 * @author borettim
 *
 * @param <R>
 */
@FunctionalInterface
public interface AbstractRoundMirrorSupport<R extends AbstractRoundMirrorReferenceToProcessingEnv>
		extends ElementHelper {
	R getRoundMirror();

	default ProcessingEnvironment getProcessingEnv() {
		return getRoundMirror().getProcessingEnv();
	}

	default RoundEnvironment getRoundEnv() {
		return getRoundMirror().getRoundEnv();
	}

	default TypeMirror getMirrorFor(String name) {
		return getProcessingEnv().getElementUtils().getTypeElement(name).asType();
	}

	default boolean isSameType(TypeElement fromField, TypeMirror compareWith) {
		return fromField != null && getProcessingEnv().getTypeUtils().isSameType(compareWith, fromField.asType());
	}

	default boolean isAssignableWithErasure(TypeElement fromField, TypeMirror compareWith) {
		return fromField != null && getProcessingEnv().getTypeUtils().isAssignable(fromField.asType(),
				getProcessingEnv().getTypeUtils().erasure(compareWith));
	}

	default String getAnnotationProcessorVersion() {
		return getClass().getPackage().getImplementationVersion();
	}

}
