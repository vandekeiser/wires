package fr.cla.wires.boxes.exampleusage;


import fr.cla.wires.Signal;
import fr.cla.wires.Time;
import fr.cla.wires.Wire;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

//@formatter:off
/**
 * An example of the expected behaviour of Boxes, and of how to tick the clock.
 * @see fr.cla.wires.boxes.exampleusage
 */
public class OrTest {

    private Wire<Boolean> in1, in2, out;
    private Time time;

    @Before public void setup() {
        in1 = Wire.make();
        in2 = Wire.make();
        out = Wire.make();
        time = Time.create();
        Or.in1(in1).in2(in2).out(out).time(time);
    }

    @Test
    public void out_should_be_false_when_1_and_2_are_false() {
        given: {
            in1.setSignal(Signal.of(false));
            in2.setSignal(Signal.of(false));
        }
        when: {
            time.tick();
        }
        then: {
            assertThat(out.getSignal()).isEqualTo(Signal.of(false));
        }
    }

    @Test
    public void out_should_be_true_when_1_is_false_and_2_is_true() {
        given: {
            in1.setSignal(Signal.of(false));
            in2.setSignal(Signal.of(true));
        }
        when: {
            time.tick();
        }
        then: {
            assertThat(out.getSignal()).isEqualTo(Signal.of(true));
        }
    }

    @Test
    public void out_should_be_true_when_1_is_true_and_2_is_false() {
        given: {
            in1.setSignal(Signal.of(false));
            in2.setSignal(Signal.of(true));
        }
        when: {
            time.tick();
        }
        then: {
            assertThat(out.getSignal()).isEqualTo(Signal.of(true));
        }
    }

    @Test
    public void out_should_be_true_when_1_and_2_are_true() {
        given: {
            in1.setSignal(Signal.of(true));
            in2.setSignal(Signal.of(true));
        }
        when: {
            time.tick();
        }
        then: {
            assertThat(out.getSignal()).isEqualTo(Signal.of(true));
        }
    }

}
//@formatter:on
