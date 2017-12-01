package fr.cla.support.oo;

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
        boolean acceptNull,
        Function<A, A> accumulationValue,
        BinaryOperator<A> combiner
    ) {
        super(initialValue, acceptNull, accumulationValue, combiner, combiner );
    }

    public static <A> SelfAccumulable<A> initiallyEmpty(
        Function<A, A> accumulationValue,    
        BinaryOperator<A> combiner
    ) {
        return new SelfAccumulable<>(null, true, accumulationValue, combiner);
    }

    public static <A> SelfAccumulable<A> initially(
        A initialValue,
        Function<A, A> accumulationValue,
        BinaryOperator<A> combiner
    ) {
        return new SelfAccumulable<>(initialValue, false, accumulationValue, combiner);
    }

}
//@formatter:on