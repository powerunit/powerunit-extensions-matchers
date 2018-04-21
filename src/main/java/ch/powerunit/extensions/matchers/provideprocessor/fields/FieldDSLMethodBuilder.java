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

import java.util.Arrays;
import java.util.Optional;

public class FieldDSLMethodBuilder {

	private static final String MATCHERS = "org.hamcrest.Matchers";

	public static BuilderDeclaration of(AbstractFieldDescription fieldDescription) {
		return new Builder(fieldDescription);
	}

	public static interface BuilderDeclaration {
		BuilderJavadoc withExplicitDeclaration(String declaration);

		BuilderJavadoc withGenericDeclaration(String generic, String postFix, String arguments);

		default BuilderJavadoc withDeclaration(String postFix, String arguments) {
			return withGenericDeclaration("", postFix, arguments);
		}

		default BuilderJavadoc withDeclaration(String arguments) {
			return withDeclaration("", arguments);
		}
	}

	public static interface BuilderJavadoc {
		BuilderImplementation withJavaDoc(Optional<String> addToDescription, Optional<String> param,
				Optional<String> see);

		default BuilderImplementation withDefaultJavaDoc() {
			return withJavaDoc(Optional.empty(), Optional.empty(), Optional.empty());
		}

		default BuilderImplementation withJavaDoc(String addToDescription, String param, String see) {
			return withJavaDoc(Optional.of(addToDescription), Optional.of(param), Optional.of(see));
		}

		default BuilderImplementation withJavaDoc(String addToDescription, String param) {
			return withJavaDoc(Optional.of(addToDescription), Optional.of(param), Optional.empty());
		}

		default BuilderImplementation withJavaDoc(String addToDescription) {
			return withJavaDoc(Optional.of(addToDescription), Optional.empty(), Optional.empty());
		}
	}

	public static interface BuilderImplementation {
		FieldDSLMethod havingDefault(String innerMatcher);

		FieldDSLMethod havingImplementation(String body);
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

	}

	public static String getJavaDocFor(AbstractFieldDescription fieldDescription, Optional<String> addToDescription,
			Optional<String> param, Optional<String> see) {
		String linkToAccessor = "{@link " + fieldDescription.getFullyQualifiedNameEnclosingClassOfField() + "#"
				+ fieldDescription.getFieldAccessor() + " This field is accessed by using this approach}.";
		StringBuilder sb = new StringBuilder();
		sb.append("/**\n * Add a validation on the field `").append(fieldDescription.getFieldName()).append("`");
		addToDescription.ifPresent(t -> sb.append(" ").append(t));
		sb.append(".\n * <p>").append("\n *\n * <i>").append(linkToAccessor).append("</i>\n * <p>\n");
		sb.append(
				" * <b>In case method specifing a matcher on a fields are used several times, only the last setted matcher will be used.</b> \n");
		sb.append(
				" * When several control must be done on a single field, hamcrest itself provides a way to combine several matchers (See for instance {@link "
						+ MATCHERS + "#both(org.hamcrest.Matcher)}.\n");
		sb.append(" *\n");
		param.ifPresent(
				t -> Arrays.stream(t.split("\n")).forEach(l -> sb.append(" * @param ").append(l).append(".\n")));
		sb.append(" * @return the DSL to continue the construction of the matcher.\n");
		see.ifPresent(t -> sb.append(" * @see ").append(t).append("\n"));
		sb.append(" */");
		return sb.toString();
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
