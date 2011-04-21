/**
 *  Copyright (c) 2011, The Staccato-Commons Team
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation; version 3 of the License.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 */

package net.sf.staccatocommons.lang.predicate.internal;

import net.sf.staccatocommons.restrictions.check.NonNull;

/**
 * @author flbulgarelli
 * 
 * @param <T>
 */
public final class Equals<T> extends NonAnnonymousPredicate<T> {
	private static final long serialVersionUID = -7587113479123370400L;
	private final T value;

	/**
	 * Creates a new {@link Equals}
	 * 
	 * @param value
	 *          the value to test equality
	 */
	public Equals(T value) {
		this.value = value;
	}

	public boolean eval(@NonNull T argument) {
		return argument.equals(value);
	}
}