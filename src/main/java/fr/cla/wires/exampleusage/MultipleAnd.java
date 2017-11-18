package fr.cla.wires.exampleusage;

import fr.cla.wires.Box;
import fr.cla.wires.Delay;
import fr.cla.wires.Time;
import fr.cla.wires.Wire;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

import static java.util.Objects.requireNonNull;
import static java.util.function.Function.identity;

public class MultipleAnd extends Box {

    private final Set<Wire<Boolean>> ins;
    private final Wire<Boolean> out;

    private MultipleAnd(Set<Wire<Boolean>> ins, Wire<Boolean> out, Time time) {
        this(ins, out, time, DEFAULT_DELAY);
    }

    private MultipleAnd(Set<Wire<Boolean>> ins, Wire<Boolean> out, Time time, Delay delay) {
        super(delay, time);
        this.ins = requireNonNull(ins);
        this.out = requireNonNull(out);
    }

    //Don't do the startup in the constructor to not let "this" escape through the method ref,
    // so that the Box is "properly constructed".
    private MultipleAnd startup() {
        ins.forEach(this::startup);
        return this;
    }

    private void startup(Wire<Boolean> in) {
        this.<Boolean, Boolean>onSignalChanged(in)
            .set(out)
            .withMapping(identity())
            .toResultOfReducing(this.ins)
            .withReduction(this::and, true)
        ;
    }

    private boolean and(boolean b1, boolean b2) {
        return b1 && b2;
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

    private static Set<Wire<Boolean>> checkNoNulls(Set<Wire<Boolean>> ins) {
        ins = new HashSet<>(requireNonNull(ins));
        if(ins.stream().anyMatch(w -> w == null)) {
            throw new NullPointerException("Detected null wires in " + ins);
        }
        return ins;
    }

}
