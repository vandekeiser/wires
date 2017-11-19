package fr.cla.wires;

import java.util.ArrayDeque;
import java.util.Queue;

import static java.util.Objects.requireNonNull;

//@formatter:off
final class TickQueue {

    //Run callbacks in FIFO order
    private final Queue<Runnable> todos = new ArrayDeque<>();

    <V> void thenCall(OnSignalChanged<V> callback, Signal<V> signal) {
        OnSignalChanged<V> _callback = requireNonNull(callback);
        Signal<V> _signal = requireNonNull(signal);

        todos.add(() -> _callback.accept(_signal));
    }

    void runAll() {
        todos.forEach(Runnable::run);
    }

}
//@formatter:on