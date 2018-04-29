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
package ch.powerunit.extensions.matchers.provideprocessor.extension.hamcrestutility;

import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.Collections;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;

import org.mockito.Mock;

import ch.powerunit.Rule;
import ch.powerunit.Test;
import ch.powerunit.TestRule;
import ch.powerunit.TestSuite;
import ch.powerunit.extensions.matchers.provideprocessor.RoundMirror;
import ch.powerunit.extensions.matchers.provideprocessor.fields.AbstractFieldDescription;
import ch.powerunit.extensions.matchers.provideprocessor.fields.CollectionFieldDescription;
import ch.powerunit.extensions.matchers.provideprocessor.fields.FieldDSLMethod;
import ch.powerunit.extensions.matchers.provideprocessor.fields.StringFieldDescription;

/**
 * @author borettim
 *
 */
public class AbstractHamcrestUtilityAutomatedExtensionTest implements TestSuite {

	@Mock
	private RoundMirror roundMirror;

	@Mock
	private ProcessingEnvironment processingEnv;

	@Mock
	private Elements elements;

	@Mock
	private TypeElement targetTypeElement;

	@Mock
	private TypeElement otherTypeElement;

	@Mock
	private TypeMirror otherTypeMirror;

	@Mock
	private FieldDSLMethod fieldDSLMethod;

	@Mock
	private StringFieldDescription stringField;

	@Mock
	private CollectionFieldDescription collectionField;

	public class TestClass extends AbstractHamcrestUtilityAutomatedExtension {

		public TestClass(RoundMirror roundMirror, String targetType) {
			super(roundMirror, targetType);
		}

		@Override
		protected Collection<FieldDSLMethod> acceptHamcrestUtility(AbstractFieldDescription field) {
			return Collections.singleton(fieldDSLMethod);
		}

	}

	private void prepare() {
		when(roundMirror.getProcessingEnv()).thenReturn(processingEnv);
		when(processingEnv.getElementUtils()).thenReturn(elements);
		when(elements.getTypeElement("com.nitorcreations.Matchers")).thenReturn(targetTypeElement);
		when(elements.getTypeElement("target")).thenReturn(otherTypeElement);
		when(otherTypeElement.asType()).thenReturn(otherTypeMirror);

	}

	@Rule
	public final TestRule rules = mockitoRule().around(before(this::prepare));

	@Test
	public void testConstructor() {
		TestClass tc = new TestClass(roundMirror, "target");
		assertThat(tc.getExpectedElement()).is("com.nitorcreations.Matchers");
		assertThat(tc.getTargetElement()).is(targetTypeElement);
		assertThat(tc.knownType).is(otherTypeMirror);
	}

	@Test
	public void testAcceptWithStringThenEmpty() {
		TestClass tc = new TestClass(roundMirror, "target");
		assertThatIterable(tc.accept(stringField)).hasSize(0);
	}

	@Test
	public void testAcceptWithCollectionAndNotEmptyGenericThenNotEmpty() {
		TestClass tc = new TestClass(roundMirror, "target");
		when(collectionField.getGeneric()).thenReturn("xxx");
		assertThatIterable(tc.accept(collectionField)).hasSize(1);
	}

	@Test
	public void testAcceptWithCollectionAndEmptyGenericThenEmpty() {
		TestClass tc = new TestClass(roundMirror, "target");
		when(collectionField.getGeneric()).thenReturn("");
		assertThatIterable(tc.accept(collectionField)).hasSize(0);
	}

}
