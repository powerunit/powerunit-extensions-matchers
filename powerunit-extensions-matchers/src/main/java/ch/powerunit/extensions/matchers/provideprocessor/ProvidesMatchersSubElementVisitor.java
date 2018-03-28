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

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVariable;
import javax.lang.model.util.SimpleElementVisitor8;
import javax.lang.model.util.TypeKindVisitor8;
import javax.tools.Diagnostic.Kind;

/**
 * @author borettim
 *
 */
public class ProvidesMatchersSubElementVisitor
		extends SimpleElementVisitor8<Optional<FieldDescription>, ProvideMatchersAnnotatedElementMirror> {

	private final ProcessingEnvironment processingEnv;
	private final Predicate<Element> isInSameRound;
	private final ExtractNameVisitor extractNameVisitor = new ExtractNameVisitor();

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
			processingEnv.getMessager().printMessage(Kind.MANDATORY_WARNING, "Unsupported type element",
					processingEnv.getTypeUtils().asElement(t));
			return null;
		}
	}

	public ProvidesMatchersSubElementVisitor(ProcessingEnvironment processingEnv, Predicate<Element> isInSameRound) {
		this.processingEnv = processingEnv;
		this.isInSameRound = isInSameRound;
	}

	@Override
	public Optional<FieldDescription> visitVariable(VariableElement e, ProvideMatchersAnnotatedElementMirror p) {
		if (e.getModifiers().contains(Modifier.PUBLIC) && !e.getModifiers().contains(Modifier.STATIC)) {
			String fieldName = e.getSimpleName().toString();
			String fieldType = parseType(e.asType(), false);
			if (fieldType != null) {
				p.removeFromIgnoreList(e);
				return Optional.of(new FieldDescription(p, fieldName, fieldName, fieldType,
						isInSameRound.test(processingEnv.getTypeUtils().asElement(e.asType())), processingEnv, e,
						e.asType()));
			}
		}
		if (p.isInsideIgnoreList(e)) {
			processingEnv.getMessager().printMessage(Kind.MANDATORY_WARNING,
					"One of the annotation is not supported as this location ; Check that this field is public and not static",
					e);
			p.removeFromIgnoreList(e);
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
		if (p.isInsideIgnoreList(e)) {
			processingEnv.getMessager().printMessage(Kind.MANDATORY_WARNING,
					"One of the annotation is not supported as this location ; CHeck that this method is public, doesn't have any parameter and is named isXXX or getXXX",
					e);
			p.removeFromIgnoreList(e);
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
			p.removeFromIgnoreList(e);
			return new FieldDescription(p, methodName + "()", fieldName, fieldType,
					isInSameRound.test(processingEnv.getTypeUtils().asElement(e.asType())), processingEnv, e,
					e.getReturnType());
		}
		return null;
	}

	@Override
	protected Optional<FieldDescription> defaultAction(Element e, ProvideMatchersAnnotatedElementMirror p) {
		return Optional.empty();
	}

	private String parseType(TypeMirror type, boolean asPrimitif) {
		return type.accept(extractNameVisitor, asPrimitif);
	}

}
