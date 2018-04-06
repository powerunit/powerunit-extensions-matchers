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

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.SimpleElementVisitor8;
import javax.tools.Diagnostic.Kind;

/**
 * @author borettim
 *
 */
public class ProvidesMatchersSubElementVisitor
		extends SimpleElementVisitor8<Optional<FieldDescription>, ProvidesMatchersAnnotatedElementMirror> {

	private final RoundMirror roundMirror;
	private final NameExtractorVisitor extractNameVisitor;

	public ProvidesMatchersSubElementVisitor(RoundMirror roundMirror) {
		this.roundMirror = roundMirror;
		this.extractNameVisitor = new NameExtractorVisitor(roundMirror.getProcessingEnv());
	}

	public Optional<FieldDescription> removeIfNeededAndThenReturn(Optional<FieldDescription> fieldDescription) {
		fieldDescription.ifPresent(f -> roundMirror.removeFromIgnoreList(f.getFieldElement()));
		return fieldDescription;
	}

	@Override
	public Optional<FieldDescription> visitVariable(VariableElement e, ProvidesMatchersAnnotatedElementMirror p) {
		if (e.getModifiers().contains(Modifier.PUBLIC) && !e.getModifiers().contains(Modifier.STATIC)) {
			String fieldName = e.getSimpleName().toString();
			return createFieldDescriptionIfApplicableAndRemoveElementFromListWhenApplicable(e, p, fieldName);
		}
		if (roundMirror.isInsideIgnoreList(e)) {
			generateWarningForNotSupportedElementAndRemoveIt("Check that this field is public and not static", e);
		}
		return Optional.empty();
	}

	@Override
	public Optional<FieldDescription> visitExecutable(ExecutableElement e, ProvidesMatchersAnnotatedElementMirror p) {
		if (e.getModifiers().contains(Modifier.PUBLIC) && e.getParameters().size() == 0
				&& !e.getModifiers().contains(Modifier.STATIC)) {
			String simpleName = e.getSimpleName().toString();
			if (simpleName.matches("^((get)|(is)).*")) {
				return visiteExecutableGet(e, "^(get)|(is)", p);
			}
		}
		if (roundMirror.isInsideIgnoreList(e)) {
			generateWarningForNotSupportedElementAndRemoveIt(
					"Check that this method is public, doesn't have any parameter and is named isXXX or getXXX", e);
		}
		return Optional.empty();
	}

	private void generateWarningForNotSupportedElementAndRemoveIt(String description, Element e) {
		roundMirror.getProcessingEnv().getMessager().printMessage(Kind.MANDATORY_WARNING,
				"One of the annotation is not supported as this location ; " + description, e);
		roundMirror.removeFromIgnoreList(e);
	}

	private Optional<FieldDescription> visiteExecutableGet(ExecutableElement e, String prefix,
			ProvidesMatchersAnnotatedElementMirror p) {
		String methodName = e.getSimpleName().toString();
		String fieldNameDirect = methodName.replaceFirst(prefix, "");
		String fieldName = fieldNameDirect.substring(0, 1).toLowerCase() + fieldNameDirect.substring(1);
		return createFieldDescriptionIfApplicableAndRemoveElementFromListWhenApplicable(e, p, fieldName);
	}

	public Optional<FieldDescription> createFieldDescriptionIfApplicableAndRemoveElementFromListWhenApplicable(
			Element e, ProvidesMatchersAnnotatedElementMirror p, String fieldName) {
		return removeIfNeededAndThenReturn(
				((e instanceof ExecutableElement) ? ((ExecutableElement) e).getReturnType() : e.asType())
						.accept(extractNameVisitor, false).map(f -> new FieldDescription(p, fieldName, f, e)));
	}

	@Override
	protected Optional<FieldDescription> defaultAction(Element e, ProvidesMatchersAnnotatedElementMirror p) {
		return Optional.empty();
	}

}
