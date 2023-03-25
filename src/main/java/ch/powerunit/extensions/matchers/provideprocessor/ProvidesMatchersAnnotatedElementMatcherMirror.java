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
import static ch.powerunit.extensions.matchers.common.ListJoining.joinWithMapperAndDelimiter;
import static java.lang.String.format;
import static java.util.Collections.unmodifiableList;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.reducing;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.TypeElement;

import ch.powerunit.extensions.matchers.common.CommonUtils;
import ch.powerunit.extensions.matchers.common.ListJoining;
import ch.powerunit.extensions.matchers.common.RessourceLoaderHelper;
import ch.powerunit.extensions.matchers.provideprocessor.fields.AbstractFieldDescription;
import ch.powerunit.extensions.matchers.provideprocessor.fields.FieldDescriptionMetaData;
import ch.powerunit.extensions.matchers.provideprocessor.fields.IgnoreFieldDescription;
import ch.powerunit.extensions.matchers.provideprocessor.helper.FeatureMatcher;

public abstract class ProvidesMatchersAnnotatedElementMatcherMirror
		extends ProvidesMatchersAnnotatedElementGeneralMirror {

	private static final String INTERFACE_FORMAT = RessourceLoaderHelper
			.loadRessource(ProvidesMatchersAnnotatedElementMatcherMirror.class, "MatchersInterface.txt");

	private static final String IMPLEMENTATION_NO_PARENT_FORMAT = RessourceLoaderHelper
			.loadRessource(ProvidesMatchersAnnotatedElementMatcherMirror.class, "MatchersImplementationNoParent.txt");

	private static final String IMPLEMENTATION_PARENT_FORMAT = RessourceLoaderHelper
			.loadRessource(ProvidesMatchersAnnotatedElementMatcherMirror.class, "MatchersImplementationParent.txt");

	private static final String METADATA_FORMAT = RessourceLoaderHelper
			.loadRessource(ProvidesMatchersAnnotatedElementMatcherMirror.class, "MatchersMetadata.txt");

	private static final ListJoining<AbstractFieldDescription> JOIN_FIELD_DSL_INTERFACE = joinWithMapperAndDelimiter(
			s -> addPrefix("  ", s.getDslInterface()), "\n");

	private static final ListJoining<AbstractFieldDescription> JOIN_FIELD_MATCHERFIELD = joinWithMapperAndDelimiter(
			s -> addPrefix("  ", s.asMatcherField()), "\n");

	private static final ListJoining<AbstractFieldDescription> JOIN_FIELD_IMPL = joinWithMapperAndDelimiter(
			s -> addPrefix("  ", s.getImplementationInterface()), "\n");

	private static final ListJoining<AbstractFieldDescription> JOIN_FIELD_MATCHES_SAFELY = joinWithMapperAndDelimiter(
			s -> addPrefix("    ", s.asMatchesSafely() + "\n"), "\n");

	private static final ListJoining<AbstractFieldDescription> JOIN_FIELD_DESCRIBE_TO = joinWithMapperAndDelimiter(
			s -> addPrefix("    ", s.asDescribeTo() + "\n"), "\n");

	private static final ListJoining<AbstractFieldDescription> JOIN_FIELD_MATCHER = joinWithMapperAndDelimiter(
			f -> addPrefix("  ", f.getMatcherForField()), "\n");

	private static final ListJoining<AbstractFieldDescription> JOIN_FIELD_METADATA = joinWithMapperAndDelimiter(
			f -> f.generateMetadata("FieldMetadata"), ",\n      ");

	private static final Comparator<AbstractFieldDescription> COMPARING_FIELD_BY_NAME = Comparator
			.comparing(FieldDescriptionMetaData::getFieldName);

	private static final String DEFAULT_FEATUREMATCHER_FORCONVERTER = "\n  private static <_TARGET,_SOURCE> org.hamcrest.Matcher<_SOURCE> asFeatureMatcher(\n      String msg,\n      java.util.function.Function<_SOURCE,_TARGET> converter,\n      org.hamcrest.Matcher<? super _TARGET> matcher) {\n   return new org.hamcrest.FeatureMatcher<_SOURCE,_TARGET>(matcher, msg, msg) {\n     protected _TARGET featureValueOf(_SOURCE actual) {\n      return converter.apply(actual);\n    }};\n  }\n\n";

	protected final List<AbstractFieldDescription> fields;

	private final String fieldsMatcher;

	private static AbstractFieldDescription reduceByOrderingOnIgnoreFieldDescription(AbstractFieldDescription l,
			AbstractFieldDescription r) {
		return Optional.ofNullable(l).filter(c -> c instanceof IgnoreFieldDescription).orElse(r);
	}

	private List<AbstractFieldDescription> generateFields(TypeElement typeElement,
			ProvidesMatchersSubElementVisitor providesMatchersSubElementVisitor) {
		return typeElement.getEnclosedElements().stream().map(ie -> ie.accept(providesMatchersSubElementVisitor, this))
				.filter(Optional::isPresent).map(Optional::get)
				.collect(collectingAndThen(groupingBy(FieldDescriptionMetaData::getFieldName, reducing(
						ProvidesMatchersAnnotatedElementMatcherMirror::reduceByOrderingOnIgnoreFieldDescription)),
						Map::values))
				.stream().filter(Optional::isPresent).map(Optional::get).sorted(COMPARING_FIELD_BY_NAME)
				.toList();
	}

	public ProvidesMatchersAnnotatedElementMatcherMirror(TypeElement typeElement, RoundMirror roundMirror) {
		super(typeElement, roundMirror);
		this.fields = unmodifiableList(generateFields(typeElement, new ProvidesMatchersSubElementVisitor(roundMirror)));
		this.fieldsMatcher = JOIN_FIELD_MATCHER.asString(fields) + "\n";
	}

	public String generatePublicInterface() {
		return addPrefix("  ",
				format(INTERFACE_FORMAT, getFullyQualifiedNameOfClassAnnotated(), getSimpleNameOfClassAnnotated(),
						getParamComment(), simpleNameOfGeneratedInterfaceMatcher, fullGeneric,
						getFullyQualifiedNameOfClassAnnotatedWithProvideMatcherWithGeneric(), getFullGenericParent(),
						generic, getGenericParent(), JOIN_FIELD_DSL_INTERFACE.asString(fields),getSimpleNameOfGeneratedImplementationMatcher()));
	}

	protected String generatePrivateImplementation() {
		return addPrefix("  ", format(hasSuperClass() ? IMPLEMENTATION_PARENT_FORMAT : IMPLEMENTATION_NO_PARENT_FORMAT,
				getSimpleNameOfGeneratedImplementationMatcher(), getFullGenericParent(),
				getFullyQualifiedNameOfClassAnnotatedWithProvideMatcherWithGeneric(),
				getSimpleNameOfGeneratedInterfaceMatcherWithGenericParent(), getFullyQualifiedNameOfClassAnnotated(),
				JOIN_FIELD_MATCHERFIELD.asString(fields), JOIN_FIELD_IMPL.asString(fields),
				JOIN_FIELD_MATCHES_SAFELY.asString(fields), JOIN_FIELD_DESCRIBE_TO.asString(fields),
				fullyQualifiedNameOfSuperClassOfClassAnnotated.orElse("")));

	}

	public String generateMatchers() {
		return new StringBuilder(DEFAULT_FEATUREMATCHER_FORCONVERTER).append(fieldsMatcher)
				.append(fullyQualifiedNameOfSuperClassOfClassAnnotated.map(this::generateParentMatcher).orElse(""))
				.toString();
	}

	public String generateParentMatcher(String parent) {
		return addPrefix("  ", new FeatureMatcher("SuperClass", fullGeneric, getFullyQualifiedNameOfClassAnnotated(),
				"", parent, "parent", "actual").toString());
	}

	public List<AbstractFieldDescription> getFields() {
		return fields;
	}

	public String generateMetadata() {
		ProcessingEnvironment processingEnv = getProcessingEnv();
		return format(METADATA_FORMAT, getAnnotationProcessorVersion(), getCompatibility(),
				getFullyQualifiedNameOfClassAnnotatedWithProvideMatcherWithGeneric().strip(),
				getFullyQualifiedNameOfClassAnnotated(),
				fullyQualifiedNameOfSuperClassOfClassAnnotated.map(CommonUtils::toJavaSyntax).orElse("null"),
				JOIN_FIELD_METADATA.asString(fields),
				processingEnv.getSourceVersion().name(),
				processingEnv.isPreviewEnabled());
	}

}
