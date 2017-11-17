package fr.cla.wires;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.requireNonNull;

public class Wire<T> {

    private Signal<T> signal = Signal.none();
    private final List<OnSignalChanged<T>> listeners = new ArrayList<>();

    public Signal<T> getSignal() {
        if(signal == null) throw new AssertionError();
        return signal;
    }

    public void setSignal(Signal<T> signal) {
        if(signal.equals(this.signal)) return;

        this.signal = signal;
        listeners.forEach(a -> a.accept(signal));
    }

    void onSignalChanged(OnSignalChanged<T> callback) {
        listeners.add(requireNonNull(callback));
    }

    public static <T> Wire<T> make() {
        return new Wire<>();
    }
}
