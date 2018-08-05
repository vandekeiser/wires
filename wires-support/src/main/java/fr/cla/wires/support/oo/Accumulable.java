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
    private final Accumulable.WhenCombining policyForCombiningWithAbsentValues;

    protected Accumulable(
        A initialValue,
        boolean acceptNull,
        Function<W, A> weight,
        BinaryOperator<A> combiner,
        WhenCombining policyForCombiningWithAbsentValues
    ) {
        super(initialValue, acceptNull);
        this.weight = requireNonNull(weight);
        this.combiner = requireNonNull(combiner);
        this.policyForCombiningWithAbsentValues = requireNonNull(policyForCombiningWithAbsentValues);
    }

    public static <I, A> Accumulable<I, A> initiallyEmpty(
        Function<I, A> weight,
        BinaryOperator<A> combiner,
        WhenCombining policyForCombiningWithAbsentValues
    ) {
        return new Accumulable<>(null, true, weight, combiner, policyForCombiningWithAbsentValues);
    }

    public static <I, A> Accumulable<I, A> initially(
        A initialValue,
        Function<I, A> weight,
        BinaryOperator<A> combiner,
        WhenCombining policyForCombiningWithAbsentValues
    ) {
        return new Accumulable<>(initialValue, false, weight, combiner, policyForCombiningWithAbsentValues);
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
        return this.policyForCombiningWithAbsentValues.combine(
            Optional.ofNullable(this.get()),
            Optional.ofNullable(value),
            this.combiner
        ).orElse(null);
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
        WhenCombining policyForCombiningWithAbsentValues,
        UnaryOperator<T> finisher
    ) {
        return new Collector<>(weight, combiner, policyForCombiningWithAbsentValues, finisher);
    }

    public enum WhenCombining {
        PRESENT_WINS {
            @Override public <T> Optional<T> combine(
                Optional<T> maybe1, Optional<T> maybe2, BinaryOperator<T> combiner
            ) {
//                if (maybe1.isPresent() && maybe2.isPresent()) {
//                    return Optional.of(combiner.apply(maybe1.get(), maybe2.get()));
//                } else {
//                    return Optional.empty();
//                }
                if (maybe1.isPresent() && maybe2.isPresent()) {
                    return Optional.of(combiner.apply(maybe1.get(), maybe2.get()));
                } else if (maybe1.isPresent()) {
                    return Optional.of(maybe1.get());
                } else if (maybe2.isPresent()) {
                    return Optional.of(maybe2.get());
                } else {
                    return Optional.empty();
                }
            }
        },
        ABSENT_WINS {
            @Override public <T> Optional<T> combine(
                Optional<T> maybe1, Optional<T> maybe2, BinaryOperator<T> combiner
            ) {
                if (maybe1.isPresent() && maybe2.isPresent()) {
                    return Optional.of(combiner.apply(maybe1.get(), maybe2.get()));
                } else if (maybe1.isPresent()) {
                    return Optional.of(maybe1.get());
                } else if (maybe2.isPresent()) {
                    return Optional.of(maybe2.get());
                } else {
                    return Optional.empty();
                }
            }
        },
        ;

        public abstract <T> Optional<T> combine(
            Optional<T> maybe1, Optional<T> maybe2, BinaryOperator<T> combiner
        );

    }


    public static class Collector<O, T>
    implements java.util.stream.Collector<O, Accumulable<O, T>, T> {
        private final Function<Accumulable<O, T>, T> getAccumulated;
        private final Function<O, T> weight;
        private final BinaryOperator<T> combiner;
        private final WhenCombining policyForCombiningWithAbsentValues;
        private final UnaryOperator<T> finisher;

        private Collector(
            Function<O, T> weight,
            BinaryOperator<T> combiner,
            WhenCombining policyForCombiningWithAbsentValues,
            UnaryOperator<T> finisher
        )  {
            this.getAccumulated = MutableValue::get;
            this.weight = requireNonNull(weight);
            this.combiner = requireNonNull(combiner);
            this.policyForCombiningWithAbsentValues = requireNonNull(policyForCombiningWithAbsentValues);
            this.finisher = requireNonNull(finisher);
        }

        @Override public Supplier<Accumulable<O, T>> supplier() {
            return () -> Accumulable.initiallyEmpty(
                weight, combiner, policyForCombiningWithAbsentValues
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
