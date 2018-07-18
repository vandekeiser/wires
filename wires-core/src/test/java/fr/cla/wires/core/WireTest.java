package fr.cla.wires.core;


import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

//@formatter:off
public class WireTest {

    @Test
    public void signal_should_initially_be_none() {
        Wire<Boolean> w = Wire.make();
        assertThat(w.getSignal()).isEqualTo(Signal.none());
    }

}
//@formatter:on