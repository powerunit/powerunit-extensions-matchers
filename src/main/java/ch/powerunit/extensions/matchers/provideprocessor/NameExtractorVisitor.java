
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

import static java.util.Optional.empty;
import static java.util.Optional.of;

import java.util.Optional;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVariable;
import javax.tools.Diagnostic.Kind;

import ch.powerunit.extensions.matchers.common.AbstractTypeKindVisitor;

final class NameExtractorVisitor extends AbstractTypeKindVisitor<Optional<String>, Boolean, RoundMirror> {

	public NameExtractorVisitor(RoundMirror support) {
		super(support);
	}

	@Override
	public Optional<String> visitPrimitive(PrimitiveType t, Boolean asPrimitif) {
		return of(asPrimitif ? t.toString() : getProcessingEnv().getTypeUtils().boxedClass(t).toString());
	}

	@Override
	public Optional<String> visitArray(ArrayType t, Boolean asPrimitif) {
		return t.getComponentType().accept(this, true).map(r -> r + "[]");
	}

	@Override
	public Optional<String> visitDeclared(DeclaredType t, Boolean asPrimitif) {
		return of(t.toString().replaceAll("@[^ ]+ ", ""));
	}

	@Override
	public Optional<String> visitTypeVariable(TypeVariable t, Boolean asPrimitif) {
		return of(t.toString());
	}

	@Override
	public Optional<String> visitUnknown(TypeMirror t, Boolean asPrimitif) {
		ProcessingEnvironment processingEnv = getProcessingEnv();
		processingEnv.getMessager().printMessage(Kind.MANDATORY_WARNING, "Unsupported type element",
				processingEnv.getTypeUtils().asElement(t));
		return empty();
	}
}