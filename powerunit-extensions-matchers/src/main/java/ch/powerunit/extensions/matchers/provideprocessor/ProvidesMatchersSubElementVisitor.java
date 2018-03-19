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

import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVariable;
import javax.lang.model.util.Elements;
import javax.lang.model.util.SimpleElementVisitor8;
import javax.lang.model.util.TypeKindVisitor8;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic.Kind;

import ch.powerunit.extensions.matchers.provideprocessor.FieldDescription.Type;

/**
 * @author borettim
 *
 */
public class ProvidesMatchersSubElementVisitor extends SimpleElementVisitor8<FieldDescription, Void> {

	private final class ExtracTypeVisitor extends TypeKindVisitor8<FieldDescription.Type, Void> {

		@Override
		public FieldDescription.Type visitPrimitiveAsBoolean(PrimitiveType t, Void p) {
			return FieldDescription.Type.NA;
		}

		@Override
		public FieldDescription.Type visitPrimitiveAsByte(PrimitiveType t, Void p) {
			return FieldDescription.Type.NA;
		}

		@Override
		public FieldDescription.Type visitPrimitiveAsShort(PrimitiveType t, Void p) {
			return FieldDescription.Type.NA;
		}

		@Override
		public FieldDescription.Type visitPrimitiveAsInt(PrimitiveType t, Void p) {
			return FieldDescription.Type.NA;
		}

		@Override
		public FieldDescription.Type visitPrimitiveAsLong(PrimitiveType t, Void p) {
			return FieldDescription.Type.NA;
		}

		@Override
		public FieldDescription.Type visitPrimitiveAsChar(PrimitiveType t, Void p) {
			return FieldDescription.Type.NA;
		}

		@Override
		public FieldDescription.Type visitPrimitiveAsFloat(PrimitiveType t, Void p) {
			return FieldDescription.Type.NA;
		}

		@Override
		public FieldDescription.Type visitPrimitiveAsDouble(PrimitiveType t, Void p) {
			return FieldDescription.Type.NA;
		}

		@Override
		public FieldDescription.Type visitArray(ArrayType t, Void p) {
			return FieldDescription.Type.ARRAY;
		}

		@Override
		public FieldDescription.Type visitDeclared(DeclaredType t, Void p) {
			if (typesUtils.isAssignable(t,
					typesUtils.erasure(elementsUtils.getTypeElement("java.util.Optional").asType()))) {
				return FieldDescription.Type.OPTIONAL;
			}
			if (typesUtils.isAssignable(t,
					typesUtils.erasure(elementsUtils.getTypeElement("java.util.Set").asType()))) {
				return FieldDescription.Type.SET;
			}
			if (typesUtils.isAssignable(t,
					typesUtils.erasure(elementsUtils.getTypeElement("java.util.List").asType()))) {
				return FieldDescription.Type.LIST;
			}
			if (typesUtils.isAssignable(t,
					typesUtils.erasure(elementsUtils.getTypeElement("java.util.Collection").asType()))) {
				return FieldDescription.Type.COLLECTION;
			}
			if (typesUtils.isAssignable(t,
					typesUtils.erasure(elementsUtils.getTypeElement("java.lang.String").asType()))) {
				return FieldDescription.Type.STRING;
			}
			if (typesUtils.isAssignable(t,
					typesUtils.erasure(elementsUtils.getTypeElement("java.lang.Comparable").asType()))) {
				return FieldDescription.Type.COMPARABLE;
			}
			return FieldDescription.Type.NA;
		}

		@Override
		public FieldDescription.Type visitTypeVariable(TypeVariable t, Void p) {
			return FieldDescription.Type.NA;
		}

		@Override
		public FieldDescription.Type visitUnknown(TypeMirror t, Void p) {
			messageUtils.printMessage(Kind.MANDATORY_WARNING, "Unsupported type element");
			return FieldDescription.Type.NA;
		}
	}

	private final class ExtractNameVisitor extends TypeKindVisitor8<String, Void> {

		public ExtractNameVisitor(Element e, boolean asPrimitif) {
			this.asPrimitif = asPrimitif;
			this.e = e;
		}

		private final boolean asPrimitif;

		private final Element e;

		@Override
		public String visitPrimitiveAsBoolean(PrimitiveType t, Void p) {
			return (asPrimitif) ? "boolean" : "Boolean";
		}

		@Override
		public String visitPrimitiveAsByte(PrimitiveType t, Void p) {
			return (asPrimitif) ? "byte" : "Byte";
		}

		@Override
		public String visitPrimitiveAsShort(PrimitiveType t, Void p) {
			return (asPrimitif) ? "short" : "Short";
		}

		@Override
		public String visitPrimitiveAsInt(PrimitiveType t, Void p) {
			return (asPrimitif) ? "int" : "Integer";
		}

		@Override
		public String visitPrimitiveAsLong(PrimitiveType t, Void p) {
			return (asPrimitif) ? "long" : "Long";
		}

		@Override
		public String visitPrimitiveAsChar(PrimitiveType t, Void p) {
			return (asPrimitif) ? "char" : "Character";
		}

		@Override
		public String visitPrimitiveAsFloat(PrimitiveType t, Void p) {
			return (asPrimitif) ? "float" : "Float";
		}

		@Override
		public String visitPrimitiveAsDouble(PrimitiveType t, Void p) {
			return (asPrimitif) ? "double" : "Double";
		}

		@Override
		public String visitArray(ArrayType t, Void p) {
			return parseType(e, t.getComponentType(), true) + "[]";
		}

		@Override
		public String visitDeclared(DeclaredType t, Void p) {
			return t.toString();
		}

		@Override
		public String visitTypeVariable(TypeVariable t, Void p) {
			return t.toString();
		}

		@Override
		public String visitUnknown(TypeMirror t, Void p) {
			messageUtils.printMessage(Kind.MANDATORY_WARNING, "Unsupported type element", e);
			return null;
		}
	}

	private final Elements elementsUtils;
	private final Types typesUtils;
	private final Messager messageUtils;

	public ProvidesMatchersSubElementVisitor(Elements elementsUtils, Types typesUtils, Messager messageUtils) {
		this.elementsUtils = elementsUtils;
		this.typesUtils = typesUtils;
		this.messageUtils = messageUtils;
	}

	@Override
	public FieldDescription visitVariable(VariableElement e, Void p) {
		if (e.getModifiers().contains(Modifier.PUBLIC)) {
			String fieldName = e.getSimpleName().toString();
			String fieldType = parseType(e, e.asType(), false);
			if (fieldType != null) {
				Type type = parseType(e.asType());
				return new FieldDescription(fieldName, fieldName,
						fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1), fieldType, type);
			}
		}
		return null;
	}

	@Override
	public FieldDescription visitExecutable(ExecutableElement e, Void p) {
		if (e.getModifiers().contains(Modifier.PUBLIC) && e.getSimpleName().toString().startsWith("get")
				&& e.getParameters().size() == 0) {
			String methodName = e.getSimpleName().toString();
			String fieldNameDirect = methodName.replaceFirst("get", "");
			String fieldName = fieldNameDirect.substring(0, 1).toLowerCase() + fieldNameDirect.substring(1);
			String fieldType = parseType(e, e.getReturnType(), false);
			if (fieldType != null) {
				Type type = parseType(e.getReturnType());
				return new FieldDescription(methodName + "()", fieldName, fieldNameDirect, fieldType, type);
			}
		} else if (e.getModifiers().contains(Modifier.PUBLIC) && e.getSimpleName().toString().startsWith("is")
				&& e.getParameters().size() == 0) {
			String methodName = e.getSimpleName().toString();
			String fieldNameDirect = methodName.replaceFirst("is", "");
			String fieldName = fieldNameDirect.substring(0, 1).toLowerCase() + fieldNameDirect.substring(1);
			String fieldType = parseType(e, e.getReturnType(), false);
			if (fieldType != null) {
				Type type = parseType(e.getReturnType());
				return new FieldDescription(methodName + "()", fieldName, fieldNameDirect, fieldType, type);
			}
		}
		return null;
	}

	private String parseType(Element e, TypeMirror type, boolean asPrimitif) {
		return type.accept(new ExtractNameVisitor(e, asPrimitif), null);
	}

	private FieldDescription.Type parseType(TypeMirror type) {
		return type.accept(new ExtracTypeVisitor(), null);
	}

}
