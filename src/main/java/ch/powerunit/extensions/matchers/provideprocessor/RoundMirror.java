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

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

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
import ch.powerunit.extensions.matchers.provideprocessor.extension.AutomatedExtension;
import ch.powerunit.extensions.matchers.provideprocessor.extension.hamcrestdate.LocalDateMatchersAutomatedExtension;
import ch.powerunit.extensions.matchers.provideprocessor.extension.hamcrestdate.LocalDateTimeMatchersAutomatedExtension;
import ch.powerunit.extensions.matchers.provideprocessor.extension.hamcrestdate.LocalTimeMatchersAutomatedExtension;
import ch.powerunit.extensions.matchers.provideprocessor.extension.hamcrestdate.ZonedDateTimeMatchersAutomatedExtension;
import ch.powerunit.extensions.matchers.provideprocessor.extension.hamcrestutility.CollectionHamcrestUtilityAutomatedExtension;
import ch.powerunit.extensions.matchers.provideprocessor.fields.AbstractFieldDescription;
import ch.powerunit.extensions.matchers.provideprocessor.fields.FieldDSLMethod;

public class RoundMirror extends AbstractRoundMirrorReferenceToProcessingEnv {

	private final Collection<AutomatedExtension> AUTOMATED_EXTENSIONS;
	private final Set<? extends Element> elementsWithPM;
	private final Map<Class<?>, Set<? extends Element>> elementsWithOtherAnnotations;
	private final Map<String, ProvidesMatchersAnnotatedElementMirror> alias = new HashMap<>();

	public RoundMirror(RoundEnvironment roundEnv, ProcessingEnvironment processingEnv) {
		super(roundEnv, processingEnv);
		this.elementsWithOtherAnnotations = new HashMap<>();
		this.elementsWithPM = roundEnv.getElementsAnnotatedWith(ProvideMatchers.class);
		elementsWithOtherAnnotations.put(IgnoreInMatcher.class,
				roundEnv.getElementsAnnotatedWith(IgnoreInMatcher.class));
		elementsWithOtherAnnotations.put(AddToMatcher.class, roundEnv.getElementsAnnotatedWith(AddToMatcher.class));
		elementsWithOtherAnnotations.put(AddToMatchers.class, roundEnv.getElementsAnnotatedWith(AddToMatchers.class));
		AUTOMATED_EXTENSIONS = getDefaultExtension().stream().filter(AutomatedExtension::isPresent)
				.collect(collectingAndThen(toList(), Collections::unmodifiableList));
	}

	private final List<AutomatedExtension> getDefaultExtension() {
		return Arrays.asList(new LocalDateMatchersAutomatedExtension(this),
				new LocalDateTimeMatchersAutomatedExtension(this), new LocalTimeMatchersAutomatedExtension(this),
				new ZonedDateTimeMatchersAutomatedExtension(this),
				new CollectionHamcrestUtilityAutomatedExtension(this));
	}

	public Collection<ProvidesMatchersAnnotatedElementMirror> parse() {
		ProvidesMatchersElementVisitor providesMatchersElementVisitor = new ProvidesMatchersElementVisitor(this);
		elementsWithPM.stream().filter(e -> roundEnv.getRootElements().contains(e))
				.map(e -> e.accept(providesMatchersElementVisitor, null)).filter(Optional::isPresent)
				.map(t -> new ProvidesMatchersAnnotatedElementMirror(t.get(), this))
				.forEach(a -> alias.put(a.getFullyQualifiedNameOfClassAnnotatedWithProvideMatcher(), a));

		doWarningforAllElements();
		return alias.values();
	}

	private void doWarningforAllElements() {
		elementsWithOtherAnnotations.entrySet().stream().forEach(e -> doWarningForElement(e.getValue(), e.getKey()));
	}

	private void doWarningForElement(Set<? extends Element> elements, Class<?> aa) {
		elements.stream()
				.forEach(e -> processingEnv.getMessager().printMessage(Kind.MANDATORY_WARNING,
						"Annotation @" + aa.getName()
								+ " not supported at this location ; The surrounding class is not annotated with @ProvideMatchers",
						e, findAnnotationMirrorFor(e, aa)));
	}

	private AnnotationMirror findAnnotationMirrorFor(Element e, Class<?> aa) {
		String aaName = aa.getName().toString();
		return e.getAnnotationMirrors().stream()
				.filter(a -> a.getAnnotationType()
						.equals(processingEnv.getElementUtils().getTypeElement(aaName).asType()))
				.findAny().orElse(null);
	}

	public boolean removeFromIgnoreList(Element e) {
		return elementsWithOtherAnnotations.values().stream().map(t -> t.remove(e)).filter(t -> t).findAny()
				.orElse(false);
	}

	public ProvidesMatchersAnnotatedElementMirror getByName(String name) {
		return alias.get(name);
	}

	public boolean isInSameRound(Element t) {
		if (t == null) {
			return false;
		}
		TypeMirror tm = t.asType();
		return elementsWithPM.stream().filter(e -> processingEnv.getTypeUtils().isSameType(e.asType(), tm)).findAny()
				.isPresent();
	}

	public AnnotationMirror getProvideMatchersAnnotation(Element e) {
		TypeMirror pmtm = processingEnv.getElementUtils()
				.getTypeElement("ch.powerunit.extensions.matchers.ProvideMatchers").asType();
		return getProcessingEnv().getElementUtils().getAllAnnotationMirrors(e).stream()
				.filter(a -> a.getAnnotationType().equals(pmtm)).findAny().orElse(null);
	}

	public Collection<Supplier<DSLMethod>> getDSLMethodFor(ProvidesMatchersAnnotatedElementData target) {
		return AUTOMATED_EXTENSIONS.stream().map(ae -> ae.accept(target)).flatMap(Collection::stream).collect(toList());
	}

	public Collection<FieldDSLMethod> getFieldDSLMethodFor(AbstractFieldDescription target) {
		return AUTOMATED_EXTENSIONS.stream().map(ae -> ae.accept(target)).flatMap(Collection::stream).collect(toList());
	}

}
