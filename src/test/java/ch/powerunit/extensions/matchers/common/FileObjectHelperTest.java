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

import javax.tools.FileObject;

import org.mockito.Mock;

import ch.powerunit.Rule;
import ch.powerunit.Test;
import ch.powerunit.TestRule;
import ch.powerunit.TestSuite;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public final class FileObjectHelperTest implements TestSuite {

	@Mock
	private FileObject matcher;

	@Rule
	public final TestRule rules = mockitoRule();

	@Test
	public void testProcessFileWithIOExceptionWithoutException() throws IOException {
		when(matcher.openOutputStream()).thenReturn(new ByteArrayOutputStream());
		boolean result = FileObjectHelper.processFileWithIOException(() -> matcher, FileObject::openOutputStream,
				(s) -> {
				} , (e) -> {
				});
		assertThat(result).is(true);
	}

	@Test
	public void testProcessFileWithIOExceptionWithException() throws IOException {
		when(matcher.openOutputStream()).thenReturn(new ByteArrayOutputStream());
		boolean result = FileObjectHelper.processFileWithIOException(() -> matcher, FileObject::openOutputStream,
				(s) -> {
					throw new IOException("tst");
				} , (e) -> {
				});
		assertThat(result).is(false);
	}
}
