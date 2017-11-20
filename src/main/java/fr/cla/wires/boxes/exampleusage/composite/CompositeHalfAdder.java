package fr.cla.wires.boxes.exampleusage.composite;

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
public final class CompositeHalfAdder extends Box {

    private final Wire<Boolean> in1, in2, sum, carry;

    private CompositeHalfAdder(
        Wire<Boolean> in1, Wire<Boolean> in2,
        Wire<Boolean> sum, Wire<Boolean> carry,
        Time time
    ) {
        this(in1, in2, sum, carry, time, DEFAULT_DELAY);
    }

    private CompositeHalfAdder(
        Wire<Boolean> in1, Wire<Boolean> in2,
        Wire<Boolean> sum, Wire<Boolean> carry,
        Time time, Delay delay
    ) {
        super(time, delay);
        this.in1 = requireNonNull(in1);
        this.in2 = requireNonNull(in2);
        this.sum = requireNonNull(sum);
        this.carry = requireNonNull(carry);
    }

    private CompositeHalfAdder startup() {
        this.<Boolean, Boolean>onSignalChanged(in1)
            .set(sum)
            .toResultOfApplying()
            .transformation(this::sum, in2)
        ;
        this.<Boolean, Boolean>onSignalChanged(in2)
            .set(sum)
            .toResultOfApplying()
            .transformation(in1, this::sum)
        ;
        this.<Boolean, Boolean>onSignalChanged(in1)
            .set(carry)
            .toResultOfApplying()
            .transformation(this::carry, in2)
        ;
        this.<Boolean, Boolean>onSignalChanged(in2)
            .set(carry)
            .toResultOfApplying()
            .transformation(in1, this::carry)
        ;
        return this;
    }

    private boolean sum(boolean b1, boolean b2) {
        return b1 != b2;
    }

    private boolean carry(boolean b1, boolean b2) {
        return b1 && b2;
    }

    public static Builder in1(Wire<Boolean> in1) {
        return new Builder(requireNonNull(in1));
    }




    public static class Builder {
        private Wire<Boolean> in1, in2, sum, carry;

        private Builder(Wire<Boolean> in) {
            this.in1 = requireNonNull(in);
        }

        public Builder in2(Wire<Boolean> in2) {
            this.in2 = requireNonNull(in2);
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

        public CompositeHalfAdder time(Time time) {
            Time _time = requireNonNull(time);
            return new CompositeHalfAdder(in1, in2, sum, carry, _time).startup();
        }
    }

}
//@formatter:on
