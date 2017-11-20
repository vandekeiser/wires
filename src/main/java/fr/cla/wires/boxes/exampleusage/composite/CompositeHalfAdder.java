package fr.cla.wires.boxes.exampleusage.composite;

import fr.cla.wires.Box;
import fr.cla.wires.Delay;
import fr.cla.wires.Time;
import fr.cla.wires.Wire;
import fr.cla.wires.boxes.exampleusage.basic.And;
import fr.cla.wires.boxes.exampleusage.basic.Not;
import fr.cla.wires.boxes.exampleusage.basic.Or;

import static java.util.Objects.requireNonNull;

//@formatter:off
/**
 * An example usage of how to connect wires to boxes.
 * @see fr.cla.wires.boxes.exampleusage
 */
public final class CompositeHalfAdder extends Box {

    private final Wire<Boolean> inA, inB, sum, carry; //Externally exposed Wires
    private final Wire<Boolean> d, e; //Internal wiring between the Or/And/Not gates and from/to the inputs/outputs

    private CompositeHalfAdder(
        Wire<Boolean> inA, Wire<Boolean> inB,
        Wire<Boolean> sum, Wire<Boolean> carry,
        Time time
    ) {
        this(inA, inB, sum, carry, time, DEFAULT_DELAY);
    }

    private CompositeHalfAdder(
        Wire<Boolean> inA, Wire<Boolean> inB,
        Wire<Boolean> sum, Wire<Boolean> carry,
        Time time, Delay delay
    ) {
        super(time, delay);
        this.inA = requireNonNull(inA);
        this.inB = requireNonNull(inB);
        this.sum = requireNonNull(sum);
        this.carry = requireNonNull(carry);
        this.d = Wire.make();
        this.e = Wire.make();
    }

    //TODO? abstract startup in Box
    private CompositeHalfAdder startup() {
        //SICP p. 274
        Or.in1(inA).in2(inB).out(d).time(time);
        And.in1(inA).in2(inB).out(carry).time(time);
        Not.in(carry).out(e).time(time);
        And.in1(d).in2(e).out(sum).time(time);
        return this;
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

        public CompositeHalfAdder time(Time time) {
            Time _time = requireNonNull(time);
            return new CompositeHalfAdder(inA, inB, sum, carry, _time).startup();
        }
    }

}
//@formatter:on
