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
package ch.powerunit.extensions.matchers.provideprocessor.fields;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;

import ch.powerunit.extensions.matchers.provideprocessor.ProvideMatchersMirror;
import ch.powerunit.extensions.matchers.provideprocessor.ProvidesMatchersAnnotatedElementData;
import ch.powerunit.extensions.matchers.provideprocessor.RoundMirror;

public class FieldDescriptionMirror {

	protected final String fieldName;
	protected final String fieldType;
	protected final TypeElement fieldTypeAsTypeElement;
	protected final Element fieldElement;

	public FieldDescriptionMirror(ProvidesMatchersAnnotatedElementData containingElementMirror, String fieldName,
			String fieldType, Element fieldElement) {
		ProcessingEnvironment processingEnv = containingElementMirror.getRoundMirror().getProcessingEnv();
		this.fieldName = fieldName;
		this.fieldType = fieldType;
		this.fieldTypeAsTypeElement = processingEnv.getElementUtils().getTypeElement(fieldType);
		this.fieldElement = fieldElement;
	}

	public String getFieldAccessor() {
		return fieldElement.getSimpleName().toString() + ((fieldElement instanceof ExecutableElement) ? "()" : "");
	}

	public String getFieldName() {
		return fieldName;
	}

	public String getMethodFieldName() {
		return fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
	}

	public String getFieldType() {
		return fieldType;
	}

	public Element getFieldElement() {
		return fieldElement;
	}

	public TypeElement getFieldTypeAsTypeElement() {
		return fieldTypeAsTypeElement;
	}

	public String computeFullyQualifiedNameMatcherInSameRound(RoundMirror roundMirror) {
		ProcessingEnvironment processingEnv = roundMirror.getProcessingEnv();
		if (roundMirror.isInSameRound(processingEnv.getTypeUtils().asElement(fieldElement.asType()))
				&& fieldTypeAsTypeElement != null) {
			return new ProvideMatchersMirror(processingEnv, fieldTypeAsTypeElement)
					.getFullyQualifiedNameOfGeneratedClass();
		}
		return null;
	}

}
