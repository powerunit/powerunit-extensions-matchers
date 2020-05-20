package ch.powerunit.extensions.matchers.samples.extension;

import org.apache.commons.lang3.text.StrBuilder;

import ch.powerunit.extensions.matchers.ComplementaryExpositionMethod;
import ch.powerunit.extensions.matchers.ProvideMatchers;

@ProvideMatchers(moreMethod = { ComplementaryExpositionMethod.CONTAINS, ComplementaryExpositionMethod.ARRAYCONTAINING,
		ComplementaryExpositionMethod.HAS_ITEMS, ComplementaryExpositionMethod.ANY_OF,
		ComplementaryExpositionMethod.NONE_OF })
public class MyTestWithoutGeneric extends StrBuilder {
	
	public String test;

}
