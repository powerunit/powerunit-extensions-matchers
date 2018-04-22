/**
 * 
 */
package ch.powerunit.extensions.matchers.provideprocessor.extension.hamcrestdate;

import ch.powerunit.extensions.matchers.provideprocessor.RoundMirror;

/**
 * @author borettim
 *
 */
public class LocalDateMatchersAutomatedExtension extends AbstractHamcrestDateMatchersAutomatedExtension {

	public LocalDateMatchersAutomatedExtension(RoundMirror roundMirror) {
		super(roundMirror, "org.exparity.hamcrest.date.LocalDateMatchers", "java.time.LocalDate");
	}

}
