package ch.powerunit.extensions.matchers.common;

import java.io.PrintWriter;
import java.time.Instant;
import java.util.function.Supplier;
import java.util.stream.Stream;

import javax.annotation.processing.AbstractProcessor;

public final class FactoryHelper {
	private FactoryHelper() {
	}

	public static void generateFactoryClass(PrintWriter wjfo, Class<? extends AbstractProcessor> processor,
			String packageName, String className, Supplier<Stream<String>> bodyProvider) {
		wjfo.println("package " + packageName + ";");
		wjfo.println();
		wjfo.println(CommonConstants.DEFAULT_JAVADOC_FOR_FACTORY);

		wjfo.println("@javax.annotation.Generated(value=\"" + processor.getName() + "\",date=\""
				+ Instant.now().toString() + "\")");
		wjfo.println("public interface " + className + " {");
		wjfo.println();
		wjfo.println(generateStaticDSL(className));
		wjfo.println();
		bodyProvider.get().forEach(wjfo::println);
		wjfo.println("}");
	}
	
	public static String generateStaticDSL(String className) {
		return new StringBuilder().append("  /**").append("\n")
				.append("   * Use this static field to access all the DSL syntax, without be required to implements this interface.")
				.append("\n").append("  */").append("\n")
				.append("  public static final " + className + " DSL = new " + className + "() {};").append("\n")
				.append("\n").toString();
	}
}
