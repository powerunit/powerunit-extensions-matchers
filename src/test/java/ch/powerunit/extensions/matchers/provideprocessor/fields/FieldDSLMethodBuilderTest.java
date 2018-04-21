package ch.powerunit.extensions.matchers.provideprocessor.fields;

import static org.mockito.Mockito.when;

import java.util.Optional;

import org.mockito.Mock;

import ch.powerunit.Rule;
import ch.powerunit.Test;
import ch.powerunit.TestRule;
import ch.powerunit.TestSuite;

public class FieldDSLMethodBuilderTest implements TestSuite {
	@Rule
	public final TestRule rules = mockitoRule().around(before(this::prepareMock));

	@Mock
	private AbstractFieldDescription fieldDescription;

	private void prepareMock() {
		when(fieldDescription.getFieldName()).thenReturn("fn");
		when(fieldDescription.getFieldAccessor()).thenReturn("fa");
		when(fieldDescription.getFullyQualifiedNameEnclosingClassOfField()).thenReturn("fqnecof");
		when(fieldDescription.getFieldType()).thenReturn("ft");
		when(fieldDescription.getDefaultReturnMethod()).thenReturn("drm");
	}

	@Test
	public void testBuildImplementationOneLine() {
		assertThatBiFunction(FieldDSLMethodBuilder::buildImplementation, "name", "ligne1")
				.is("@Override\npublic name {\n  ligne1\n}\n");
	}

	@Test
	public void testBuildImplementationTwoLine() {
		assertThatBiFunction(FieldDSLMethodBuilder::buildImplementation, "name", "ligne1\nligne2")
				.is("@Override\npublic name {\n  ligne1\n  ligne2\n}\n");
	}

	@Test
	public void testBuildDsl() {
		assertThatBiFunction(FieldDSLMethodBuilder::buildDsl, "javadoc", "declaration").is("javadoc\ndeclaration;\n");
	}

	@Test(fastFail = false)
	public void testGetJavaDocFor() {
		assertThat(FieldDSLMethodBuilder.getJavaDocFor(fieldDescription, Optional.empty(), Optional.empty(),
				Optional.empty())).is(
						"/**\n * Add a validation on the field `fn`.\n * <p>\n *\n * <i>{@link fqnecof#fa This field is accessed by using this approach}.</i>\n * <p>\n * <b>In case method specifing a matcher on a fields are used several times, only the last setted matcher will be used.</b> \n * When several control must be done on a single field, hamcrest itself provides a way to combine several matchers (See for instance {@link org.hamcrest.Matchers#both(org.hamcrest.Matcher)}.\n *\n * @return the DSL to continue the construction of the matcher.\n */");

		assertThat(FieldDSLMethodBuilder.getJavaDocFor(fieldDescription, Optional.of("add"), Optional.empty(),
				Optional.empty())).is(
						"/**\n * Add a validation on the field `fn` add.\n * <p>\n *\n * <i>{@link fqnecof#fa This field is accessed by using this approach}.</i>\n * <p>\n * <b>In case method specifing a matcher on a fields are used several times, only the last setted matcher will be used.</b> \n * When several control must be done on a single field, hamcrest itself provides a way to combine several matchers (See for instance {@link org.hamcrest.Matchers#both(org.hamcrest.Matcher)}.\n *\n * @return the DSL to continue the construction of the matcher.\n */");

		assertThat(FieldDSLMethodBuilder.getJavaDocFor(fieldDescription, Optional.empty(), Optional.of("p is p"),
				Optional.empty())).is(
						"/**\n * Add a validation on the field `fn`.\n * <p>\n *\n * <i>{@link fqnecof#fa This field is accessed by using this approach}.</i>\n * <p>\n * <b>In case method specifing a matcher on a fields are used several times, only the last setted matcher will be used.</b> \n * When several control must be done on a single field, hamcrest itself provides a way to combine several matchers (See for instance {@link org.hamcrest.Matchers#both(org.hamcrest.Matcher)}.\n *\n * @param p is p.\n * @return the DSL to continue the construction of the matcher.\n */");

		assertThat(FieldDSLMethodBuilder.getJavaDocFor(fieldDescription, Optional.empty(), Optional.empty(),
				Optional.of("other"))).is(
						"/**\n * Add a validation on the field `fn`.\n * <p>\n *\n * <i>{@link fqnecof#fa This field is accessed by using this approach}.</i>\n * <p>\n * <b>In case method specifing a matcher on a fields are used several times, only the last setted matcher will be used.</b> \n * When several control must be done on a single field, hamcrest itself provides a way to combine several matchers (See for instance {@link org.hamcrest.Matchers#both(org.hamcrest.Matcher)}.\n *\n * @return the DSL to continue the construction of the matcher.\n * @see other\n */");
	}

	@Test(fastFail = false)
	public void testDefaultImplementationV1() {
		FieldDSLMethod fieldDSLMethod = FieldDSLMethodBuilder.of(fieldDescription).withDeclaration("a a")
				.withDefaultJavaDoc().havingImplementation("b");
		assertThat(fieldDSLMethod.asDSLMethod()).is(
				"/**\n * Add a validation on the field `fn`.\n * <p>\n *\n * <i>{@link fqnecof#fa This field is accessed by using this approach}.</i>\n * <p>\n * <b>In case method specifing a matcher on a fields are used several times, only the last setted matcher will be used.</b> \n * When several control must be done on a single field, hamcrest itself provides a way to combine several matchers (See for instance {@link org.hamcrest.Matchers#both(org.hamcrest.Matcher)}.\n *\n * @return the DSL to continue the construction of the matcher.\n */\n drm fn(a a);\n");
		assertThat(fieldDSLMethod.asImplementationMethod()).is("@Override\npublic  drm fn(a a) {\n  b\n}\n");
	}

	@Test(fastFail = false)
	public void testDefaultDSLV1() {
		FieldDSLMethod fieldDSLMethod = FieldDSLMethodBuilder.of(fieldDescription).withDeclaration("a a")
				.withDefaultJavaDoc().havingDefault("m(a)");
		assertThat(fieldDSLMethod.asDSLMethod()).is(
				"/**\n * Add a validation on the field `fn`.\n * <p>\n *\n * <i>{@link fqnecof#fa This field is accessed by using this approach}.</i>\n * <p>\n * <b>In case method specifing a matcher on a fields are used several times, only the last setted matcher will be used.</b> \n * When several control must be done on a single field, hamcrest itself provides a way to combine several matchers (See for instance {@link org.hamcrest.Matchers#both(org.hamcrest.Matcher)}.\n *\n * @return the DSL to continue the construction of the matcher.\n */\ndefault  drm fn(a a){\n  return fn(m(a));\n}");
		assertThat(fieldDSLMethod.asImplementationMethod()).is("");
	}

}
