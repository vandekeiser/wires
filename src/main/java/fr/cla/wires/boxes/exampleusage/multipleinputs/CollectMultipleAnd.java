package fr.cla.wires.boxes.exampleusage.multipleinputs;

import fr.cla.wires.Clock;
import fr.cla.wires.Delay;
import fr.cla.wires.Wire;
import fr.cla.wires.boxes.CollectHomogeneousInputsToOutputOfSameType;

import java.util.List;
import java.util.Set;
import java.util.function.BinaryOperator;

import static java.util.Objects.requireNonNull;

//@formatter:off
/**
 * An example usage of how to connect wires to boxes.
 * @see fr.cla.wires.boxes.exampleusage
 */
public final class CollectMultipleAnd
extends CollectHomogeneousInputsToOutputOfSameType<Boolean> {

    private CollectMultipleAnd(List<Wire<Boolean>> ins, Wire<Boolean> out, Clock clock) {
        this(ins, out, clock, DEFAULT_DELAY);
    }

    private CollectMultipleAnd(List<Wire<Boolean>> ins, Wire<Boolean> out, Clock clock, Delay delay) {
        super(ins, out, clock, delay);
    }

    @Override protected BinaryOperator<Boolean> combiner() {
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
    @Override protected CollectMultipleAnd startup() {
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

        public CollectMultipleAnd time(Clock clock) {
            Clock _clock = requireNonNull(clock);
            return new CollectMultipleAnd(ins, out, _clock).startup();
        }
    }

}
//@formatter:on
