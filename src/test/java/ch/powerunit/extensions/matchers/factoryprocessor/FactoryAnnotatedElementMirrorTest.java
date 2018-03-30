package ch.powerunit.extensions.matchers.factoryprocessor;

import static org.mockito.Mockito.when;

import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Name;
import javax.lang.model.element.PackageElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;

import org.mockito.Mock;

import ch.powerunit.Rule;
import ch.powerunit.Test;
import ch.powerunit.TestRule;
import ch.powerunit.TestSuite;

public class FactoryAnnotatedElementMirrorTest implements TestSuite {

	@Mock
	private FactoryAnnotationsProcessor factoryAnnotationsProcessor;

	@Mock
	private Elements elements;

	@Mock
	private Messager messageUtils;

	@Mock
	private ExecutableElement executableElement;

	@Mock
	private Element classElement;

	@Mock
	private TypeMirror classTypeMirror;

	@Mock
	private PackageElement packageElement;

	@Mock
	private Name className;

	@Mock
	private Name packageName;

	@Mock
	private Name elementName;

	@Mock
	private TypeMirror returnType;

	private void prepareMock() {
		when(executableElement.getEnclosingElement()).thenReturn(classElement);
		when(executableElement.getSimpleName()).thenReturn(elementName);
		when(executableElement.getReturnType()).thenReturn(returnType);

		when(classElement.asType()).thenReturn(classTypeMirror);
		when(classElement.getSimpleName()).thenReturn(className);

		when(classTypeMirror.toString()).thenReturn("fqn.sn");

		when(factoryAnnotationsProcessor.getElementUtils()).thenReturn(elements);
		when(factoryAnnotationsProcessor.getMessager()).thenReturn(messageUtils);

		when(elements.getPackageOf(classElement)).thenReturn(packageElement);

		when(packageElement.getQualifiedName()).thenReturn(packageName);

		when(packageName.toString()).thenReturn("fqn");
		when(className.toString()).thenReturn("sn");
		when(elementName.toString()).thenReturn("method");
	}

	@Rule
	public final TestRule rules = mockitoRule().around(before(this::prepareMock)).around(before(
			() -> underTest = new FactoryAnnotatedElementMirror(factoryAnnotationsProcessor, executableElement)));

	private FactoryAnnotatedElementMirror underTest;

	@Test
	public void testGenerateFactoryWithReturn() {
		when(returnType.getKind()).thenReturn(TypeKind.DECLARED);
		when(returnType.toString()).thenReturn("returnType");
		assertThat(underTest.generateFactory()).is(
				"  // method\n  /**\n   * No javadoc found from the source method.\n   * @see .sn#method()\n   */\n  default returnType method() {\n    return fqn.sn.method();\n  }\n\n");
	}

	@Test
	public void testGenerateFactoryWithoutReturn() {
		when(returnType.getKind()).thenReturn(TypeKind.VOID);
		when(returnType.toString()).thenReturn("void");
		assertThat(underTest.generateFactory()).is(
				"  // method\n  /**\n   * No javadoc found from the source method.\n   * @see .sn#method()\n   */\n  default void method() {\n    fqn.sn.method();\n  }\n\n");
	}

}
