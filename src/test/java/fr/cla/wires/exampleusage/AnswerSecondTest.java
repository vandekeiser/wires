package fr.cla.wires.exampleusage;


import fr.cla.wires.Agenda;
import fr.cla.wires.Clock;
import fr.cla.wires.Signal;
import fr.cla.wires.Wire;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Java6Assertions.assertThat;

//@formatter:off
public class AnswerSecondTest {

    private Wire<Boolean> in1, in2, out;
    private Clock clock;
    private AnswerSecond answerSecond;

    @Before
    public void setup() {
        in1 = Wire.make();
        in2 = Wire.make();
        out = Wire.make();
        Agenda agenda = new Agenda();
        clock = agenda;
        answerSecond = AnswerSecond.in1(in1).in2(in2).out(out).agenda(agenda);
    }

    @Test
    public void out_should_be_false_when_1_and_2_are_false() {
        //When
        in1.setSignal(Signal.of(false));
        in2.setSignal(Signal.of(false));
        clock.tick();

        //Then
        assertThat(out.getSignal()).isEqualTo(Signal.of(false));
    }

    @Test
    public void out_should_be_true_when_1_is_false_and_2_is_true() {
        //When
        in1.setSignal(Signal.of(false));
        in2.setSignal(Signal.of(true));
        clock.tick();

        //Then
        assertThat(out.getSignal()).isEqualTo(Signal.of(true));
    }

    @Test
    public void out_should_be_false_when_1_is_true_and_2_is_false() {
        //When
        in1.setSignal(Signal.of(true));
        in2.setSignal(Signal.of(false));
        clock.tick();

        //Then
        assertThat(out.getSignal()).isEqualTo(Signal.of(false));
    }

    @Test
    public void out_should_be_true_when_1_and_2_are_true() {
        //When
        in1.setSignal(Signal.of(true));
        in2.setSignal(Signal.of(true));
        clock.tick();

        //Then
        assertThat(out.getSignal()).isEqualTo(Signal.of(true));
    }

}
//@formatter:on
