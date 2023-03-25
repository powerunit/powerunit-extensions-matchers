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
package ch.powerunit.extensions.matchers.provideprocessor.extension;

import static ch.powerunit.extensions.matchers.provideprocessor.dsl.DSLMethod.of;
import static java.util.stream.Collectors.joining;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

import ch.powerunit.extensions.matchers.provideprocessor.dsl.DSLMethod;

public abstract class AbstractDSLExtensionSupplier {
	protected final String targetName;
	protected final String returnType;
	protected final String methodName;
	protected final String targetMethodName;

	public AbstractDSLExtensionSupplier(String targetName, String returnType, String methodName,
			String targetMethodName) {
		this.targetName = targetName;
		this.returnType = returnType;
		this.methodName = methodName;
		this.targetMethodName = targetMethodName;
	}

	public abstract Collection<Supplier<DSLMethod>> asSuppliers();

	public String[][] getSeveralParameter(boolean lastIsVarargs, String... name) {
		List<String[]> tmp = Arrays.stream(name).map(this::getOneParameter).toList();
		if (lastIsVarargs) {
			tmp.get(name.length - 1)[0] += "...";
		}
		return tmp.toArray(new String[0][0]);
	}

	public String[] getOneParameter(String name) {
		return new String[] { targetName, name };
	}

	public String getSeveralWith(String... name) {
		return Arrays.stream(name).map(this::getOneWith).collect(joining(","));
	}

	public String getOneWith(String name) {
		return targetMethodName + "(" + name + ")";
	}

	public String[] generateSeveralWithImplementation(String matcher, String... parameters) {
		return new String[] { "java.util.List<org.hamcrest.Matcher<" + targetName
				+ ">> tmp = new java.util.ArrayList<>(java.util.Arrays.asList(" + getSeveralWith(parameters) + "));",
				"tmp.addAll(java.util.Arrays.stream(last).map(v->" + targetMethodName
						+ "(v)).collect(java.util.stream.Collectors.toList()));",
				"return " + matcher + "(tmp.toArray(new org.hamcrest.Matcher[0]));" };
	}

	public DSLMethod generateSimpleDSLMethodFor(String javadoc[], String containerMatcher, String... parameters) {
		return of(returnType + " " + methodName).withArguments(getSeveralParameter(false, parameters))
				.withImplementation("return " + containerMatcher + "(" + getSeveralWith(parameters) + ");")
				.withJavadoc(javadoc);
	}
}
