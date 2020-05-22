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
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.reducing;
import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.Optional;

import javax.lang.model.element.TypeElement;

import ch.powerunit.extensions.matchers.common.CommonUtils;
import ch.powerunit.extensions.matchers.common.ListJoining;
import ch.powerunit.extensions.matchers.provideprocessor.fields.AbstractFieldDescription;
import ch.powerunit.extensions.matchers.provideprocessor.fields.FieldDescriptionMetaData;
import ch.powerunit.extensions.matchers.provideprocessor.fields.IgnoreFieldDescription;
import ch.powerunit.extensions.matchers.provideprocessor.xml.GeneratedMatcher;

public abstract class ProvidesMatchersAnnotatedElementFieldMatcherMirror
		extends ProvidesMatchersAnnotatedElementGeneralMirror {

	private static final String DEFAULT_FEATUREMATCHER_FORCONVERTER = "\n  private static <_TARGET,_SOURCE> org.hamcrest.Matcher<_SOURCE> asFeatureMatcher(\n      String msg,\n      java.util.function.Function<_SOURCE,_TARGET> converter,\n      org.hamcrest.Matcher<? super _TARGET> matcher) {\n   return new org.hamcrest.FeatureMatcher<_SOURCE,_TARGET>(matcher, msg, msg) {\n     protected _TARGET featureValueOf(_SOURCE actual) {\n      return converter.apply(actual);\n    }};\n  }\n\n";

	protected final List<AbstractFieldDescription> fields;

	private final String fieldsMatcher;

	private List<AbstractFieldDescription> generateFields(TypeElement typeElement,
			ProvidesMatchersSubElementVisitor providesMatchersSubElementVisitor) {
		return typeElement
				.getEnclosedElements().stream().map(
						ie -> ie.accept(providesMatchersSubElementVisitor, this))
				.filter(Optional::isPresent).map(
						Optional::get)
				.collect(collectingAndThen(
						groupingBy(FieldDescriptionMetaData::getFieldName,
								reducing(null,
										(v1, v2) -> v1 == null ? v2 : v1 instanceof IgnoreFieldDescription ? v1 : v2)),
						c -> c == null ? emptyList() : c.values().stream().collect(toList())));
	}

	public ProvidesMatchersAnnotatedElementFieldMatcherMirror(TypeElement typeElement, RoundMirror roundMirror) {
		super(typeElement, roundMirror);
		this.fields = generateFields(typeElement, new ProvidesMatchersSubElementVisitor(roundMirror));
		this.fieldsMatcher = fields.stream().map(FieldDescriptionMetaData::getMatcherForField)
				.map(f -> addPrefix("  ", f)).collect(joining("\n")) + "\n";
	}

	public String generateMatchers() {
		return new StringBuilder(DEFAULT_FEATUREMATCHER_FORCONVERTER).append(fieldsMatcher)
				.append(fullyQualifiedNameOfSuperClassOfClassAnnotated.map(this::generateParentMatcher).orElse(""))
				.toString();
	}

	public String generateParentMatcher(String parent) {
		return String.format(
				"  private static class SuperClassMatcher%1$s extends org.hamcrest.FeatureMatcher<%2$s,%3$s> {\n\n    public SuperClassMatcher(org.hamcrest.Matcher<? super %3$s> matcher) {\n      super(matcher,\"parent\",\"parent\");\n  }\n\n\n    protected %3$s featureValueOf(%2$s actual) {\n      return actual;\n    }\n\n  }\n\n\n",
				fullGeneric, getFullyQualifiedNameOfClassAnnotated(), parent);
	}

	public GeneratedMatcher asXml() {
		GeneratedMatcher gm = new GeneratedMatcher();
		gm.setFullyQualifiedNameGeneratedClass(getFullyQualifiedNameOfGeneratedClass());
		gm.setFullyQualifiedNameInputClass(getFullyQualifiedNameOfClassAnnotated());
		gm.setSimpleNameGeneratedClass(getSimpleNameOfGeneratedClass());
		gm.setSimpleNameInputClass(getSimpleNameOfClassAnnotated());
		gm.setDslMethodNameStart(methodShortClassName);
		gm.setGeneratedMatcherField(
				fields.stream().map(AbstractFieldDescription::asGeneratedMatcherField).collect(toList()));
		gm.setElement(getElement());
		return gm;
	}

	public String generateMetadata() {
		// @formatter:off
		return "\n\n"
				+ "  // ---------------------------------------------------------------------------\n"
				+ "  // METADATA\n\n"
				+ "  /**\n"
		        + "   * Metadata regarding this matcher.\n"
				+ "   */\n"
				+ "  public static final Metadata METADATA = new Metadata();\n\n"
				+"   public static final class FieldMetadata {\n    public final String NAME, TYPE, ACCESSOR, STEREOTYPE;\n    public final boolean IGNORED;\n    private FieldMetadata(String name, String type, String accessor,String stereotype, boolean ignored) {\n      NAME=name; TYPE=type; ACCESSOR=accessor; STEREOTYPE=stereotype; IGNORED=ignored;\n    }\n  }\n\n"
				+ "  public static final class Metadata {\n\n"
				+ "    private Metadata() {}\n\n"
				+ "    public final String ANNOTATION_PROCESSOR_VERSION = \"" + getAnnotationProcessorVersion() + "\";\n\n"
				+ "    public final String SOURCE_CLASS_NAME = \"" + getFullyQualifiedNameOfClassAnnotatedWithProvideMatcherWithGeneric() + "\";\n\n"
				+ "    public final Class<"+getFullyQualifiedNameOfClassAnnotated()+"> SOURCE_CLASS = " + getFullyQualifiedNameOfClassAnnotated() + ".class;\n\n"
				+ "    public final String SOURCE_PARENT_CLASS_NAME = " + fullyQualifiedNameOfSuperClassOfClassAnnotated.map(CommonUtils::toJavaSyntax).orElse("null") + ";\n\n"
				+ "    public final FieldMetadata[] FIELDS = new FieldMetadata[]{ " +ListJoining.<AbstractFieldDescription>joinWithMapperAndDelimiter(f->f.generateMetadata("FieldMetadata"), ", ").asString(fields) + " };\n\n"
				+ "  }\n";
		// @formatter:on
	}

}
