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
public class LocalDateTimeMatchersAutomatedExtension extends AutomatedExtension {

	private static final String TARGET_ELEMENT = "org.exparity.hamcrest.date.LocalDateTimeMatchers";

	private TypeMirror knownType;

	public LocalDateTimeMatchersAutomatedExtension(RoundMirror roundMirror) {
		super(roundMirror, TARGET_ELEMENT);
		knownType = getMirrorOr("java.time.LocalDateTime");
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
				FieldDSLMethodBuilder.of(field).withDeclaration("After", "java.time.LocalDateTime after")
						.withJavaDoc("Verify that this LocalDateTime is after another one",
								"after the LocalDate to compare with",
								TARGET_ELEMENT + "#after(java.time.LocalDateTime)")
				.havingDefault(TARGET_ELEMENT + ".after(after)"),
				FieldDSLMethodBuilder.of(field).withDeclaration("SameOrAfter", "java.time.LocalDateTime date")
						.withJavaDoc("Verify that this LocalDateTime is after or same another one",
								"date the LocalDate to compare with",
								TARGET_ELEMENT + "#sameOrAfter(java.time.LocalDateTime)")
						.havingDefault(TARGET_ELEMENT + ".sameOrAfter(date)"),
				FieldDSLMethodBuilder.of(field).withDeclaration("Before", "java.time.LocalDateTime before")
						.withJavaDoc("Verify that this LocalDateTime is before another one",
								"before the LocalDate to compare with",
								TARGET_ELEMENT + "#before(java.time.LocalDateTime)")
						.havingDefault(TARGET_ELEMENT + ".before(before)"),
				FieldDSLMethodBuilder.of(field).withDeclaration("SameorBefore", "java.time.LocalDateTime date")
						.withJavaDoc("Verify that this LocalDateTime is same or before another one",
								"date the LocalDate to compare with",
								TARGET_ELEMENT + "#sameOrBefore(java.time.LocalDateTime)")
						.havingDefault(TARGET_ELEMENT + ".sameOrBefore(date)"),
				FieldDSLMethodBuilder.of(field).withDeclaration("SameDay", "java.time.LocalDateTime date")
						.withJavaDoc("Verify that this LocalDateTime is same day another one",
								"date the LocalDate to compare with",
								TARGET_ELEMENT + "#sameDay(java.time.LocalDateTime)")
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
