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

import java.io.PrintWriter;
import java.time.Instant;
import java.util.function.Supplier;
import java.util.stream.Stream;

import javax.annotation.processing.AbstractProcessor;

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

	public static String generateStaticDSL(String className) {
		return new StringBuilder().append("  /**").append("\n")
				.append("   * Use this static field to access all the DSL syntax, without be required to implements this interface.")
				.append("\n").append("  */").append("\n")
				.append("  public static final " + className + " DSL = new " + className + "() {};").append("\n")
				.append("\n").toString();
	}

	public static void generateFactoryClass(PrintWriter wjfo, Class<? extends AbstractProcessor> processor,
			String packageName, String className, Supplier<Stream<String>> bodyProvider) {
		wjfo.println("package " + packageName + ";");
		wjfo.println();
		wjfo.println(CommonConstants.DEFAULT_JAVADOC_FOR_FACTORY);

		wjfo.println("@javax.annotation.Generated(value=\"" + processor.getName() + "\",date=\""
				+ Instant.now().toString() + "\")");
		wjfo.println("public interface " + className + " {");
		wjfo.println();
		wjfo.println(CommonUtils.generateStaticDSL(className));
		wjfo.println();
		bodyProvider.get().forEach(wjfo::println);
		wjfo.println("}");
	}
}
