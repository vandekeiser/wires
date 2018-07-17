package fr.cla.wires.core;

import fr.cla.wires.support.oo.AbstractValueObject;

import java.util.List;

import static java.util.Collections.singletonList;

//@formatter:off
/**
 * A discrete Delay; each Box has a delay.
 */
public final class Delay extends AbstractValueObject<Delay> {

    private final int duration;

    //long would be too long a Delay.. But since Tick.tick is long, Achtung of "mixed-type computations"
    private Delay(int duration) {
        super(Delay.class);
        if(duration <= 0) throw new AssertionError("duration must be > 0, was: " + duration);
        this.duration = duration;
    }

    @Override
    protected List<Object> equalityCriteria() {
        return singletonList(duration);
    }

    public static Delay of(int duration) {
        return new Delay(duration);
    }

    /**
     * @return the >0 duration
     */
    int duration() {
        //Checked >0 in the constructor, so OK as long as this stays immutable.
        return duration;
    }

}
//@formatter:on