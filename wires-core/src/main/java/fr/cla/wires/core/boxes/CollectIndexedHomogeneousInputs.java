package fr.cla.wires.core.boxes;

import fr.cla.wires.support.functional.Indexed;
import fr.cla.wires.support.oo.Accumulable;
import fr.cla.wires.core.Box;
import fr.cla.wires.core.Clock;
import fr.cla.wires.core.Delay;
import fr.cla.wires.core.Wire;

import java.util.List;
import java.util.function.*;
import java.util.stream.Collector;

import static java.util.Collections.singletonList;
import static java.util.Objects.requireNonNull;

//@formatter:off
/**
 * Take the index of observed Wires into account for neural networks (use a weight matrix).
 * @param <O> The type of Signal that transits on observed Wires, same as in Box
 * @param <T> The type of Signal that transits on target Wires, same as in Box
 * @param <I> The type of index for observed Wires
 */
public abstract class CollectIndexedHomogeneousInputs<O, T, I>
extends Box {

    private final List<Wire<O>> ins;
    private final List<Wire<T>> outs;

    protected CollectIndexedHomogeneousInputs(List<Wire<O>> ins, Wire<T> out, Clock clock) {
        this(ins, out, clock, DEFAULT_DELAY);
    }

    protected CollectIndexedHomogeneousInputs(List<Wire<O>> ins, Wire<T> out, Clock clock, Delay delay) {
        this(ins, singletonList(out), clock, delay);
    }

    protected CollectIndexedHomogeneousInputs(List<Wire<O>> ins, List<Wire<T>> outs, Clock clock, Delay delay) {
        super(clock, delay);
        this.ins = checkNoNulls(ins);
        this.outs = checkNoNulls(outs);
    }


    /**
     * This method is used to not do the startup in the constructor,
     * to not let "this" escape through the method ref,
     * so that the Box is "properly constructed".
     *
     * @implNote The contract for overriders is to call super.startup(), return this:
     * This method is only not marked final as a convenience to allow covariant return.
     *
     * @return this Box, started.
     */
    protected CollectIndexedHomogeneousInputs<O, T, I> startup() {
        ins.forEach(this::startup);
        return this;
    }

    private CollectIndexedHomogeneousInputs<O, T, I> startup(Wire<O> in) {
        outs.forEach(out -> startup(in, out));
        return this;
    }

    private void startup(Wire<O> in, Wire<T> out) {
        this.<O, T>onSignalChanged(in)
            .set(out)
            .from(ins)
            .collectIndexed(collector())
        ;
    }

    private Collector<Indexed<O>, ?, T> collector() {
        return Accumulable.indexedCollector(
           accumulationValue(), accumulator(), finisher()
        );
    }

    protected abstract Function<Indexed<O>, T> accumulationValue();
    protected abstract BinaryOperator<T> accumulator();
    protected abstract UnaryOperator<T> finisher();






}
//@formatter:on
