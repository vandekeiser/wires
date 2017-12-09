package fr.cla.support.oo;

import java.util.function.BinaryOperator;
import java.util.function.Function;

import static java.util.Objects.requireNonNull;

//@formatter:off
public class Accumulable<I, A> extends Mutable<A> {

    private final Function<I, A> accumulationValue;
    private final BinaryOperator<A> accumulator;

    protected Accumulable(
        A initialValue,
        boolean acceptNull, 
        Function<I, A> accumulationValue,
        BinaryOperator<A> accumulator
    ) {
        super(initialValue, acceptNull);
        this.accumulationValue = requireNonNull(accumulationValue);
        this.accumulator = requireNonNull(accumulator);
    }

    public static <I, A> Accumulable<I, A> initiallyEmpty(
        Function<I, A> accumulationValue,
        BinaryOperator<A> accumulator
    ) {
        return new Accumulable<>(null, true, accumulationValue, accumulator);
    }

    public static <I, A> Accumulable<I, A> initially(
        A initialValue,
        Function<I, A> accumulationValue,
        BinaryOperator<A> accumulator
    ) {
        return new Accumulable<>(initialValue, false, accumulationValue, accumulator);
    }

    public final void accumulate(I elt) {
        if(this.isPresent() ) {
            set(accumulator.apply(this.get(), accumulationValue.apply(elt)));
        } else {
            set(accumulationValue.apply(elt));
        }
    }

    public final Accumulable<I, A> combine(Accumulable<I, A> that) {
        return this.isPresent() && that.isPresent() ?
            initially(
                accumulator.apply(this.get(), that.get()),
                accumulationValue, accumulator
            ) :
            initiallyEmpty(
                accumulationValue, accumulator
            )
        ;
    }

}
//@formatter:on
