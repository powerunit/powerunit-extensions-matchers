/**
 * 
 */
package ch.powerunit.extensions.matchers.provideprocessor.extension.hamcrestdate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import javax.lang.model.type.TypeMirror;

import ch.powerunit.extensions.matchers.provideprocessor.ProvidesMatchersAnnotatedElementData;
import ch.powerunit.extensions.matchers.provideprocessor.RoundMirror;
import ch.powerunit.extensions.matchers.provideprocessor.dsl.DSLMethod;
import ch.powerunit.extensions.matchers.provideprocessor.extension.AutomatedExtension;
import ch.powerunit.extensions.matchers.provideprocessor.fields.AbstractFieldDescription;
import ch.powerunit.extensions.matchers.provideprocessor.fields.FieldDSLMethod;

/**
 * @author borettim
 *
 */
public abstract class AbstractHamcrestDateMatchersAutomatedExtension extends AutomatedExtension {

	private final TypeMirror knownType;

	private final String targetType;

	private final boolean withSameDay;

	public AbstractHamcrestDateMatchersAutomatedExtension(RoundMirror roundMirror, String targetElement,
			String targetType, boolean withSameDay) {
		super(roundMirror, targetElement);
		this.targetType = targetType;
		knownType = getMirrorFor(targetType);
		this.withSameDay = withSameDay;
	}

	private FieldDSLMethod builderFor(AbstractFieldDescription field, String targetMethod) {
		String suffix = targetMethod.substring(0, 1).toUpperCase() + targetMethod.substring(1);
		String readable = Arrays.stream(targetMethod.split("(?=[A-Z])")).collect(Collectors.joining(" ")).toLowerCase();
		String target = getExpectedElement();
		return builderFor(field).withDeclaration(suffix, targetType + " date")
				.withJavaDoc("Verify that this `" + targetType + "` is " + readable + " another one",
						"date the `" + targetType + "` to compare with",
						target + "#" + targetMethod + "(" + targetType + ")")
				.havingDefault(target + "." + targetMethod + "(date)");
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
		if (!isSameType(field.getMirror().getFieldTypeAsTypeElement(), knownType)) {
			return Collections.emptyList();
		}
		Collection<FieldDSLMethod> tmp = new ArrayList<>(Arrays.asList(builderFor(field, "after"),
				builderFor(field, "sameOrAfter"), builderFor(field, "before"), builderFor(field, "sameOrBefore")));
		if (withSameDay) {
			tmp.add(builderFor(field, "sameDay"));
		}
		return Collections.unmodifiableCollection(tmp);
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
