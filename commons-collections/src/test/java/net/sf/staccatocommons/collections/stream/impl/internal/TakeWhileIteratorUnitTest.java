/*
 Copyright (c) 2010, The Staccato-Commons Team

 This program is free software; you can redistribute it and/or modify
 it under the terms of the GNU Lesser General Public License as published by
 the Free Software Foundation; version 3 of the License.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU Lesser General Public License for more details.
 */
package net.sf.staccatocommons.collections.stream.impl.internal;

import static net.sf.staccatocommons.lang.Compare.*;
import net.sf.staccatocommons.collections.stream.Cons;
import net.sf.staccatocommons.testing.junit.theories.IterableTheories;

/**
 * @author flbulgarelli
 * 
 */
public class TakeWhileIteratorUnitTest extends IterableTheories {

	protected Iterable<?> createTwoElementsIterable() {
		return Cons.from(4, 5, 6).takeWhile(lessThan(6));
	}

	protected Iterable<?> createOneElementIterable() {
		return Cons.from(10, 15, 90).takeWhile(lessThan(12));
	}

}
