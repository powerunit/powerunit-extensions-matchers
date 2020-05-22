package ch.powerunit.extensions.matchers.provideprocessor.fields;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;

import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVariable;
import javax.lang.model.type.TypeVisitor;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic.Kind;

import org.mockito.Mock;
import org.mockito.Mockito;

import ch.powerunit.Rule;
import ch.powerunit.Test;
import ch.powerunit.TestRule;
import ch.powerunit.TestSuite;
import ch.powerunit.extensions.matchers.IgnoreInMatcher;
import ch.powerunit.extensions.matchers.provideprocessor.ProvidesMatchersAnnotatedElementData;
import ch.powerunit.extensions.matchers.provideprocessor.RoundMirror;

public class FieldDescriptionProviderTest implements TestSuite {
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

	@Mock
	private Types types;

	@Mock
	private Messager messager;

	@Mock
	private VariableElement fieldElement;

	@Mock
	private FieldDescriptionMirror fieldMirror;

	private void prepareMock() {
		doReturn(roundMirror).when(annotated).getRoundMirror();
		doReturn(processingEnv).when(roundMirror).getProcessingEnv();
		doReturn(elements).when(processingEnv).getElementUtils();
		doReturn(types).when(processingEnv).getTypeUtils();
		doReturn(messager).when(processingEnv).getMessager();

		doReturn(fieldElement).when(fieldMirror).getFieldElement();

		doReturn(mock(TypeElement.class)).when(elements).getTypeElement(Mockito.anyString());

		doAnswer(i -> i.getArgumentAt(0, TypeMirror.class)).when(types).erasure(Mockito.any());
		doAnswer(i -> i.getArgumentAt(0, TypeMirror.class) == i.getArgumentAt(1, TypeMirror.class)).when(types)
				.isAssignable(Mockito.any(), Mockito.any());
	}

	@Test
	public void testOfIsIgnored() {

		doReturn(mock(IgnoreInMatcher.class)).when(fieldElement).getAnnotation(IgnoreInMatcher.class);

		AbstractFieldDescription result = FieldDescriptionProvider.of(annotated, fieldMirror);

		assertThat(result).isNotNull();
		assertThat(result).is(instanceOf(IgnoreFieldDescription.class));
	}

	@Test
	public void testOfPrimitive() {
		PrimitiveType pt = mock(PrimitiveType.class);
		doAnswer(i -> i.getArgumentAt(0, TypeVisitor.class).visitPrimitive(pt, i.getArgumentAt(1, Object.class)))
				.when(pt).accept(Mockito.any(), Mockito.any());
		doReturn(TypeKind.BOOLEAN).when(pt).getKind();

		doReturn(pt).when(fieldElement).asType();

		AbstractFieldDescription result = FieldDescriptionProvider.of(annotated, fieldMirror);

		assertThat(result).isNotNull();
		assertThat(result).is(instanceOf(PrimitiveFieldDescription.class));
	}

	@Test
	public void testOfArray() {
		ArrayType pt = mock(ArrayType.class);
		doAnswer(i -> i.getArgumentAt(0, TypeVisitor.class).visitArray(pt, i.getArgumentAt(1, Object.class))).when(pt)
				.accept(Mockito.any(), Mockito.any());
		doReturn(TypeKind.ARRAY).when(pt).getKind();

		doReturn(pt).when(fieldElement).asType();

		AbstractFieldDescription result = FieldDescriptionProvider.of(annotated, fieldMirror);

		assertThat(result).isNotNull();
		assertThat(result).is(instanceOf(ArrayFieldDescription.class));
	}

	@Test
	public void testOfDeclaredOptional() {
		DeclaredType pt = mock(DeclaredType.class);
		doAnswer(i -> i.getArgumentAt(0, TypeVisitor.class).visitDeclared(pt, i.getArgumentAt(1, Object.class)))
				.when(pt).accept(Mockito.any(), Mockito.any());
		doReturn(TypeKind.DECLARED).when(pt).getKind();

		doReturn(pt).when(fieldElement).asType();

		TypeElement tm = mock(TypeElement.class);
		doReturn(pt).when(tm).asType();
		doReturn(tm).when(elements).getTypeElement("java.util.Optional");

		AbstractFieldDescription result = FieldDescriptionProvider.of(annotated, fieldMirror);

		assertThat(result).isNotNull();
		assertThat(result).is(instanceOf(OptionalFieldDescription.class));
	}

	@Test
	public void testOfDeclaredMap() {
		DeclaredType pt = mock(DeclaredType.class);
		doAnswer(i -> i.getArgumentAt(0, TypeVisitor.class).visitDeclared(pt, i.getArgumentAt(1, Object.class)))
				.when(pt).accept(Mockito.any(), Mockito.any());
		doReturn(TypeKind.DECLARED).when(pt).getKind();

		doReturn(pt).when(fieldElement).asType();

		TypeElement tm = mock(TypeElement.class);
		doReturn(pt).when(tm).asType();
		doReturn(tm).when(elements).getTypeElement("java.util.Map");

		AbstractFieldDescription result = FieldDescriptionProvider.of(annotated, fieldMirror);

		assertThat(result).isNotNull();
		assertThat(result).is(instanceOf(MapFieldDescription.class));
	}

	@Test
	public void testOfDeclaredSet() {
		DeclaredType pt = mock(DeclaredType.class);
		doAnswer(i -> i.getArgumentAt(0, TypeVisitor.class).visitDeclared(pt, i.getArgumentAt(1, Object.class)))
				.when(pt).accept(Mockito.any(), Mockito.any());
		doReturn(TypeKind.DECLARED).when(pt).getKind();

		doReturn(pt).when(fieldElement).asType();

		TypeElement tm = mock(TypeElement.class);
		doReturn(pt).when(tm).asType();
		doReturn(tm).when(elements).getTypeElement("java.util.Set");

		AbstractFieldDescription result = FieldDescriptionProvider.of(annotated, fieldMirror);

		assertThat(result).isNotNull();
		assertThat(result).is(instanceOf(CollectionFieldDescription.class));
	}

	@Test
	public void testOfDeclaredList() {
		DeclaredType pt = mock(DeclaredType.class);
		doAnswer(i -> i.getArgumentAt(0, TypeVisitor.class).visitDeclared(pt, i.getArgumentAt(1, Object.class)))
				.when(pt).accept(Mockito.any(), Mockito.any());
		doReturn(TypeKind.DECLARED).when(pt).getKind();

		doReturn(pt).when(fieldElement).asType();

		TypeElement tm = mock(TypeElement.class);
		doReturn(pt).when(tm).asType();
		doReturn(tm).when(elements).getTypeElement("java.util.List");

		AbstractFieldDescription result = FieldDescriptionProvider.of(annotated, fieldMirror);

		assertThat(result).isNotNull();
		assertThat(result).is(instanceOf(CollectionFieldDescription.class));
	}

	@Test
	public void testOfDeclaredCollection() {
		DeclaredType pt = mock(DeclaredType.class);
		doAnswer(i -> i.getArgumentAt(0, TypeVisitor.class).visitDeclared(pt, i.getArgumentAt(1, Object.class)))
				.when(pt).accept(Mockito.any(), Mockito.any());
		doReturn(TypeKind.DECLARED).when(pt).getKind();

		doReturn(pt).when(fieldElement).asType();

		TypeElement tm = mock(TypeElement.class);
		doReturn(pt).when(tm).asType();
		doReturn(tm).when(elements).getTypeElement("java.util.Collection");

		AbstractFieldDescription result = FieldDescriptionProvider.of(annotated, fieldMirror);

		assertThat(result).isNotNull();
		assertThat(result).is(instanceOf(CollectionFieldDescription.class));
	}

	@Test
	public void testOfDeclaredString() {
		DeclaredType pt = mock(DeclaredType.class);
		doAnswer(i -> i.getArgumentAt(0, TypeVisitor.class).visitDeclared(pt, i.getArgumentAt(1, Object.class)))
				.when(pt).accept(Mockito.any(), Mockito.any());
		doReturn(TypeKind.DECLARED).when(pt).getKind();

		doReturn(pt).when(fieldElement).asType();

		TypeElement tm = mock(TypeElement.class);
		doReturn(pt).when(tm).asType();
		doReturn(tm).when(elements).getTypeElement("java.lang.String");

		AbstractFieldDescription result = FieldDescriptionProvider.of(annotated, fieldMirror);

		assertThat(result).isNotNull();
		assertThat(result).is(instanceOf(StringFieldDescription.class));
	}

	@Test
	public void testOfDeclaredComparable() {
		DeclaredType pt = mock(DeclaredType.class);
		doAnswer(i -> i.getArgumentAt(0, TypeVisitor.class).visitDeclared(pt, i.getArgumentAt(1, Object.class)))
				.when(pt).accept(Mockito.any(), Mockito.any());
		doReturn(TypeKind.DECLARED).when(pt).getKind();

		doReturn(pt).when(fieldElement).asType();

		TypeElement tm = mock(TypeElement.class);
		doReturn(pt).when(tm).asType();
		doReturn(tm).when(elements).getTypeElement("java.lang.Comparable");

		AbstractFieldDescription result = FieldDescriptionProvider.of(annotated, fieldMirror);

		assertThat(result).isNotNull();
		assertThat(result).is(instanceOf(ComparableFieldDescription.class));
	}

	@Test
	public void testOfDeclaredSupplier() {
		DeclaredType pt = mock(DeclaredType.class);
		doAnswer(i -> i.getArgumentAt(0, TypeVisitor.class).visitDeclared(pt, i.getArgumentAt(1, Object.class)))
				.when(pt).accept(Mockito.any(), Mockito.any());
		doReturn(TypeKind.DECLARED).when(pt).getKind();

		doReturn(pt).when(fieldElement).asType();

		TypeElement tm = mock(TypeElement.class);
		doReturn(pt).when(tm).asType();
		doReturn(tm).when(elements).getTypeElement("java.util.function.Supplier");

		AbstractFieldDescription result = FieldDescriptionProvider.of(annotated, fieldMirror);

		assertThat(result).isNotNull();
		assertThat(result).is(instanceOf(SupplierFieldDescription.class));
	}

	@Test
	public void testOfDeclaredGeneral() {
		DeclaredType pt = mock(DeclaredType.class);
		doAnswer(i -> i.getArgumentAt(0, TypeVisitor.class).visitDeclared(pt, i.getArgumentAt(1, Object.class)))
				.when(pt).accept(Mockito.any(), Mockito.any());
		doReturn(TypeKind.DECLARED).when(pt).getKind();

		doReturn(pt).when(fieldElement).asType();

		TypeElement tm = mock(TypeElement.class);
		doReturn(pt).when(tm).asType();

		AbstractFieldDescription result = FieldDescriptionProvider.of(annotated, fieldMirror);

		assertThat(result).isNotNull();
		assertThat(result).is(instanceOf(DefaultFieldDescription.class));
	}

	@Test
	public void testOfUnknownDeclaredGeneral() {
		DeclaredType pt = mock(DeclaredType.class);
		doAnswer(i -> i.getArgumentAt(0, TypeVisitor.class).visitUnknown(pt, i.getArgumentAt(1, Object.class))).when(pt)
				.accept(Mockito.any(), Mockito.any());
		doReturn(TypeKind.DECLARED).when(pt).getKind();

		doReturn(pt).when(fieldElement).asType();

		TypeElement tm = mock(TypeElement.class);
		doReturn(pt).when(tm).asType();

		AbstractFieldDescription result = FieldDescriptionProvider.of(annotated, fieldMirror);

		assertThat(result).isNotNull();
		assertThat(result).is(instanceOf(DefaultFieldDescription.class));
		Mockito.verify(messager).printMessage(Kind.MANDATORY_WARNING, "Unsupported type element");
	}

	@Test
	public void testOfDeclaredTypeVariable() {
		TypeVariable pt = mock(TypeVariable.class);
		doAnswer(i -> i.getArgumentAt(0, TypeVisitor.class).visitTypeVariable(pt, i.getArgumentAt(1, Object.class)))
				.when(pt).accept(Mockito.any(), Mockito.any());

		doReturn(pt).when(fieldElement).asType();

		AbstractFieldDescription result = FieldDescriptionProvider.of(annotated, fieldMirror);

		assertThat(result).isNotNull();
		assertThat(result).is(instanceOf(DefaultFieldDescription.class));
	}

}
