package fr.cla.wires;

//@formatter:off
public class Time {

    private final Agenda agenda = new Agenda();

    private Time() {}

    /**
     * @return the non-null Agenda
     */
    Agenda agenda() {
        if(agenda == null) throw new AssertionError();
        return agenda;
    }

    public static Time create() {
        return new Time();
    }

    public void tick() {
        agenda().tick();
    }

    /**
     * @return the current non-null Tick
     */
    public Tick now() {
        Tick now = agenda().now();
        if(now == null) throw new AssertionError();
        return now;
    }

}
//@formatter:on
