package fr.cla.wires;


import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

//@formatter:off
public class TimeTest {

    private final Time time = Time.create();

    @Test
    public void tick_number_should_initially_be_0() {
        assertThat(time.now()).isEqualTo(Tick.ZERO);
    }

    @Test
    public void tick_number_should_be_incremeted_when_tick_is_called() {
        time.tick();
        assertThat(time.now()).isEqualTo(Tick.number(1));
    }

}
//@formatter:on