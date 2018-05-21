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
package ch.powerunit.extensions.matchers.common.lang;

import java.util.function.UnaryOperator;

import ch.powerunit.extensions.matchers.common.ListJoining;

/**
 * @author borettim
 *
 */
public interface ListJoiningAround<E> {
	ListJoining<E> withFinalFunction(UnaryOperator<String> finalize);

	default ListJoining<E> withPrefixAndSuffix(String prefix, String suffix) {
		return withFinalFunction(s -> prefix + s + suffix);
	}

	default ListJoining<E> withoutSuffixAndPrefix() {
		return withPrefixAndSuffix("", "");
	}

	default ListJoining<E> withOptionalPrefixAndSuffix(String prefix, String suffix) {
		return withFinalFunction(s -> s.isEmpty() ? "" : (prefix + s + suffix));
	}
}
