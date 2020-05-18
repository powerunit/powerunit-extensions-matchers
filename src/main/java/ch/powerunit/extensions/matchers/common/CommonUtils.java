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
package ch.powerunit.extensions.matchers.common;

import java.io.PrintStream;
import java.time.Instant;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;
import javax.tools.Diagnostic.Kind;
import javax.tools.StandardLocation;

/**
 * These are some method to manipulate java.
 * 
 * @author borettim
 *
 */
public class CommonUtils {
	private CommonUtils() {
	}

	public static String toJavaSyntax(String unformatted) {
		StringBuilder sb = new StringBuilder();
		sb.append('"');
		for (char c : unformatted.toCharArray()) {
			sb.append(toJavaSyntax(c));
		}
		sb.append('"');
		return sb.toString();
	}

	private static String toJavaSyntax(char ch) {
		switch (ch) {
		case '"':
			return "\\\"";
		case '\n':
			return "\\n";
		case '\r':
			return "\\r";
		case '\t':
			return "\\t";
		default:
			return "" + ch;
		}
	}

	public static String addPrefix(String prefix, String input) {
		return ListJoining.accepting(String.class).withMapper(l -> prefix + l).withDelimiter("\n")
				.withPrefixAndSuffix("\n", "\n").asString(input.split("\\R"));
	}

	public static String generateGeneratedAnnotation(Class<?> generatedBy, String comments) {
		return "@javax.annotation.Generated(value=\"" + generatedBy.getName() + "\",date=\"" + Instant.now().toString()
				+ "\"" + (comments == null ? "" : (",comments=" + toJavaSyntax(comments))) + ")";
	}

	public static void traceErrorAndDump(Messager messager, Filer filer, Exception e, Element te) {
		FileObjectHelper.processFileWithIOException(
				() -> filer.createResource(StandardLocation.SOURCE_OUTPUT, "",
						"dump" + System.currentTimeMillis() + "txt", te),
				s -> new PrintStream(s.openOutputStream()), s -> e.printStackTrace(s),
				e2 -> messager.printMessage(Kind.ERROR,
						"Unable to create the file containing the dump of the error because of " + e2
								+ " during handling of " + e,
						te));
		messager.printMessage(Kind.ERROR, "Unable to create the file containing the target class because of " + e, te);
	}

}
