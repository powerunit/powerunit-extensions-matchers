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
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.SimpleElementVisitor8;
import javax.tools.Diagnostic.Kind;

/**
 * @author borettim
 *
 */
public class ProvidesMatchersSubElementVisitor
		extends SimpleElementVisitor8<Optional<FieldDescription>, ProvidesMatchersAnnotatedElementMirror> {

	private final ProcessingEnvironment processingEnv;
	private final Predicate<Element> isInSameRound;
	private final NameExtractorVisitor extractNameVisitor;

	public ProvidesMatchersSubElementVisitor(ProcessingEnvironment processingEnv, Predicate<Element> isInSameRound) {
		this.processingEnv = processingEnv;
		this.isInSameRound = isInSameRound;
		this.extractNameVisitor = new NameExtractorVisitor(processingEnv);
	}

	public static Optional<FieldDescription> removeIfNeededAndThenReturn(Optional<FieldDescription> fieldDescription,
			ProvidesMatchersAnnotatedElementMirror p) {
		fieldDescription.ifPresent(f -> p.removeFromIgnoreList(f.getFieldElement()));
		return fieldDescription;
	}

	@Override
	public Optional<FieldDescription> visitVariable(VariableElement e, ProvidesMatchersAnnotatedElementMirror p) {
		if (e.getModifiers().contains(Modifier.PUBLIC) && !e.getModifiers().contains(Modifier.STATIC)) {
			String fieldName = e.getSimpleName().toString();
			return createFieldDescriptionIfApplicableAndRemoveElementFromListWhenApplicable(e, p, fieldName, fieldName,
					e.asType());
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
	public Optional<FieldDescription> visitExecutable(ExecutableElement e, ProvidesMatchersAnnotatedElementMirror p) {
		if (e.getModifiers().contains(Modifier.PUBLIC) && e.getParameters().size() == 0
				&& !e.getModifiers().contains(Modifier.STATIC)) {
			String simpleName = e.getSimpleName().toString();
			if (simpleName.startsWith("get")) {
				return visiteExecutableGet(e, "get", p);
			} else if (simpleName.startsWith("is")) {
				return visiteExecutableGet(e, "is", p);
			}
		}
		if (p.isInsideIgnoreList(e)) {
			processingEnv.getMessager().printMessage(Kind.MANDATORY_WARNING,
					"One of the annotation is not supported as this location ; Check that this method is public, doesn't have any parameter and is named isXXX or getXXX",
					e);
			p.removeFromIgnoreList(e);
		}
		return Optional.empty();
	}

	private Optional<FieldDescription> visiteExecutableGet(ExecutableElement e, String prefix,
			ProvidesMatchersAnnotatedElementMirror p) {
		String methodName = e.getSimpleName().toString();
		String fieldNameDirect = methodName.replaceFirst(prefix, "");
		String fieldName = fieldNameDirect.substring(0, 1).toLowerCase() + fieldNameDirect.substring(1);
		return createFieldDescriptionIfApplicableAndRemoveElementFromListWhenApplicable(e, p, methodName + "()",
				fieldName, e.getReturnType());
	}

	public Optional<FieldDescription> createFieldDescriptionIfApplicableAndRemoveElementFromListWhenApplicable(
			Element e, ProvidesMatchersAnnotatedElementMirror p, String methodName, String fieldName,
			TypeMirror typeMirrorForField) {
		return removeIfNeededAndThenReturn(typeMirrorForField.accept(extractNameVisitor, false)
				.map(f -> new FieldDescription(p, methodName, fieldName, f,
						isInSameRound.test(processingEnv.getTypeUtils().asElement(e.asType())), e)),
				p);
	}

	@Override
	protected Optional<FieldDescription> defaultAction(Element e, ProvidesMatchersAnnotatedElementMirror p) {
		return Optional.empty();
	}

}
