/**
 * Powerunit - A JDK1.8 test framework
 * Copyright (C) 2014 Mathieu Boretti.
 *
 * This file is part of Powerunit
 *
 * Powerunit is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Powerunit is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Powerunit. If not, see <http://www.gnu.org/licenses/>.
 */
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
	
	private Pojo1 onePojo1b;
	
	public void setOnePojo1b(Pojo1 onePojo1b) {
		this.onePojo1b=onePojo1b;
	}
	
	public Pojo1 getOnePojo1b() {
		return 	onePojo1b;
	}
}
