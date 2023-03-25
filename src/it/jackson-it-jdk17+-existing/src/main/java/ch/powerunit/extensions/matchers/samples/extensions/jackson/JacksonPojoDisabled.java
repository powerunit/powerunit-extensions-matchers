package ch.powerunit.extensions.matchers.samples.extensions.jackson;

import com.fasterxml.jackson.databind.JsonNode;

import ch.powerunit.extensions.matchers.api.ProvideMatchers;

@ProvideMatchers(extensions = {})
public class JacksonPojoDisabled {

	public JsonNode p1;

}
