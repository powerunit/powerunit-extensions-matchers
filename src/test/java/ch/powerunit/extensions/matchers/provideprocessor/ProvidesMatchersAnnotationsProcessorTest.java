package ch.powerunit.extensions.matchers.provideprocessor;

import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Collections;

import javax.annotation.processing.Filer;
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
	ProcessingEnvironment processingEnv;

	@Mock
	Filer filer;

	@Mock
	FileObject matcher;

	private void prepare() {
		when(processingEnv.getFiler()).thenReturn(filer);
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
	public void testInitFactoryNoOldFIle() {
		ProvidesMatchersAnnotationsProcessor underTest = new ProvidesMatchersAnnotationsProcessor();
		when(matcher.getLastModified()).thenReturn(0l);
		underTest.init(processingEnv);
	}

	@Test
	public void testInitFactory() {
		ProvidesMatchersAnnotationsProcessor underTest = new ProvidesMatchersAnnotationsProcessor();
		when(processingEnv.getOptions()).thenReturn(Collections.singletonMap(
				"ch.powerunit.extensions.matchers.provideprocessor.ProvidesMatchersAnnotationsProcessor.factory",
				"test"));
		underTest.init(processingEnv);
	}
}
