package fr.cla.wires;

import java.util.HashSet;
import java.util.Set;

import static java.util.Objects.requireNonNull;

//@formatter:off
/**
 * A wire on which a Signal transits.
 * When the Signal changes, all listeners are notified.
 */
public final class Wire<T> {

    private Signal<T> signal = Signal.none();

    //In the publish-subscribe pattern, the order in which listeners are notified should not matter.
    //-->change from Set to something else _iff_ we should not exactly use the publish-subscribe pattern (maybe re-read SICP).
    //At least for now I don't see why listeners should be notified in the order they were registered or any other specific order.
    //But at least they should be uniquely registered, so in that case the new Collection type used should preserve unicity.
    //
    //This also means that the unicity of listeners should be well-defined.
    // For now they are all instances of Wire, thus (since Wire is mutable) it must either:
    //      -continue to inherit equals/hashCode from Object,
    //      -become a Mutable (requires refactoring Mutable)
    //      -become a DDD Entity with a specific ID
    private final Set<OnSignalChanged<T>> listeners = new HashSet<>();

    /**
     * @return the current non-null Signal
     */
    public Signal<T> getSignal() {
        if(signal == null) throw new AssertionError();
        return signal;
    }

    public void setSignal(Signal<T> signal) {
        if(signal.equals(this.signal)) return;

        this.signal = signal;
        listeners.forEach(callback -> callback.accept(signal));
    }

    void onSignalChanged(OnSignalChanged<T> callback) {
        listeners.add(requireNonNull(callback));
    }

    public static <T> Wire<T> make() {
        return new Wire<>();
    }

    private Wire() {}

}
//@formatter:on
