package fr.cla.support;

import java.util.function.BinaryOperator;
import java.util.function.Function;

//TODO: Also try to implement CollectMultipleAnd2 use this and extend CollectHomogeneousInputs,
//      as an alternative to using Accumulable
//                           and extending CollectHomogeneousInputsToOutputOfSameType.
//      Do a PBT test that both are equivalent.
//@formatter:off
public class SelfAccumulable<A> extends Accumulable<A, A> {

    protected SelfAccumulable(
        A initialValue,
        BinaryOperator<A> combiner
    ) {
        super(initialValue, Function.identity(), combiner, combiner );
    }

    public static <A> SelfAccumulable<A> initiallyEmpty(
        BinaryOperator<A> combiner
    ) {
        return new SelfAccumulable<>(null, combiner);
    }

    public static <A> SelfAccumulable<A> initially(
        A initialValue,
        BinaryOperator<A> combiner
    ) {
        return new SelfAccumulable<>(initialValue, combiner);
    }

}
//@formatter:on