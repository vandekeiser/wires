package fr.cla.wires;

import java.util.HashMap;
import java.util.Map;

import static java.util.Objects.requireNonNull;

//@formatter:off
/**
 * Remembers which tasks (Tick.Queue) to execute at each Tick.
 */
final class Agenda {

    private Tick now = Tick.ZERO;
    private final Map<Tick, Tick.Queue> appointments = new HashMap<>();

    void tick() {
        now = now.plus(Delay.of(1));

        Tick.Queue todo = appointments.get(now);
        if(todo == null) return;

        todo.runAll();
        appointments.remove(now);
    }

    Tick now() {
        if(now == null) throw new AssertionError();
        return now;
    }

    <V> OnSignalChanged<V> afterDelay(Delay delay, OnSignalChanged<V> callback) {
        OnSignalChanged<V> _callback = requireNonNull(callback);
        Delay _delay = requireNonNull(delay);

        return v -> waitFor(_delay).thenCall(_callback, v);
    }

    private Tick.Queue waitFor(Delay delay) {
        return appointments.computeIfAbsent(
            now.plus(delay),
            k -> new Tick.Queue()
        );
    }

}
//@formatter:on
