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
import ch.powerunit.extensions.matchers.provideprocessor.fields.lang.BuilderJavadoc;

/**
 * @author borettim
 *
 */
public abstract class AbstractHamcrestDateMatchersAutomatedExtension extends AutomatedExtension {

	private final TypeMirror knownType;

	private final String targetType;

	public AbstractHamcrestDateMatchersAutomatedExtension(RoundMirror roundMirror, String targetElement,
			String targetType) {
		super(roundMirror, targetElement);
		this.targetType = targetType;
		knownType = getMirrorOr(targetType);
	}

	private BuilderJavadoc builderFor(AbstractFieldDescription field, String suffix) {
		return builderFor(field).withDeclaration(suffix, targetType + " date");
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
		String target = getExpectedElement();
		return Arrays.asList(
				builderFor(field, "After")
						.withJavaDoc("Verify that this `" + targetType + "` is after another one",
								"date the `" + targetType + "` to compare with", target + "#after(" + targetType + ")")
						.havingDefault(target + ".after(date)"),
				builderFor(field, "SameOrAfter")
						.withJavaDoc("Verify that this `" + targetType + "` is after or same another one",
								"date the LocalDate to compare with", target + "#sameOrAfter(" + targetType + ")")
						.havingDefault(
								target + ".sameOrAfter(date)"),
				builderFor(field, "Before")
						.withJavaDoc("Verify that this `" + targetType + "` is before another one",
								"date the `" + targetType + "` to compare with",
								target + "#before(" + targetType + ")")
						.havingDefault(target + ".before(date)"),
				builderFor(field, "SameOrBefore")
						.withJavaDoc("Verify that this `" + targetType + "` is same or before another one",
								"date the `" + targetType + "` to compare with",
								target + "#sameOrBefore(" + targetType + ")")
						.havingDefault(target + ".sameOrBefore(date)"),
				builderFor(field, "SameDay")
						.withJavaDoc("Verify that this `" + targetType + "` is same day another one",
								"date the `" + targetType + "` to compare with",
								target + "#sameDay(" + targetType + ")")
						.havingDefault(target + ".sameDay(date)"));
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
