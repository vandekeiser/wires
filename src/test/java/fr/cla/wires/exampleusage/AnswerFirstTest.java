package fr.cla.wires.exampleusage;


import fr.cla.wires.Agenda;
import fr.cla.wires.Signal;
import fr.cla.wires.Wire;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Java6Assertions.assertThat;

//@formatter:off
public class AnswerFirstTest {

    private Wire<Boolean> in1, in2, out;
    private Agenda agenda;
    private AnswerFirst answerFirst;

    @Before
    public void setup() {
        in1 = Wire.make();
        in2 = Wire.make();
        out = Wire.make();
        agenda = new Agenda();
        answerFirst = AnswerFirst.in1(in1).in2(in2).out(out).agenda(agenda);
    }

    @Test
    public void out_should_be_false_when_1_and_2_are_false() {
        //When
        in1.setSignal(Signal.of(false));
        in2.setSignal(Signal.of(false));
        agenda.tick();

        //Then
        assertThat(out.getSignal()).isEqualTo(Signal.of(false));
    }

    @Test
    public void out_should_be_false_when_1_is_false_and_2_is_true() {
        //When
        in1.setSignal(Signal.of(false));
        in2.setSignal(Signal.of(true));
        agenda.tick();

        //Then
        assertThat(out.getSignal()).isEqualTo(Signal.of(false));
    }

    @Test
    public void out_should_be_true_when_1_is_true_and_2_is_false() {
        //When
        in1.setSignal(Signal.of(true));
        in2.setSignal(Signal.of(false));
        agenda.tick();

        //Then
        assertThat(out.getSignal()).isEqualTo(Signal.of(true));
    }

    @Test
    public void out_should_be_true_when_1_and_2_are_true() {
        //When
        in1.setSignal(Signal.of(true));
        in2.setSignal(Signal.of(true));
        agenda.tick();

        //Then
        assertThat(out.getSignal()).isEqualTo(Signal.of(true));
    }

}
//@formatter:on
