/**
 * This is the module containing all the support for the powerunit matchers.
 * 
 * @see ch.powerunit.extensions.matchers
 */
module powerunit.exceptions {
	exports ch.powerunit.extensions.matchers;

	requires java.compiler;
	
	provides javax.annotation.processing.Processor with ch.powerunit.extensions.matchers.provideprocessor.ProvidesMatchersAnnotationsProcessor;
}