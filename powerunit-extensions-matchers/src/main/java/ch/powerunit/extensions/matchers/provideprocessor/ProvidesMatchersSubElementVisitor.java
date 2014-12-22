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

import java.io.PrintWriter;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.SimpleElementVisitor8;
import javax.lang.model.util.TypeKindVisitor8;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic.Kind;

/**
 * @author borettim
 *
 */
public class ProvidesMatchersSubElementVisitor extends
		SimpleElementVisitor8<FieldDescription, Void> {

	private final ProvidesMatchersAnnotationsProcessor providesMatchersAnnotationsProcessor;
	private final Elements elementsUtils;
	private final Types typesUtils;
	private final Filer filerUtils;
	private final Messager messageUtils;
	private final TypeElement typeElement;
	private final PrintWriter writer;

	public ProvidesMatchersSubElementVisitor(
			ProvidesMatchersAnnotationsProcessor providesMatchersAnnotationsProcessor,
			Elements elementsUtils, Filer filerUtils, Types typesUtils,
			Messager messageUtils, TypeElement typeElement, PrintWriter writer) {
		this.providesMatchersAnnotationsProcessor = providesMatchersAnnotationsProcessor;
		this.elementsUtils = elementsUtils;
		this.typesUtils = typesUtils;
		this.filerUtils = filerUtils;
		this.messageUtils = messageUtils;
		this.typeElement = typeElement;
		this.writer = writer;
	}

	@Override
	public FieldDescription visitVariable(VariableElement e, Void p) {
		if (e.getModifiers().contains(Modifier.PUBLIC)) {
			String fieldName = e.getSimpleName().toString();
			String fieldType = parseType(e, e.asType(), false);
			if (fieldType != null) {
				writer.println("  // Field " + fieldName + " of " + fieldType);
				createFeatureMatcher(
						fieldName,
						fieldName,
						fieldName.substring(0, 1).toUpperCase()
								+ fieldName.substring(1), fieldType);
				writer.println();
				return new FieldDescription(fieldName, fieldName,
						fieldName.substring(0, 1).toUpperCase()
								+ fieldName.substring(1), fieldType);
			}
		}
		return null;
	}

	@Override
	public FieldDescription visitExecutable(ExecutableElement e, Void p) {
		if (e.getModifiers().contains(Modifier.PUBLIC)
				&& e.getSimpleName().toString().startsWith("get")
				&& e.getParameters().size() == 0) {
			String methodName = e.getSimpleName().toString();
			String fieldNameDirect = methodName.replaceFirst("get", "");
			String fieldName = fieldNameDirect.substring(0, 1).toLowerCase()
					+ fieldNameDirect.substring(1);
			String fieldType = parseType(e, e.getReturnType(), false);
			if (fieldType != null) {
				writer.println("  // Method " + methodName + " for field "
						+ fieldName + " of " + fieldType);
				createFeatureMatcher(methodName + "()", fieldName,
						fieldNameDirect, fieldType);
				writer.println();
				return new FieldDescription(methodName + "()", fieldName,
						fieldNameDirect, fieldType);
			}
		}
		return null;
	}

	private void createFeatureMatcher(String fieldAccessor, String fieldName,
			String methodFieldName, String fieldType) {
		String type = typeElement.getSimpleName().toString();
		writer.println("  private static class " + methodFieldName
				+ "Matcher extends org.hamcrest.FeatureMatcher<" + type + ","
				+ fieldType + "> {");
		writer.println();
		writer.println("    public " + methodFieldName
				+ "Matcher(org.hamcrest.Matcher<? super " + fieldType
				+ "> matcher) {");
		writer.println("      super(matcher,\"" + fieldName + "\",\""
				+ fieldName + "\");");
		writer.println("  }");
		writer.println();
		writer.println("    protected " + fieldType + " featureValueOf(" + type
				+ " actual) {");
		writer.println("      return actual." + fieldAccessor + ";");
		writer.println("    }");
		writer.println();
		writer.println("  }");
		writer.println();
	}

	private String parseType(Element e, TypeMirror type, boolean asPrimitif) {
		return type.accept(new TypeKindVisitor8<String, Void>() {

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
			public String visitUnknown(TypeMirror t, Void p) {
				messageUtils.printMessage(Kind.MANDATORY_WARNING,
						"Unsupported type element", e);
				return null;
			}
		}, null);
	}

}
