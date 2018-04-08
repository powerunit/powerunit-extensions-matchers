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

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic.Kind;

import ch.powerunit.extensions.matchers.AddToMatcher;
import ch.powerunit.extensions.matchers.AddToMatchers;
import ch.powerunit.extensions.matchers.IgnoreInMatcher;
import ch.powerunit.extensions.matchers.ProvideMatchers;

public class RoundMirror {

	private final RoundEnvironment roundEnv;
	private final ProcessingEnvironment processingEnv;
	private final Set<? extends Element> elementsWithPM;
	private final Set<? extends Element> elementsWithIgnore;
	private final Set<? extends Element> elementsWithAddToMatcher;
	private final Set<? extends Element> elementsWithAddToMatchers;
	private final TypeElement provideMatchersTE;
	private final Map<String, ProvidesMatchersAnnotatedElementMirror> alias = new HashMap<>();

	public RoundMirror(RoundEnvironment roundEnv, ProcessingEnvironment processingEnv) {
		this.roundEnv = roundEnv;
		this.processingEnv = processingEnv;
		this.elementsWithPM = roundEnv.getElementsAnnotatedWith(ProvideMatchers.class);
		this.elementsWithIgnore = roundEnv.getElementsAnnotatedWith(IgnoreInMatcher.class);
		this.elementsWithAddToMatcher = roundEnv.getElementsAnnotatedWith(AddToMatcher.class);
		this.elementsWithAddToMatchers = roundEnv.getElementsAnnotatedWith(AddToMatchers.class);
		this.provideMatchersTE = processingEnv.getElementUtils()
				.getTypeElement("ch.powerunit.extensions.matchers.ProvideMatchers");
	}

	public ProcessingEnvironment getProcessingEnv() {
		return processingEnv;
	}

	public TypeElement getProvideMatchersTE() {
		return provideMatchersTE;
	}

	public Collection<ProvidesMatchersAnnotatedElementMirror> parse() {
		ProvidesMatchersElementVisitor providesMatchersElementVisitor = new ProvidesMatchersElementVisitor(this);
		elementsWithPM.stream().filter(e -> roundEnv.getRootElements().contains(e))
				.map(e -> e.accept(providesMatchersElementVisitor, null)).filter(Optional::isPresent)
				.map(t -> new ProvidesMatchersAnnotatedElementMirror(t.get(), this))
				.forEach(a -> alias.put(a.getFullyQualifiedNameOfClassAnnotatedWithProvideMatcher(), a));

		doWarningForElement(elementsWithIgnore, IgnoreInMatcher.class);
		doWarningForElement(elementsWithAddToMatcher, AddToMatcher.class);
		doWarningForElement(elementsWithAddToMatchers, AddToMatchers.class);
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
		return Arrays.stream(new Set[] { elementsWithIgnore, elementsWithAddToMatcher, elementsWithAddToMatchers })
				.map(t -> t.remove(e)).filter(t -> t).findAny().orElse(false);
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
		return getProcessingEnv().getElementUtils().getAllAnnotationMirrors(e).stream()
				.filter(a -> a.getAnnotationType().equals(provideMatchersTE.asType())).findAny().orElse(null);
	}

}
