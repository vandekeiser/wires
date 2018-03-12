package fr.cla.wires.core.boxes.exampleusage.reentrant;


import fr.cla.wires.core.Signal;
import fr.cla.wires.core.Wire;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class CounterTest {

    private Wire<Long> out;

    @Test
    public void out_should_initially_be_0() {
        assertThat(out.getSignal()).isEqualTo(new Signal());
    }
    
}
