package ch.powerunit.extensions.matchers.samples.third;

import ch.powerunit.extensions.matchers.ProvideMatchers;
import ch.powerunit.extensions.matchers.samples.Pojo1;
import ch.powerunit.extensions.matchers.samples.Pojo3;
import ch.powerunit.extensions.matchers.samples.others.PojoRenameMatcher;

@ProvideMatchers
public class OneFunnyOne {
	public Pojo1 onePojo1;
	
	public String secondOne;
	
	public Pojo3 secondPojo3;
	
	public PojoRenameMatcher thirdPojo;
}
