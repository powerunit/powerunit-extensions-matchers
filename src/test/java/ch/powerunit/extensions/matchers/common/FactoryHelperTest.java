package ch.powerunit.extensions.matchers.common;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.util.stream.Stream;

import javax.annotation.processing.AbstractProcessor;

import ch.powerunit.Test;
import ch.powerunit.TestSuite;

public class FactoryHelperTest implements TestSuite {

	@Test
	public void testGenerateFactoryClass() {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		PrintWriter printStream = new PrintWriter(output);
		FactoryHelper.generateFactoryClass(printStream, AbstractProcessor.class, "pckName", "clazzName",
				() -> Stream.of("yxx"));
		printStream.flush();
		String clean = output.toString().replaceAll("\r", "");
		assertThat(clean).containsString("package pckName;");
		assertThat(clean)
				.containsString("@javax.annotation.Generated(value=\"javax.annotation.processing.AbstractProcessor");
		assertThat(clean).containsString("public static final clazzName DSL = new clazzName() {};");
		assertThat(clean).containsString("yxx");
	}
}
