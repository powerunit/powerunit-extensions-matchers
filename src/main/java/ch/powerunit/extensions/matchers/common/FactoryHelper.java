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
		wjfo.println(CommonUtils.generateStaticDSL(className));
		wjfo.println();
		bodyProvider.get().forEach(wjfo::println);
		wjfo.println("}");
	}
}
