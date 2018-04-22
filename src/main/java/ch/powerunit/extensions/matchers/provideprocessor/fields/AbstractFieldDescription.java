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

import static java.util.stream.Collectors.joining;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import ch.powerunit.extensions.matchers.AddToMatcher;
import ch.powerunit.extensions.matchers.provideprocessor.ProvidesMatchersAnnotatedElementData;

public abstract class AbstractFieldDescription extends FieldDescriptionMetaData {

	private final List<FieldDSLMethod> dsl;

	public AbstractFieldDescription(ProvidesMatchersAnnotatedElementData containingElementMirror,
			FieldDescriptionMirror mirror) {
		super(containingElementMirror, mirror);
		List<FieldDSLMethod> base = new ArrayList<>(generatedFieldDSLMethod(mirror));
		base.addAll(containingElementMirror.getRoundMirror().getFieldDSLMethodFor(this));
		this.dsl = Collections.unmodifiableList(base);
	}

	private List<FieldDSLMethod> generatedFieldDSLMethod(FieldDescriptionMirror mirror) {
		List<FieldDSLMethod> tmp = new ArrayList<>();

		tmp.addAll(getFieldDslMethodFor());
		AddToMatcher addToMatchers[] = mirror.getFieldElement().getAnnotationsByType(AddToMatcher.class);
		Arrays.stream(addToMatchers)
				.map(a -> FieldDSLMethodBuilder.of(this).withDeclaration(a.suffix(), a.argument()).withDefaultJavaDoc()
						.havingImplementation(Arrays.stream(a.body()).collect(joining("\n")) + "\nreturn this;"))
				.filter(Objects::nonNull).forEach(tmp::add);
		return tmp;
	}

	protected abstract Collection<FieldDSLMethod> getFieldDslMethodFor();

	public String getImplementationInterface() {
		return dsl.stream().map(FieldDSLMethod::asImplementationMethod).collect(joining("\n"));
	}

	public String getDslInterface() {
		return dsl.stream().map(FieldDSLMethod::asDSLMethod).collect(joining("\n"));
	}

}
