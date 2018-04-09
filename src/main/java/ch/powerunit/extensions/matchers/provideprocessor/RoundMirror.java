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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic.Kind;

import ch.powerunit.extensions.matchers.AddToMatcher;
import ch.powerunit.extensions.matchers.AddToMatchers;
import ch.powerunit.extensions.matchers.IgnoreInMatcher;
import ch.powerunit.extensions.matchers.ProvideMatchers;

public class RoundMirror {

	private final RoundEnvironment roundEnv;
	private final ProcessingEnvironment processingEnv;
	private final Set<? extends Element> elementsWithPM;
	private final Map<Class<?>, Set<? extends Element>> elementsWithOtherAnnotations;
	private final Map<String, ProvidesMatchersAnnotatedElementMirror> alias = new HashMap<>();

	public RoundMirror(RoundEnvironment roundEnv, ProcessingEnvironment processingEnv) {
		this.roundEnv = roundEnv;
		this.processingEnv = processingEnv;
		this.elementsWithOtherAnnotations = new HashMap<>();
		this.elementsWithPM = roundEnv.getElementsAnnotatedWith(ProvideMatchers.class);
		elementsWithOtherAnnotations.put(IgnoreInMatcher.class,
				roundEnv.getElementsAnnotatedWith(IgnoreInMatcher.class));
		elementsWithOtherAnnotations.put(AddToMatcher.class, roundEnv.getElementsAnnotatedWith(AddToMatcher.class));
		elementsWithOtherAnnotations.put(AddToMatchers.class, roundEnv.getElementsAnnotatedWith(AddToMatchers.class));
	}

	public ProcessingEnvironment getProcessingEnv() {
		return processingEnv;
	}

	public Collection<ProvidesMatchersAnnotatedElementMirror> parse() {
		ProvidesMatchersElementVisitor providesMatchersElementVisitor = new ProvidesMatchersElementVisitor(this);
		elementsWithPM.stream().filter(e -> roundEnv.getRootElements().contains(e))
				.map(e -> e.accept(providesMatchersElementVisitor, null)).filter(Optional::isPresent)
				.map(t -> new ProvidesMatchersAnnotatedElementMirror(t.get(), this))
				.forEach(a -> alias.put(a.getFullyQualifiedNameOfClassAnnotatedWithProvideMatcher(), a));

		elementsWithOtherAnnotations.entrySet().stream().forEach(e -> doWarningForElement(e.getValue(), e.getKey()));
		return alias.values();
	}

	private void doWarningForElement(Set<? extends Element> elements, Class<?> aa) {
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

	public boolean removeFromIgnoreList(Element e) {
		return elementsWithOtherAnnotations.values().stream().map(t -> t.remove(e)).filter(t -> t).findAny()
				.orElse(false);
	}

	public ProvidesMatchersAnnotatedElementMirror getByName(String name) {
		return alias.get(name);
	}

	public boolean isInSameRound(Element t) {
		return t == null ? false
				: elementsWithPM.stream().filter(e -> processingEnv.getTypeUtils().isSameType(e.asType(), t.asType()))
						.findAny().isPresent();
	}

	public AnnotationMirror getProvideMatchersAnnotation(Element e) {
		TypeMirror pmtm = processingEnv.getElementUtils()
				.getTypeElement("ch.powerunit.extensions.matchers.ProvideMatchers").asType();
		return getProcessingEnv().getElementUtils().getAllAnnotationMirrors(e).stream()
				.filter(a -> a.getAnnotationType().equals(pmtm)).findAny().orElse(null);
	}

}
