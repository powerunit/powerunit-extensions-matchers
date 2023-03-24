package ch.powerunit.extensions.matchers.provideprocessor;

import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import org.mockito.Mock;
import org.mockito.Mockito;

import ch.powerunit.Rule;
import ch.powerunit.Test;
import ch.powerunit.TestRule;
import ch.powerunit.TestSuite;
import ch.powerunit.extensions.matchers.AddToMatcher;
import ch.powerunit.extensions.matchers.provideprocessor.fields.AbstractFieldDescription;
import ch.powerunit.extensions.matchers.provideprocessor.fields.DefaultFieldDescription;

public class ProvidesMatchersSubElementVisitorTest implements TestSuite {

	@Mock
	private ProvidesMatchersAnnotatedElementMirror providesMatchersAnnotatedElementMirror;

	@Mock
	private ProcessingEnvironment processingEnv;

	@Mock
	private Messager messager;

	@Mock
	private DefaultFieldDescription fieldDescription;

	@Mock
	private Element targetElement;

	@Mock
	private VariableElement variableElement;

	@Mock
	private Name variableName;

	@Mock
	private Name executableName;

	@Mock
	private PrimitiveType typeMirror;

	@Mock
	private Element elementType;

	@Mock
	private Elements elements;

	@Mock
	private TypeElement typeElement;

	@Mock
	private Types types;

	@Mock
	private ExecutableElement executableElement;

	@Mock
	private RoundMirror roundMirror;

	@Rule
	public final TestRule rules = mockitoRule().around(before(this::prepare));

	private void prepare() {
		when(providesMatchersAnnotatedElementMirror.getRoundMirror()).thenReturn(roundMirror);
		when(roundMirror.getProcessingEnv()).thenReturn(processingEnv);
		when(fieldDescription.getFieldElement()).thenReturn(targetElement);
		when(fieldDescription.getFieldName()).thenReturn("fn");
		when(processingEnv.getElementUtils()).thenReturn(elements);
		when(processingEnv.getMessager()).thenReturn(messager);
		when(processingEnv.getTypeUtils()).thenReturn(types);
		when(elements.getTypeElement(Mockito.anyString())).thenReturn(typeElement);
		when(types.asElement(typeMirror)).thenReturn(elementType);
		when(variableElement.getSimpleName()).thenReturn(variableName);
		when(variableName.toString()).thenReturn("fn");
		when(executableElement.getSimpleName()).thenReturn(executableName);
		when(variableElement.getAnnotationsByType(AddToMatcher.class)).thenReturn(new AddToMatcher[] {});
		when(variableElement.asType()).thenReturn(typeMirror);
		when(typeMirror.getKind()).thenReturn(TypeKind.BOOLEAN);
		when(typeMirror.accept(Mockito.argThat(instanceOf(NameExtractorVisitor.class)), Mockito.any()))
				.thenReturn(Optional.of("x"));
		when(executableElement.getReturnType()).thenReturn(typeMirror);
		when(executableElement.getAnnotationsByType(AddToMatcher.class)).thenReturn(new AddToMatcher[] {});
		underTest = new ProvidesMatchersSubElementVisitor(roundMirror);
	}

	private ProvidesMatchersSubElementVisitor underTest;

	@Test
	public void testDefaultActionThenEmpty() {
		Optional<AbstractFieldDescription> ofd = underTest.defaultAction(targetElement,
				providesMatchersAnnotatedElementMirror);
		assertThat(ofd).isNotNull();
		assertThat(ofd.isPresent()).is(false);
	}

	@Test
	public void testVisitVariableNoPublicAndNotInListThenEmptyAndNoWarning() {
		when(variableElement.getModifiers()).thenReturn(Collections.emptySet());
		when(variableElement.getEnclosingElement()).thenReturn(variableElement);
		Optional<AbstractFieldDescription> ofd = underTest.visitVariable(variableElement,
				providesMatchersAnnotatedElementMirror);
		assertThat(ofd).isNotNull();
		assertThat(ofd.isPresent()).is(false);
		Mockito.verify(messager, Mockito.never()).printMessage(Mockito.any(), Mockito.anyString(), Mockito.any());
		Mockito.verify(roundMirror).removeFromIgnoreList(Mockito.any());
	}

	@Test
	public void testVisitVariablePublicAndStaticAndNotInListThenEmptyAndNoWarning() {
		when(variableElement.getModifiers()).thenReturn(new HashSet(Arrays.asList(Modifier.PUBLIC, Modifier.STATIC)));
		when(variableElement.getEnclosingElement()).thenReturn(variableElement);
		Optional<AbstractFieldDescription> ofd = underTest.visitVariable(variableElement,
				providesMatchersAnnotatedElementMirror);
		assertThat(ofd).isNotNull();
		assertThat(ofd.isPresent()).is(false);
		Mockito.verify(messager, Mockito.never()).printMessage(Mockito.any(), Mockito.anyString(), Mockito.any());
		Mockito.verify(roundMirror).removeFromIgnoreList(Mockito.any());
	}

	@Test
	public void testVisitExecutableNoPublicAndNotInListThenEmptyAndNoWarning() {
		when(executableElement.getModifiers()).thenReturn(Collections.emptySet());
		Optional<AbstractFieldDescription> ofd = underTest.visitExecutable(executableElement,
				providesMatchersAnnotatedElementMirror);
		assertThat(ofd).isNotNull();
		assertThat(ofd.isPresent()).is(false);
		Mockito.verify(messager, Mockito.never()).printMessage(Mockito.any(), Mockito.anyString(), Mockito.any());
		Mockito.verify(roundMirror).removeFromIgnoreList(Mockito.any());
	}

	@Test
	public void testVisitExecutablePublicAndStaticAndNotInListThenEmptyAndNoWarning() {
		when(executableElement.getModifiers()).thenReturn(new HashSet(Arrays.asList(Modifier.PUBLIC, Modifier.STATIC)));
		when(executableElement.getParameters()).thenReturn(Collections.emptyList());
		Optional<AbstractFieldDescription> ofd = underTest.visitExecutable(executableElement,
				providesMatchersAnnotatedElementMirror);
		assertThat(ofd).isNotNull();
		assertThat(ofd.isPresent()).is(false);
		Mockito.verify(messager, Mockito.never()).printMessage(Mockito.any(), Mockito.anyString(), Mockito.any());
		Mockito.verify(roundMirror).removeFromIgnoreList(Mockito.any());
	}

	@Test
	public void testVisitExecutablePublicStaticAndNotSize0AndNotInListThenEmptyAndNoWarning() {
		when(executableElement.getModifiers()).thenReturn(new HashSet(Arrays.asList(Modifier.PUBLIC)));
		when(executableElement.getParameters()).thenReturn((List) Collections.singletonList(variableElement));
		Optional<AbstractFieldDescription> ofd = underTest.visitExecutable(executableElement,
				providesMatchersAnnotatedElementMirror);
		assertThat(ofd).isNotNull();
		assertThat(ofd.isPresent()).is(false);
		Mockito.verify(messager, Mockito.never()).printMessage(Mockito.any(), Mockito.anyString(), Mockito.any());
		Mockito.verify(roundMirror).removeFromIgnoreList(Mockito.any());
	}

	@Test
	public void testVisitVariableNoPublicAndInListThenEmptyAndWarning() {
		when(variableElement.getModifiers()).thenReturn(Collections.emptySet());
		when(variableElement.getEnclosingElement()).thenReturn(variableElement);
		when(roundMirror.removeFromIgnoreList(Mockito.any())).thenReturn(true);
		Optional<AbstractFieldDescription> ofd = underTest.visitVariable(variableElement,
				providesMatchersAnnotatedElementMirror);
		assertThat(ofd).isNotNull();
		assertThat(ofd.isPresent()).is(false);
		Mockito.verify(messager).printMessage(Mockito.any(), Mockito.anyString(), Mockito.any());
		Mockito.verify(roundMirror).removeFromIgnoreList(Mockito.any());
	}

	@Test
	public void testVisitVariablePublicAndStaticAndInListThenEmptyAndWarning() {
		when(variableElement.getModifiers()).thenReturn(new HashSet(Arrays.asList(Modifier.PUBLIC, Modifier.STATIC)));
		when(variableElement.getEnclosingElement()).thenReturn(variableElement);
		when(roundMirror.removeFromIgnoreList(Mockito.any())).thenReturn(true);
		Optional<AbstractFieldDescription> ofd = underTest.visitVariable(variableElement,
				providesMatchersAnnotatedElementMirror);
		assertThat(ofd).isNotNull();
		assertThat(ofd.isPresent()).is(false);
		Mockito.verify(messager).printMessage(Mockito.any(), Mockito.anyString(), Mockito.any());
		Mockito.verify(roundMirror).removeFromIgnoreList(Mockito.any());
	}

	@Test
	public void testVisitExecutableNoPublicAndInListThenEmptyAndWarning() {
		when(executableElement.getModifiers()).thenReturn(Collections.emptySet());
		when(roundMirror.removeFromIgnoreList(Mockito.any())).thenReturn(true);
		when(roundMirror.removeFromIgnoreList(Mockito.any())).thenReturn(true);
		Optional<AbstractFieldDescription> ofd = underTest.visitExecutable(executableElement,
				providesMatchersAnnotatedElementMirror);
		assertThat(ofd).isNotNull();
		assertThat(ofd.isPresent()).is(false);
		Mockito.verify(messager).printMessage(Mockito.any(), Mockito.anyString(), Mockito.any());
		Mockito.verify(roundMirror).removeFromIgnoreList(Mockito.any());
	}

	@Test
	public void testVisitExecutablePublicAndStaticAndInListThenEmptyAndWarning() {
		when(executableElement.getModifiers()).thenReturn(new HashSet(Arrays.asList(Modifier.PUBLIC, Modifier.STATIC)));
		when(roundMirror.removeFromIgnoreList(Mockito.any())).thenReturn(true);
		when(executableElement.getParameters()).thenReturn(Collections.emptyList());
		Optional<AbstractFieldDescription> ofd = underTest.visitExecutable(executableElement,
				providesMatchersAnnotatedElementMirror);
		assertThat(ofd).isNotNull();
		assertThat(ofd.isPresent()).is(false);
		Mockito.verify(messager).printMessage(Mockito.any(), Mockito.anyString(), Mockito.any());
		Mockito.verify(roundMirror).removeFromIgnoreList(Mockito.any());
	}

	@Test
	public void testVisitExecutablePublicAndNotStaticAndZize0AndNamedTotoThenEmptyAndWarning() {
		when(executableName.toString()).thenReturn("toto");
		when(roundMirror.removeFromIgnoreList(Mockito.any())).thenReturn(true);
		when(executableElement.getModifiers()).thenReturn(Collections.singleton(Modifier.PUBLIC));
		when(executableElement.getParameters()).thenReturn(Collections.emptyList());
		when(executableElement.getEnclosingElement()).thenReturn(executableElement);
		Optional<AbstractFieldDescription> ofd = underTest.visitExecutable(executableElement,
				providesMatchersAnnotatedElementMirror);
		assertThat(ofd).isNotNull();
		assertThat(ofd.isPresent()).is(false);
		Mockito.verify(messager).printMessage(Mockito.any(), Mockito.anyString(), Mockito.any());
		Mockito.verify(roundMirror).removeFromIgnoreList(Mockito.any());
	}

	@Test
	public void testVisitExecutablePublicStaticAndNotSize0AndInListThenEmptyAndWarning() {
		when(executableElement.getModifiers()).thenReturn(new HashSet(Arrays.asList(Modifier.PUBLIC)));
		when(roundMirror.removeFromIgnoreList(Mockito.any())).thenReturn(true);
		when(executableElement.getParameters()).thenReturn((List) Collections.singletonList(variableElement));
		Optional<AbstractFieldDescription> ofd = underTest.visitExecutable(executableElement,
				providesMatchersAnnotatedElementMirror);
		assertThat(ofd).isNotNull();
		assertThat(ofd.isPresent()).is(false);
		Mockito.verify(messager).printMessage(Mockito.any(), Mockito.anyString(), Mockito.any());
		Mockito.verify(roundMirror).removeFromIgnoreList(Mockito.any());
	}

}
