package fr.cla.wires.core.boxes.exampleusage.reentrant;


import fr.cla.wires.core.Clock;
import fr.cla.wires.core.Signal;
import fr.cla.wires.core.Wire;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

//@formatter:off
/**
 * An example of the expected behaviour of Boxes, and of how to tick the clock.
 * @see fr.cla.wires.core.boxes.exampleusage
 */
public class CounterTest {

    private Wire<Long> out;
    private Clock clock;

    @Before public void setup() {
        out = Wire.make();
        clock = Clock.createTime();
        Counter.out(out).time(clock);
    }

    @Test
    public void out_should_initially_be_0() {
        given: {
            //Nothing
        }
        when: {
            //No clock.tick()
        }
        then: {
            assertThat(out.getSignal()).isEqualTo(Signal.of(0L));
        }
    }
    
    @Test
    public void out_should_be_1_after_1_tick() {
        given: {
            //Nothing
        }
        when: {
            clock.tick();
        }
        then: {
            assertThat(out.getSignal()).isEqualTo(Signal.of(1L));
        }
    }
    
    @Test
    public void out_should_be_2_after_2_ticks() {
        given: {
            //Nothing
        }
        when: {
            clock.tick(); clock.tick();
        }
        then: {
            assertThat(out.getSignal()).isEqualTo(Signal.of(2L));
        }
    }
    
    @Test
    public void should_not_overflow_silently() {
        given: {
            Counter.out(out).initial(Long.MAX_VALUE).step(1L).time(clock);
        }
        try {
            when: {
                clock.tick();
            }
        } catch (Counter.OverflowException expected) {
            then: {
                assertThat(expected.getCurrentCounter()).isEqualTo(Long.MAX_VALUE);
                assertThat(expected.getAttemptedStep()).isEqualTo(1L);
            }       
        }
    }

}
//@formatter:on
