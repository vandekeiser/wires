package fr.cla.wires.exampleusage;

import fr.cla.wires.Box;
import fr.cla.wires.Delay;
import fr.cla.wires.Time;
import fr.cla.wires.Wire;

import static java.util.Objects.requireNonNull;

//@formatter:off
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
        this.<Boolean, Boolean>onSignalChanged(in)
            .set(out)
            .toResultOfApplying()
            .transformation(this::not)
        ;
        return this;
    }

    private boolean not(boolean b) {
        return !b;
    }

    public static Builder in(Wire<Boolean> in) {
        return new Builder(requireNonNull(in));
    }

    public static class Builder {
        private Wire<Boolean> in, out;

        private Builder(Wire<Boolean> in) {
            this.in = requireNonNull(in);
        }

        public Builder out(Wire<Boolean> out) {
            this.out = requireNonNull(out);
            return this;
        }

        public Not time(Time time) {
            Time _time = requireNonNull(time);
            return new Not(in, out, _time).startup();
        }
    }
}
//@formatter:on
