package ch.powerunit.extensions.matchers.provideprocessor;

import static org.mockito.Mockito.when;

import java.util.Optional;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;

import org.mockito.Mock;
import org.mockito.Mockito;

import ch.powerunit.Rule;
import ch.powerunit.Test;
import ch.powerunit.TestRule;
import ch.powerunit.TestSuite;

public class ProvidesMatchersSubElementVisitorTest implements TestSuite {

	@Mock
	private ProvidesMatchersAnnotatedElementMirror providesMatchersAnnotatedElementMirror;

	@Mock
	private ProcessingEnvironment processingEnv;

	@Mock
	private FieldDescription fieldDescription;

	@Mock
	private Element targetElement;

	@Rule
	public final TestRule rules = mockitoRule().around(before(this::prepare));

	private void prepare() {
		underTest = new ProvidesMatchersSubElementVisitor(processingEnv, a -> false);
		when(fieldDescription.getFieldElement()).thenReturn(targetElement);
	}

	private ProvidesMatchersSubElementVisitor underTest;

	@Test
	public void testRemoveIfNeededAndThenReturnEmptyThenEmptyAndNoRemove() {
		Optional<FieldDescription> ofd = ProvidesMatchersSubElementVisitor.removeIfNeededAndThenReturn(Optional.empty(),
				providesMatchersAnnotatedElementMirror);
		assertThat(ofd).isNotNull();
		assertThat(ofd.isPresent()).is(false);
		Mockito.verify(providesMatchersAnnotatedElementMirror, Mockito.never()).removeFromIgnoreList(Mockito.any());
	}

	@Test
	public void testRemoveIfNeededAndThenReturnNotEmptyThenNotEmptyAndRemove() {
		Optional<FieldDescription> ofd = ProvidesMatchersSubElementVisitor
				.removeIfNeededAndThenReturn(Optional.of(fieldDescription), providesMatchersAnnotatedElementMirror);
		assertThat(ofd).isNotNull();
		assertThat(ofd.isPresent()).is(true);
		assertThat(ofd.get()).is(sameInstance(fieldDescription));
		Mockito.verify(providesMatchersAnnotatedElementMirror).removeFromIgnoreList(targetElement);
	}

	@Test
	public void testDefaultActionThenEmpty() {
		Optional<FieldDescription> ofd = underTest.defaultAction(targetElement, providesMatchersAnnotatedElementMirror);
		assertThat(ofd).isNotNull();
		assertThat(ofd.isPresent()).is(false);
	}
}
