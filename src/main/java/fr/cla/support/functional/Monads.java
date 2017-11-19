package fr.cla.support.functional;

import java.util.Optional;
import java.util.function.BinaryOperator;
import java.util.function.Function;

//@formatter:off
//TODO? liftMutable/liftAccumulable
public final class Monads {

    public static <T> BinaryOperator<Optional<T>> liftOptional(BinaryOperator<T> reducer) {
        return (maybe1, maybe2) ->
            maybe1.isPresent() && maybe2.isPresent() ?
            Optional.of(reducer.apply(maybe1.get(), maybe2.get())) :
            Optional.empty()
        ;
    }

    public static <O, T> Function<Optional<O>, Optional<T>> liftOptional(
        Function<O, T> accumulationValue
    ) {
        return maybe1 -> maybe1.map(accumulationValue);
    }

}
//@formatter:on
