package fr.cla.wires.support.pbt;

import fr.cla.wires.support.oo.AbstractValueObject;

import static java.util.Objects.requireNonNull;

//@formatter:off
public class VoSingleton {

    public final AbstractValueObject<?> x;

    public VoSingleton(AbstractValueObject<?> x) {
        this.x = requireNonNull(x);
    }

}
//@formatter:oN