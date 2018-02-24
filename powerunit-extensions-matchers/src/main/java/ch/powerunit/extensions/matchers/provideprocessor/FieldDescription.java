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
package ch.powerunit.extensions.matchers.provideprocessor;

public class FieldDescription {

	public static enum Type {
		NA, ARRAY, COLLECTION, LIST, SET, OPTIONAL
	}

	private final String fieldAccessor;
	private final String fieldName;
	private final String methodFieldName;
	private final String fieldType;
	private final Type type;

	public FieldDescription(String fieldAccessor, String fieldName, String methodFieldName, String fieldType,
			Type type) {
		this.fieldAccessor = fieldAccessor;
		this.fieldName = fieldName;
		this.methodFieldName = methodFieldName;
		this.fieldType = fieldType;
		this.type = type;
	}

	public String getFieldAccessor() {
		return fieldAccessor;
	}

	public String getFieldName() {
		return fieldName;
	}

	public String getMethodFieldName() {
		return methodFieldName;
	}

	public String getFieldType() {
		return fieldType;
	}

	public Type getType() {
		return type;
	}

}
