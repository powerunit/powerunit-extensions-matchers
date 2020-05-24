package ch.powerunit.extensions.matchers.provideprocessor.fields;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;

import org.mockito.Mock;

import ch.powerunit.Rule;
import ch.powerunit.Test;
import ch.powerunit.TestRule;
import ch.powerunit.TestSuite;
import ch.powerunit.extensions.matchers.provideprocessor.ProvidesMatchersAnnotatedElementData;
import ch.powerunit.extensions.matchers.provideprocessor.RoundMirror;

public class FieldDescriptionMirrorTest implements TestSuite {
	@Rule
	public final TestRule rules = mockitoRule().around(before(this::prepareMock));

	@Mock
	private ProvidesMatchersAnnotatedElementData annotated;

	@Mock
	private RoundMirror roundMirror;

	@Mock
	private ProcessingEnvironment processingEnv;

	@Mock
	private Elements elements;

	private void prepareMock() {
		doReturn(roundMirror).when(annotated).getRoundMirror();
		doReturn(processingEnv).when(roundMirror).getProcessingEnv();
		doReturn(elements).when(processingEnv).getElementUtils();
	}

	private Name mockName(String name) {
		Name mock = mock(Name.class);
		doReturn(name).when(mock).toString();
		return mock;
	}

	@Test(fastFail = false)
	public void testExecutable() {
		ExecutableElement ee = mock(ExecutableElement.class);

		TypeElement te = mock(TypeElement.class);
		doReturn(te).when(elements).getTypeElement("Ft");
		doReturn(mockName("fn")).when(ee).getSimpleName();

		TypeMirror tm = mock(TypeMirror.class);
		doReturn(tm).when(ee).getReturnType();

		FieldDescriptionMirror underTest = new FieldDescriptionMirror(annotated, "fn", "Ft", ee);

		assertThat(underTest).isNotNull();

		assertThat(underTest.getFieldAccessor()).is("fn()");

		assertThat(underTest.getFieldName()).is("fn");

		assertThat(underTest.getMethodFieldName()).is("Fn");

		assertThat(underTest.getFieldType()).is("Ft");

		assertThat(underTest.getFieldElement()).is(sameInstance(ee));

		assertThat(underTest.getFieldTypeAsTypeElement()).is(sameInstance(te));

		assertThat(underTest.getFieldTypeMirror()).is(sameInstance(tm));
	}

	@Test(fastFail = false)
	public void testField() {
		VariableElement ee = mock(VariableElement.class);

		TypeElement te = mock(TypeElement.class);
		doReturn(te).when(elements).getTypeElement("Ft");
		doReturn(mockName("fn")).when(ee).getSimpleName();

		TypeMirror tm = mock(TypeMirror.class);
		doReturn(tm).when(ee).asType();

		FieldDescriptionMirror underTest = new FieldDescriptionMirror(annotated, "fn", "Ft", ee);

		assertThat(underTest).isNotNull();

		assertThat(underTest.getFieldAccessor()).is("fn");

		assertThat(underTest.getFieldName()).is("fn");

		assertThat(underTest.getMethodFieldName()).is("Fn");

		assertThat(underTest.getFieldType()).is("Ft");

		assertThat(underTest.getFieldElement()).is(sameInstance(ee));

		assertThat(underTest.getFieldTypeAsTypeElement()).is(sameInstance(te));

		assertThat(underTest.getFieldTypeMirror()).is(sameInstance(tm));
	}

}
