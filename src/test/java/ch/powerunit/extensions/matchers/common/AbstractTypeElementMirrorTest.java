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
package ch.powerunit.extensions.matchers.common;

import static org.mockito.Mockito.when;

import java.lang.annotation.Annotation;

import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;

import org.mockito.Mock;

import ch.powerunit.Rule;
import ch.powerunit.Test;
import ch.powerunit.TestRule;
import ch.powerunit.extensions.matchers.TestSuiteSupport;

/**
 * @author borettim
 *
 */
public class AbstractTypeElementMirrorTest implements TestSuiteSupport {

	private AbstractRoundMirrorReferenceToProcessingEnv roundMirror;

	@Rule
	public final TestRule rules = mockitoRule().around(before(this::prepare));

	@Mock
	private TypeMirror objectTypeMirror;

	@Mock
	private TypeElement typeElement;

	@Mock
	private Name nameTypeElement;

	@Mock
	private Name nameFullTypeElement;

	@Mock
	private Annotation annotation;

	private void prepare() {
		roundMirror = new AbstractRoundMirrorReferenceToProcessingEnv(generateMockitoRoundEnvironment(),
				generateMockitoProcessingEnvironment()) {
		};
		objectTypeMirror = roundMirror.getProcessingEnv().getElementUtils().getTypeElement("java.lang.Object").asType();
		when(typeElement.getSimpleName()).thenReturn(nameTypeElement);
		when(nameTypeElement.toString()).thenReturn("sn");
		when(typeElement.getQualifiedName()).thenReturn(nameFullTypeElement);
		when(nameFullTypeElement.toString()).thenReturn("fn.sn");
	}

	@Test
	public void testGetFullyQualifiedNameOfClassAnnotated() {
		when(typeElement.getSuperclass()).thenReturn(objectTypeMirror);
		assertThat(new AbstractTypeElementMirror<AbstractRoundMirrorReferenceToProcessingEnv>(
				"java.lang.annotation.Annotation", roundMirror, typeElement) {
		}.getFullyQualifiedNameOfClassAnnotated()).is("fn.sn");
	}

	@Test
	public void testGetSimpleNameOfClassAnnotated() {
		when(typeElement.getSuperclass()).thenReturn(objectTypeMirror);
		assertThat(new AbstractTypeElementMirror<AbstractRoundMirrorReferenceToProcessingEnv>(
				"java.lang.annotation.Annotation", roundMirror, typeElement) {
		}.getSimpleNameOfClassAnnotated()).is("sn");
	}

	@Test
	public void testGetDefaultLinkForAnnotatedClass() {
		when(typeElement.getSuperclass()).thenReturn(objectTypeMirror);
		assertThat(new AbstractTypeElementMirror<AbstractRoundMirrorReferenceToProcessingEnv>(
				"java.lang.annotation.Annotation", roundMirror, typeElement) {
		}.getDefaultLinkForAnnotatedClass()).is("{@link fn.sn sn}");
	}

}
