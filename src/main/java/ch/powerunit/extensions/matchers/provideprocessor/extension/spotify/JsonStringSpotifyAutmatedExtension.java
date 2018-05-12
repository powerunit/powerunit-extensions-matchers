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
package ch.powerunit.extensions.matchers.provideprocessor.extension.spotify;

import java.util.Collection;
import java.util.Collections;

import ch.powerunit.extensions.matchers.provideprocessor.RoundMirror;
import ch.powerunit.extensions.matchers.provideprocessor.fields.FieldDSLMethod;
import ch.powerunit.extensions.matchers.provideprocessor.fields.StringFieldDescription;

/**
 * @author borettim
 *
 */
public class JsonStringSpotifyAutmatedExtension extends AbstractSpotifyAutomatedExtension {

	public JsonStringSpotifyAutmatedExtension(RoundMirror roundMirror) {
		super(roundMirror);
	}

	protected Collection<FieldDSLMethod> acceptJsonMatcher(StringFieldDescription field) {
		return Collections.singletonList(builderFor(field)
				.withDeclaration("AsJson", "org.hamcrest.Matcher<com.fasterxml.jackson.databind.JsonNode> matcher")
				.withJavaDoc("The object will be validated as an Json Object", "matcher the matcher on the json")
				.havingDefault("com.spotify.hamcrest.jackson.JsonMatchers.isJsonStringMatching(matcher)"));
	}

}
