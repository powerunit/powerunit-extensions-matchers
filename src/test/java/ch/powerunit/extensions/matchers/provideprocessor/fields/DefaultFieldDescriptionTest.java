package ch.powerunit.extensions.matchers.provideprocessor.fields;

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
import ch.powerunit.extensions.matchers.provideprocessor.ProvidesMatchersAnnotatedElementMirror;
import ch.powerunit.extensions.matchers.provideprocessor.RoundMirror;

public class DefaultFieldDescriptionTest implements TestSuite {
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
		when(provideMatchersAnnotatedElementMirror.getFullGeneric()).thenReturn("");
		when(provideMatchersAnnotatedElementMirror.getGeneric()).thenReturn("");
		when(provideMatchersAnnotatedElementMirror.getFullyQualifiedNameOfClassAnnotated()).thenReturn("fqn.sn");
		when(provideMatchersAnnotatedElementMirror.getFullyQualifiedNameOfClassAnnotatedWithProvideMatcherWithGeneric())
				.thenReturn("fqn.sn");
		when(provideMatchersAnnotatedElementMirror.getSimpleNameOfClassAnnotated()).thenReturn("sn");
		when(provideMatchersAnnotatedElementMirror.getMethodShortClassName()).thenReturn("sn");
		when(provideMatchersAnnotatedElementMirror.getDefaultReturnMethod()).thenReturn("snMatcher");
		when(processingEnv.getElementUtils()).thenReturn(elements);
		when(processingEnv.getTypeUtils()).thenReturn(types);
		when(primitiveType.getKind()).thenReturn(TypeKind.BOOLEAN);
		when(elements.getTypeElement("java.lang.String")).thenReturn(typeElement);
		when(typeElement.getSimpleName()).thenReturn(executableElementName);
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
		assertThatFunction(AbstractFieldDescription::computeGenericInformation, fieldTypeMirrorMainInterface).is("");
	}

	@Test
	public void testComputeGenericInformationIsDeclaredTypeThenJoinArgumentType() {
		when(fieldTypeMirror1.toString()).thenReturn("X");
		when(fieldTypeMirror2.toString()).thenReturn("Y");
		when(fieldTypeMirrorAsDeclaredType.getTypeArguments())
				.thenReturn((List) Arrays.asList(fieldTypeMirror1, fieldTypeMirror2));
		assertThatFunction(AbstractFieldDescription::computeGenericInformation, fieldTypeMirrorAsDeclaredType)
				.is("X,Y");
	}

	@Test
	public void testAsDescribeTo() {
		DefaultFieldDescription undertest = new DefaultFieldDescription(() -> provideMatchersAnnotatedElementMirror,
				new FieldDescriptionMirror(() -> provideMatchersAnnotatedElementMirror, "field", "boolean",
						executableElement));
		assertThat(undertest.asDescribeTo())
				.is("description.appendText(\"[\").appendDescriptionOf(field).appendText(\"]\\n\");");
	}

	@Test
	public void testAsMatchesSafely() {
		DefaultFieldDescription undertest = new DefaultFieldDescription(() -> provideMatchersAnnotatedElementMirror,
				new FieldDescriptionMirror(() -> provideMatchersAnnotatedElementMirror, "field", "boolean",
						executableElement));
		assertThat(undertest.asMatchesSafely()).is(
				"if(!field.matches(actual)) {\n  mismatchDescription.appendText(\"[\"); field.describeMismatch(actual,mismatchDescription); mismatchDescription.appendText(\"]\\n\");\n  result=false;\n}");
	}

	@Test
	public void testGetFieldCopyIgnore() {
		DefaultFieldDescription undertest = new DefaultFieldDescription(() -> provideMatchersAnnotatedElementMirror,
				new FieldDescriptionMirror(() -> provideMatchersAnnotatedElementMirror, "field", "boolean",
						executableElement));
		assertThat(undertest.getFieldCopy("a", "b", ",x"))
				.is("a.field((org.hamcrest.Matcher)org.hamcrest.Matchers.is((java.lang.Object)b.field()));");
	}

	@Test
	public void testGetMatcherForField() {
		DefaultFieldDescription undertest = new DefaultFieldDescription(() -> provideMatchersAnnotatedElementMirror,
				new FieldDescriptionMirror(() -> provideMatchersAnnotatedElementMirror, "field", "boolean",
						executableElement));
		assertThat(undertest.getMatcherForField()).is(
				"private static class FieldMatcher extends org.hamcrest.FeatureMatcher<fqn.sn,boolean> {\n  public FieldMatcher(org.hamcrest.Matcher<? super boolean> matcher) {\n    super(matcher,\"field\",\"field\");\n  }\n\n  protected boolean featureValueOf(fqn.sn actual) {\n    return actual.field();\n  }\n}\n");
	}

	@Test
	public void testAsMatcherField() {
		DefaultFieldDescription undertest = new DefaultFieldDescription(() -> provideMatchersAnnotatedElementMirror,
				new FieldDescriptionMirror(() -> provideMatchersAnnotatedElementMirror, "field", "boolean",
						executableElement));
		assertThat(undertest.asMatcherField())
				.is("private FieldMatcher field = new FieldMatcher(org.hamcrest.Matchers.anything());");
	}

	@Test
	public void testGetDslInterface() {
		DefaultFieldDescription undertest = new DefaultFieldDescription(() -> provideMatchersAnnotatedElementMirror,
				new FieldDescriptionMirror(() -> provideMatchersAnnotatedElementMirror, "field", "boolean",
						executableElement));
		assertThat(undertest.getDslInterface()).is(
				"/**\n * Add a validation on the field `field` .\n * <p>\n *\n * <i>{@link fqn.sn#field() This field is accessed by using this approach}.</i>\n * <p>\n * <b>In case method specifing a matcher on a fields are used several times, only the last setted matcher will be used.</b> \n * When several control must be done on a single field, hamcrest itself provides a way to combine several matchers.\n * (See for instance {@link org.hamcrest.Matchers#both(org.hamcrest.Matcher)}.\n *\n * @param matcher a Matcher on the field.\n * @return the DSL to continue the construction of the matcher.\n * @see org.hamcrest.Matchers The main class from hamcrest that provides default matchers.\n */\n snMatcher field(org.hamcrest.Matcher<? super boolean> matcher);\n\n/**\n * Add a validation on the field `field` .\n * <p>\n *\n * <i>{@link fqn.sn#field() This field is accessed by using this approach}.</i>\n * <p>\n * <b>In case method specifing a matcher on a fields are used several times, only the last setted matcher will be used.</b> \n * When several control must be done on a single field, hamcrest itself provides a way to combine several matchers.\n * (See for instance {@link org.hamcrest.Matchers#both(org.hamcrest.Matcher)}.\n *\n * @param value an expected value for the field, which will be compared using the is matcher.\n * @return the DSL to continue the construction of the matcher.\n * @see org.hamcrest.Matchers#is(java.lang.Object)\n */\ndefault  snMatcher field(boolean value){\n  return field((org.hamcrest.Matcher)org.hamcrest.Matchers.is((java.lang.Object)value));\n}\n/**\n * Add a validation on the field `field` by converting the received field before validat it.\n * <p>\n *\n * <i>{@link fqn.sn#field() This field is accessed by using this approach}.</i>\n * <p>\n * <b>In case method specifing a matcher on a fields are used several times, only the last setted matcher will be used.</b> \n * When several control must be done on a single field, hamcrest itself provides a way to combine several matchers.\n * (See for instance {@link org.hamcrest.Matchers#both(org.hamcrest.Matcher)}.\n *\n * @param converter a function to convert the field.\n * @param matcher a matcher on the resulting.\n * @param <_TARGETFIELD> The type which this field must be converter.\n * @return the DSL to continue the construction of the matcher.\n */\ndefault <_TARGETFIELD> snMatcher fieldAs(java.util.function.Function<boolean,_TARGETFIELD> converter,org.hamcrest.Matcher<? super _TARGETFIELD> matcher){\n  return field(asFeatureMatcher(\" <field is converted> \",converter,matcher));\n}\n/**\n * Add a validation on the field `field` .\n * <p>\n *\n * <i>{@link fqn.sn#field() This field is accessed by using this approach}.</i>\n * <p>\n * <b>In case method specifing a matcher on a fields are used several times, only the last setted matcher will be used.</b> \n * When several control must be done on a single field, hamcrest itself provides a way to combine several matchers.\n * (See for instance {@link org.hamcrest.Matchers#both(org.hamcrest.Matcher)}.\n *\n * @param value an expected value for the field, which will be compared that it is the same instance..\n * @return the DSL to continue the construction of the matcher.\n * @see org.hamcrest.Matchers#is(java.lang.Object)\n */\ndefault  snMatcher fieldIsSameInstance(boolean value){\n  return field((org.hamcrest.Matcher)org.hamcrest.Matchers.sameInstance((java.lang.Object)value));\n}");
	}

	@Test
	public void testGetImplementationInterface() {
		DefaultFieldDescription undertest = new DefaultFieldDescription(() -> provideMatchersAnnotatedElementMirror,
				new FieldDescriptionMirror(() -> provideMatchersAnnotatedElementMirror, "field", "boolean",
						executableElement));
		assertThat(undertest.getImplementationInterface()).is(
				"@Override\npublic  snMatcher field(org.hamcrest.Matcher<? super boolean> matcher) {\n  field= new FieldMatcher(matcher);\n  return this;\n}\n\n\n\n");
	}

}
