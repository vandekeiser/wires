package fr.cla.wires;

import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;

//@formatter:off
public abstract class CollectHomogeneousInputsToOutputOfSameType<O> extends CollectHomogeneousInputs<O, O> {

    protected CollectHomogeneousInputsToOutputOfSameType(Set<Wire<O>> ins, Wire<O> out, Time time) {
        this(ins, out, time, DEFAULT_DELAY);
    }

    protected CollectHomogeneousInputsToOutputOfSameType(Set<Wire<O>> ins, Wire<O> out, Time time, Delay delay) {
        super(ins, out, time, delay);
    }

    @Override protected final Function<O, O> accumulatorConstructor() {
        return Function.identity();
    }
    @Override protected final BiFunction<O, O, O> accumulator() {
        return combiner();
    }

}
//@formatter:on
