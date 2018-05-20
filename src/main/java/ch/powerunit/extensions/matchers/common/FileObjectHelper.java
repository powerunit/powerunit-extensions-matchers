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

import java.io.Closeable;
import java.util.function.Consumer;

import javax.tools.FileObject;

/**
 * These are method to write class.
 * 
 * @author borettim
 *
 */
public final class FileObjectHelper {
	private FileObjectHelper() {
	}

	@FunctionalInterface
	public static interface ConsumerWithException<S> {
		void accept(S input) throws Exception;
	}

	@FunctionalInterface
	public static interface SupplierWithException<T> {
		T get() throws Exception;
	}

	@FunctionalInterface
	public static interface FunctionWithException<T, R> {
		R apply(T input) throws Exception;
	}

	public static <T extends FileObject, S extends Closeable> boolean processFileWithIOException(
			SupplierWithException<T> generateFileObject, FunctionWithException<T, S> openStream,
			ConsumerWithException<S> actions, Consumer<Exception> exceptionHandler) {
		try {
			try (S wjfo = openStream.apply(generateFileObject.get())) {
				actions.accept(wjfo);
			}
		} catch (Exception e) {
			exceptionHandler.accept(e);
			return false;
		}
		return true;
	}
}
