package fr.cla.support.oo;

import java.util.Optional;
import java.util.function.BinaryOperator;
import java.util.function.Function;

//TODO: Also try to implement CollectMultipleAnd2 use this and extend CollectHomogeneousInputs,
//      as an alternative to using Accumulable
//                           and extending CollectHomogeneousInputsToOutputOfSameType.
//      Do a PBT test that both are equivalent.
//@formatter:off
public class SelfAccumulable<A> extends Accumulable<A, A> {

    protected SelfAccumulable(
        Optional<A> initialValue,
        BinaryOperator<A> accumulator
    ) {
        super(initialValue, Function.identity(), accumulator, accumulator );
    }

    public static <A> SelfAccumulable<A> initiallyUnset(
        BinaryOperator<A> accumulator
    ) {
        return new SelfAccumulable<>(Optional.empty(), accumulator);
    }

    public static <A> SelfAccumulable<A> initially(
        A initialValue,
        BinaryOperator<A> combiner
    ) {
        return new SelfAccumulable<>(Optional.of(initialValue), combiner);
    }

}
//@formatter:on