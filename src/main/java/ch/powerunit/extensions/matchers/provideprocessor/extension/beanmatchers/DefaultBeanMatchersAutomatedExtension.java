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

import ch.powerunit.extensions.matchers.provideprocessor.RoundMirror;
import ch.powerunit.extensions.matchers.provideprocessor.fields.AbstractFieldDescription;
import ch.powerunit.extensions.matchers.provideprocessor.fields.FieldDSLMethod;

/**
 * @author borettim
 *
 */
public class DefaultBeanMatchersAutomatedExtension extends AbstractBeanMatchersAutomatedExtension {

	public DefaultBeanMatchersAutomatedExtension(RoundMirror roundMirror) {
		super(roundMirror);

	}

	@Override
	protected Collection<FieldDSLMethod> acceptBeanMatchers(AbstractFieldDescription field) {
		return Collections.singletonList(builderFor(field).withSuffixDeclarationJavadocAndDefault("IsAValidBean",
				"Check that this field (clazz) is a correct bean, based on Bean Matchers",
				"org.hamcrest.CoreMatchers.allOf(com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor(),com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters(),com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCode(),com.google.code.beanmatchers.BeanMatchers.hasValidBeanEquals(),com.google.code.beanmatchers.BeanMatchers.hasValidBeanToString())"));
	}

}
