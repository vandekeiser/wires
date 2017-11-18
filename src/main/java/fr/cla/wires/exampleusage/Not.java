package fr.cla.wires.exampleusage;

import fr.cla.wires.Box;
import fr.cla.wires.Delay;
import fr.cla.wires.Time;
import fr.cla.wires.Wire;

import static java.util.Objects.requireNonNull;

public class Not extends Box {

    private final Wire<Boolean> in, out;

    private Not(Wire<Boolean> in, Wire<Boolean> out, Time time) {
        this(in, out, time, DEFAULT_DELAY);
    }

    private Not(Wire<Boolean> in, Wire<Boolean> out, Time time, Delay delay) {
        super(delay, time);
        this.in = requireNonNull(in);
        this.out = requireNonNull(out);
    }

    //Don't do the startup in the constructor to not let "this" escape through the method ref,
    // so that the Box is "properly constructed".
    private Not startup() {
        this.<Boolean, Boolean>onSignalChanged(in).set(out).toResultOf(this::not);
        return this;
    }

    private boolean not(boolean b) {
        return !b;
    }

    public static Builder in(Wire<Boolean> in) {
        return new Builder(in);
    }

    public static class Builder {
        private Wire<Boolean> in, out;

        public Builder(Wire<Boolean> in) {
            this.in = in;
        }

        public Builder out(Wire<Boolean> out) {
            this.out = out;
            return this;
        }

        public Not time(Time time) {
            return new Not(in, out, time).startup();
        }
    }
}
