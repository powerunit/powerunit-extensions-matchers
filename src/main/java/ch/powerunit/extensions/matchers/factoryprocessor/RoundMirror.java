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
package ch.powerunit.extensions.matchers.factoryprocessor;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

import ch.powerunit.extensions.matchers.common.AbstractRoundMirrorReferenceToProcessingEnv;

public class RoundMirror extends AbstractRoundMirrorReferenceToProcessingEnv {

	private final TypeElement factoryAnnotationTE;

	public RoundMirror(RoundEnvironment roundEnv, ProcessingEnvironment processingEnv) {
		super(roundEnv, processingEnv);
		factoryAnnotationTE = processingEnv.getElementUtils().getTypeElement("org.hamcrest.Factory");
	}

	public AnnotationMirror getFactoryAnnotation(Element e) {
		return processingEnv.getElementUtils().getAllAnnotationMirrors(e).stream()
				.filter(a -> a.getAnnotationType().equals(factoryAnnotationTE.asType())).findAny().orElse(null);
	}

}
