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
import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableList;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toMap;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedOptions;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic.Kind;

import ch.powerunit.extensions.matchers.common.CommonUtils;
import ch.powerunit.extensions.matchers.common.FactoryHelper;
import ch.powerunit.extensions.matchers.common.FileObjectHelper;
import ch.powerunit.extensions.matchers.common.RessourceLoaderHelper;

/**
 * @author borettim
 *
 */
@SupportedAnnotationTypes({ "ch.powerunit.extensions.matchers.api.ProvideMatchers",
		"ch.powerunit.extensions.matchers.api.IgnoreInMatcher", "ch.powerunit.extensions.matchers.api.AddToMatcher",
		"ch.powerunit.extensions.matchers.api.AddToMatchers" })
@SupportedSourceVersion(SourceVersion.RELEASE_17)
@SupportedOptions({ "ch.powerunit.extensions.matchers.provideprocessor.ProvidesMatchersAnnotationsProcessor.factory" })
public class ProvidesMatchersAnnotationsProcessor extends AbstractProcessor {

	private Optional<String> factoryParam = null;

	private List<String> factories = new ArrayList<>();

	private List<Element> allSourceElements = new ArrayList<>();

	private static final List<String> POSTPROCESSOR_FORMAT = unmodifiableList(asList(addPrefix("  ",
			RessourceLoaderHelper.loadRessource(ProvidesMatchersAnnotatedElementMirror.class, "PostProcessor.txt"))
					.split("\n")));

	@Override
	public synchronized void init(ProcessingEnvironment processingEnv) {
		super.init(processingEnv);
		factoryParam = Optional.ofNullable(
				processingEnv.getOptions().get(ProvidesMatchersAnnotationsProcessor.class.getName() + ".factory"));
		if(processingEnv.getSourceVersion()!=null && processingEnv.getSourceVersion().compareTo(SourceVersion.RELEASE_17)<0) {
			processingEnv.getMessager().printMessage(Kind.ERROR, "The source version "+processingEnv.getSourceVersion()+" is not supported by this powerunit-extensions-matchers.");
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
			processRound(roundEnv);
		} else {
			processFinalRound();
		}
		return true;
	}

	private void processRound(RoundEnvironment roundEnv) {
		Collection<ProvidesMatchersAnnotatedElementMirror> alias = new RoundMirror(roundEnv, processingEnv).parse();
		factories.addAll(alias.stream()
				.collect(toMap(ProvidesMatchersAnnotatedElementMirror::getFullyQualifiedNameOfGeneratedClass,
						ProvidesMatchersAnnotatedElementMirror::process))
				.entrySet().stream().map(e -> e.getValue().stream()
						.map(m -> CommonUtils.addPrefix("  ", m.asDefaultReference(e.getKey()))).collect(joining("\n")))
				.toList());
		alias.stream().map(ProvidesMatchersAnnotatedElementMirror::getElement).forEach(allSourceElements::add);
	}

	private void processFinalRound() {
		factoryParam.ifPresent(this::processFactory);
	}

	private void processFactory(String factory) {
		FileObjectHelper.processFileWithIOException(
				() -> processingEnv.getFiler().createSourceFile(factory, allSourceElements.toArray(new Element[0])),
				jfo -> new PrintWriter(jfo.openWriter()),
				wjfo -> FactoryHelper.generateFactoryClass(wjfo, ProvidesMatchersAnnotationsProcessor.class,
						factory.replaceAll("\\.[^.]+$", ""), factory.replaceAll("^([^.]+\\.)*", ""),
						() -> Stream.concat(factories.stream(), POSTPROCESSOR_FORMAT.stream())),
				e -> processingEnv.getMessager().printMessage(Kind.ERROR,
						"Unable to create the file containing the target class `" + factory + "`, because of "
								+ e.getMessage()));
	}

}
