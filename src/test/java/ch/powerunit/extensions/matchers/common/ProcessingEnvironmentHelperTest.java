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

import ch.powerunit.Rule;
import ch.powerunit.Test;
import ch.powerunit.TestRule;
import ch.powerunit.extensions.matchers.TestSuiteSupport;

/**
 * @author borettim
 *
 */
public class ProcessingEnvironmentHelperTest implements TestSuiteSupport, ProcessingEnvironmentHelper {

	private ProcessingEnvironment processingEnv;

	@Rule
	public final TestRule rules = mockitoRule().around(before(this::prepare));

	private void prepare() {
		processingEnv = generateMockitoProcessingEnvironment();
	}

	@Override
	public ProcessingEnvironment getProcessingEnv() {
		return processingEnv;
	}

	@Test(fastFail = false)
	public void testGetter() {
		assertThat(getOptions()).is(sameInstance(processingEnv.getOptions()));

		assertThat(getMessager()).is(sameInstance(processingEnv.getMessager()));

		assertThat(getFiler()).is(sameInstance(processingEnv.getFiler()));

		assertThat(getElementUtils()).is(sameInstance(processingEnv.getElementUtils()));

		assertThat(getTypeUtils()).is(sameInstance(processingEnv.getTypeUtils()));

		assertThat(getSourceVersion()).is(sameInstance(processingEnv.getSourceVersion()));

		assertThat(getLocale()).is(sameInstance(processingEnv.getLocale()));

	}

}
