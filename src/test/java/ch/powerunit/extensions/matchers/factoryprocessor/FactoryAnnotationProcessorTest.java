package ch.powerunit.extensions.matchers.factoryprocessor;

import static org.mockito.Mockito.when;

import java.util.Collections;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
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
	private Filer filer;

	@Mock
	private Types typeUtils;

	@Mock
	private ProcessingEnvironment processingEnv;

	@Mock
	private RoundEnvironment roundEnv;

	@Mock
	private TypeElement factoryTE;

	private void prepareMock() {
		when(processingEnv.getMessager()).thenReturn(messageUtils);
		when(processingEnv.getFiler()).thenReturn(filer);
		when(processingEnv.getTypeUtils()).thenReturn(typeUtils);
		when(processingEnv.getElementUtils()).thenReturn(elements);
	}

	@Rule
	public final TestRule rules = mockitoRule().around(before(this::prepareMock))
			.around(before(() -> underTest = new FactoryAnnotationsProcessor()));

	private FactoryAnnotationsProcessor underTest;

	@Test
	public void testHelperMethod() {
		when(processingEnv.getOptions()).thenReturn(Collections.emptyMap());
		underTest.init(processingEnv);

		assertThat(underTest.getElementUtils()).is(sameInstance(elements));
		assertThat(underTest.getFiler()).is(sameInstance(filer));
		assertThat(underTest.getTypeUtils()).is(sameInstance(typeUtils));
	}

	@Test
	public void testInitNoTarget() {
		when(processingEnv.getOptions()).thenReturn(Collections.emptyMap());
		underTest.init(processingEnv);
		Mockito.verify(messageUtils).printMessage(Kind.MANDATORY_WARNING, "The parameter `"
				+ FactoryAnnotationsProcessor.class.getName() + ".targets` is missing, please use it.");
	}

	@Test
	public void testProcessNoTarget() {
		when(processingEnv.getOptions()).thenReturn(Collections.emptyMap());
		underTest.init(processingEnv);
		assertThat(underTest.process(Collections.emptySet(), roundEnv)).is(false);
	}
}
