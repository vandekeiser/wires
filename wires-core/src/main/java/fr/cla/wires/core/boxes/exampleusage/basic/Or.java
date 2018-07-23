package fr.cla.wires.core.boxes.exampleusage.basic;

import fr.cla.wires.core.Box;
import fr.cla.wires.core.Clock;
import fr.cla.wires.core.Delay;
import fr.cla.wires.core.Wire;

import static java.util.Objects.requireNonNull;

//@formatter:off
/**
 * An example usage of how to connect wires to boxes.
 * @see fr.cla.wires.core.boxes.exampleusage
 */
public final class Or extends Box {

    private final Wire<Boolean> in1, in2, out;

    private Or(Wire<Boolean> in1, Wire<Boolean> in2, Wire<Boolean> out, Clock clock) {
        this(in1, in2, out, clock, DEFAULT_DELAY);
    }

    private Or(Wire<Boolean> in1, Wire<Boolean> in2, Wire<Boolean> out, Clock clock, Delay delay) {
        super(clock, delay);
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
     *          .transformation(this::or, in2)
     *      ;
     * }
     * to the less linear:
     * {@code
     *      onSignalChanged(in1,
     *          newIn1 -> out.setSignal(
     *              Signal.map(newIn1, in2.getSignal(), this::or)
     *          )
     *      );
     * }
     */
    @Override protected Or startup() {
        this.<Boolean, Boolean>onSignalChanged(in1)
            .set(out)
            .toResultOfApplying()
            .transformation(Boolean::logicalOr, in2)
        ;
        this.<Boolean, Boolean>onSignalChanged(in2)
            .set(out)
            .toResultOfApplying()
            .transformation(in1, Boolean::logicalOr)
        ;
        return this;
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

        public Or time(Clock clock) {
            Clock _clock = requireNonNull(clock);
            return new Or(in1, in2, out, _clock).startup();
        }
    }

}
//@formatter:on
