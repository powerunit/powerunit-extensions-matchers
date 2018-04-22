/**
 * 
 */
package ch.powerunit.extensions.matchers.provideprocessor.extension.hamcrestutility;

import java.util.Collection;
import java.util.Collections;

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
		return Collections.singleton(builderFor(field, "hasNoDuplicates", "")
				.withJavaDoc("That this collection contains no duplication")
				.havingDefault("(org.hamcrest.Matcher)" + getExpectedElement() + ".hasNoDuplicates(java.lang.Object.class)"));
	}

}
