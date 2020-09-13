package ch.powerunit.extensions.matchers.provideprocessor.helper;

import static ch.powerunit.extensions.matchers.common.CommonUtils.addPrefix;
import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.Arrays.stream;
import static java.util.Collections.emptyList;
import static java.util.Optional.of;
import static java.util.stream.Collectors.joining;

import java.util.Collection;
import java.util.Map.Entry;
import java.util.Optional;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.tools.Diagnostic.Kind;

import ch.powerunit.extensions.matchers.common.RessourceLoaderHelper;
import ch.powerunit.extensions.matchers.provideprocessor.Matchable;
import ch.powerunit.extensions.matchers.provideprocessor.ProvidesMatchersAnnotatedElementMirror;
import ch.powerunit.extensions.matchers.provideprocessor.dsl.DSLMethod;
import ch.powerunit.extensions.matchers.provideprocessor.dsl.lang.DSLMethodArgument;
import ch.powerunit.extensions.matchers.provideprocessor.fields.AbstractFieldDescription;

public final class ProvidesMatchersWithSameValueHelper {

	private static final String HAS_SAME_VALUE_IGNORE_CYCLE = RessourceLoaderHelper
			.loadRessource(ProvidesMatchersWithSameValueHelper.class, "DSLHasSameValueIgnoreAndCycle.txt");

	private static final String JAVADOC_OTHER = "other the other object to be used as a reference.";

	private static final String JAVDOC_PREVIOUS = "previous the previous object of the call stack of matcher.";

	private static final String JAVADOC_IGNORE = "ignoredFields fields name that must be ignored.";

	private static final String JAVADOC_POSTPROCESSOR = "postProcessor Function to be applied to modify, if necessary, the matchers.";

	private static final String JAVADOC_OTHER_IGNORE = JAVADOC_OTHER + "\n" + JAVADOC_IGNORE;

	private static final String JAVADOC_OTHER_IGNORE_POSTPROCESSOR = JAVADOC_OTHER + "\n" + JAVADOC_IGNORE + "\n"
			+ JAVADOC_POSTPROCESSOR;

	private static final String JAVADOC_OTHER_PREVIOUS_IGNORE_POST = JAVADOC_OTHER + "\n" + JAVDOC_PREVIOUS + "\n"
			+ JAVADOC_IGNORE + "\n" + JAVADOC_POSTPROCESSOR;

	private ProvidesMatchersWithSameValueHelper() {
	}

	private static DSLMethod generateWithSameValueWithParentMatcherIgnoreAndCycle(
			ProvidesMatchersAnnotatedElementMirror target, boolean hasSuper) {
		return generateHasSameValueDeclaration(target)
				.addOneArgument(target.getFullyQualifiedNameOfClassAnnotatedWithProvideMatcherWithGeneric(), "other")
				.addOneArgument("java.util.Set<java.lang.Object>", "previous")
				.addOneArgument(
						"java.util.function.BiFunction<org.hamcrest.Matcher<?>,java.lang.Object,org.hamcrest.Matcher<?>>",
						"postProcessor")
				.addOneArgument("String...", "ignoredFields")
				.withImplementation(asList(format(HAS_SAME_VALUE_IGNORE_CYCLE,
						target.getSimpleNameOfGeneratedInterfaceMatcherWithGenericNoParent(),
						target.getSimpleNameOfGeneratedImplementationMatcherWithGenericNoParent(),
						generateParentMatcher(target, hasSuper), copyFields(target),
						target.getSimpleNameOfGeneratedInterfaceMatcher()).split("\n")))
				.withJavadoc(target.generateDefaultJavaDocWithoutDSLStarter(of(JAVADOC_OTHER_PREVIOUS_IGNORE_POST),
						"the DSL matcher", false));
	}

	private static String generateParentMatcher(ProvidesMatchersAnnotatedElementMirror target, boolean hasSuper) {
		return hasSuper ? (target.getParentMirror()
				.map(p -> "(" + p.getFullyQualifiedNameOfGeneratedClass() + "."
						+ p.getSimpleNameOfGeneratedInterfaceMatcher()
						+ ")java.util.Objects.requireNonNull(postProcessor,\"postProcessor can't be null\").apply("
						+ p.getWithSameValue(false) + "(other"
						+ (p.supportSameValueWithParentPostPrecessor() ? ",postProcessor" : "")
						+ (p.supportIgnore() ? ",ignoredFields" : "") + "),other)")
				.orElse("org.hamcrest.Matchers.anything()")) : "";
	}

	private static String copyFields(ProvidesMatchersAnnotatedElementMirror target) {
		return target.getFields().stream().flatMap(f -> stream(copyField(f).split("\n"))).collect(joining("\n"));
	}

	private static String copyField(AbstractFieldDescription f) {
		String args = f
				.getTargetAsMatchable().filter(Matchable::supportCycleDetectionV1).map(x -> ",nPrevious"
						+ (x.supportSameValueWithParentPostPrecessor() ? ",postProcessor" : "") + ",localIgnored")
				.orElse(",localIgnored");
		return format(
				"if(!ignored.contains(\"%1$s\")) {\n  String localIgnored[] = ignored.stream().filter(s->s.startsWith(\"%1$s.\")).map(s->s.replaceFirst(\"%1$s\\\\.\",\"\")).toArray(String[]::new);\n%2$s\n}",
				f.getFieldName(), addPrefix("  ", f.getFieldCopy("m", "other", args)));
	}

	private static DSLMethod generateWithSameValueWithParentMatcherIgnoreAndPostProcessor(
			ProvidesMatchersAnnotatedElementMirror target) {
		return generateHasSameValueDeclaration(target)
				.addOneArgument(target.getFullyQualifiedNameOfClassAnnotatedWithProvideMatcherWithGeneric(), "other")
				.addOneArgument(
						"java.util.function.BiFunction<org.hamcrest.Matcher<?>,java.lang.Object,org.hamcrest.Matcher<?>>",
						"postProcessor")
				.addOneArgument("String...", "ignoredFields")
				.withImplementation(generateReturnOther(target, "postProcessor,ignoredFields"))
				.withJavadoc(target.generateDefaultJavaDocWithoutDSLStarter(of(JAVADOC_OTHER_IGNORE_POSTPROCESSOR),
						"the DSL matcher", false));
	}

	private static DSLMethod generateWithSameValueWithParentMatcherIgnore(
			ProvidesMatchersAnnotatedElementMirror target) {
		return generateHasSameValueDeclaration(target)
				.addOneArgument(target.getFullyQualifiedNameOfClassAnnotatedWithProvideMatcherWithGeneric(), "other")
				.addOneArgument("String...", "ignoredFields")
				.withImplementation(generateReturnOther(target, "(m,o)->m,ignoredFields")).withJavadoc(target
						.generateDefaultJavaDocWithoutDSLStarter(of(JAVADOC_OTHER_IGNORE), "the DSL matcher", false));
	}

	private static DSLMethod generateWithSameValueWithParentMatcherAndNoIgnore(
			ProvidesMatchersAnnotatedElementMirror target) {
		return generateHasSameValueDeclaration(target)
				.withOneArgument(target.getFullyQualifiedNameOfClassAnnotatedWithProvideMatcherWithGeneric(), "other")
				.withImplementation(generateReturnOther(target, "(m,o)->m,new String[]{}")).withJavadoc(
						target.generateDefaultJavaDocWithoutDSLStarter(of(JAVADOC_OTHER), "the DSL matcher", false));
	}

	private static String generateReturnOther(ProvidesMatchersAnnotatedElementMirror target, String complement) {
		return format("return %1$s(other,java.util.Collections.emptySet(),%2$s);",
				target.getMethodNameDSLWithSameValue(), complement);
	}

	private static DSLMethodArgument generateHasSameValueDeclaration(ProvidesMatchersAnnotatedElementMirror target) {
		return DSLMethod.of(
				format("%1$s %2$s.%3$s %4$s", target.getFullGeneric(), target.getFullyQualifiedNameOfGeneratedClass(),
						target.getSimpleNameOfGeneratedInterfaceMatcherWithGenericNoParent(),
						target.getMethodNameDSLWithSameValue()));
	}

	private static boolean isWeakAllowed(ProvidesMatchersAnnotatedElementMirror target) {
		return target.getRealAnnotation().allowWeakWithSameValue();
	}

	private static void logWeak(ProvidesMatchersAnnotatedElementMirror target) {
		Optional<AnnotationMirror> am = target.getAnnotationMirror();
		Optional<? extends AnnotationValue> av = am.map(a -> a.getElementValues().entrySet().stream()
				.filter(kv -> kv.getKey().getSimpleName().toString().equals("allowWeakWithSameValue"))
				.map(Entry::getValue).findAny().orElse(null));
		target.getMessager().printMessage(Kind.MANDATORY_WARNING,
				"This class use the option allowWeakWithSameValue and a weak WithSameValue is detected. The generated WithSameValue DSL may not be able to fully control all the field of this class",
				target.getElement(), am.orElse(null), av.orElse(null));
	}

	public static Collection<DSLMethod> generateParentValueDSLStarter(ProvidesMatchersAnnotatedElementMirror target) {
		return target.getParentMirror()
				.map(parentMirror -> asList(generateWithSameValueWithParentMatcherIgnoreAndCycle(target, true),
						generateWithSameValueWithParentMatcherIgnoreAndPostProcessor(target),
						generateWithSameValueWithParentMatcherIgnore(target),
						generateWithSameValueWithParentMatcherAndNoIgnore(target)))
				.orElseGet(() -> {
					if (isWeakAllowed(target)) {
						logWeak(target);
						return asList(generateWithSameValueWithParentMatcherIgnoreAndCycle(target, true),
								generateWithSameValueWithParentMatcherIgnoreAndPostProcessor(target),
								generateWithSameValueWithParentMatcherIgnore(target),
								generateWithSameValueWithParentMatcherAndNoIgnore(target));
					} else {
						return emptyList();
					}
				});
	}

	public static Collection<DSLMethod> generateNoParentValueDSLStarter(ProvidesMatchersAnnotatedElementMirror target) {
		return asList(generateWithSameValueWithParentMatcherIgnoreAndCycle(target, false),
				generateWithSameValueWithParentMatcherIgnoreAndPostProcessor(target),
				generateWithSameValueWithParentMatcherIgnore(target),
				generateWithSameValueWithParentMatcherAndNoIgnore(target));
	}

}
