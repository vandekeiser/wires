package fr.cla.wires.core;


import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

//@formatter:off
public class SignalTest {

    //This test is required to justify the @SuppressWarnings("unchecked") comment in Signal::signalOfV
    @Test
    public void should_not_get_classcast_when_calling_equals_on_signals_of_different_types() {
        Signal<String> stringSignal = Signal.of("foo");
        Signal<Boolean> booleanSignal = Signal.of(true);
        assertThat(
            stringSignal.equals(booleanSignal)//shouldn't throw ClassCastException
        ).isFalse();
    }

    //This test is required to justify the @SuppressWarnings("unchecked") comment in Signal::signalOfV
    @Test
    public void equals_should_be_true_for_signals_of_different_types_but_same_value() {
        //Using new guarantees these are equal, but not the same instance.
        //This is so that the test is stronger.
        Object o = new String("foo");
        String s = new String("foo");

        Signal<Object> objectSignal = Signal.of(o);
        Signal<String> stringSignal = Signal.of(s);

        assertThat(
            objectSignal.equals(stringSignal)
        ).isTrue();
    }

}
//@formatter:on
