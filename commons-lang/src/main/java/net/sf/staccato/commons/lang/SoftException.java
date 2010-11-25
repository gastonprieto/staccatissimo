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
package net.sf.staccato.commons.lang;

import java.util.concurrent.Callable;

import net.sf.staccato.commons.check.annotation.NonNull;

/**
 * A {@link SoftException} is a {@link RuntimeException} that wraps another with
 * only solely purpose of making it unchecked
 * 
 * @author flbulgarelli
 */
public class SoftException extends RuntimeException {

	private static final long serialVersionUID = 4364656280386270636L;

	/**
	 * Creates a new {@link SoftException} that wraps the given one
	 * 
	 * @param cause
	 *          the wrapped exception. Non null.
	 */

	public SoftException(@NonNull Exception cause) {
		super(cause);
	}

	/**
	 * Converts an exception into a {@link RuntimeException}, either by casting
	 * it, if possible, or wrapping it into a {@link SoftException}
	 * 
	 * @param exception
	 *          the target exception to soften. Non null.
	 * @return The given casted, casted to {@link RuntimeException}, if possible,
	 *         or a new {@link SoftException} that wrapps it.
	 */
	@NonNull
	public static RuntimeException soften(@NonNull Exception exception) {
		if (exception instanceof RuntimeException)
			return (RuntimeException) exception;
		return new SoftException(exception);
	}

	/**
	 * Tries to call the given {@link Callable} and return its result, and
	 * rethrows a soften exception if {@link Callable#call()} threw an
	 * {@link Exception}
	 * 
	 * @param <T>
	 *          the callable return type
	 * @param callable
	 *          the callable to execute. Non null.
	 * @return the result of the callable, if {@link Callable#call()} succeed
	 * @throws RuntimeException
	 *           if the call failed
	 */
	@NonNull
	public static <T> T callOrSoften(@NonNull Callable<T> callable) {
		try {
			return callable.call();
		} catch (Exception e) {
			throw soften(e);
		}
	}
}
