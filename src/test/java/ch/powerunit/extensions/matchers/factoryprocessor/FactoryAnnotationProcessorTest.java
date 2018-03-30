package ch.powerunit.extensions.matchers.factoryprocessor;

import static org.mockito.Mockito.when;

import java.util.Collections;

import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic.Kind;

import org.mockito.Mock;
import org.mockito.Mockito;

import ch.powerunit.Rule;
import ch.powerunit.Test;
import ch.powerunit.TestRule;
import ch.powerunit.TestSuite;

public class FactoryAnnotationProcessorTest implements TestSuite {

	@Mock
	private Elements elements;

	@Mock
	private Messager messageUtils;

	@Mock
	private ProcessingEnvironment processingEnv;

	@Mock
	private TypeElement factoryTE;

	private void prepareMock() {
		when(processingEnv.getMessager()).thenReturn(messageUtils);
		when(processingEnv.getElementUtils()).thenReturn(elements);
	}

	@Rule
	public final TestRule rules = mockitoRule().around(before(this::prepareMock))
			.around(before(() -> underTest = new FactoryAnnotationsProcessor()));

	private FactoryAnnotationsProcessor underTest;

	@Test
	public void testInitNoTarget() {
		when(processingEnv.getOptions()).thenReturn(Collections.emptyMap());
		underTest.init(processingEnv);
		Mockito.verify(messageUtils).printMessage(Kind.MANDATORY_WARNING, "The parameter `"
				+ FactoryAnnotationsProcessor.class.getName() + ".targets` is missing, please use it.");
	}
}
