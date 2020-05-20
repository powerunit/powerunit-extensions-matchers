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
package ch.powerunit.extensions.matchers.provideprocessor.extension.jackson;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.function.Supplier;

import ch.powerunit.extensions.matchers.ProvideMatchers;
import ch.powerunit.extensions.matchers.provideprocessor.ProvidesMatchersAnnotatedElementData;
import ch.powerunit.extensions.matchers.provideprocessor.RoundMirror;
import ch.powerunit.extensions.matchers.provideprocessor.dsl.DSLMethod;
import ch.powerunit.extensions.matchers.provideprocessor.extension.AutomatedExtension;
import ch.powerunit.extensions.matchers.provideprocessor.fields.AbstractFieldDescription;
import ch.powerunit.extensions.matchers.provideprocessor.fields.DefaultFieldDescription;
import ch.powerunit.extensions.matchers.provideprocessor.fields.FieldDSLMethod;

/**
 * @author borettim
 *
 */
public abstract class AbstractJacksonAutomatedExtension extends AutomatedExtension {

	public AbstractJacksonAutomatedExtension(RoundMirror roundMirror) {
		super(roundMirror, "com.fasterxml.jackson.databind.JsonNode");
	}

	protected abstract Collection<FieldDSLMethod> acceptJsonMatcher(DefaultFieldDescription field);
	
	protected final FieldDSLMethod buildBasic(DefaultFieldDescription field, String suffix, String description, String method) {
		String fieldType = field.getFieldType();
		return builderFor(field).withSuffixDeclarationJavadocAndDefault(suffix, description,
				"new org.hamcrest.CustomTypeSafeMatcher<" + fieldType + ">(\"" + description
						+ "\"){ public boolean matchesSafely(" + fieldType + " o) {return o." + method + ";}}");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.powerunit.extensions.matchers.provideprocessor.extension.
	 * AutomatedExtension#accept(ch.powerunit.extensions.matchers.
	 * provideprocessor.fields.FieldDescriptionMetaData)
	 */
	@Override
	public final Collection<FieldDSLMethod> accept(AbstractFieldDescription field) {
		if (!Arrays.asList(field.getContainingElementMirror().getFullData().getExtension())
				.contains(ProvideMatchers.JSON_EXTENSION)) {
			return Collections.emptyList();
		}
		if (!(field instanceof DefaultFieldDescription)) {
			return Collections.emptyList();
		}
		if (!isAssignableWithErasure(field.getMirror().getFieldTypeAsTypeElement(), getTargetElement().asType())) {
			return Collections.emptyList();
		}
		return acceptJsonMatcher((DefaultFieldDescription) field);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.powerunit.extensions.matchers.provideprocessor.extension.
	 * AutomatedExtension#accept(ch.powerunit.extensions.matchers.
	 * provideprocessor.ProvidesMatchersAnnotatedElementData)
	 */
	@Override
	public final Collection<Supplier<DSLMethod>> accept(ProvidesMatchersAnnotatedElementData clazz) {
		return Collections.emptyList();
	}

}
