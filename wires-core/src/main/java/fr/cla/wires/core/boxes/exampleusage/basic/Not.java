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
public final class Not extends Box {

    private final Wire<Boolean> in, out;

    private Not(Wire<Boolean> in, Wire<Boolean> out, Clock clock) {
        this(in, out, clock, DEFAULT_DELAY);
    }

    private Not(Wire<Boolean> in, Wire<Boolean> out, Clock clock, Delay delay) {
        super(clock, delay);
        this.in = requireNonNull(in);
        this.out = requireNonNull(out);
    }

    /**
     * This method is used to not do the startup in the constructor,
     * to not let "this" escape through the method ref,
     * so that the Box is "properly constructed".
     *
     * The DSL implemented by the "Staged Builder" pattern translates:
     * {@code
     *      onSignalChanged(in)
     *          .set(out)
     *          .toResultOfApplying()
     *          .signalValueTransformation(this::not)
     *      ;
     * }
     * to the less linear:
     * {@code
     *      onSignalChanged(observed,
     *          newSignal -> target.setSignal(
     *              newSignal.map(this::not)
     *          )
     *      );
     * }
     */
    @Override protected Not startup() {
        this.<Boolean, Boolean>onSignalChanged(in)
            .set(out)
            .toResultOfApplying()
            .signalValueTransformation(this::not)
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

        public Not time(Clock clock) {
            Clock _clock = requireNonNull(clock);
            return new Not(in, out, _clock).startup();
        }
    }

}
//@formatter:on
