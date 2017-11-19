package fr.cla.wires;

import fr.cla.support.oo.ddd.AbstractValueObject;

import java.util.Arrays;
import java.util.List;

//@formatter:off
public final class Delay extends AbstractValueObject<Delay> {
    private final int duration;

    private Delay(int duration) {
        super(Delay.class);
        if(duration <= 0) throw new IllegalArgumentException("duration must be > 0, was: " + duration);
        this.duration = duration;
    }

    @Override
    protected List<Object> attributesToIncludeInEqualityCheck() {
        return Arrays.asList(duration);
    }

    public static Delay of(int duration) {
        return new Delay(duration);
    }

    /**
     * @return the >0 duration
     */
    public long duration() {
        if(duration <= 0) throw new AssertionError("Duration managed to get <0! Was: " + duration);
        return duration;
    }
}
//@formatter:on