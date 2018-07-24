package fr.cla.wires.core.boxes.exampleusage.composite;

import fr.cla.wires.core.Box;
import fr.cla.wires.core.Clock;
import fr.cla.wires.core.Delay;
import fr.cla.wires.core.Wire;
import fr.cla.wires.core.boxes.exampleusage.basic.Or;

import static java.util.Objects.requireNonNull;

//@formatter:off
/**
 * An example usage of how to connect wires to boxes.
 * @see fr.cla.wires.core.boxes.exampleusage
 */
public final class CompositeFullAdder extends Box {

    private final Wire<Boolean> inA, inB, inCarry, sum, carry; //Externally exposed Wires
    private final Wire<Boolean> s, c1, c2; //Internal wiring between the Or/And/Not gates and from/to the inputs/outputs

    private CompositeFullAdder(
        Wire<Boolean> inA, Wire<Boolean> inB, Wire<Boolean> inCarry,
        Wire<Boolean> sum, Wire<Boolean> carry,
        Clock clock
    ) {
        this(inA, inB, inCarry, sum, carry, clock, DEFAULT_DELAY);
    }

    private CompositeFullAdder(
        Wire<Boolean> inA, Wire<Boolean> inB, Wire<Boolean> inCarry,
        Wire<Boolean> sum, Wire<Boolean> carry,
        Clock clock, Delay delay
    ) {
        super(clock, delay);
        this.inA = requireNonNull(inA);
        this.inB = requireNonNull(inB);
        this.inCarry = requireNonNull(inCarry);
        this.sum = requireNonNull(sum);
        this.carry = requireNonNull(carry);
        this.s = Wire.make();
        this.c1 = Wire.make();
        this.c2 = Wire.make();
    }

    @Override
    protected CompositeFullAdder startup() {
        //SICP p. 276
        CompositeHalfAdder.inA(inB).inB(inCarry).sum(s).carry(c1).time(clock);
        CompositeHalfAdder.inA(inA).inB(s).sum(sum).carry(c2).time(clock);
        Or.in1(c1).in2(c2).out(carry).time(clock);
        return this;
    }

    public static Builder inA(Wire<Boolean> inA) {
        return new Builder(requireNonNull(inA));
    }




    public static class Builder {
        private Wire<Boolean> inA, inB, inCarry, sum, carry;

        private Builder(Wire<Boolean> in) {
            this.inA = requireNonNull(in);
        }

        public Builder inB(Wire<Boolean> inB) {
            this.inB = requireNonNull(inB);
            return this;
        }

        public Builder inCarry(Wire<Boolean> inCarry) {
            this.inCarry = requireNonNull(inCarry);
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

        public CompositeFullAdder time(Clock clock) {
            Clock _clock = requireNonNull(clock);
            return new CompositeFullAdder(inA, inB, inCarry, sum, carry, _clock).startup();
        }
    }

}
//@formatter:on
