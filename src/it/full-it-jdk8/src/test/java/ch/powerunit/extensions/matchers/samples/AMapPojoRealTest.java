package ch.powerunit.extensions.matchers.samples;

import ch.powerunit.PowerUnitRunner;
import ch.powerunit.Test;
import ch.powerunit.TestContext;
import ch.powerunit.TestResultListener;
import ch.powerunit.TestSuite;
import ch.powerunit.impl.DefaultPowerUnitRunnerImpl;

public class AMapPojoRealTest implements TestSuite {

	private int success = 0;

	private int failure = 0;

	private int error = 0;
	
	private Throwable throwable = null;

	@Test(fastFail = false)
	public void testTODO() {
		PowerUnitRunner<AMapPojoOtherName> r = new DefaultPowerUnitRunnerImpl<>(AMapPojoOtherName.class);

		TestResultListener<AMapPojoOtherName> listener = new TestResultListener<AMapPojoOtherName>() {

			@Override
			public void notifySetStart(String setName, String groups) {
			}

			@Override
			public void notifySetEnd(String setName, String groups) {

			}

			@Override
			public void notifyStart(TestContext<AMapPojoOtherName> context) {

			}

			@Override
			public void notifySuccess(TestContext<AMapPojoOtherName> context) {
				success++;
			}

			@Override
			public void notifyFailure(TestContext<AMapPojoOtherName> context, Throwable cause) {
				failure++;
				throwable=cause;
			}

			@Override
			public void notifyError(TestContext<AMapPojoOtherName> context, Throwable cause) {
				error++;
				throwable=cause;
			}

			@Override
			public void notifySkipped(TestContext<AMapPojoOtherName> context) {

			}

			@Override
			public void notifyParameterStart(String setName, String parameterName) {

			}

			@Override
			public void notifyParameterEnd(String setName, String parameterName) {

			}
		};
		r.addListener(listener);
		r.run();
		
		assertThat(success).is(0);
		assertThat(failure).is(1);
		assertThat(error).is(0);
		throwable.printStackTrace(System.out);
	}
}
