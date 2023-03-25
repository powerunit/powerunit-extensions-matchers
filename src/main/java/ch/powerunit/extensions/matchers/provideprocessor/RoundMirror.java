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

import static ch.powerunit.extensions.matchers.common.CommonUtils.asStandardMethodName;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;

import static javax.lang.model.util.ElementFilter.typesIn;
import static javax.lang.model.util.ElementFilter.fieldsIn;
import static javax.lang.model.util.ElementFilter.methodsIn;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.tools.Diagnostic.Kind;

import ch.powerunit.extensions.matchers.api.AddToMatcher;
import ch.powerunit.extensions.matchers.api.AddToMatchers;
import ch.powerunit.extensions.matchers.api.IgnoreInMatcher;
import ch.powerunit.extensions.matchers.api.ProvideMatchers;
import ch.powerunit.extensions.matchers.common.AbstractRoundMirrorReferenceToProcessingEnv;
import ch.powerunit.extensions.matchers.provideprocessor.dsl.DSLMethod;
import ch.powerunit.extensions.matchers.provideprocessor.extension.AutomatedExtension;
import ch.powerunit.extensions.matchers.provideprocessor.extension.beanmatchers.DefaultBeanMatchersAutomatedExtension;
import ch.powerunit.extensions.matchers.provideprocessor.extension.hamcrestdate.LocalDateMatchersAutomatedExtension;
import ch.powerunit.extensions.matchers.provideprocessor.extension.hamcrestdate.LocalDateTimeMatchersAutomatedExtension;
import ch.powerunit.extensions.matchers.provideprocessor.extension.hamcrestdate.LocalTimeMatchersAutomatedExtension;
import ch.powerunit.extensions.matchers.provideprocessor.extension.hamcrestdate.ZonedDateTimeMatchersAutomatedExtension;
import ch.powerunit.extensions.matchers.provideprocessor.extension.hamcrestutility.CollectionHamcrestUtilityAutomatedExtension;
import ch.powerunit.extensions.matchers.provideprocessor.extension.jackson.DefaultJsonNodeJacksonAutomatedExtension;
import ch.powerunit.extensions.matchers.provideprocessor.extension.spotify.JsonStringSpotifyAutomatedExtension;
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
				new HashSet<>(roundEnv.getElementsAnnotatedWith(IgnoreInMatcher.class)));
		elementsWithOtherAnnotations.put(AddToMatcher.class,
				new HashSet<>(roundEnv.getElementsAnnotatedWith(AddToMatcher.class)));
		elementsWithOtherAnnotations.put(AddToMatchers.class,
				new HashSet<>(roundEnv.getElementsAnnotatedWith(AddToMatchers.class)));
		AUTOMATED_EXTENSIONS = getDefaultExtension().stream().filter(AutomatedExtension::isPresent)
				.collect(collectingAndThen(toList(), Collections::unmodifiableList));
	}

	private final List<AutomatedExtension> getDefaultExtension() {
		return Arrays.asList(new LocalDateMatchersAutomatedExtension(this),
				new LocalDateTimeMatchersAutomatedExtension(this), new LocalTimeMatchersAutomatedExtension(this),
				new ZonedDateTimeMatchersAutomatedExtension(this),
				new CollectionHamcrestUtilityAutomatedExtension(this), new JsonStringSpotifyAutomatedExtension(this),
				new DefaultBeanMatchersAutomatedExtension(this), new DefaultJsonNodeJacksonAutomatedExtension(this));
	}

	public Collection<ProvidesMatchersAnnotatedElementMirror> parse() {
		ProvidesMatchersElementVisitor providesMatchersElementVisitor = new ProvidesMatchersElementVisitor(this);
		elementsWithPM.stream().filter(e -> roundEnv.getRootElements().contains(e))
				.map(e -> e.accept(providesMatchersElementVisitor, null)).filter(Optional::isPresent)
				.map(t -> new ProvidesMatchersAnnotatedElementMirror(t.get(), this))
				.forEach(a -> alias.put(a.getFullyQualifiedNameOfClassAnnotated(), a));

		doErrorforAllElements();
		return alias.values();
	}

	private void doErrorforAllElements() {
		elementsWithOtherAnnotations.entrySet().stream().forEach(e -> doErrorForElement(e.getValue(), e.getKey()));
	}

	private void doErrorForElement(Set<? extends Element> elements, Class<?> aa) {
		elements.stream().forEach(e -> getMessager().printMessage(Kind.ERROR, "Annotation @" + aa.getName()
				+ " not supported at this location ; The surrounding class is not annotated with @ProvideMatchers. Since version 0.2.0 of powerunit-extension-matchers this is considered as an error.",
				e, findAnnotationMirrorFor(e, aa)));
	}

	private AnnotationMirror findAnnotationMirrorFor(Element e, Class<?> aa) {
		String aaName = aa.getName().toString();
		return e.getAnnotationMirrors().stream()
				.filter(a -> a.getAnnotationType().equals(getElementUtils().getTypeElement(aaName).asType())).findAny()
				.orElse(null);
	}

	public boolean removeFromIgnoreList(Element e) {
		return elementsWithOtherAnnotations.values().stream().map(t -> t.remove(e)).filter(t -> t).findAny()
				.orElse(false);
	}

	public Matchable getByName(String name) {
		return Optional.ofNullable((Matchable) alias.get(name)).orElseGet(() -> Optional
				.ofNullable(getElementUtils().getTypeElement(name)).map(this::lookupMatchableByType).orElse(null));

	}

	public Matchable lookupMatchableByType(TypeElement type) {
		return Optional.ofNullable(getElementUtils().getTypeElement(getQualifiedName(type) + "Matchers"))
				.map(m -> lookupMatchableByTypeAndMatchers(type, m)).orElse(null);
	}

	private Matchable lookupMatchableByTypeAndMatchers(TypeElement type, TypeElement guestMatcher) {
		List<? extends Element> guestMatcherEnclosed = guestMatcher.getEnclosedElements();
		List<TypeElement> types = typesIn(guestMatcherEnclosed);
		Optional<Long> compatibilityField = types.stream().filter(t -> isSimpleName(t, "Metadata"))
				.flatMap(t -> fieldsIn(t.getEnclosedElements()).stream()).filter(t -> isSimpleName(t, "COMPATIBILITY"))
				.map(VariableElement::getConstantValue).filter(Objects::nonNull).filter(Long.class::isInstance)
				.map(Long.class::cast).findAny();
		if (!compatibilityField.isPresent()) {
			return null;// NOT FOUND
		}
		// In future, verify compatiblity
		String guestMatcherName = getSimpleName(type) + "Matcher";
		if (!types.stream().anyMatch(t -> isSimpleName(t, guestMatcherName))) {
			return null;// NOT FOUND
		}
		String shortMethodClassName = asStandardMethodName(getSimpleName(type));
		String withSameValue = shortMethodClassName + "WithSameValue";
		boolean hasSameValue = methodsIn(guestMatcherEnclosed).stream().filter(t -> isSimpleName(t, withSameValue))
				.anyMatch(this::isStatic);
		return Matchable.of(getQualifiedName(guestMatcher), shortMethodClassName, guestMatcherName, hasSameValue,
				compatibilityField.get());
	}

	public AnnotationMirror getProvideMatchersAnnotation(Element e) {
		return getElementUtils().getAllAnnotationMirrors(e).stream()
				.filter(a -> a.getAnnotationType().equals(provideMatchersMirror)).findAny().orElse(null);
	}

	public Collection<Supplier<DSLMethod>> getDSLMethodFor(ProvidesMatchersAnnotatedElementData target) {
		return AUTOMATED_EXTENSIONS.stream().map(ae -> ae.accept(target)).flatMap(Collection::stream).collect(toList());
	}

	public Collection<FieldDSLMethod> getFieldDSLMethodFor(AbstractFieldDescription target) {
		return AUTOMATED_EXTENSIONS.stream().map(ae -> ae.accept(target)).flatMap(Collection::stream).collect(toList());
	}

}
