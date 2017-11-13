package fr.cla.wires;

import java.util.ArrayDeque;
import java.util.Objects;
import java.util.Queue;

import static java.util.Objects.requireNonNull;

class TickQueue {

    //Run actions in FIFO order
    private final Queue<Runnable> todo = new ArrayDeque<>();

    <V> void thenCall(Action<V> act, Signal<V> sig) {
        Action<V> _act = requireNonNull(act);
        Signal<V> _sig = requireNonNull(sig);
        todo.add(() -> _act.accept(_sig));
    }

    void runAll() {
        todo.forEach(Runnable::run);
    }

}
