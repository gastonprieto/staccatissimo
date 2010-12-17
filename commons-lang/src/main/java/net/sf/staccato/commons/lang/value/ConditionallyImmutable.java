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
package net.sf.staccato.commons.lang.value;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import net.sf.staccato.commons.lang.restriction.Restriction;

/**
 * {@link ConditionallyImmutable} annotated classes are {@link Unmodifiable}s
 * whose instance can be trated as {@link Immutable} as long as their attributes
 * are {@link Immutable} too.
 * 
 * 
 * TODO about inheritance
 * 
 * @author flbulgarelli
 * 
 */
@Documented
@Inherited
@Restriction
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface ConditionallyImmutable {

}
