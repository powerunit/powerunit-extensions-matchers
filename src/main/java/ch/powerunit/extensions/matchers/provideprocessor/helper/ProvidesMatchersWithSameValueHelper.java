package ch.powerunit.extensions.matchers.provideprocessor.helper;

import static ch.powerunit.extensions.matchers.common.CommonUtils.addPrefix;
import static ch.powerunit.extensions.matchers.provideprocessor.dsl.DSLMethod.of;
import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.Arrays.stream;
import static java.util.Collections.emptyList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Stream;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.tools.Diagnostic.Kind;

import ch.powerunit.extensions.matchers.provideprocessor.Matchable;
import ch.powerunit.extensions.matchers.provideprocessor.ProvidesMatchersAnnotatedElementMirror;
import ch.powerunit.extensions.matchers.provideprocessor.dsl.DSLMethod;
import ch.powerunit.extensions.matchers.provideprocessor.fields.AbstractFieldDescription;

public final class ProvidesMatchersWithSameValueHelper {

	private ProvidesMatchersWithSameValueHelper() {
	}

	private static DSLMethod generateWithSameValueWithParentMatcherIgnoreAndCycle(
			ProvidesMatchersAnnotatedElementMirror target, boolean hasSuper) {
		String genericNoParent = target.getSimpleNameOfGeneratedInterfaceMatcherWithGenericNoParent();
		String simpleNameGenericNoParent = target.getSimpleNameOfGeneratedImplementationMatcherWithGenericNoParent();
		String simpleNameGenericWithParent = target
				.getFullyQualifiedNameOfClassAnnotatedWithProvideMatcherWithGeneric();
		String argumentForParentBuilder = hasSuper
				? (target.getParentMirror()
						.map(p -> p.getWithSameValue(false) + "(other" + (p.supportIgnore() ? ",ignoredFields" : "")
								+ ")")
						.orElse("org.hamcrest.Matchers.anything()"))
				: "";
		String javadoc = target.generateDefaultJavaDocWithoutDSLStarter(Optional.of(
				"other the other object to be used as a reference.\nprevious the previous object of the call stack of matcher\nignoredFields fields name that must be ignored."),
				"the DSL matcher", false);
		List<String> lines = new ArrayList<>();
		lines.add("java.util.Set<java.lang.Object> nPrevious = new java.util.HashSet(previous);");
		lines.add("nPrevious.add(other);");
		lines.add("java.util.Set<String> ignored = new java.util.HashSet<>(java.util.Arrays.asList(ignoredFields));");
		lines.add(genericNoParent + " m=new " + simpleNameGenericNoParent + "(" + argumentForParentBuilder + ");");
		lines.add("if (previous.stream().anyMatch(p->p==other)) {");
		lines.add(
				"  return m.andWith(org.hamcrest.Matchers.describedAs(\"Same instance control only. A cycle has been detected.\",org.hamcrest.Matchers.sameInstance(other)));");
		lines.add("}");
		target.getFields().stream().flatMap(ProvidesMatchersWithSameValueHelper::copyField).forEach(lines::add);
		lines.add("return m;");
		return of(generateHasSameValueDeclaration(target))
				.withArguments(new String[][] { { simpleNameGenericWithParent, "other" },
						{ "java.util.Set<java.lang.Object>", "previous" }, { "String...", "ignoredFields" } })
				.withImplementation(lines).withJavadoc(javadoc);
	}

	private static Stream<String> copyField(AbstractFieldDescription f) {
		String args = f.getTargetAsMatchable().filter(Matchable::supportCycleDetectionV1)
				.map(x -> ",nPrevious,localIgnored").orElse(",localIgnored");
		return stream(format(
				"if(!ignored.contains(\"%1$s\")) {\n  String localIgnored[] = ignored.stream().filter(s->s.startsWith(\"%1$s.\")).map(s->s.replaceFirst(\"%1$s\\\\.\",\"\")).toArray(String[]::new);\n%2$s\n}",
				f.getFieldName(), addPrefix("  ", f.getFieldCopy("m", "other", args))).split("\n"));
	}

	private static DSLMethod generateWithSameValueWithParentMatcherIgnore(
			ProvidesMatchersAnnotatedElementMirror target) {
		String simpleNameGenericWithParent = target
				.getFullyQualifiedNameOfClassAnnotatedWithProvideMatcherWithGeneric();
		String javadoc = target.generateDefaultJavaDocWithoutDSLStarter(Optional.of(
				"other the other object to be used as a reference.\nignoredFields fields name that must be ignored."),
				"the DSL matcher", false);
		return of(generateHasSameValueDeclaration(target))
				.withArguments(
						new String[][] { { simpleNameGenericWithParent, "other" }, { "String...", "ignoredFields" } })
				.withImplementation(format("return %1$s(other,java.util.Collections.emptySet(),ignoredFields);",
						target.getMethodNameDSLWithSameValue()))
				.withJavadoc(javadoc);
	}

	private static DSLMethod generateWithSameValueWithParentMatcherAndNoIgnore(
			ProvidesMatchersAnnotatedElementMirror target) {
		return of(generateHasSameValueDeclaration(target))
				.withOneArgument(target.getFullyQualifiedNameOfClassAnnotatedWithProvideMatcherWithGeneric(), "other")
				.withImplementation(format("return %1$s(other,java.util.Collections.emptySet(),new String[]{});",
						target.getMethodNameDSLWithSameValue()))
				.withJavadoc(target.generateDefaultJavaDocWithoutDSLStarter(
						Optional.of("other the other object to be used as a reference."), "the DSL matcher", false));
	}

	private static String generateHasSameValueDeclaration(ProvidesMatchersAnnotatedElementMirror target) {
		return format("%1$s %2$s.%3$s %4$s", target.getFullGeneric(), target.getFullyQualifiedNameOfGeneratedClass(),
				target.getSimpleNameOfGeneratedInterfaceMatcherWithGenericNoParent(),
				target.getMethodNameDSLWithSameValue());
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
						generateWithSameValueWithParentMatcherIgnore(target),
						generateWithSameValueWithParentMatcherAndNoIgnore(target)))
				.orElseGet(() -> {
					if (isWeakAllowed(target)) {
						logWeak(target);
						return asList(generateWithSameValueWithParentMatcherIgnoreAndCycle(target, true),
								generateWithSameValueWithParentMatcherIgnore(target),
								generateWithSameValueWithParentMatcherAndNoIgnore(target));
					} else {
						return emptyList();
					}
				});
	}

	public static Collection<DSLMethod> generateNoParentValueDSLStarter(ProvidesMatchersAnnotatedElementMirror target) {
		return asList(generateWithSameValueWithParentMatcherIgnoreAndCycle(target, false),
				generateWithSameValueWithParentMatcherIgnore(target),
				generateWithSameValueWithParentMatcherAndNoIgnore(target));
	}

}
