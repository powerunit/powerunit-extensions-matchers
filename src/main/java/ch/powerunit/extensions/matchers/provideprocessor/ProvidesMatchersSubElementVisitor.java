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

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.VariableElement;
import javax.tools.Diagnostic.Kind;

import ch.powerunit.extensions.matchers.common.AbstractSimpleElementVisitor;
import ch.powerunit.extensions.matchers.provideprocessor.fields.AbstractFieldDescription;
import ch.powerunit.extensions.matchers.provideprocessor.fields.FieldDescriptionMirror;
import ch.powerunit.extensions.matchers.provideprocessor.fields.FieldDescriptionProvider;

/**
 * @author borettim
 *
 */
public class ProvidesMatchersSubElementVisitor extends
		AbstractSimpleElementVisitor<Optional<AbstractFieldDescription>, ProvidesMatchersAnnotatedElementFieldMatcherMirror, RoundMirror> {

	private final Function<Element, Boolean> removeFromIgnoreList;
	private final NameExtractorVisitor extractNameVisitor;

	public ProvidesMatchersSubElementVisitor(RoundMirror roundMirror) {
		super(roundMirror);
		this.removeFromIgnoreList = roundMirror::removeFromIgnoreList;
		this.extractNameVisitor = new NameExtractorVisitor(roundMirror);
	}

	public Optional<AbstractFieldDescription> removeIfNeededAndThenReturn(
			Optional<AbstractFieldDescription> fieldDescription) {
		fieldDescription.ifPresent(f -> removeFromIgnoreList.apply(f.getFieldElement()));
		return fieldDescription;
	}

	@Override
	public Optional<AbstractFieldDescription> visitVariable(VariableElement e,
			ProvidesMatchersAnnotatedElementFieldMatcherMirror p) {
		if (e.getModifiers().contains(Modifier.PUBLIC) && !e.getModifiers().contains(Modifier.STATIC)) {
			String fieldName = getSimpleName(e);
			return createFieldDescriptionIfApplicableAndRemoveElementFromListWhenApplicable(e, p, fieldName);
		}
		generateIfNeededErrorForNotSupportedElementAndRemoveIt("Check that this field is public and not static", e);
		return Optional.empty();
	}

	@Override
	public Optional<AbstractFieldDescription> visitExecutable(ExecutableElement e,
			ProvidesMatchersAnnotatedElementFieldMatcherMirror p) {
		if (e.getModifiers().contains(Modifier.PUBLIC) && e.getParameters().size() == 0
				&& !e.getModifiers().contains(Modifier.STATIC)) {
			String simpleName = getSimpleName(e);
			if (simpleName.matches("^((get)|(is)).*")) {
				return visiteExecutableGet(e, "^(get)|(is)", p);
			}
		}
		generateIfNeededErrorForNotSupportedElementAndRemoveIt(
				"Check that this method is public, doesn't have any parameter and is named isXXX or getXXX", e);
		return Optional.empty();
	}

	private void generateIfNeededErrorForNotSupportedElementAndRemoveIt(String description, Element e) {
		if (removeFromIgnoreList.apply(e)) {
			getProcessingEnv().getMessager().printMessage(Kind.ERROR,
					"One of the annotation is not supported as this location ; " + description
							+ ". Since version 0.2.0 of powerunit-extension-matchers this is considered as an error.",
					e);
		}
	}

	private Optional<AbstractFieldDescription> visiteExecutableGet(ExecutableElement e, String prefix,
			ProvidesMatchersAnnotatedElementFieldMatcherMirror p) {
		String methodName = getSimpleName(e);
		String fieldNameDirect = methodName.replaceFirst(prefix, "");
		String fieldName = fieldNameDirect.substring(0, 1).toLowerCase() + fieldNameDirect.substring(1);
		return createFieldDescriptionIfApplicableAndRemoveElementFromListWhenApplicable(e, p, fieldName);
	}

	public Optional<AbstractFieldDescription> createFieldDescriptionIfApplicableAndRemoveElementFromListWhenApplicable(
			Element e, ProvidesMatchersAnnotatedElementFieldMatcherMirror p, String fieldName) {
		return removeIfNeededAndThenReturn(
				((e instanceof ExecutableElement) ? ((ExecutableElement) e).getReturnType() : e.asType())
						.accept(extractNameVisitor, false).map(f -> FieldDescriptionProvider.of(() -> p,
								new FieldDescriptionMirror(() -> p, fieldName, f, e))));
	}

	@Override
	protected Optional<AbstractFieldDescription> defaultAction(Element e,
			ProvidesMatchersAnnotatedElementFieldMatcherMirror p) {
		return Optional.empty();
	}

}
