/**
 * Powerunit - A JDK1.8 test framework
 * Copyright (C) 2014 Mathieu Boretti.
 *
 * This file is part of Powerunit
 *
 * Powerunit is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Powerunit is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Powerunit. If not, see <http://www.gnu.org/licenses/>.
 */
package ch.powerunit.extensions.matchers.provideprocessor;

import static ch.powerunit.extensions.matchers.common.CommonUtils.addPrefix;
import static java.util.Collections.unmodifiableList;
import static java.util.stream.Collectors.toList;

import java.io.PrintWriter;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic.Kind;

import ch.powerunit.extensions.matchers.common.CommonUtils;
import ch.powerunit.extensions.matchers.common.FileObjectHelper;
import ch.powerunit.extensions.matchers.provideprocessor.fields.AbstractFieldDescription;
import ch.powerunit.extensions.matchers.provideprocessor.xml.GeneratedMatcher;

public class ProvidesMatchersAnnotatedElementMirror extends ProvidesMatchersAnnotatedElementMatcherMirror {

	private final Collection<Supplier<DSLMethod>> dslProvider;

	public ProvidesMatchersAnnotatedElementMirror(TypeElement typeElement, RoundMirror roundMirror) {
		super(typeElement, roundMirror);
		boolean hasParentInSameRound = roundMirror.isInSameRound(typeElement);
		List<Supplier<DSLMethod>> tmp = new ArrayList<>(
				Arrays.asList(this::generateDefaultDSLStarter, this::generateDefaultForChainingDSLStarter));
		if (fullyQualifiedNameOfSuperClassOfClassAnnotatedWithProvideMatcher.isPresent()) {
			tmp.add(this::generateParentDSLStarter);
			if (hasParentInSameRound) {
				tmp.add(this::generateParentValueDSLStarter);
				if (((TypeElement) roundMirror.getProcessingEnv().getTypeUtils()
						.asElement(typeElementForClassAnnotatedWithProvideMatcher.getSuperclass())).getTypeParameters()
								.isEmpty()) {
					tmp.add(this::generateParentInSameRoundWithChaningDSLStarter);
				}
			}
		} else {
			tmp.add(this::generateNoParentValueDSLStarter);
		}
		tmp.addAll(Optional.ofNullable(getDSLExtension()).orElseGet(Collections::emptyList).stream()
				.map(t -> t.getDSLMethodFor(() -> this)).flatMap(Collection::stream).collect(toList()));
		tmp.addAll(roundMirror.getDSLMethodFor(() -> this));
		this.dslProvider = unmodifiableList(tmp);
	}

	public Collection<DSLMethod> process() {
		RoundMirror rm = roundMirror;
		Element te = typeElementForClassAnnotatedWithProvideMatcher;
		String simpleName = getSimpleNameOfGeneratedClass();
		Collection<DSLMethod> results = new ArrayList<>();
		FileObjectHelper.processFileWithIOException(
				() -> rm.getProcessingEnv().getFiler().createSourceFile(getFullyQualifiedNameOfGeneratedClass(), te),
				jfo -> new PrintWriter(jfo.openWriter()), wjfo -> {
					wjfo.println("package " + getPackageNameOfGeneratedClass() + ";");
					wjfo.println();
					wjfo.println(generateMainJavaDoc());
					wjfo.println("@javax.annotation.Generated(value=\""
							+ ProvidesMatchersAnnotationsProcessor.class.getName() + "\",date=\""
							+ Instant.now().toString() + "\",comments="
							+ CommonUtils.toJavaSyntax(annotation.get().comments()) + ")");
					wjfo.println("public final class " + simpleName + " {");
					wjfo.println();
					wjfo.println("  private " + simpleName + "() {}");
					wjfo.println();
					wjfo.println(generateMatchers());
					wjfo.println();
					wjfo.println(generatePublicInterface());
					wjfo.println();
					wjfo.println(generatePrivateImplementation());
					wjfo.println();
					Collection<DSLMethod> tmp = generateDSLStarter();
					tmp.stream().map(m -> addPrefix("  ", m.asStaticImplementation())).forEach(wjfo::println);
					wjfo.println("}");
					results.addAll(tmp);
				}, e -> rm.getProcessingEnv().getMessager().printMessage(Kind.ERROR,
						"Unable to create the file containing the target class because of " + e, te));
		return results;
	}

	public Collection<DSLMethod> generateDSLStarter() {
		return dslProvider.stream().map(Supplier::get).collect(toList());
	}

	public String getDefaultStarterBody(boolean withParentBuilder) {
		String targetImpl = withParentBuilder ? getSimpleNameOfGeneratedImplementationMatcherWithGenericParent()
				: getSimpleNameOfGeneratedImplementationMatcherWithGenericNoParent();
		boolean withSuper = fullyQualifiedNameOfSuperClassOfClassAnnotatedWithProvideMatcher.isPresent();
		if (withParentBuilder) {
			return withSuper ? ("return new " + targetImpl + "(org.hamcrest.Matchers.anything(),parentBuilder);")
					: ("return new " + targetImpl + "(parentBuilder);");
		} else {
			return withSuper ? ("return new " + targetImpl + "(org.hamcrest.Matchers.anything());")
					: ("return new " + targetImpl + "();");
		}
	}

	public DSLMethod generateDefaultDSLStarter() {
		return new DSLMethod(
				generateDefaultJavaDoc(Optional.of(
						"The returned builder (which is also a Matcher), at this point accepts any object that is a "
								+ getDefaultLinkForAnnotatedClass() + "."),
						Optional.empty(), "the DSL matcher", false),
				fullGeneric + " " + getFullyQualifiedNameOfGeneratedClass() + "."
						+ getSimpleNameOfGeneratedInterfaceMatcherWithGenericNoParent() + " " + methodShortClassName
						+ "With",
				getDefaultStarterBody(false));
	}

	public DSLMethod generateDefaultForChainingDSLStarter() {
		return new DSLMethod(
				generateDefaultJavaDoc(Optional.of(
						"The returned builder (which is also a Matcher), at this point accepts any object that is a "
								+ getDefaultLinkForAnnotatedClass() + "."),
						Optional.of("parentBuilder the parentBuilder."), "the DSL matcher", true),
				getFullGenericParent() + " " + getFullyQualifiedNameOfGeneratedClass() + "."
						+ getSimpleNameOfGeneratedInterfaceMatcherWithGenericParent() + " " + methodShortClassName
						+ "WithParent",
				new String[] { "_PARENT", "parentBuilder" }, getDefaultStarterBody(true));
	}

	public DSLMethod generateParentDSLStarter() {
		String mscn = methodShortClassName;
		String fqngc = getFullyQualifiedNameOfGeneratedClass();
		return new DSLMethod(
				generateDefaultJavaDoc(Optional.empty(), Optional.of("matcherOnParent the matcher on the parent data."),
						"the DSL matcher", false),
				fullGeneric + " " + fqngc + "." + getSimpleNameOfGeneratedInterfaceMatcherWithGenericNoParent() + " "
						+ mscn + "With",
				new String[] {
						"org.hamcrest.Matcher<? super "
								+ fullyQualifiedNameOfSuperClassOfClassAnnotatedWithProvideMatcher.get() + ">",
						"matcherOnParent" },
				"return " + fqngc + "." + mscn + "With(matcherOnParent);");
	}

	public DSLMethod generatParentValueDSLStarter(String argumentForParentBuilder) {
		String genericNoParent = getSimpleNameOfGeneratedInterfaceMatcherWithGenericNoParent();
		String javadoc = generateDefaultJavaDoc(Optional.empty(),
				Optional.of("other the other object to be used as a reference."), "the DSL matcher", false);
		List<String> lines = new ArrayList<>();
		lines.add(genericNoParent + " m=new " + getSimpleNameOfGeneratedImplementationMatcherWithGenericNoParent() + "("
				+ argumentForParentBuilder + ");");
		lines.addAll(fields.stream().map(f -> "    " + f.getFieldCopy("m", "other") + ";").collect(toList()));
		lines.add("return m;");
		return new DSLMethod(javadoc,
				fullGeneric + " " + getFullyQualifiedNameOfGeneratedClass() + "." + genericNoParent + " "
						+ methodShortClassName + "WithSameValue",
				new String[] { getFullyQualifiedNameOfClassAnnotatedWithProvideMatcherWithGeneric(), "other" },
				lines.toArray(new String[0]));
	}

	public DSLMethod generateNoParentValueDSLStarter() {
		return generatParentValueDSLStarter("");
	}

	public ProvidesMatchersAnnotatedElementMirror getParentMirror() {
		RoundMirror rm = roundMirror;
		return rm.getByName(((TypeElement) rm.getProcessingEnv().getTypeUtils()
				.asElement(typeElementForClassAnnotatedWithProvideMatcher.getSuperclass())).getQualifiedName()
						.toString());
	}

	public DSLMethod generateParentValueDSLStarter() {
		ProvidesMatchersAnnotatedElementMirror parentMirror = getParentMirror();
		String argumentForParentBuilder = parentMirror.getFullyQualifiedNameOfGeneratedClass() + "."
				+ parentMirror.methodShortClassName + "WithSameValue(other)";
		return generatParentValueDSLStarter(argumentForParentBuilder);
	}

	public DSLMethod generateParentInSameRoundWithChaningDSLStarter() {
		String implGenericNoParent = getSimpleNameOfGeneratedImplementationMatcherWithGenericNoParent();
		ProvidesMatchersAnnotatedElementMirror parentMirror = getParentMirror();
		String pmfqngc = parentMirror.getFullyQualifiedNameOfGeneratedClass();
		String parentSimpleName = parentMirror.simpleNameOfGeneratedInterfaceMatcher;
		return new DSLMethod(generateDefaultJavaDoc(Optional.empty(), Optional.empty(), "the DSL matcher", false),
				fullGeneric + " " + pmfqngc + "." + parentSimpleName + genericForChaining + " " + methodShortClassName
						+ "WithParent",
				new String[] {
						implGenericNoParent + " m=new " + implGenericNoParent + "(org.hamcrest.Matchers.anything());",
						pmfqngc + "." + parentSimpleName + " tmp = " + pmfqngc + "." + parentMirror.methodShortClassName
								+ "WithParent(m);",
						"m._parent = new SuperClassMatcher(tmp);", "return tmp;" });
	}

	public GeneratedMatcher asXml() {
		GeneratedMatcher gm = new GeneratedMatcher();
		gm.setFullyQualifiedNameGeneratedClass(getFullyQualifiedNameOfGeneratedClass());
		gm.setFullyQualifiedNameInputClass(getFullyQualifiedNameOfClassAnnotatedWithProvideMatcher());
		gm.setSimpleNameGeneratedClass(getSimpleNameOfGeneratedClass());
		gm.setSimpleNameInputClass(getSimpleNameOfClassAnnotatedWithProvideMatcher());
		gm.setDslMethodNameStart(methodShortClassName);
		gm.setGeneratedMatcherField(
				fields.stream().map(AbstractFieldDescription::asGeneratedMatcherField).collect(toList()));
		gm.setMirror(this);
		return gm;
	}

}
