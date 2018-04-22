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

import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.function.Supplier;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;

import org.mockito.Mock;
import org.mockito.Mockito;

import ch.powerunit.Rule;
import ch.powerunit.Test;
import ch.powerunit.TestRule;
import ch.powerunit.TestSuite;
import ch.powerunit.extensions.matchers.provideprocessor.DSLMethod;
import ch.powerunit.extensions.matchers.provideprocessor.ProvidesMatchersAnnotatedElementData;
import ch.powerunit.extensions.matchers.provideprocessor.RoundMirror;
import ch.powerunit.extensions.matchers.provideprocessor.fields.FieldDSLMethod;
import ch.powerunit.extensions.matchers.provideprocessor.fields.FieldDescriptionMetaData;

public class AutomatedExtensionTest implements TestSuite {

	@Mock
	private RoundMirror roundMirror;

	@Mock
	private ProcessingEnvironment processingEnv;

	@Mock
	private Elements elements;

	@Mock
	private TypeElement typeElement;

	@Rule
	public final TestRule rules = mockitoRule().around(before(this::prepare));

	private class TestAutomatedExtension extends AutomatedExtension {
		public TestAutomatedExtension(RoundMirror roundMirror, String expectedElement) {
			super(roundMirror, expectedElement);
		}

		@Override
		public Collection<FieldDSLMethod> accept(FieldDescriptionMetaData field) {
			return null;
		}

		@Override
		public Collection<Supplier<DSLMethod>> accept(ProvidesMatchersAnnotatedElementData clazz) {
			return null;
		}
	};

	private void prepare() {
		when(roundMirror.getProcessingEnv()).thenReturn(processingEnv);
		when(processingEnv.getElementUtils()).thenReturn(elements);
	}

	@Test
	public void testIsPresentTrueWhenNotNull() {
		when(elements.getTypeElement(Mockito.anyString())).thenReturn(typeElement);
		AutomatedExtension underTest = new TestAutomatedExtension(roundMirror, "target");
		assertThat(underTest.isPresent()).is(true);
	}

	@Test
	public void testIsPresentFalseWhenNull() {
		when(elements.getTypeElement(Mockito.anyString())).thenReturn(null);
		AutomatedExtension underTest = new TestAutomatedExtension(roundMirror, "target");
		assertThat(underTest.isPresent()).is(false);
	}

}
