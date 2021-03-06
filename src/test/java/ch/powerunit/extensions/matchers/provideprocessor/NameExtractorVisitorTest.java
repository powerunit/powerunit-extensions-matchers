package ch.powerunit.extensions.matchers.provideprocessor;

import static org.mockito.Mockito.when;

import java.util.Optional;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVariable;
import javax.lang.model.util.TypeKindVisitor8;

import org.mockito.Mock;
import org.mockito.Mockito;

import ch.powerunit.Rule;
import ch.powerunit.Test;
import ch.powerunit.TestRule;
import ch.powerunit.extensions.matchers.TestSuiteSupport;

public class NameExtractorVisitorTest implements TestSuiteSupport {

	private ProcessingEnvironment processingEnv;

	private RoundEnvironment roundEnv;

	@Mock
	private PrimitiveType primitiveType;

	@Mock
	private DeclaredType declaredType;

	@Mock
	private TypeVariable typeVariable;

	@Mock
	private TypeMirror typeMirror;

	@Mock
	private ArrayType arrayType;

	@Mock
	private TypeElement typeElement;

	@Rule
	public final TestRule rules = mockitoRule().around(before(this::prepare));

	private NameExtractorVisitor underTest;

	private void prepare() {
		processingEnv = generateMockitoProcessingEnvironment();
		roundEnv = generateMockitoRoundEnvironment();
		when(processingEnv.getElementUtils().getTypeElement(Mockito.anyString())).thenReturn(typeElement);
		RoundMirror rm = new RoundMirror(roundEnv, processingEnv);
		underTest = new NameExtractorVisitor(rm);
	}

	@Test(fastFail = false)
	public void testVisitPrimitiveAsBoolean() {
		when(processingEnv.getTypeUtils().boxedClass(primitiveType)).thenReturn(typeElement);
		when(primitiveType.toString()).thenReturn("boolean");
		when(typeElement.toString()).thenReturn("Boolean");

		Optional<String> r1 = underTest.visitPrimitive(primitiveType, false);
		assertThat(r1).isNotNull();
		assertThat(r1.orElse("error")).is("Boolean");

		Optional<String> r2 = underTest.visitPrimitive(primitiveType, true);
		assertThat(r2).isNotNull();
		assertThat(r2.orElse("error")).is("boolean");
	}

	@Test(fastFail = false)
	public void testVisitArray() {
		when(processingEnv.getTypeUtils().boxedClass(primitiveType)).thenReturn(typeElement);
		when(primitiveType.toString()).thenReturn("boolean");
		when(typeElement.toString()).thenReturn("Boolean");
		when(arrayType.getComponentType()).thenReturn(primitiveType);
		when(primitiveType.getKind()).thenReturn(TypeKind.BOOLEAN);
		when(primitiveType.accept(Mockito.any(), Mockito.any()))
				.thenAnswer(ip -> ip.getArgumentAt(0, TypeKindVisitor8.class).visitPrimitive(primitiveType,
						ip.getArgumentAt(1, Object.class)));

		Optional<String> r1 = underTest.visitArray(arrayType, false);
		assertThat(r1).isNotNull();
		assertThat(r1.orElse("error")).is("boolean[]");

		Optional<String> r2 = underTest.visitArray(arrayType, true);
		assertThat(r2).isNotNull();
		assertThat(r2.orElse("error")).is("boolean[]");
	}

	@Test(fastFail = false)
	public void testVisitDeclared() {
		when(declaredType.toString()).thenReturn("x");

		Optional<String> r1 = underTest.visitDeclared(declaredType, false);
		assertThat(r1).isNotNull();
		assertThat(r1.orElse("error")).is("x");

		Optional<String> r2 = underTest.visitDeclared(declaredType, true);
		assertThat(r2).isNotNull();
		assertThat(r2.orElse("error")).is("x");
	}

	@Test(fastFail = false)
	public void testVisitTypeVariable() {
		when(typeVariable.toString()).thenReturn("x");

		Optional<String> r1 = underTest.visitTypeVariable(typeVariable, false);
		assertThat(r1).isNotNull();
		assertThat(r1.orElse("error")).is("x");

		Optional<String> r2 = underTest.visitTypeVariable(typeVariable, true);
		assertThat(r2).isNotNull();
		assertThat(r2.orElse("error")).is("x");
	}

	@Test(fastFail = false)
	public void testVisitUnknown() {

		Optional<String> r1 = underTest.visitUnknown(typeMirror, false);
		assertThat(r1).isNotNull();
		assertThat(r1.isPresent()).is(false);

		Optional<String> r2 = underTest.visitUnknown(typeMirror, true);
		assertThat(r2).isNotNull();
		assertThat(r1.isPresent()).is(false);
	}

}
