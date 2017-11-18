package fr.cla.wires;


import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class WireTest {

    @Test
    public void signal_should_initially_be_none() {
        Wire<Boolean> w = Wire.make();
        assertThat(w.getSignal()).isEqualTo(Signal.none());
    }

}
