package fr.cla.wires.boxes;

import fr.cla.support.functional.Indexed;
import fr.cla.wires.Box;
import fr.cla.wires.Clock;
import fr.cla.wires.Delay;
import fr.cla.wires.Wire;

import java.util.List;
import java.util.function.BinaryOperator;
import java.util.function.Function;

import static java.util.Objects.requireNonNull;

//@formatter:off

/**
 * Try this abstraction to take index into account for neural networks (use weigth matrix).
 * @param <O> Same as in Box
 * @param <T> Same as in Box
 * @param <I> Same as in Box
 */
public abstract class ReduceIndexedHomogeneousInputs<O, T, I>
extends Box {

    private final List<Wire<O>> ins;
    private final Wire<T> out;

    protected ReduceIndexedHomogeneousInputs(List<Wire<O>> ins, Wire<T> out, Clock clock) {
        this(ins, out, clock, DEFAULT_DELAY);
    }

    protected ReduceIndexedHomogeneousInputs(List<Wire<O>> ins, Wire<T> out, Clock clock, Delay delay) {
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
    protected ReduceIndexedHomogeneousInputs<O, T, I> startup() {
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
     *          newIn -> out.setSignal(
     *              mapAndReduce(ins, accumulationValue(), accumulator(), identity())
     *          )
     *      )
     * }
     */
    private void startup(Wire<O> in) {
        this.<O, T>onSignalChanged(in)
            .set(out)
            .from(ins)
            .mapIndexed(accumulationValue())
            .reduce(accumulator(), identity())
        ;
    }

    protected abstract Function<Indexed<O>, T> accumulationValue();
    protected abstract BinaryOperator<T> accumulator();
    protected abstract T identity();

}
//@formatter:on
