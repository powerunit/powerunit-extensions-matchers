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

import static ch.powerunit.extensions.matchers.common.ListJoining.accepting;

import java.util.List;

import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.Parameterizable;
import javax.lang.model.element.QualifiedNameable;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.type.TypeMirror;

/**
 * @author borettim
 *
 */
public interface ElementHelper {

	static final ListJoining<TypeMirror> TYPE_PARAMETER_BOUND_AS_LIST = accepting(TypeMirror.class).withToStringMapper()
			.withDelimiter("&").withoutSuffixAndPrefix();

	static final ListJoining<TypeMirror> TYPE_PARAMETER_BOUND_AS_LIST_WITH_EXTENDS = accepting(TypeMirror.class)
			.withToStringMapper().withDelimiter("&").withOptionalPrefixAndSuffix(" extends ", "");

	static final ListJoining<TypeParameterElement> TYPE_PARAMETER_SIMPLE_AS_LIST = accepting(TypeParameterElement.class)
			.withToStringMapper().withCommaDelimiter().withOptionalPrefixAndSuffix("<", ">");

	@SuppressWarnings("unchecked")
	static final ListJoining<TypeParameterElement> TYPE_PARAMETER_FULL_AS_LIST = accepting(TypeParameterElement.class)
			.withMapper(t -> t.toString()
					+ TYPE_PARAMETER_BOUND_AS_LIST_WITH_EXTENDS.asString((List<TypeMirror>) t.getBounds()))
			.withCommaDelimiter().withOptionalPrefixAndSuffix("<", ">");

	default String getSimpleName(Element e) {
		return e.getSimpleName().toString();
	}

	default boolean isSimpleName(Element e, String name) {
		return name.equals(getSimpleName(e));
	}

	default String getQualifiedName(QualifiedNameable e) {
		return e.getQualifiedName().toString();
	}

	default boolean isStatic(Element e) {
		return e.getModifiers().contains(Modifier.STATIC);
	}

	default boolean isPublic(Element e) {
		return e.getModifiers().contains(Modifier.PUBLIC);
	}

	@SuppressWarnings("unchecked")
	default String boundsAsString(TypeParameterElement e) {
		return TYPE_PARAMETER_BOUND_AS_LIST.asString((List<TypeMirror>) e.getBounds());
	}

	@SuppressWarnings("unchecked")
	default String getGeneric(Parameterizable typeElement) {
		return TYPE_PARAMETER_SIMPLE_AS_LIST.asString((List<TypeParameterElement>) typeElement.getTypeParameters());
	}

	@SuppressWarnings("unchecked")
	default String getFullGeneric(Parameterizable typeElement) {
		return TYPE_PARAMETER_FULL_AS_LIST.asString((List<TypeParameterElement>) typeElement.getTypeParameters());
	}
}
