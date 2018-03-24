package ch.powerunit.extensions.matchers.samples;

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
}
