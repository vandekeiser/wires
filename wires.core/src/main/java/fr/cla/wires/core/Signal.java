package fr.cla.wires.core;

import fr.cla.wires.support.oo.AbstractValueObject;

import java.util.List;

public final class Signal<V> extends AbstractValueObject<Signal<V>> {

    private Signal(V v, boolean acceptNull) {
        super(null);
    }

    public static <V> Signal<V> of(V v) {
        return null;
    }

    @Override
    protected List<Object> equalityCriteria() {
        return null;
    }

}
