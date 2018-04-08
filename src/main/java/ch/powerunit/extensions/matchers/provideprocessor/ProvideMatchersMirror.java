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
import ch.powerunit.extensions.matchers.provideprocessor.extension.ArrayContainingDSLExtension;
import ch.powerunit.extensions.matchers.provideprocessor.extension.ContainsDSLExtension;
import ch.powerunit.extensions.matchers.provideprocessor.extension.DSLExtension;
import ch.powerunit.extensions.matchers.provideprocessor.extension.HasItemsExtension;

public class ProvideMatchersMirror {

	private static final Collection<DSLExtension> EXTENSION = Collections.unmodifiableList(
			Arrays.asList(new ContainsDSLExtension(), new ArrayContainingDSLExtension(), new HasItemsExtension()));

	private final String comments;
	private final String simpleNameOfGeneratedClass;
	private final String fullyQualifiedNameOfGeneratedClass;
	private final String packageNameOfGeneratedClass;
	private final ComplementaryExpositionMethod[] moreMethod;

	public ProvideMatchersMirror(ProcessingEnvironment processingEnv, TypeElement annotatedElement) {
		String fullyQualifiedNameOfClassAnnotatedWithProvideMatcher = annotatedElement.getQualifiedName().toString();
		String tpackageName = processingEnv.getElementUtils().getPackageOf(annotatedElement).getQualifiedName()
				.toString();
		String toutputClassName = fullyQualifiedNameOfClassAnnotatedWithProvideMatcher + "Matchers";
		String tsimpleNameOfGeneratedClass = annotatedElement.getSimpleName().toString() + "Matchers";
		ProvideMatchers pm = annotatedElement.getAnnotation(ProvideMatchers.class);
		this.comments = pm.comments();
		if (!"".equals(pm.matchersClassName())) {
			toutputClassName = toutputClassName.replaceAll(tsimpleNameOfGeneratedClass + "$", pm.matchersClassName());
			tsimpleNameOfGeneratedClass = pm.matchersClassName();
		}
		this.simpleNameOfGeneratedClass = tsimpleNameOfGeneratedClass;
		if (!"".equals(pm.matchersPackageName())) {
			toutputClassName = toutputClassName.replaceAll("^" + tpackageName, pm.matchersPackageName());
			tpackageName = pm.matchersPackageName();
		}
		this.fullyQualifiedNameOfGeneratedClass = toutputClassName;
		this.packageNameOfGeneratedClass = tpackageName;
		this.moreMethod = pm.moreMethod();
	}

	public String getComments() {
		return comments;
	}

	public String getSimpleNameOfGeneratedClass() {
		return simpleNameOfGeneratedClass;
	}

	public String getFullyQualifiedNameOfGeneratedClass() {
		return fullyQualifiedNameOfGeneratedClass;
	}

	public String getPackageNameOfGeneratedClass() {
		return packageNameOfGeneratedClass;
	}

	public Collection<DSLExtension> getDSLExtension() {
		return EXTENSION.stream().filter(e -> e.accept(moreMethod))
				.collect(collectingAndThen(toList(), Collections::unmodifiableList));
	}

}
