package fr.cla;

import java.util.Optional;
import java.util.function.BinaryOperator;

//@formatter:off
public class Monads {

    public static <T> BinaryOperator<Optional<T>> liftOptional(BinaryOperator<T> reducer) {
        return (maybeValue1, maybeValue2) -> {
            if(! maybeValue1.isPresent() || ! maybeValue2.isPresent()) {
                return Optional.empty();
            }
            T v1 = maybeValue1.get();
            T v2 = maybeValue2.get();
            return Optional.of(reducer.apply(v1, v2));
        };
    }

}
//@formatter:on
