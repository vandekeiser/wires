package fr.cla.wires.core.boxes;

import fr.cla.wires.core.Box;
import fr.cla.wires.core.Clock;
import fr.cla.wires.core.Delay;
import fr.cla.wires.core.Wire;
import fr.cla.wires.support.functional.Indexed;

import java.util.List;
import java.util.function.BinaryOperator;
import java.util.function.Function;

import static java.util.Objects.requireNonNull;

//@formatter:off
/**
 * Try this abstraction to take index into account for neural networks (use weigth matrix).
 * @param <O> The type of Signal that transits on observed Wires, same as in Box
 * @param <T> The type of Signal that transits on target Wires, same as in Box
 * @param <I> The type of index for observed Wires
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
     * to avoid letting "this" escape through the method ref,
     * so that the Box is "properly constructed".
     *
     * The contract for overriders is to call super.startup(), return this, and that's it:
     *  this class already knows all there is to know about startup,
     *  since it knows about all the in/out Wires and that's all there is to startup.
     * This method is only not marked final as a convenience to allow covariant return.
     *
     * @return this Box, started.
     */
    @Override
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
     *          .map(weight())
     *          .reduce(accumulator(), identity()
     *      ;
     * }
     * to the less linear:
     * {@code
     *      onSignalChanged(in,
     *          newSignal -> out.setSignal(
     *              mapAndReduce(ins, weight(), accumulator(), identity())
     *          )
     *      )
     * }
     */
    private void startup(Wire<O> in) {
        this.<O, T>onSignalChanged(in)
            .set(out)
            .from(ins)
            .mapIndexed(weight())
            .reduce(accumulator(), identity())
        ;
    }

    protected abstract Function<Indexed<O>, T> weight();
    protected abstract BinaryOperator<T> accumulator();
    protected abstract T identity();

}
//@formatter:on
