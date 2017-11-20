package fr.cla.wires.boxes.exampleusage.multipleinputs;


import fr.cla.wires.Signal;
import fr.cla.wires.Time;
import fr.cla.wires.Wire;
import org.junit.Before;
import org.junit.Test;

import java.util.Set;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toSet;
import static org.assertj.core.api.Assertions.assertThat;

//@formatter:off
/**
 * An example of the expected behaviour of Boxes, and of how to tick the clock.
 * @see fr.cla.wires.boxes.exampleusage
 */
public class ReduceMultipleAndTest {

    private static final int MULTIPLICITY = 100;
    private Set<Wire<Boolean>> ins;
    private Wire<Boolean> out;
    private Time time;

    @Before
    public void setup() {
        setup(MULTIPLICITY);
    }

    private void setup(long multiplicity) {
        ins = Stream.generate(() -> Wire.<Boolean>make()).limit(multiplicity).collect(toSet());
        out = Wire.make();
        time = Time.create();
        ReduceMultipleAnd.ins(ins).out(out).time(time);
    }

    @Test
    public void out_should_initially_be_no_signal() {
        given: {
            //Nothing
        }
        when: {
            //No time.tick()
        }
        then: {
            assertThat(out.getSignal()).isEqualTo(Signal.none());
        }
    }

    @Test
    public void out_should_be_false_when_an_input_is_false() {
        given: {
            ins.forEach(i -> i.setSignal(Signal.of(true)));
            ins.iterator().next().setSignal(Signal.of(false));
        }
        when: {
            time.tick();
        }
        then: {
            assertThat(out.getSignal()).isEqualTo(Signal.of(false));
        }
    }

    @Test
    public void out_should_be_true_when_no_input_is_false() {
        given: {
            ins.forEach(i -> i.setSignal(Signal.of(true)));
        }
        when: {
            time.tick();
        }
        then: {
            assertThat(out.getSignal()).isEqualTo(Signal.of(true));
        }
    }

    @Test
    public void out_should_be_none_when_any_input_is_none__even_if_all_others_are_false() {
        given: {
            ins.forEach(i -> i.setSignal(Signal.of(false)));
            ins.iterator().next().setSignal(Signal.none());
        }
        when: {
            time.tick();
        }
        then: {
            assertThat(out.getSignal()).isEqualTo(Signal.none());
        }
    }

    @Test
    public void out_should_be_none_when_any_input_is_none__even_if_all_others_are_true() {
        given: {
            ins.forEach(i -> i.setSignal(Signal.of(true)));
            ins.iterator().next().setSignal(Signal.none());
        }
        when: {
            time.tick();
        }
        then: {
            assertThat(out.getSignal()).isEqualTo(Signal.none());
        }
    }

}
//@formatter:on
