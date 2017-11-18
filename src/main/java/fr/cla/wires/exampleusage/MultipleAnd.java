package fr.cla.wires.exampleusage;

import fr.cla.wires.Delay;
import fr.cla.wires.ReduceHomogeneousInputs;
import fr.cla.wires.Time;
import fr.cla.wires.Wire;

import java.util.Set;
import java.util.function.BinaryOperator;
import java.util.function.Function;

import static java.util.Objects.requireNonNull;

//@formatter:off
public class MultipleAnd extends ReduceHomogeneousInputs<Boolean, Boolean> {

    private MultipleAnd(Set<Wire<Boolean>> ins, Wire<Boolean> out, Time time) {
        this(ins, out, time, DEFAULT_DELAY);
    }

    private MultipleAnd(Set<Wire<Boolean>> ins, Wire<Boolean> out, Time time, Delay delay) {
        super(ins, out, time, delay);
    }

    @Override protected Function<Boolean, Boolean> mapping() {
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

    @Override protected MultipleAnd startup() {
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

        public MultipleAnd time(Time time) {
            Time _time = requireNonNull(time);
            return new MultipleAnd(ins, out, _time).startup();
        }
    }

}
//@formatter:on
