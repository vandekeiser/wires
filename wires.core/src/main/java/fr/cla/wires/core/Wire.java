package fr.cla.wires.core;

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
