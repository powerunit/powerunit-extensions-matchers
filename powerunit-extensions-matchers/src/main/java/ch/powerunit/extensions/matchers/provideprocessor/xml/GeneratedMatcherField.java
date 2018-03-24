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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class GeneratedMatcherField {
	private String fieldName;

	private boolean fieldIsIgnored;

	private String fieldCategory;

	private String fieldAccessor;

	private String genericDetails;

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public boolean getFieldIsIgnored() {
		return fieldIsIgnored;
	}

	public void setFieldIsIgnored(boolean fieldIsIgnored) {
		this.fieldIsIgnored = fieldIsIgnored;
	}

	public String getFieldCategory() {
		return fieldCategory;
	}

	public void setFieldCategory(String fieldCategory) {
		this.fieldCategory = fieldCategory;
	}

	public String getFieldAccessor() {
		return fieldAccessor;
	}

	public void setFieldAccessor(String fieldAccessor) {
		this.fieldAccessor = fieldAccessor;
	}

	public String getGenericDetails() {
		return genericDetails;
	}

	public void setGenericDetails(String genericDetails) {
		this.genericDetails = genericDetails;
	}

}
