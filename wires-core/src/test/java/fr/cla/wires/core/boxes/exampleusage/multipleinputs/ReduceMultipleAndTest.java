package fr.cla.wires.core.boxes.exampleusage.multipleinputs;


import fr.cla.wires.core.Clock;
import fr.cla.wires.core.Signal;
import fr.cla.wires.core.Wire;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

//@formatter:off
/**
 * An example of the expected behaviour of Boxes, and of how to tick the clock.
 * @see fr.cla.wires.core.boxes.exampleusage
 */
public class ReduceMultipleAndTest {

    private static final int MULTIPLICITY = 100;

    private List<Wire<Boolean>> ins;
    private Wire<Boolean> out;
    private Clock clock;

    @Before
    public void setup() {
        setup(MULTIPLICITY);
    }

    private void setup(long multiplicity) {
        ins = Stream.generate(() -> Wire.<Boolean>make()).limit(multiplicity).collect(toList());
        out = Wire.make();
        clock = Clock.createTime();
        ReduceMultipleAnd.ins(ins).out(out).time(clock);
    }

    @Test
    public void out_should_initially_be_no_signal() {
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
    public void out_should_be_false_when_an_input_is_false() {
        given: {
            ins.forEach(i -> i.setSignal(Signal.of(true)));
            ins.iterator().next().setSignal(Signal.of(false));
        }
        when: {
            clock.tick();
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
            clock.tick();
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
            clock.tick();
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
            clock.tick();
        }
        then: {
            assertThat(out.getSignal()).isEqualTo(Signal.none());
        }
    }

}
//@formatter:on
