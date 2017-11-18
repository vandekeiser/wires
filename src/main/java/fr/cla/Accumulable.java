package fr.cla;

import java.util.Optional;
import java.util.function.BinaryOperator;

import static java.util.Objects.requireNonNull;

public class Accumulable<T> extends Mutable<T> {

    private final BinaryOperator<T> accumulator;

    private Accumulable(
        Optional<T> initialValue,
        BinaryOperator<T> accumulator
    ) {
        super(initialValue);
        this.accumulator = requireNonNull(accumulator);
    }

    public static <T> Accumulable<T> initiallyUnset(BinaryOperator<T> accumulator) {
        return new Accumulable<>(Optional.empty(), accumulator);
    }

    public static <T> Accumulable<T> initially(T t, BinaryOperator<T> accumulator) {
        return new Accumulable<>(Optional.of(t), accumulator);
    }

    public void accumulate(Optional<T> maybe) {
        if(this.isPresent() && maybe.isPresent()) {
            set(accumulator.apply(this.get(), maybe.get()));
        } else if(maybe.isPresent()) {
            set(maybe);
        }
    }

    public Accumulable<T> combine(Accumulable<T> that) {
        if(!this.isPresent()) return initiallyUnset(accumulator);
        if(!that.isPresent()) return initiallyUnset(accumulator);
        return initially(
            accumulator.apply(this.get(), that.get()),
            accumulator
        );
    }

}
