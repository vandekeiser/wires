package fr.cla.wires;

public class Time {

    private final Agenda agenda = new Agenda();

    private Time() {}

    Agenda agenda() {
        return agenda;
    }

    public static Time create() {
        return new Time();
    }

    public void tick() {
        agenda.tick();
    }

    public Tick now() {
        return agenda.now();
    }

}
