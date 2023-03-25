package ch.powerunit.extensions.matchers.provideprocessor;

import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.stream.Collectors;

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
import ch.powerunit.extensions.matchers.TestSuiteSupport;
import ch.powerunit.extensions.matchers.api.ProvideMatchers;
import ch.powerunit.extensions.matchers.provideprocessor.dsl.DSLMethod;

public class ProvidesMatchersAnnotatedElementMirrorTest implements TestSuiteSupport {
	@Rule
	public final TestRule rules = mockitoRule().around(before(this::prepareMock));

	@Mock
	private TypeElement typeElement;

	@Mock
	private Name fullyQualifiedName;

	@Mock
	private Name simpleName;

	@Mock
	private Name packageName;

	@Mock
	private PackageElement packageElement;

	private ProcessingEnvironment processingEnv;

	private Elements elements;

	@Mock
	private Types types;

	@Mock
	private ProvideMatchers provideMatcher;

	@Mock
	private TypeElement te1;

	private RoundMirror roundMirror;

	private void prepareMock() {
		processingEnv = generateMockitoProcessingEnvironment();
		when(provideMatcher.matchersClassName()).thenReturn("");
		when(provideMatcher.matchersPackageName()).thenReturn("");
		when(provideMatcher.comments()).thenReturn("");

		elements = processingEnv.getElementUtils();
		when(elements.getTypeElement(Mockito.argThat(not("java.lang.Object")))).thenReturn(te1);
		when(processingEnv.getElementUtils()).thenReturn(elements);
		when(processingEnv.getTypeUtils()).thenReturn(types);

		when(elements.getPackageOf(Mockito.any(Element.class))).thenReturn(packageElement);

		when(typeElement.getQualifiedName()).thenReturn(fullyQualifiedName);
		when(typeElement.getSimpleName()).thenReturn(simpleName);
		when(typeElement.getAnnotation(ProvideMatchers.class)).thenReturn(provideMatcher);
		TypeMirror object = elements.getTypeElement("java.lang.Object").asType();
		when(typeElement.getSuperclass()).thenReturn(object);

		when(fullyQualifiedName.toString()).thenReturn("fqn.Sn");
		when(packageElement.getQualifiedName()).thenReturn(packageName);
		when(packageName.toString()).thenReturn("fqn");
		when(simpleName.toString()).thenReturn("Sn");
		roundMirror = new RoundMirror(generateMockitoRoundEnvironment(), processingEnv);
	}

	@Test
	public void testGenerateAndExtractFieldAndParentPrivateMatcherWithoutField() {
		ProvidesMatchersAnnotatedElementMirror underTest = new ProvidesMatchersAnnotatedElementMirror(typeElement,
				roundMirror);
		assertThat(underTest.generateMatchers()).is(
				"\n  private static <_TARGET,_SOURCE> org.hamcrest.Matcher<_SOURCE> asFeatureMatcher(\n      String msg,\n      java.util.function.Function<_SOURCE,_TARGET> converter,\n      org.hamcrest.Matcher<? super _TARGET> matcher) {\n   return new org.hamcrest.FeatureMatcher<_SOURCE,_TARGET>(matcher, msg, msg) {\n     protected _TARGET featureValueOf(_SOURCE actual) {\n      return converter.apply(actual);\n    }};\n  }\n\n\n");
	}

	@Test
	public void testGeneratePublicInterfaceWithoutField() {
		ProvidesMatchersAnnotatedElementMirror underTest = new ProvidesMatchersAnnotatedElementMirror(typeElement,
				roundMirror);
		assertThat(underTest.generatePublicInterface()).is(
				"\n  /**\n   * DSL interface for matcher on {@link fqn.Sn Sn} to support the build syntaxic sugar.\n   * \n   * \n  \n   *\n   */\n  public static interface SnMatcherBuildSyntaxicSugar\n    extends org.hamcrest.Matcher<fqn.Sn > {\n  \n    /**\n     * Method that returns the matcher itself.\n     * <p>\n     * <b>This method is a syntaxic sugar that end the DSL and make clear that the matcher can't be change anymore.</b>\n     *\n     * @return the matcher\n     */\n    default org.hamcrest.Matcher<fqn.Sn > build() {\n      return this;\n    }\n  \n  }\n  \n  /**\n   * DSL interface for matcher on {@link fqn.Sn Sn} to support the end build syntaxic sugar.\n   * \n   * \n  \n   * @param <_PARENT> used to reference, if necessary, a parent for this builder. By default Void is used to indicate no parent builder.\n   *\n   */\n  public static interface SnMatcherEndSyntaxicSugar<_PARENT>\n    extends org.hamcrest.Matcher<fqn.Sn > {\n  \n    /**\n     * Method that returns the parent builder.\n     * <p>\n     * <b>This method only works in the context of a parent builder. If the real type is Void, then nothing will be returned.</b>\n     *\n     * @return the parent builder or null if not applicable\n     */\n    _PARENT end();\n  \n  }\n  \n  \n  /**\n   * Start a DSL matcher for the {@link fqn.Sn Sn}.\n   * \n   * \n  \n   *\n   */\n  public static sealed interface SnMatcher<_PARENT>\n    extends org.hamcrest.Matcher<fqn.Sn >,\n            SnMatcherBuildSyntaxicSugar,\n            SnMatcherEndSyntaxicSugar<_PARENT>\n    permits SnMatcherImpl {\n  \n  \n  \n    /**\n     * Add a matcher on the object itself and not on a specific field.\n     * <p>\n     * <i>This method, when used more than once, just add more matcher to the list.</i>\n     *\n     * @param otherMatcher the matcher on the object itself.\n     * @return the DSL to continue\n     */\n    SnMatcher<_PARENT> andWith(org.hamcrest.Matcher<? super fqn.Sn > otherMatcher);\n  \n    /**\n     * Add a matcher on the object itself and not on a specific field, but convert the object before passing it to the matcher.\n     * <p>\n     * <i>This method, when used more than once, just add more matcher to the list.</i>\n     *\n     * @param converter the function to convert the object.\n     * @param otherMatcher the matcher on the converter object itself.\n     * @param <_TARGETOBJECT> the type of the target object\n     * @return the DSL to continue\n     */\n    default <_TARGETOBJECT> SnMatcher<_PARENT> andWithAs(java.util.function.Function<fqn.Sn  ,_TARGETOBJECT> converter,org.hamcrest.Matcher<? super _TARGETOBJECT> otherMatcher) {\n      return andWith(asFeatureMatcher(\" <object is converted> \",converter,otherMatcher));\n    }\n  \n    /**\n     * Method that return the matcher itself and accept one single Matcher on the object itself.\n     * <p>\n     * <b>This method is a syntaxic sugar that end the DSL and make clear that the matcher can't be change anymore.</b>\n     *\n     * @param otherMatcher the matcher on the object itself.\n     * @return the matcher\n     */\n    default org.hamcrest.Matcher<fqn.Sn > buildWith(org.hamcrest.Matcher<? super fqn.Sn > otherMatcher) {\n      return andWith(otherMatcher);\n    }\n  \n    /**\n     * Method that return the parent builder and accept one single Matcher on the object itself.\n     * <p>\n     * <b>This method only works in the context of a parent builder. If the real type is Void, then nothing will be returned.</b>\n     *\n     * @param otherMatcher the matcher on the object itself.\n     * @return the parent builder or null if not applicable\n     */\n    default _PARENT endWith(org.hamcrest.Matcher<? super fqn.Sn > otherMatcher){\n      return andWith(otherMatcher).end();\n    }\n  }\n");
	}

	@Test
	public void testGenerateDSLStarter() {
		ProvidesMatchersAnnotatedElementMirror underTest = new ProvidesMatchersAnnotatedElementMirror(typeElement,
				roundMirror);
		Collection<DSLMethod> results = underTest.generateDSLStarter();
		assertThat(results).is(iterableWithSize(7));
		assertThat(results.stream().map(x -> x.asStaticImplementation()).collect(Collectors.joining("\n"))).is(
				"/**\n * Start a DSL matcher for the {@link fqn.Sn Sn}.\n * <p>\n * The returned builder (which is also a Matcher), at this point accepts any object that is a {@link fqn.Sn Sn}.\n * \n * \n * @return the DSL matcher\n */\npublic static  fqn.SnMatchers.SnMatcher <Void> snWith() {\n  return new SnMatcherImpl<Void>();\n}\n\n/**\n * Start a DSL matcher for the {@link fqn.Sn Sn}.\n * <p>\n * The returned builder (which is also a Matcher), at this point accepts any object that is a {@link fqn.Sn Sn}.\n * @param parentBuilder the parentBuilder.\n * \n * \n * @param <_PARENT> used to reference, if necessary, a parent for this builder. By default Void is used an indicate no parent builder.\n * @return the DSL matcher\n */\npublic static <_PARENT> fqn.SnMatchers.SnMatcher <_PARENT> snWithParent(_PARENT parentBuilder) {\n  return new SnMatcherImpl<_PARENT>(parentBuilder);\n}\n\n/**\n * Helper method to retrieve the Class of the matcher interface.\n * @return the class.\n */\npublic static <_PARENT> Class<fqn.SnMatchers.SnMatcher <_PARENT>> snMatcherClass() {\n  return (Class)SnMatcher.class;\n}\n\n/**\n * Start a DSL matcher for the {@link fqn.Sn Sn}.\n * @param other the other object to be used as a reference.\n * @param previous the previous object of the call stack of matcher.\n * @param ignoredFields fields name that must be ignored.\n * @param postProcessor Function to be applied to modify, if necessary, the matchers.\n * \n * \n * @return the DSL matcher\n */\npublic static  fqn.SnMatchers.SnMatcher <Void> snWithSameValue(fqn.Sn  other,java.util.Set<java.lang.Object> previous,java.util.function.BiFunction<org.hamcrest.Matcher<?>,java.lang.Object,org.hamcrest.Matcher<?>> postProcessor,String... ignoredFields) {\n  java.util.Set<java.lang.Object> nPrevious = new java.util.HashSet(previous);\n  nPrevious.add(other);\n  \n  java.util.Set<String> ignored = new java.util.HashSet<>(java.util.Arrays.asList(ignoredFields));\n  \n  SnMatcher <Void> m=new SnMatcherImpl<Void>();\n  \n  if (previous.stream().anyMatch(p->p==other)) {\n    return m.andWith(org.hamcrest.Matchers.describedAs(\"Same instance control only. A cycle has been detected.\",org.hamcrest.Matchers.sameInstance(other)));\n  }\n  \t\t\n  \n  \n  return (SnMatcher)java.util.Objects.requireNonNull(postProcessor,\"postProcessor can't be null\").apply(m,other);\n}\n\n/**\n * Start a DSL matcher for the {@link fqn.Sn Sn}.\n * @param other the other object to be used as a reference.\n * @param ignoredFields fields name that must be ignored.\n * @param postProcessor Function to be applied to modify, if necessary, the matchers.\n * \n * \n * @return the DSL matcher\n */\npublic static  fqn.SnMatchers.SnMatcher <Void> snWithSameValue(fqn.Sn  other,java.util.function.BiFunction<org.hamcrest.Matcher<?>,java.lang.Object,org.hamcrest.Matcher<?>> postProcessor,String... ignoredFields) {\n  return snWithSameValue(other,java.util.Collections.emptySet(),postProcessor,ignoredFields);\n}\n\n/**\n * Start a DSL matcher for the {@link fqn.Sn Sn}.\n * @param other the other object to be used as a reference.\n * @param ignoredFields fields name that must be ignored.\n * \n * \n * @return the DSL matcher\n */\npublic static  fqn.SnMatchers.SnMatcher <Void> snWithSameValue(fqn.Sn  other,String... ignoredFields) {\n  return snWithSameValue(other,java.util.Collections.emptySet(),(m,o)->m,ignoredFields);\n}\n\n/**\n * Start a DSL matcher for the {@link fqn.Sn Sn}.\n * @param other the other object to be used as a reference.\n * \n * \n * @return the DSL matcher\n */\npublic static  fqn.SnMatchers.SnMatcher <Void> snWithSameValue(fqn.Sn  other) {\n  return snWithSameValue(other,java.util.Collections.emptySet(),(m,o)->m,new String[]{});\n}\n");
	}

}
