package ch.powerunit.extensions.matchers.provideprocessor;

import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVisitor;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import ch.powerunit.Rule;
import ch.powerunit.Test;
import ch.powerunit.TestRule;
import ch.powerunit.TestSuite;
import ch.powerunit.extensions.matchers.AddToMatcher;

public class FieldDescriptionTest implements TestSuite {
	@Rule
	public final TestRule rules = mockitoRule().around(before(this::prepareMock));

	@Mock
	private TypeMirror fieldTypeMirrorMainInterface;

	@Mock
	private TypeMirror fieldTypeMirror1;

	@Mock
	private TypeMirror fieldTypeMirror2;

	@Mock
	private DeclaredType fieldTypeMirrorAsDeclaredType;

	@Mock
	private PrimitiveType primitiveType;

	@Mock
	private ExecutableElement executableElement;

	@Mock
	private Name executableElementName;

	@Mock
	private ProvidesMatchersAnnotatedElementMirror provideMatchersAnnotatedElementMirror;

	@Mock
	private RoundMirror roundMirror;

	@Mock
	private ProcessingEnvironment processingEnv;

	@Mock
	private Elements elements;

	@Mock
	private TypeElement typeElement;

	@Mock
	private Types types;

	private void prepareMock() {
		when(provideMatchersAnnotatedElementMirror.getRoundMirror()).thenReturn(roundMirror);
		when(roundMirror.getProcessingEnv()).thenReturn(processingEnv);
		when(roundMirror.isInSameRound(Mockito.any())).thenReturn(false);
		when(provideMatchersAnnotatedElementMirror.getProcessingEnv()).thenReturn(processingEnv);
		when(provideMatchersAnnotatedElementMirror.getFullGeneric()).thenReturn("");
		when(provideMatchersAnnotatedElementMirror.getGeneric()).thenReturn("");
		when(provideMatchersAnnotatedElementMirror.getFullyQualifiedNameOfClassAnnotatedWithProvideMatcher())
				.thenReturn("fqn.sn");
		when(processingEnv.getElementUtils()).thenReturn(elements);
		when(processingEnv.getTypeUtils()).thenReturn(types);
		when(primitiveType.getKind()).thenReturn(TypeKind.BOOLEAN);
		when(executableElement.getSimpleName()).thenReturn(executableElementName);
		when(executableElementName.toString()).thenReturn("field");
		when(executableElement.getReturnType()).thenReturn(primitiveType);
		when(executableElement.getAnnotationsByType(Mockito.eq(AddToMatcher.class))).thenReturn(new AddToMatcher[] {});
		when(primitiveType.accept(Mockito.any(TypeVisitor.class), Mockito.any(Object.class)))
				.then(new Answer<Object>() {

					@Override
					public Object answer(InvocationOnMock invocation) throws Throwable {
						return invocation.getArgumentAt(0, TypeVisitor.class).visitPrimitive(primitiveType,
								invocation.getArgumentAt(1, Object.class));
					}
				});
	}

	@Test
	public void testComputeGenericInformationIsNotDeclaredTypeThenEmptyString() {
		assertThatFunction(FieldDescription::computeGenericInformation, fieldTypeMirrorMainInterface).is("");
	}

	@Test
	public void testComputeGenericInformationIsDeclaredTypeThenJoinArgumentType() {
		when(fieldTypeMirror1.toString()).thenReturn("X");
		when(fieldTypeMirror2.toString()).thenReturn("Y");
		when(fieldTypeMirrorAsDeclaredType.getTypeArguments())
				.thenReturn((List) Arrays.asList(fieldTypeMirror1, fieldTypeMirror2));
		assertThatFunction(FieldDescription::computeGenericInformation, fieldTypeMirrorAsDeclaredType).is("X,Y");
	}

	@Test
	public void testAsDescribeTo() {
		FieldDescription undertest = new FieldDescription(() -> provideMatchersAnnotatedElementMirror, "field",
				"boolean", executableElement);
		assertThat(undertest.asDescribeTo())
				.is("description.appendText(\"[\").appendDescriptionOf(field).appendText(\"]\\n\");");
	}

	@Test
	public void testAsMatchesSafely() {
		FieldDescription undertest = new FieldDescription(() -> provideMatchersAnnotatedElementMirror, "field",
				"boolean", executableElement);
		assertThat(undertest.asMatchesSafely()).is(
				"if(!field.matches(actual)) {\n  mismatchDescription.appendText(\"[\"); field.describeMismatch(actual,mismatchDescription); mismatchDescription.appendText(\"]\\n\");\n  result=false;\n}");
	}

	@Test
	public void testGetDslForDefault() {
		FieldDescription undertest = new FieldDescription(() -> provideMatchersAnnotatedElementMirror, "field",
				"boolean", executableElement);
		assertThat(undertest.getDslForDefault()).is(
				"/**\n * Add a validation on the field `field`.\n * <p>\n *\n * <i>{@link fqn.sn#field() This field is accessed by using this approach}.</i>\n * <p>\n * <b>In case method specifing a matcher on a fields are used several times, only the last setted matcher will be used.</b> \n * When several control must be done on a single field, hamcrest itself provides a way to combine several matchers (See for instance {@link org.hamcrest.Matchers#both(org.hamcrest.Matcher)}.\n *\n * @param matcher a Matcher on the field.\n * @return the DSL to continue the construction of the matcher.\n * @see org.hamcrest.Matchers The main class from hamcrest that provides default matchers.\n */\nnull field(org.hamcrest.Matcher<? super boolean> matcher);\n\n/**\n * Add a validation on the field `field`.\n * <p>\n *\n * <i>{@link fqn.sn#field() This field is accessed by using this approach}.</i>\n * <p>\n * <b>In case method specifing a matcher on a fields are used several times, only the last setted matcher will be used.</b> \n * When several control must be done on a single field, hamcrest itself provides a way to combine several matchers (See for instance {@link org.hamcrest.Matchers#both(org.hamcrest.Matcher)}.\n *\n * @param value an expected value for the field, which will be compared using the is matcher.\n * @return the DSL to continue the construction of the matcher.\n * @see org.hamcrest.Matchers#is(java.lang.Object)\n */\ndefault null field(boolean value){\n  return field(org.hamcrest.Matchers.is(value));\n}\n/**\n * Add a validation on the field `field` by converting the received field before validat it.\n * <p>\n *\n * <i>{@link fqn.sn#field() This field is accessed by using this approach}.</i>\n * <p>\n * <b>In case method specifing a matcher on a fields are used several times, only the last setted matcher will be used.</b> \n * When several control must be done on a single field, hamcrest itself provides a way to combine several matchers (See for instance {@link org.hamcrest.Matchers#both(org.hamcrest.Matcher)}.\n *\n * @param converter a function to convert the field.\n * @param matcher a matcher on the resulting.\n * @param <_TARGETFIELD> The type which this field must be converter.\n * @return the DSL to continue the construction of the matcher.\n */\ndefault <_TARGETFIELD> null fieldAs(java.util.function.Function<boolean,_TARGETFIELD> converter,org.hamcrest.Matcher<? super _TARGETFIELD> matcher){\n  return field(asFeatureMatcher(\" <field is converted> \",converter,matcher));\n}\n");
	}

	@Test
	public void testGetDslForString() {
		FieldDescription undertest = new FieldDescription(() -> provideMatchersAnnotatedElementMirror, "field",
				"java.lang.String", executableElement);
		assertThat(undertest.getDslForString()).is(
				"/**\n * Add a validation on the field `field` that the string contains another one.\n * <p>\n *\n * <i>{@link fqn.sn#field() This field is accessed by using this approach}.</i>\n * <p>\n * <b>In case method specifing a matcher on a fields are used several times, only the last setted matcher will be used.</b> \n * When several control must be done on a single field, hamcrest itself provides a way to combine several matchers (See for instance {@link org.hamcrest.Matchers#both(org.hamcrest.Matcher)}.\n *\n * @param other the string is contains in the other one.\n * @return the DSL to continue the construction of the matcher.\n * @see org.hamcrest.Matchers#containsString(java.lang.String)\n */\ndefault null fieldContainsString(String other){\n  return field(org.hamcrest.Matchers.containsString(other));\n}/**\n * Add a validation on the field `field` that the string starts with another one.\n * <p>\n *\n * <i>{@link fqn.sn#field() This field is accessed by using this approach}.</i>\n * <p>\n * <b>In case method specifing a matcher on a fields are used several times, only the last setted matcher will be used.</b> \n * When several control must be done on a single field, hamcrest itself provides a way to combine several matchers (See for instance {@link org.hamcrest.Matchers#both(org.hamcrest.Matcher)}.\n *\n * @param other the string to use to compare.\n * @return the DSL to continue the construction of the matcher.\n * @see org.hamcrest.Matchers#startsWith(java.lang.String)\n */\ndefault null fieldStartsWith(String other){\n  return field(org.hamcrest.Matchers.startsWith(other));\n}/**\n * Add a validation on the field `field` that the string ends with another one.\n * <p>\n *\n * <i>{@link fqn.sn#field() This field is accessed by using this approach}.</i>\n * <p>\n * <b>In case method specifing a matcher on a fields are used several times, only the last setted matcher will be used.</b> \n * When several control must be done on a single field, hamcrest itself provides a way to combine several matchers (See for instance {@link org.hamcrest.Matchers#both(org.hamcrest.Matcher)}.\n *\n * @param other the string to use to compare.\n * @return the DSL to continue the construction of the matcher.\n * @see org.hamcrest.Matchers#endsWith(java.lang.String)\n */\ndefault null fieldEndsWith(String other){\n  return field(org.hamcrest.Matchers.endsWith(other));\n}");
	}

	@Test
	public void testGetFieldCopy() {
		FieldDescription undertest = new FieldDescription(() -> provideMatchersAnnotatedElementMirror, "field",
				"java.lang.String", executableElement);
		assertThat(undertest.getFieldCopy("a", "b")).is("a.field(org.hamcrest.Matchers.is(b.field()))");
	}

	@Test
	public void testGetMatcherForField() {
		FieldDescription undertest = new FieldDescription(() -> provideMatchersAnnotatedElementMirror, "field",
				"boolean", executableElement);
		assertThat(undertest.getMatcherForField()).is(
				"private static class FieldMatcher extends org.hamcrest.FeatureMatcher<fqn.sn,boolean> {\n  public FieldMatcher(org.hamcrest.Matcher<? super boolean> matcher) {\n    super(matcher,\"field\",\"field\");\n  }\n  protected boolean featureValueOf(fqn.sn actual) {\n    return actual.field();\n  }\n}\n");
	}

	@Test
	public void testComputeFullyQualifiedNameMatcherInSameRoundFalseThenNull() {
		assertThat(FieldDescription.computeFullyQualifiedNameMatcherInSameRound(roundMirror, executableElement,
				typeElement)).isNull();
	}

	@Test
	public void testComputeFullyQualifiedNameMatcherInSameRoundTrueNullThenNull() {
		when(roundMirror.isInSameRound(Mockito.any())).thenReturn(true);
		assertThat(FieldDescription.computeFullyQualifiedNameMatcherInSameRound(roundMirror, executableElement, null))
				.isNull();
	}

	@Test
	public void testBuildImplementationOneLine() {
		assertThatBiFunction(FieldDescription::buildImplementation, "name", "ligne1")
				.is("@Override\npublic name {\n  ligne1\n}\n");
	}

	@Test
	public void testBuildImplementationTwoLine() {
		assertThatBiFunction(FieldDescription::buildImplementation, "name", "ligne1\nligne2")
				.is("@Override\npublic name {\n  ligne1\n  ligne2\n}\n");
	}

	@Test
	public void testBuildDsl() {
		assertThatBiFunction(FieldDescription::buildDsl, "javadoc", "declaration").is("javadoc\ndeclaration;\n");
	}

	@Test
	public void testBuildDefaultDsl() {
		FieldDescription undertest = new FieldDescription(() -> provideMatchersAnnotatedElementMirror, "field",
				"boolean", executableElement);
		assertThat(undertest.buildDefaultDsl("javadoc", "declaration", "inner"))
				.is("javadoc\ndefault declaration{\n  return field(inner);\n}");
	}
}
