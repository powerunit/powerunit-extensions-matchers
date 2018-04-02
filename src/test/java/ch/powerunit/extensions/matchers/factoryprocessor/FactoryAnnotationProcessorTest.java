package ch.powerunit.extensions.matchers.factoryprocessor;

import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ElementVisitor;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.Name;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.JavaFileObject;
import javax.tools.Diagnostic.Kind;

import org.hamcrest.Factory;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;

import ch.powerunit.Rule;
import ch.powerunit.Test;
import ch.powerunit.TestRule;
import ch.powerunit.TestSuite;

public class FactoryAnnotationProcessorTest implements TestSuite {

	@Mock
	private Elements elements;

	@Mock
	private Messager messageUtils;

	@Mock
	private Filer filer;

	@Mock
	private Types typeUtils;

	@Mock
	private ProcessingEnvironment processingEnv;

	@Mock
	private RoundEnvironment roundEnv;

	@Mock
	private TypeElement factoryTE;

	@Mock
	private JavaFileObject javaFileObject;

	@Mock
	private ExecutableElement executableElement;

	@Mock
	private Element classElement;

	@Mock
	private TypeMirror classTypeMirror;

	@Mock
	private Name className;

	@Mock
	private Name elementName;

	@Mock
	private TypeMirror returnType;

	@Mock
	private PackageElement packageElement;

	@Mock
	private Name packageName;

	@Spy
	private StringWriter outputStream = new StringWriter();

	private void prepareMock() {
		when(processingEnv.getMessager()).thenReturn(messageUtils);
		when(processingEnv.getFiler()).thenReturn(filer);
		when(processingEnv.getTypeUtils()).thenReturn(typeUtils);
		when(processingEnv.getElementUtils()).thenReturn(elements);
		when(elements.getTypeElement("org.hamcrest.Factory")).thenReturn(factoryTE);
		when(executableElement.getModifiers()).thenReturn(EnumSet.of(Modifier.STATIC, Modifier.PUBLIC));
		when(executableElement.getKind()).thenReturn(ElementKind.METHOD);
		when(executableElement.getEnclosingElement()).thenReturn(classElement);
		when(executableElement.getSimpleName()).thenReturn(elementName);
		when(executableElement.getReturnType()).thenReturn(returnType);
		when(executableElement.accept(Mockito.any(), Mockito.any()))
				.thenAnswer(ip -> ip.getArgumentAt(0, ElementVisitor.class).visitExecutable(executableElement,
						ip.getArgumentAt(1, Object.class)));
		when(classElement.asType()).thenReturn(classTypeMirror);
		when(classElement.getSimpleName()).thenReturn(className);
		when(elements.getPackageOf(classElement)).thenReturn(packageElement);
		when(packageElement.getQualifiedName()).thenReturn(packageName);

		when(classTypeMirror.toString()).thenReturn("fqn.sn");
		when(className.toString()).thenReturn("sn");
		when(elementName.toString()).thenReturn("method");
		when(returnType.getKind()).thenReturn(TypeKind.VOID);
		when(returnType.toString()).thenReturn("void");
		when(packageName.toString()).thenReturn("fqn");
		when(elementName.toString()).thenReturn("method");

	}

	@Rule
	public final TestRule rules = mockitoRule().around(before(this::prepareMock))
			.around(before(() -> underTest = new FactoryAnnotationsProcessor()));

	private FactoryAnnotationsProcessor underTest;

	@Test
	public void testHelperMethod() {
		when(processingEnv.getOptions()).thenReturn(Collections.emptyMap());
		underTest.init(processingEnv);

		assertThat(underTest.getElementUtils()).is(sameInstance(elements));
		assertThat(underTest.getFiler()).is(sameInstance(filer));
		assertThat(underTest.getTypeUtils()).is(sameInstance(typeUtils));
		assertThat(underTest.getMessager()).is(sameInstance(messageUtils));
		assertThat(underTest.getOptions().keySet()).is(empty());
	}

	@Test
	public void testInitNoTarget() {
		when(processingEnv.getOptions()).thenReturn(Collections.emptyMap());
		underTest.init(processingEnv);
		Mockito.verify(messageUtils).printMessage(Kind.MANDATORY_WARNING, "The parameter `"
				+ FactoryAnnotationsProcessor.class.getName() + ".targets` is missing, please use it.");
	}

	@Test
	public void testProcessNoTarget() {
		when(processingEnv.getOptions()).thenReturn(Collections.emptyMap());
		underTest.init(processingEnv);
		assertThat(underTest.process(Collections.emptySet(), roundEnv)).is(false);
	}

	@Test
	public void testProcessOneTargetNoAnnotatedElement() throws IOException {
		when(filer.createSourceFile(Mockito.eq("target"), Mockito.anyVararg())).thenReturn(javaFileObject);
		when(javaFileObject.openWriter()).thenReturn(outputStream);
		when(processingEnv.getOptions()).thenReturn(
				Collections.singletonMap(FactoryAnnotationsProcessor.class.getName() + ".targets", ".*:target"));
		underTest.init(processingEnv);
		when(roundEnv.processingOver()).thenReturn(false, true);
		// First round
		assertThat(underTest.process(Collections.emptySet(), roundEnv)).is(true);
		// Second round
		assertThat(underTest.process(Collections.emptySet(), roundEnv)).is(true);

		// Validate
		String target = outputStream.toString().replace('\r', '\n');
		assertThat(target).is(containsString("package target"));
		assertThat(target).is(containsString(
				"@javax.annotation.Generated(value=\"ch.powerunit.extensions.matchers.factoryprocessor.FactoryAnnotationsProcessor\","));
		assertThat(target).is(containsString("public interface target {"));
		assertThat(target).is(containsString("public static final target DSL = new target() {}"));
	}
 
	@Test
	public void testProcessOneTargetOneAnnotatedElement() throws IOException {
		when(filer.createSourceFile(Mockito.eq("target"), Mockito.anyVararg())).thenReturn(javaFileObject);
		when(javaFileObject.openWriter()).thenReturn(outputStream);
		when(processingEnv.getOptions()).thenReturn(
				Collections.singletonMap(FactoryAnnotationsProcessor.class.getName() + ".targets", ".*:target"));
		underTest.init(processingEnv);
		when(roundEnv.processingOver()).thenReturn(false, true);
		when(roundEnv.getElementsAnnotatedWith(Factory.class))
				.thenReturn((Set) Collections.singleton(executableElement));
		when(roundEnv.getRootElements()).thenReturn((Set) Collections.singleton(classElement));
		// First round
		assertThat(underTest.process(Collections.emptySet(), roundEnv)).is(true);
		// Second round
		assertThat(underTest.process(Collections.emptySet(), roundEnv)).is(true);

		// Validate
		String target = outputStream.toString().replace('\r', '\n');
		assertThat(target).is(containsString("package target"));
		assertThat(target).is(containsString(
				"@javax.annotation.Generated(value=\"ch.powerunit.extensions.matchers.factoryprocessor.FactoryAnnotationsProcessor\","));
		assertThat(target).is(containsString("public interface target {"));
		assertThat(target).is(containsString("public static final target DSL = new target() {}"));
	}
}
