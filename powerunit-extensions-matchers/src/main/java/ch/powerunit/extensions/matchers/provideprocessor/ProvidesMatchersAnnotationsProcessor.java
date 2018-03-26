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
import java.io.OutputStream;
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
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedOptions;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic.Kind;
import javax.tools.FileObject;
import javax.tools.JavaFileObject;
import javax.tools.StandardLocation;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import ch.powerunit.extensions.matchers.AddToMatcher;
import ch.powerunit.extensions.matchers.AddToMatchers;
import ch.powerunit.extensions.matchers.IgnoreInMatcher;
import ch.powerunit.extensions.matchers.ProvideMatchers;
import ch.powerunit.extensions.matchers.provideprocessor.xml.GeneratedMatchers;

/**
 * @author borettim
 *
 */
@SupportedAnnotationTypes({ "ch.powerunit.extensions.matchers.ProvideMatchers",
		"ch.powerunit.extensions.matchers.IgnoreInMatcher", "ch.powerunit.extensions.matchers.AddToMatcher",
		"ch.powerunit.extensions.matchers.AddToMatchers" })
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedOptions({ "ch.powerunit.extensions.matchers.provideprocessor.ProvidesMatchersAnnotationsProcessor.factory" })
public class ProvidesMatchersAnnotationsProcessor extends AbstractProcessor {

	private String factory = null;

	private List<String> factories = new ArrayList<>();

	private GeneratedMatchers allGeneratedMatchers = new GeneratedMatchers();

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
		TypeElement provideMatchersTE = processingEnv.getElementUtils()
				.getTypeElement("ch.powerunit.extensions.matchers.ProvideMatchers");
		if (!roundEnv.processingOver()) {
			processAnnotatedElements(roundEnv, provideMatchersTE);

		} else {
			processReport();
			if (factory != null) {
				processFactory();
			}
		}
		return true;
	}

	private void processReport() {
		try {
			FileObject jfo = processingEnv.getFiler().createResource(StandardLocation.SOURCE_OUTPUT, "",
					"META-INF/" + getClass().getName() + "/matchers.xml",
					allGeneratedMatchers.getGeneratedMatcher().stream()
							.map(g -> g.getMirror().getTypeElementForClassAnnotatedWithProvideMatcher())
							.collect(Collectors.toList()).toArray(new Element[0]));
			try (OutputStream os = jfo.openOutputStream();) {
				Marshaller m = JAXBContext.newInstance(GeneratedMatchers.class).createMarshaller();
				m.setProperty("jaxb.formatted.output", true);
				m.marshal(allGeneratedMatchers, os);
			}
		} catch (IOException | JAXBException e1) {
			processingEnv.getMessager().printMessage(Kind.MANDATORY_WARNING,
					"Unable to create the file containing meta data about this generation, because of "
							+ e1.getMessage());
		}

	}

	private void processFactory() {
		try {
			processingEnv.getMessager().printMessage(Kind.NOTE,
					"The interface `" + factory + "` will be generated as a factory interface.");
			JavaFileObject jfo = processingEnv.getFiler().createSourceFile(factory,
					allGeneratedMatchers.getGeneratedMatcher().stream()
							.map(g -> g.getMirror().getTypeElementForClassAnnotatedWithProvideMatcher())
							.collect(Collectors.toList()).toArray(new Element[0]));
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
			processingEnv.getMessager().printMessage(Kind.ERROR,
					"Unable to create the file containing the target class `" + factory + "`, because of "
							+ e1.getMessage());
		}
	}

	private void processAnnotatedElements(RoundEnvironment roundEnv, TypeElement provideMatchersTE) {
		Set<? extends Element> elementsWithPM = roundEnv.getElementsAnnotatedWith(ProvideMatchers.class);
		Set<? extends Element> elementsWithIgnore = roundEnv.getElementsAnnotatedWith(IgnoreInMatcher.class);
		Set<? extends Element> elementsWithAddToMatcher = roundEnv.getElementsAnnotatedWith(AddToMatcher.class);
		Set<? extends Element> elementsWithAddToMatchers = roundEnv.getElementsAnnotatedWith(AddToMatchers.class);

		ProvidesMatchersElementVisitor providesMatchersElementVisitor = new ProvidesMatchersElementVisitor(this,
				processingEnv, provideMatchersTE);
		Map<String, ProvideMatchersAnnotatedElementMirror> alias = new HashMap<>();
		elementsWithPM.stream().filter(e -> roundEnv.getRootElements().contains(e))
				.map(e -> e.accept(providesMatchersElementVisitor, null)).filter(Optional::isPresent)
				.map(t -> new ProvideMatchersAnnotatedElementMirror(t.get(), processingEnv,
						isInSameRound(elementsWithPM, processingEnv.getTypeUtils()), (n) -> alias.get(n),
						elementsWithIgnore, elementsWithAddToMatcher, elementsWithAddToMatchers))
				.forEach(a -> alias.put(a.getFullyQualifiedNameOfClassAnnotatedWithProvideMatcher(), a));

		factories.addAll(alias.values().stream().map(ProvideMatchersAnnotatedElementMirror::process)
				.collect(Collectors.toList()));
		allGeneratedMatchers.getGeneratedMatcher().addAll(
				alias.values().stream().map(ProvideMatchersAnnotatedElementMirror::asXml).collect(Collectors.toList()));
		doWarningForElement((Set) elementsWithIgnore, IgnoreInMatcher.class);
		doWarningForElement((Set) elementsWithAddToMatcher, AddToMatcher.class);
		doWarningForElement((Set) elementsWithAddToMatchers, AddToMatchers.class);
	}

	private void doWarningForElement(Set<Element> elements, Class aa) {
		elements.stream()
				.forEach(
						e -> processingEnv.getMessager().printMessage(Kind.MANDATORY_WARNING,
								"Annotation @" + aa.getName()
										+ " not supported at this location ; The surrounding class is not annotated with @ProvideMatchers",
								e,
								e.getAnnotationMirrors().stream()
										.filter(a -> a.getAnnotationType()
												.equals(processingEnv.getElementUtils()
														.getTypeElement(aa.getName().toString()).asType()))
										.findAny().orElse(null)));
	}

	private Predicate<Element> isInSameRound(Set<? extends Element> elements, Types typesUtils) {
		return t -> t == null ? false
				: elements.stream().filter(e -> typesUtils.isSameType(e.asType(), t.asType())).findAny().isPresent();
	}

	public AnnotationMirror getProvideMatchersAnnotation(TypeElement provideMatchersTE,
			Collection<? extends AnnotationMirror> annotations) {
		return annotations.stream().filter(a -> a.getAnnotationType().equals(provideMatchersTE.asType())).findAny()
				.orElse(null);
	}
}
