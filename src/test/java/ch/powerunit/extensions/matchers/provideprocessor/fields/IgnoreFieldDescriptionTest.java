package ch.powerunit.extensions.matchers.provideprocessor.fields;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.util.Elements;

import org.mockito.Mock;
import org.mockito.Mockito;

import ch.powerunit.Rule;
import ch.powerunit.Test;
import ch.powerunit.TestRule;
import ch.powerunit.TestSuite;
import ch.powerunit.extensions.matchers.AddToMatcher;
import ch.powerunit.extensions.matchers.provideprocessor.ProvidesMatchersAnnotatedElementData;
import ch.powerunit.extensions.matchers.provideprocessor.RoundMirror;

public class IgnoreFieldDescriptionTest implements TestSuite {
	@Rule
	public final TestRule rules = mockitoRule().around(before(this::prepareMock));

	@Mock
	private RoundMirror round;

	@Mock
	private ProcessingEnvironment environment;

	@Mock
	private Elements elements;

	@Mock
	private ProvidesMatchersAnnotatedElementData element;

	@Mock
	private VariableElement fieldElement;

	@Mock
	private TypeElement elementType;

	@Mock
	private PrimitiveType type;

	private FieldDescriptionMirror field;

	private Name mockName(String name) {
		Name mock = mock(Name.class);
		doReturn(name).when(mock).toString();
		return mock;
	}

	private void prepareMock() {
		doReturn(round).when(element).getRoundMirror();
		doReturn(environment).when(round).getProcessingEnv();
		doReturn(elements).when(environment).getElementUtils();
		doReturn(elementType).when(elements).getTypeElement("X");
		doReturn(TypeKind.INT).when(type).getKind();
		doReturn(type).when(elementType).asType();
		doReturn(type).when(fieldElement).asType();
		doReturn(mockName("fn")).when(fieldElement).getSimpleName();
		doReturn(new AddToMatcher[0]).when(fieldElement).getAnnotationsByType(Mockito.any());
		doReturn("ch.pkg.MyName").when(element).getFullyQualifiedNameOfClassAnnotatedWithProvideMatcher();
		doReturn("ch.pkg.MyName").when(element).getFullyQualifiedNameOfClassAnnotatedWithProvideMatcherWithGeneric();
		doReturn("MyName").when(element).getSimpleNameOfClassAnnotatedWithProvideMatcher();
		doReturn("MyNameMatcher").when(element).getDefaultReturnMethod();

		field = new FieldDescriptionMirror(element, "fn", "X", fieldElement);
	}

	@Test
	public void testConstructor() {
		new IgnoreFieldDescription(element, field);
	}

	@Test
	public void testGetImplementationInterface() {
		String result = new IgnoreFieldDescription(element, field).getImplementationInterface();
		assertThat(result).isNotNull();
		assertThat(result).is("");
	}

	@Test
	public void testGetDslInterface() {
		String result = new IgnoreFieldDescription(element, field).getDslInterface();
		assertThat(result).is("");
	}

	@Test
	public void testAsMatcherField() {
		String result = new IgnoreFieldDescription(element, field).asMatcherField();
		assertThat(result).is(
				"private FnMatcher fn = new FnMatcher(org.hamcrest.Matchers.anything(\"This field is ignored \"+\"\"));");
	}

	@Test
	public void testGetFieldCopy() {
		String result = new IgnoreFieldDescription(element, field).getFieldCopy("lhs", "rhs","");
		assertThat(result).is("/* ignored - fn */");
	}

}
