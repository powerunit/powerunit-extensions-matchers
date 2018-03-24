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

import java.util.Optional;
import java.util.function.Predicate;

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

import ch.powerunit.extensions.matchers.IgnoreInMatcher;
import ch.powerunit.extensions.matchers.provideprocessor.FieldDescription.Type;

/**
 * @author borettim
 *
 */
public class ProvidesMatchersSubElementVisitor
		extends SimpleElementVisitor8<Optional<FieldDescription>, ProvideMatchersAnnotatedElementMirror> {

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

	private final class ExtractNameVisitor extends TypeKindVisitor8<String, Boolean> {

		@Override
		public String visitPrimitiveAsBoolean(PrimitiveType t, Boolean asPrimitif) {
			return (asPrimitif) ? "boolean" : "Boolean";
		}

		@Override
		public String visitPrimitiveAsByte(PrimitiveType t, Boolean asPrimitif) {
			return (asPrimitif) ? "byte" : "Byte";
		}

		@Override
		public String visitPrimitiveAsShort(PrimitiveType t, Boolean asPrimitif) {
			return (asPrimitif) ? "short" : "Short";
		}

		@Override
		public String visitPrimitiveAsInt(PrimitiveType t, Boolean asPrimitif) {
			return (asPrimitif) ? "int" : "Integer";
		}

		@Override
		public String visitPrimitiveAsLong(PrimitiveType t, Boolean asPrimitif) {
			return (asPrimitif) ? "long" : "Long";
		}

		@Override
		public String visitPrimitiveAsChar(PrimitiveType t, Boolean asPrimitif) {
			return (asPrimitif) ? "char" : "Character";
		}

		@Override
		public String visitPrimitiveAsFloat(PrimitiveType t, Boolean asPrimitif) {
			return (asPrimitif) ? "float" : "Float";
		}

		@Override
		public String visitPrimitiveAsDouble(PrimitiveType t, Boolean asPrimitif) {
			return (asPrimitif) ? "double" : "Double";
		}

		@Override
		public String visitArray(ArrayType t, Boolean asPrimitif) {
			return parseType(t.getComponentType(), true) + "[]";
		}

		@Override
		public String visitDeclared(DeclaredType t, Boolean asPrimitif) {
			return t.toString();
		}

		@Override
		public String visitTypeVariable(TypeVariable t, Boolean asPrimitif) {
			return t.toString();
		}

		@Override
		public String visitUnknown(TypeMirror t, Boolean asPrimitif) {
			messageUtils.printMessage(Kind.MANDATORY_WARNING, "Unsupported type element", typesUtils.asElement(t));
			return null;
		}
	}

	private final Elements elementsUtils;
	private final Types typesUtils;
	private final Messager messageUtils;
	private final Predicate<Element> isInSameRound;
	private final ExtracTypeVisitor extractTypeVisitor = new ExtracTypeVisitor();
	private final ExtractNameVisitor extractNameVisitor = new ExtractNameVisitor();

	public ProvidesMatchersSubElementVisitor(Elements elementsUtils, Types typesUtils, Messager messageUtils,
			Predicate<Element> isInSameRound) {
		this.elementsUtils = elementsUtils;
		this.typesUtils = typesUtils;
		this.messageUtils = messageUtils;
		this.isInSameRound = isInSameRound;
	}

	@Override
	public Optional<FieldDescription> visitVariable(VariableElement e, ProvideMatchersAnnotatedElementMirror p) {
		if (e.getModifiers().contains(Modifier.PUBLIC) && !e.getModifiers().contains(Modifier.STATIC)) {
			String fieldName = e.getSimpleName().toString();
			String fieldType = parseType(e.asType(), false);
			if (fieldType != null) {
				Type type = parseType(e.asType());
				return Optional.of(new FieldDescription(p, fieldName, fieldName,
						fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1), fieldType, type,
						isInSameRound.test(typesUtils.asElement(e.asType())), elementsUtils,
						e.getAnnotation(IgnoreInMatcher.class) != null,e));
			}
		}
		return Optional.empty();
	}

	@Override
	public Optional<FieldDescription> visitExecutable(ExecutableElement e, ProvideMatchersAnnotatedElementMirror p) {
		if (e.getModifiers().contains(Modifier.PUBLIC) && e.getParameters().size() == 0
				&& !e.getModifiers().contains(Modifier.STATIC)) {
			String simpleName = e.getSimpleName().toString();
			if (simpleName.startsWith("get")) {
				return Optional.ofNullable(visiteExecutableGet(e, "get", p));
			} else if (simpleName.startsWith("is")) {
				return Optional.ofNullable(visiteExecutableGet(e, "is", p));
			}
		}
		return Optional.empty();
	}

	private FieldDescription visiteExecutableGet(ExecutableElement e, String prefix,
			ProvideMatchersAnnotatedElementMirror p) {
		String methodName = e.getSimpleName().toString();
		String fieldNameDirect = methodName.replaceFirst(prefix, "");
		String fieldName = fieldNameDirect.substring(0, 1).toLowerCase() + fieldNameDirect.substring(1);
		String fieldType = parseType(e.getReturnType(), false);
		if (fieldType != null) {
			Type type = parseType(e.getReturnType());
			return new FieldDescription(p, methodName + "()", fieldName, fieldNameDirect, fieldType, type,
					isInSameRound.test(typesUtils.asElement(e.asType())), elementsUtils,
					e.getAnnotation(IgnoreInMatcher.class) != null,e);
		}
		return null;
	}

	private String parseType(TypeMirror type, boolean asPrimitif) {
		return type.accept(extractNameVisitor, asPrimitif);
	}

	private FieldDescription.Type parseType(TypeMirror type) {
		return type.accept(extractTypeVisitor, null);
	}

}
