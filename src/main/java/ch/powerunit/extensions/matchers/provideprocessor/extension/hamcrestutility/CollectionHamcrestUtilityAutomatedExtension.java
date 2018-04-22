/**
 * 
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
				builderFor(field, "hasNoDuplicates", "").withJavaDoc("That this collection contains no duplication")
						.havingDefault("(org.hamcrest.Matcher)" + expectedElement
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
