package ch.powerunit.extensions.matchers.provideprocessor;

import static org.mockito.Mockito.when;

import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;

import org.mockito.Mock;

import ch.powerunit.Rule;
import ch.powerunit.Test;
import ch.powerunit.TestRule;
import ch.powerunit.TestSuite;

public class ProvidesMatchersElementVisitorTest implements TestSuite {
	@Mock
	private RoundMirror roundMirror;

	@Mock
	private ProcessingEnvironment processingEnv;

	@Mock
	private Messager messager;

	@Mock
	private Elements elements;

	@Mock
	private TypeElement typeElement;

	@Rule
	public final TestRule rules = mockitoRule().around(before(this::prepare));

	private ProvidesMatchersElementVisitor underTest;

	private void prepare() {
		when(roundMirror.getProcessingEnv()).thenReturn(processingEnv);
		when(processingEnv.getMessager()).thenReturn(messager);
		when(processingEnv.getElementUtils()).thenReturn(elements);
		underTest = new ProvidesMatchersElementVisitor(roundMirror);
	}

	@Test
	public void testVisitTypeEnum() {
		when(typeElement.getKind()).thenReturn(ElementKind.ENUM);
		assertThat(underTest.visitType(typeElement, null)).is(optionalIsNotPresent());
	}

	@Test
	public void testVisitTypeInterface() {
		when(typeElement.getKind()).thenReturn(ElementKind.INTERFACE);
		assertThat(underTest.visitType(typeElement, null)).is(optionalIsNotPresent());
	}

	@Test
	public void testVisitTypeAccepted() {
		when(typeElement.getKind()).thenReturn(ElementKind.CLASS);
		assertThat(underTest.visitType(typeElement, null)).is(optionalIs(typeElement));
	}

	@Test
	public void testDefaultAction() {
		assertThat(underTest.defaultAction(typeElement, null)).is(optionalIsNotPresent());
	}
}
