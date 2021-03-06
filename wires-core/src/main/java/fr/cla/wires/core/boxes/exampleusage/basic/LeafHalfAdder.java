package fr.cla.wires.core.boxes.exampleusage.basic;

import fr.cla.wires.core.*;
import fr.cla.wires.core.boxes.exampleusage.composite.CompositeHalfAdder;
import fr.cla.wires.support.oo.Accumulable;

import static java.util.Objects.requireNonNull;

//@formatter:off
/**
 * An example usage of how to connect wires to boxes.
 * @see fr.cla.wires.core.boxes.exampleusage.basic for a general description of the examples of basic Boxes
 * @see CompositeHalfAdder for the alternative composite implementation
 */
public final class LeafHalfAdder extends Box {

    private final Wire<Boolean> inA, inB, sum, carry;

    private LeafHalfAdder(
        Wire<Boolean> inA, Wire<Boolean> inB,
        Wire<Boolean> sum, Wire<Boolean> carry,
        Clock clock
    ) {
        this(inA, inB, sum, carry, clock, DEFAULT_DELAY);
    }

    private LeafHalfAdder(
        Wire<Boolean> inA, Wire<Boolean> inB,
        Wire<Boolean> sum, Wire<Boolean> carry,
        Clock clock, Delay delay
    ) {
        super(clock, delay);
        this.inA = requireNonNull(inA);
        this.inB = requireNonNull(inB);
        this.sum = requireNonNull(sum);
        this.carry = requireNonNull(carry);
    }

    @Override
    protected LeafHalfAdder startup() {
        this.onSignalChanged2(inA)
            .set(sum)
            .toResultOfApplying()
            .signalValuesCombinator(this::sum, inB, Signal.WhenCombining.ABSENT_WINS)
        ;
        this.onSignalChanged2(inB)
            .set(sum)
            .toResultOfApplying()
            .signalValuesCombinator(inA, this::sum, Signal.WhenCombining.ABSENT_WINS)
        ;
        this.onSignalChanged2(inA)
            .set(carry)
            .toResultOfApplying()
            .signalValuesCombinator(this::carry, inB, Signal.WhenCombining.ABSENT_WINS)
        ;
        this.onSignalChanged2(inB)
            .set(carry)
            .toResultOfApplying()
            .signalValuesCombinator(inA, this::carry, Signal.WhenCombining.ABSENT_WINS)
        ;
        return this;
    }

    private boolean sum(boolean bA, boolean bB) {
        return bA != bB;
    }

    private boolean carry(boolean bA, boolean bB) {
        return bA && bB;
    }

    public static Builder inA(Wire<Boolean> inA) {
        return new Builder(requireNonNull(inA));
    }




    public static class Builder {
        private Wire<Boolean> inA, inB, sum, carry;

        private Builder(Wire<Boolean> in) {
            this.inA = requireNonNull(in);
        }

        public Builder inB(Wire<Boolean> inB) {
            this.inB = requireNonNull(inB);
            return this;
        }

        public Builder sum(Wire<Boolean> sum) {
            this.sum = requireNonNull(sum);
            return this;
        }

        public Builder carry(Wire<Boolean> carry) {
            this.carry = requireNonNull(carry);
            return this;
        }

        public LeafHalfAdder time(Clock clock) {
            Clock _clock = requireNonNull(clock);
            return new LeafHalfAdder(inA, inB, sum, carry, _clock).startup();
        }
    }

}
//@formatter:on
