package ch.powerunit.extensions.matchers.samples;

import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonMap;

import java.util.HashMap;

import ch.powerunit.Test;
import ch.powerunit.TestSuite;

public class AMapPojoOtherName implements TestSuite {
	@Test(fastFail = false)
	public void testTODO() {
		AMapPojo pojo = new AMapPojo();

		//F 1
		assertThat(pojo).is(AMapPojoMatchers.aMapPojoWith().myMapHasSameValues(emptyMap()));

		pojo.myMap = emptyMap();

		//S 1
		assertThat(pojo).is(AMapPojoMatchers.aMapPojoWith().myMapHasSameValues(emptyMap()));

		pojo.myMap = singletonMap("k", "v");

		// F2
		assertThat(pojo).is(AMapPojoMatchers.aMapPojoWith().myMapHasSameValues(emptyMap()));

		// S2
		assertThat(pojo).is(AMapPojoMatchers.aMapPojoWith().myMapHasSameValues(singletonMap("k", "v")));

		pojo.myMap = new HashMap<String, String>() {
			{
				put("k1", "v1");
				put("k2", "v2");
			}
		};

		// F3
		assertThat(pojo).is(AMapPojoMatchers.aMapPojoWith().myMapHasSameValues(emptyMap()));

		// F4
		assertThat(pojo).is(AMapPojoMatchers.aMapPojoWith().myMapHasSameValues(singletonMap("k", "v")));

		// F5
		assertThat(pojo).is(AMapPojoMatchers.aMapPojoWith().myMapHasSameValues(new HashMap<String, String>() {
			{
				put("k1", "v1");
				put("k3", "v3");
			}
		}));
		
		// F6
		assertThat(pojo).is(AMapPojoMatchers.aMapPojoWith().myMapHasSameValues(new HashMap<String, String>() {
			{
				put("k1", "v1");
				put("k2", "v3");
			}
		}));
		
		// F7
		assertThat(pojo).is(AMapPojoMatchers.aMapPojoWith().myMapHasSameValues(new HashMap<String, String>() {
			{
				put("k1", "v1");
				put("k3", "v2");
			}
		}));
		
		// S3
		assertThat(pojo).is(AMapPojoMatchers.aMapPojoWith().myMapHasSameValues(new HashMap<String, String>() {
			{
				put("k1", "v1");
				put("k2", "v2");
			}
		}));
	}
}
