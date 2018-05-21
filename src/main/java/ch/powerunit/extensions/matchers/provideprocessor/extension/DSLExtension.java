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
package ch.powerunit.extensions.matchers.provideprocessor.extension;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.function.Supplier;

import ch.powerunit.extensions.matchers.ComplementaryExpositionMethod;
import ch.powerunit.extensions.matchers.provideprocessor.ProvidesMatchersAnnotatedElementData;
import ch.powerunit.extensions.matchers.provideprocessor.dsl.DSLMethod;

public interface DSLExtension {
	
	static final Collection<DSLExtension> EXTENSION = Collections.unmodifiableList(Arrays.asList(
			new ContainsDSLExtension(), new ArrayContainingDSLExtension(), new HasItemsExtension(),
			new ContainsInAnyOrderDSLExtension(), new ArrayContainingInAnyOrderDSLExtension(), new AnyOfExtension()));
	
	ComplementaryExpositionMethod supportedEnum();

	default boolean accept(ComplementaryExpositionMethod[] values) {
		return values != null && Arrays.stream(values).filter(o -> o == supportedEnum()).findAny().isPresent();
	}

	Collection<Supplier<DSLMethod>> getDSLMethodFor(ProvidesMatchersAnnotatedElementData element);

}
