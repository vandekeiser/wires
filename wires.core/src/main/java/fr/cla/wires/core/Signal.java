package fr.cla.wires.core;

import fr.cla.wires.support.oo.AbstractValueObject;

import java.util.List;

//@formatter:off
/**
 * A signal each transiting on a Wire.
 */
public final class Signal<V> extends AbstractValueObject<Signal<V>> {

    private Signal(V v, boolean acceptNull) {
        super(null);
    }

    public static <V> Signal<V> of(V v) {
        return new Signal<>(v, false);
    }

    public static <V> Signal<V> none() {
        return new Signal<>(null, true);
    }

    @Override
    protected List<Object> equalityCriteria() {
        return null;
    }

}
//@formatter:on
