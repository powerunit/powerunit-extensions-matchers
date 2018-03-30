package ch.powerunit.extensions.matchers.provideprocessor;

import static org.mockito.Mockito.when;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.Name;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import org.mockito.Mock;
import org.mockito.Mockito;

import ch.powerunit.Rule;
import ch.powerunit.Test;
import ch.powerunit.TestRule;
import ch.powerunit.TestSuite;
import ch.powerunit.extensions.matchers.ProvideMatchers;

public class ProvidesMatchersAnnotatedElementMirrorTest implements TestSuite {
	@Rule
	public final TestRule rules = mockitoRule().around(before(this::prepareMock));

	@Mock
	private TypeElement typeElement;

	@Mock
	private TypeElement object;

	@Mock
	private TypeMirror objectMirror;

	@Mock
	private Name fullyQualifiedName;

	@Mock
	private Name simpleName;

	@Mock
	private Name packageName;

	@Mock
	private PackageElement packageElement;

	@Mock
	private ProcessingEnvironment processingEnv;

	@Mock
	private Elements elements;

	@Mock
	private Types types;

	@Mock
	private ProvideMatchers provideMatcher;

	private void prepareMock() {
		when(provideMatcher.matchersClassName()).thenReturn("");
		when(provideMatcher.matchersPackageName()).thenReturn("");
		when(provideMatcher.comments()).thenReturn("");

		when(processingEnv.getElementUtils()).thenReturn(elements);
		when(processingEnv.getTypeUtils()).thenReturn(types);

		when(elements.getPackageOf(Mockito.any(Element.class))).thenReturn(packageElement);
		when(elements.getTypeElement("java.lang.Object")).thenReturn(object);
		when(types.asElement(objectMirror)).thenReturn(object);

		when(typeElement.getQualifiedName()).thenReturn(fullyQualifiedName);
		when(typeElement.getSimpleName()).thenReturn(simpleName);
		when(typeElement.getAnnotation(ProvideMatchers.class)).thenReturn(provideMatcher);
		when(typeElement.getSuperclass()).thenReturn(objectMirror);

		when(fullyQualifiedName.toString()).thenReturn("fqn.Sn");
		when(packageElement.getQualifiedName()).thenReturn(packageName);
		when(packageName.toString()).thenReturn("fnq");
		when(simpleName.toString()).thenReturn("Sn");
		when(object.asType()).thenReturn(objectMirror);
	}

	@Test
	public void testGenerateAndExtractFieldAndParentPrivateMatcherWithoutField() {
		ProvidesMatchersAnnotatedElementMirror underTest = new ProvidesMatchersAnnotatedElementMirror(typeElement,
				processingEnv, a -> false, a -> null);
		assertThat(underTest.generateAndExtractFieldAndParentPrivateMatcher()).is(
				"\n  private static <_TARGET,_SOURCE> org.hamcrest.Matcher<_SOURCE> asFeatureMatcher(String msg,java.util.function.Function<_SOURCE,_TARGET> converter,org.hamcrest.Matcher<? super _TARGET> matcher) {\n   return new org.hamcrest.FeatureMatcher<_SOURCE,_TARGET>(matcher, msg, msg) {\n     protected _TARGET featureValueOf(_SOURCE actual) {\n      return converter.apply(actual);\n    }};\n  }\n\n\n");
	}

}
