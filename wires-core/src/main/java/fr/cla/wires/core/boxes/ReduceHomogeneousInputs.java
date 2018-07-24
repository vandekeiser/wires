package fr.cla.wires.core.boxes;

import fr.cla.wires.core.Box;
import fr.cla.wires.core.Clock;
import fr.cla.wires.core.Delay;
import fr.cla.wires.core.Wire;

import java.util.List;
import java.util.function.BinaryOperator;
import java.util.function.Function;

import static java.util.Objects.requireNonNull;

//TODO: refact as extending CollectHomogeneousInputs (accumulator->collector)

/**
 * TODO javadoc in same style as CollectHomogeneousInputsToOutputOfSameType
 */
//@formatter:off
public abstract class ReduceHomogeneousInputs<O, T>
extends Box {

    private final List<Wire<O>> ins;
    private final Wire<T> out;

    protected ReduceHomogeneousInputs(List<Wire<O>> ins, Wire<T> out, Clock clock) {
        this(ins, out, clock, DEFAULT_DELAY);
    }

    protected ReduceHomogeneousInputs(List<Wire<O>> ins, Wire<T> out, Clock clock, Delay delay) {
        super(clock, delay);
        this.ins = checkNoNulls(ins);
        this.out = requireNonNull(out);
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
    protected ReduceHomogeneousInputs<O, T> startup() {
        ins.forEach(this::startup);
        return this;
    }

    /**
     * The DSL implemented by the "Staged Builder" pattern translates:
     * {@code
     *      onSignalChanged(in)
     *          .set(out)
     *          .from(ins)
     *          .map(accumulationValue())
     *          .reduce(accumulator(), identity()
     *      ;
     * }
     * to the less linear:
     * {@code
     *      onSignalChanged(in,
     *          newSignal -> out.setSignal(
     *              mapAndReduce(ins, accumulationValue(), accumulator(), identity())
     *          )
     *      )
     * }
     */
    private void startup(Wire<O> in) {
        this.<O, T>onSignalChanged(in)
            .set(out)
            .from(ins)
            .map(accumulationValue())
            .reduce(accumulator(), identity())
        ;
    }

    protected abstract Function<O,T> accumulationValue();
    protected abstract BinaryOperator<T> accumulator();
    protected abstract T identity();

}
//@formatter:on
