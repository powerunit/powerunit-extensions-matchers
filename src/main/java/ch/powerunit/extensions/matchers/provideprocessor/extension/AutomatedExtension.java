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
package ch.powerunit.extensions.matchers.provideprocessor.extension;

import java.util.Collection;
import java.util.function.Supplier;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.TypeElement;

import ch.powerunit.extensions.matchers.provideprocessor.ProvidesMatchersAnnotatedElementData;
import ch.powerunit.extensions.matchers.provideprocessor.RoundMirror;
import ch.powerunit.extensions.matchers.provideprocessor.RoundMirrorSupport;
import ch.powerunit.extensions.matchers.provideprocessor.dsl.DSLMethod;
import ch.powerunit.extensions.matchers.provideprocessor.fields.AbstractFieldDescription;
import ch.powerunit.extensions.matchers.provideprocessor.fields.FieldDSLMethod;
import ch.powerunit.extensions.matchers.provideprocessor.fields.FieldDSLMethodBuilder;
import ch.powerunit.extensions.matchers.provideprocessor.fields.lang.BuilderDeclaration;

public abstract class AutomatedExtension implements RoundMirrorSupport {

	private final String expectedElement;

	private final TypeElement targetElement;

	protected final RoundMirror roundMirror;

	protected final ProcessingEnvironment processingEnv;

	public AutomatedExtension(RoundMirror roundMirror, String expectedElement) {
		this.expectedElement = expectedElement;
		this.roundMirror = roundMirror;
		this.processingEnv = roundMirror.getProcessingEnv();
		this.targetElement = processingEnv.getElementUtils().getTypeElement(expectedElement);
	}

	public abstract Collection<FieldDSLMethod> accept(AbstractFieldDescription field);

	public abstract Collection<Supplier<DSLMethod>> accept(ProvidesMatchersAnnotatedElementData clazz);

	protected BuilderDeclaration builderFor(AbstractFieldDescription field) {
		return FieldDSLMethodBuilder.of(field);
	}

	public final boolean isPresent() {
		return targetElement != null;
	}

	public final String getExpectedElement() {
		return expectedElement;
	}

	public final TypeElement getTargetElement() {
		return targetElement;
	}

	@Override
	public RoundMirror getRoundMirror() {
		return roundMirror;
	}
}