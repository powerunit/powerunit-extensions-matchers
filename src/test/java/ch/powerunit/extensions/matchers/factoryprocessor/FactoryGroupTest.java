package ch.powerunit.extensions.matchers.factoryprocessor;

import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.StringWriter;

import javax.annotation.processing.Filer;
import javax.tools.JavaFileObject;

import org.mockito.Mock;
import org.mockito.Mockito;

import ch.powerunit.Rule;
import ch.powerunit.Test;
import ch.powerunit.TestRule;
import ch.powerunit.TestSuite;

public class FactoryGroupTest implements TestSuite {

	@Mock
	private FactoryAnnotationsProcessor factoryAnnotationProcessor;

	@Mock
	private Filer filer;

	@Mock
	private JavaFileObject javaFileObject;

	private StringWriter outputStream;

	private void prepareMock() {
		when(factoryAnnotationProcessor.getFiler()).thenReturn(filer);
		try {
			when(filer.createSourceFile(Mockito.eq("target"), Mockito.anyVararg())).thenReturn(javaFileObject);
		} catch (IOException e) {
			// ignore
		}
		outputStream = new StringWriter();
		try {
			when(javaFileObject.openWriter()).thenReturn(outputStream);
		} catch (IOException e) {
			// ignore
		}
	}

	@Rule
	public final TestRule rules = mockitoRule().around(before(this::prepareMock));

	@Test
	public void testConstructorWithOneEntry() {
		FactoryGroup underTest = new FactoryGroup(factoryAnnotationProcessor, ".*:target");
		assertThat(underTest.getFullyQualifiedTargetName()).is("target");
		assertThat(underTest.getAcceptingRegex()).is(arrayContaining(".*"));
	}

	@Test
	public void testConstructorWithTwoEntry() {
		FactoryGroup underTest = new FactoryGroup(factoryAnnotationProcessor, "a.*,b.*:target");
		assertThat(underTest.getFullyQualifiedTargetName()).is("target");
		assertThat(underTest.getAcceptingRegex()).is(arrayContaining("a.*", "b.*"));
	}

	@Test
	public void testConstructorWithTwoEntryAndNoTargetMethod() {
		FactoryGroup underTest = new FactoryGroup(factoryAnnotationProcessor, "a.*,b.*:target");
		underTest.processGenerateOneFactoryInterface();
		String target = outputStream.toString().replace('\r', '\n');
		assertThat(target).is(containsString("package target"));
		assertThat(target).is(containsString(
				"@javax.annotation.Generated(value=\"ch.powerunit.extensions.matchers.factoryprocessor.FactoryAnnotationsProcessor\","));
		assertThat(target).is(containsString("public interface target {"));
		assertThat(target).is(containsString("public static final target DSL = new target() {}"));
	}

}
