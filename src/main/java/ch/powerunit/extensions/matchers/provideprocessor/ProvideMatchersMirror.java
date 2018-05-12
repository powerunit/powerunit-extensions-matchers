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
import static java.util.stream.Collectors.toList;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.TypeElement;

import ch.powerunit.extensions.matchers.ComplementaryExpositionMethod;
import ch.powerunit.extensions.matchers.ProvideMatchers;
import ch.powerunit.extensions.matchers.provideprocessor.extension.AnyOfExtension;
import ch.powerunit.extensions.matchers.provideprocessor.extension.ArrayContainingDSLExtension;
import ch.powerunit.extensions.matchers.provideprocessor.extension.ArrayContainingInAnyOrderDSLExtension;
import ch.powerunit.extensions.matchers.provideprocessor.extension.ContainsDSLExtension;
import ch.powerunit.extensions.matchers.provideprocessor.extension.ContainsInAnyOrderDSLExtension;
import ch.powerunit.extensions.matchers.provideprocessor.extension.DSLExtension;
import ch.powerunit.extensions.matchers.provideprocessor.extension.HasItemsExtension;

public class ProvideMatchersMirror {

	private static final Collection<DSLExtension> EXTENSION = Collections.unmodifiableList(Arrays.asList(
			new ContainsDSLExtension(), new ArrayContainingDSLExtension(), new HasItemsExtension(),
			new ContainsInAnyOrderDSLExtension(), new ArrayContainingInAnyOrderDSLExtension(), new AnyOfExtension()));

	private final ProvideMatchers pm;
	private final String simpleNameOfGeneratedClass;
	private final String packageNameOfGeneratedClass;
	private final ComplementaryExpositionMethod[] moreMethod;

	public ProvideMatchersMirror(ProcessingEnvironment processingEnv, TypeElement annotatedElement) {
		pm = annotatedElement.getAnnotation(ProvideMatchers.class);
		if ("".equals(pm.matchersClassName())) {
			simpleNameOfGeneratedClass = annotatedElement.getSimpleName().toString() + "Matchers";
		} else {
			simpleNameOfGeneratedClass = pm.matchersClassName();
		}
		if ("".equals(pm.matchersPackageName())) {
			packageNameOfGeneratedClass = processingEnv.getElementUtils().getPackageOf(annotatedElement)
					.getQualifiedName().toString();
		} else {
			packageNameOfGeneratedClass = pm.matchersPackageName();
		}
		this.moreMethod = pm.moreMethod();
	}

	public final String getComments() {
		return pm.comments();
	}
	
	public final String[] getExtension() {
		return pm.extensions();
		
	}

	public final String getSimpleNameOfGeneratedClass() {
		return simpleNameOfGeneratedClass;
	}

	public final String getFullyQualifiedNameOfGeneratedClass() {
		return packageNameOfGeneratedClass + "." + simpleNameOfGeneratedClass;
	}

	public final String getPackageNameOfGeneratedClass() {
		return packageNameOfGeneratedClass;
	}

	public final Collection<DSLExtension> getDSLExtension() {
		return EXTENSION.stream().filter(e -> e.accept(moreMethod))
				.collect(collectingAndThen(toList(), Collections::unmodifiableList));
	}

}
