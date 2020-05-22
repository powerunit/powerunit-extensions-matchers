/**
 * 
 */
package ch.powerunit.extensions.matchers.provideprocessor.extension.jackson;

import java.util.Arrays;
import java.util.Collection;

import ch.powerunit.extensions.matchers.provideprocessor.RoundMirror;
import ch.powerunit.extensions.matchers.provideprocessor.fields.DefaultFieldDescription;
import ch.powerunit.extensions.matchers.provideprocessor.fields.FieldDSLMethod;

/**
 * @author borettim
 *
 */
public class DefaultJsonNodeJacksonAutomatedExtension extends AbstractJacksonAutomatedExtension {

	/**
	 * @param roundMirror
	 */
	public DefaultJsonNodeJacksonAutomatedExtension(RoundMirror roundMirror) {
		super(roundMirror);
	}

	@Override
	protected Collection<FieldDSLMethod> acceptJsonMatcher(DefaultFieldDescription field) {
		return Arrays.asList(buildBasic(field, "IsJsonMissing", "is missing", "isMissingNode()"),
				buildBasic(field, "IsJsonValue", "is value", "isValueNode()"),
				buildBasic(field, "IsJsonContainer", "is container", "isContainerNode()"),
				buildBasic(field, "IsJsonArray", "is array", "isArray()"),
				buildBasic(field, "IsJsonObject", "is object", "isObject()"));
	}

}
