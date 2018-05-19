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

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;

import ch.powerunit.Rule;
import ch.powerunit.Test;
import ch.powerunit.TestRule;
import ch.powerunit.extensions.matchers.TestSuiteSupport;

public class AbstractRoundMirrorSupportTest
		implements TestSuiteSupport, AbstractRoundMirrorSupport<AbstractRoundMirrorReferenceToProcessingEnv> {
	private RoundEnvironment roundEnv;

	private ProcessingEnvironment processingEnv;

	private AbstractRoundMirrorReferenceToProcessingEnv roundMirror;

	@Rule
	public final TestRule rules = mockitoRule().around(before(this::prepare));

	private void prepare() {
		this.roundEnv = generateMockitoRoundEnvironment();
		this.processingEnv = generateMockitoProcessingEnvironment();
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

}
