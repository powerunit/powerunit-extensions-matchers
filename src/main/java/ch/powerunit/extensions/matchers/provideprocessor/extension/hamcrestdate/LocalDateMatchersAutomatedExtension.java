/**
 * 
 */
package ch.powerunit.extensions.matchers.provideprocessor.extension.hamcrestdate;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.function.Supplier;

import javax.lang.model.type.TypeMirror;

import ch.powerunit.extensions.matchers.provideprocessor.DSLMethod;
import ch.powerunit.extensions.matchers.provideprocessor.ProvidesMatchersAnnotatedElementData;
import ch.powerunit.extensions.matchers.provideprocessor.RoundMirror;
import ch.powerunit.extensions.matchers.provideprocessor.extension.AutomatedExtension;
import ch.powerunit.extensions.matchers.provideprocessor.fields.AbstractFieldDescription;
import ch.powerunit.extensions.matchers.provideprocessor.fields.FieldDSLMethod;
import ch.powerunit.extensions.matchers.provideprocessor.fields.FieldDSLMethodBuilder;

/**
 * @author borettim
 *
 */
public class LocalDateMatchersAutomatedExtension extends AutomatedExtension {

	private static final String TARGET_ELEMENT = "org.exparity.hamcrest.date.LocalDateMatchers";

	private TypeMirror knownType;

	public LocalDateMatchersAutomatedExtension(RoundMirror roundMirror) {
		super(roundMirror, TARGET_ELEMENT);
		knownType = roundMirror.getProcessingEnv().getElementUtils().getTypeElement("java.time.LocalDate").asType();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.powerunit.extensions.matchers.provideprocessor.extension.
	 * AutomatedExtension#accept(ch.powerunit.extensions.matchers.
	 * provideprocessor.fields.FieldDescriptionMetaData)
	 */
	@Override
	public Collection<FieldDSLMethod> accept(AbstractFieldDescription field) {
		if (!isSameType(field.getFieldTypeAsTypeElement(), knownType)) {
			return Collections.emptyList();
		}
		return Arrays.asList(
				FieldDSLMethodBuilder.of(field).withDeclaration("After", "java.time.LocalDate after")
						.withJavaDoc("Verify that this LocalDate is after another on",
								"after the LocalDate to compare with", TARGET_ELEMENT + "#after(java.time.LocalDate)")
				.havingDefault(TARGET_ELEMENT + ".after(after)"),
				FieldDSLMethodBuilder.of(field).withDeclaration("Before", "java.time.LocalDate before")
						.withJavaDoc("Verify that this LocalDate is before another on",
								"before the LocalDate to compare with", TARGET_ELEMENT + "#before(java.time.LocalDate)")
						.havingDefault(TARGET_ELEMENT + ".before(before)"),
				FieldDSLMethodBuilder.of(field).withDeclaration("SameDay", "java.time.LocalDate date")
						.withJavaDoc("Verify that this LocalDate is same day another on",
								"date the LocalDate to compare with", TARGET_ELEMENT + "#sameDay(java.time.LocalDate)")
						.havingDefault(TARGET_ELEMENT + ".sameDay(date)"));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.powerunit.extensions.matchers.provideprocessor.extension.
	 * AutomatedExtension#accept(ch.powerunit.extensions.matchers.
	 * provideprocessor.ProvidesMatchersAnnotatedElementData)
	 */
	@Override
	public Collection<Supplier<DSLMethod>> accept(ProvidesMatchersAnnotatedElementData clazz) {
		return Collections.emptyList();
	}

}
