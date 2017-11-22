package fr.cla.wires.boxes.exampleusage.multipleinputs;

import fr.cla.wires.Clock;
import fr.cla.wires.Delay;
import fr.cla.wires.Wire;
import fr.cla.wires.boxes.ReduceHomogeneousInputs;

import java.util.Set;
import java.util.function.BinaryOperator;
import java.util.function.Function;

import static java.util.Objects.requireNonNull;

//@formatter:off
/**
 * An example usage of how to connect wires to boxes.
 * @see fr.cla.wires.boxes.exampleusage
 */
public class ReduceMultipleAnd extends ReduceHomogeneousInputs<Boolean, Boolean> {

    private ReduceMultipleAnd(Set<Wire<Boolean>> ins, Wire<Boolean> out, Clock clock) {
        this(ins, out, clock, DEFAULT_DELAY);
    }

    private ReduceMultipleAnd(Set<Wire<Boolean>> ins, Wire<Boolean> out, Clock clock, Delay delay) {
        super(ins, out, clock, delay);
    }

    @Override protected Function<Boolean, Boolean> accumulationValue() {
        return Function.identity();
    }
    @Override protected Boolean neutralElement() {
        return true;
    }
    @Override protected BinaryOperator<Boolean> reduction() {
        return this::and;
    }

    private boolean and(boolean b1, boolean b2) {
        return b1 && b2;
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
    @Override protected ReduceMultipleAnd startup() {
        super.startup();
        return this;
    }

    public static Builder ins(Set<Wire<Boolean>> ins) {
        return new Builder(checkNoNulls(ins));
    }




    public static class Builder {
        private Set<Wire<Boolean>> ins;
        private Wire<Boolean> out;

        private Builder(Set<Wire<Boolean>> ins) {
            this.ins = checkNoNulls(ins);
        }

        public Builder out(Wire<Boolean> out) {
            this.out = requireNonNull(out);
            return this;
        }

        public ReduceMultipleAnd time(Clock clock) {
            Clock _clock = requireNonNull(clock);
            return new ReduceMultipleAnd(ins, out, _clock).startup();
        }
    }

}
//@formatter:on
