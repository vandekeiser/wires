package fr.cla.wires.boxes.exampleusage.composite;


import fr.cla.wires.Clock;
import fr.cla.wires.Signal;
import fr.cla.wires.Wire;
import org.junit.Before;
import org.junit.Test;

import static fr.cla.wires.boxes.exampleusage.composite.CompositeHalfAdderTest.COMPOSITE_HALF_ADDER_LONGEST_PATH;
import static java.util.stream.IntStream.range;
import static org.assertj.core.api.Assertions.assertThat;

//@formatter:off
/**
 * An example of the expected behaviour of Boxes, and of how to tick the clock.
 * @see fr.cla.wires.boxes.exampleusage
 * See the expected "truth table" at https://en.wikipedia.org/wiki/Adder_(electronics)
 */
public class CompositeFullAdderTest {

    //The max number of DELAY=1 boxes the Signal takes through a CompositeFullAdder
    private static final int COMPOSITE_FULL_ADDER_LONGEST_PATH = COMPOSITE_HALF_ADDER_LONGEST_PATH * 2;

    private Wire<Boolean> inA, inB, inCarry, sum, carry;
    private Clock clock;

    @Before public void setup() {
        inA = Wire.make();
        inB = Wire.make();
        inCarry = Wire.make();
        sum = Wire.make();
        carry = Wire.make();
        clock = Clock.createTime();
        CompositeFullAdder.inA(inA).inB(inB).inCarry(inCarry).sum(sum).carry(carry).time(clock);
    }

    private void tickCompositeFullAdder() {
        range(0, COMPOSITE_FULL_ADDER_LONGEST_PATH).forEach(i -> clock.tick());
    }

    //-------------------Sum-------------------VVVVVVVVVVVVVVVVVVVVVVV
    @Test
    public void sum_should_be_false_when_A_B_and_inCarry_are_false() {
        given: {
            inA.setSignal(Signal.of(false));
            inB.setSignal(Signal.of(false));
            inCarry.setSignal(Signal.of(false));
        }
        when: {
            tickCompositeFullAdder();
        }
        then: {
            assertThat(sum.getSignal()).isEqualTo(Signal.of(false));
        }
    }

    @Test
    public void sum_should_be_true_when_A_B_are_false_but_inCarry_is_true() {
        given: {
            inA.setSignal(Signal.of(false));
            inB.setSignal(Signal.of(false));
            inCarry.setSignal(Signal.of(true));
        }
        when: {
            tickCompositeFullAdder();
        }
        then: {
            assertThat(sum.getSignal()).isEqualTo(Signal.of(true));
        }
    }

    @Test
    public void sum_should_be_true_when_A_and_inCarry_are_false_but_B_is_true() {
        given: {
            inA.setSignal(Signal.of(false));
            inB.setSignal(Signal.of(true));
            inCarry.setSignal(Signal.of(false));
        }
        when: {
            tickCompositeFullAdder();
        }
        then: {
            assertThat(sum.getSignal()).isEqualTo(Signal.of(true));
        }
    }

    @Test
    public void sum_should_be_false_when_B_and_inCarry_are_true_but_A_is_false() {
        given: {
            inA.setSignal(Signal.of(false));
            inB.setSignal(Signal.of(true));
            inCarry.setSignal(Signal.of(true));
        }
        when: {
            tickCompositeFullAdder();
        }
        then: {
            assertThat(sum.getSignal()).isEqualTo(Signal.of(false));
        }
    }

    @Test
    public void sum_should_be_true_when_B_and_inCarry_are_false_but_A_is_true() {
        given: {
            inA.setSignal(Signal.of(true));
            inB.setSignal(Signal.of(false));
            inCarry.setSignal(Signal.of(false));
        }
        when: {
            tickCompositeFullAdder();
        }
        then: {
            assertThat(sum.getSignal()).isEqualTo(Signal.of(true));
        }
    }

    @Test
    public void sum_should_be_false_when_A_and_inCarry_are_true_but_B_is_false() {
        given: {
            inA.setSignal(Signal.of(true));
            inB.setSignal(Signal.of(false));
            inCarry.setSignal(Signal.of(true));
        }
        when: {
            tickCompositeFullAdder();
        }
        then: {
            assertThat(sum.getSignal()).isEqualTo(Signal.of(false));
        }
    }

    @Test
    public void sum_should_be_false_when_A_and_B_are_true_but_inCarry_is_false() {
        given: {
            inA.setSignal(Signal.of(true));
            inB.setSignal(Signal.of(true));
            inCarry.setSignal(Signal.of(false));
        }
        when: {
            tickCompositeFullAdder();
        }
        then: {
            assertThat(sum.getSignal()).isEqualTo(Signal.of(false));
        }
    }

    @Test
    public void sum_should_be_true_when_A_B_and_inCarry_are_true() {
        given: {
            inA.setSignal(Signal.of(true));
            inB.setSignal(Signal.of(true));
            inCarry.setSignal(Signal.of(true));
        }
        when: {
            tickCompositeFullAdder();
        }
        then: {
            assertThat(sum.getSignal()).isEqualTo(Signal.of(true));
        }
    }
    //-------------------Sum-------------------^^^^^^^^^^^^^^^^^^^^^^^

    //-------------------Carry-----------------VVVVVVVVVVVVVVVVVVVVVVV
    @Test
    public void carry_should_be_false_when_A_B_and_inCarry_are_false() {
        given: {
            inA.setSignal(Signal.of(false));
            inB.setSignal(Signal.of(false));
            inCarry.setSignal(Signal.of(false));
        }
        when: {
            tickCompositeFullAdder();
        }
        then: {
            assertThat(carry.getSignal()).isEqualTo(Signal.of(false));
        }
    }

    @Test
    public void carry_should_be_false_when_A_B_are_false_but_inCarry_is_true() {
        given: {
            inA.setSignal(Signal.of(false));
            inB.setSignal(Signal.of(false));
            inCarry.setSignal(Signal.of(true));
        }
        when: {
            tickCompositeFullAdder();
        }
        then: {
            assertThat(carry.getSignal()).isEqualTo(Signal.of(false));
        }
    }

    @Test
    public void carry_should_be_false_when_A_and_inCarry_are_false_but_B_is_true() {
        given: {
            inA.setSignal(Signal.of(false));
            inB.setSignal(Signal.of(true));
            inCarry.setSignal(Signal.of(false));
        }
        when: {
            tickCompositeFullAdder();
        }
        then: {
            assertThat(carry.getSignal()).isEqualTo(Signal.of(false));
        }
    }

    @Test
    public void carry_should_be_true_when_B_and_inCarry_are_true_but_A_is_false() {
        given: {
            inA.setSignal(Signal.of(false));
            inB.setSignal(Signal.of(true));
            inCarry.setSignal(Signal.of(true));
        }
        when: {
            tickCompositeFullAdder();
        }
        then: {
            assertThat(carry.getSignal()).isEqualTo(Signal.of(true));
        }
    }

    @Test
    public void carry_should_be_false_when_B_and_inCarry_are_false_but_A_is_true() {
        given: {
            inA.setSignal(Signal.of(true));
            inB.setSignal(Signal.of(false));
            inCarry.setSignal(Signal.of(false));
        }
        when: {
            tickCompositeFullAdder();
        }
        then: {
            assertThat(carry.getSignal()).isEqualTo(Signal.of(false));
        }
    }

    @Test
    public void carry_should_be_true_when_A_and_inCarry_are_true_but_B_is_false() {
        given: {
            inA.setSignal(Signal.of(true));
            inB.setSignal(Signal.of(false));
            inCarry.setSignal(Signal.of(true));
        }
        when: {
            tickCompositeFullAdder();
        }
        then: {
            assertThat(carry.getSignal()).isEqualTo(Signal.of(true));
        }
    }

    @Test
    public void carry_should_be_true_when_A_and_B_are_true_but_inCarry_is_false() {
        given: {
            inA.setSignal(Signal.of(true));
            inB.setSignal(Signal.of(true));
            inCarry.setSignal(Signal.of(false));
        }
        when: {
            tickCompositeFullAdder();
        }
        then: {
            assertThat(carry.getSignal()).isEqualTo(Signal.of(true));
        }
    }

    @Test
    public void carry_should_be_true_when_A_B_and_inCarry_are_true() {
        given: {
            inA.setSignal(Signal.of(true));
            inB.setSignal(Signal.of(true));
            inCarry.setSignal(Signal.of(true));
        }
        when: {
            tickCompositeFullAdder();
        }
        then: {
            assertThat(carry.getSignal()).isEqualTo(Signal.of(true));
        }
    }
    //-------------------Carry-----------------^^^^^^^^^^^^^^^^^^^^^^^

}
//@formatter:on
