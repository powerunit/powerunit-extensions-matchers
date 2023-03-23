package ch.powerunit.extensions.matchers.samples;

import ch.powerunit.extensions.matchers.ProvideMatchers;

@ProvideMatchers(disableGenerationOfFactory = true)
public class Sample2 {
	private String field1;

	public String getField1() {
		return field1;
	}

	public void setField1(String field1) {
		this.field1 = field1;
	}
	
}
