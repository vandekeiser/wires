package fr.cla.wires.exampleusage;

import fr.cla.wires.CollectHomogeneousInputs;
import fr.cla.wires.Delay;
import fr.cla.wires.Time;
import fr.cla.wires.Wire;

import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;

import static java.util.Objects.requireNonNull;

//@formatter:off
public class CollectMultipleAnd extends CollectHomogeneousInputs<Boolean, Boolean> {

    private CollectMultipleAnd(Set<Wire<Boolean>> ins, Wire<Boolean> out, Time time) {
        this(ins, out, time, DEFAULT_DELAY);
    }

    private CollectMultipleAnd(Set<Wire<Boolean>> ins, Wire<Boolean> out, Time time, Delay delay) {
        super(ins, out, time, delay);
    }

    private boolean and(boolean b1, boolean b2) {
        return b1 && b2;
    }

    @Override protected CollectMultipleAnd startup() {
        super.startup();
        return this;
    }

    @Override protected Function<Boolean, Boolean> accumulatorConstructor() {
        return Function.identity();
    }
    @Override protected BiFunction<Boolean, Boolean, Boolean> accumulator() {
        return this::and;
    }
    @Override protected BinaryOperator<Boolean> combiner() {
        return this::and;
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
