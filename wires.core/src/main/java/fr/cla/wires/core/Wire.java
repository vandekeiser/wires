package fr.cla.wires.core;

import fr.cla.wires.support.functional.Indexed;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collector;

import static java.util.Objects.requireNonNull;

//@formatter:off
/**
 * A wire on which a Signal transits.
 * When the Signal changes, all listeners are notified.
 */
public final class Wire<T> {

    private Signal<T> signal = Signal.none();

    public Signal<T> getSignal() {
        if(signal == null) throw new AssertionError();
        return signal;
    }

    private Wire() {}

}
//@formatter:on
