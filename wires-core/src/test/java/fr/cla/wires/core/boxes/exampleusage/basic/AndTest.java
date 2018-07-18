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
public class AndTest {

    private Wire<Boolean> in1, in2, out;
    private Clock clock;

    @Before public void setup() {
        in1 = Wire.make();
        in2 = Wire.make();
        out = Wire.make();
        clock = Clock.createTime();
        And.in1(in1).in2(in2).out(out).time(clock);
    }

    @Test
    public void out_should_be_false_when_1_and_2_are_false() {
        given: {
            in1.setSignal(Signal.of(false));
            in2.setSignal(Signal.of(false));
        }
        when: {
            clock.tick();
        }
        then: {
            assertThat(out.getSignal()).isEqualTo(Signal.of(false));
        }
    }

    @Test
    public void out_should_be_false_when_1_is_false_and_2_is_true() {
        given: {
            in1.setSignal(Signal.of(false));
            in2.setSignal(Signal.of(true));
        }
        when: {
            clock.tick();
        }
        then: {
            assertThat(out.getSignal()).isEqualTo(Signal.of(false));
        }
    }

    @Test
    public void out_should_be_false_when_1_is_true_and_2_is_false() {
        given: {
            in1.setSignal(Signal.of(false));
            in2.setSignal(Signal.of(true));
        }
        when: {
            clock.tick();
        }
        then: {
            assertThat(out.getSignal()).isEqualTo(Signal.of(false));
        }
    }

    @Test
    public void out_should_be_true_when_1_and_2_are_true() {
        given: {
            in1.setSignal(Signal.of(true));
            in2.setSignal(Signal.of(true));
        }
        when: {
            clock.tick();
        }
        then: {
            assertThat(out.getSignal()).isEqualTo(Signal.of(true));
        }
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
        //Tick 0
        assertThat(out.getSignal()).isEqualTo(Signal.none());    //o1
        in1.setSignal(Signal.of(false));                         //i1
        assertThat(out.getSignal()).isEqualTo(Signal.none());    //o2

        clock.tick();                                             //t1
        //Tick 1
        assertThat(out.getSignal()).isEqualTo(Signal.none());    //o3
        in2.setSignal(Signal.of(false));                         //i2
        assertThat(out.getSignal()).isEqualTo(Signal.none());    //o4

        clock.tick();                                             //t2
        //Tick 2
        assertThat(out.getSignal()).isEqualTo(Signal.of(false)); //o5
        in1.setSignal(Signal.of(true));                          //i3
        assertThat(out.getSignal()).isEqualTo(Signal.of(false)); //o6

        clock.tick();                                             //t3
        //Tick 3
        assertThat(out.getSignal()).isEqualTo(Signal.of(false)); //o7
        in2.setSignal(Signal.of(true));                          //i4
        assertThat(out.getSignal()).isEqualTo(Signal.of(false)); //o8

        clock.tick();                                             //t4
        //Tick 4
        assertThat(out.getSignal()).isEqualTo(Signal.of(true));  //o9

        clock.tick();                                             //t5
        //Tick 5
        assertThat(out.getSignal()).isEqualTo(Signal.of(true));  //o10
        in1.setSignal(Signal.of(false));                         //i6
        in2.setSignal(Signal.of(false));                         //i7

        clock.tick();                                             //t6
        //Tick 6
        assertThat(out.getSignal()).isEqualTo(Signal.of(false)); //o11
    }

}
//@formatter:on
