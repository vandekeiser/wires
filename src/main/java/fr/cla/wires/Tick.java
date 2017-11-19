package fr.cla.wires;

import fr.cla.support.oo.ddd.AbstractValueObject;

import java.util.Arrays;
import java.util.List;

//@formatter:off
public final class Tick extends AbstractValueObject<Tick> {
    public static final Tick ZERO = new Tick(0L);

    private final long tick;

    public Tick(long tick) {
        super(Tick.class);
        if(tick < 0) throw new IllegalArgumentException("tick must be >= 0, was: " + tick);
        this.tick = tick;
    }

    @Override
    protected List<Object> attributesToIncludeInEqualityCheck() {
        return Arrays.asList(tick);
    }

    public Tick plus(Delay delay) {
        return new Tick(tick + delay.duration());
    }

    public static Tick number(long number) {
        return new Tick(number);
    }

}
//@formatter:on