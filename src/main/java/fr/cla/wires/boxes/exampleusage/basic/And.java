package fr.cla.wires.boxes.exampleusage.basic;

import fr.cla.wires.Box;
import fr.cla.wires.Delay;
import fr.cla.wires.Time;
import fr.cla.wires.Wire;

import static java.util.Objects.requireNonNull;

//@formatter:off
/**
 * An example usage of how to connect wires to boxes.
 * @see fr.cla.wires.boxes.exampleusage
 */
public final class And extends Box {

    private final Wire<Boolean> in1, in2, out;

    private And(Wire<Boolean> in1, Wire<Boolean> in2, Wire<Boolean> out, Time time) {
        this(in1, in2, out, time, DEFAULT_DELAY);
    }

    private And(Wire<Boolean> in1, Wire<Boolean> in2, Wire<Boolean> out, Time time, Delay delay) {
        super(time, delay);
        this.in1 = requireNonNull(in1);
        this.in2 = requireNonNull(in2);
        this.out = requireNonNull(out);
    }

    /**
     * This method is used to not do the startup in the constructor,
     * to not let "this" escape through the method ref,
     * so that the Box is "properly constructed".
     *
     * The DSL implemented by the "Staged Builder" pattern translates:
     * {@code
     *      onSignalChanged(in1)
     *          .set(out)
     *          .toResultOfApplying()
     *          .transformation(this::and, in2)
     *      ;
     * }
     * to the less linear:
     * {@code
     *      onSignalChanged(in1,
     *          newIn1 -> out.setSignal(
     *              Signal.map(newIn1, in2.getSignal(), this::and)
     *          )
     *      );
     * }
     */
    private And startup() {
        this.<Boolean, Boolean>onSignalChanged(in1)
            .set(out)
            .toResultOfApplying()
            .transformation(this::and, in2)
        ;
        this.<Boolean, Boolean>onSignalChanged(in2)
            .set(out)
            .toResultOfApplying()
            .transformation(in1, this::and)
        ;
        return this;
    }

    private boolean and(boolean b1, boolean b2) {
        return b1 && b2;
    }

    public static Builder in1(Wire<Boolean> in1) {
        return new Builder(requireNonNull(in1));
    }




    public static class Builder {
        private Wire<Boolean> in1, in2, out;

        private Builder(Wire<Boolean> in) {
            this.in1 = requireNonNull(in);
        }

        public Builder in2(Wire<Boolean> in2) {
            this.in2 = requireNonNull(in2);
            return this;
        }

        public Builder out(Wire<Boolean> out) {
            this.out = requireNonNull(out);
            return this;
        }

        public And time(Time time) {
            Time _time = requireNonNull(time);
            return new And(in1, in2, out, _time).startup();
        }
    }

}
//@formatter:on
