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

import java.io.Serializable;
import java.util.List;

import ch.powerunit.extensions.matchers.ComplementaryExpositionMethod;
import ch.powerunit.extensions.matchers.ProvideMatchers;

/**
 * @author borettim
 * @param <T>
 *            This is T
 * @param <O>
 *            This is O
 *
 */
@ProvideMatchers(comments = "this is a \"comment\"", moreMethod = { ComplementaryExpositionMethod.CONTAINS,
		ComplementaryExpositionMethod.ARRAYCONTAINING, ComplementaryExpositionMethod.HAS_ITEMS,
		ComplementaryExpositionMethod.ANY_OF })
public class Pojo3<T, O extends Serializable> {
	public String msg1;

	public T msg2;

	public O msg3;

	private T msg4;

	public T[] msg5;

	public List<T> msg6;

	/**
	 * @return the msg4
	 */
	public T getMsg4() {
		return msg4;
	}

	/**
	 * @param msg4
	 *            the msg4 to set
	 */
	public void setMsg4(T msg4) {
		this.msg4 = msg4;
	}

}
