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
package ch.powerunit.extensions.matchers.common;

import javax.lang.model.util.TypeKindVisitor8;

/**
 * @author borettim
 *
 */
public class AbstractTypeKindVisitor<R, P, S extends AbstractRoundMirrorReferenceToProcessingEnv>
		extends TypeKindVisitor8<R, P> implements AbstractRoundMirrorSupport<S> {

	protected final S support;

	public AbstractTypeKindVisitor(S support) {
		this.support = support;
	}

	@Override
	public S getRoundMirror() {
		return support;
	}

}
