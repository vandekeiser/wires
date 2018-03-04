package fr.cla.wires.boxes.exampleusage.basic;

import fr.cla.wires.Box;
import fr.cla.wires.Clock;
import fr.cla.wires.Delay;
import fr.cla.wires.Wire;

import static java.util.Objects.requireNonNull;

//@formatter:off
/**
 * An example usage of how to connect wires to boxes.
 * @see fr.cla.wires.boxes.exampleusage.basic for a general description of the examples of basic Boxes
 * @see fr.cla.wires.boxes.exampleusage.composite.CompositeHalfAdder for the alternative composite implementation
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

    @Override protected LeafHalfAdder startup() {
        this.<Boolean, Boolean>onSignalChanged(inA)
            .set(sum)
            .toResultOfApplying()
            .transformation(this::sum, inB)
        ;
        this.<Boolean, Boolean>onSignalChanged(inB)
            .set(sum)
            .toResultOfApplying()
            .transformation(inA, this::sum)
        ;
        this.<Boolean, Boolean>onSignalChanged(inA)
            .set(carry)
            .toResultOfApplying()
            .transformation(this::carry, inB)
        ;
        this.<Boolean, Boolean>onSignalChanged(inB)
            .set(carry)
            .toResultOfApplying()
            .transformation(inA, this::carry)
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
