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
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic.Kind;

import ch.powerunit.extensions.matchers.common.AbstractSimpleElementVisitor;

class ProvidesMatchersElementVisitor extends AbstractSimpleElementVisitor<Optional<TypeElement>, Void, RoundMirror>
		implements RoundMirrorSupport {

	public ProvidesMatchersElementVisitor(RoundMirror roundMirror) {
		super(roundMirror);
	}

	@Override
	public Optional<TypeElement> visitType(TypeElement e, Void p) {
		switch (e.getKind()) {
		case ENUM:
			errorForType(e, "enum");
			return Optional.empty();
		case INTERFACE:
			errorForType(e, "interface");
			return Optional.empty();
		default:
			return Optional.of(e);
		}
	}

	@Override
	protected Optional<TypeElement> defaultAction(Element e, Void p) {
		errorForType(e, "unexpected element");
		return Optional.empty();
	}

	private void errorForType(Element e, String type) {
		getProcessingEnv().getMessager().printMessage(Kind.ERROR, "The annotation `ProvideMatchers` is used on an "
				+ type
				+ ", which is not supported. Since version 0.2.0 of powerunit-extension-matchers this is considered as an error.",
				e, support.getProvideMatchersAnnotation(e));
	}

}