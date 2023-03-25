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
package ch.powerunit.extensions.matchers.samples;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import ch.powerunit.extensions.matchers.api.ProvideMatchers;

/**
 * @author borettim
 *
 */
@ProvideMatchers
public class Pojo1 {
	private String msg1;

	public String msg2;

	public int msg3;

	public String[] msg4;

	public int[] msg5;

	public List<String> msg6;

	public List<String[]> msg7;

	public List<String>[] msg8;

	public Collection<Integer> msg9;

	public Set<String> msg12;

	public String getMsg1() {
		return msg1;
	}

	public void setMsg1(String msg1) {
		this.msg1 = msg1;
	}

	public boolean myBoolean;

	private boolean oneBoolean;

	public boolean isOneBoolean() {
		return oneBoolean;
	}

	public void setOneBoolean(boolean oneBoolean) {
		this.oneBoolean = oneBoolean;
	}

	public Pojo1() {
	}

	public Pojo1(String msg1) {
		this.msg1 = msg1;
	}

	public Pojo1(List<String> msg6) {
		this.msg6 = msg6;
	}

	@Override
	public String toString() {
		return "Pojo1 [msg1=" + msg1 + ", msg2=" + msg2 + ", msg3=" + msg3 + ", msg4=" + Arrays.toString(msg4)
				+ ", msg5=" + Arrays.toString(msg5) + ", msg6=" + msg6 + ", msg7=" + msg7 + ", msg8="
				+ Arrays.toString(msg8) + ", msg9=" + msg9 + ", msg12=" + msg12 + ", myBoolean=" + myBoolean
				+ ", oneBoolean=" + oneBoolean + "]";
	}

}
