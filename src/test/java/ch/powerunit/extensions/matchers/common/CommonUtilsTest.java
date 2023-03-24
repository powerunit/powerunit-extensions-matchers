package ch.powerunit.extensions.matchers.common;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.mockito.Mockito.eq;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import javax.tools.Diagnostic.Kind;

import org.mockito.Mockito;

import ch.powerunit.Test;
import ch.powerunit.extensions.matchers.TestSuiteSupport;

public class CommonUtilsTest implements TestSuiteSupport {

	@Test
	public void testToJavaSyntax() {
		assertThatFunction(CommonUtils::toJavaSyntax, "<\"><\r><\t><\n>").is("\"<\\\"><\\r><\\t><\\n>\"");
	}

	@Test(fastFail = false)
	public void testAddPrefix() {
		assertThatBiFunction(CommonUtils::addPrefix, " ", "").is("\n \n");
		assertThatBiFunction(CommonUtils::addPrefix, " ", "x").is("\n x\n");
		assertThatBiFunction(CommonUtils::addPrefix, " ", "x\ny").is("\n x\n y\n");
	}

	@Test(fastFail = false)
	public void testGenerateGeneratedAnnotation() {
		assertThatBiFunction(CommonUtils::generateGeneratedAnnotation, Object.class, null)
				.is(both(containsString("@javax.annotation.Generated(\n   value=\"java.lang.Object\",\n   date"))
						.and(not(containsString("comments"))));
		assertThatBiFunction(CommonUtils::generateGeneratedAnnotation, Object.class, "x")
				.is(both(containsString("@javax.annotation.Generated(\n   value=\"java.lang.Object\",\n   date"))
						.and(containsString("comments=\"x\"")));
	}

	@Test(fastFail = false)
	public void testAsStandardMethodName() {
		assertThatFunction(CommonUtils::asStandardMethodName, "Name").is("name");
		assertThatFunction(CommonUtils::asStandardMethodName, "name").is("name");
	}

	@Test
	public void testTraceErrorAndDumpNoSubError() throws IOException {
		Messager messager = mock(Messager.class);

		FileObject output = mock(FileObject.class);
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		doReturn(stream).when(output).openOutputStream();

		Filer filer = mock(Filer.class);
		doReturn(output).when(filer).createResource(eq(StandardLocation.SOURCE_OUTPUT), eq(""),
				Mockito.contains("dump"), Mockito.any());

		ProcessingEnvironmentHelper environment = mock(ProcessingEnvironmentHelper.class);
		doReturn(messager).when(environment).getMessager();
		doReturn(filer).when(environment).getFiler();

		Exception exception = new Exception("msg");

		Element element = mock(Element.class);

		CommonUtils.traceErrorAndDump(environment, exception, element);

		verify(messager).printMessage(Kind.ERROR,
				"Unable to create the file containing the target class because of java.lang.Exception: msg", element);

		verifyNoMoreInteractions(messager);

		assertThat(stream.toString()).is(startsWith("java.lang.Exception: msg"));

	}

	@Test
	public void testTraceErrorAndDumpWithSubError() throws IOException {
		Messager messager = mock(Messager.class);

		FileObject output = mock(FileObject.class);
		doReturn(null).when(output).openOutputStream();

		Filer filer = mock(Filer.class);
		doReturn(output).when(filer).createResource(eq(StandardLocation.SOURCE_OUTPUT), eq(""),
				Mockito.contains("dump"), Mockito.any());

		ProcessingEnvironmentHelper environment = mock(ProcessingEnvironmentHelper.class);
		doReturn(messager).when(environment).getMessager();
		doReturn(filer).when(environment).getFiler();

		Exception exception = new Exception("msg");

		Element element = mock(Element.class);

		CommonUtils.traceErrorAndDump(environment, exception, element);
		
		verify(messager).printMessage(Kind.ERROR,
				"Unable to create the file containing the dump of the error because of java.lang.NullPointerException: Null output stream during handling of java.lang.Exception: msg",
				element);

		verify(messager).printMessage(Kind.ERROR,
				"Unable to create the file containing the target class because of java.lang.Exception: msg", element);
		
		verifyNoMoreInteractions(messager);

	}
}
