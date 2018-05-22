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
package ch.powerunit.extensions.matchers.factoryprocessor;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.lang.model.element.ExecutableElement;
import javax.tools.Diagnostic.Kind;

import ch.powerunit.extensions.matchers.common.FactoryHelper;
import ch.powerunit.extensions.matchers.common.FileObjectHelper;

class FactoryGroup {

	private final String acceptingRegex[];

	private final String fullyQualifiedTargetName;

	private final List<FactoryAnnotatedElementMirror> method = new ArrayList<>();

	private final FactoryAnnotationsProcessor parent;

	public FactoryGroup(FactoryAnnotationsProcessor parent, String definition) {
		this.parent = parent;
		String split[] = definition.split("\\s*:\\s*");
		this.acceptingRegex = split[0].split("\\s*,\\s*");
		this.fullyQualifiedTargetName = split[1];
	}

	public String[] getAcceptingRegex() {
		return acceptingRegex;
	}

	public String getFullyQualifiedTargetName() {
		return fullyQualifiedTargetName;
	}

	public void addMethod(FactoryAnnotatedElementMirror faem) {
		method.add(faem);
	}

	public boolean isAccepted(FactoryAnnotatedElementMirror faem) {
		return Arrays.stream(acceptingRegex).anyMatch(a -> faem.getSurroundingFullyQualifiedName().matches(a));
	}

	public void processGenerateOneFactoryInterface() {
		FileObjectHelper.processFileWithIOException(
				() -> parent.getFiler().createSourceFile(fullyQualifiedTargetName,
						method.stream().map((e) -> e.getElement()).toArray(ExecutableElement[]::new)),
				jfo -> new PrintWriter(jfo.openWriter()), wjfo -> {
					String pName = fullyQualifiedTargetName.replaceAll("\\.[^.]+$", "");
					String cName = fullyQualifiedTargetName.substring(fullyQualifiedTargetName.lastIndexOf('.') + 1);
					FactoryHelper.generateFactoryClass(wjfo, FactoryAnnotationsProcessor.class, pName, cName,
							() -> method.stream().map(FactoryAnnotatedElementMirror::generateFactory));
				},
				e -> parent.getMessager().printMessage(Kind.ERROR,
						"Unable to create the file containing the target class `" + fullyQualifiedTargetName
								+ "`, because of " + e.getMessage()));
	}

}