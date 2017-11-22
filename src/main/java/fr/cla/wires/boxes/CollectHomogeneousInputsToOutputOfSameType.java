package fr.cla.wires.boxes;

import fr.cla.wires.Clock;
import fr.cla.wires.Delay;
import fr.cla.wires.Wire;

import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;

//@formatter:off
/**
 * TODO @links & CO
 * -->"Explaining the intention" approach to javadoc:
 * Does like its parent CollectHomogeneousInputs: "
 *      Use a java.util.stream.Collector to compute the Signal to write to the target Wire
 *      when a Signal on an observed Wires change.
 * ",
 * but since here the output type is the same as the the inputs type,
 * instead of the the more general approach of returning a Collector,
 * we can simplify to just
 * returning the {@code BinaryOperator<Boolean> combiner} used by the Collector.
 *
 * -->"More formal / JDK style" approach to javadoc:
 * A CollectHomogeneousInputs whose T type (as in "target", output type)
 *  is the same as its O type (as in "observed", input type).
 * @param <O> O like "observed".
 *           The type of {@code Signal} that transits on both:
 *              -the observed {@code Wire}s.
 *              -the target {@code Wire}
 *           This means this Box's input is N {@code Wire}s of type {@code O},
 *           and its output is 1 {@code Wire} of type {@code O}.
 */
public abstract class CollectHomogeneousInputsToOutputOfSameType<O>
extends CollectHomogeneousInputs<O, O> {

    protected CollectHomogeneousInputsToOutputOfSameType(
    Set<Wire<O>> ins, Wire<O> out, Clock clock
    ) {
        this(ins, out, clock, DEFAULT_DELAY);
    }

    protected CollectHomogeneousInputsToOutputOfSameType(
    Set<Wire<O>> ins, Wire<O> out, Clock clock, Delay delay
    ) {
        super(ins, out, clock, delay);
    }

    @Override protected final Function<O, O> accumulationValue() {
        return Function.identity();
    }
    @Override protected final BiFunction<O, O, O> accumulator() {
        return combiner();
    }

}
//@formatter:on
