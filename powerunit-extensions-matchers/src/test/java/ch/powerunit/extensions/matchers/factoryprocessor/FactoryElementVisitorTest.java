package ch.powerunit.extensions.matchers.factoryprocessor;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.EnumSet;
import java.util.Optional;

import javax.annotation.processing.Messager;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic.Kind;

import org.mockito.Mock;
import org.mockito.Mockito;

import ch.powerunit.Rule;
import ch.powerunit.Test;
import ch.powerunit.TestRule;
import ch.powerunit.TestSuite;

public class FactoryElementVisitorTest implements TestSuite {

	@Mock
	private FactoryAnnotationsProcessor factoryAnnotationsProcessor;

	@Mock
	private Elements elements;

	@Mock
	private Messager messageUtils;

	@Mock
	private TypeElement factoryTE;

	@Rule
	public final TestRule rules = mockitoRule()
			.around(before(() -> underTest = new FactoryElementVisitor(factoryAnnotationsProcessor, elements,
					messageUtils, factoryTE)));

	private FactoryElementVisitor underTest;

	@Test
	public void testVisitExecutablePublicAndStaticThenResultIsPresent() {
		ExecutableElement ee = mock(ExecutableElement.class);
		when(ee.getModifiers()).thenReturn(EnumSet.of(Modifier.STATIC, Modifier.PUBLIC));
		when(ee.getKind()).thenReturn(ElementKind.METHOD);
		Optional<ExecutableElement> visitResult = underTest.visitExecutable(ee, null);
		assertThat(visitResult).isNotNull();
		assertThat(visitResult.isPresent()).is(true);
		assertThat(visitResult.get()).is(sameInstance(ee));
	}

	@Test
	public void testVisitExecutablePublicAndNotStaticThenResultIsPresent() {
		ExecutableElement ee = mock(ExecutableElement.class);
		when(ee.getModifiers()).thenReturn(EnumSet.of(Modifier.STATIC));
		when(ee.getKind()).thenReturn(ElementKind.METHOD);
		Optional<ExecutableElement> visitResult = underTest.visitExecutable(ee, null);
		assertThat(visitResult).isNotNull();
		assertThat(visitResult.isPresent()).is(false);
		verify(messageUtils).printMessage(Mockito.eq(Kind.MANDATORY_WARNING), Mockito.anyString(),
				Mockito.same(ee), Mockito.anyVararg());
	}
}
