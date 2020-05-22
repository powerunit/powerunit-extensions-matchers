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
package ch.powerunit.extensions.matchers;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Locale;
import java.util.Map;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import ch.powerunit.TestSuite;

/**
 * @author borettim
 *
 */
public interface TestSuiteSupport extends TestSuite {
	default ProcessingEnvironment generateMockitoProcessingEnvironment() {
		TypeElement object = mock(TypeElement.class);
		Name objectName = mock(Name.class);
		when(object.getSimpleName()).thenReturn(objectName);
		when(objectName.toString()).thenReturn("Object");
		Name fullObjectName = mock(Name.class);
		when(object.getQualifiedName()).thenReturn(fullObjectName);
		when(fullObjectName.toString()).thenReturn("java.lang.Object");
		TypeElement provide = mock(TypeElement.class);
		objectName = mock(Name.class);
		when(provide.getSimpleName()).thenReturn(objectName);
		when(objectName.toString()).thenReturn("ProvideMatchers");
		fullObjectName = mock(Name.class);
		when(provide.getQualifiedName()).thenReturn(fullObjectName);
		when(fullObjectName.toString()).thenReturn("ch.powerunit.extensions.matchers.ProvideMatchers");
		Elements elements = mock(Elements.class);
		ProcessingEnvironment processingEnv = mock(ProcessingEnvironment.class);
		when(processingEnv.getMessager()).thenReturn(mock(Messager.class));
		when(processingEnv.getElementUtils()).thenReturn(elements);
		when(processingEnv.getFiler()).thenReturn(mock(Filer.class));
		when(processingEnv.getLocale()).thenReturn(Locale.getDefault());
		when(processingEnv.getOptions()).thenReturn(mock(Map.class));
		when(processingEnv.getTypeUtils()).thenReturn(mock(Types.class));
		when(processingEnv.getSourceVersion()).thenReturn(SourceVersion.RELEASE_8);
		when(elements.getTypeElement("java.lang.Object")).thenReturn(object);
		when(elements.getTypeElement("ch.powerunit.extensions.matchers.ProvideMatchers")).thenReturn(provide);

		when(object.asType()).thenReturn(mock(TypeMirror.class));
		return processingEnv;
	}

	default RoundEnvironment generateMockitoRoundEnvironment() {
		RoundEnvironment roundEnv = mock(RoundEnvironment.class);
		return roundEnv;
	}
}
