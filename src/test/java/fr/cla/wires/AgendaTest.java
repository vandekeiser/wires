package fr.cla.wires;


import org.junit.Test;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class AgendaTest {

    @Test
    public void tick_number_should_initially_be_0() {
        //Given
        Agenda agenda = new Agenda();

        //Then
        assertThat(agenda.now()).isEqualTo(Tick.ZERO);
    }

    @Test
    public void tick_number_should_be_incremeted_when_tick_is_called() {
        //Given
        Agenda agenda = new Agenda();

        //When
        agenda.tick();

        //Then
        assertThat(agenda.now()).isEqualTo(Tick.number(1));
    }

}
