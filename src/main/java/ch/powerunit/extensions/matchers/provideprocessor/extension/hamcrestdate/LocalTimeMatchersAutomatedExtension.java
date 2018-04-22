/**
 * 
 */
package ch.powerunit.extensions.matchers.provideprocessor.extension.hamcrestdate;

import ch.powerunit.extensions.matchers.provideprocessor.RoundMirror;

/**
 * @author borettim
 *
 */
public class LocalTimeMatchersAutomatedExtension extends AbstractHamcrestDateMatchersAutomatedExtension {

	public LocalTimeMatchersAutomatedExtension(RoundMirror roundMirror) {
		super(roundMirror, "org.exparity.hamcrest.date.LocalTimeMatchers", "java.time.LocalTime",false);
	}

}
