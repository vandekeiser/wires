package fr.cla;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;

import static java.util.Objects.requireNonNull;

//@formatter:off
public class SameAccumulable<A> extends Accumulable<A, A> {

    private SameAccumulable(
        Optional<A> initialValue,
        BinaryOperator<A> accumulator
    ) {
        super(
            initialValue,
            Function.identity(),
            (acc, cur) -> accumulator.apply(acc, cur),
            (acc, cur) -> accumulator.apply(acc, cur)
        );
    }

}
//@formatter:on