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
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVariable;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic.Kind;

import ch.powerunit.extensions.matchers.api.IgnoreInMatcher;
import ch.powerunit.extensions.matchers.common.AbstractTypeKindVisitor;
import ch.powerunit.extensions.matchers.provideprocessor.ProvidesMatchersAnnotatedElementData;
import ch.powerunit.extensions.matchers.provideprocessor.RoundMirror;

public final class FieldDescriptionProvider {

	private FieldDescriptionProvider() {
	}

	public static final class ExtractTypeVisitor
			extends AbstractTypeKindVisitor<AbstractFieldDescription, FieldDescriptionMirror, RoundMirror> {

		private final ProvidesMatchersAnnotatedElementData containingElementMirror;

		public ExtractTypeVisitor(ProvidesMatchersAnnotatedElementData containingElementMirror) {
			super(containingElementMirror.getRoundMirror());
			this.containingElementMirror = containingElementMirror;
		}

		private boolean isAssignable(DeclaredType t, String target) {
			ProcessingEnvironment processingEnv = getProcessingEnv();
			Types types = processingEnv.getTypeUtils();
			Elements elements = processingEnv.getElementUtils();
			return types.isAssignable(t, types.erasure(elements.getTypeElement(target).asType()));
		}

		@Override
		protected AbstractFieldDescription defaultAction(TypeMirror t, FieldDescriptionMirror mirror) {
			return new DefaultFieldDescription(containingElementMirror, mirror);
		}

		@Override
		public AbstractFieldDescription visitPrimitive(PrimitiveType t, FieldDescriptionMirror mirror) {
			return new PrimitiveFieldDescription(containingElementMirror, mirror);
		}

		@Override
		public AbstractFieldDescription visitArray(ArrayType t, FieldDescriptionMirror mirror) {
			return new ArrayFieldDescription(containingElementMirror, mirror);
		}

		@Override
		public AbstractFieldDescription visitDeclared(DeclaredType t, FieldDescriptionMirror mirror) {
			if (isAssignable(t, "java.util.Optional")) {
				return new OptionalFieldDescription(containingElementMirror, mirror);
			} else if (isAssignable(t, "java.util.Map")) {
				return new MapFieldDescription(containingElementMirror, mirror);
			} else if (isAssignable(t, "java.util.Set") || isAssignable(t, "java.util.List")
					|| isAssignable(t, "java.util.Collection")) {
				return new CollectionFieldDescription(containingElementMirror, mirror);
			} else if (isAssignable(t, "java.lang.String")) {
				return new StringFieldDescription(containingElementMirror, mirror);
			} else if (isAssignable(t, "java.lang.Comparable")) {
				return new ComparableFieldDescription(containingElementMirror, mirror);
			} else if (isAssignable(t, "java.util.function.Supplier")) {
				return new SupplierFieldDescription(containingElementMirror, mirror);
			}
			return new DefaultFieldDescription(containingElementMirror, mirror);
		}

		@Override
		public AbstractFieldDescription visitTypeVariable(TypeVariable t, FieldDescriptionMirror mirror) {
			return new DefaultFieldDescription(containingElementMirror, mirror);
		}

		@Override
		public AbstractFieldDescription visitUnknown(TypeMirror t, FieldDescriptionMirror mirror) {
			getProcessingEnv().getMessager().printMessage(Kind.MANDATORY_WARNING, "Unsupported type element");
			return new DefaultFieldDescription(containingElementMirror, mirror);
		}
	}

	public static boolean isIgnored(FieldDescriptionMirror mirror) {
		return mirror.getFieldElement().getAnnotation(IgnoreInMatcher.class) != null;
	}

	public static AbstractFieldDescription of(ProvidesMatchersAnnotatedElementData containingElementMirror,
			FieldDescriptionMirror mirror) {
		if (isIgnored(mirror)) {
			return new IgnoreFieldDescription(containingElementMirror, mirror);
		}
		Element te = mirror.getFieldElement();
		return new ExtractTypeVisitor(containingElementMirror).visit(
				(te instanceof ExecutableElement ee) ? ee.getReturnType() : te.asType(), mirror);
	}

}
