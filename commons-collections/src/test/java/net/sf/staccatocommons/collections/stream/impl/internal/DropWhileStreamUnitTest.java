/*
 Copyright (c) 2011, The Staccato-Commons Team

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
import static org.junit.Assert.*;

import java.util.Arrays;

import net.sf.staccatocommons.collections.stream.Stream;
import net.sf.staccatocommons.collections.stream.Streams;
import net.sf.staccatocommons.defs.Evaluable;

import org.junit.Test;

/**
 * Test for {@link DropWhileStream}
 * 
 * @author flbulgarelli
 */
public class DropWhileStreamUnitTest {

	/** Test for {@link Stream#dropWhile(Evaluable)} */
	@Test
	public void tesDropWhile() throws Exception {
		assertEquals(Arrays.asList(1, 9, 2, 0),//
			Streams.cons(1, 9, 2, 0).dropWhile(greaterThan(5)).toList());

		assertEquals(Arrays.asList(),//
			Streams.cons(1, 9, 2, 0).dropWhile(greaterThanOrEqualTo(0)).toList());

		assertEquals(Arrays.asList(1, 9, 2, 0),//
			Streams.cons(1, 9, 2, 0).dropWhile(greaterThanOrEqualTo(2)).toList());

		assertEquals(Arrays.asList(0),//
			Streams.cons(1, 9, 2, 0).dropWhile(greaterThanOrEqualTo(1)).toList());
	}

}
