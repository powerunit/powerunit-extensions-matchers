/**
 * 
 */
package ch.powerunit.extensions.matchers.provideprocessor.extension.hamcrestutility;

import java.util.Collection;
import java.util.Collections;
import java.util.function.Supplier;

import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic.Kind;

import ch.powerunit.extensions.matchers.provideprocessor.DSLMethod;
import ch.powerunit.extensions.matchers.provideprocessor.ProvidesMatchersAnnotatedElementData;
import ch.powerunit.extensions.matchers.provideprocessor.RoundMirror;
import ch.powerunit.extensions.matchers.provideprocessor.extension.AutomatedExtension;
import ch.powerunit.extensions.matchers.provideprocessor.fields.AbstractFieldDescription;
import ch.powerunit.extensions.matchers.provideprocessor.fields.CollectionFieldDescription;
import ch.powerunit.extensions.matchers.provideprocessor.fields.FieldDSLMethod;
import ch.powerunit.extensions.matchers.provideprocessor.fields.lang.BuilderJavadoc;

/**
 * @author borettim
 *
 */
public abstract class AbstractHamcrestUtilityAutomatedExtension extends AutomatedExtension {

	protected final TypeMirror knownType;

	protected final String targetType;

	public AbstractHamcrestUtilityAutomatedExtension(RoundMirror roundMirror, String targetType) {
		super(roundMirror, "com.nitorcreations.Matchers");
		this.targetType = targetType;
		this.knownType = getMirrorFor(targetType);
	}

	protected BuilderJavadoc builderFor(AbstractFieldDescription field, String targetMethod, String arguments) {
		String suffix = targetMethod.substring(0, 1).toUpperCase() + targetMethod.substring(1);
		return builderFor(field).withDeclaration(suffix, arguments);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.powerunit.extensions.matchers.provideprocessor.extension.
	 * AutomatedExtension#accept(ch.powerunit.extensions.matchers.
	 * provideprocessor.fields.FieldDescriptionMetaData)
	 */
	@Override
	public final Collection<FieldDSLMethod> accept(AbstractFieldDescription field) {
		if (!(field instanceof CollectionFieldDescription) || "".equals(field.getGeneric())) {
			return Collections.emptyList();
		}
		return acceptHamcrestUtility(field);
	}

	protected abstract Collection<FieldDSLMethod> acceptHamcrestUtility(AbstractFieldDescription field);

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.powerunit.extensions.matchers.provideprocessor.extension.
	 * AutomatedExtension#accept(ch.powerunit.extensions.matchers.
	 * provideprocessor.ProvidesMatchersAnnotatedElementData)
	 */
	@Override
	public final Collection<Supplier<DSLMethod>> accept(ProvidesMatchersAnnotatedElementData clazz) {
		return Collections.emptyList();
	}

}
