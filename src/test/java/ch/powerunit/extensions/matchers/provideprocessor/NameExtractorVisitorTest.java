package ch.powerunit.extensions.matchers.provideprocessor;

import static org.mockito.Mockito.when;

import java.util.Optional;

import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVariable;
import javax.lang.model.util.TypeKindVisitor8;
import javax.lang.model.util.Types;

import org.mockito.Mock;
import org.mockito.Mockito;

import ch.powerunit.Rule;
import ch.powerunit.Test;
import ch.powerunit.TestRule;
import ch.powerunit.TestSuite;

public class NameExtractorVisitorTest implements TestSuite {
	@Mock
	private ProcessingEnvironment processingEnv;

	@Mock
	private PrimitiveType primitiveType;

	@Mock
	private DeclaredType declaredType;

	@Mock
	private TypeVariable typeVariable;

	@Mock
	private Types types;

	@Mock
	private Messager messager;

	@Mock
	private TypeMirror typeMirror;

	@Mock
	private ArrayType arrayType;

	@Rule
	public final TestRule rules = mockitoRule().around(before(this::prepare));

	private NameExtractorVisitor underTest;

	private void prepare() {
		when(processingEnv.getTypeUtils()).thenReturn(types);
		when(processingEnv.getMessager()).thenReturn(messager);
		underTest = new NameExtractorVisitor(processingEnv);
	}

	@Test(fastFail = false)
	public void testVisitPrimitiveAsBoolean() {
		Optional<String> r1 = underTest.visitPrimitiveAsBoolean(primitiveType, false);
		assertThat(r1).isNotNull();
		assertThat(r1.orElse("error")).is("Boolean");

		Optional<String> r2 = underTest.visitPrimitiveAsBoolean(primitiveType, true);
		assertThat(r2).isNotNull();
		assertThat(r2.orElse("error")).is("boolean");
	}

	@Test(fastFail = false)
	public void testVisitPrimitiveAsByte() {
		Optional<String> r1 = underTest.visitPrimitiveAsByte(primitiveType, false);
		assertThat(r1).isNotNull();
		assertThat(r1.orElse("error")).is("Byte");

		Optional<String> r2 = underTest.visitPrimitiveAsByte(primitiveType, true);
		assertThat(r2).isNotNull();
		assertThat(r2.orElse("error")).is("byte");
	}

	@Test(fastFail = false)
	public void testVisitPrimitiveAsShort() {
		Optional<String> r1 = underTest.visitPrimitiveAsShort(primitiveType, false);
		assertThat(r1).isNotNull();
		assertThat(r1.orElse("error")).is("Short");

		Optional<String> r2 = underTest.visitPrimitiveAsShort(primitiveType, true);
		assertThat(r2).isNotNull();
		assertThat(r2.orElse("error")).is("short");
	}

	@Test(fastFail = false)
	public void testVisitPrimitiveAsInt() {
		Optional<String> r1 = underTest.visitPrimitiveAsInt(primitiveType, false);
		assertThat(r1).isNotNull();
		assertThat(r1.orElse("error")).is("Integer");

		Optional<String> r2 = underTest.visitPrimitiveAsInt(primitiveType, true);
		assertThat(r2).isNotNull();
		assertThat(r2.orElse("error")).is("int");
	}

	@Test(fastFail = false)
	public void testVisitPrimitiveAsLong() {
		Optional<String> r1 = underTest.visitPrimitiveAsLong(primitiveType, false);
		assertThat(r1).isNotNull();
		assertThat(r1.orElse("error")).is("Long");

		Optional<String> r2 = underTest.visitPrimitiveAsLong(primitiveType, true);
		assertThat(r2).isNotNull();
		assertThat(r2.orElse("error")).is("long");
	}

	@Test(fastFail = false)
	public void testVisitPrimitiveAsChar() {
		Optional<String> r1 = underTest.visitPrimitiveAsChar(primitiveType, false);
		assertThat(r1).isNotNull();
		assertThat(r1.orElse("error")).is("Character");

		Optional<String> r2 = underTest.visitPrimitiveAsChar(primitiveType, true);
		assertThat(r2).isNotNull();
		assertThat(r2.orElse("error")).is("char");
	}

	@Test(fastFail = false)
	public void testVisitPrimitiveAsFloat() {
		Optional<String> r1 = underTest.visitPrimitiveAsFloat(primitiveType, false);
		assertThat(r1).isNotNull();
		assertThat(r1.orElse("error")).is("Float");

		Optional<String> r2 = underTest.visitPrimitiveAsFloat(primitiveType, true);
		assertThat(r2).isNotNull();
		assertThat(r2.orElse("error")).is("float");
	}

	@Test(fastFail = false)
	public void testVisitPrimitiveAsDouble() {
		Optional<String> r1 = underTest.visitPrimitiveAsDouble(primitiveType, false);
		assertThat(r1).isNotNull();
		assertThat(r1.orElse("error")).is("Double");

		Optional<String> r2 = underTest.visitPrimitiveAsDouble(primitiveType, true);
		assertThat(r2).isNotNull();
		assertThat(r2.orElse("error")).is("double");
	}

	@Test(fastFail = false)
	public void testVisitArray() {
		when(arrayType.getComponentType()).thenReturn(primitiveType);
		when(primitiveType.getKind()).thenReturn(TypeKind.BOOLEAN);
		when(primitiveType.accept(Mockito.any(), Mockito.any()))
				.thenAnswer(ip -> ip.getArgumentAt(0, TypeKindVisitor8.class).visitPrimitiveAsBoolean(primitiveType,
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
