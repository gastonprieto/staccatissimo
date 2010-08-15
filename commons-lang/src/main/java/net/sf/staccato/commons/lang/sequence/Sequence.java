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
package net.sf.staccato.commons.lang.sequence;

import java.util.Iterator;

import net.sf.staccato.commons.lang.Applicable;
import net.sf.staccato.commons.lang.Evaluable;
import net.sf.staccato.commons.lang.check.Ensure;
import net.sf.staccato.commons.lang.value.Unmodifiable;
import net.sf.staccato.commons.lang.value.UnmodifiableObject;

/**
 * A {@link Sequence} is an {@link Iterable} object whose {@link Iterator},
 * starting with a seed value, retrieves elements generated by a
 * {@link Applicable}, until an {@link Evaluable} stop condition is satisfied.
 * 
 * Sequences are {@link Unmodifiable}.
 * 
 * @author flbulgarelli
 * 
 * @param <T>
 */
public class Sequence<T> extends UnmodifiableObject implements Iterable<T> {

	private static final long serialVersionUID = 8811454338704704525L;

	private final T seed;

	private final Applicable<T, T> generator;

	private final Evaluable<T> stopCondition;

	/**
	 * 
	 * Creates a new {@link Sequence}
	 * 
	 * @param seed
	 *          the initial value to be retrieved
	 * @param generator
	 * @param stopCondition
	 *          a predicate that evaluates to true when sequencing should stop,
	 *          that is, when the given element and subsequent should not be
	 *          retrieved.
	 */
	public Sequence(T seed, Applicable<T, T> generator, Evaluable<T> stopCondition) {
		this.seed = seed;
		this.generator = generator;
		this.stopCondition = stopCondition;
	}

	/**
	 * @return the initial value of the sequence
	 */
	public T getSeed() {
		return seed;
	}

	/**
	 * @return the generator of the sequence elements. Elements are generated
	 *         applying the previous element of the sequence to the generator
	 */
	public Applicable<T, T> getGenerator() {
		return generator;
	}

	/**
	 * @return the stopCondition. Sequencing will continue until it
	 */
	public Evaluable<T> getStopCondition() {
		return stopCondition;
	}

	/**
	 * Returns an iterator that retrieves the values generated by
	 * {@link #getGenerator()}, starting by {@link #getSeed()}, and until
	 * {@link #getStopCondition()} evaluates to true.
	 * 
	 * There are therr particular cases of the previous definition:
	 * <ul>
	 * <li>An <em>infinite</em> iterator - if the stop condition is in practice
	 * never met, that is, if this predicate evaluates to false always, or until
	 * all system resources have been consumed</li>
	 * <li>An <em>empty</em> iterator - if the stop condition is true for the seed
	 * <li>A non-empty, finite iterator - if the stop condition is true for at
	 * least one generated element, and false for the seed</li>
	 * </ul>
	 * 
	 * @return a new sequencing iterator over the generated values
	 */
	@Override
	public Iterator<T> iterator() {
		return new Iterator<T>() {

			private T next = getSeed();

			@Override
			public boolean hasNext() {
				return !getStopCondition().eval(next);
			}

			@Override
			public T next() {
				T next = this.next;
				this.next = getGenerator().apply(next);
				return next;
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException("Unmodifiable interator");
			}
		};
	}

	/**
	 * Factory method for creating new {@link Sequence}
	 * 
	 * @param <T>
	 * @param start
	 * @param generator
	 * @param stopCondition
	 * @return a new Sequence
	 */
	public static <T> Sequence<T> from(T start, Applicable<T, T> generator,
		Evaluable<T> stopCondition) {
		return new Sequence<T>(start, generator, stopCondition);
	}

	/**
	 * Factory method that creates a new infinte {@link Sequence} that generates
	 * integers applying an a positive, zero, or negative increment.
	 * 
	 * Zero increment will produce a sequence that just replicates the seed.
	 * 
	 * @param from
	 *          the seed of the sequence
	 * @param step
	 *          the positive, zero o negative increment
	 * @return a new Sequence
	 */
	public static Sequence<Integer> fromBy(int from, int step) {
		return new Sequence(
			from,
			new IntegerIncrement(step),
			StopConditions.stopNever());
	}

	/**
	 * Factory method that creates a new finite {@link Sequence} that generates
	 * integers applying an a positive, or negative increment, until it reaches an
	 * stop value.
	 * 
	 * For example <code>Sequence.fromToBy(0,4,1)</code> will iterate over 0,1,2
	 * and 3.
	 * 
	 * @param from
	 *          the seed of the sequence
	 * @param to
	 *          the stop value
	 * @param step
	 *          the positive or negative increment. Non zero.
	 * @return a new Sequence
	 */
	public static Sequence<Integer> fromToBy(int from, int to, int step) {
		Ensure.isTrue("step", step != 0, "must be non zero");
		return new Sequence(
			from,
			new IntegerIncrement(step),
			step < 0 ? StopConditions.downTo(to) : StopConditions.upTo(to));
	}

	/**
	 * Factory method that creates a new finite {@link Sequence} that generates
	 * integers applying incrementing or decrementing by one - depending on if the
	 * seed is lower to the stop value or not, until it reaches an stop value.
	 * 
	 * For example <code>Sequence.fromTo(0,4)</code> will iterate over 0,1,2 and
	 * 3, and <code>Sequence.fromTo(4,2)</code> will iterate over 4 and 3.
	 * 
	 * @param from
	 *          the seed of the sequence
	 * @param to
	 *          the stop value
	 * @return a new Sequence
	 */
	// FIXME BUG! in the latter example it should sequence up to 3, but it
	// sequences up to 2!
	public static Sequence<Integer> fromTo(int from, int to) {
		return fromToBy(from, to, from < to ? 1 : -1);
	}

}