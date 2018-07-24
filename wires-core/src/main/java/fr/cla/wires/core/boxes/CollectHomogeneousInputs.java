package fr.cla.wires.core.boxes;

import fr.cla.wires.core.Box;
import fr.cla.wires.core.Clock;
import fr.cla.wires.core.Delay;
import fr.cla.wires.core.Wire;
import fr.cla.wires.support.oo.Accumulable;

import java.util.List;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collector;

import static java.util.Objects.requireNonNull;

//@formatter:off
/**
 * A Box that has N inputs, but all of the same type of {@code Signal}.
 * @param <O> O like "observed".
 *           The type of {@code Signal} that transits on the observed {@code Wire}s.
 *           This means this Box's input is N {@code Wire}s of type {@code O}.
 * @param <T> T like "target".
 *           The type of {@code Signal} that transits on the target {@code Wire}
 *           This means this Box's output is 1 {@code Wire} of type {@code T}.
 */
public abstract class CollectHomogeneousInputs<O, T>
extends Box {

    private final List<Wire<O>> ins;
    private final Wire<T> out;

    protected CollectHomogeneousInputs(List<Wire<O>> ins, Wire<T> out, Clock clock) {
        this(ins, out, clock, DEFAULT_DELAY);
    }

    protected CollectHomogeneousInputs(List<Wire<O>> ins, Wire<T> out, Clock clock, Delay delay) {
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
    protected CollectHomogeneousInputs<O, T> startup() {
        ins.forEach(this::startup);
        return this;
    }

    /**
     * The DSL implemented by the "Staged Builder" pattern translates:
     * {@code
     *      onSignalChanged(in)
     *          .set(out)
     *          .from(ins)
     *          .collect(collector())
     *      ;
     * }
     * to the less linear:
     * {@code
     *      onSignalChanged(in,
     *          newSignal -> out.setSignal(
     *              collect(ins, collector())
     *          )
     *      );
     * }
     */
    private void startup(Wire<O> in) {
        this.<O, T>onSignalChanged(in)
            .set(out)
            .from(ins)
            .collect(collector())
        ;
    }

    private Collector<O, ?, T> collector() {
        return new Accumulable.Collector<>(
            accumulationValue(), accumulator()
        );
    }

    protected abstract Function<O, T> accumulationValue();
    protected abstract BinaryOperator<T> accumulator();

}
//@formatter:on
