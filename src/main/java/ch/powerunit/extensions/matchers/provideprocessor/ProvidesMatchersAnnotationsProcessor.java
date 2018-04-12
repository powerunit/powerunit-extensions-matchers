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

import static java.util.stream.Collectors.toMap;

import java.io.Closeable;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedOptions;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic.Kind;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import ch.powerunit.extensions.matchers.common.CommonUtils;
import ch.powerunit.extensions.matchers.provideprocessor.xml.GeneratedMatcher;
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

	private Optional<String> factory = null;

	private Map<String, GeneratedMatcher> allGeneratedMatchers = new HashMap<>();

	private JAXBContext context;

	@Override
	public synchronized void init(ProcessingEnvironment processingEnv) {
		super.init(processingEnv);
		try {
			context = JAXBContext.newInstance(GeneratedMatchers.class);
		} catch (JAXBException e) {
			processingEnv.getMessager().printMessage(Kind.ERROR, "Unable to open prepare jaxb " + e.getMessage());
		}
		factory = Optional.ofNullable(
				processingEnv.getOptions().get(ProvidesMatchersAnnotationsProcessor.class.getName() + ".factory"));
		factory.ifPresent(f -> retrieveInformationFromOldBuild());
	}

	private void retrieveInformationFromOldBuild() {
		try {
			FileObject matchers = processingEnv.getFiler().getResource(StandardLocation.SOURCE_OUTPUT, "",
					"META-INF/" + getClass().getName() + "/matchers.xml");
			if (matchers.getLastModified() != 0) {
				try (InputStream is = matchers.openInputStream()) {
					Unmarshaller m = context.createUnmarshaller();
					GeneratedMatchers gm = (GeneratedMatchers) m.unmarshal(is);
					gm.getGeneratedMatcher()
							.forEach(mo -> allGeneratedMatchers.put(mo.getFullyQualifiedNameGeneratedClass(), mo));
				}
			}
		} catch (Exception e) {
			processingEnv.getMessager().printMessage(Kind.ERROR, "Unable to open matchers.xml file " + e.getMessage());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.annotation.processing.AbstractProcessor#process(java.util.Set,
	 * javax.annotation.processing.RoundEnvironment)
	 */
	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		if (!roundEnv.processingOver()) {
			boolean processed = processBuildRound(roundEnv);
			if (processed) {
				processFinalRound();
			}
		}
		return true;

	}

	private void processFinalRound() {
		GeneratedMatchers gm = new GeneratedMatchers();
		gm.setGeneratedMatcher(new ArrayList<>(allGeneratedMatchers.values()));
		processReportXML(gm);
		factory.ifPresent(f -> processFactory());
	}

	private boolean processBuildRound(RoundEnvironment roundEnv) {
		Collection<ProvidesMatchersAnnotatedElementMirror> alias = new RoundMirror(roundEnv, processingEnv).parse();
		allGeneratedMatchers.putAll(alias.stream()
				.collect(toMap(ProvidesMatchersAnnotatedElementMirror::getFullyQualifiedNameOfGeneratedClass,
						ProvidesMatchersAnnotatedElementMirror::process)));
		return !alias.isEmpty();
	}

	@FunctionalInterface
	public static interface ConsumerWithException<S> {
		void accept(S input) throws Exception;
	}

	@FunctionalInterface
	public static interface SupplierWithException<T> {
		T get() throws Exception;
	}

	@FunctionalInterface
	public static interface FunctionWithException<T, R> {
		R apply(T input) throws Exception;
	}

	public <T extends FileObject, S extends Closeable> boolean processFileWithIOException(
			SupplierWithException<T> generateFileObject, FunctionWithException<T, S> openStream,
			ConsumerWithException<S> actions) {
		try {
			try (S wjfo = openStream.apply(generateFileObject.get())) {
				actions.accept(wjfo);
			}
		} catch (Exception e) {
			processingEnv.getMessager().printMessage(Kind.ERROR,
					"Unable to create a file, because of " + e.getMessage());
			return false;
		}
		return true;
	}

	private void processReportXML(GeneratedMatchers gm) {
		processFileWithIOException(() -> processingEnv.getFiler().createResource(StandardLocation.SOURCE_OUTPUT, "",
				"META-INF/" + getClass().getName() + "/matchers.xml"), FileObject::openOutputStream, os -> {
					Marshaller m = context.createMarshaller();
					m.setProperty("jaxb.formatted.output", true);
					m.marshal(gm, os);
				});
	}

	private void processFactory() {
		String target = factory.get();
		processFileWithIOException(() -> processingEnv.getFiler().createSourceFile(target),
				jfo -> new PrintWriter(jfo.openWriter()),
				wjfo -> CommonUtils.generateFactoryClass(wjfo, ProvidesMatchersAnnotationsProcessor.class,
						target.replaceAll("\\.[^.]+$", ""), target.replaceAll("^([^.]+\\.)*", ""),
						() -> allGeneratedMatchers.values().stream().map(GeneratedMatcher::getFactories)));
	}

	public AnnotationMirror getProvideMatchersAnnotation(TypeElement provideMatchersTE,
			Collection<? extends AnnotationMirror> annotations) {
		return annotations.stream().filter(a -> a.getAnnotationType().equals(provideMatchersTE.asType())).findAny()
				.orElse(null);
	}
}
