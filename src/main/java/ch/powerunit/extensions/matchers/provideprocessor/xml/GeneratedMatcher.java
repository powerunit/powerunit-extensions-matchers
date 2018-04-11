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

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import ch.powerunit.extensions.matchers.provideprocessor.ProvidesMatchersAnnotatedElementMirror;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class GeneratedMatcher {
	private String fullyQualifiedNameInputClass;

	private String simpleNameInputClass;

	private String fullyQualifiedNameGeneratedClass;

	private String simpleNameGeneratedClass;

	private String dslMethodNameStart;

	private List<GeneratedMatcherField> generatedMatcherField;

	private String factories;

	private transient ProvidesMatchersAnnotatedElementMirror mirror;

	public String getFullyQualifiedNameInputClass() {
		return fullyQualifiedNameInputClass;
	}

	public void setFullyQualifiedNameInputClass(String fullyQualifiedNameInputClass) {
		this.fullyQualifiedNameInputClass = fullyQualifiedNameInputClass;
	}

	public String getSimpleNameInputClass() {
		return simpleNameInputClass;
	}

	public void setSimpleNameInputClass(String simpleNameInputClass) {
		this.simpleNameInputClass = simpleNameInputClass;
	}

	public String getFullyQualifiedNameGeneratedClass() {
		return fullyQualifiedNameGeneratedClass;
	}

	public void setFullyQualifiedNameGeneratedClass(String fullyQualifiedNameGeneratedClass) {
		this.fullyQualifiedNameGeneratedClass = fullyQualifiedNameGeneratedClass;
	}

	public String getSimpleNameGeneratedClass() {
		return simpleNameGeneratedClass;
	}

	public void setSimpleNameGeneratedClass(String simpleNameGeneratedClass) {
		this.simpleNameGeneratedClass = simpleNameGeneratedClass;
	}

	public String getDslMethodNameStart() {
		return dslMethodNameStart;
	}

	public void setDslMethodNameStart(String dslMethodNameStart) {
		this.dslMethodNameStart = dslMethodNameStart;
	}

	public List<GeneratedMatcherField> getGeneratedMatcherField() {
		return generatedMatcherField;
	}

	public void setGeneratedMatcherField(List<GeneratedMatcherField> generatedMatcherField) {
		this.generatedMatcherField = generatedMatcherField;
	}

	public ProvidesMatchersAnnotatedElementMirror getMirror() {
		return mirror;
	}

	public void setMirror(ProvidesMatchersAnnotatedElementMirror mirror) {
		this.mirror = mirror;
	}

	public String getFactories() {
		return factories;
	}

	public void setFactories(String factories) {
		this.factories = factories;
	}

}
