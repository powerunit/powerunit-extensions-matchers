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
package ch.powerunit.extensions.matchers.provideprocessor.dsl;

import static java.util.stream.Collectors.joining;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ch.powerunit.extensions.matchers.common.ListJoining;
import ch.powerunit.extensions.matchers.provideprocessor.dsl.lang.DSLMethodArgument;
import ch.powerunit.extensions.matchers.provideprocessor.dsl.lang.DSLMethodImplementation;
import ch.powerunit.extensions.matchers.provideprocessor.dsl.lang.DSLMethodJavadoc;

/**
 * @author borettim
 * 
 */
public final class DSLMethod {
	public static final Pattern DECLARATION_PARSER = Pattern.compile("^\\s*.*\\s+([0-9A-Za-z_]+)\\s*$");

	private static final ListJoining<String[]> ARGUMENTS_JOIN = ListJoining
			.joinWithMapper((String a[]) -> a[0] + " " + a[1]).withCommaDelimiter().withPrefixAndSuffix("(", ")");

	private static final ListJoining<String[]> ARGUMENTNAMES_JOIN = ListJoining.joinWithMapper((String a[]) -> a[1])
			.withCommaDelimiter().withPrefixAndSuffix("(", ")");

	private static final ListJoining<String> IMPLEMENTATION_JOIN = ListJoining.joinWithMapper((String s) -> "  " + s)
			.withDelimiter("\n").withPrefixAndSuffix("", "\n");

	private final String javadoc;
	private final String fullDeclaration;
	private final String implementation;
	private final String fullMethodName;

	private static class Builder implements DSLMethodArgument, DSLMethodImplementation, DSLMethodJavadoc {

		private final String declaration;
		private final List<String[]> arguments = new ArrayList<>();
		private String implementation[];

		public Builder(String declaration) {
			this.declaration = declaration;
		}

		@Override
		public DSLMethod withJavadoc(String javadoc) {
			return new DSLMethod(javadoc, declaration, arguments.toArray(new String[][] {}), implementation);
		}

		@Override
		public DSLMethodJavadoc withImplementation(String... implementation) {
			this.implementation = implementation;
			return this;
		}

		@Override
		public DSLMethodArgument addOneArgument(String type, String name) {
			arguments.add(new String[] { Objects.requireNonNull(type, "type can't be null"),
					Objects.requireNonNull(name, "name can't be null") });
			return this;
		}

	}

	public static DSLMethodArgument of(String declaration) {
		return new Builder(declaration);
	}

	public DSLMethod(String javadoc, String declaration, String arguments[][], String implementation[]) {
		Matcher m = DECLARATION_PARSER.matcher(declaration);
		if (!m.matches()) {
			throw new IllegalArgumentException("Unable to parse the received declaration");
		}
		this.javadoc = javadoc;
		this.implementation = IMPLEMENTATION_JOIN.asString(implementation);
		this.fullDeclaration = declaration + ARGUMENTS_JOIN.asString(arguments);
		this.fullMethodName = m.group(1) + ARGUMENTNAMES_JOIN.asString(arguments);
	}

	public String asStaticImplementation() {
		return javadoc + "@org.hamcrest.Factory\npublic static " + fullDeclaration + " {\n" + implementation + "}\n";
	}

	public String asDefaultReference(String target) {
		return javadoc + "default " + fullDeclaration + " {\n  return " + target + "." + fullMethodName + ";\n}\n";
	}

}
