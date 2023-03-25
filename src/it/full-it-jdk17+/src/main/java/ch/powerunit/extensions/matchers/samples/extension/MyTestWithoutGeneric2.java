package ch.powerunit.extensions.matchers.samples.extension;

import org.apache.commons.lang3.text.StrBuilder;

import ch.powerunit.extensions.matchers.api.ComplementaryExpositionMethod;
import ch.powerunit.extensions.matchers.api.ProvideMatchers;

@ProvideMatchers(moreMethod = { ComplementaryExpositionMethod.CONTAINS, ComplementaryExpositionMethod.ARRAYCONTAINING,
		ComplementaryExpositionMethod.HAS_ITEMS, ComplementaryExpositionMethod.ANY_OF,
		ComplementaryExpositionMethod.NONE_OF }, allowWeakWithSameValue = true)
public class MyTestWithoutGeneric2 extends StrBuilder {

	public MyTestWithoutGeneric2() {
	}

	public MyTestWithoutGeneric2(String test) {
		this.test = test;
	}

	public String test;

}
