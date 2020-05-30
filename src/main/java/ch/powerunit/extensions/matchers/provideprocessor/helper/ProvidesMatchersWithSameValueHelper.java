package ch.powerunit.extensions.matchers.provideprocessor.helper;

import static ch.powerunit.extensions.matchers.common.CommonUtils.addPrefix;
import static ch.powerunit.extensions.matchers.provideprocessor.dsl.DSLMethod.of;
import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.Arrays.stream;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.tools.Diagnostic.Kind;

import ch.powerunit.extensions.matchers.provideprocessor.ProvidesMatchersAnnotatedElementMirror;
import ch.powerunit.extensions.matchers.provideprocessor.dsl.DSLMethod;
import ch.powerunit.extensions.matchers.provideprocessor.fields.AbstractFieldDescription;

public final class ProvidesMatchersWithSameValueHelper {

	private ProvidesMatchersWithSameValueHelper() {
	}

	private static DSLMethod generateWithSameValueWithParentMatcherIgnore(ProvidesMatchersAnnotatedElementMirror target,
			boolean hasSuper) {
		String genericNoParent = target.getSimpleNameOfGeneratedInterfaceMatcherWithGenericNoParent();
		String simpleNameGenericNoParent = target.getSimpleNameOfGeneratedImplementationMatcherWithGenericNoParent();
		String simpleNameGenericWithParent = target
				.getFullyQualifiedNameOfClassAnnotatedWithProvideMatcherWithGeneric();
		String fullyQualified = target.getFullyQualifiedNameOfGeneratedClass();
		String withSameValueMethodName = target.getMethodNameDSLWithSameValue();
		String fullGeneric = target.getFullGeneric();
		String argumentForParentBuilder = hasSuper
				? (target.getParentMirror()
						.map(p -> p.getWithSameValue(false) + "(other" + (p.supportIgnore() ? ",ignoredFields" : "")
								+ ")")
						.orElse("org.hamcrest.Matchers.anything()"))
				: "";
		String javadoc = target.generateDefaultJavaDocWithoutDSLStarter(Optional.of(
				"other the other object to be used as a reference.\n ignoredFields fields name that must be ignored."),
				"the DSL matcher", false);
		List<String> lines = new ArrayList<>();
		lines.add("java.util.Set<String> ignored = new java.util.HashSet<>(java.util.Arrays.asList(ignoredFields));");
		lines.add(genericNoParent + " m=new " + simpleNameGenericNoParent + "(" + argumentForParentBuilder + ");");
		target.getFields().stream().flatMap(ProvidesMatchersWithSameValueHelper::copyField).forEach(lines::add);
		lines.add("return m;");
		return of(format("%1$s %2$s.%3$s %4$s", fullGeneric, fullyQualified, genericNoParent, withSameValueMethodName))
				.withArguments(
						new String[][] { { simpleNameGenericWithParent, "other" }, { "String...", "ignoredFields" } })
				.withImplementation(lines).withJavadoc(javadoc);
	}

	private static Stream<String> copyField(AbstractFieldDescription f) {
		return stream(format(
				"if(!ignored.contains(\"%1$s\")) {\n  String localIgnored[] = ignored.stream().filter(s->s.startsWith(\"%1$s.\")).map(s->s.replaceFirst(\"%1$s\\\\.\",\"\")).toArray(String[]::new);\n%2$s\n}",
				f.getFieldName(), addPrefix("  ", f.getFieldCopy("m", "other", ",localIgnored"))).split("\n"));
	}

	private static DSLMethod generateWithSameValueWithParentMatcherAndNoIgnore(
			ProvidesMatchersAnnotatedElementMirror target) {
		String withSameValueMethodName = target.getMethodNameDSLWithSameValue();
		return of(format("%1$s %2$s.%3$s %4$s", target.getFullGeneric(), target.getFullyQualifiedNameOfGeneratedClass(),
				target.getSimpleNameOfGeneratedInterfaceMatcherWithGenericNoParent(), withSameValueMethodName))
						.withOneArgument(target.getFullyQualifiedNameOfClassAnnotatedWithProvideMatcherWithGeneric(),
								"other")
						.withImplementation(format("return %1$s(other,new String[]{});", withSameValueMethodName))
						.withJavadoc(target.generateDefaultJavaDocWithoutDSLStarter(
								Optional.of("other the other object to be used as a reference."), "the DSL matcher",
								false));
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

	public static Collection<Supplier<DSLMethod>> generateParentValueDSLStarter(
			ProvidesMatchersAnnotatedElementMirror target) {
		return asList(() -> target.getParentMirror()
				.map(parentMirror -> generateWithSameValueWithParentMatcherIgnore(target, true)).orElseGet(() -> {
					if (isWeakAllowed(target)) {
						logWeak(target);
						return generateWithSameValueWithParentMatcherIgnore(target, true);
					} else {
						return null;
					}
				}),
				() -> target.getParentMirror()
						.map(parentMirror -> generateWithSameValueWithParentMatcherAndNoIgnore(target))
						.orElseGet(() -> {
							if (isWeakAllowed(target)) {
								logWeak(target);
								return generateWithSameValueWithParentMatcherAndNoIgnore(target);
							} else {
								return null;
							}
						}));
	}

	public static Collection<Supplier<DSLMethod>> generateNoParentValueDSLStarter(
			ProvidesMatchersAnnotatedElementMirror target) {
		return asList(() -> generateWithSameValueWithParentMatcherIgnore(target, false),
				() -> generateWithSameValueWithParentMatcherAndNoIgnore(target));
	}

}
