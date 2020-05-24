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

import static ch.powerunit.extensions.matchers.common.CommonUtils.generateGeneratedAnnotation;

import java.io.PrintWriter;
import java.util.function.Supplier;
import java.util.stream.Stream;

import javax.annotation.processing.AbstractProcessor;

/**
 * These are some method to generate factory methods.
 * 
 * @author borettim
 *
 */
public final class FactoryHelper {
	private FactoryHelper() {
	}

	public static void generateFactoryClass(PrintWriter wjfo, Class<? extends AbstractProcessor> processor,
			String packageName, String className, Supplier<Stream<String>> bodyProvider) {
		wjfo.println("package " + packageName + ";");
		wjfo.println();
		wjfo.println(CommonConstants.DEFAULT_JAVADOC_FOR_FACTORY);

		wjfo.println(generateGeneratedAnnotation(processor, null));
		wjfo.println("public interface " + className + " {");
		wjfo.println();
		wjfo.println(generateStaticDSL(className));
		wjfo.println();
		bodyProvider.get().forEach(wjfo::println);
		wjfo.println("}");
	}

	public static String generateStaticDSL(String className) {
		return String.format(
				"  /**\n   * Use this static field to access all the DSL syntax, without be required to implements this interface.\n  */\n  public static final %1$s DSL = new %1$s() {};\n\n",
				className);
	}
}
