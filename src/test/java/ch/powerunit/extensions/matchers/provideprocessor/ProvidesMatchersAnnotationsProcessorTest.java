package ch.powerunit.extensions.matchers.provideprocessor;

import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collections;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.tools.FileObject;

import org.mockito.Mock;
import org.mockito.Mockito;

import ch.powerunit.Rule;
import ch.powerunit.Test;
import ch.powerunit.TestRule;
import ch.powerunit.TestSuite;

public class ProvidesMatchersAnnotationsProcessorTest implements TestSuite {

	@Mock
	private ProcessingEnvironment processingEnv;

	@Mock
	private Filer filer;

	@Mock
	private Messager messager;

	@Mock
	private FileObject matcher;

	private void prepare() {
		when(processingEnv.getFiler()).thenReturn(filer);
		when(processingEnv.getMessager()).thenReturn(messager);
		try {
			when(filer.getResource(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(matcher);
		} catch (IOException e) {
			// ignore
		}
	}

	@Rule
	public final TestRule rules = mockitoRule().around(before(this::prepare));

	@Test
	public void testAddPrefix() {
		assertThatBiFunction(ProvidesMatchersAnnotationsProcessor::addPrefix, " ", "x\ny").is("\n x\n y\n");
	}

	@Test
	public void testInitFactoryNoFactory() {
		ProvidesMatchersAnnotationsProcessor underTest = new ProvidesMatchersAnnotationsProcessor();
		underTest.init(processingEnv);
	}

	@Test
	public void testInitFactoryNoOldFIle() {
		ProvidesMatchersAnnotationsProcessor underTest = new ProvidesMatchersAnnotationsProcessor();
		when(processingEnv.getOptions()).thenReturn(Collections.singletonMap(
				"ch.powerunit.extensions.matchers.provideprocessor.ProvidesMatchersAnnotationsProcessor.factory",
				"test"));
		when(matcher.getLastModified()).thenReturn(0l);
		underTest.init(processingEnv);
	}

	@Test
	public void testInitFactoryOldFile() throws IOException {
		ProvidesMatchersAnnotationsProcessor underTest = new ProvidesMatchersAnnotationsProcessor();
		when(processingEnv.getOptions()).thenReturn(Collections.singletonMap(
				"ch.powerunit.extensions.matchers.provideprocessor.ProvidesMatchersAnnotationsProcessor.factory",
				"test"));
		when(matcher.getLastModified()).thenReturn(1l);
		when(matcher.openInputStream()).thenReturn(new ByteArrayInputStream(
				"<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><generatedMatchers/>".getBytes()));
		underTest.init(processingEnv);
	}

	@Test
	public void testProcessFileWithIOExceptionWithoutException() throws IOException {
		ProvidesMatchersAnnotationsProcessor underTest = new ProvidesMatchersAnnotationsProcessor();
		underTest.init(processingEnv);
		when(matcher.openOutputStream()).thenReturn(new ByteArrayOutputStream());
		boolean result = underTest.processFileWithIOException(() -> matcher, FileObject::openOutputStream, (s) -> {
		});
		assertThat(result).is(true);
	}

	@Test
	public void testProcessFileWithIOExceptionWithException() throws IOException {
		ProvidesMatchersAnnotationsProcessor underTest = new ProvidesMatchersAnnotationsProcessor();
		underTest.init(processingEnv);
		when(matcher.openOutputStream()).thenReturn(new ByteArrayOutputStream());
		boolean result = underTest.processFileWithIOException(() -> matcher, FileObject::openOutputStream, (s) -> {
			throw new IOException("tst");
		});
		assertThat(result).is(false);
	}
}
