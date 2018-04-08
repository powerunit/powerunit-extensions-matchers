package ch.powerunit.extensions.matchers.provideprocessor;

import java.util.Collection;
import java.util.function.Supplier;

import ch.powerunit.Test;
import ch.powerunit.TestSuite;
import ch.powerunit.extensions.matchers.provideprocessor.extension.AbstractDSLExtensionSupplier;

public class AbstractDSLExtensionSupplierTest implements TestSuite {
	@Test(fastFail = false)
	public void testGetSeveralParameter() {
		AbstractDSLExtensionSupplier underTest = new AbstractDSLExtensionSupplier("a", "b", "c", "d") {

			@Override
			public Collection<Supplier<DSLMethod>> asSuppliers() {
				return null;
			}
		};
		assertThat(underTest.getSeveralParameter(false, "n1", "n2"))
				.is(new String[][] { { "a", "n1" }, { "a", "n2" } });
		assertThat(underTest.getSeveralParameter(true, "n1", "n2"))
				.is(new String[][] { { "a", "n1" }, { "a...", "n2" } });
	}

	@Test(fastFail = false)
	public void testGetSeveralWith() {
		AbstractDSLExtensionSupplier underTest = new AbstractDSLExtensionSupplier("a", "b", "c", "d") {

			@Override
			public Collection<Supplier<DSLMethod>> asSuppliers() {
				return null;
			}
		};
		assertThat(underTest.getSeveralWith("n1", "n2")).is("d(n1),d(n2)");
	}
}
