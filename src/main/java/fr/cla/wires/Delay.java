package fr.cla.wires;

import fr.cla.support.oo.ddd.AbstractValueObject;

import java.util.List;

import static java.util.Collections.singletonList;

//@formatter:off
/**
 * A discrete Delay; each Box has a delay.
 */
public final class Delay extends AbstractValueObject<Delay> {

    private final int duration;

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
    public long duration() {
        return duration;
    }

}
//@formatter:on