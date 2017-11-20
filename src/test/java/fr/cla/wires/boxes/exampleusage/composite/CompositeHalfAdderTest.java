package fr.cla.wires.boxes.exampleusage.composite;


import fr.cla.wires.Signal;
import fr.cla.wires.Time;
import fr.cla.wires.Wire;
import fr.cla.wires.boxes.exampleusage.composite.CompositeHalfAdder;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

//@formatter:off
/**
 * An example of the expected behaviour of Boxes, and of how to tick the clock.
 * @see fr.cla.wires.boxes.exampleusage
 */
public class CompositeHalfAdderTest {

    private Wire<Boolean> in1, in2, sum, carry;
    private Time time;

    @Before public void setup() {
        in1 = Wire.make();
        in2 = Wire.make();
        sum = Wire.make();
        carry = Wire.make();
        time = Time.create();
        CompositeHalfAdder.in1(in1).in2(in2).sum(sum).carry(carry).time(time);
    }

    //-------------------Sum-------------------VVVVVVVVVVVVVVVVVVVVVVV
    @Test
    public void sum_should_be_false_when_1_and_2_are_false() {
        given: {
            in1.setSignal(Signal.of(false));
            in2.setSignal(Signal.of(false));
        }
        when: {
            time.tick();
        }
        then: {
            assertThat(sum.getSignal()).isEqualTo(Signal.of(false));
        }
    }

    @Test
    public void sum_should_be_false_when_1_and_2_are_true() {
        given: {
            in1.setSignal(Signal.of(true));
            in2.setSignal(Signal.of(true));
        }
        when: {
            time.tick();
        }
        then: {
            assertThat(sum.getSignal()).isEqualTo(Signal.of(false));
        }
    }

    @Test
    public void sum_should_be_true_when_1_is_false_and_2_is_true() {
        given: {
            in1.setSignal(Signal.of(false));
            in2.setSignal(Signal.of(true));
        }
        when: {
            time.tick();
        }
        then: {
            assertThat(sum.getSignal()).isEqualTo(Signal.of(true));
        }
    }

    @Test
    public void sum_should_be_true_when_1_is_true_and_2_is_false() {
        given: {
            in1.setSignal(Signal.of(true));
            in2.setSignal(Signal.of(false));
        }
        when: {
            time.tick();
        }
        then: {
            assertThat(sum.getSignal()).isEqualTo(Signal.of(true));
        }
    }
    //-------------------Sum-------------------^^^^^^^^^^^^^^^^^^^^^^^

    //-------------------Carry-------------------VVVVVVVVVVVVVVVVVVVVVVV
    @Test
    public void carry_should_be_false_when_1_and_2_are_false() {
        given: {
            in1.setSignal(Signal.of(false));
            in2.setSignal(Signal.of(false));
        }
        when: {
            time.tick();
        }
        then: {
            assertThat(carry.getSignal()).isEqualTo(Signal.of(false));
        }
    }

    @Test
    public void carry_should_be_true_when_1_and_2_are_true() {
        given: {
            in1.setSignal(Signal.of(true));
            in2.setSignal(Signal.of(true));
        }
        when: {
            time.tick();
        }
        then: {
            assertThat(carry.getSignal()).isEqualTo(Signal.of(true));
        }
    }

    @Test
    public void carry_should_be_false_when_1_is_false_and_2_is_true() {
        given: {
            in1.setSignal(Signal.of(false));
            in2.setSignal(Signal.of(true));
        }
        when: {
            time.tick();
        }
        then: {
            assertThat(carry.getSignal()).isEqualTo(Signal.of(false));
        }
    }

    @Test
    public void carry_should_be_false_when_1_is_true_and_2_is_false() {
        given: {
            in1.setSignal(Signal.of(true));
            in2.setSignal(Signal.of(false));
        }
        when: {
            time.tick();
        }
        then: {
            assertThat(carry.getSignal()).isEqualTo(Signal.of(false));
        }
    }
    //-------------------Carry-------------------^^^^^^^^^^^^^^^^^^^^^^^

}
//@formatter:on
