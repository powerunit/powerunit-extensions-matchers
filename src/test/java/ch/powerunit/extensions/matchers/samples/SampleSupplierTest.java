package ch.powerunit.extensions.matchers.samples;

import java.util.Arrays;

import ch.powerunit.Ignore;
import ch.powerunit.Test;
import ch.powerunit.TestSuite;

public class SampleSupplierTest implements TestSuite {
	@Test
	public void testSupplierNull() {
		SampleSupplier ss = new SampleSupplier();
		assertThat(ss).is(SampleSupplierMatchers.sampleSupplierWith().s1(nullValue()));
	}

	@Test
	@Ignore
	public void testSupplierNullKO() {
		SampleSupplier ss = new SampleSupplier();
		assertThat(ss).is(SampleSupplierMatchers.sampleSupplierWith().s1SupplierResult(is("x")));
	}

	@Test
	public void testSupplierNotNull() {
		SampleSupplier ss = new SampleSupplier();
		ss.s1 = () -> "x";
		assertThat(ss).is(SampleSupplierMatchers.sampleSupplierWith().s1SupplierResult(is("x")));
	}

	@Test
	public void testSupplierNotNullList() {
		SampleSupplier ss = new SampleSupplier();
		ss.s2 = () -> Arrays.asList("x");
		assertThat(ss).is(SampleSupplierMatchers.sampleSupplierWith().s2SupplierResult(contains("x")));
	}
}
