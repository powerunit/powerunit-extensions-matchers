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
package ch.powerunit.extensions.matchers.provideprocessor.xml;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.Element;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class GeneratedMatchers {
	private List<GeneratedMatcher> generatedMatcher = new ArrayList<>();

	public List<GeneratedMatcher> getGeneratedMatcher() {
		return generatedMatcher;
	}

	public void setGeneratedMatcher(List<GeneratedMatcher> generatedMatcher) {
		this.generatedMatcher = generatedMatcher;
	}

	public Element[] listElements() {
		return generatedMatcher.stream().map(g -> g.getMirror().getElement()).collect(toList()).toArray(new Element[0]);
	}

}
