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

import static java.util.stream.Collectors.joining;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author borettim
 *
 */
public class DSLMethod {
	public static Pattern DECLARATION_PARSER = Pattern.compile("^\\s*.*\\s+([0-9A-Za-z_]+)\\s*$");

	private final String javadoc;
	private final String declaration;
	private final String implementation;
	private final String methodName;
	private final String realArguments;
	private final String realArgumentsName;

	private static String cleanJavadoc(String javadoc[]) {
		return "/**\n" + Arrays.stream(javadoc).map(s -> " * " + s).collect(joining("\n")) + "\n */\n";
	}

	public DSLMethod(String javadoc[], String declaration, String arguments[], String implementation) {
		this(cleanJavadoc(javadoc), declaration, new String[][] { arguments }, new String[] { implementation });
	}

	public DSLMethod(String javadoc[], String declaration, String arguments[][], String implementation) {
		this(cleanJavadoc(javadoc), declaration, arguments, new String[] { implementation });
	}

	public DSLMethod(String javadoc[], String declaration, String arguments[][], String implementation[]) {
		this(cleanJavadoc(javadoc), declaration, arguments, implementation);
	}

	public DSLMethod(String javadoc, String declaration, String implementation) {
		this(javadoc, declaration, new String[][] {}, new String[] { implementation });
	}

	public DSLMethod(String javadoc, String declaration, String implementation[]) {
		this(javadoc, declaration, new String[][] {}, implementation);
	}

	public DSLMethod(String javadoc, String declaration, String arguments[], String implementation) {
		this(javadoc, declaration, new String[][] { arguments }, new String[] { implementation });
	}

	public DSLMethod(String javadoc, String declaration, String arguments[], String implementation[]) {
		this(javadoc, declaration, new String[][] { arguments }, implementation);
	}

	public DSLMethod(String javadoc, String declaration, String arguments[][], String implementation[]) {
		this.javadoc = javadoc;
		this.declaration = declaration;
		this.implementation = Arrays.stream(implementation).map(s -> "  " + s).collect(joining("\n")) + "\n";
		this.realArguments = Arrays.stream(arguments).map(a -> a[0] + " " + a[1]).collect(joining(","));
		this.realArgumentsName = Arrays.stream(arguments).map(a -> a[1]).collect(joining(","));
		Matcher m = DECLARATION_PARSER.matcher(declaration);
		if (!m.matches()) {
			throw new IllegalArgumentException("Unable to parse the received declaration");
		}
		methodName = m.group(1);
	}

	public String asStaticImplementation() {
		return javadoc + "@org.hamcrest.Factory\npublic static " + declaration + "(" + realArguments + ")" + " {\n"
				+ implementation + "}\n";
	}

	public String asDefaultReference(String target) {
		return javadoc + "default " + declaration + "(" + realArguments + ")" + " {\n  return " + target + "."
				+ methodName + "(" + realArgumentsName + ");\n}\n";
	}

	public String getJavadoc() {
		return javadoc;
	}

	public String getDeclaration() {
		return declaration;
	}

	public String getImplementation() {
		return implementation;
	}

	public String getMethodName() {
		return methodName;
	}

	public String getRealArguments() {
		return realArguments;
	}

	public String getRealArgumentsName() {
		return realArgumentsName;
	}

}
