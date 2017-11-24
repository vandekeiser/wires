package fr.cla.support;

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
        A initialValue,
        Function<T, A> accumulationValue,
        BiFunction<A, T, A> accumulator,
        BinaryOperator<A> combiner
    ) {
        super(initialValue);
        this.accumulationValue = requireNonNull(accumulationValue);
        this.accumulator = requireNonNull(accumulator);
        this.combiner = requireNonNull(combiner);
    }

    public static <T, A> Accumulable<A, T> initiallyEmpty(
        Function<T, A> accumulationValue,
        BiFunction<A, T, A> accumulator,
        BinaryOperator<A> combiner
    ) {
        return new Accumulable<>(null, accumulationValue, accumulator, combiner);
    }

    public static <T, A> Accumulable<A, T> initially(
        A initialValue,
        Function<T, A> accumulationValue,
        BiFunction<A, T, A> accumulator,
        BinaryOperator<A> combiner
    ) {
        return new Accumulable<>(initialValue, accumulationValue, accumulator, combiner);
    }

    public final void accumulate(T elt) {
        if(this.isPresent() ) {
            set(accumulator.apply(this.get(), elt));
        } else {
            set(accumulationValue.apply(elt));
        }
    }

    public final Accumulable<A, T> combine(Accumulable<A, T> that) {
        return
            this.isPresent() && that.isPresent() ?
            initially(
                combiner.apply(this.get(), that.get()),
                accumulationValue, accumulator, combiner
            ):
            initiallyEmpty(
                accumulationValue, accumulator, combiner
            )
        ;
    }

}
//@formatter:on
