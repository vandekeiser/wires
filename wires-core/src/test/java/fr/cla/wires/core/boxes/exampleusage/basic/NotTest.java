package fr.cla.wires.core.boxes.exampleusage.basic;


import fr.cla.wires.core.Clock;
import fr.cla.wires.core.Signal;
import fr.cla.wires.core.Wire;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

//@formatter:off
/**
 * An example of the expected behaviour of Boxes, and of how to tick the clock.
 * @see fr.cla.wires.core.exampleusage
 */
public class NotTest {

    private Wire<Boolean> in, out;
    private Clock clock;

    @Before
    public void setup() {
        in = Wire.make();
        out = Wire.make();
        clock = Clock.createTime();
        Not.in(in).out(out).time(clock);
    }

    @Test
    public void out_should_initially_be_none() {
        given: {
            //Nothing
        }
        when: {
            //No clock.tick()
        }
        then: {
            assertThat(out.getSignal()).isEqualTo(Signal.none());
        }
    }

    @Test
    public void out_should_be_none_when_in_is_none() {
        given: {
            in.setSignal(Signal.none());
        }
        when: {
            clock.tick();
        }
        then: {
            assertThat(out.getSignal()).isEqualTo(Signal.none());
        }
    }

    @Test
    public void out_should_be_false_when_in_is_true() {
        given: {
            in.setSignal(Signal.of(true));
        }
        when: {
            clock.tick();
        }
        then: {
            assertThat(out.getSignal()).isEqualTo(Signal.of(false));
        }
    }

    @Test
    public void out_should_be_true_when_in_is_false() {
        given: {
            in.setSignal(Signal.of(false));
        }
        when: {
            clock.tick();
        }
        then: {
            assertThat(out.getSignal()).isEqualTo(Signal.of(true));
        }
    }

    @Test
    public void out_should_still_be_none_if_in_is_true_but_the_agenda_didnt_tick() {
        given: {
            in.setSignal(Signal.of(true));
        }
        when: {
            //No clock.tick()
        }
        then: {
            assertThat(out.getSignal()).isEqualTo(Signal.none());
        }
    }

    @Test
    public void out_should_still_be_none_if_in_is_false_but_the_agenda_didnt_tick() {
        given: {
            in.setSignal(Signal.of(false));
        }
        when: {
            //No clock.tick()
        }
        then: {
            assertThat(out.getSignal()).isEqualTo(Signal.none());
        }
    }

}
//@formatter:on
