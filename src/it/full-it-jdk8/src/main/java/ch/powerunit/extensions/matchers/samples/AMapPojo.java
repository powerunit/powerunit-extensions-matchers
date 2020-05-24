package ch.powerunit.extensions.matchers.samples;

import java.util.Map;

import ch.powerunit.extensions.matchers.ProvideMatchers;

@ProvideMatchers
public class AMapPojo {
	public Map<String, String> myMap;

	@Override
	public String toString() {
		return "AMapPojo [myMap=" + myMap + "]";
	}
}
