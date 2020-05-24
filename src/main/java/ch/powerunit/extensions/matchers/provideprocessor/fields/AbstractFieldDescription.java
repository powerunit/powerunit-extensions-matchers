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
package ch.powerunit.extensions.matchers.provideprocessor.fields;

import static ch.powerunit.extensions.matchers.common.ListJoining.joinWithMapperAndDelimiter;
import static ch.powerunit.extensions.matchers.common.ListJoining.nlSeparated;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

import ch.powerunit.extensions.matchers.AddToMatcher;
import ch.powerunit.extensions.matchers.common.ListJoining;
import ch.powerunit.extensions.matchers.provideprocessor.ProvidesMatchersAnnotatedElementData;

public abstract class AbstractFieldDescription extends FieldDescriptionMetaData {

	private static final ListJoining<FieldDSLMethod> IMPLEMENTATION_JOINER = joinWithMapperAndDelimiter(
			FieldDSLMethod::asImplementationMethod, "\n");

	private static final ListJoining<FieldDSLMethod> INTERFACE_JOINER = joinWithMapperAndDelimiter(
			FieldDSLMethod::asDSLMethod, "\n");

	private static final ListJoining<String> STRING_JOINER = nlSeparated();

	private final Supplier<List<FieldDSLMethod>> dsl;

	public AbstractFieldDescription(ProvidesMatchersAnnotatedElementData containingElementMirror,
			FieldDescriptionMirror mirror) {
		super(containingElementMirror, mirror);
		this.dsl = this::generateFieldsDSL;
	}

	private List<FieldDSLMethod> generateFieldsDSL() {
		List<FieldDSLMethod> base = new ArrayList<>(generatedFieldDSLMethod(mirror));
		base.addAll(containingElementMirror.getRoundMirror().getFieldDSLMethodFor(this));
		return base;
	}

	private List<FieldDSLMethod> generatedFieldDSLMethod(FieldDescriptionMirror mirror) {
		List<FieldDSLMethod> tmp = new ArrayList<>(getFieldDslMethodFor());
		Arrays.stream(mirror.getFieldElement().getAnnotationsByType(AddToMatcher.class))
				.map(a -> FieldDSLMethodBuilder.of(this).withDeclaration(a.suffix(), a.argument()).withDefaultJavaDoc()
						.havingImplementation(STRING_JOINER.asString(a.body()) + "\nreturn this;"))
				.filter(Objects::nonNull).forEach(tmp::add);
		return tmp;
	}

	protected abstract Collection<FieldDSLMethod> getFieldDslMethodFor();

	public String getImplementationInterface() {
		return IMPLEMENTATION_JOINER.asString(dsl.get());
	}

	public String getDslInterface() {
		return INTERFACE_JOINER.asString(dsl.get());
	}

}
