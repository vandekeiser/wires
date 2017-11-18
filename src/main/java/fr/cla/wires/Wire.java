package fr.cla.wires;

import java.util.HashSet;
import java.util.Set;

import static java.util.Objects.requireNonNull;

public class Wire<T> {

    private Signal<T> signal = Signal.none();
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
