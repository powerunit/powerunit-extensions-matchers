package ch.powerunit.extensions.matchers.samples;

import java.util.Map;

import ch.powerunit.extensions.matchers.ProvideMatchers;

@ProvideMatchers
public class MapSample<E, V> {
	public Map<String, Integer> map1;

	public Map<E, V> map2;
	
	public Map map3;
	
	public Map<?,?> map4;
}
