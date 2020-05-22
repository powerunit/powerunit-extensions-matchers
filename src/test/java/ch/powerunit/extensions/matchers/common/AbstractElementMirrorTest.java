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

import java.lang.annotation.Annotation;

import javax.lang.model.element.Element;

import org.mockito.Mock;
import org.mockito.Mockito;

import ch.powerunit.Rule;
import ch.powerunit.Test;
import ch.powerunit.TestRule;
import ch.powerunit.extensions.matchers.TestSuiteSupport;

/**
 * @author borettim
 *
 */
public class AbstractElementMirrorTest implements TestSuiteSupport {

	private AbstractRoundMirrorReferenceToProcessingEnv roundMirror;

	@Rule
	public final TestRule rules = mockitoRule().around(before(this::prepare));

	@Mock
	private Element element;

	@Mock
	private Annotation annotation;

	private void prepare() {
		roundMirror = new AbstractRoundMirrorReferenceToProcessingEnv(generateMockitoRoundEnvironment(),
				generateMockitoProcessingEnvironment()) {
		};
	}

	@Test
	public void testGetRoundMirror() {
		assertThat(new AbstractElementMirror<Element, AbstractRoundMirrorReferenceToProcessingEnv>(
				"java.lang.annotation.Annotation", roundMirror, element) {
		}.getRoundMirror()).is(sameInstance(roundMirror));
	}

	@Test
	public void testGetElement() {
		assertThat(new AbstractElementMirror<Element, AbstractRoundMirrorReferenceToProcessingEnv>(
				"java.lang.annotation.Annotation", roundMirror, element) {
		}.getElement()).is(sameInstance(element));
	}

	@Test(fastFail = false)
	public void testGetParamComment() {
		assertThat(new AbstractElementMirror<Element, AbstractRoundMirrorReferenceToProcessingEnv>(
				"java.lang.annotation.Annotation", roundMirror, element) {
		}.getParamComment()).is(" * \n");
		Mockito.when(roundMirror.getProcessingEnv().getElementUtils().getDocComment(element))
				.thenReturn("\n  @param test\n");
		assertThat(new AbstractElementMirror<Element, AbstractRoundMirrorReferenceToProcessingEnv>(
				"java.lang.annotation.Annotation", roundMirror, element) {
		}.getParamComment()).is(" * \n *  @param test\n");
	}
}
