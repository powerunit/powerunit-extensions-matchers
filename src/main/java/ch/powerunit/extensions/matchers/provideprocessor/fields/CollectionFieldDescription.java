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

import static java.util.Optional.ofNullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import ch.powerunit.extensions.matchers.provideprocessor.Matchable;
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
				getDslMethodBuilder().withSuffixDeclarationJavadocAndDefault("IsEmptyIterable",
						"that the iterable is empty", "(org.hamcrest.Matcher)" + MATCHERS + ".emptyIterable()"),
				getDslMethodBuilder().withSuffixDeclarationJavadocAndDefault("IsEmpty", "that the collection is empty",
						"(org.hamcrest.Matcher)" + MATCHERS + ".empty()"));
	}

	public List<FieldDSLMethod> getDslForIterableWithGeneric() {
		String tgeneric = this.generic;
		String genericmatcher = "org.hamcrest.Matcher<" + tgeneric + ">";
		return Arrays.asList(
				getDslMethodBuilder().withDeclaration("Contains", tgeneric + "... elements")
						.withJavaDoc("that the iterable contains the received elements", "elements the elements",
								MATCHERS + "#contains(java.lang.Object[])")
						.havingDefault(MATCHERS + ".contains(elements)"),
				getDslMethodBuilder().withDeclaration("Contains", genericmatcher + "... matchersOnElements")
						.withJavaDoc("that the iterable contains the received elements, using matchers",
								DEFAULT_JAVADOC_MATCHER_ON_ELEMENTS, MATCHERS + "#contains(org.hamcrest.Matcher[])")
						.havingDefault(MATCHERS + ".contains(matchersOnElements)"),
				getDslMethodBuilder().withDeclaration("ContainsInAnyOrder", tgeneric + "... elements")
						.withJavaDoc("that the iterable contains the received elements in any order",
								"elements the elements", MATCHERS + "#containsInAnyOrder(java.lang.Object[])")
						.havingDefault(MATCHERS + ".containsInAnyOrder(elements)"),
				getDslMethodBuilder().withDeclaration("ContainsInAnyOrder", genericmatcher + "... matchersOnElements")
						.withJavaDoc("that the iterable contains the received elements, using matchers in any order",
								DEFAULT_JAVADOC_MATCHER_ON_ELEMENTS,
								MATCHERS + "#containsInAnyOrder(org.hamcrest.Matcher[])")
						.havingDefault(MATCHERS + ".containsInAnyOrder(matchersOnElements)"),
				getDslMethodBuilder()
						.withDeclaration("Contains",
								"java.util.List<org.hamcrest.Matcher<? super " + tgeneric + ">> matchersOnElements")
						.withJavaDoc("that the iterable contains the received elements, using list of matcher",
								DEFAULT_JAVADOC_MATCHER_ON_ELEMENTS, MATCHERS + "#contains(java.util.List)")
						.havingDefault(MATCHERS + ".contains(matchersOnElements)"));
	}

	@Override
	public String getFieldCopy(String lhs, String rhs, String ignore) {
		if (!"".equals(generic)) {
			return getFieldCopyForList(lhs, rhs);
		}
		return super.getFieldCopy(lhs, rhs, ignore);
	}

	protected String getFieldCopyForList(String lhs, String rhs) {
		String fieldAccessor = getFieldAccessor();
		String fieldName = getFieldName();
		return "if(" + rhs + "." + fieldAccessor + "==null) {\n  " + lhs + "." + fieldName + "(" + MATCHERS
				+ ".nullValue());\n} else if (" + rhs + "." + fieldAccessor + ".isEmpty()) {\n  " + lhs + "."
				+ fieldName + "IsEmptyIterable();\n} else {\n  " + lhs + "." + fieldName + "Contains(" + rhs + "."
				+ fieldAccessor + ".stream().map(" + generateMatcherBuilderReferenceFor(generic)
				+ ").collect(java.util.stream.Collectors.toList()));\n}";
	}

	protected String generateMatcherBuilderReferenceFor(String generic) {
		return ofNullable(getByName(generic.replaceAll("@[^ ]+ ",""))).filter(Matchable::hasWithSameValue).map(t -> t.getWithSameValue(true))
				.orElse(MATCHERS + "::is");
	}

}
