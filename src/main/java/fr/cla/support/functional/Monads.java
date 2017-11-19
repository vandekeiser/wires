package fr.cla.support.functional;

import java.util.Optional;
import java.util.function.BinaryOperator;

//@formatter:off
public final class Monads {

    public static <T> BinaryOperator<Optional<T>> liftOptional(BinaryOperator<T> reducer) {
        return (maybe1, maybe2) ->
            maybe1.isPresent() && maybe2.isPresent() ?
            Optional.of(reducer.apply(maybe1.get(), maybe2.get())) :
            Optional.empty()
        ;
    }

}
//@formatter:on
