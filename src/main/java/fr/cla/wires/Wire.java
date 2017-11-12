package fr.cla.wires;

import java.util.ArrayList;
import java.util.List;

public class Wire<T> {

    private Signal<T> signal = Signal.none();
    private final List<Action<T>> actions = new ArrayList<>();

    public Signal<T> getSignal() {
        return signal;
    }

    public void setSignal(Signal<T> signal) {
        if(signal.equals(this.signal)) return;

        this.signal = signal;
        actions.forEach(a -> a.accept(signal));
    }

    void addAction(Action<T> action) {
        actions.add(action);
    }

    public static <T> Wire<T> make() {
        return new Wire<>();
    }
}
