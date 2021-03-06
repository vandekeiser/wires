package fr.cla.wires.core.boxes.exampleusage.multipleinputs;

import fr.cla.wires.core.Clock;
import fr.cla.wires.core.Delay;
import fr.cla.wires.core.Signal;
import fr.cla.wires.core.Wire;
import fr.cla.wires.core.boxes.ReduceHomogeneousInputs;
import fr.cla.wires.support.functional.Indexed;
import fr.cla.wires.support.oo.Accumulable;

import java.util.List;
import java.util.function.BinaryOperator;
import java.util.function.Function;

import static java.util.Objects.requireNonNull;

//@formatter:off
/**
 * An example usage of how to connect wires to boxes.
 * @see fr.cla.wires.core.boxes.exampleusage
 */
public class ReduceMultipleIndexedAnd extends ReduceHomogeneousInputs<Indexed<Boolean>, Boolean> {

    private ReduceMultipleIndexedAnd(List<Wire<Indexed<Boolean>>> ins, Wire<Boolean> out, Clock clock) {
        this(ins, out, clock, DEFAULT_DELAY);
    }

    private ReduceMultipleIndexedAnd(List<Wire<Indexed<Boolean>>> ins, Wire<Boolean> out, Clock clock, Delay delay) {
        super(ins, out, clock, delay);
    }

    @Override protected Function<Indexed<Boolean>, Boolean> weight() {
        return identityWithoutCaringAboutTheIndex();
    }

    //I just want to test simplifying Signal::mapAndReduceIndexed
    private Function<Indexed<Boolean>, Boolean> identityWithoutCaringAboutTheIndex() {
        return Indexed::getValue;
    }

    @Override protected BinaryOperator<Boolean> accumulator() {
        return this::and;
    }

    private boolean and(boolean b1, boolean b2) {
        return b1 && b2;
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
    protected ReduceMultipleIndexedAnd startup() {
        super.startup();
        return this;
    }

    public static Builder ins(List<Wire<Indexed<Boolean>>> ins) {
        return new Builder(checkNoNulls(ins));
    }




    public static class Builder {
        private List<Wire<Indexed<Boolean>>> ins;
        private Wire<Boolean> out;

        private Builder(List<Wire<Indexed<Boolean>>> ins) {
            this.ins = checkNoNulls(ins);
        }

        public Builder out(Wire<Boolean> out) {
            this.out = requireNonNull(out);
            return this;
        }

        public ReduceMultipleIndexedAnd time(Clock clock) {
            Clock _clock = requireNonNull(clock);
            return new ReduceMultipleIndexedAnd(ins, out, _clock).startup();
        }
    }

}
//@formatter:on
