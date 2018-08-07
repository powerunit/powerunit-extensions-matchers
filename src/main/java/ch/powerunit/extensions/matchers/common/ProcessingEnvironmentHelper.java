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

import java.util.Locale;
import java.util.Map;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

@FunctionalInterface
public interface ProcessingEnvironmentHelper {
	ProcessingEnvironment getProcessingEnv();

	default Map<String, String> getOptions() {
		return getProcessingEnv().getOptions();
	}

	default Messager getMessager() {
		return getProcessingEnv().getMessager();
	}

	default Filer getFiler() {
		return getProcessingEnv().getFiler();
	}

	default Elements getElementUtils() {
		return getProcessingEnv().getElementUtils();
	}

	default Types getTypeUtils() {
		return getProcessingEnv().getTypeUtils();
	}

	default SourceVersion getSourceVersion() {
		return getProcessingEnv().getSourceVersion();
	}

	default Locale getLocale() {
		return getProcessingEnv().getLocale();
	}
}
