package ch.powerunit.extensions.matchers.factoryprocessor;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.StringWriter;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.tools.Diagnostic.Kind;
import javax.tools.JavaFileObject;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;

import ch.powerunit.Rule;
import ch.powerunit.Test;
import ch.powerunit.TestRule;
import ch.powerunit.TestSuite;

public class FactoryGroupTest implements TestSuite {

	@Mock
	private FactoryAnnotatedElementMirror factoryAnnotatedElementMiror;

	@Mock
	private FactoryAnnotationsProcessor factoryAnnotationProcessor;

	@Mock
	private Filer filer;

	@Mock
	private Messager messager;

	@Mock
	private JavaFileObject javaFileObject;

	@Spy
	private StringWriter outputStream = new StringWriter();

	private void prepareMock() {
		when(factoryAnnotationProcessor.getFiler()).thenReturn(filer);
		when(factoryAnnotationProcessor.getMessager()).thenReturn(messager);
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
	public void testConstructorWithTwoEntryAndNoTargetMethod() throws IOException {
		when(filer.createSourceFile(Mockito.eq("target"), Mockito.anyVararg())).thenReturn(javaFileObject);
		when(javaFileObject.openWriter()).thenReturn(outputStream);
		FactoryGroup underTest = new FactoryGroup(factoryAnnotationProcessor, "a.*,b.*:target");
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
		FactoryGroup underTest = new FactoryGroup(factoryAnnotationProcessor, "a.*:target");
		assertThatFunction(underTest::isAccepted, factoryAnnotatedElementMiror).is(true);
	}

	@Test
	public void testConstructorWithOneEntryAndIsAcceptedReturnFalse() {
		when(factoryAnnotatedElementMiror.getSurroundingFullyQualifiedName()).thenReturn("ba");
		FactoryGroup underTest = new FactoryGroup(factoryAnnotationProcessor, "a.*:target");
		assertThatFunction(underTest::isAccepted, factoryAnnotatedElementMiror).is(false);
	}

	@Test
	public void testConstructorWithTwoEntryAndOneTargetMethod() throws IOException {
		when(filer.createSourceFile(Mockito.eq("target"), Mockito.anyVararg())).thenReturn(javaFileObject);
		when(javaFileObject.openWriter()).thenReturn(outputStream);
		when(factoryAnnotatedElementMiror.generateFactory()).thenReturn("<generated>");
		FactoryGroup underTest = new FactoryGroup(factoryAnnotationProcessor, "a.*,b.*:target");
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
		when(filer.createSourceFile(Mockito.eq("target"), Mockito.anyVararg())).thenReturn(javaFileObject);
		when(javaFileObject.openWriter()).thenThrow(IOException.class);
		when(factoryAnnotatedElementMiror.generateFactory()).thenReturn("<generated>");
		FactoryGroup underTest = new FactoryGroup(factoryAnnotationProcessor, "a.*,b.*:target");
		underTest.addMethod(factoryAnnotatedElementMiror);
		underTest.processGenerateOneFactoryInterface();
		Mockito.verify(factoryAnnotationProcessor).getMessager();
		Mockito.verify(messager).printMessage(Mockito.eq(Kind.ERROR), Mockito.anyString());

	}

}
