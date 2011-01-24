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

import java.util.Iterator;

import net.sf.staccatocommons.check.annotation.NonNull;
import net.sf.staccatocommons.collections.internal.iterator.AbstractUnmodifiableIterator;
import net.sf.staccatocommons.collections.stream.AbstractStream;
import net.sf.staccatocommons.collections.stream.Stream;
import net.sf.staccatocommons.defs.type.NumberType;

/**
 * @author flbulgarelli
 * 
 */
public final class ConcatStream<A> extends AbstractStream<A> {
	private final Stream<A> stream;
	private final Iterable<A> other;

	/**
	 * Creates a new {@link ConcatStream}
	 */
	public ConcatStream(@NonNull Stream<A> stream, @NonNull Iterable<A> other) {
		this.stream = stream;
		this.other = other;
	}

	public Iterator<A> iterator() {
		return new AbstractUnmodifiableIterator<A>() {
			private Iterator<A> iter = stream.iterator();
			private boolean second = false;

			public boolean hasNext() {
				if (iter.hasNext())
					return true;

				if (second)
					return false;

				iter = other.iterator();
				second = true;
				return iter.hasNext();
			}

			public A next() {
				return iter.next();
			}

		};
	}

	public NumberType<A> numberType() {
		return stream.numberType();
	}
}