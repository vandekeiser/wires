package fr.cla.wires.exampleusage;


import fr.cla.wires.Signal;
import fr.cla.wires.Time;
import fr.cla.wires.Wire;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

//@formatter:off
public class AndTest {

    private Wire<Boolean> in1, in2, out;
    private Time time;

    @Before
    public void setup() {
        in1 = Wire.make();
        in2 = Wire.make();
        out = Wire.make();
        time = Time.create();
        And.in1(in1).in2(in2).out(out).time(time);
    }

    @Test
    public void out_should_be_false_when_1_and_2_are_false() {
        //When
        in1.setSignal(Signal.of(false));
        in2.setSignal(Signal.of(false));
        time.tick();

        //Then
        assertThat(out.getSignal()).isEqualTo(Signal.of(false));
    }

    @Test
    public void out_should_be_false_when_1_is_false_and_2_is_true() {
        //When
        in1.setSignal(Signal.of(false));
        in2.setSignal(Signal.of(true));
        time.tick();

        //Then
        assertThat(out.getSignal()).isEqualTo(Signal.of(false));
    }

    @Test
    public void out_should_be_false_when_1_is_true_and_2_is_false() {
        //When
        in1.setSignal(Signal.of(true));
        in2.setSignal(Signal.of(false));
        time.tick();

        //Then
        assertThat(out.getSignal()).isEqualTo(Signal.of(false));
    }

    @Test
    public void out_should_be_true_when_1_and_2_are_true() {
        //When
        in1.setSignal(Signal.of(true));
        in2.setSignal(Signal.of(true));
        time.tick();

        //Then
        assertThat(out.getSignal()).isEqualTo(Signal.of(true));
    }

    /*
------i1-----------i2-----------i3-----------i4------------------i6----i7------------>input timeline
-----1=F----------2=F----------1=T----------2=T------------------1=F--2=F------------>inputs (1=in1, 2=in2, F=false, T=true)
-------------t1-----------t2-----------t3-----------t4-----t5--------------t6-------->ticks
--o1-----o2----o3-----o4----o5-----o6----o7-----o8-----o9-----o10--------------o11--->expected output timeline
--N------N-----N------N-----F------F-----F------F------T------T----------------F----->expected output (F=false, T=true, N=none)
     */
    @Test
    public void more_complex_scenario() {
        //o1
        assertThat(out.getSignal()).isEqualTo(Signal.none());

        //i1
        in1.setSignal(Signal.of(false));

        //o2
        assertThat(out.getSignal()).isEqualTo(Signal.none());

        //t1
        time.tick();

        //o3
        assertThat(out.getSignal()).isEqualTo(Signal.none());

        //i2
        in2.setSignal(Signal.of(false));

        //o4
        assertThat(out.getSignal()).isEqualTo(Signal.none());

        //t2
        time.tick();

        //o5
        assertThat(out.getSignal()).isEqualTo(Signal.of(false));

        //i3
        in1.setSignal(Signal.of(true));

        //o6
        assertThat(out.getSignal()).isEqualTo(Signal.of(false));

        //t3
        time.tick();

        //o7
        assertThat(out.getSignal()).isEqualTo(Signal.of(false));

        //i4
        in2.setSignal(Signal.of(true));

        //o8
        assertThat(out.getSignal()).isEqualTo(Signal.of(false));

        //t4
        time.tick();

        //o9
        assertThat(out.getSignal()).isEqualTo(Signal.of(true));

        //t5
        time.tick();

        //o10
        assertThat(out.getSignal()).isEqualTo(Signal.of(true));

        //i6
        in1.setSignal(Signal.of(false));

        //i7
        in2.setSignal(Signal.of(false));

        //t6
        time.tick();

        //o11
        assertThat(out.getSignal()).isEqualTo(Signal.of(false));
    }

}
//@formatter:on
