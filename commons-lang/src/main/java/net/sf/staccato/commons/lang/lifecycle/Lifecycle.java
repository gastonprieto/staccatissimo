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
package net.sf.staccato.commons.lang.lifecycle;

import net.sf.staccato.commons.check.annotation.NonNull;

/**
 * A {@link Lifecycle} represents a flow of initialization, usage and dispose of
 * a resource.
 * 
 * @author flbulgarelli
 * 
 * @param <ResourceType>
 * @param <ExceptionType>
 * @param <ResultType>
 */
public interface Lifecycle<ResourceType, ResultType> {

	/**
	 * Initializes and gets a resource of ResourceType
	 * 
	 * @return the initialized resources
	 * @throws Exception
	 *           if any error occurs
	 */
	@NonNull
	ResourceType initialize() throws Exception;

	/**
	 * Makes usage of a resource, and returns a result
	 * 
	 * @param resource
	 *          the resource to use
	 * @return the result of using the resource, of ResultType. It may be null, if
	 *         and only if ResultType is {@link Void}
	 * @throws Exception
	 *           if any error occurs
	 */
	ResultType doWork(@NonNull ResourceType resource) throws Exception;

	/**
	 * Disposes the resource.
	 * 
	 * @param resource
	 * @throws Exception
	 *           if any error occurs
	 */
	void dispose(@NonNull ResourceType resource) throws Exception;

}