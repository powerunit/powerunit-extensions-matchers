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
import ch.powerunit.extensions.matchers.api.AddToMatcher;
import ch.powerunit.extensions.matchers.provideprocessor.ProvidesMatchersAnnotatedElementData;
import ch.powerunit.extensions.matchers.provideprocessor.RoundMirror;

public class PrimitiveFieldDescriptionTest implements TestSuite {
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
		new PrimitiveFieldDescription(element, field);
	}

	@Test
	public void testGetImplementationInterface() {
		String result = new PrimitiveFieldDescription(element, field).getImplementationInterface();
		assertThat(result).isNotNull();
		assertThat(result).is(
				"@Override\npublic  MyNameMatcher fn(org.hamcrest.Matcher<? super X> matcher) {\n  fn= new FnMatcher(matcher);\n  return this;\n}\n\n\n");
	}

	@Test
	public void testGetDslInterface() {
		String result = new PrimitiveFieldDescription(element, field).getDslInterface();
		assertThat(result).is(
				"/**\n * Add a validation on the field `fn` .\n * <p>\n *\n * <i>{@link ch.pkg.MyName#fn This field is accessed by using this approach}.</i>\n * <p>\n * <b>In case method specifing a matcher on a fields are used several times, only the last setted matcher will be used.</b> \n * When several control must be done on a single field, hamcrest itself provides a way to combine several matchers.\n * (See for instance {@link org.hamcrest.Matchers#both(org.hamcrest.Matcher)}.\n *\n * @param matcher a Matcher on the field.\n * @return the DSL to continue the construction of the matcher.\n * @see org.hamcrest.Matchers The main class from hamcrest that provides default matchers.\n */\n MyNameMatcher fn(org.hamcrest.Matcher<? super X> matcher);\n\n/**\n * Add a validation on the field `fn` .\n * <p>\n *\n * <i>{@link ch.pkg.MyName#fn This field is accessed by using this approach}.</i>\n * <p>\n * <b>In case method specifing a matcher on a fields are used several times, only the last setted matcher will be used.</b> \n * When several control must be done on a single field, hamcrest itself provides a way to combine several matchers.\n * (See for instance {@link org.hamcrest.Matchers#both(org.hamcrest.Matcher)}.\n *\n * @param value an expected value for the field, which will be compared using the is matcher.\n * @return the DSL to continue the construction of the matcher.\n * @see org.hamcrest.Matchers#is(java.lang.Object)\n */\ndefault  MyNameMatcher fn(X value){\n  return fn((org.hamcrest.Matcher)org.hamcrest.Matchers.is((java.lang.Object)value));\n}\n/**\n * Add a validation on the field `fn` by converting the received field before validat it.\n * <p>\n *\n * <i>{@link ch.pkg.MyName#fn This field is accessed by using this approach}.</i>\n * <p>\n * <b>In case method specifing a matcher on a fields are used several times, only the last setted matcher will be used.</b> \n * When several control must be done on a single field, hamcrest itself provides a way to combine several matchers.\n * (See for instance {@link org.hamcrest.Matchers#both(org.hamcrest.Matcher)}.\n *\n * @param converter a function to convert the field.\n * @param matcher a matcher on the resulting.\n * @param <_TARGETFIELD> The type which this field must be converter.\n * @return the DSL to continue the construction of the matcher.\n */\ndefault <_TARGETFIELD> MyNameMatcher fnAs(java.util.function.Function<X,_TARGETFIELD> converter,org.hamcrest.Matcher<? super _TARGETFIELD> matcher){\n  return fn(asFeatureMatcher(\" <field is converted> \",converter,matcher));\n}");
	}

}
