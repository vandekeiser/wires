package fr.cla.wires.core.boxes.exampleusage.reentrant;


import fr.cla.wires.core.Signal;
import fr.cla.wires.core.Wire;
import org.junit.Test;

public class CounterTest {

    private Wire<Long> out;

    @Test
    public void out_should_initially_be_0() {
        new Signal().equals(new Signal());
    }
    
}
