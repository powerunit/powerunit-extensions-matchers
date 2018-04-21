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
package ch.powerunit.extensions.matchers.provideprocessor;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.joining;

import javax.lang.model.element.TypeElement;

public abstract class ProvidesMatchersAnnotatedElementGenericMirror
		extends ProvidesMatchersAnnotatedElementJavadocMirror {

	protected final String generic;
	protected final String fullGeneric;
	protected final String simpleNameOfGeneratedInterfaceMatcher;

	public ProvidesMatchersAnnotatedElementGenericMirror(TypeElement typeElement, RoundMirror roundMirror) {
		super(typeElement, roundMirror);
		this.generic = parseGeneric(typeElement);
		this.fullGeneric = parseFullGeneric(typeElement);
		this.simpleNameOfGeneratedInterfaceMatcher = simpleNameOfClassAnnotatedWithProvideMatcher + "Matcher";
	}

	private static String parseFullGeneric(TypeElement typeElement) {
		return typeElement.getTypeParameters().stream()
				.map(t -> t.toString() + " extends "
						+ t.getBounds().stream().map(b -> b.toString()).collect(joining("&")))
				.collect(collectingAndThen(joining(","), r -> r.isEmpty() ? "" : ("<" + r + ">")));
	}

	private static String parseGeneric(TypeElement typeElement) {
		return typeElement.getTypeParameters().stream().map(t -> t.toString())
				.collect(collectingAndThen(joining(","), r -> r.isEmpty() ? "" : ("<" + r + ">")));
	}

	public String getSimpleNameOfGeneratedInterfaceMatcherWithGenericParent() {
		return simpleNameOfGeneratedInterfaceMatcher + " " + getGenericParent();
	}

	public String getSimpleNameOfGeneratedInterfaceMatcherWithGenericNoParent() {
		return simpleNameOfGeneratedInterfaceMatcher + " " + getGenericNoParent();
	}

	public String getFullyQualifiedNameOfClassAnnotatedWithProvideMatcherWithGeneric() {
		return fullyQualifiedNameOfClassAnnotatedWithProvideMatcher + " " + generic;
	}

	public static String getAddParentToGeneric(String generic) {
		if ("".equals(generic)) {
			return "<_PARENT>";
		} else {
			return generic.replaceFirst("<", "<_PARENT,");
		}
	}

	public static String getAddNoParentToGeneric(String generic) {
		if ("".equals(generic)) {
			return "<Void>";
		} else {
			return generic.replaceFirst("<", "<Void,");
		}
	}

	public String getFullGeneric() {
		return fullGeneric;
	}

	public String getGeneric() {
		return generic;
	}

	public String getGenericParent() {
		return getAddParentToGeneric(generic);
	}

	public String getGenericNoParent() {
		return getAddNoParentToGeneric(generic);
	}

	public String getFullGenericParent() {
		return getAddParentToGeneric(fullGeneric);
	}

}
