package fr.cla;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;

import static java.util.Objects.requireNonNull;

//@formatter:off
public class Accumulable<A, T> extends Mutable<A> {

    private final Function<T, A> converter;
    private final BiFunction<A, T, A> accumulator;
    private final BinaryOperator<A> combiner;

    protected Accumulable(
        Optional<A> initialValue,
        Function<T, A> converter,
        BiFunction<A, T, A> accumulator,
        BinaryOperator<A> combiner
    ) {
        super(initialValue);
        this.converter = requireNonNull(converter);
        this.accumulator = requireNonNull(accumulator);
        this.combiner = requireNonNull(combiner);
    }

    public static <T, A> Accumulable<A, T> initiallyUnset(
        Function<T, A> accumulatorConstructor,
        BiFunction<A, T, A> accumulator,
        BinaryOperator<A> combiner
    ) {
        return new Accumulable<>(Optional.empty(), accumulatorConstructor, accumulator, combiner);
    }

    public static <T, A> Accumulable<A, T> initially(
        A t,
        Function<T, A> converter,
        BiFunction<A, T, A> accumulator,
        BinaryOperator<A> combiner
    ) {
        return new Accumulable<>(Optional.of(t), converter, accumulator, combiner);
    }

    public void accumulate(Optional<T> maybe) {
        if(this.isPresent() && maybe.isPresent()) {
            set(accumulator.apply(this.get(), maybe.get()));
        } else if(maybe.isPresent()) {
            set(converter.apply(maybe.get()));
        }
    }

    public Accumulable<A, T> combine(Accumulable<A, T> that) {
        if(!this.isPresent()) return initiallyUnset(converter, accumulator, combiner);
        if(!that.isPresent()) return initiallyUnset(converter, accumulator, combiner);

        return initially(
            combiner.apply(this.get(), that.get()),
            converter,
            accumulator,
            combiner
        );
    }

}
//@formatter:on
