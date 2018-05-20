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

import javax.lang.model.element.TypeElement;

public abstract class ProvidesMatchersAnnotatedElementGenericMirror extends ProvideMatchersMirror {

	protected final String genericForChaining;

	public ProvidesMatchersAnnotatedElementGenericMirror(TypeElement typeElement, RoundMirror roundMirror) {
		super(roundMirror, typeElement);
		this.genericForChaining = getGenericParent().replaceAll("^<_PARENT",
				"<" + getFullyQualifiedNameOfGeneratedClass() + "." + simpleNameOfGeneratedInterfaceMatcher
						+ getGenericNoParent());
	}

	public String getSimpleNameOfGeneratedInterfaceMatcherWithGenericParent() {
		return simpleNameOfGeneratedInterfaceMatcher + " " + getGenericParent();
	}

	public String getSimpleNameOfGeneratedInterfaceMatcherWithGenericNoParent() {
		return simpleNameOfGeneratedInterfaceMatcher + " " + getGenericNoParent();
	}

	public String getFullyQualifiedNameOfClassAnnotatedWithProvideMatcherWithGeneric() {
		return getFullyQualifiedNameOfClassAnnotated() + " " + generic;
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
