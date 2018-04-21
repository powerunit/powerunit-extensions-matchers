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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import ch.powerunit.extensions.matchers.provideprocessor.ProvidesMatchersAnnotatedElementData;

public class CollectionFieldDescription extends DefaultFieldDescription {

	private static final String DEFAULT_JAVADOC_MATCHER_ON_ELEMENTS = "matchersOnElements the matchers on the elements";

	public CollectionFieldDescription(ProvidesMatchersAnnotatedElementData containingElementMirror,
			FieldDescriptionMirror mirror) {
		super(containingElementMirror, mirror);
	}

	@Override
	protected Collection<FieldDSLMethod> getSpecificFieldDslMethodFor() {
		List<FieldDSLMethod> tmp = new ArrayList<>(getFieldDslMethodForIterableAndComparable());
		if (!"".equals(generic)) {
			tmp.addAll(getDslForIterableWithGeneric());
		}
		return tmp;
	}

	public List<FieldDSLMethod> getFieldDslMethodForIterableAndComparable() {
		return Arrays.asList(
				getDslMethodBuilder().withDeclaration("IsEmptyIterable", "").withJavaDoc("that the iterable is empty")
						.havingDefault("(org.hamcrest.Matcher)" + MATCHERS + ".emptyIterable()"),
				getDslMethodBuilder().withDeclaration("IsEmpty", "").withJavaDoc("that the collection is empty")
						.havingDefault("(org.hamcrest.Matcher)" + MATCHERS + ".empty()"));
	}

	public List<FieldDSLMethod> getDslForIterableWithGeneric() {
		return Arrays.asList(
				getDslMethodBuilder().withDeclaration("Contains", this.generic + "... elements")
						.withJavaDoc("that the iterable contains the received elements", "elements the elements",
								MATCHERS + "#contains(java.lang.Object[])")
						.havingDefault(MATCHERS + ".contains(elements)"),
				getDslMethodBuilder()
						.withDeclaration("Contains", "org.hamcrest.Matcher<" + this.generic + ">... matchersOnElements")
						.withJavaDoc("that the iterable contains the received elements, using matchers",
								DEFAULT_JAVADOC_MATCHER_ON_ELEMENTS, MATCHERS + "#contains(org.hamcrest.Matcher[])")
						.havingDefault(MATCHERS + ".contains(matchersOnElements)"),
				getDslMethodBuilder().withDeclaration("ContainsInAnyOrder", this.generic + "... elements")
						.withJavaDoc("that the iterable contains the received elements in any order",
								"elements the elements", MATCHERS + "#containsInAnyOrder(java.lang.Object[])")
						.havingDefault(MATCHERS + ".containsInAnyOrder(elements)"),
				getDslMethodBuilder()
						.withDeclaration("ContainsInAnyOrder",
								"org.hamcrest.Matcher<" + this.generic + ">... matchersOnElements")
						.withJavaDoc("that the iterable contains the received elements, using matchers in any order",
								DEFAULT_JAVADOC_MATCHER_ON_ELEMENTS,
								MATCHERS + "#containsInAnyOrder(org.hamcrest.Matcher[])")
						.havingDefault(MATCHERS + ".containsInAnyOrder(matchersOnElements)"),
				getDslMethodBuilder()
						.withDeclaration("Contains",
								"java.util.List<org.hamcrest.Matcher<? super " + this.generic + ">> matchersOnElements")
						.withJavaDoc("that the iterable contains the received elements, using list of matcher",
								DEFAULT_JAVADOC_MATCHER_ON_ELEMENTS, MATCHERS + "#contains(java.util.List)")
						.havingDefault(MATCHERS + ".contains(matchersOnElements)"));
	}

	@Override
	public String getFieldCopy(String lhs, String rhs) {
		if (!"".equals(generic)) {
			return getFieldCopyForList(lhs, rhs);
		}
		return super.getFieldCopy(lhs, rhs);
	}

}
