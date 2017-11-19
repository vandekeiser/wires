package fr.cla.wires.boxes.exampleusage;


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
public class CollectMultipleAndTest {

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
        CollectMultipleAnd.ins(ins).out(out).time(time);
    }

    @Test
    public void out_should_initially_be_no_signal() {
        //When
        time.tick();

        //Then
        assertThat(out.getSignal()).isEqualTo(Signal.none());
    }

    @Test
    public void without_ins_out_should_be_no_signal() {
        //Given
        setup(0);

        //When
        time.tick();

        //Then
        assertThat(out.getSignal()).isEqualTo(Signal.none());
    }

    @Test
    public void with_1_in_out_should_be_same_signal() {
        with_1_in_out_should_be_same_signal(Signal.of(true));
        with_1_in_out_should_be_same_signal(Signal.of(false));
        with_1_in_out_should_be_same_signal(Signal.none());
    }

    public void with_1_in_out_should_be_same_signal(Signal<Boolean> signal) {
        //Given
        setup(1);

        //When
        ins.iterator().next().setSignal(signal);
        time.tick();

        //Then
        assertThat(out.getSignal()).isEqualTo(signal);
    }

    @Test
    public void out_should_be_false_when_an_input_is_false() {
        //When
        ins.forEach(i -> i.setSignal(Signal.of(true)));
        ins.iterator().next().setSignal(Signal.of(false));
        time.tick();

        //Then
        assertThat(out.getSignal()).isEqualTo(Signal.of(false));
    }

    @Test
    public void out_should_be_true_when_no_input_is_false() {
        ins.forEach(i -> i.setSignal(Signal.of(true)));
        time.tick();

        //Then
        assertThat(out.getSignal()).isEqualTo(Signal.of(true));
    }

}
//@formatter:on
