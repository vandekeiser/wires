package fr.cla.wires.core.boxes.exampleusage.basic;

import fr.cla.wires.core.*;
import fr.cla.wires.support.oo.Accumulable;

import java.util.function.BiFunction;

import static java.util.Objects.requireNonNull;

//@formatter:off
/**
 * An example usage of how to connect wires to boxes.
 * @see fr.cla.wires.core.boxes.exampleusage
 */
public final class And extends Box {

    private final Wire<Boolean> in1, in2, out;

    private And(Wire<Boolean> in1, Wire<Boolean> in2, Wire<Boolean> out, Clock clock) {
        this(in1, in2, out, clock, DEFAULT_DELAY);
    }

    private And(Wire<Boolean> in1, Wire<Boolean> in2, Wire<Boolean> out, Clock clock, Delay delay) {
        super(clock, delay);
        this.in1 = requireNonNull(in1);
        this.in2 = requireNonNull(in2);
        this.out = requireNonNull(out);
    }

    /**
     * This method is used to not do the startup in the constructor,
     * to avoid letting "this" escape through the method ref,
     * so that the Box is "properly constructed".
     *
     * The DSL implemented by the "Staged Builder" pattern translates:
     * {@code
     *      onSignalChanged(in1)
     *          .set(out)
     *          .toResultOfApplying()
     *          .signalValuesCombinator(this::and, in2)
     *      ;
     * }
     * to the less linear:
     * {@code
     *      onSignalChanged(in1,
     *          newIn1Signal -> out.setSignal(
     *              Signal.map(newIn1Signal, in2.getSignal(), this::and)
     *          )
     *      );
     * }
     */
    @Override
    protected And startup() {
        this.onSignalChanged2(in1)
            .set(out)
            .toResultOfApplying()
            .signalValuesCombinator(Boolean::logicalAnd, in2, Signal.WhenCombining.ABSENT_WINS)
        ;

        this.onSignalChanged2(in2)
            .set(out)
            .toResultOfApplying()
            .signalValuesCombinator(in1, Boolean::logicalAnd, Signal.WhenCombining.ABSENT_WINS)
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

        public And time(Clock clock) {
            Clock _clock = requireNonNull(clock);
            return new And(in1, in2, out, _clock).startup();
        }
    }

}
//@formatter:on
