package fr.cla.wires.exampleusage;


import fr.cla.wires.Signal;
import fr.cla.wires.Time;
import fr.cla.wires.Wire;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

//@formatter:off
public class NotTest {

    private Wire<Boolean> in, out;
    private Time time;

    @Before
    public void setup() {
        in = Wire.make();
        out = Wire.make();
        time = Time.create();
        Not.in(in).out(out).time(time);
    }

    @Test
    public void out_should_initially_be_none() {
        assertThat(out.getSignal()).isEqualTo(Signal.none());
    }

    @Test
    public void out_should_be_none_when_in_is_none() {
        //When
        in.setSignal(Signal.none());
        time.tick();

        //Then
        assertThat(out.getSignal()).isEqualTo(Signal.none());
    }

    @Test
    public void out_should_be_false_when_in_is_true() {
        //When
        in.setSignal(Signal.of(true));
        time.tick();

        //Then
        assertThat(out.getSignal()).isEqualTo(Signal.of(false));
    }

    @Test
    public void out_should_be_true_when_in_is_false() {
        //When
        in.setSignal(Signal.of(false));
        time.tick();

        //Then
        assertThat(out.getSignal()).isEqualTo(Signal.of(true));
    }

    @Test
    public void out_should_still_be_none_if_in_is_true_but_the_agenda_didnt_tick() {
        //When
        in.setSignal(Signal.of(true));

        //Then
        assertThat(out.getSignal()).isEqualTo(Signal.none());
    }

    @Test
    public void out_should_still_be_none_if_in_is_false_but_the_agenda_didnt_tick() {
        //When
        in.setSignal(Signal.of(false));

        //Then
        assertThat(out.getSignal()).isEqualTo(Signal.none());
    }
}
//@formatter:on
