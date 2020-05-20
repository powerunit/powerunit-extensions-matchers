package ch.powerunit.extensions.matchers.samples;

import java.util.Map;

import ch.powerunit.extensions.matchers.ProvideMatchers;
import ch.powerunit.extensions.matchers.samples.extension.MyTestWithoutGeneric;
import ch.powerunit.extensions.matchers.samples.extension.MyTestWithoutGeneric2;

@ProvideMatchers
public class MapSample<E, V> {
	public Map<String, Integer> map1;

	public Map<E, V> map2;

	public Map map3;

	public Map<?, ?> map4;

	public Map<String, MyTestWithoutGeneric> map5;

	public Map<String, MyTestWithoutGeneric2> map6;
}
