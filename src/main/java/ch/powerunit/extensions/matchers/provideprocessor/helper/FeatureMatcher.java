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
package ch.powerunit.extensions.matchers.provideprocessor.helper;

import static java.lang.String.format;

import ch.powerunit.extensions.matchers.common.RessourceLoaderHelper;

public final class FeatureMatcher {

	private static final String MATCHER_FORMAT = RessourceLoaderHelper.loadRessource(FeatureMatcher.class,
			"FeatureMatcher.txt");

	private final String matcher;

	public FeatureMatcher(String prefix, String genericOnTargetMatcher, String matcherOverClass,
			String genericOnMatcherOverClass, String targetObject, String matcherDescription, String accessor) {
		matcher = format(MATCHER_FORMAT, prefix, genericOnTargetMatcher, matcherOverClass, genericOnMatcherOverClass,
				targetObject, matcherDescription, accessor);
	}

	public String toString() {
		return matcher;
	}

}
