package fr.cla.wires.boxes;

import fr.cla.wires.Box;
import fr.cla.wires.Delay;
import fr.cla.wires.Time;
import fr.cla.wires.Wire;

import java.util.Set;
import java.util.function.BinaryOperator;
import java.util.function.Function;

import static java.util.Objects.requireNonNull;

//TODO: refact as extending CollectHomogeneousInputs (reduction->collector)
//@formatter:off
public abstract class ReduceHomogeneousInputs<O, T> extends Box {

    private final Set<Wire<O>> ins;
    private final Wire<T> out;

    protected ReduceHomogeneousInputs(Set<Wire<O>> ins, Wire<T> out, Time time) {
        this(ins, out, time, DEFAULT_DELAY);
    }

    protected ReduceHomogeneousInputs(Set<Wire<O>> ins, Wire<T> out, Time time, Delay delay) {
        super(delay, time);
        this.ins = requireNonNull(ins);
        this.out = requireNonNull(out);
    }

    /**
     * This method is used to not do the startup in the constructor,
     * to not let "this" escape through the method ref,
     * so that the Box is "properly constructed".
     *
     * @implNote The contract of overriders is to call super.startup(), return this:
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
     *          .map(mapping())
     *          .reduce(reduction(), neutralElement()
     *      ;
     * }
     * to the less linear:
     * {@code
     *      onSignalChanged(in,
     *          newIn -> out.setSignal(
     *              mapAndReduce(ins, mapping(), reduction(), neutralElement())
     *          )
     *      )
     * }
     */
    private void startup(Wire<O> in) {
        this.<O, T>onSignalChanged(in)
            .set(out)
            .from(ins)
            .map(mapping())
            .reduce(reduction(), neutralElement())
        ;
    }

    protected abstract Function<O,T> mapping();
    protected abstract T neutralElement();
    protected abstract BinaryOperator<T> reduction();

}
//@formatter:on
