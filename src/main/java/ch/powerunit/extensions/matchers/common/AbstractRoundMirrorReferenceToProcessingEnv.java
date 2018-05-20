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
import javax.lang.model.type.TypeMirror;

/**
 * This is an abstract class to contains all required element of a run.
 * <p>
 * It should be implemented by all elements which used these both elements.
 * 
 * @author borettim
 *
 */
public abstract class AbstractRoundMirrorReferenceToProcessingEnv {

	protected final RoundEnvironment roundEnv;
	protected final ProcessingEnvironment processingEnv;
	protected final TypeMirror objectTypeMirror;

	public AbstractRoundMirrorReferenceToProcessingEnv(RoundEnvironment roundEnv, ProcessingEnvironment processingEnv) {
		this.roundEnv = roundEnv;
		this.processingEnv = processingEnv;
		this.objectTypeMirror = getProcessingEnv().getElementUtils().getTypeElement("java.lang.Object").asType();
	}

	public ProcessingEnvironment getProcessingEnv() {
		return processingEnv;
	}

	public RoundEnvironment getRoundEnv() {
		return roundEnv;
	}

	public TypeMirror getObject() {
		return objectTypeMirror;
	}

}
