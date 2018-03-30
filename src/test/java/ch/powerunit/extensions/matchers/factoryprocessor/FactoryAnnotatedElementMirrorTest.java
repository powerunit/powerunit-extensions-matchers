package ch.powerunit.extensions.matchers.factoryprocessor;

import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Name;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import org.mockito.Mock;
import org.mockito.Mockito;

import ch.powerunit.Parameter;
import ch.powerunit.Parameters;
import ch.powerunit.Rule;
import ch.powerunit.Test;
import ch.powerunit.TestRule;
import ch.powerunit.TestSuite;

public class FactoryAnnotatedElementMirrorTest implements TestSuite {

	@Parameters("Test with return void = %0$s and with param = %1$s")
	public static Stream<Object[]> getParameters() {
		return Arrays.stream(new Object[][] {
				{ true, false, false,
						"  // method\n  /**\n   * No javadoc found from the source method.\n   * @see .sn#method()\n   */\n  default void method() {\n    fqn.sn.method();\n  }\n\n" },
				{ false, false, false,
						"  // method\n  /**\n   * No javadoc found from the source method.\n   * @see .sn#method()\n   */\n  default returnType method() {\n    return fqn.sn.method();\n  }\n\n" },
				{ false, true, false,
						"  // method\n  /**\n   * No javadoc found from the source method.\n   * @see .sn#method(type)\n   */\n  default returnType method(type param) {\n    return fqn.sn.method(param);\n  }\n\n" },
				{ true, true, false,
						"  // method\n  /**\n   * No javadoc found from the source method.\n   * @see .sn#method(type)\n   */\n  default void method(type param) {\n    fqn.sn.method(param);\n  }\n\n" },
				{ false, true, true,
						"  // method\n  /**\n   * No javadoc found from the source method.\n   * @see .sn#method(type...)\n   */\n  default returnType method(type... param) {\n    return fqn.sn.method(param);\n  }\n\n" },
				{ true, true, true,
						"  // method\n  /**\n   * No javadoc found from the source method.\n   * @see .sn#method(type...)\n   */\n  default void method(type... param) {\n    fqn.sn.method(param);\n  }\n\n" } });
	}

	@Parameter(0)
	public boolean returnVoid;

	@Parameter(1)
	public boolean withParam;

	@Parameter(2)
	public boolean withVarArgs;

	@Parameter(3)
	public String expectingResult;

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

	@Mock
	private VariableElement variableElement;

	@Mock
	private TypeMirror typeParameterType;

	@Mock
	private Name typeParameterName;

	@Mock
	private Types types;

	private void prepareMock() {
		when(executableElement.getEnclosingElement()).thenReturn(classElement);
		when(executableElement.getSimpleName()).thenReturn(elementName);
		when(executableElement.getReturnType()).thenReturn(returnType);

		when(classElement.asType()).thenReturn(classTypeMirror);
		when(classElement.getSimpleName()).thenReturn(className);

		when(classTypeMirror.toString()).thenReturn("fqn.sn");

		when(factoryAnnotationsProcessor.getElementUtils()).thenReturn(elements);
		when(factoryAnnotationsProcessor.getTypeUtils()).thenReturn(types);
		when(factoryAnnotationsProcessor.getMessager()).thenReturn(messageUtils);

		when(elements.getPackageOf(classElement)).thenReturn(packageElement);

		when(types.erasure(Mockito.any(TypeMirror.class))).thenAnswer(invocation -> invocation.getArguments()[0]);

		when(packageElement.getQualifiedName()).thenReturn(packageName);

		when(variableElement.asType()).thenReturn(typeParameterType);
		when(variableElement.getSimpleName()).thenReturn(typeParameterName);

		when(packageName.toString()).thenReturn("fqn");
		when(className.toString()).thenReturn("sn");
		when(elementName.toString()).thenReturn("method");
		when(typeParameterName.toString()).thenReturn("param");

	}

	private void useparam() {
		if (returnVoid) {
			when(returnType.getKind()).thenReturn(TypeKind.VOID);
			when(returnType.toString()).thenReturn("void");
		} else {
			when(returnType.getKind()).thenReturn(TypeKind.DECLARED);
			when(returnType.toString()).thenReturn("returnType");
		}
		if (withParam) {
			when(executableElement.getParameters()).thenReturn((List) Collections.singletonList(variableElement));
			if (withVarArgs) {
				when(executableElement.isVarArgs()).thenReturn(true);
				when(typeParameterType.toString()).thenReturn("type[]");
			} else {
				when(executableElement.isVarArgs()).thenReturn(false);
				when(typeParameterType.toString()).thenReturn("type");
			}
		} else {
			when(executableElement.getParameters()).thenReturn(Collections.emptyList());
		}
	}

	@Rule
	public final TestRule rules = mockitoRule().around(before(this::prepareMock)).around(before(this::useparam))
			.around(before(() -> underTest = new FactoryAnnotatedElementMirror(factoryAnnotationsProcessor,
					executableElement)));

	private FactoryAnnotatedElementMirror underTest;

	@Test
	public void testGenerateFactory() {
		assertThat(underTest.generateFactory()).is(expectingResult);
	}

}
