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

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVariable;
import javax.lang.model.util.TypeKindVisitor8;
import javax.tools.Diagnostic.Kind;

public class FieldDescriptionMirror {

	public static enum Type {
		NA, ARRAY, COLLECTION, LIST, SET, OPTIONAL, COMPARABLE, STRING, SUPPLIER
	}

	protected final String fieldAccessor;
	protected final String fieldName;
	protected final String methodFieldName;
	protected final String fieldType;
	protected final Type type;
	protected final TypeElement fieldTypeAsTypeElement;

	public static final class ExtracTypeVisitor extends TypeKindVisitor8<Type, ProcessingEnvironment> {

		@Override
		protected Type defaultAction(TypeMirror t, ProcessingEnvironment processingEnv) {
			return Type.NA;
		}

		@Override
		public Type visitArray(ArrayType t, ProcessingEnvironment processingEnv) {
			return Type.ARRAY;
		}

		@Override
		public Type visitDeclared(DeclaredType t, ProcessingEnvironment processingEnv) {
			if (processingEnv.getTypeUtils().isAssignable(t, processingEnv.getTypeUtils()
					.erasure(processingEnv.getElementUtils().getTypeElement("java.util.Optional").asType()))) {
				return Type.OPTIONAL;
			} else if (processingEnv.getTypeUtils().isAssignable(t, processingEnv.getTypeUtils()
					.erasure(processingEnv.getElementUtils().getTypeElement("java.util.Set").asType()))) {
				return Type.SET;
			} else if (processingEnv.getTypeUtils().isAssignable(t, processingEnv.getTypeUtils()
					.erasure(processingEnv.getElementUtils().getTypeElement("java.util.List").asType()))) {
				return Type.LIST;
			} else if (processingEnv.getTypeUtils().isAssignable(t, processingEnv.getTypeUtils()
					.erasure(processingEnv.getElementUtils().getTypeElement("java.util.Collection").asType()))) {
				return Type.COLLECTION;
			} else if (processingEnv.getTypeUtils().isAssignable(t, processingEnv.getTypeUtils()
					.erasure(processingEnv.getElementUtils().getTypeElement("java.lang.String").asType()))) {
				return Type.STRING;
			} else if (processingEnv.getTypeUtils().isAssignable(t, processingEnv.getTypeUtils()
					.erasure(processingEnv.getElementUtils().getTypeElement("java.lang.Comparable").asType()))) {
				return Type.COMPARABLE;
			} else if (processingEnv.getTypeUtils().isAssignable(t, processingEnv.getTypeUtils()
					.erasure(processingEnv.getElementUtils().getTypeElement("java.util.function.Supplier").asType()))) {
				return Type.SUPPLIER;
			}
			return Type.NA;
		}

		@Override
		public Type visitTypeVariable(TypeVariable t, ProcessingEnvironment processingEnv) {
			return Type.NA;
		}

		@Override
		public Type visitUnknown(TypeMirror t, ProcessingEnvironment processingEnv) {
			processingEnv.getMessager().printMessage(Kind.MANDATORY_WARNING, "Unsupported type element");
			return Type.NA;
		}
	}

	public FieldDescriptionMirror(ProvidesMatchersAnnotatedElementData containingElementMirror, String fieldName,
			String fieldType, Element fieldElement) {
		RoundMirror roundMirror = containingElementMirror.getFullData().getRoundMirror();
		TypeMirror fieldTypeMirror = (fieldElement instanceof ExecutableElement)
				? ((ExecutableElement) fieldElement).getReturnType() : fieldElement.asType();
		ProcessingEnvironment processingEnv = roundMirror.getProcessingEnv();
		this.fieldAccessor = fieldElement.getSimpleName().toString()
				+ ((fieldElement instanceof ExecutableElement) ? "()" : "");
		this.fieldName = fieldName;
		this.methodFieldName = fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
		this.fieldType = fieldType;
		this.type = new ExtracTypeVisitor().visit(fieldTypeMirror, processingEnv);
		this.fieldTypeAsTypeElement = processingEnv.getElementUtils().getTypeElement(fieldType);
	}

}
