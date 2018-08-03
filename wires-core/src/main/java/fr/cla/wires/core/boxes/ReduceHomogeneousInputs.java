package fr.cla.wires.core.boxes;

import fr.cla.wires.core.Box;
import fr.cla.wires.core.Clock;
import fr.cla.wires.core.Delay;
import fr.cla.wires.core.Wire;
import fr.cla.wires.support.oo.Accumulable;

import java.util.List;
import java.util.function.BinaryOperator;
import java.util.function.Function;

import static java.util.Objects.requireNonNull;

/**
 * A Box that has N inputs, but all of the same type of {@code Signal},
 *  and the ouptut of which is the result is map(weight).reduce(accumulator, identity)
 * @param <O> The type of Signal that transits on observed Wires, same as in Box
 * @param <T> The type of Signal that transits on target Wires, same as in Box
 */
//@formatter:off
public abstract class ReduceHomogeneousInputs<O, T>
extends Box {

    private final List<Wire<O>> ins;
    private final Wire<T> out;

    protected ReduceHomogeneousInputs(
        List<Wire<O>> ins,
        Wire<T> out,
        Clock clock,
        Accumulable.WhenCombining policyForCombiningWithAbsentValues
    ) {
        this(ins, out, clock, DEFAULT_DELAY, policyForCombiningWithAbsentValues);
    }

    protected ReduceHomogeneousInputs(
        List<Wire<O>> ins,
        Wire<T> out,
        Clock clock,
        Delay delay,
        Accumulable.WhenCombining policyForCombiningWithAbsentValues
    ) {
        super(clock, delay, policyForCombiningWithAbsentValues);
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
     *          .map(weight())
     *          .reduce(accumulator(), identity())
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
            .map(weight())
            .reduce(accumulator())
        ;
    }

    protected abstract Function<O,T> weight();
    protected abstract BinaryOperator<T> accumulator();

}
//@formatter:on
