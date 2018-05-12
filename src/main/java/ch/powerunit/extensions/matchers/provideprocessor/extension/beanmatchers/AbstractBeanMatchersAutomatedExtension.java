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
package ch.powerunit.extensions.matchers.provideprocessor.extension.beanmatchers;

import java.util.Collection;
import java.util.Collections;
import java.util.function.Supplier;

import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic.Kind;

import ch.powerunit.extensions.matchers.provideprocessor.DSLMethod;
import ch.powerunit.extensions.matchers.provideprocessor.ProvidesMatchersAnnotatedElementData;
import ch.powerunit.extensions.matchers.provideprocessor.RoundMirror;
import ch.powerunit.extensions.matchers.provideprocessor.extension.AutomatedExtension;
import ch.powerunit.extensions.matchers.provideprocessor.fields.AbstractFieldDescription;
import ch.powerunit.extensions.matchers.provideprocessor.fields.FieldDSLMethod;

/**
 * @author borettim
 *
 */
public abstract class AbstractBeanMatchersAutomatedExtension extends AutomatedExtension {

	public AbstractBeanMatchersAutomatedExtension(RoundMirror roundMirror) {
		super(roundMirror, "com.google.code.beanmatchers.BeanMatchers");
	}

	protected abstract Collection<FieldDSLMethod> acceptBeanMatchers(AbstractFieldDescription field);

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.powerunit.extensions.matchers.provideprocessor.extension.
	 * AutomatedExtension#accept(ch.powerunit.extensions.matchers.
	 * provideprocessor.fields.FieldDescriptionMetaData)
	 */
	@Override
	public final Collection<FieldDSLMethod> accept(AbstractFieldDescription field) {
		if (!field.getFieldType().matches("^java.lang.Class.*$")) {
			return Collections.emptyList();
		}
		return acceptBeanMatchers(field);
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
