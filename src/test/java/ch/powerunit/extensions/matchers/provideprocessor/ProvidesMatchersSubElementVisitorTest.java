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
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.util.Types;

import org.mockito.Mock;
import org.mockito.Mockito;

import ch.powerunit.Rule;
import ch.powerunit.Test;
import ch.powerunit.TestRule;
import ch.powerunit.TestSuite;
import ch.powerunit.extensions.matchers.AddToMatcher;
import ch.powerunit.extensions.matchers.provideprocessor.FieldDescription.Type;

public class ProvidesMatchersSubElementVisitorTest implements TestSuite {

	@Mock
	private ProvidesMatchersAnnotatedElementMirror providesMatchersAnnotatedElementMirror;

	@Mock
	private ProcessingEnvironment processingEnv;

	@Mock
	private Messager messager;

	@Mock
	private FieldDescription fieldDescription;

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
	private Types types;

	@Mock
	private ExecutableElement executableElement;

	@Rule
	public final TestRule rules = mockitoRule().around(before(this::prepare));

	private void prepare() {
		underTest = new ProvidesMatchersSubElementVisitor(processingEnv, a -> false);
		when(fieldDescription.getFieldElement()).thenReturn(targetElement);
		when(processingEnv.getMessager()).thenReturn(messager);
		when(processingEnv.getTypeUtils()).thenReturn(types);
		when(types.asElement(typeMirror)).thenReturn(elementType);
		when(variableElement.getSimpleName()).thenReturn(variableName);
		when(variableName.toString()).thenReturn("fn");
		when(executableElement.getSimpleName()).thenReturn(executableName);
		when(variableElement.getAnnotationsByType(AddToMatcher.class)).thenReturn(new AddToMatcher[] {});
		when(variableElement.asType()).thenReturn(typeMirror);
		when(typeMirror.getKind()).thenReturn(TypeKind.BOOLEAN);
		when(typeMirror.accept(Mockito.argThat(instanceOf(NameExtractorVisitor.class)), Mockito.any()))
				.thenReturn(Optional.of("x"));
		when(typeMirror.accept(Mockito.argThat(instanceOf(FieldDescription.ExtracTypeVisitor.class)), Mockito.any()))
				.thenReturn(Type.NA);
		when(executableElement.getReturnType()).thenReturn(typeMirror);
		when(executableElement.getAnnotationsByType(AddToMatcher.class)).thenReturn(new AddToMatcher[] {});
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

	@Test
	public void testVisitVariableNoPublicAndNotInListThenEmptyAndNoWarning() {
		when(variableElement.getModifiers()).thenReturn(Collections.emptySet());
		Optional<FieldDescription> ofd = underTest.visitVariable(variableElement,
				providesMatchersAnnotatedElementMirror);
		assertThat(ofd).isNotNull();
		assertThat(ofd.isPresent()).is(false);
		Mockito.verify(messager, Mockito.never()).printMessage(Mockito.any(), Mockito.anyString(), Mockito.any());
		Mockito.verify(providesMatchersAnnotatedElementMirror, Mockito.never()).removeFromIgnoreList(Mockito.any());
	}

	@Test
	public void testVisitVariablePublicAndStaticAndNotInListThenEmptyAndNoWarning() {
		when(variableElement.getModifiers()).thenReturn(new HashSet(Arrays.asList(Modifier.PUBLIC, Modifier.STATIC)));
		Optional<FieldDescription> ofd = underTest.visitVariable(variableElement,
				providesMatchersAnnotatedElementMirror);
		assertThat(ofd).isNotNull();
		assertThat(ofd.isPresent()).is(false);
		Mockito.verify(messager, Mockito.never()).printMessage(Mockito.any(), Mockito.anyString(), Mockito.any());
		Mockito.verify(providesMatchersAnnotatedElementMirror, Mockito.never()).removeFromIgnoreList(Mockito.any());
	}

	@Test
	public void testVisitExecutableNoPublicAndNotInListThenEmptyAndNoWarning() {
		when(executableElement.getModifiers()).thenReturn(Collections.emptySet());
		Optional<FieldDescription> ofd = underTest.visitExecutable(executableElement,
				providesMatchersAnnotatedElementMirror);
		assertThat(ofd).isNotNull();
		assertThat(ofd.isPresent()).is(false);
		Mockito.verify(messager, Mockito.never()).printMessage(Mockito.any(), Mockito.anyString(), Mockito.any());
		Mockito.verify(providesMatchersAnnotatedElementMirror, Mockito.never()).removeFromIgnoreList(Mockito.any());
	}

	@Test
	public void testVisitExecutablePublicAndStaticAndNotInListThenEmptyAndNoWarning() {
		when(executableElement.getModifiers()).thenReturn(new HashSet(Arrays.asList(Modifier.PUBLIC, Modifier.STATIC)));
		when(executableElement.getParameters()).thenReturn(Collections.emptyList());
		Optional<FieldDescription> ofd = underTest.visitExecutable(executableElement,
				providesMatchersAnnotatedElementMirror);
		assertThat(ofd).isNotNull();
		assertThat(ofd.isPresent()).is(false);
		Mockito.verify(messager, Mockito.never()).printMessage(Mockito.any(), Mockito.anyString(), Mockito.any());
		Mockito.verify(providesMatchersAnnotatedElementMirror, Mockito.never()).removeFromIgnoreList(Mockito.any());
	}

	@Test
	public void testVisitExecutablePublicStaticAndNotSize0AndNotInListThenEmptyAndNoWarning() {
		when(executableElement.getModifiers()).thenReturn(new HashSet(Arrays.asList(Modifier.PUBLIC)));
		when(executableElement.getParameters()).thenReturn((List) Collections.singletonList(variableElement));
		Optional<FieldDescription> ofd = underTest.visitExecutable(executableElement,
				providesMatchersAnnotatedElementMirror);
		assertThat(ofd).isNotNull();
		assertThat(ofd.isPresent()).is(false);
		Mockito.verify(messager, Mockito.never()).printMessage(Mockito.any(), Mockito.anyString(), Mockito.any());
		Mockito.verify(providesMatchersAnnotatedElementMirror, Mockito.never()).removeFromIgnoreList(Mockito.any());
	}

	@Test
	public void testVisitVariableNoPublicAndInListThenEmptyAndWarning() {
		when(variableElement.getModifiers()).thenReturn(Collections.emptySet());
		when(providesMatchersAnnotatedElementMirror.isInsideIgnoreList(Mockito.any())).thenReturn(true);
		Optional<FieldDescription> ofd = underTest.visitVariable(variableElement,
				providesMatchersAnnotatedElementMirror);
		assertThat(ofd).isNotNull();
		assertThat(ofd.isPresent()).is(false);
		Mockito.verify(messager).printMessage(Mockito.any(), Mockito.anyString(), Mockito.any());
		Mockito.verify(providesMatchersAnnotatedElementMirror).removeFromIgnoreList(Mockito.any());
	}

	@Test
	public void testVisitVariablePublicAndStaticAndInListThenEmptyAndWarning() {
		when(variableElement.getModifiers()).thenReturn(new HashSet(Arrays.asList(Modifier.PUBLIC, Modifier.STATIC)));
		when(providesMatchersAnnotatedElementMirror.isInsideIgnoreList(Mockito.any())).thenReturn(true);
		Optional<FieldDescription> ofd = underTest.visitVariable(variableElement,
				providesMatchersAnnotatedElementMirror);
		assertThat(ofd).isNotNull();
		assertThat(ofd.isPresent()).is(false);
		Mockito.verify(messager).printMessage(Mockito.any(), Mockito.anyString(), Mockito.any());
		Mockito.verify(providesMatchersAnnotatedElementMirror).removeFromIgnoreList(Mockito.any());
	}

	@Test
	public void testVisitExecutableNoPublicAndInListThenEmptyAndWarning() {
		when(executableElement.getModifiers()).thenReturn(Collections.emptySet());
		when(providesMatchersAnnotatedElementMirror.isInsideIgnoreList(Mockito.any())).thenReturn(true);
		when(providesMatchersAnnotatedElementMirror.isInsideIgnoreList(Mockito.any())).thenReturn(true);
		Optional<FieldDescription> ofd = underTest.visitExecutable(executableElement,
				providesMatchersAnnotatedElementMirror);
		assertThat(ofd).isNotNull();
		assertThat(ofd.isPresent()).is(false);
		Mockito.verify(messager).printMessage(Mockito.any(), Mockito.anyString(), Mockito.any());
		Mockito.verify(providesMatchersAnnotatedElementMirror).removeFromIgnoreList(Mockito.any());
	}

	@Test
	public void testVisitExecutablePublicAndStaticAndInListThenEmptyAndWarning() {
		when(executableElement.getModifiers()).thenReturn(new HashSet(Arrays.asList(Modifier.PUBLIC, Modifier.STATIC)));
		when(providesMatchersAnnotatedElementMirror.isInsideIgnoreList(Mockito.any())).thenReturn(true);
		when(executableElement.getParameters()).thenReturn(Collections.emptyList());
		Optional<FieldDescription> ofd = underTest.visitExecutable(executableElement,
				providesMatchersAnnotatedElementMirror);
		assertThat(ofd).isNotNull();
		assertThat(ofd.isPresent()).is(false);
		Mockito.verify(messager).printMessage(Mockito.any(), Mockito.anyString(), Mockito.any());
		Mockito.verify(providesMatchersAnnotatedElementMirror).removeFromIgnoreList(Mockito.any());
	}

	@Test
	public void testVisitExecutablePublicStaticAndNotSize0AndInListThenEmptyAndWarning() {
		when(executableElement.getModifiers()).thenReturn(new HashSet(Arrays.asList(Modifier.PUBLIC)));
		when(providesMatchersAnnotatedElementMirror.isInsideIgnoreList(Mockito.any())).thenReturn(true);
		when(executableElement.getParameters()).thenReturn((List) Collections.singletonList(variableElement));
		Optional<FieldDescription> ofd = underTest.visitExecutable(executableElement,
				providesMatchersAnnotatedElementMirror);
		assertThat(ofd).isNotNull();
		assertThat(ofd.isPresent()).is(false);
		Mockito.verify(messager).printMessage(Mockito.any(), Mockito.anyString(), Mockito.any());
		Mockito.verify(providesMatchersAnnotatedElementMirror).removeFromIgnoreList(Mockito.any());
	}

	@Test
	public void testVisitVariablePublicAndNotStaticThenReturnFieldDescription() {
		when(variableElement.getModifiers()).thenReturn(Collections.singleton(Modifier.PUBLIC));
		Optional<FieldDescription> ofd = underTest.visitVariable(variableElement,
				providesMatchersAnnotatedElementMirror);
		assertThat(ofd).isNotNull();
		assertThat(ofd.isPresent()).is(true);
		assertThat(ofd.get().getFieldName()).is("fn");
	}

	@Test
	public void testVisitExecutablePublicAndNotStaticAndZize0AndNamedGetThenReturnFieldDescription() {
		when(executableName.toString()).thenReturn("getXXX");
		when(executableElement.getModifiers()).thenReturn(Collections.singleton(Modifier.PUBLIC));
		when(executableElement.getParameters()).thenReturn(Collections.emptyList());
		Optional<FieldDescription> ofd = underTest.visitExecutable(executableElement,
				providesMatchersAnnotatedElementMirror);
		assertThat(ofd).isNotNull();
		assertThat(ofd.isPresent()).is(true);
		assertThat(ofd.get().getFieldName()).is("xXX");
	}

	@Test
	public void testVisitExecutablePublicAndNotStaticAndZize0AndNamedIsThenReturnFieldDescription() {
		when(executableName.toString()).thenReturn("isXy");
		when(executableElement.getModifiers()).thenReturn(Collections.singleton(Modifier.PUBLIC));
		when(executableElement.getParameters()).thenReturn(Collections.emptyList());
		Optional<FieldDescription> ofd = underTest.visitExecutable(executableElement,
				providesMatchersAnnotatedElementMirror);
		assertThat(ofd).isNotNull();
		assertThat(ofd.isPresent()).is(true);
		assertThat(ofd.get().getFieldName()).is("xy");
	}

}
