package fr.cla.wires;

import java.util.Arrays;
import java.util.List;

public class Delay extends AbstractValueObject<Delay> {
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

    public long duration() {
        return duration;
    }
}
