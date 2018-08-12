package ch.powerunit.extensions.matchers.factoryprocessor;

import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.StringWriter;

import javax.annotation.processing.ProcessingEnvironment;
import javax.tools.Diagnostic.Kind;
import javax.tools.JavaFileObject;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;

import ch.powerunit.Rule;
import ch.powerunit.Test;
import ch.powerunit.TestRule;
import ch.powerunit.extensions.matchers.TestSuiteSupport;

public class FactoryGroupTest implements TestSuiteSupport {

	@Mock
	private FactoryAnnotatedElementMirror factoryAnnotatedElementMiror;

	private ProcessingEnvironment processingEnv;

	@Mock
	private JavaFileObject javaFileObject;

	@Spy
	private StringWriter outputStream = new StringWriter();

	private void prepareMock() {
		processingEnv = generateMockitoProcessingEnvironment();
	}

	@Rule
	public final TestRule rules = mockitoRule().around(before(this::prepareMock));

	@Test
	public void testConstructorWithOneEntry() {
		FactoryGroup underTest = new FactoryGroup(processingEnv, ".*:target");
		assertThat(underTest.getFullyQualifiedTargetName()).is("target");
		assertThat(underTest.getAcceptingRegex()).is(arrayContaining(".*"));
	}

	@Test
	public void testConstructorWithTwoEntry() {
		FactoryGroup underTest = new FactoryGroup(processingEnv, "a.*,b.*:target");
		assertThat(underTest.getFullyQualifiedTargetName()).is("target");
		assertThat(underTest.getAcceptingRegex()).is(arrayContaining("a.*", "b.*"));
	}

	@Test
	public void testConstructorWithTwoEntryAndNoTargetMethod() throws IOException {
		when(processingEnv.getFiler().createSourceFile(Mockito.eq("target"), Mockito.anyVararg()))
				.thenReturn(javaFileObject);
		when(javaFileObject.openWriter()).thenReturn(outputStream);
		FactoryGroup underTest = new FactoryGroup(processingEnv, "a.*,b.*:target");
		underTest.processGenerateOneFactoryInterface();
		String target = outputStream.toString().replace('\r', '\n');
		assertThat(target).is(containsString("package target"));
		assertThat(target).is(containsString(
				"@javax.annotation.Generated(value=\"ch.powerunit.extensions.matchers.factoryprocessor.FactoryAnnotationsProcessor\","));
		assertThat(target).is(containsString("public interface target {"));
		assertThat(target).is(containsString("public static final target DSL = new target() {}"));
	}

	@Test
	public void testConstructorWithOneEntryAndIsAcceptedReturnTrue() {
		when(factoryAnnotatedElementMiror.getSurroundingFullyQualifiedName()).thenReturn("ab");
		FactoryGroup underTest = new FactoryGroup(processingEnv, "a.*:target");
		assertThatFunction(underTest::isAccepted, factoryAnnotatedElementMiror).is(true);
	}

	@Test
	public void testConstructorWithOneEntryAndIsAcceptedReturnFalse() {
		when(factoryAnnotatedElementMiror.getSurroundingFullyQualifiedName()).thenReturn("ba");
		FactoryGroup underTest = new FactoryGroup(processingEnv, "a.*:target");
		assertThatFunction(underTest::isAccepted, factoryAnnotatedElementMiror).is(false);
	}

	@Test(fastFail = false)
	public void testIsAccepted() throws IOException {
		FactoryGroup underTest = new FactoryGroup(processingEnv, "a.*,b.*:target");

		when(factoryAnnotatedElementMiror.getSurroundingFullyQualifiedName()).thenReturn("aa");
		assertThat(underTest.isAccepted(factoryAnnotatedElementMiror)).is(true);
		
		when(factoryAnnotatedElementMiror.getSurroundingFullyQualifiedName()).thenReturn("bb");
		assertThat(underTest.isAccepted(factoryAnnotatedElementMiror)).is(true);
		
		when(factoryAnnotatedElementMiror.getSurroundingFullyQualifiedName()).thenReturn("cc");
		assertThat(underTest.isAccepted(factoryAnnotatedElementMiror)).is(false);
	}

	@Test
	public void testConstructorWithTwoEntryAndOneTargetMethod() throws IOException {
		when(processingEnv.getFiler().createSourceFile(Mockito.eq("target"), Mockito.anyVararg()))
				.thenReturn(javaFileObject);
		when(javaFileObject.openWriter()).thenReturn(outputStream);
		when(factoryAnnotatedElementMiror.generateFactory()).thenReturn("<generated>");
		FactoryGroup underTest = new FactoryGroup(processingEnv, "a.*,b.*:target");
		underTest.addMethod(factoryAnnotatedElementMiror);
		underTest.processGenerateOneFactoryInterface();
		String target = outputStream.toString().replace('\r', '\n');

		assertThat(target).is(containsString("package target"));
		assertThat(target).is(containsString(
				"@javax.annotation.Generated(value=\"ch.powerunit.extensions.matchers.factoryprocessor.FactoryAnnotationsProcessor\","));
		assertThat(target).is(containsString("public interface target {"));
		assertThat(target).is(containsString("public static final target DSL = new target() {}"));
		assertThat(target).is(containsString("<generated>"));
	}

	@Test
	public void testConstructorWithTwoEntryAndOneTargetMethodWithIOException() throws IOException {
		when(processingEnv.getFiler().createSourceFile(Mockito.eq("target"), Mockito.anyVararg()))
				.thenReturn(javaFileObject);
		when(javaFileObject.openWriter()).thenThrow(IOException.class);
		when(factoryAnnotatedElementMiror.generateFactory()).thenReturn("<generated>");
		FactoryGroup underTest = new FactoryGroup(processingEnv, "a.*,b.*:target");
		underTest.addMethod(factoryAnnotatedElementMiror);
		underTest.processGenerateOneFactoryInterface();
		Mockito.verify(processingEnv).getMessager();
		Mockito.verify(processingEnv.getMessager()).printMessage(Mockito.eq(Kind.ERROR), Mockito.anyString());

	}

}
