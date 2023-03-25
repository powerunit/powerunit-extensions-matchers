package ch.powerunit.extensions.matchers.samples.extension;

import org.apache.commons.lang3.tuple.Pair;

import ch.powerunit.extensions.matchers.api.ProvideMatchers;

@ProvideMatchers(allowWeakWithSameValue = true)
public class MyPair2<L,R> extends Pair<L, R> {
	
	public String test;

	@Override
	public R setValue(R arg0) {
		return null;
	}

	@Override
	public L getLeft() {
		return null;
	}

	@Override
	public R getRight() {
		return null;
	}

}
