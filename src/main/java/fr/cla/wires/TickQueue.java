package fr.cla.wires;

import java.util.ArrayDeque;
import java.util.Queue;

class TickQueue {

    //Run actions in FIFO order
    private final Queue<Runnable> todo = new ArrayDeque<>();

    <V> void thenCall(Action<V> act, Signal<V> sig) {
        todo.add(() -> act.accept(sig));
    }

    void runAll() {
        todo.forEach(Runnable::run);
    }

}
