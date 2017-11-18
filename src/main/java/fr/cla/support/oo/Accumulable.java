package fr.cla.support.oo;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;

import static java.util.Objects.requireNonNull;

//@formatter:off
public class Accumulable<A, T> extends Mutable<A> {

    private final Function<T, A> accumulationValue;
    private final BiFunction<A, T, A> accumulator;
    private final BinaryOperator<A> combiner;

    protected Accumulable(
        Optional<A> initialValue,
        Function<T, A> accumulationValue,
        BiFunction<A, T, A> accumulator,
        BinaryOperator<A> combiner
    ) {
        super(initialValue);
        this.accumulationValue = requireNonNull(accumulationValue);
        this.accumulator = requireNonNull(accumulator);
        this.combiner = requireNonNull(combiner);
    }

    public static <T, A> Accumulable<A, T> initiallyUnset(
        Function<T, A> accumulationValue,
        BiFunction<A, T, A> accumulator,
        BinaryOperator<A> combiner
    ) {
        return new Accumulable<>(Optional.empty(), accumulationValue, accumulator, combiner);
    }

    public static <T, A> Accumulable<A, T> initially(
        A initialValue,
        Function<T, A> accumulationValue,
        BiFunction<A, T, A> accumulator,
        BinaryOperator<A> combiner
    ) {
        return new Accumulable<>(Optional.of(initialValue), accumulationValue, accumulator, combiner);
    }

    public final void accumulate(Optional<T> maybe) {
        if(this.isPresent() && maybe.isPresent()) {
            set(accumulator.apply(this.get(), maybe.get()));
        } else if(maybe.isPresent()) {
            set(accumulationValue.apply(maybe.get()));
        }
    }

    public final Accumulable<A, T> combine(Accumulable<A, T> that) {
        return
            this.isPresent() && that.isPresent() ?
            initially(
                combiner.apply(this.get(), that.get()),
                accumulationValue, accumulator, combiner
            ):
            initiallyUnset(accumulationValue, accumulator, combiner)
        ;
    }

}
//@formatter:on
