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

import java.io.IOException;
import java.io.PrintWriter;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedOptions;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic.Kind;
import javax.tools.JavaFileObject;

import ch.powerunit.extensions.matchers.ProvideMatchers;

/**
 * @author borettim
 *
 */
@SupportedAnnotationTypes({ "ch.powerunit.extensions.matchers.ProvideMatchers" })
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedOptions({ "ch.powerunit.extensions.matchers.provideprocessor.ProvidesMatchersAnnotationsProcessor.factory" })
public class ProvidesMatchersAnnotationsProcessor extends AbstractProcessor {

	private String factory = null;

	private List<String> factories = new ArrayList<>();

	@Override
	public synchronized void init(ProcessingEnvironment processingEnv) {
		super.init(processingEnv);
		factory = processingEnv.getOptions().get(ProvidesMatchersAnnotationsProcessor.class.getName() + ".factory");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.annotation.processing.AbstractProcessor#process(java.util.Set,
	 * javax.annotation.processing.RoundEnvironment)
	 */
	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		Elements elementsUtils = processingEnv.getElementUtils();
		Filer filerUtils = processingEnv.getFiler();
		Types typesUtils = processingEnv.getTypeUtils();
		Messager messageUtils = processingEnv.getMessager();
		TypeElement provideMatchersTE = elementsUtils
				.getTypeElement("ch.powerunit.extensions.matchers.ProvideMatchers");
		TypeElement objectTE = elementsUtils.getTypeElement("java.lang.Object");

		if (!roundEnv.processingOver()) {
			processAnnotatedElements(roundEnv, elementsUtils, filerUtils, typesUtils, messageUtils, provideMatchersTE,
					objectTE);

		} else if (factory != null) {
			processFactory(filerUtils, messageUtils);
		}
		return true;
	}

	private void processFactory(Filer filerUtils, Messager messageUtils) {
		try {
			messageUtils.printMessage(Kind.NOTE,
					"The interface `" + factory + "` will be generated as a factory interface.");
			JavaFileObject jfo = filerUtils.createSourceFile(factory);
			try (PrintWriter wjfo = new PrintWriter(jfo.openWriter());) {
				wjfo.println("package " + factory.replaceAll("\\.[^.]+$", "") + ";");
				wjfo.println();
				wjfo.println("/**");
				wjfo.println(" * Factories generated.");
				wjfo.println(" * <p> ");
				wjfo.println(" * This DSL can be use in several way : ");
				wjfo.println(" * <ul> ");
				wjfo.println(
						" *  <li>By implementing this interface. In this case, all the methods of this interface will be available inside the implementing class.</li>");
				wjfo.println(
						" *  <li>By refering the static field named {@link #DSL} which expose all the DSL method.</li>");
				wjfo.println(" * </ul> ");
				wjfo.println(" */");
				wjfo.println(
						"@javax.annotation.Generated(value=\"" + ProvidesMatchersAnnotationsProcessor.class.getName()
								+ "\",date=\"" + Instant.now().toString() + "\")");
				String cName = factory.replaceAll("^([^.]+\\.)*", "");
				wjfo.println("public interface " + cName + " {");
				wjfo.println();
				wjfo.println("  /**");
				wjfo.println(
						"   * Use this static field to access all the DSL syntax, without be required to implements this interface.");
				wjfo.println("   */");
				wjfo.println("  public static final " + cName + " DSL = new " + cName + "() {};");
				wjfo.println();
				factories.stream().forEach(wjfo::println);
				wjfo.println("}");
			}
		} catch (IOException e1) {
			messageUtils.printMessage(Kind.ERROR, "Unable to create the file containing the target class `" + factory
					+ "`, because of " + e1.getMessage());
		}
	}

	private void processAnnotatedElements(RoundEnvironment roundEnv, Elements elementsUtils, Filer filerUtils,
			Types typesUtils, Messager messageUtils, TypeElement provideMatchersTE, TypeElement objectTE) {
		Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(ProvideMatchers.class);
		ProvidesMatchersElementVisitor providesMatchersElementVisitor = new ProvidesMatchersElementVisitor(this,
				elementsUtils, messageUtils, provideMatchersTE);
		Map<String, ProvideMatchersAnnotatedElementMirror> alias = new HashMap<>();
		elements.stream().filter(e -> roundEnv.getRootElements().contains(e))
				.map(e -> e.accept(providesMatchersElementVisitor, null)).filter(Optional::isPresent)
				.map(t -> new ProvideMatchersAnnotatedElementMirror(t.get(), elementsUtils, filerUtils, typesUtils,
						messageUtils, isInSameRound(elements, typesUtils), (n) -> alias.get(n)))
				.forEach(a -> alias.put(a.getFullyQualifiedNameOfClassAnnotatedWithProvideMatcher(), a));

		factories.addAll(alias.values().stream().map(ProvideMatchersAnnotatedElementMirror::process)
				.collect(Collectors.toList()));
	}

	private Predicate<Element> isInSameRound(Set<? extends Element> elements, Types typesUtils) {
		return t -> t == null ? false
				: elements.stream().filter(e -> typesUtils.isSameType(e.asType(), t.asType())).findAny().isPresent();
	}

	AnnotationMirror getProvideMatchersAnnotation(TypeElement provideMatchersTE,
			Collection<? extends AnnotationMirror> annotations) {
		return annotations.stream().filter(a -> a.getAnnotationType().equals(provideMatchersTE.asType())).findAny()
				.orElse(null);
	}
}
