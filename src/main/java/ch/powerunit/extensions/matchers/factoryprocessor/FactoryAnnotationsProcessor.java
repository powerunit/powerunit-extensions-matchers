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
package ch.powerunit.extensions.matchers.factoryprocessor;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

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

import org.hamcrest.Factory;

@SupportedAnnotationTypes({ "org.hamcrest.Factory" })
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedOptions({ "ch.powerunit.extensions.matchers.factoryprocessor.FactoryAnnotationsProcessor.targets" })
public class FactoryAnnotationsProcessor extends AbstractProcessor implements ProcessingEnvironment {

	private List<FactoryGroup> build;

	private TypeElement factoryAnnotationTE;

	@Override
	public synchronized void init(ProcessingEnvironment processingEnv) {
		super.init(processingEnv);
		String targets = processingEnv.getOptions().get(FactoryAnnotationsProcessor.class.getName() + ".targets");
		if (targets == null || targets.trim().equals("")) {
			processingEnv.getMessager().printMessage(Kind.MANDATORY_WARNING, "The parameter `"
					+ FactoryAnnotationsProcessor.class.getName() + ".targets` is missing, please use it.");
			build = Collections.emptyList();
		} else {
			build = Arrays.stream(targets.split("\\s*;\\s*")).map(e -> new FactoryGroup(this, e))
					.collect(collectingAndThen(toList(), Collections::unmodifiableList));
		}
		factoryAnnotationTE = processingEnv.getElementUtils().getTypeElement("org.hamcrest.Factory");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.annotation.processing.AbstractProcessor#process(java.util.Set,
	 * javax.annotation.processing.RoundEnvironment)
	 */
	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		if (build.isEmpty()) {
			return false;
		}

		if (!roundEnv.processingOver()) {
			processFactoryAnnotation(roundEnv);
		} else {
			processGenerationOfFinalClasses();
		}
		return true;
	}

	public void processGenerationOfFinalClasses() {
		build.forEach(FactoryGroup::processGenerateOneFactoryInterface);
	}

	public void processFactoryAnnotation(RoundEnvironment roundEnv) {
		Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(Factory.class);
		FactoryElementVisitor factoryElementVisitor = new FactoryElementVisitor();
		elements.stream().filter(e -> roundEnv.getRootElements().contains(e.getEnclosingElement()))
				.forEach(e -> e
						.accept(factoryElementVisitor, this).map(
								ee -> new FactoryAnnotatedElementMirror(this, ee))
				.ifPresent(faem -> build.stream().filter(f -> f.isAccepted(faem)).forEach(f -> f.addMethod(faem))));
	}

	public AnnotationMirror getFactoryAnnotation(Element e) {
		return getElementUtils().getAllAnnotationMirrors(e).stream()
				.filter(a -> a.getAnnotationType().equals(factoryAnnotationTE.asType())).findAny().orElse(null);
	}

	@Override
	public Elements getElementUtils() {
		return processingEnv.getElementUtils();
	}

	@Override
	public Filer getFiler() {
		return processingEnv.getFiler();
	}

	@Override
	public Locale getLocale() {
		return processingEnv.getLocale();
	}

	@Override
	public Messager getMessager() {
		return processingEnv.getMessager();
	}

	@Override
	public Map<String, String> getOptions() {
		return processingEnv.getOptions();
	}

	@Override
	public SourceVersion getSourceVersion() {
		return processingEnv.getSourceVersion();
	}

	@Override
	public Types getTypeUtils() {
		return processingEnv.getTypeUtils();
	}
}
