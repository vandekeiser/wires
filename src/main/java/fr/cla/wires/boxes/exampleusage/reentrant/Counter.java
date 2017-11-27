package fr.cla.wires.boxes.exampleusage.reentrant;

import fr.cla.wires.*;
import fr.cla.wires.boxes.exampleusage.basic.And;

import static java.util.Objects.requireNonNull;

//@formatter:off

/**
 * An example usage of how to connect wires to boxes.
 * @see fr.cla.wires.boxes.exampleusage
 */
public final class Counter extends Box {

    private final Wire<Long> step, out;

    private Counter(long step, Wire<Long> out, Clock clock) {
        this(step, out, clock, DEFAULT_DELAY);
    }

    private Counter(long step, Wire<Long> out, Clock clock, Delay delay) {
        super(clock, delay);
        this.out = requireNonNull(out);
        this.step = Wire.make();
        this.step.setSignal(Signal.of(step));
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
    @Override protected Counter startup() {
        this.<Long, Long>onSignalChanged(out)
            .set(out)
            .toResultOfApplying()
            .transformation(step, this::add)
        ;
        this.out.setSignal(Signal.of(0L));
        return this;
    }
    
    private long add(long out, long step) {
        return out + step;
    }

    public static Builder step(long step) {
        return new Builder(step);
    }




    public static class Builder {
        private long step;
        private Wire<Long> out;

        private Builder(long step) {
            this.step = step;
        }

        public Counter time(Clock clock) {
            Clock _clock = requireNonNull(clock);
            return new Counter(step, out, _clock).startup();
        }

        public Builder out(Wire<Long> out) {
            this.out = out;
            return this;
        }
    }

}
//@formatter:on
