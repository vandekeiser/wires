package fr.cla.wires.core.boxes.exampleusage.multipleinputs;

import fr.cla.wires.core.Clock;
import fr.cla.wires.core.Delay;
import fr.cla.wires.core.Wire;
import fr.cla.wires.core.boxes.ReduceHomogeneousInputs;

import java.util.List;
import java.util.function.BinaryOperator;
import java.util.function.Function;

import static java.util.Objects.requireNonNull;

//@formatter:off
/**
 * An example usage of how to connect wires to boxes.
 * @see fr.cla.wires.core.exampleusage
 */
public class ReduceMultipleAnd extends ReduceHomogeneousInputs<Boolean, Boolean> {

    private ReduceMultipleAnd(List<Wire<Boolean>> ins, Wire<Boolean> out, Clock clock) {
        this(ins, out, clock, DEFAULT_DELAY);
    }

    private ReduceMultipleAnd(List<Wire<Boolean>> ins, Wire<Boolean> out, Clock clock, Delay delay) {
        super(ins, out, clock, delay);
    }

    @Override protected Function<Boolean, Boolean> accumulationValue() {
        return Function.identity();
    }
    @Override protected Boolean identity() {
        return true;
    }
    @Override protected BinaryOperator<Boolean> accumulator() {
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

    public static Builder ins(List<Wire<Boolean>> ins) {
        return new Builder(checkNoNulls(ins));
    }




    public static class Builder {
        private List<Wire<Boolean>> ins;
        private Wire<Boolean> out;

        private Builder(List<Wire<Boolean>> ins) {
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
