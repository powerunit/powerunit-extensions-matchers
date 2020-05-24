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

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doReturn;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.argThat;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.Name;
import javax.lang.model.element.QualifiedNameable;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;

import org.mockito.Mock;

import ch.powerunit.Rule;
import ch.powerunit.Test;
import ch.powerunit.TestRule;
import ch.powerunit.TestSuite;

/**
 * @author borettim
 *
 */
public class RoundMirrorTest implements TestSuite {

	@Mock
	private RoundEnvironment roundEnv;

	@Mock
	private ProcessingEnvironment processingEnv;

	@Mock
	private Elements elements;

	@Mock
	private TypeElement te1;

	@Rule
	public final TestRule rules = mockitoRule().around(before(this::prepare));

	private Name mockName(String name) {
		Name mock = mock(Name.class);
		doReturn(name).when(mock).toString();
		return mock;
	}

	private void mockName(QualifiedNameable mock, String pkg, String name) {
		doReturn(mockName(name)).when(mock).getSimpleName();
		doReturn(mockName(pkg + "." + name)).when(mock).getQualifiedName();
	}

	private TypeElement mockTypeElement(String pkg, String name) {
		TypeElement mock = mock(TypeElement.class);
		doReturn(ElementKind.CLASS).when(mock).getKind();
		mockName(mock, pkg, name);
		return mock;
	}

	private void prepare() {
		when(processingEnv.getElementUtils()).thenReturn(elements);
		when(elements.getTypeElement(eq("java.lang.Object"))).thenReturn(te1);
		when(elements.getTypeElement(eq("ch.powerunit.extensions.matchers.ProvideMatchers"))).thenReturn(te1);
		when(elements.getTypeElement(eq("ch.powerunit.extensions.matchers.ProvideMatchers"))).thenReturn(te1);
		when(elements.getTypeElement(argThat(not(containsString("Matcher"))))).thenReturn(te1);
	}

	@Test
	public void testConstructor() {
		new RoundMirror(roundEnv, processingEnv);
	}

	@Test
	public void testLookupMatchableByTypeMissingMatchers() {
		TypeElement input = mock(TypeElement.class);
		mockName(input, "ch.powerunit", "MyTest");
		RoundMirror underTest = new RoundMirror(roundEnv, processingEnv);

		assertThat(underTest.lookupMatchableByType(input)).isNull();
	}

	@Test
	public void testLookupMatchableByTypeMatchersIsEmpty() {
		TypeElement matcher = mockTypeElement("ch.powerunit", "MyTestMatchers");
		when(matcher.getEnclosedElements()).thenReturn(Collections.emptyList());
		doReturn(matcher).when(elements).getTypeElement(eq("ch.powerunit.MyTestMatchers"));
		TypeElement input = mockTypeElement("ch.powerunit", "MyTest");
		RoundMirror underTest = new RoundMirror(roundEnv, processingEnv);

		assertThat(underTest.lookupMatchableByType(input)).isNull();
	}

	@Test
	public void testLookupMatchableByTypeMatchersIsMissingMetadataClass() {
		TypeElement notMetadata = mockTypeElement("ch.powerunit.MyTestMatchers", "NotMetadata");
		TypeElement matcher = mockTypeElement("ch.powerunit", "MyTestMatchers");
		when(matcher.getEnclosedElements()).thenReturn((List) Collections.singletonList(notMetadata));
		doReturn(matcher).when(elements).getTypeElement(eq("ch.powerunit.MyTestMatchers"));
		TypeElement input = mockTypeElement("ch.powerunit", "MyTest");
		RoundMirror underTest = new RoundMirror(roundEnv, processingEnv);

		assertThat(underTest.lookupMatchableByType(input)).isNull();
	}

	@Test
	public void testLookupMatchableByTypeMatchersMetadataClassIsEmpty() {
		TypeElement metadata = mockTypeElement("ch.powerunit.MyTestMatchers", "Metadata");
		TypeElement matcher = mockTypeElement("ch.powerunit", "MyTestMatchers");
		when(matcher.getEnclosedElements()).thenReturn((List) Collections.singletonList(metadata));
		doReturn(matcher).when(elements).getTypeElement(eq("ch.powerunit.MyTestMatchers"));
		TypeElement input = mockTypeElement("ch.powerunit", "MyTest");
		RoundMirror underTest = new RoundMirror(roundEnv, processingEnv);

		assertThat(underTest.lookupMatchableByType(input)).isNull();
	}

	@Test
	public void testLookupMatchableByTypeMatchersMetadataClassMissingCompatibility() {
		TypeElement metadata = mockTypeElement("ch.powerunit.MyTestMatchers", "Metadata");
		VariableElement notCompatibility = mock(VariableElement.class);
		doReturn(ElementKind.FIELD).when(notCompatibility).getKind();
		doReturn(mockName("something")).when(notCompatibility).getSimpleName();
		when(metadata.getEnclosedElements()).thenReturn((List) Collections.singletonList(notCompatibility));
		TypeElement matcher = mockTypeElement("ch.powerunit", "MyTestMatchers");
		when(matcher.getEnclosedElements()).thenReturn((List) Collections.singletonList(metadata));
		doReturn(matcher).when(elements).getTypeElement(eq("ch.powerunit.MyTestMatchers"));
		TypeElement input = mockTypeElement("ch.powerunit", "MyTest");
		RoundMirror underTest = new RoundMirror(roundEnv, processingEnv);

		assertThat(underTest.lookupMatchableByType(input)).isNull();
	}

	@Test
	public void testLookupMatchableByTypeMatchersMetadataClassHavingCompatibilityNotSupportedMissingConstant() {
		TypeElement metadata = mockTypeElement("ch.powerunit.MyTestMatchers", "Metadata");
		VariableElement notCompatibility = mock(VariableElement.class);
		doReturn(ElementKind.FIELD).when(notCompatibility).getKind();
		doReturn(mockName("something")).when(notCompatibility).getSimpleName();
		VariableElement compatibility = mock(VariableElement.class);
		doReturn(ElementKind.FIELD).when(compatibility).getKind();
		doReturn(mockName("COMPATIBILITY")).when(compatibility).getSimpleName();
		when(metadata.getEnclosedElements()).thenReturn((List) Arrays.asList(notCompatibility, compatibility));
		TypeElement matcher = mockTypeElement("ch.powerunit", "MyTestMatchers");
		when(matcher.getEnclosedElements()).thenReturn((List) Collections.singletonList(metadata));
		doReturn(matcher).when(elements).getTypeElement(eq("ch.powerunit.MyTestMatchers"));
		TypeElement input = mockTypeElement("ch.powerunit", "MyTest");
		RoundMirror underTest = new RoundMirror(roundEnv, processingEnv);

		assertThat(underTest.lookupMatchableByType(input)).isNull();
	}

	@Test
	public void testLookupMatchableByTypeMatchersMetadataClassHavingCompatibilityNotSupportedWrongType() {
		TypeElement metadata = mockTypeElement("ch.powerunit.MyTestMatchers", "Metadata");
		VariableElement notCompatibility = mock(VariableElement.class);
		doReturn(ElementKind.FIELD).when(notCompatibility).getKind();
		doReturn(mockName("something")).when(notCompatibility).getSimpleName();
		VariableElement compatibility = mock(VariableElement.class);
		doReturn(ElementKind.FIELD).when(compatibility).getKind();
		doReturn(mockName("COMPATIBILITY")).when(compatibility).getSimpleName();
		doReturn("x").when(compatibility).getConstantValue();
		when(metadata.getEnclosedElements()).thenReturn((List) Arrays.asList(notCompatibility, compatibility));
		TypeElement matcher = mockTypeElement("ch.powerunit", "MyTestMatchers");
		when(matcher.getEnclosedElements()).thenReturn((List) Collections.singletonList(metadata));
		doReturn(matcher).when(elements).getTypeElement(eq("ch.powerunit.MyTestMatchers"));
		TypeElement input = mockTypeElement("ch.powerunit", "MyTest");
		RoundMirror underTest = new RoundMirror(roundEnv, processingEnv);

		assertThat(underTest.lookupMatchableByType(input)).isNull();
	}

	@Test
	public void testLookupMatchableByTypeMatchersMetadataClassHavingCompatibilityMissingMatcherInterface() {
		TypeElement metadata = mockTypeElement("ch.powerunit.MyTestMatchers", "Metadata");
		VariableElement notCompatibility = mock(VariableElement.class);
		doReturn(ElementKind.FIELD).when(notCompatibility).getKind();
		doReturn(mockName("something")).when(notCompatibility).getSimpleName();
		VariableElement compatibility = mock(VariableElement.class);
		doReturn(ElementKind.FIELD).when(compatibility).getKind();
		doReturn(mockName("COMPATIBILITY")).when(compatibility).getSimpleName();
		doReturn(0L).when(compatibility).getConstantValue();
		when(metadata.getEnclosedElements()).thenReturn((List) Arrays.asList(notCompatibility, compatibility));
		TypeElement matcher = mockTypeElement("ch.powerunit", "MyTestMatchers");
		when(matcher.getEnclosedElements()).thenReturn((List) Collections.singletonList(metadata));
		doReturn(matcher).when(elements).getTypeElement(eq("ch.powerunit.MyTestMatchers"));
		TypeElement input = mockTypeElement("ch.powerunit", "MyTest");
		RoundMirror underTest = new RoundMirror(roundEnv, processingEnv);

		assertThat(underTest.lookupMatchableByType(input)).isNull();
	}

	@Test
	public void testLookupMatchableByTypeMatchersMetadataClassHavingCompatibilityHavingMatcherInterfaceButNoDSL() {
		TypeElement metadata = mockTypeElement("ch.powerunit.MyTestMatchers", "Metadata");
		VariableElement notCompatibility = mock(VariableElement.class);
		doReturn(ElementKind.FIELD).when(notCompatibility).getKind();
		doReturn(mockName("something")).when(notCompatibility).getSimpleName();
		VariableElement compatibility = mock(VariableElement.class);
		doReturn(ElementKind.FIELD).when(compatibility).getKind();
		doReturn(mockName("COMPATIBILITY")).when(compatibility).getSimpleName();
		doReturn(0L).when(compatibility).getConstantValue();
		when(metadata.getEnclosedElements()).thenReturn((List) Arrays.asList(notCompatibility, compatibility));
		TypeElement matcherInterface = mockTypeElement("ch.powerunit.MyTestMatchers", "MyTestMatcher");
		TypeElement matcher = mockTypeElement("ch.powerunit", "MyTestMatchers");
		when(matcher.getEnclosedElements()).thenReturn((List) Arrays.asList(metadata, matcherInterface));
		doReturn(matcher).when(elements).getTypeElement(eq("ch.powerunit.MyTestMatchers"));
		TypeElement input = mockTypeElement("ch.powerunit", "MyTest");
		RoundMirror underTest = new RoundMirror(roundEnv, processingEnv);

		Matchable result = underTest.lookupMatchableByType(input);
		assertThat(result).isNotNull();
		assertThat(result.getMethodShortClassName()).is("myTest");
		assertThat(result.hasWithSameValue()).is(false);
		assertThat(result.getFullyQualifiedNameOfGeneratedClass()).is("ch.powerunit.MyTestMatchers");
		assertThat(result.getSimpleNameOfGeneratedInterfaceMatcher()).is("MyTestMatcher");
	}

	@Test
	public void testLookupMatchableByTypeMatchersMetadataClassHavingCompatibilityHavingMatcherInterfaceButNoWithDSL() {
		TypeElement metadata = mockTypeElement("ch.powerunit.MyTestMatchers", "Metadata");
		VariableElement notCompatibility = mock(VariableElement.class);
		doReturn(ElementKind.FIELD).when(notCompatibility).getKind();
		doReturn(mockName("something")).when(notCompatibility).getSimpleName();
		VariableElement compatibility = mock(VariableElement.class);
		doReturn(ElementKind.FIELD).when(compatibility).getKind();
		doReturn(mockName("COMPATIBILITY")).when(compatibility).getSimpleName();
		doReturn(0L).when(compatibility).getConstantValue();
		when(metadata.getEnclosedElements()).thenReturn((List) Arrays.asList(notCompatibility, compatibility));
		TypeElement matcherInterface = mockTypeElement("ch.powerunit.MyTestMatchers", "MyTestMatcher");
		TypeElement matcher = mockTypeElement("ch.powerunit", "MyTestMatchers");
		ExecutableElement notWithSameValue = mock(ExecutableElement.class);
		doReturn(ElementKind.METHOD).when(notWithSameValue).getKind();
		doReturn(mockName("anyThing")).when(notWithSameValue).getSimpleName();
		when(matcher.getEnclosedElements())
				.thenReturn((List) Arrays.asList(metadata, matcherInterface, notWithSameValue));
		doReturn(matcher).when(elements).getTypeElement(eq("ch.powerunit.MyTestMatchers"));
		TypeElement input = mockTypeElement("ch.powerunit", "MyTest");
		RoundMirror underTest = new RoundMirror(roundEnv, processingEnv);

		Matchable result = underTest.lookupMatchableByType(input);
		assertThat(result).isNotNull();
		assertThat(result.getMethodShortClassName()).is("myTest");
		assertThat(result.hasWithSameValue()).is(false);
		assertThat(result.getFullyQualifiedNameOfGeneratedClass()).is("ch.powerunit.MyTestMatchers");
		assertThat(result.getSimpleNameOfGeneratedInterfaceMatcher()).is("MyTestMatcher");
	}

	@Test
	public void testLookupMatchableByTypeMatchersMetadataClassHavingCompatibilityHavingMatcherInterfaceButNoStaticWithDSL() {
		TypeElement metadata = mockTypeElement("ch.powerunit.MyTestMatchers", "Metadata");
		VariableElement notCompatibility = mock(VariableElement.class);
		doReturn(ElementKind.FIELD).when(notCompatibility).getKind();
		doReturn(mockName("something")).when(notCompatibility).getSimpleName();
		VariableElement compatibility = mock(VariableElement.class);
		doReturn(ElementKind.FIELD).when(compatibility).getKind();
		doReturn(mockName("COMPATIBILITY")).when(compatibility).getSimpleName();
		doReturn(0L).when(compatibility).getConstantValue();
		when(metadata.getEnclosedElements()).thenReturn((List) Arrays.asList(notCompatibility, compatibility));
		TypeElement matcherInterface = mockTypeElement("ch.powerunit.MyTestMatchers", "MyTestMatcher");
		TypeElement matcher = mockTypeElement("ch.powerunit", "MyTestMatchers");
		ExecutableElement notWithSameValue = mock(ExecutableElement.class);
		doReturn(ElementKind.METHOD).when(notWithSameValue).getKind();
		doReturn(mockName("anyThing")).when(notWithSameValue).getSimpleName();
		ExecutableElement withSameValue = mock(ExecutableElement.class);
		doReturn(ElementKind.METHOD).when(withSameValue).getKind();
		doReturn(mockName("myTestWithSameValue")).when(withSameValue).getSimpleName();
		when(matcher.getEnclosedElements())
				.thenReturn((List) Arrays.asList(metadata, matcherInterface, notWithSameValue, withSameValue));
		doReturn(matcher).when(elements).getTypeElement(eq("ch.powerunit.MyTestMatchers"));
		TypeElement input = mockTypeElement("ch.powerunit", "MyTest");
		RoundMirror underTest = new RoundMirror(roundEnv, processingEnv);

		Matchable result = underTest.lookupMatchableByType(input);
		assertThat(result).isNotNull();
		assertThat(result.getMethodShortClassName()).is("myTest");
		assertThat(result.hasWithSameValue()).is(false);
		assertThat(result.getFullyQualifiedNameOfGeneratedClass()).is("ch.powerunit.MyTestMatchers");
		assertThat(result.getSimpleNameOfGeneratedInterfaceMatcher()).is("MyTestMatcher");
	}

	@Test
	public void testLookupMatchableByTypeMatchersMetadataClassHavingCompatibilityHavingMatcherInterfaceAndStaticWithDSL() {
		TypeElement metadata = mockTypeElement("ch.powerunit.MyTestMatchers", "Metadata");
		VariableElement notCompatibility = mock(VariableElement.class);
		doReturn(ElementKind.FIELD).when(notCompatibility).getKind();
		doReturn(mockName("something")).when(notCompatibility).getSimpleName();
		VariableElement compatibility = mock(VariableElement.class);
		doReturn(ElementKind.FIELD).when(compatibility).getKind();
		doReturn(mockName("COMPATIBILITY")).when(compatibility).getSimpleName();
		doReturn(0L).when(compatibility).getConstantValue();
		when(metadata.getEnclosedElements()).thenReturn((List) Arrays.asList(notCompatibility, compatibility));
		TypeElement matcherInterface = mockTypeElement("ch.powerunit.MyTestMatchers", "MyTestMatcher");
		TypeElement matcher = mockTypeElement("ch.powerunit", "MyTestMatchers");
		ExecutableElement notWithSameValue = mock(ExecutableElement.class);
		doReturn(ElementKind.METHOD).when(notWithSameValue).getKind();
		doReturn(mockName("anyThing")).when(notWithSameValue).getSimpleName();
		ExecutableElement withSameValue = mock(ExecutableElement.class);
		doReturn(ElementKind.METHOD).when(withSameValue).getKind();
		doReturn(mockName("myTestWithSameValue")).when(withSameValue).getSimpleName();
		doReturn(Collections.singleton(Modifier.STATIC)).when(withSameValue).getModifiers();
		when(matcher.getEnclosedElements())
				.thenReturn((List) Arrays.asList(metadata, matcherInterface, notWithSameValue, withSameValue));
		doReturn(matcher).when(elements).getTypeElement(eq("ch.powerunit.MyTestMatchers"));
		TypeElement input = mockTypeElement("ch.powerunit", "MyTest");
		RoundMirror underTest = new RoundMirror(roundEnv, processingEnv);

		Matchable result = underTest.lookupMatchableByType(input);
		assertThat(result).isNotNull();
		assertThat(result.getMethodShortClassName()).is("myTest");
		assertThat(result.hasWithSameValue()).is(true);
		assertThat(result.getFullyQualifiedNameOfGeneratedClass()).is("ch.powerunit.MyTestMatchers");
		assertThat(result.getSimpleNameOfGeneratedInterfaceMatcher()).is("MyTestMatcher");
	}
}
