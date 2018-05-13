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
package ch.powerunit.extensions.matchers.provideprocessor.extension.hamcrestutility;

import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;

import ch.powerunit.extensions.matchers.provideprocessor.RoundMirror;
import ch.powerunit.extensions.matchers.provideprocessor.fields.AbstractFieldDescription;
import ch.powerunit.extensions.matchers.provideprocessor.fields.FieldDSLMethod;

/**
 * @author borettim
 *
 */
public class CollectionHamcrestUtilityAutomatedExtension extends AbstractHamcrestUtilityAutomatedExtension {

	public CollectionHamcrestUtilityAutomatedExtension(RoundMirror roundMirror) {
		super(roundMirror, "java.util.Collection");

	}

	@Override
	protected Collection<FieldDSLMethod> acceptHamcrestUtility(AbstractFieldDescription field) {
		String expectedElement = getExpectedElement();
		String genericMatcher = "org.hamcrest.Matcher<" + field.getGeneric() + ">";
		return Arrays.asList(
				builderFor(field, "hasNoDuplicates", "")
						.withJavaDocAndDefault("That this collection contains no duplication",
								"(org.hamcrest.Matcher)" + expectedElement
										+ ".hasNoDuplicates(java.lang.Object.class)"),
				builderFor(field, "hasFirstItem", genericMatcher + " matcher")
						.withJavaDoc(Optional.of("having first item with a specific value"),
								Optional.of("matcher matcher on the item"), Optional.empty())
						.havingDefault(expectedElement + ".hasFirstItem(matcher)"),
				builderFor(field, "hasLastItem", genericMatcher + " matcher")
						.withJavaDoc(Optional.of("having last item with a specific value"),
								Optional.of("matcher matcher on the item"), Optional.empty())
						.havingDefault(expectedElement + ".hasLastItem(matcher)"),
				builderFor(field, "hasItemAtIndex", "int index," + genericMatcher + " matcher")
						.withJavaDoc(Optional.of("having last item with a specific value"),
								Optional.of("index index of the item\nmatcher matcher on the item"), Optional.empty())
						.havingDefault(expectedElement + ".hasItemAtIndex(index,matcher)"));
	}

}
