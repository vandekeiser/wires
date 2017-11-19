package fr.cla.wires;

import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;

//@formatter:off
/**
 * TODO
 * A CollectHomogeneousInputs whose T type (as in "target", output type)
 *  is the same as its O type (as in "observed", input type).
 * @param <O> O like "observed".
 *           The type of {@code Signal} that transits on both:
 *              -the observed {@code Wire}s.
 *              -the target {@code Wire}
 *           This means this Box's input is N {@code Wire}s of type {@code O},
 *           and its output is 1 {@code Wire} of type {@code O}.
 */
public abstract class CollectHomogeneousInputsToOutputOfSameType<O> extends CollectHomogeneousInputs<O, O> {

    protected CollectHomogeneousInputsToOutputOfSameType(Set<Wire<O>> ins, Wire<O> out, Time time) {
        this(ins, out, time, DEFAULT_DELAY);
    }

    protected CollectHomogeneousInputsToOutputOfSameType(Set<Wire<O>> ins, Wire<O> out, Time time, Delay delay) {
        super(ins, out, time, delay);
    }

    @Override protected final Function<O, O> accumulationValue() {
        return Function.identity();
    }
    @Override protected final BiFunction<O, O, O> accumulator() {
        return combiner();
    }

}
//@formatter:on
