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
		when(roundMirror.isInSameRound(Mockito.any())).thenReturn(false);
		when(provideMatchersAnnotatedElementMirror.getFullGeneric()).thenReturn("");
		when(provideMatchersAnnotatedElementMirror.getGeneric()).thenReturn("");
		when(provideMatchersAnnotatedElementMirror.getFullyQualifiedNameOfClassAnnotatedWithProvideMatcher())
				.thenReturn("fqn.sn");
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
	public void testGetFieldCopy() {
		DefaultFieldDescription undertest = new DefaultFieldDescription(() -> provideMatchersAnnotatedElementMirror,
				new FieldDescriptionMirror(() -> provideMatchersAnnotatedElementMirror, "field", "boolean",
						executableElement));
		assertThat(undertest.getFieldCopy("a", "b")).is("a.field((org.hamcrest.Matcher)org.hamcrest.Matchers.is((java.lang.Object)b.field()))");
	}

	@Test
	public void testGetMatcherForField() {
		DefaultFieldDescription undertest = new DefaultFieldDescription(() -> provideMatchersAnnotatedElementMirror,
				new FieldDescriptionMirror(() -> provideMatchersAnnotatedElementMirror, "field", "boolean",
						executableElement));
		assertThat(undertest.getMatcherForField()).is(
				"private static class FieldMatcher extends org.hamcrest.FeatureMatcher<fqn.sn,boolean> {\n  public FieldMatcher(org.hamcrest.Matcher<? super boolean> matcher) {\n    super(matcher,\"field\",\"field\");\n  }\n  protected boolean featureValueOf(fqn.sn actual) {\n    return actual.field();\n  }\n}\n");
	}

	@Test
	public void testAsMatcherField() {
		DefaultFieldDescription undertest = new DefaultFieldDescription(() -> provideMatchersAnnotatedElementMirror,
				new FieldDescriptionMirror(() -> provideMatchersAnnotatedElementMirror, "field", "boolean",
						executableElement));
		assertThat(undertest.asMatcherField())
				.is("private FieldMatcher field = new FieldMatcher(org.hamcrest.Matchers.anything());");
	}

}
