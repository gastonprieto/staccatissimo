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


package net.sf.staccatocommons.lang.sequence;

import net.sf.staccatocommons.defs.predicate.Predicate;
import net.sf.staccatocommons.lang.Compare;
import net.sf.staccatocommons.lang.predicate.Predicates;
import net.sf.staccatocommons.restrictions.check.NonNull;

/**
 * @author flbulgarelli
 */
public class StopConditions {

	/**
	 * @param <T>
	 * @return <code>Predicates.false_()</code>
	 */
	@NonNull
	public static <T> Predicate<T> stopNever() {
		return Predicates.false_();
	}

	/**
	 * @param <T>
	 * @param value
	 * @return <code>Predicates.lowerThan(value)</code>
	 */
	@NonNull
	public static <T extends Comparable<T>> Predicate<T> downTo(@NonNull T value) {
		return Compare.lessThan(value);
	}

	/**
	 * @param <T>
	 * @param value
	 * @return <code>Predicates.greaterThan(value)</code>
	 */
	@NonNull
	public static <T extends Comparable<T>> Predicate<T> upTo(@NonNull T value) {
		return Compare.greaterThan(value);
	}

}
