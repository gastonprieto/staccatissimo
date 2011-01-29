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
package net.sf.staccatocommons.io;

import java.io.File;

import net.sf.staccatocommons.check.annotation.NonNull;
import net.sf.staccatocommons.defs.restriction.Constant;
import net.sf.staccatocommons.lang.function.Function;

/**
 * @author flbulgarelli
 * 
 */
public class IOFunctions {

	/**
	 * Answers a function that returns the name of a file
	 * 
	 * @return a {@link Function} that returns <code>arg.getName()</code>
	 */
	@NonNull
	@Constant
	public static Function<File, String> fileName() {
		return new Function<File, String>() {
			public String apply(File arg) {
				return arg.getName();
			}
		};
	}

	/**
	 * Answers a function that returns the path of a file
	 * 
	 * @return a {@link Function} that returns <code>arg.getPath()</code>
	 */
	@NonNull
	@Constant
	public static Function<File, String> filePath() {
		return new Function<File, String>() {
			public String apply(File arg) {
				return arg.getPath();
			}
		};
	}

}
