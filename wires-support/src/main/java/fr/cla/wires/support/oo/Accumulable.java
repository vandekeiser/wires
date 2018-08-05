package fr.cla.wires.support.oo;

import java.util.Collection;
import java.util.EnumSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.*;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collector.Characteristics.UNORDERED;

//@formatter:off
/**
 * An Accumulable has an initial value and knows how to take in new partial values ({@code combiner}),
 * and how to combine itself with another Accumulable. This way it can easily be used by a Collector.
 * @param <W> Type of "weightable".
 *           Their weights are determined by {@code combineWithValue}
 *           then accumulated into the current value by {@code combiner}
 * @param <A> Type of accumulated value
 */
public class Accumulable<W, A> extends MutableValue<A> {

    private final A EMPTY = null;
    private final Function<W, A> weight;
    private final BinaryOperator<A> combiner;

    protected Accumulable(
        A initialValue,
        boolean acceptNull,
        Function<W, A> weight,
        BinaryOperator<A> combiner
    ) {
        super(initialValue, acceptNull);
        this.weight = requireNonNull(weight);
        this.combiner = requireNonNull(combiner);
    }

    public static <I, A> Accumulable<I, A> initiallyEmpty(
        Function<I, A> weight,
        BinaryOperator<A> combiner
    ) {
        return new Accumulable<>(null, true, weight, combiner);
    }

    public static <I, A> Accumulable<I, A> initially(
        A initialValue,
        Function<I, A> weight,
        BinaryOperator<A> combiner
    ) {
        return new Accumulable<>(initialValue, false, weight, combiner);
    }

    public final void accumulate(W weightable) {
        mutableEquivalentToInitially(combineWithValue(
            weight.apply(weightable)
        ));
    }

    public final Accumulable<W, A> combine(Accumulable<W, A> that) {
        mutableEquivalentToInitially(combineWithValue(
            that.get()
        ));
        return this;
    }

    private A combineWithValue(A value) {
        A maybe1 = this.get();
        A maybe2 = value;

        if (maybe1 != null && maybe2 != null) {
            return combiner.apply(maybe1, maybe2);
        } else if (maybe1 != null) {
            return maybe1;
        } else {
            return maybe2;
        }
    }

    /**
     * Modifies this to put it into the same state (as defined by equals)
     * as calling initially (static method which creates a new Accumulable instance) would.
     *
     * -If newValue is null, throws NullPointerException.
     * -Otherwise after calling this method, this.equals(initially(newValue)) is guaranteed to be true.
     * @param newValue If W was calling initially W would pass this single param to it.
     */
    public void mutableEquivalentToInitially(A newValue) {
        set(newValue);
    }

    /**
     * This method must not be called from anything other than a whitebox test.
     * (then if something throws ClassCastException later, it will be detectable before pushing).
     * It is only public because of MCOMPILER-354. But at least it's not in an exported package.
     *
     * If newValue is not an instance of AbstractValueObject<A>, the result and later behaviour is undefined.
     * @param newValue Must be an AbstractValueObject<A>
     * @throws NullPointerException if newValue is null
     */
    public void unsafeMutableEquivalentToInitially(AbstractValueObject<?> newValue) {
        @SuppressWarnings("unchecked") //See javadoc
        A unsafeNewValue = (A) newValue;
        this.mutableEquivalentToInitially(unsafeNewValue);
    }

    public static <O, T> java.util.stream.Collector<O, ?, T> collector(
        Function<O, T> weight,
        BinaryOperator<T> combiner,
        UnaryOperator<T> finisher
    ) {
        return new Collector<>(weight, combiner, finisher);
    }




    public static class Collector<O, T>
    implements java.util.stream.Collector<O, Accumulable<O, T>, T> {
        private final Function<Accumulable<O, T>, T> getAccumulated;
        private final Function<O, T> weight;
        private final BinaryOperator<T> combiner;
        private final UnaryOperator<T> finisher;

        private Collector(
            Function<O, T> weight,
            BinaryOperator<T> combiner,
            UnaryOperator<T> finisher
        )  {
            this.getAccumulated = MutableValue::get;
            this.weight = requireNonNull(weight);
            this.combiner = requireNonNull(combiner);
            this.finisher = requireNonNull(finisher);
        }

        @Override public Supplier<Accumulable<O, T>> supplier() {
            return () -> Accumulable.initiallyEmpty(
                weight, combiner
            );
        }

        @Override public BiConsumer<Accumulable<O, T>, O> accumulator() {
            return Accumulable::accumulate;
        }

        @Override public BinaryOperator<Accumulable<O, T>> combiner() {
            return Accumulable::combine;
        }

        @Override public Function<Accumulable<O, T>, T> finisher() {
            return getAccumulated.andThen(finisher);
        }

        @Override public Set<Characteristics> characteristics() {
            //TODO some collectors might not be UNORDERED
            return EnumSet.of(UNORDERED);
        }
    }

}
//@formatter:on
