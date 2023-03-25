/**
 * This is the module containing all the support for the powerunit matchers.
 * 
 * @see ch.powerunit.extensions.matchers.api
 */
module powerunit.matchers {
	exports ch.powerunit.extensions.matchers.api;

	requires java.compiler;
	
	provides javax.annotation.processing.Processor with ch.powerunit.extensions.matchers.provideprocessor.ProvidesMatchersAnnotationsProcessor;
}