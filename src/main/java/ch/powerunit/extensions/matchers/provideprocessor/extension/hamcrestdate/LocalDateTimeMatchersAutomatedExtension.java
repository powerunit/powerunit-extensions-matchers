/**
 * 
 */
package ch.powerunit.extensions.matchers.provideprocessor.extension.hamcrestdate;

import ch.powerunit.extensions.matchers.provideprocessor.RoundMirror;

/**
 * @author borettim
 *
 */
public class LocalDateTimeMatchersAutomatedExtension extends AbstractHamcrestDateMatchersAutomatedExtension {

	public LocalDateTimeMatchersAutomatedExtension(RoundMirror roundMirror) {
		super(roundMirror, "org.exparity.hamcrest.date.LocalDateTimeMatchers", "java.time.LocalDateTime");
	}

}
