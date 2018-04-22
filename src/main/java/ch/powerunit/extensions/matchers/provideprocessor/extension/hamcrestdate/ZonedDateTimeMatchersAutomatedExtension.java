/**
 * 
 */
package ch.powerunit.extensions.matchers.provideprocessor.extension.hamcrestdate;

import ch.powerunit.extensions.matchers.provideprocessor.RoundMirror;

/**
 * @author borettim
 *
 */
public class ZonedDateTimeMatchersAutomatedExtension extends AbstractHamcrestDateMatchersAutomatedExtension {

	public ZonedDateTimeMatchersAutomatedExtension(RoundMirror roundMirror) {
		super(roundMirror, "org.exparity.hamcrest.date.ZonedDateTimeMatchers", "java.time.ZonedDateTime",true);
	}

}
