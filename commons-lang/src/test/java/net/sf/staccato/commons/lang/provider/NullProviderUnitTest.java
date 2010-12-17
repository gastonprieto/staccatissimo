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
package net.sf.staccato.commons.lang.provider;


import static org.junit.Assert.assertNull;
import net.sf.staccato.commons.lang.provider.internal.NullProvider;

import org.junit.Before;
import org.junit.Test;

/**
 * @author flbulgarelli
 *
 */
public class NullProviderUnitTest {

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testValue() throws Exception {
		assertNull(NullProvider.getInstance().value());
	}

}
