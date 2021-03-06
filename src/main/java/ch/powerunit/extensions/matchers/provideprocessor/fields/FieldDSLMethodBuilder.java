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

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.joining;

import java.util.Optional;

import ch.powerunit.extensions.matchers.provideprocessor.fields.lang.BuilderDeclaration;
import ch.powerunit.extensions.matchers.provideprocessor.fields.lang.BuilderImplementation;
import ch.powerunit.extensions.matchers.provideprocessor.fields.lang.BuilderJavadoc;

public class FieldDSLMethodBuilder {

	private static final String MATCHERS = "org.hamcrest.Matchers";

	private static final String DEFAULT_JAVADOCFORMAT = "/**\n" + " * %1$s\n" + " * <p>\n" + " *\n" + " * <i>%2$s</i>\n"
			+ " * <p>\n"
			+ " * <b>In case method specifing a matcher on a fields are used several times, only the last setted matcher will be used.</b> \n"
			+ " * When several control must be done on a single field, hamcrest itself provides a way to combine several matchers.\n"
			+ " * (See for instance {@link " + MATCHERS + "#both(org.hamcrest.Matcher)}.\n" + " *\n" + "%3$s"
			+ " * @return the DSL to continue the construction of the matcher.\n" + "%4$s */";

	public static BuilderDeclaration of(AbstractFieldDescription fieldDescription) {
		return new Builder(fieldDescription);
	}

	public static class Builder implements BuilderDeclaration, BuilderJavadoc, BuilderImplementation {
		private final AbstractFieldDescription fieldDescription;
		private String declaration;
		private String javadoc;

		private Builder(AbstractFieldDescription fieldDescription) {
			this.fieldDescription = fieldDescription;
		}

		@Override
		public BuilderImplementation withJavaDoc(Optional<String> addToDescription, Optional<String> param,
				Optional<String> see) {
			javadoc = getJavaDocFor(fieldDescription, addToDescription, param, see);
			return this;
		}

		@Override
		public FieldDSLMethod havingDefault(String innerMatcher) {
			return new FieldDSLMethod(buildDefaultDsl(fieldDescription, javadoc, declaration, innerMatcher), "");
		}

		@Override
		public FieldDSLMethod havingImplementation(String body) {
			return new FieldDSLMethod(buildDsl(javadoc, declaration), buildImplementation(declaration, body));
		}

		@Override
		public BuilderJavadoc withExplicitDeclaration(String declaration) {
			this.declaration = declaration;
			return this;
		}

		@Override
		public BuilderJavadoc withGenericDeclaration(String generic, String postFix, String arguments) {
			this.declaration = generic + " " + String.format("%1$s %2$s%3$s(%4$s)",
					fieldDescription.getDefaultReturnMethod(), fieldDescription.getFieldName(), postFix, arguments);
			return this;
		}

		@Override
		public FieldDSLMethod withExplicitDeclarationJavadocAndImplementation(String declaration,
				String addToDescription, String body) {
			return withExplicitDeclaration(declaration).withJavaDoc(addToDescription).havingImplementation(body);
		}

		@Override
		public FieldDSLMethod withJavaDocAndDefault(String addToDescription, String innerMatcher) {
			return withJavaDoc(addToDescription).havingDefault(innerMatcher);
		}

		@Override
		public FieldDSLMethod withSuffixDeclarationJavadocAndDefault(String declaration, String addToDescription,
				String innerMatcher) {
			return withSuffixDeclaration(declaration).withJavaDocAndDefault(addToDescription, innerMatcher);
		}

	}

	public static String getJavaDocFor(AbstractFieldDescription fieldDescription, Optional<String> addToDescription,
			Optional<String> param, Optional<String> see) {
		String linkToAccessor = String.format("{@link %1$s#%2$s This field is accessed by using this approach}.",
				fieldDescription.getFullyQualifiedNameEnclosingClassOfField(), fieldDescription.getFieldAccessor());
		String title = String.format("Add a validation on the field `%1$s`%2$s.", fieldDescription.getFieldName(),
				addToDescription.map(s -> " " + s).orElse(""));
		String paramString = param
				.map(t -> stream(t.split("\n")).map(l -> " * @param " + l + ".\n").collect(joining()))
				.orElse("");
		String seeString = see.map(s -> " * @see " + s + "\n").orElse("");
		return String.format(DEFAULT_JAVADOCFORMAT, title, linkToAccessor, paramString, seeString);
	}

	public static String buildImplementation(String declaration, String body) {
		return String.format("@Override\npublic %1$s {\n  %2$s\n}\n", declaration, body.replaceAll("\\R", "\n" + "  "));
	}

	public static String buildDsl(String javadoc, String declaration) {
		return String.format("%1$s\n%2$s;\n", javadoc.replaceAll("\\R", "\n"), declaration);
	}

	public static String buildDefaultDsl(AbstractFieldDescription fieldDescription, String javadoc, String declaration,
			String innerMatcher) {
		return String.format("%1$s\ndefault %2$s{\n  return %3$s(%4$s);\n}", javadoc.replaceAll("\\R", "\n"),
				declaration, fieldDescription.getFieldName(), innerMatcher);

	}

}
