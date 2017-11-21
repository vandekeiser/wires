package fr.cla.wires.boxes.exampleusage.composite;


import fr.cla.wires.Signal;
import fr.cla.wires.Time;
import fr.cla.wires.Wire;
import org.junit.Before;
import org.junit.Test;

import static java.util.stream.IntStream.range;
import static org.assertj.core.api.Assertions.assertThat;

//@formatter:off
/**
 * An example of the expected behaviour of Boxes, and of how to tick the clock.
 * @see fr.cla.wires.boxes.exampleusage
 */
public class CompositeHalfAdderTest {

    //The max number of DELAY=1 boxes the Signal takes through a CompositeHalfAdder
    //TODO 1: extract abstract Box::longestPath
    //TODO 2: PBT test that CompositeHalfAdder gives the same result as LeafHalfAdder,
    // after enough ticks, but not before.
    //TODO? 3: add box Box::tickToQuiescence, but account for infinite loops in recurring boxes
    private static final int COMPOSITE_HALF_ADDER_LONGEST_PATH = 3;

    private Wire<Boolean> inA, inB, sum, carry;
    private Time time;

    @Before public void setup() {
        inA = Wire.make();
        inB = Wire.make();
        sum = Wire.make();
        carry = Wire.make();
        time = Time.create();
        CompositeHalfAdder.inA(inA).inB(inB).sum(sum).carry(carry).time(time);
    }

    private void tickCompositeHalfAdder() {
        range(0, COMPOSITE_HALF_ADDER_LONGEST_PATH).forEach(i -> time.tick());
    }

    //-------------------Sum-------------------VVVVVVVVVVVVVVVVVVVVVVV
    @Test
    public void sum_should_be_false_when_A_and_B_are_false() {
        given: {
            inA.setSignal(Signal.of(false));
            inB.setSignal(Signal.of(false));
        }
        when: {
            tickCompositeHalfAdder();
        }
        then: {
            assertThat(sum.getSignal()).isEqualTo(Signal.of(false));
        }
    }

    @Test
    public void sum_should_be_false_when_A_and_B_are_true() {
        given: {
            inA.setSignal(Signal.of(true));
            inB.setSignal(Signal.of(true));
        }
        when: {
            tickCompositeHalfAdder();
        }
        then: {
            assertThat(sum.getSignal()).isEqualTo(Signal.of(false));
        }
    }

    @Test
    public void sum_should_be_true_when_A_is_false_and_B_is_true() {
        given: {
            inA.setSignal(Signal.of(false));
            inB.setSignal(Signal.of(true));
        }
        when: {
            tickCompositeHalfAdder();
        }
        then: {
            assertThat(sum.getSignal()).isEqualTo(Signal.of(true));
        }
    }

    @Test
    public void sum_should_be_true_when_A_is_true_and_B_is_false() {
        given: {
            inA.setSignal(Signal.of(true));
            inB.setSignal(Signal.of(false));
        }
        when: {
            tickCompositeHalfAdder();
        }
        then: {
            assertThat(sum.getSignal()).isEqualTo(Signal.of(true));
        }
    }
    //-------------------Sum-------------------^^^^^^^^^^^^^^^^^^^^^^^

    //-------------------Carry-------------------VVVVVVVVVVVVVVVVVVVVVVV
    @Test
    public void carry_should_be_false_when_A_and_B_are_false() {
        given: {
            inA.setSignal(Signal.of(false));
            inB.setSignal(Signal.of(false));
        }
        when: {
            tickCompositeHalfAdder();
        }
        then: {
            assertThat(carry.getSignal()).isEqualTo(Signal.of(false));
        }
    }

    @Test
    public void carry_should_be_true_when_A_and_B_are_true() {
        given: {
            inA.setSignal(Signal.of(true));
            inB.setSignal(Signal.of(true));
        }
        when: {
            tickCompositeHalfAdder();
        }
        then: {
            assertThat(carry.getSignal()).isEqualTo(Signal.of(true));
        }
    }

    @Test
    public void carry_should_be_false_when_A_is_false_and_B_is_true() {
        given: {
            inA.setSignal(Signal.of(false));
            inB.setSignal(Signal.of(true));
        }
        when: {
            tickCompositeHalfAdder();
        }
        then: {
            assertThat(carry.getSignal()).isEqualTo(Signal.of(false));
        }
    }

    @Test
    public void carry_should_be_false_when_A_is_true_and_B_is_false() {
        given: {
            inA.setSignal(Signal.of(true));
            inB.setSignal(Signal.of(false));
        }
        when: {
            tickCompositeHalfAdder();
        }
        then: {
            assertThat(carry.getSignal()).isEqualTo(Signal.of(false));
        }
    }
    //-------------------Carry-------------------^^^^^^^^^^^^^^^^^^^^^^^

}
//@formatter:on
