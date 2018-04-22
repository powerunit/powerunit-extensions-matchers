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
		return Arrays.asList(
				builderFor(field, "hasNoDuplicates", "").withJavaDoc("That this collection contains no duplication")
						.havingDefault("(org.hamcrest.Matcher)" + getExpectedElement()
								+ ".hasNoDuplicates(java.lang.Object.class)"),
				builderFor(field, "hasFirstItem", "org.hamcrest.Matcher<" + field.getGeneric() + "> matcher")
						.withJavaDoc(Optional.of("having first item with a specific value"),
								Optional.of("matcher matcher on the item"), Optional.empty())
						.havingDefault(getExpectedElement() + ".hasFirstItem(matcher)"));
	}

}
