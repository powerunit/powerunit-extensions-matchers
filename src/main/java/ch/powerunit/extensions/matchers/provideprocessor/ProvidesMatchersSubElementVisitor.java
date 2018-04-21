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
import java.util.function.Function;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.SimpleElementVisitor8;
import javax.tools.Diagnostic.Kind;

import ch.powerunit.extensions.matchers.provideprocessor.fields.AbstractFieldDescription;
import ch.powerunit.extensions.matchers.provideprocessor.fields.FieldDescriptionMirror;
import ch.powerunit.extensions.matchers.provideprocessor.fields.FieldDescriptionProvider;

/**
 * @author borettim
 *
 */
public class ProvidesMatchersSubElementVisitor extends
		SimpleElementVisitor8<Optional<AbstractFieldDescription>, ProvidesMatchersAnnotatedElementMatcherMirror> {

	private final Function<Element, Boolean> removeFromIgnoreList;
	private final NameExtractorVisitor extractNameVisitor;
	private final ProcessingEnvironment processingEnv;

	public ProvidesMatchersSubElementVisitor(RoundMirror roundMirror) {
		this.processingEnv = roundMirror.getProcessingEnv();
		this.removeFromIgnoreList = roundMirror::removeFromIgnoreList;
		this.extractNameVisitor = new NameExtractorVisitor(processingEnv);
	}

	public Optional<AbstractFieldDescription> removeIfNeededAndThenReturn(
			Optional<AbstractFieldDescription> fieldDescription) {
		fieldDescription.ifPresent(f -> removeFromIgnoreList.apply(f.getFieldElement()));
		return fieldDescription;
	}

	@Override
	public Optional<AbstractFieldDescription> visitVariable(VariableElement e,
			ProvidesMatchersAnnotatedElementMatcherMirror p) {
		if (e.getModifiers().contains(Modifier.PUBLIC) && !e.getModifiers().contains(Modifier.STATIC)) {
			String fieldName = e.getSimpleName().toString();
			return createFieldDescriptionIfApplicableAndRemoveElementFromListWhenApplicable(e, p, fieldName);
		}
		generateIfNeededWarningForNotSupportedElementAndRemoveIt("Check that this field is public and not static", e);
		return Optional.empty();
	}

	@Override
	public Optional<AbstractFieldDescription> visitExecutable(ExecutableElement e,
			ProvidesMatchersAnnotatedElementMatcherMirror p) {
		if (e.getModifiers().contains(Modifier.PUBLIC) && e.getParameters().size() == 0
				&& !e.getModifiers().contains(Modifier.STATIC)) {
			String simpleName = e.getSimpleName().toString();
			if (simpleName.matches("^((get)|(is)).*")) {
				return visiteExecutableGet(e, "^(get)|(is)", p);
			}
		}
		generateIfNeededWarningForNotSupportedElementAndRemoveIt(
				"Check that this method is public, doesn't have any parameter and is named isXXX or getXXX", e);
		return Optional.empty();
	}

	private void generateIfNeededWarningForNotSupportedElementAndRemoveIt(String description, Element e) {
		if (removeFromIgnoreList.apply(e)) {
			processingEnv.getMessager().printMessage(Kind.MANDATORY_WARNING,
					"One of the annotation is not supported as this location ; " + description, e);
		}
	}

	private Optional<AbstractFieldDescription> visiteExecutableGet(ExecutableElement e, String prefix,
			ProvidesMatchersAnnotatedElementMatcherMirror p) {
		String methodName = e.getSimpleName().toString();
		String fieldNameDirect = methodName.replaceFirst(prefix, "");
		String fieldName = fieldNameDirect.substring(0, 1).toLowerCase() + fieldNameDirect.substring(1);
		return createFieldDescriptionIfApplicableAndRemoveElementFromListWhenApplicable(e, p, fieldName);
	}

	public Optional<AbstractFieldDescription> createFieldDescriptionIfApplicableAndRemoveElementFromListWhenApplicable(
			Element e, ProvidesMatchersAnnotatedElementMatcherMirror p, String fieldName) {
		return removeIfNeededAndThenReturn(
				((e instanceof ExecutableElement) ? ((ExecutableElement) e).getReturnType() : e.asType())
						.accept(extractNameVisitor, false).map(f -> FieldDescriptionProvider.of(() -> p,
								new FieldDescriptionMirror(() -> p, fieldName, f, e))));
	}

	@Override
	protected Optional<AbstractFieldDescription> defaultAction(Element e,
			ProvidesMatchersAnnotatedElementMatcherMirror p) {
		return Optional.empty();
	}

}
