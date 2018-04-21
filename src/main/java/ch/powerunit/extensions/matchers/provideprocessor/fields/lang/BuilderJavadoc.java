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
package ch.powerunit.extensions.matchers.provideprocessor.fields.lang;

import java.util.Optional;

public interface BuilderJavadoc {
	BuilderImplementation withJavaDoc(Optional<String> addToDescription, Optional<String> param, Optional<String> see);

	default BuilderImplementation withJavaDoc(String addToDescription, String param, String see) {
		return withJavaDoc(Optional.of(addToDescription), Optional.of(param), Optional.of(see));
	}

	default BuilderImplementation withJavaDoc(String addToDescription, String param) {
		return withJavaDoc(Optional.of(addToDescription), Optional.of(param), Optional.empty());
	}

	default BuilderImplementation withJavaDoc(String addToDescription) {
		return withJavaDoc(Optional.of(addToDescription), Optional.empty(), Optional.empty());
	}

	default BuilderImplementation withDefaultJavaDoc() {
		return withJavaDoc(Optional.empty(), Optional.empty(), Optional.empty());
	}

}
