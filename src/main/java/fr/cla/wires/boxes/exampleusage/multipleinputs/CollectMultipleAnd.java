package fr.cla.wires.boxes.exampleusage.multipleinputs;

import fr.cla.wires.Delay;
import fr.cla.wires.Time;
import fr.cla.wires.Wire;
import fr.cla.wires.boxes.CollectHomogeneousInputsToOutputOfSameType;

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

    private CollectMultipleAnd(Set<Wire<Boolean>> ins, Wire<Boolean> out, Time time) {
        this(ins, out, time, DEFAULT_DELAY);
    }

    private CollectMultipleAnd(Set<Wire<Boolean>> ins, Wire<Boolean> out, Time time, Delay delay) {
        super(ins, out, time, delay);
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
     * @implNote The contract of overriders is to call super.startup(), return this:
     * This method is only not marked final as a convenience to allow covariant return.
     *
     * @return this Box, started.
     */
    @Override protected CollectMultipleAnd startup() {
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
            this.ins = requireNonNull(ins);
        }

        public Builder out(Wire<Boolean> out) {
            this.out = requireNonNull(out);
            return this;
        }

        public CollectMultipleAnd time(Time time) {
            Time _time = requireNonNull(time);
            return new CollectMultipleAnd(ins, out, _time).startup();
        }
    }

}
//@formatter:on
