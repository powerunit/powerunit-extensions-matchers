package ch.powerunit.extensions.matchers.provideprocessor;

import static org.mockito.Mockito.when;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Name;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;

import org.mockito.Mock;
import org.mockito.Mockito;

import ch.powerunit.Rule;
import ch.powerunit.Test;
import ch.powerunit.TestRule;
import ch.powerunit.extensions.matchers.TestSuiteSupport;
import ch.powerunit.extensions.matchers.api.ProvideMatchers;

public class ProvideMatchersMirrorTest implements TestSuiteSupport {

	@Mock
	private ProvideMatchers provideMatchers;

	@Mock
	private TypeElement typeElement;

	@Mock
	private Name qualifiedNameTypeElement;

	@Mock
	private TypeMirror typeMirror;

	@Mock
	private Name simpleNameTypeElement;

	@Mock
	private PackageElement packageElement;

	@Mock
	private Name qualifiedNamePackageElement;

	private ProcessingEnvironment processingEnv;

	private Elements elements;

	private RoundMirror roundMirror;

	private void prepare() {
		processingEnv = generateMockitoProcessingEnvironment();
		elements = processingEnv.getElementUtils();
		TypeMirror objectTypeMirror = elements.getTypeElement("java.lang.Object").asType();
		when(typeElement.getSuperclass()).thenReturn(objectTypeMirror);
		when(elements.getTypeElement(Mockito.anyString())).thenReturn(typeElement);
		when(typeElement.asType()).thenReturn(typeMirror);
		when(typeElement.getQualifiedName()).thenReturn(qualifiedNameTypeElement);
		when(qualifiedNameTypeElement.toString()).thenReturn("fqn.sn");
		when(typeElement.getSimpleName()).thenReturn(simpleNameTypeElement);
		when(simpleNameTypeElement.toString()).thenReturn("sn");
		when(elements.getPackageOf(typeElement)).thenReturn(packageElement);
		when(packageElement.getQualifiedName()).thenReturn(qualifiedNamePackageElement);
		when(qualifiedNamePackageElement.toString()).thenReturn("fqn");
		when(typeElement.getAnnotation(ProvideMatchers.class)).thenReturn(provideMatchers);
		roundMirror = new RoundMirror(generateMockitoRoundEnvironment(), processingEnv);
	}

	@Rule
	public final TestRule rules = mockitoRule().around(before(this::prepare));

	@Test
	public void testJustAnnotation() {
		when(provideMatchers.comments()).thenReturn("");
		when(provideMatchers.matchersClassName()).thenReturn("");
		when(provideMatchers.matchersPackageName()).thenReturn("");
		ProvideMatchersMirror mirror = new ProvideMatchersMirror(roundMirror, typeElement);
		assertThat(mirror.getFullyQualifiedNameOfGeneratedClass()).is("fqn.snMatchers");
		assertThat(mirror.getPackageNameOfGeneratedClass()).is("fqn");
		assertThat(mirror.getSimpleNameOfGeneratedClass()).is("snMatchers");
	}

	@Test
	public void testAnnotationName() {
		when(provideMatchers.comments()).thenReturn("");
		when(provideMatchers.matchersClassName()).thenReturn("Name");
		when(provideMatchers.matchersPackageName()).thenReturn("");
		ProvideMatchersMirror mirror = new ProvideMatchersMirror(roundMirror, typeElement);
		assertThat(mirror.getFullyQualifiedNameOfGeneratedClass()).is("fqn.Name");
		assertThat(mirror.getPackageNameOfGeneratedClass()).is("fqn");
		assertThat(mirror.getSimpleNameOfGeneratedClass()).is("Name");
	}

	@Test
	public void testAnnotationPackage() {
		when(provideMatchers.comments()).thenReturn("");
		when(provideMatchers.matchersClassName()).thenReturn("");
		when(provideMatchers.matchersPackageName()).thenReturn("package");
		ProvideMatchersMirror mirror = new ProvideMatchersMirror(roundMirror, typeElement);
		assertThat(mirror.getFullyQualifiedNameOfGeneratedClass()).is("package.snMatchers");
		assertThat(mirror.getPackageNameOfGeneratedClass()).is("package");
		assertThat(mirror.getSimpleNameOfGeneratedClass()).is("snMatchers");
	}

	@Test
	public void testJustAnnotationNameAndPackage() {
		when(provideMatchers.comments()).thenReturn("");
		when(provideMatchers.matchersClassName()).thenReturn("name");
		when(provideMatchers.matchersPackageName()).thenReturn("pck");
		ProvideMatchersMirror mirror = new ProvideMatchersMirror(roundMirror, typeElement);
		assertThat(mirror.getFullyQualifiedNameOfGeneratedClass()).is("pck.name");
		assertThat(mirror.getPackageNameOfGeneratedClass()).is("pck");
		assertThat(mirror.getSimpleNameOfGeneratedClass()).is("name");
	}
}
