package fr.cla.wires.support.test.random.vo;

import fr.cla.wires.support.oo.AbstractValueObject;

import static java.util.Objects.requireNonNull;

//@formatter:off
public class VoTriplet {

    public final AbstractValueObject<?> x, y, z;

    public VoTriplet(AbstractValueObject<?> x, AbstractValueObject<?> y, AbstractValueObject<?> z) {
        this.x = requireNonNull(x);
        this.y = requireNonNull(y);
        this.z = requireNonNull(z);
    }

}
//@formatter:oN