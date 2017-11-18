package fr.cla;

import fr.cla.wires.Signal;
import fr.cla.wires.Tick;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class AbstractValueObjectTest {

    @Test
    public void should_not_get_classcast_when_calling_equals_on_different_types() {
        Signal<String> stringSignal = Signal.of("foo");
        Tick tick = Tick.ZERO;
        assertThat(
            stringSignal.equals(tick)//shouldn't throw ClassCastException
        ).isFalse();
    }

}
