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

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;

import org.mockito.Mock;

import ch.powerunit.Rule;
import ch.powerunit.Test;
import ch.powerunit.TestRule;
import ch.powerunit.extensions.matchers.TestSuiteSupport;

public class AbstractRoundMirrorSupportTest
		implements TestSuiteSupport, AbstractRoundMirrorSupport<AbstractRoundMirrorReferenceToProcessingEnv> {
	private RoundEnvironment roundEnv;

	private ProcessingEnvironment processingEnv;

	private AbstractRoundMirrorReferenceToProcessingEnv roundMirror;

	@Mock
	private TypeElement typeElement;

	@Mock
	private TypeMirror typeMirror;

	@Mock
	private TypeMirror typeMirror2;

	@Mock
	private TypeMirror typeMirror3;

	@Rule
	public final TestRule rules = mockitoRule().around(before(this::prepare));

	private void prepare() {
		this.roundEnv = generateMockitoRoundEnvironment();
		this.processingEnv = generateMockitoProcessingEnvironment();
		when(processingEnv.getElementUtils().getTypeElement("testme")).thenReturn(typeElement);
		when(typeElement.asType()).thenReturn(typeMirror);
		when(processingEnv.getTypeUtils().isSameType(typeMirror, typeMirror)).thenReturn(true);
		when(processingEnv.getTypeUtils().isSameType(typeMirror, typeMirror2)).thenReturn(false);
		when(processingEnv.getTypeUtils().isSameType(typeMirror2, typeMirror)).thenReturn(false);
		when(processingEnv.getTypeUtils().isAssignable(typeMirror, typeMirror)).thenReturn(true);
		when(processingEnv.getTypeUtils().isAssignable(typeMirror, typeMirror2)).thenReturn(true);
		when(processingEnv.getTypeUtils().isAssignable(typeMirror, typeMirror3)).thenReturn(false);
		when(processingEnv.getTypeUtils().erasure(typeMirror)).thenReturn(typeMirror);
		when(processingEnv.getTypeUtils().erasure(typeMirror2)).thenReturn(typeMirror2);
		when(processingEnv.getTypeUtils().erasure(typeMirror3)).thenReturn(typeMirror3);
		when(processingEnv.getTypeUtils().isSameType(typeMirror, typeMirror2)).thenReturn(false);
		this.roundMirror = new AbstractRoundMirrorReferenceToProcessingEnv(roundEnv, processingEnv) {
		};
	}

	@Override
	public AbstractRoundMirrorReferenceToProcessingEnv getRoundMirror() {
		return roundMirror;
	}

	@Test
	public void testGetProcessingEnv() {
		assertThat(getProcessingEnv()).is(sameInstance(processingEnv));
	}

	@Test
	public void testGetRoundEnv() {
		assertThat(getRoundEnv()).is(sameInstance(roundEnv));
	}

	@Test
	public void testGetMirrorFor() {
		assertThat(getMirrorFor("testme")).is(sameInstance(typeMirror));
	}

	@Test(fastFail = false)
	public void testIsSameType() {
		assertThat(isSameType(typeElement, typeMirror)).is(true);
		assertThat(isSameType(typeElement, typeMirror2)).is(false);
		assertThat(isSameType(null, typeMirror2)).is(false);
	}

	@Test(fastFail = false)
	public void testIsAssignableWithErasure() {
		assertThat(isAssignableWithErasure(typeElement, typeMirror)).is(true);
		assertThat(isAssignableWithErasure(typeElement, typeMirror2)).is(true);
		assertThat(isAssignableWithErasure(typeElement, typeMirror3)).is(false);
		assertThat(isAssignableWithErasure(null, typeMirror)).is(false);
	}

}
