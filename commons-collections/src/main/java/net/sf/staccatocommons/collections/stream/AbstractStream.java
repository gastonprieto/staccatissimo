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
package net.sf.staccatocommons.collections.stream;

import static net.sf.staccatocommons.lang.function.Functions.*;
import static net.sf.staccatocommons.lang.predicate.Predicates.*;
import static net.sf.staccatocommons.lang.tuple.Tuple.*;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import net.sf.staccatocommons.check.Validate;
import net.sf.staccatocommons.check.annotation.ForceChecks;
import net.sf.staccatocommons.check.annotation.NonNull;
import net.sf.staccatocommons.check.annotation.NotNegative;
import net.sf.staccatocommons.collections.internal.ToPair;
import net.sf.staccatocommons.collections.internal.comparator.NaturalComparator;
import net.sf.staccatocommons.collections.iterable.Iterables;
import net.sf.staccatocommons.collections.iterable.internal.IterablesInternal;
import net.sf.staccatocommons.collections.stream.impl.ListStream;
import net.sf.staccatocommons.collections.stream.impl.internal.AppendStream;
import net.sf.staccatocommons.collections.stream.impl.internal.ConcatStream;
import net.sf.staccatocommons.collections.stream.impl.internal.DropStream;
import net.sf.staccatocommons.collections.stream.impl.internal.DropWhileStream;
import net.sf.staccatocommons.collections.stream.impl.internal.FilterStream;
import net.sf.staccatocommons.collections.stream.impl.internal.FlatMapStream;
import net.sf.staccatocommons.collections.stream.impl.internal.MapStream;
import net.sf.staccatocommons.collections.stream.impl.internal.TakeStream;
import net.sf.staccatocommons.collections.stream.impl.internal.TakeWhileStream;
import net.sf.staccatocommons.collections.stream.impl.internal.ZipStream;
import net.sf.staccatocommons.defs.Applicable;
import net.sf.staccatocommons.defs.Applicable2;
import net.sf.staccatocommons.defs.Evaluable;
import net.sf.staccatocommons.defs.Evaluable2;
import net.sf.staccatocommons.defs.Thunk;
import net.sf.staccatocommons.defs.type.NumberType;
import net.sf.staccatocommons.lang.Compare;
import net.sf.staccatocommons.lang.Option;
import net.sf.staccatocommons.lang.function.Function2;
import net.sf.staccatocommons.lang.tuple.Pair;
import net.sf.staccatocommons.lang.value.NamedTupleToStringStyle;

import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * An abstract implementation of a {@link Stream}. Only it {@link Iterator}
 * method is abstract
 * 
 * @author flbulgarelli
 * 
 * @param <A>
 */
public abstract class AbstractStream<A> implements Stream<A> {

	protected static final Validate<NoSuchElementException> validate = Validate
		.throwing(NoSuchElementException.class);

	@Override
	public int size() {
		return Iterables.size(this);
	}

	@Override
	public boolean isEmpty() {
		return IterablesInternal.isEmptyInternal(this);
	}

	@Override
	public boolean contains(A element) {
		return IterablesInternal.containsInternal(this, element);
	}

	@Override
	public Stream<A> filter(final Evaluable<? super A> predicate) {
		return new FilterStream<A>(this, predicate);
	}

	@Override
	public Stream<A> takeWhile(final Evaluable<? super A> predicate) {
		return new TakeWhileStream<A>(this, predicate);
	}

	@Override
	public Stream<A> take(@NotNegative final int amountOfElements) {
		return new TakeStream<A>(this, amountOfElements);
	}

	public Stream<A> dropWhile(Evaluable<? super A> predicate) {
		return new DropWhileStream<A>(this, predicate);
	}

	public Stream<A> drop(@NotNegative int amountOfElements) {
		return new DropStream<A>(this, amountOfElements);
	}

	@Override
	public A reduce(Applicable2<? super A, ? super A, ? extends A> function) {
		validate.that(!isEmpty(), "Can not reduce an empty stream");
		return Iterables.reduce(this, function);
	}

	@Override
	public <O> O fold(O initial, Applicable2<? super O, ? super A, ? extends O> function) {
		return Iterables.fold(this, initial, function);
	}

	@Override
	public A any() {
		return IterablesInternal.anyInternal(this);
	}

	@Override
	public Option<A> anyOrNone() {
		return Iterables.anyOrNone(this);
	}

	@Override
	public A anyOrNull() {
		return anyOrNone().valueOrNull();
	}

	@Override
	public A anyOrElse(Thunk<A> provider) {
		return anyOrNone().valueOrElse(provider);
	}

	@Override
	public A anyOrElse(A value) {
		return anyOrNone().valueOrElse(value);
	}

	@Override
	public A find(Evaluable<? super A> predicate) {
		return Iterables.find(this, predicate);
	}

	@Override
	public Option<A> findOrNone(Evaluable<? super A> predicate) {
		return Iterables.findOrNone(this, predicate);
	}

	@Override
	public A findOrNull(Evaluable<? super A> predicate) {
		return findOrNone(predicate).valueOrNull();
	}

	@Override
	public A findOrElse(Evaluable<? super A> predicate, Thunk<? extends A> provider) {
		return findOrNone(predicate).valueOrElse(provider);
	}

	@Override
	public boolean all(Evaluable<? super A> predicate) {
		return Iterables.all(this, predicate);
	}

	@Override
	public boolean any(Evaluable<? super A> predicate) {
		return Iterables.any(this, predicate);
	}

	@Override
	public <B> Stream<B> map(final Applicable<? super A, ? extends B> function) {
		return new MapStream<A, B>(this, function);
	}

	@Override
	public <B> Stream<B> flatMap(final Applicable<? super A, ? extends Iterable<? extends B>> function) {
		return new FlatMapStream<A, B>(this, function);
	}

	@Override
	public Stream<A> concat(final Iterable<A> other) {
		return new ConcatStream<A>(this, other);
	}

	@Override
	public A first() {
		return get(0);
	}

	@Override
	public A second() {
		return get(1);
	}

	@Override
	public A third() {
		return get(2);
	}

	@Override
	public A last() {
		return get(size() - 1);
	}

	@Override
	public A get(int n) {
		return Iterables.get(this, n);
	}

	@Override
	public int indexOf(A element) {
		return Iterables.indexOf(this, element);
	}

	@Override
	public int positionOf(A element) {
		int index = indexOf(element);
		if (index == -1)
			throw new NoSuchElementException(element.toString());
		return index;
	}

	@Override
	public boolean isBefore(A previous, A next) {
		return Iterables.isBefore(this, previous, next);
	}

	@Override
	public Set<A> toSet() {
		return Iterables.toSet(this);
	}

	@Override
	public List<A> toList() {
		return Iterables.toList(this);
	}

	public Stream<A> toRepetableStream() {
		return new ListStream<A>(toList()) {
			public Stream<A> toRepetableStream() {
				return this;
			}

			public List<A> toList() {
				return Collections.unmodifiableList(getList());
			}
		};
	}

	@Override
	public A[] toArray(Class<? extends A> clazz) {
		return toArray(clazz, toList());
	}

	protected A[] toArray(Class<? extends A> clazz, Collection<A> readOnlyColView) {
		return readOnlyColView.toArray((A[]) Array.newInstance(clazz, readOnlyColView.size()));
	}

	@Override
	@ForceChecks
	public String joinStrings(@NonNull String separator) {
		return StringUtils.join(iterator(), separator);
	}

	@Override
	public Pair<List<A>, List<A>> partition(Evaluable<? super A> predicate) {
		return Iterables.partition(this, predicate);
	}

	@Override
	public Pair<Stream<A>, Stream<A>> streamPartition(Evaluable<? super A> predicate) {
		Pair<List<A>, List<A>> partition = partition(predicate);
		return _(Streams.from(partition._1()), Streams.from(partition._2()));
	}

	@Override
	public boolean elementsEquals(A... elements) {
		return elementsEquals(Arrays.asList(elements));
	}

	@Override
	public boolean elementsEquals(Iterable<? extends A> other) {
		return Iterables.elementsEquals(this, other);
	}

	public boolean elementsEquals(Iterable<? extends A> other, Evaluable2<A, A> equalty) {
		return Iterables.elementsEquals(this, other, equalty);
	}

	public <B extends Comparable<B>> boolean elementsEquals(Iterable<? extends A> iterable,
		Applicable<A, B> function) {
		return elementsEquals(iterable, on(function));
	}

	@Override
	public <B> Stream<B> then(final Applicable<Stream<A>, ? extends Iterable<B>> function) {
		class ThenStream extends AbstractStream<B> {
			public Iterator<B> iterator() {
				return function.apply(AbstractStream.this).iterator();
			}
		}
		return new ThenStream();
	}

	/**
	 * <pre>
	 * intersperse _   []      = []
	 * intersperse _   [x]     = [x]
	 * intersperse sep (x:xs)  = x : sep : intersperse sep xs
	 * </pre>
	 * 
	 */
	public Stream<A> intersperse(final A sep) {
		return then(new DeconsFunction<A, A>() {
			public Iterable<A> apply(A head, Stream<A> tail) {
				if (tail.isEmpty())
					return Streams.from(head);
				return tail.intersperse(sep).prepend(sep).prepend(head);
			}
		});
	}

	public Stream<A> append(A element) {
		return new AppendStream<A>(this, element);
	}

	public Stream<A> prepend(A element) {
		return Streams.from(element, this);
	}

	public <B> Stream<B> then(final DeconsApplicable<A, B> function) {
		class DeconsThenStream extends AbstractStream<B> {
			public Iterator<B> iterator() {
				if (AbstractStream.this.isEmpty())
					return function.emptyApply().iterator();
				Pair<A, Stream<A>> decons = AbstractStream.this.decons();
				return function.apply(decons._1(), decons._2()).iterator();
			}
		}
		return new DeconsThenStream();
	}

	@Override
	public <B> Stream<Pair<A, B>> zip(Iterable<B> iterable) {
		return zip(iterable, ToPair.<A, B> getInstance());
	}

	public Pair<A, Stream<A>> decons() {
		Iterator<A> iter = iterator();
		validate.that(iter.hasNext(), "Empty streams have no head");
		return _(iter.next(), Streams.from(iter));
	}

	public Stream<A> tail() {
		validate.that(!isEmpty(), "Empty streams have not tail");
		return drop(1);
	}

	public A head() {
		validate.that(!isEmpty(), "Empty streams have not head");
		return first();
	}

	@ForceChecks
	public <B, C> Stream<C> zip(@NonNull final Iterable<B> iterable,
		@NonNull final Applicable2<A, B, C> function) {
		return new ZipStream<C, A, B>(this, iterable, function);
	}

	@Override
	public A sum() {
		return Iterables.sum(this);
	}

	@Override
	public A sum(NumberType<A> numberType) {
		return Iterables.sum(this, numberType);
	}

	@Override
	public A product() {
		return Iterables.product(this);
	}

	@Override
	public A product(NumberType<A> numberType) {
		return Iterables.product(this, numberType);
	}

	public A average() {
		return average(numberType());
	}

	public A average(final NumberType<A> numberType) {
		validate.that(!isEmpty(), "Can not get average on an empty stream");
		class Ref {
			A val = numberType.zero();
		}
		final Ref size = new Ref();
		return numberType.divide(fold(numberType.zero(), new Function2<A, A, A>() {
			public A apply(A arg1, A arg2) {
				size.val = numberType.increment(size.val);
				return numberType.add(arg1, arg2);
			}
		}), size.val);
	}

	@Override
	public A maximum() {
		return maximum(NaturalComparator.<A> natural());
	}

	@Override
	public A minimum() {
		return minimum(NaturalComparator.<A> natural());
	}

	@Override
	public A maximum(Comparator<A> comparator) {
		return reduce(max(comparator));
	}

	@Override
	public A minimum(Comparator<A> comparator) {
		return reduce(min(comparator));
	}

	@Override
	public <B extends Comparable<B>> A maximum(Applicable<A, B> function)
		throws NoSuchElementException {
		return maximum(Compare.on(function));
	}

	@Override
	public <B extends Comparable<B>> A minimum(Applicable<A, B> function)
		throws NoSuchElementException {
		return minimum(Compare.on(function));
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, NamedTupleToStringStyle.getInstance());
	}

	public NumberType<A> numberType() {
		throw new ClassCastException("Source can not be casted to ImplicitNumerType");
	}

	public Stream<A> reverse() {
		throw new NotImplementedException("not yet");
	}

}
